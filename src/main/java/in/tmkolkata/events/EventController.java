package in.tmkolkata.events;

import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
public class EventController {

  private final EventJpaRepository eventJpaRepository;

  public EventController(EventJpaRepository eventJpaRepository) {
    this.eventJpaRepository = eventJpaRepository;
  }

  @GetMapping
  public List<EventResponse> publicEvents() {
    return eventJpaRepository.findByPublishedTrueAndEventDateAfterOrderByEventDateAsc(Instant.now()).stream()
        .map(EventResponse::from)
        .toList();
  }

  @GetMapping("/admin")
  public List<EventResponse> adminEvents() {
    return eventJpaRepository.findAllByOrderByEventDateAsc().stream()
        .map(EventResponse::from)
        .toList();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public EventResponse createEvent(@Valid @RequestBody EventRequest request) {
    return EventResponse.from(eventJpaRepository.save(EventResponse.toEntity(request)));
  }

  @PatchMapping("/{id}")
  public EventResponse updateEvent(@PathVariable Long id, @Valid @RequestBody EventRequest request) {
    EventEntity event = eventJpaRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Event not found"));
    EventResponse.updateEntity(event, request);
    return EventResponse.from(eventJpaRepository.save(event));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteEvent(@PathVariable Long id) {
    if (!eventJpaRepository.existsById(id)) {
      throw new IllegalArgumentException("Event not found");
    }
    eventJpaRepository.deleteById(id);
  }
}
