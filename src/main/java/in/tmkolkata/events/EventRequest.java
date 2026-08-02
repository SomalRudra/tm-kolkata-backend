package in.tmkolkata.events;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EventRequest(
    @NotBlank String title,
    @NotBlank String kolkata_region,
    @NotBlank String event_mode,
    @NotBlank String event_date,
    @NotBlank String venue,
    @NotNull @Min(1) Integer capacity,
    Boolean published
) {
}
