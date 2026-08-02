package in.tmkolkata.leads;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record InquiryRequest(
    @NotBlank String full_name,
    @NotBlank @Email String email,
    @NotBlank @Pattern(regexp = "^[0-9+\\-\\s]{8,18}$") String phone,
    @NotBlank @Size(min = 4, max = 1200) String question_text,
    @NotBlank @Pattern(regexp = "^(Meta Ads|Direct Web)$") String source_channel,
    @NotBlank @Pattern(regexp = "^BUCKET_A_UNCONVERTED$") String bucket,
    @AssertTrue boolean redirected_to_whatsapp
) {
}
