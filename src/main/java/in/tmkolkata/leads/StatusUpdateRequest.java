package in.tmkolkata.leads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record StatusUpdateRequest(
    @NotBlank String lead_id,
    @NotBlank @Pattern(regexp = "^BUCKET_[ABC]$") String current_bucket,
    @NotBlank @Pattern(regexp = "^BUCKET_[ABC]$") String new_bucket,
    boolean attended_intro,
    boolean completed_tm_course
) {
}
