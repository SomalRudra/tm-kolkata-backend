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
- `PATCH /api/leads/update-status`
- `GET /api/leads`
- `GET /api/health`

The current repository stores leads in memory so the frontend/backend contract is ready. Replace `LeadRepository` with a database-backed repository when the production database is selected.

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
```

`server.port` reads Railway's `PORT` variable automatically, with `8080` as a local fallback.
