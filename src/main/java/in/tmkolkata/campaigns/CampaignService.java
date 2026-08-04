package in.tmkolkata.campaigns;

import in.tmkolkata.leads.LeadRecord;
import in.tmkolkata.leads.LeadRepository;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class CampaignService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CampaignService.class);

  private final LeadRepository leadRepository;
  private final JavaMailSender mailSender;
  private final RestClient restClient;
  private final String smtpHost;
  private final String mailFromEmail;
  private final String whatsappWebhookUrl;

  public CampaignService(
      LeadRepository leadRepository,
      JavaMailSender mailSender,
      RestClient.Builder restClientBuilder,
      @Value("${spring.mail.host}") String smtpHost,
      @Value("${app.auth.admin-email}") String adminEmail,
      @Value("${app.mail.from-email:}") String mailFromEmail,
      @Value("${app.whatsapp.webhook-url}") String whatsappWebhookUrl
  ) {
    this.leadRepository = leadRepository;
    this.mailSender = mailSender;
    this.restClient = restClientBuilder.build();
    this.smtpHost = smtpHost;
    this.mailFromEmail = mailFromEmail == null || mailFromEmail.isBlank() ? adminEmail : mailFromEmail;
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
    if (smtpHost == null || smtpHost.isBlank()) {
      return response(request, recipients, 0, recipients.size(), "SMTP is not configured", "spring-mail");
    }

    int sent = 0;
    int failed = 0;

    for (LeadRecord recipient : recipients) {
      try {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipient.email());
        message.setFrom(mailFromEmail);
        message.setReplyTo(mailFromEmail);
        message.setSubject(request.message_payload().subject() == null || request.message_payload().subject().isBlank()
            ? request.template_name()
            : request.message_payload().subject());
        message.setText(buildMessageBody(request));
        mailSender.send(message);
        sent += 1;
      } catch (MailException exception) {
        failed += 1;
        LOGGER.warn("Campaign email failed for lead {}: {}", recipient.id(), exception.getMessage(), exception);
      }
    }

    return response(request, recipients, sent, failed, failed == 0 ? "Email broadcast sent" : "Email broadcast partially failed",
        "spring-mail");
  }

  private BroadcastResponse sendWhatsapp(BroadcastRequest request, List<LeadRecord> recipients) {
    if (whatsappWebhookUrl == null || whatsappWebhookUrl.isBlank()) {
      return response(request, recipients, 0, recipients.size(),
          "WhatsApp provider is not configured. Set WHATSAPP_WEBHOOK_URL in Railway.", "webhook");
    }

    int sent = 0;
    int failed = 0;

    for (LeadRecord recipient : recipients) {
      try {
        restClient.post()
            .uri(whatsappWebhookUrl)
            .body(new WhatsappWebhookPayload(
                recipient.id(),
                recipient.fullName(),
                recipient.phone(),
                buildMessageBody(request),
                request.message_payload().cta_url()
            ))
            .retrieve()
            .toBodilessEntity();
        sent += 1;
      } catch (RestClientException exception) {
        failed += 1;
        LOGGER.warn("WhatsApp webhook failed for lead {}: {}", recipient.id(), exception.getMessage(), exception);
      }
    }

    return response(request, recipients, sent, failed,
        failed == 0 ? "WhatsApp broadcast sent" : "WhatsApp broadcast partially failed", "webhook");
  }

  private List<LeadRecord> recipients(BroadcastRequest request) {
    Set<String> ids = request.recipient_ids().stream().collect(Collectors.toSet());
    return leadRepository.findAll().stream()
        .filter((lead) -> ids.contains(String.valueOf(lead.id())))
        .toList();
  }

  private String buildMessageBody(BroadcastRequest request) {
    String body = request.message_payload().body();
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
      String provider
  ) {
    return new BroadcastResponse(
        request.channel(),
        request.recipient_ids().size(),
        sent,
        failed,
        status,
        provider,
        recipients.stream()
            .map((lead) -> new BroadcastRecipient(String.valueOf(lead.id()), lead.fullName(), lead.email(), lead.phone()))
            .toList()
    );
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
