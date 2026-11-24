package in.tmkolkata.leads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EventRegistrationRequest(
    String event_id,
    String event_title,
    @NotBlank String full_name,
    @NotBlank @Email String email,
    @NotBlank @Pattern(regexp = "^[0-9+\\-\\s]{8,18}$") String phone,
    @NotBlank String gender,
    @NotBlank @Pattern(regexp = "^[1-9][0-9]?$|^1[01][0-9]$|^120$") String age,
    @NotBlank @Size(min = 4, max = 1200) String question,
    @NotBlank @Pattern(regexp = "^(Meta Ads|Direct Web|Organic)$") String source_channel,
    String utm_source,
    String utm_campaign,
    @NotBlank @Pattern(regexp = "^BUCKET_B_REGISTERED$") String bucket
) {
}
