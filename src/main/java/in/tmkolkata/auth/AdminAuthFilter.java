package in.tmkolkata.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AdminAuthFilter extends OncePerRequestFilter {

  private final AuthService authService;

  public AdminAuthFilter(AuthService authService) {
    this.authService = authService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (!requiresAdmin(request)) {
      filterChain.doFilter(request, response);
      return;
    }

    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing admin access token");
      return;
    }

    String token = authHeader.substring("Bearer ".length());
    if (authService.validateAccessToken(token).isEmpty()) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid admin access token");
      return;
    }

    filterChain.doFilter(request, response);
  }

  private boolean requiresAdmin(HttpServletRequest request) {
    String path = request.getRequestURI();
    String method = request.getMethod();

    if ("OPTIONS".equals(method)) {
      return false;
    }

    return ("/api/leads".equals(path) && "GET".equals(method))
        || ("/api/leads/export".equals(path) && "GET".equals(method))
        || ("/api/leads/update-status".equals(path) && "PATCH".equals(method))
        || ("/api/events/admin".equals(path) && "GET".equals(method))
        || ("/api/events".equals(path) && "POST".equals(method))
        || (path.startsWith("/api/events/") && ("PATCH".equals(method) || "DELETE".equals(method)))
        || ("/api/campaigns/broadcast".equals(path) && "POST".equals(method));
  }
}
