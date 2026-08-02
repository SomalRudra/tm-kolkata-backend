package in.tmkolkata.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public AuthResponse login(@Valid @RequestBody LoginRequest request) {
    return authService.login(request.username(), request.password());
  }

  @PostMapping("/refresh")
  public AuthResponse refresh(@Valid @RequestBody RefreshRequest request) {
    return authService.refresh(request.refresh_token());
  }

  @PostMapping("/forgot-password")
  public ForgotPasswordResponse forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
    return authService.forgotPassword(request.email());
  }

  @PostMapping("/reset-password")
  public Map<String, Boolean> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
    authService.resetPassword(request.token(), request.new_password());
    return Map.of("ok", true);
  }

  @PostMapping("/change-password")
  public Map<String, Boolean> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
    authService.changePassword(request.username(), request.current_password(), request.new_password());
    return Map.of("ok", true);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public Map<String, String> unauthorized(IllegalArgumentException exception) {
    return Map.of("error", exception.getMessage());
  }

  public record LoginRequest(
      @NotBlank String username,
      @NotBlank String password
  ) {
  }

  public record RefreshRequest(
      @NotBlank String refresh_token
  ) {
  }

  public record ForgotPasswordRequest(
      @NotBlank @Email String email
  ) {
  }

  public record ResetPasswordRequest(
      @NotBlank String token,
      @NotBlank @Size(min = 8, max = 120) String new_password
  ) {
  }

  public record ChangePasswordRequest(
      @NotBlank String username,
      @NotBlank String current_password,
      @NotBlank @Size(min = 8, max = 120) String new_password
  ) {
  }
}
