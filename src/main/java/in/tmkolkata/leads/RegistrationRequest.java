package in.tmkolkata.leads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(
    @NotBlank @Size(min = 2, max = 120) String fullName,
    @NotBlank @Email String email,
    @NotBlank @Pattern(regexp = "^[0-9+\\-\\s]{8,15}$") String phone,
    @NotBlank String cityArea,
    @NotBlank String preferredDate
) {
}
