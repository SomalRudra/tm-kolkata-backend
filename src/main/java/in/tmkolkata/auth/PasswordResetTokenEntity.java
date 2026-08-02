package in.tmkolkata.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetTokenEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private AdminUserEntity admin;

  @Column(nullable = false, unique = true)
  private String tokenHash;

  @Column(nullable = false)
  private Instant expiresAt;

  private Instant usedAt;

  protected PasswordResetTokenEntity() {
  }

  public PasswordResetTokenEntity(AdminUserEntity admin, String tokenHash, Instant expiresAt) {
    this.admin = admin;
    this.tokenHash = tokenHash;
    this.expiresAt = expiresAt;
  }

  public AdminUserEntity getAdmin() {
    return admin;
  }

  public Instant getExpiresAt() {
    return expiresAt;
  }

  public Instant getUsedAt() {
    return usedAt;
  }

  public void markUsed() {
    this.usedAt = Instant.now();
  }
}
