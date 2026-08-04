package in.tmkolkata.auth;

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
  private final String smtpHost;
  private final String mailFromEmail;

  public AuthEmailService(
      JavaMailSender mailSender,
      @Value("${spring.mail.host}") String smtpHost,
      @Value("${app.auth.admin-email}") String adminEmail,
      @Value("${app.mail.from-email:}") String mailFromEmail
  ) {
    this.mailSender = mailSender;
    this.smtpHost = smtpHost;
    this.mailFromEmail = mailFromEmail == null || mailFromEmail.isBlank() ? adminEmail : mailFromEmail;
  }

  public boolean sendResetLink(String to, String resetUrl) {
    if (smtpHost == null || smtpHost.isBlank()) {
      return false;
    }

    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setFrom(mailFromEmail);
    message.setReplyTo(mailFromEmail);
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
}
