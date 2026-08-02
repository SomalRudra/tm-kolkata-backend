package in.tmkolkata.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements CommandLineRunner {

  private final AdminUserRepository adminUserRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  private final String adminUsername;
  private final String adminEmail;
  private final String adminPassword;

  public AdminBootstrap(
      AdminUserRepository adminUserRepository,
      BCryptPasswordEncoder passwordEncoder,
      @Value("${app.auth.admin-username}") String adminUsername,
      @Value("${app.auth.admin-email}") String adminEmail,
      @Value("${app.auth.admin-password}") String adminPassword
  ) {
    this.adminUserRepository = adminUserRepository;
    this.passwordEncoder = passwordEncoder;
    this.adminUsername = adminUsername;
    this.adminEmail = adminEmail;
    this.adminPassword = adminPassword;
  }

  @Override
  public void run(String... args) {
    adminUserRepository.findByUsername(adminUsername)
        .orElseGet(() -> adminUserRepository.save(
            new AdminUserEntity(adminUsername, adminEmail, passwordEncoder.encode(adminPassword))
        ));
  }
}
