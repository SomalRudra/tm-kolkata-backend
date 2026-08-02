# TM Kolkata Backend

Spring Boot API for landing-page registration and WhatsApp question leads.

## Run

```bash
mvn spring-boot:run
```

## Endpoints

- `POST /api/registrations`
- `POST /api/questions`
- `POST /api/leads/register`
- `POST /api/leads/inquiry`
- `PATCH /api/leads/update-status` requires admin access token
- `GET /api/leads` requires admin access token
- `GET /api/leads/export` requires admin access token and downloads a JSON snapshot
- `GET /api/events`
- `GET /api/events/admin` requires admin access token
- `POST /api/events` requires admin access token
- `PATCH /api/events/{id}` requires admin access token
- `DELETE /api/events/{id}` requires admin access token
- `POST /api/campaigns/broadcast` requires admin access token and sends email via SMTP or WhatsApp via `WHATSAPP_WEBHOOK_URL`
- `GET /api/health`
- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `POST /api/auth/forgot-password`
- `POST /api/auth/reset-password`
- `POST /api/auth/change-password`

Lead and admin auth data are stored in Postgres through Spring Data JPA. Public TM site lead capture remains open; analytics reads and graduation/status updates are admin protected.

## Railway

Railway should detect this as a Maven/Spring Boot service.

Recommended settings:

```text
Healthcheck Path: /api/health
Start Command: java -jar target/tm-kolkata-backend-0.0.1-SNAPSHOT.jar
```

Environment variables:

```text
PORT=8080
CORS_ALLOWED_ORIGINS=http://tmkolkata.org,https://tmkolkata.org,http://tm-kolkata.org,https://tm-kolkata.org,https://somalrudra.github.io,http://localhost:3000,http://localhost:4173
FRONTEND_RESET_URL=http://tmkolkata.org/analyticFunnel/reset-password/
ADMIN_USERNAME=admin
ADMIN_EMAIL=tmbengal108@gmail.com
ADMIN_PASSWORD=tmAdmin@2026
ACCESS_TOKEN_MINUTES=60
REFRESH_TOKEN_HOURS=3
```

`server.port` reads Railway's `PORT` variable automatically, with `8080` as a local fallback.

The first app start creates the configured admin if it does not exist. For an existing admin row, use `POST /api/auth/change-password` with the current password to update the stored password hash in Postgres.
