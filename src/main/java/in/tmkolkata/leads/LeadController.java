package in.tmkolkata.leads;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LeadController {

  private final LeadRepository leadRepository;
  private final ObjectMapper objectMapper;

  public LeadController(LeadRepository leadRepository, ObjectMapper objectMapper) {
    this.leadRepository = leadRepository;
    this.objectMapper = objectMapper;
  }

  @PostMapping("/registrations")
  @ResponseStatus(HttpStatus.CREATED)
  public LeadResponse register(@Valid @RequestBody RegistrationRequest request) {
    LeadRecord saved = leadRepository.save(LeadRecord.registration(request));
    return LeadResponse.from(saved);
  }

  @PostMapping("/leads/register")
  @ResponseStatus(HttpStatus.CREATED)
  public LeadResponse registerLead(@Valid @RequestBody EventRegistrationRequest request) {
    LeadRecord saved = leadRepository.save(LeadRecord.eventRegistration(request));
    return LeadResponse.from(saved);
  }

  @PostMapping("/questions")
  @ResponseStatus(HttpStatus.CREATED)
  public LeadResponse question(@Valid @RequestBody QuestionRequest request) {
    LeadRecord saved = leadRepository.save(LeadRecord.question(request));
    return LeadResponse.from(saved);
  }

  @PostMapping("/leads/inquiry")
  @ResponseStatus(HttpStatus.CREATED)
  public LeadResponse inquiryLead(@Valid @RequestBody InquiryRequest request) {
    LeadRecord saved = leadRepository.save(LeadRecord.inquiry(request));
    return LeadResponse.from(saved);
  }

  @PatchMapping("/leads/update-status")
  public LeadResponse updateStatus(@Valid @RequestBody StatusUpdateRequest request) {
    LeadRecord saved = leadRepository.save(LeadRecord.statusUpdate(request));
    return LeadResponse.from(saved);
  }

  @GetMapping("/leads")
  public List<LeadRecord> leads() {
    return leadRepository.findAll();
  }

  @GetMapping("/leads/export")
  public ResponseEntity<byte[]> leadSnapshot() {
    Map<String, Object> snapshot = Map.of(
        "generated_at", Instant.now().toString(),
        "records", leadRepository.findAll()
    );

    try {
      byte[] body = objectMapper.writerWithDefaultPrettyPrinter()
          .writeValueAsString(snapshot)
          .getBytes(StandardCharsets.UTF_8);

      return ResponseEntity.ok()
          .contentType(MediaType.APPLICATION_JSON)
          .header(HttpHeaders.CONTENT_DISPOSITION,
              ContentDisposition.attachment()
                  .filename("tm-kolkata-user-data-snapshot.json")
                  .build()
                  .toString())
          .body(body);
    } catch (JsonProcessingException exception) {
      throw new IllegalArgumentException("Lead snapshot could not be generated", exception);
    }
  }

  @GetMapping("/health")
  public Map<String, Object> health() {
    return Map.of(
        "status", "ok",
        "service", "tm-kolkata-backend",
        "storage", "database",
        "leadCount", leadRepository.count()
    );
  }

  public record LeadResponse(long id, String type, Instant submittedAt) {
    static LeadResponse from(LeadRecord record) {
      return new LeadResponse(record.id(), record.type(), record.submittedAt());
    }
  }
}
