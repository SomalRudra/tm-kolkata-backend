package in.tmkolkata.leads;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class LeadRepository {

  private static final TypeReference<Map<String, String>> DETAILS_TYPE = new TypeReference<>() {
  };

  private final LeadJpaRepository leadJpaRepository;
  private final ObjectMapper objectMapper;

  public LeadRepository(LeadJpaRepository leadJpaRepository, ObjectMapper objectMapper) {
    this.leadJpaRepository = leadJpaRepository;
    this.objectMapper = objectMapper;
  }

  public LeadRecord save(LeadRecord lead) {
    LeadEntity entity = new LeadEntity(
        lead.type(),
        lead.fullName(),
        lead.email(),
        lead.phone(),
        writeDetails(lead.details()),
        lead.submittedAt()
    );

    return toRecord(leadJpaRepository.save(entity));
  }

  public List<LeadRecord> findAll() {
    return leadJpaRepository.findAll().stream()
        .map(this::toRecord)
        .toList();
  }

  private LeadRecord toRecord(LeadEntity entity) {
    return new LeadRecord(
        entity.getId(),
        entity.getType(),
        entity.getFullName(),
        entity.getEmail(),
        entity.getPhone(),
        readDetails(entity.getDetailsJson()),
        entity.getSubmittedAt()
    );
  }

  private String writeDetails(Map<String, String> details) {
    try {
      return objectMapper.writeValueAsString(details);
    } catch (JsonProcessingException exception) {
      throw new IllegalArgumentException("Lead details could not be serialized", exception);
    }
  }

  private Map<String, String> readDetails(String detailsJson) {
    try {
      return objectMapper.readValue(detailsJson, DETAILS_TYPE);
    } catch (JsonProcessingException exception) {
      throw new IllegalArgumentException("Lead details could not be read", exception);
    }
  }
}
