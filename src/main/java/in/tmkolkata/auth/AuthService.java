package in.tmkolkata.auth;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

  private static final String ACCESS = "ACCESS";
  private static final String REFRESH = "REFRESH";

  private final AdminUserRepository adminUserRepository;
  private final AdminTokenRepository adminTokenRepository;
  private final PasswordResetTokenRepository passwordResetTokenRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  private final TokenService tokenService;
  private final AuthEmailService authEmailService;
  private final long accessTokenMinutes;
  private final long refreshTokenDays;
  private final long resetTokenMinutes;
  private final String frontendResetUrl;
  private final boolean includeResetLinkInResponse;

  public AuthService(
      AdminUserRepository adminUserRepository,
      AdminTokenRepository adminTokenRepository,
      PasswordResetTokenRepository passwordResetTokenRepository,
      BCryptPasswordEncoder passwordEncoder,
      TokenService tokenService,
      AuthEmailService authEmailService,
      @Value("${app.auth.access-token-minutes}") long accessTokenMinutes,
      @Value("${app.auth.refresh-token-days}") long refreshTokenDays,
      @Value("${app.auth.reset-token-minutes}") long resetTokenMinutes,
      @Value("${app.frontend.reset-url}") String frontendResetUrl,
      @Value("${app.auth.include-reset-link-in-response}") boolean includeResetLinkInResponse
  ) {
    this.adminUserRepository = adminUserRepository;
    this.adminTokenRepository = adminTokenRepository;
    this.passwordResetTokenRepository = passwordResetTokenRepository;
    this.passwordEncoder = passwordEncoder;
    this.tokenService = tokenService;
    this.authEmailService = authEmailService;
    this.accessTokenMinutes = accessTokenMinutes;
    this.refreshTokenDays = refreshTokenDays;
    this.resetTokenMinutes = resetTokenMinutes;
    this.frontendResetUrl = frontendResetUrl;
    this.includeResetLinkInResponse = includeResetLinkInResponse;
  }

  @Transactional
  public AuthResponse login(String username, String password) {
    AdminUserEntity admin = adminUserRepository.findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("Invalid admin credentials"));

    if (!passwordEncoder.matches(password, admin.getPasswordHash())) {
      throw new IllegalArgumentException("Invalid admin credentials");
    }

    return issueTokens(admin);
  }

  @Transactional
  public AuthResponse refresh(String refreshToken) {
    AdminTokenEntity token = adminTokenRepository.findByTokenHashAndTokenType(tokenService.hash(refreshToken), REFRESH)
        .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

    if (!tokenService.isActive(token.getExpiresAt(), token.getRevokedAt())) {
      throw new IllegalArgumentException("Invalid refresh token");
    }

    token.revoke();
    return issueTokens(token.getAdmin());
  }

  public Optional<AdminUserEntity> validateAccessToken(String accessToken) {
    return adminTokenRepository.findByTokenHashAndTokenType(tokenService.hash(accessToken), ACCESS)
        .filter(token -> tokenService.isActive(token.getExpiresAt(), token.getRevokedAt()))
        .map(AdminTokenEntity::getAdmin);
  }

  @Transactional
  public ForgotPasswordResponse forgotPassword(String email) {
    Optional<AdminUserEntity> admin = adminUserRepository.findByEmail(email);
    if (admin.isEmpty()) {
      return new ForgotPasswordResponse(true, false, null);
    }

    String resetToken = tokenService.newToken();
    String resetUrl = frontendResetUrl + "?token=" + resetToken;
    passwordResetTokenRepository.save(new PasswordResetTokenEntity(
        admin.get(),
        tokenService.hash(resetToken),
        Instant.now().plus(resetTokenMinutes, ChronoUnit.MINUTES)
    ));

    boolean emailSent = authEmailService.sendResetLink(admin.get().getEmail(), resetUrl);
    return new ForgotPasswordResponse(true, emailSent, includeResetLinkInResponse ? resetUrl : null);
  }

  @Transactional
  public void resetPassword(String resetToken, String newPassword) {
    PasswordResetTokenEntity token = passwordResetTokenRepository.findByTokenHash(tokenService.hash(resetToken))
        .orElseThrow(() -> new IllegalArgumentException("Invalid reset token"));

    if (!tokenService.isActive(token.getExpiresAt(), token.getUsedAt())) {
      throw new IllegalArgumentException("Invalid reset token");
    }

    token.getAdmin().setPasswordHash(passwordEncoder.encode(newPassword));
    token.markUsed();
  }

  private AuthResponse issueTokens(AdminUserEntity admin) {
    String accessToken = tokenService.newToken();
    String refreshToken = tokenService.newToken();

    adminTokenRepository.save(new AdminTokenEntity(
        admin,
        tokenService.hash(accessToken),
        ACCESS,
        Instant.now().plus(accessTokenMinutes, ChronoUnit.MINUTES)
    ));
    adminTokenRepository.save(new AdminTokenEntity(
        admin,
        tokenService.hash(refreshToken),
        REFRESH,
        Instant.now().plus(refreshTokenDays, ChronoUnit.DAYS)
    ));

    return new AuthResponse(accessToken, refreshToken, accessTokenMinutes * 60, admin.getUsername());
  }
}
