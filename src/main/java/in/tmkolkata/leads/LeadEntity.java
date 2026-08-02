package in.tmkolkata.leads;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "leads")
public class LeadEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String type;

  @Column(nullable = false)
  private String fullName;

  private String email;

  private String phone;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String detailsJson;

  @Column(nullable = false)
  private Instant submittedAt;

  protected LeadEntity() {
  }

  public LeadEntity(String type, String fullName, String email, String phone, String detailsJson,
      Instant submittedAt) {
    this.type = type;
    this.fullName = fullName;
    this.email = email;
    this.phone = phone;
    this.detailsJson = detailsJson;
    this.submittedAt = submittedAt;
  }

  public Long getId() {
    return id;
  }

  public String getType() {
    return type;
  }

  public String getFullName() {
    return fullName;
  }

  public String getEmail() {
    return email;
  }

  public String getPhone() {
    return phone;
  }

  public String getDetailsJson() {
    return detailsJson;
  }

  public Instant getSubmittedAt() {
    return submittedAt;
  }
}
