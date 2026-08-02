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
- `GET /api/health`
- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `POST /api/auth/forgot-password`
- `POST /api/auth/reset-password`

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
CORS_ALLOWED_ORIGINS=https://somalrudra.github.io,http://localhost:3000,http://localhost:4173
FRONTEND_RESET_URL=https://somalrudra.github.io/tm-kolkata-analytics-funnel/reset-password/
ADMIN_USERNAME=admin
ADMIN_EMAIL=tmbengal108@gmail.com
ADMIN_PASSWORD=<set-on-first-deploy-only>
RESEND_API_KEY=<resend-api-key>
RESEND_FROM_EMAIL=TM Kolkata <onboarding@resend.dev>
```

`RESEND_API_KEY` sends reset emails through Resend's HTTPS API on port 443, which works on Railway plans where outbound SMTP is blocked. Use `onboarding@resend.dev` for initial tests, or verify a domain in Resend and set `RESEND_FROM_EMAIL` to an address on that domain.

SMTP variables are still supported as a fallback when `RESEND_API_KEY` is empty:

```text
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=<gmail-address>
SMTP_PASSWORD=<gmail-app-password>
SMTP_AUTH=true
SMTP_STARTTLS=true
```

`server.port` reads Railway's `PORT` variable automatically, with `8080` as a local fallback.

The first app start creates the configured admin if it does not exist. After the admin password is reset in the dashboard, the password hash is stored in Postgres.
