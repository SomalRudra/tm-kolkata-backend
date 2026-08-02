package in.tmkolkata.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthEmailService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthEmailService.class);

  private final JavaMailSender mailSender;
  private final ObjectMapper objectMapper;
  private final String smtpHost;
  private final String smtpUsername;
  private final String adminEmail;
  private final String resendApiKey;
  private final String resendFromEmail;
  private final String resendApiUrl;
  private final HttpClient httpClient;

  public AuthEmailService(
      JavaMailSender mailSender,
      ObjectMapper objectMapper,
      @Value("${spring.mail.host}") String smtpHost,
      @Value("${spring.mail.username}") String smtpUsername,
      @Value("${app.auth.admin-email}") String adminEmail,
      @Value("${app.email.resend-api-key}") String resendApiKey,
      @Value("${app.email.resend-from-email}") String resendFromEmail,
      @Value("${app.email.resend-api-url}") String resendApiUrl
  ) {
    this.mailSender = mailSender;
    this.objectMapper = objectMapper;
    this.smtpHost = smtpHost;
    this.smtpUsername = smtpUsername;
    this.adminEmail = adminEmail;
    this.resendApiKey = resendApiKey;
    this.resendFromEmail = resendFromEmail;
    this.resendApiUrl = resendApiUrl;
    this.httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();
  }

  public boolean sendResetLink(String to, String resetUrl) {
    if (resendApiKey != null && !resendApiKey.isBlank()) {
      return sendWithResend(to, resetUrl);
    }

    if (smtpHost == null || smtpHost.isBlank()) {
      return false;
    }

    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setFrom(smtpUsername == null || smtpUsername.isBlank() ? adminEmail : smtpUsername);
    message.setSubject("TM Kolkata admin password reset");
    message.setText("Open this link to reset your TM Kolkata admin password:\n\n" + resetUrl
        + "\n\nThis link expires soon. If you did not request this, ignore this email.");
    try {
      mailSender.send(message);
      return true;
    } catch (MailException exception) {
      LOGGER.warn("Admin password reset email failed: {}", exception.getMessage(), exception);
      return false;
    }
  }

  private boolean sendWithResend(String to, String resetUrl) {
    Map<String, Object> payload = Map.of(
        "from", resendFromEmail,
        "to", List.of(to),
        "subject", "TM Kolkata admin password reset",
        "text", "Open this link to reset your TM Kolkata admin password:\n\n" + resetUrl
            + "\n\nThis link expires soon. If you did not request this, ignore this email."
    );

    try {
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(resendApiUrl))
          .timeout(Duration.ofSeconds(12))
          .header("Authorization", "Bearer " + resendApiKey)
          .header("Content-Type", "application/json")
          .header("User-Agent", "tm-kolkata-backend/1.0")
          .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
          .build();

      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() >= 200 && response.statusCode() < 300) {
        return true;
      }

      LOGGER.warn("Admin password reset email failed through Resend: status={} body={}",
          response.statusCode(), response.body());
      return false;
    } catch (JsonProcessingException exception) {
      LOGGER.warn("Admin password reset email payload could not be serialized: {}", exception.getMessage(),
          exception);
      return false;
    } catch (IOException exception) {
      LOGGER.warn("Admin password reset email failed through Resend: {}", exception.getMessage(), exception);
      return false;
    } catch (InterruptedException exception) {
      Thread.currentThread().interrupt();
      LOGGER.warn("Admin password reset email was interrupted: {}", exception.getMessage(), exception);
      return false;
    }
  }
}
