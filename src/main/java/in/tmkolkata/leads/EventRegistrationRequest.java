package in.tmkolkata.leads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EventRegistrationRequest(
    Long event_id,
    @NotBlank String full_name,
    @NotBlank @Email String email,
    @NotBlank @Pattern(regexp = "^[0-9+\\-\\s]{8,18}$") String phone,
    @NotBlank String kolkata_region,
    @NotBlank String event_date,
    @NotBlank @Pattern(regexp = "^(In-Person|Virtual)$") String event_mode,
    @NotBlank @Pattern(regexp = "^(Meta Ads|Direct Web|Organic)$") String source_channel,
    String utm_source,
    String utm_campaign,
    @NotBlank @Pattern(regexp = "^BUCKET_B_REGISTERED$") String bucket
) {
}
