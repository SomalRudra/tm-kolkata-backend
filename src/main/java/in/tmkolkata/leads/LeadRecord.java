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
    details.put("event_id", request.event_id() == null ? "" : String.valueOf(request.event_id()));
    details.put("city_state", request.city_state());
    details.put("kolkata_region", request.kolkata_region());
    details.put("event_date", request.event_date());
    details.put("event_mode", request.event_mode());
    details.put("source_channel", request.source_channel());
    details.put("utm_source", request.utm_source() == null ? "" : request.utm_source());
    details.put("utm_campaign", request.utm_campaign() == null ? "" : request.utm_campaign());
    details.put("bucket", request.bucket());
    details.put("age_group", request.age_group() == null ? "" : request.age_group());
    details.put("occupation", request.occupation() == null ? "" : request.occupation());
    details.put("heard_about", request.heard_about() == null ? "" : request.heard_about());
    details.put("motivation", request.motivation() == null ? "" : request.motivation());
    details.put("prior_meditation", request.prior_meditation() == null ? "" : request.prior_meditation());
    details.put("prior_meditation_types", request.prior_meditation_types() == null ? "" : request.prior_meditation_types());
    details.put("current_challenge", request.current_challenge() == null ? "" : request.current_challenge());
    details.put("stress_level", request.stress_level() == null ? "" : request.stress_level());
    details.put("practice_commitment", request.practice_commitment() == null ? "" : request.practice_commitment());
    details.put("best_contact_time", request.best_contact_time() == null ? "" : request.best_contact_time());
    details.put("future_updates", request.future_updates() == null ? "" : request.future_updates());
    details.put("pre_session_questions", request.pre_session_questions() == null ? "" : request.pre_session_questions());

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
