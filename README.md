# TM Kolkata Backend

Spring Boot API for landing-page registration and WhatsApp question leads.

## Run

```bash
mvn spring-boot:run
```

## Endpoints

- `POST /api/registrations`
- `POST /api/questions`

The current repository stores leads in memory so the frontend/backend contract is ready. Replace `LeadRepository` with a database-backed repository when the production database is selected.
