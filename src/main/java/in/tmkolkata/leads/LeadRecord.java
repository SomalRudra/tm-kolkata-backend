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

  public static LeadRecord eventRegistration(EventRegistrationRequest request) {
    return new LeadRecord(
        0,
        "event_registration",
        request.full_name(),
        request.email(),
        request.phone(),
        Map.of(
            "kolkata_region", request.kolkata_region(),
            "event_date", request.event_date(),
            "event_mode", request.event_mode(),
            "source_channel", request.source_channel(),
            "utm_source", request.utm_source() == null ? "" : request.utm_source(),
            "utm_campaign", request.utm_campaign() == null ? "" : request.utm_campaign(),
            "bucket", request.bucket()
        ),
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
