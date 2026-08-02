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
@Table(name = "admin_tokens")
public class AdminTokenEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private AdminUserEntity admin;

  @Column(nullable = false, unique = true)
  private String tokenHash;

  @Column(nullable = false)
  private String tokenType;

  @Column(nullable = false)
  private Instant expiresAt;

  private Instant revokedAt;

  protected AdminTokenEntity() {
  }

  public AdminTokenEntity(AdminUserEntity admin, String tokenHash, String tokenType, Instant expiresAt) {
    this.admin = admin;
    this.tokenHash = tokenHash;
    this.tokenType = tokenType;
    this.expiresAt = expiresAt;
  }

  public AdminUserEntity getAdmin() {
    return admin;
  }

  public Instant getExpiresAt() {
    return expiresAt;
  }

  public Instant getRevokedAt() {
    return revokedAt;
  }

  public void revoke() {
    this.revokedAt = Instant.now();
  }
}
