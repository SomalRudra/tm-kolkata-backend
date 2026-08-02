package in.tmkolkata.auth;

public record ForgotPasswordResponse(
    boolean ok,
    boolean email_sent,
    String reset_url
) {
}
