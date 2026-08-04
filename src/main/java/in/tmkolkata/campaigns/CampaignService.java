package in.tmkolkata.campaigns;

import in.tmkolkata.leads.LeadRecord;
import in.tmkolkata.leads.LeadRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Service
public class CampaignService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CampaignService.class);
  private static final String BREVO_EMAIL_ENDPOINT = "https://api.brevo.com/v3/smtp/email";

  private final LeadRepository leadRepository;
  private final RestClient restClient;
  private final String mailFromEmail;
  private final String brevoApiKey;
  private final String whatsappWebhookUrl;

  public CampaignService(
      LeadRepository leadRepository,
      RestClient.Builder restClientBuilder,
      @Value("${app.auth.admin-email}") String adminEmail,
      @Value("${app.mail.from-email:}") String mailFromEmail,
      @Value("${app.brevo.api-key}") String brevoApiKey,
      @Value("${app.whatsapp.webhook-url}") String whatsappWebhookUrl
  ) {
    this.leadRepository = leadRepository;
    this.restClient = restClientBuilder.build();
    this.mailFromEmail = mailFromEmail == null || mailFromEmail.isBlank() ? adminEmail : mailFromEmail;
    this.brevoApiKey = brevoApiKey;
    this.whatsappWebhookUrl = whatsappWebhookUrl;
  }

  public BroadcastResponse broadcast(BroadcastRequest request) {
    List<LeadRecord> recipients = recipients(request);
    String channel = request.channel().toLowerCase(Locale.ROOT);

    if ("email".equals(channel)) {
      return sendEmail(request, recipients);
    }

    if ("whatsapp".equals(channel)) {
      return sendWhatsapp(request, recipients);
    }

    throw new IllegalArgumentException("Unsupported broadcast channel");
  }

  private BroadcastResponse sendEmail(BroadcastRequest request, List<LeadRecord> recipients) {
    if (brevoApiKey == null || brevoApiKey.isBlank()) {
      return response(request, recipients, 0, recipients.size(), "Brevo API is not configured", "brevo-api",
          List.of("BREVO_API_KEY is blank in the backend environment."));
    }

    int sent = 0;
    int failed = 0;
    List<String> errors = new ArrayList<>();

    for (LeadRecord recipient : recipients) {
      try {
        restClient.post()
            .uri(BREVO_EMAIL_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("api-key", brevoApiKey)
            .body(new BrevoEmailPayload(
                new BrevoEmailAddress(mailFromEmail, "TM Kolkata"),
                List.of(new BrevoEmailAddress(recipient.email(), recipient.fullName())),
                new BrevoEmailAddress(mailFromEmail, "TM Kolkata"),
                buildSubject(request),
                buildMessageBody(request, recipient),
                List.of("tm-kolkata-campaign")
            ))
            .retrieve()
            .toBodilessEntity();
        sent += 1;
      } catch (RestClientException exception) {
        failed += 1;
        String error = "lead " + recipient.id() + ": " + rootMessage(exception);
        errors.add(error);
        LOGGER.warn("Brevo email failed for lead {}: {}", recipient.id(), error, exception);
      }
    }

    return response(request, recipients, sent, failed, failed == 0 ? "Email broadcast sent" : "Email broadcast partially failed",
        "brevo-api", errors);
  }

  private BroadcastResponse sendWhatsapp(BroadcastRequest request, List<LeadRecord> recipients) {
    if (whatsappWebhookUrl == null || whatsappWebhookUrl.isBlank()) {
      return response(request, recipients, 0, recipients.size(),
          "WhatsApp provider is not configured. Set WHATSAPP_WEBHOOK_URL in Railway.", "webhook",
          List.of("WHATSAPP_WEBHOOK_URL is blank in the backend environment."));
    }

    int sent = 0;
    int failed = 0;
    List<String> errors = new ArrayList<>();

    for (LeadRecord recipient : recipients) {
      try {
        restClient.post()
            .uri(whatsappWebhookUrl)
            .body(new WhatsappWebhookPayload(
                recipient.id(),
                recipient.fullName(),
                recipient.phone(),
                buildMessageBody(request, recipient),
                request.message_payload().cta_url()
            ))
            .retrieve()
            .toBodilessEntity();
        sent += 1;
      } catch (RestClientException exception) {
        failed += 1;
        String error = "lead " + recipient.id() + ": " + rootMessage(exception);
        errors.add(error);
        LOGGER.warn("WhatsApp webhook failed for lead {}: {}", recipient.id(), error, exception);
      }
    }

    return response(request, recipients, sent, failed,
        failed == 0 ? "WhatsApp broadcast sent" : "WhatsApp broadcast partially failed", "webhook", errors);
  }

  private List<LeadRecord> recipients(BroadcastRequest request) {
    Set<String> ids = request.recipient_ids().stream().collect(Collectors.toSet());
    return leadRepository.findAll().stream()
        .filter((lead) -> ids.contains(String.valueOf(lead.id())))
        .toList();
  }

  private String buildSubject(BroadcastRequest request) {
    return request.message_payload().subject() == null || request.message_payload().subject().isBlank()
        ? request.template_name()
        : request.message_payload().subject();
  }

  private String buildMessageBody(BroadcastRequest request, LeadRecord recipient) {
    String body = personalize(request.message_payload().body(), request, recipient);
    String ctaUrl = request.message_payload().cta_url();
    if (ctaUrl == null || ctaUrl.isBlank()) {
      return body;
    }

    return body + "\n\n" + ctaUrl;
  }

  private BroadcastResponse response(
      BroadcastRequest request,
      List<LeadRecord> recipients,
      int sent,
      int failed,
      String status,
      String provider,
      List<String> errors
  ) {
    return new BroadcastResponse(
        request.channel(),
        request.recipient_ids().size(),
        sent,
        failed,
        status,
        provider,
        "email".equalsIgnoreCase(request.channel()) ? mailFromEmail : null,
        errors,
        recipients.stream()
            .map((lead) -> new BroadcastRecipient(String.valueOf(lead.id()), lead.fullName(), lead.email(), lead.phone()))
            .toList()
    );
  }

  private String rootMessage(Exception exception) {
    if (exception instanceof RestClientResponseException responseException) {
      String responseBody = responseException.getResponseBodyAsString();
      if (responseBody != null && !responseBody.isBlank()) {
        return responseException.getStatusCode() + " " + responseBody;
      }
    }

    Throwable current = exception;
    while (current.getCause() != null) {
      current = current.getCause();
    }

    return current.getMessage() == null ? exception.getMessage() : current.getMessage();
  }

  private String personalize(String value, BroadcastRequest request, LeadRecord recipient) {
    if (value == null) {
      return "";
    }

    BroadcastRequest.MessagePayload payload = request.message_payload();
    return value
        .replace("{{full_name}}", recipient.fullName())
        .replace("{{event_title}}", payload.event_title() == null ? "" : payload.event_title())
        .replace("{{event_date}}", payload.event_date() == null ? "" : payload.event_date())
        .replace("{{event_venue}}", payload.event_venue() == null ? "" : payload.event_venue());
  }

  private record BrevoEmailPayload(
      BrevoEmailAddress sender,
      List<BrevoEmailAddress> to,
      BrevoEmailAddress replyTo,
      String subject,
      String textContent,
      List<String> tags
  ) {
  }

  private record BrevoEmailAddress(
      String email,
      String name
  ) {
  }

  private record WhatsappWebhookPayload(
      long lead_id,
      String full_name,
      String phone,
      String message,
      String cta_url
  ) {
  }
}
