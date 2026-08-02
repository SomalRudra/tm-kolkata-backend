package in.tmkolkata.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class AuthEmailService {

  private final JavaMailSender mailSender;
  private final String smtpHost;
  private final String smtpUsername;
  private final String adminEmail;

  public AuthEmailService(
      JavaMailSender mailSender,
      @Value("${spring.mail.host}") String smtpHost,
      @Value("${spring.mail.username}") String smtpUsername,
      @Value("${app.auth.admin-email}") String adminEmail
  ) {
    this.mailSender = mailSender;
    this.smtpHost = smtpHost;
    this.smtpUsername = smtpUsername;
    this.adminEmail = adminEmail;
  }

  public boolean sendResetLink(String to, String resetUrl) {
    if (smtpHost == null || smtpHost.isBlank()) {
      return false;
    }

    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setFrom(smtpUsername == null || smtpUsername.isBlank() ? adminEmail : smtpUsername);
    message.setSubject("TM Kolkata admin password reset");
    message.setText("Open this link to reset your TM Kolkata admin password:\n\n" + resetUrl
        + "\n\nThis link expires soon. If you did not request this, ignore this email.");
    mailSender.send(message);
    return true;
  }
}
