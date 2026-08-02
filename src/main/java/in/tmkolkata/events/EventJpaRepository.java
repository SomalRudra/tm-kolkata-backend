package in.tmkolkata.events;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventJpaRepository extends JpaRepository<EventEntity, Long> {

  List<EventEntity> findByPublishedTrueAndEventDateAfterOrderByEventDateAsc(Instant eventDate);

  List<EventEntity> findAllByOrderByEventDateAsc();
}
