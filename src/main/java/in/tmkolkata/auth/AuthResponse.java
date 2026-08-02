package in.tmkolkata.auth;

public record AuthResponse(
    String access_token,
    String refresh_token,
    long expires_in,
    String username
) {
}
