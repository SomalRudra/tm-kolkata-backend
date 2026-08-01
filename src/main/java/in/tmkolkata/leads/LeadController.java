package in.tmkolkata.leads;

import jakarta.validation.Valid;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {
    "http://localhost:4173",
    "https://somalrudra.github.io"
})
public class LeadController {

  private final LeadRepository leadRepository;

  public LeadController(LeadRepository leadRepository) {
    this.leadRepository = leadRepository;
  }

  @PostMapping("/registrations")
  @ResponseStatus(HttpStatus.CREATED)
  public LeadResponse register(@Valid @RequestBody RegistrationRequest request) {
    LeadRecord saved = leadRepository.save(LeadRecord.registration(request));
    return LeadResponse.from(saved);
  }

  @PostMapping("/questions")
  @ResponseStatus(HttpStatus.CREATED)
  public LeadResponse question(@Valid @RequestBody QuestionRequest request) {
    LeadRecord saved = leadRepository.save(LeadRecord.question(request));
    return LeadResponse.from(saved);
  }

  public record LeadResponse(long id, String type, Instant submittedAt) {
    static LeadResponse from(LeadRecord record) {
      return new LeadResponse(record.id(), record.type(), record.submittedAt());
    }
  }
}
