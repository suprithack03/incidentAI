# IncidentAI — Distributed Incident Monitoring and Root Cause Analysis

IncidentAI is a distributed incident monitoring and root cause analysis platform built to simulate service failures, detect anomalies, group related events into incidents, classify severity, and support investigations using AI and retrieval-augmented generation (RAG).

The project uses a microservices architecture with a React dashboard, Spring Boot services, PostgreSQL, Redis, and Gemini-based investigation.

## Key Features

- **Microservices:** Separate User, Payment, Inventory, and Incident Core services.
- **Authentication and authorization:** Spring Security, JWT authentication, and role-based access control (RBAC).
- **Failure simulation:** Failure injection endpoints for simulating service and dependency issues.
- **Structured logging:** Services produce structured logs and report relevant events to Incident Core.
- **Anomaly detection:** Rule-based detection for database failures, Redis failures, slow responses, timeouts, and elevated HTTP 5xx errors.
- **Incident grouping:** Groups related anomalies within a configured 10-minute window.
- **Severity classification:** Assigns CRITICAL, HIGH, MEDIUM, or LOW severity based on service and dependency impact.
- **Incident management:** Stores incidents and anomalies, supports incident queries, and allows status updates.
- **Redis caching:** Uses cache-aside behaviour with a 10-minute TTL and database fallback.
- **AI-powered RCA:** Uses Gemini to generate structured root cause analysis, including probable cause, evidence, affected service, confidence, and remediation.
- **RAG-based investigation:** Retrieves relevant incident logs, historical incidents, and runbooks to provide context for RCA.
- **React dashboard:** Provides login and a dashboard to view incidents, anomalies, service health, RCA, and incident status.
- **Containerized deployment:** Uses Docker and Docker Compose with PostgreSQL and Redis.

## Architecture

```text
                  React Dashboard
                         |
                         | REST API + JWT
                         v
                 User Service
                  (Auth / Users)
                         |
          +--------------+--------------+
          |              |              |
          v              v              v
    Payment Service  Inventory      Incident Core
                     Service       (Detection / RCA)
          |              |              |
          +--------------+--------------+
                         |
                  Structured Logs
                         |
                         v
                  Incident Core
                         |
             +-----------+-----------+
             |           |           |
             v           v           v
         PostgreSQL    Redis     Gemini + RAG
          + pgvector               Runbooks /
                                  Past Incidents
```

The diagram is a conceptual overview. The exact request and event flow depends on the endpoint and service involved.

## Technology Stack

| Component | Technology |
|---|---|
| Backend | Java 17, Spring Boot 4.0.8 |
| API | REST |
| Security | Spring Security, JWT, RBAC |
| Persistence | Spring Data JPA, PostgreSQL |
| Vector storage | PostgreSQL with pgvector |
| Caching | Redis 7 |
| AI | Gemini API |
| AI retrieval | RAG and embeddings |
| Frontend | React 19, JavaScript, Vite |
| Build | Maven, npm |
| Containers | Docker, Docker Compose |
| Version control | Git, GitHub |

## Repository Structure

```text
incidentai/
├── frontend/             # React dashboard
│   ├── public/
│   ├── src/
│   │   ├── App.jsx
│   │   ├── Login.jsx
│   │   ├── App.css
│   │   └── index.css
│   ├── package.json
│   └── vite.config.js
│
├── user-service/         # Authentication, users, logging
├── payment-service/      # Payment service and failure simulation
├── inventory-service/    # Inventory service
├── incident-core/        # Detection, incidents, severity, RCA, RAG
│   └── src/main/resources/runbooks/
│
├── incidentai/           # Separate Spring Boot user CRUD application
├── docker-compose.yml
└── README.md
```

Each backend service contains its own Maven configuration and source code. The `incidentai` directory is a separate Spring Boot application and is not listed as a service in the current Docker Compose configuration.

## Incident Detection

Incident Core evaluates structured service logs using deterministic rules.

| Anomaly | Detection |
|---|---|
| Database failure | Detects database-related failures |
| Redis failure | Detects Redis-related failures |
| Slow response | Response time exceeds the configured 1000 ms threshold |
| Timeout | Detects timeout events |
| Elevated 5xx | More than 5 HTTP 5xx errors within 5 minutes |

Related anomalies are grouped into incidents within a configured 10-minute window. Incident severity is classified as CRITICAL, HIGH, MEDIUM, or LOW based on service criticality and dependency impact.

## AI-Powered Root Cause Analysis

IncidentAI integrates Gemini to generate structured root cause analysis.

The RCA response can include:
- Probable root cause
- Supporting evidence
- Affected service
- Confidence level
- Suggested remediation

If Gemini is unavailable or returns an invalid response, the application has a fallback path so the investigation can indicate that automated analysis is unavailable and manual review is needed.

## Retrieval-Augmented Generation (RAG)

The RAG workflow retrieves relevant context to support incident investigation.

It uses:
- Current incident logs
- Historical incident records
- Relevant operational runbooks
- Embeddings stored in PostgreSQL with pgvector

The project uses 768-dimensional embeddings. Five runbooks and seven historical incident records were loaded during development. Retrieved context is supplied to the RCA workflow to help ground the generated analysis.

## Docker Deployment

The current Docker Compose setup includes six containers:

| Service | Host port | Container port |
|---|---:|---:|
| PostgreSQL with pgvector | 5433 | 5432 |
| Redis | 6379 | 6379 |
| User Service | 8081 | 8080 |
| Payment Service | 8082 | 8080 |
| Inventory Service | 8083 | 8080 |
| Incident Core | 8084 | 8084 |

Docker Compose currently references prebuilt images for the four backend services. Build the JAR files and Docker images before starting the Compose environment.

### Prerequisites

- Java 17
- Maven wrapper included in the backend modules
- Node.js and npm
- Docker Desktop with Docker Compose

### 1. Build the backend JAR files

From the repository root in PowerShell:

```powershell
foreach ($service in "user-service", "payment-service", "inventory-service", "incident-core") {
    Push-Location $service
    .\mvnw.cmd clean package
    if ($LASTEXITCODE -ne 0) {
        Pop-Location
        throw "Build failed for $service"
    }
    Pop-Location
}
```

### 2. Build the backend Docker images

Run from the repository root:

```powershell
docker build -t incidentai-user-service:latest ./user-service
docker build -t incidentai-payment-service:latest ./payment-service
docker build -t incidentai-inventory-service:latest ./inventory-service
docker build -t incidentai-incident-core:latest ./incident-core
```

### 3. Configure environment variables

Configure the environment variables required by `docker-compose.yml`, including database credentials, the internal service token, and the Gemini API key.

Use your own local values. Do not commit passwords, API keys, tokens, or a secrets-containing `.env` file to GitHub.

### 4. Start the containers

```powershell
docker compose up -d
```

Check container status:

```powershell
docker compose ps
```

View logs for a particular service:

```powershell
docker compose logs -f incident-core
```

Stop the containers:

```powershell
docker compose down
```

The current Compose setup uses persistent volumes for PostgreSQL and Redis. `docker compose down -v` removes those volumes and their stored data, so use it only when you intentionally want to delete that data.

## Frontend

The frontend is a React application powered by Vite.

From the repository root:

```powershell
cd frontend
npm install
npm run dev
```

Vite starts the development server and prints the local URL in the terminal.

Other available frontend commands:

```powershell
npm run build
npm run lint
npm run preview
```

**Configuration note:** The current `Login.jsx` calls `http://localhost:8080/api/auth/login`, while Docker Compose maps the User Service to host port `8081`. The dashboard calls Incident Core on port `8084`. Align the frontend login API URL with the way the backend is being run before expecting login to work with Docker Compose.

## Verification

During development, the services and features were exercised incrementally, including authentication, protected endpoints, incident detection, incident persistence, RCA fallback, RAG data loading, and Docker container startup.

The Docker environment was checked for running containers, PostgreSQL readiness, Redis connectivity, and expected protected endpoint responses. These checks are development verification, not a formal performance benchmark or production-readiness certification.

## Configuration Reference

The following are implemented configuration values and data counts, not benchmark results:

| Item | Value |
|---|---:|
| Backend services | 4 |
| Docker containers | 6 |
| Incident grouping window | 10 minutes |
| Slow response threshold | 1000 ms |
| Elevated 5xx threshold | More than 5 within 5 minutes |
| Redis TTL | 10 minutes |
| Embedding dimensions | 768 |
| Runbooks loaded | 5 |
| Historical incidents loaded | 7 |

No controlled latency, throughput, detection-accuracy, or RCA-accuracy benchmark is claimed.

## Future Improvements

Potential future work includes:
- Automated CI/CD with GitHub Actions
- Expanded automated integration and end-to-end tests
- Configurable frontend API URLs
- Additional observability and deployment hardening

## Author

Developed as a portfolio project to explore distributed systems, incident monitoring, backend security, AI-assisted investigation, and containerized deployment.

## Repository

GitHub: https://github.com/suprithack03/incidentAI
