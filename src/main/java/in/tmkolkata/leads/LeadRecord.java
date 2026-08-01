package in.tmkolkata.leads;

import java.time.Instant;
import java.util.Map;

public record LeadRecord(
    long id,
    String type,
    String fullName,
    String email,
    String phone,
    Map<String, String> details,
    Instant submittedAt
) {

  public static LeadRecord registration(RegistrationRequest request) {
    return new LeadRecord(
        0,
        "registration",
        request.fullName(),
        request.email(),
        request.phone(),
        Map.of(
            "cityArea", request.cityArea(),
            "preferredDate", request.preferredDate()
        ),
        Instant.now()
    );
  }

  public static LeadRecord question(QuestionRequest request) {
    return new LeadRecord(
        0,
        "question",
        request.fullName(),
        request.email(),
        request.phone(),
        Map.of("question", request.question()),
        Instant.now()
    );
  }
}
