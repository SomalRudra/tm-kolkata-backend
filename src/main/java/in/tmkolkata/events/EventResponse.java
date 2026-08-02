package in.tmkolkata.events;

import java.time.Instant;

public record EventResponse(
    long id,
    String title,
    String kolkata_region,
    String event_mode,
    String event_date,
    String venue,
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
        request.capacity(),
        request.published() == null || request.published(),
        Instant.now()
    );
  }
}
