# Kafka-Based Email Platform

This repository contains a multi-module Spring Boot implementation of an event-driven email platform.

## Components

- `template-service`: Owns template lifecycle, versioning, and rendering.
- `mail-gateway`: Accepts send requests, validates them, and writes Kafka-bound outbox records.
- `email-engine`: Consumes send requests, renders content, builds MIME messages, and handles retry/DLQ flows.
- `shared-model`: Shared Java model package for Kafka event contracts.
- `ui`: React dashboard for viewing/interacting with the platform.

---

## 1) Prerequisites

Install the following locally:

- Java 17+
- Maven 3.9+
- Node.js 20+ and npm (for `ui`)
- Docker + Docker Compose (recommended for infra dependencies)

---

## 2) Deploy third-party dependencies with Docker

The platform depends on PostgreSQL, Kafka, Redis, and an SMTP sink. The fastest approach is running these via Docker.

### 2.1 Create `docker-compose.yml`

Create this file at the repository root:

```yaml
services:
  postgres-template:
    image: postgres:16
    container_name: postgres-template
    environment:
      POSTGRES_DB: template_service
      POSTGRES_USER: template_service_user
      POSTGRES_PASSWORD: template_service_pass
    ports:
      - "5433:5432"
    volumes:
      - pg_template_data:/var/lib/postgresql/data

  postgres-gateway:
    image: postgres:16
    container_name: postgres-gateway
    environment:
      POSTGRES_DB: mail_gateway
      POSTGRES_USER: mail_gateway_user
      POSTGRES_PASSWORD: mail_gateway_pass
    ports:
      - "5434:5432"
    volumes:
      - pg_gateway_data:/var/lib/postgresql/data

  zookeeper:
    image: confluentinc/cp-zookeeper:7.6.1
    container_name: zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000

  kafka:
    image: confluentinc/cp-kafka:7.6.1
    container_name: kafka
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: "true"

  redis:
    image: redis:7
    container_name: redis
    ports:
      - "6379:6379"

  mailhog:
    image: mailhog/mailhog:v1.0.1
    container_name: mailhog
    ports:
      - "1025:1025"
      - "8025:8025"

volumes:
  pg_template_data:
  pg_gateway_data:
```

### 2.2 Start infrastructure

```bash
docker compose up -d
```

### 2.3 Verify infrastructure health

```bash
docker ps
```

Expected listening ports:

- PostgreSQL (`template-service`): `localhost:5433`
- PostgreSQL (`mail-gateway`): `localhost:5434`
- Kafka broker: `localhost:9092`
- Redis: `localhost:6379`
- SMTP (MailHog): `localhost:1025`
- MailHog UI: `http://localhost:8025`

> Note: Services in this repository currently point PostgreSQL to port `5432` in `application.yml`. Either map one DB to 5432 and update the other service, or keep the above compose ports and override datasource URLs at runtime (examples included below).

---

## 3) Build all Java modules

From repository root:

```bash
mvn clean install
```

This builds:

- `shared-model`
- `template-service`
- `mail-gateway`
- `email-engine`

---

## 4) Deploy each backend component

Run each service in its own terminal from repository root.

## 4.1 `shared-model`

`shared-model` is a library module and is deployed as an internal artifact dependency when you run root Maven build.

No standalone runtime process is needed.

## 4.2 `template-service` deployment

Default service port: `8082`.

### Option A: Run with current defaults (requires Postgres on `localhost:5432`)

```bash
mvn -pl template-service spring-boot:run
```

### Option B: Run against Docker DB shown above (port `5433`)

```bash
mvn -pl template-service spring-boot:run \
  -Dspring-boot.run.jvmArguments="-Dspring.datasource.url=jdbc:postgresql://localhost:5433/template_service"
```

Health check:

```bash
curl http://localhost:8082/actuator/health
```

## 4.3 `mail-gateway` deployment

Default service port: `8081`.

Requires Kafka (`localhost:9092`) and Postgres.

### Option A: Run with current defaults (requires Postgres on `localhost:5432`)

```bash
mvn -pl mail-gateway spring-boot:run
```

### Option B: Run against Docker DB shown above (port `5434`)

```bash
mvn -pl mail-gateway spring-boot:run \
  -Dspring-boot.run.jvmArguments="-Dspring.datasource.url=jdbc:postgresql://localhost:5434/mail_gateway"
```

Health check:

```bash
curl http://localhost:8081/actuator/health
```

## 4.4 `email-engine` deployment

Default service port: `8083`.

Requires Kafka (`localhost:9092`), Redis (`localhost:6379`), and SMTP sink (`localhost:1025`).

```bash
mvn -pl email-engine spring-boot:run
```

Health check:

```bash
curl http://localhost:8083/actuator/health
```

---

## 5) Deploy UI component (`ui`)

The UI is a standalone React app.

### Development run

```bash
cd ui
npm install
npm run dev
```

### Production build

```bash
cd ui
npm ci
npm run build
```

Preview build locally:

```bash
npm run preview
```

---

## 6) Suggested startup order (local environment)

1. Start Docker dependencies (`docker compose up -d`).
2. Start `template-service`.
3. Start `mail-gateway`.
4. Start `email-engine`.
5. Start `ui` (optional for backend-only testing).

---

## 7) Smoke test flow

1. Open MailHog at `http://localhost:8025`.
2. Create or verify templates in `template-service`.
3. Submit a send request to `mail-gateway`.
4. Verify Kafka flow and consumer processing in `email-engine` logs.
5. Confirm delivered email in MailHog inbox.

---

## 8) Stop and clean up

Stop Java services (`Ctrl + C` in each terminal), then:

```bash
docker compose down
```

To remove volumes too:

```bash
docker compose down -v
```
