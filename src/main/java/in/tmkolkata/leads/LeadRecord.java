package in.tmkolkata.leads;

import java.time.Instant;
import java.util.LinkedHashMap;
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

  public static LeadRecord eventRegistration(EventRegistrationRequest request) {
    Map<String, String> details = new LinkedHashMap<>();
    details.put("event_id", request.event_id() == null ? "" : request.event_id());
    details.put("event_title", request.event_title() == null ? "" : request.event_title());
    details.put("gender", request.gender());
    details.put("age", request.age());
    details.put("question", request.question());
    details.put("source_channel", request.source_channel());
    details.put("utm_source", request.utm_source() == null ? "" : request.utm_source());
    details.put("utm_campaign", request.utm_campaign() == null ? "" : request.utm_campaign());
    details.put("bucket", request.bucket());

    return new LeadRecord(
        0,
        "event_registration",
        request.full_name(),
        request.email(),
        request.phone(),
        details,
        Instant.now()
    );
  }

  public static LeadRecord inquiry(InquiryRequest request) {
    return new LeadRecord(
        0,
        "inquiry",
        request.full_name(),
        request.email(),
        request.phone(),
        Map.of(
            "question_text", request.question_text(),
            "source_channel", request.source_channel(),
            "bucket", request.bucket(),
            "redirected_to_whatsapp", String.valueOf(request.redirected_to_whatsapp())
        ),
        Instant.now()
    );
  }

  public static LeadRecord statusUpdate(StatusUpdateRequest request) {
    return new LeadRecord(
        0,
        "status_update",
        request.lead_id(),
        "",
        "",
        Map.of(
            "lead_id", request.lead_id(),
            "current_bucket", request.current_bucket(),
            "new_bucket", request.new_bucket(),
            "attended_intro", String.valueOf(request.attended_intro()),
            "completed_tm_course", String.valueOf(request.completed_tm_course())
        ),
        Instant.now()
    );
  }
}
