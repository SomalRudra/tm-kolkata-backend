package in.tmkolkata.events;

import java.time.Instant;

public record EventResponse(
    long id,
    String title,
    String kolkata_region,
    String event_mode,
    String event_date,
    String venue,
    String description,
    int capacity,
    boolean published,
    String created_at
) {

  static EventResponse from(EventEntity event) {
    return new EventResponse(
        event.getId(),
        event.getTitle(),
        event.getKolkataRegion(),
        event.getEventMode(),
        event.getEventDate().toString(),
        event.getVenue(),
        event.getDescription(),
        event.getCapacity(),
        event.isPublished(),
        event.getCreatedAt().toString()
    );
  }

  static EventEntity toEntity(EventRequest request) {
    return new EventEntity(
        request.title(),
        request.kolkata_region(),
        request.event_mode(),
        Instant.parse(request.event_date()),
        request.venue(),
        normalizeDescription(request.description()),
        request.capacity(),
        request.published() == null || request.published(),
        Instant.now()
    );
  }

  static void updateEntity(EventEntity event, EventRequest request) {
    event.update(
        request.title(),
        request.kolkata_region(),
        request.event_mode(),
        Instant.parse(request.event_date()),
        request.venue(),
        normalizeDescription(request.description()),
        request.capacity(),
        request.published() == null || request.published()
    );
  }

  private static String normalizeDescription(String description) {
    return description == null ? "" : description;
  }
}
