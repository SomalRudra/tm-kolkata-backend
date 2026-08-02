package in.tmkolkata.events;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "events")
public class EventEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String kolkataRegion;

  @Column(nullable = false)
  private String eventMode;

  @Column(nullable = false)
  private Instant eventDate;

  @Column(nullable = false)
  private String venue;

  @Column(columnDefinition = "TEXT")
  private String description = "";

  @Column(nullable = false)
  private int capacity;

  @Column(nullable = false)
  private boolean published;

  @Column(nullable = false)
  private Instant createdAt;

  protected EventEntity() {
  }

  public EventEntity(String title, String kolkataRegion, String eventMode, Instant eventDate, String venue,
      String description, int capacity, boolean published, Instant createdAt) {
    this.title = title;
    this.kolkataRegion = kolkataRegion;
    this.eventMode = eventMode;
    this.eventDate = eventDate;
    this.venue = venue;
    this.description = description;
    this.capacity = capacity;
    this.published = published;
    this.createdAt = createdAt;
  }

  public void update(String title, String kolkataRegion, String eventMode, Instant eventDate, String venue,
      String description, int capacity, boolean published) {
    this.title = title;
    this.kolkataRegion = kolkataRegion;
    this.eventMode = eventMode;
    this.eventDate = eventDate;
    this.venue = venue;
    this.description = description;
    this.capacity = capacity;
    this.published = published;
  }

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getKolkataRegion() {
    return kolkataRegion;
  }

  public String getEventMode() {
    return eventMode;
  }

  public Instant getEventDate() {
    return eventDate;
  }

  public String getVenue() {
    return venue;
  }

  public String getDescription() {
    return description == null ? "" : description;
  }

  public int getCapacity() {
    return capacity;
  }

  public boolean isPublished() {
    return published;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
