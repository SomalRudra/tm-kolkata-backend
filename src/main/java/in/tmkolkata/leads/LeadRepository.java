package in.tmkolkata.leads;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class LeadRepository {

  private final AtomicLong sequence = new AtomicLong(1);
  private final List<LeadRecord> records = new ArrayList<>();

  public synchronized LeadRecord save(LeadRecord lead) {
    LeadRecord saved = new LeadRecord(
        sequence.getAndIncrement(),
        lead.type(),
        lead.fullName(),
        lead.email(),
        lead.phone(),
        lead.details(),
        lead.submittedAt()
    );
    records.add(saved);
    return saved;
  }

  public synchronized List<LeadRecord> findAll() {
    return List.copyOf(records);
  }
}
