package in.tmkolkata.campaigns;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record BroadcastRequest(
    @NotBlank String channel,
    @NotBlank String template_id,
    @NotBlank String template_name,
    List<String> target_buckets,
    @NotEmpty List<String> recipient_ids,
    @Valid @NotNull MessagePayload message_payload
) {

  public record MessagePayload(
      String subject,
      @NotBlank String body,
      String cta_url,
      Long event_id,
      String event_title,
      String event_date,
      String event_venue
  ) {
  }
}
