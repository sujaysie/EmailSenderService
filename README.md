# Kafka-Based Email Platform

This repository contains a scaffolded multi-module Spring Boot implementation of the requested email platform design:

- `template-service`: owns template lifecycle, versioning, and rendering.
- `mail-gateway`: accepts send requests, validates them, and writes Kafka-bound outbox records.
- `email-engine`: consumes send requests, renders content, builds MIME messages, and routes retries/DLQ flows.
- `shared-model`: shared Kafka event contracts.

The project is intentionally structured around the design supplied in the task so each service can evolve independently.


## React UI

A standalone React dashboard is available in `ui/` and mirrors the MailFlow platform view from the provided mockup.

```bash
cd ui
npm install
npm run dev
```

Create a production bundle with:

```bash
cd ui
npm run build
```
