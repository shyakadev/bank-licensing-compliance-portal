# Bank Licensing & Compliance Portal 
![CI](https://github.com/shyakadev/bank-licensing-compliance-portal/actions/workflows/main.yml/badge.svg)


---

Bank licensing & Compliance applications. Enforces a strict state machine across applicant, reviewer, approver, and admin roles 
with a legal-grade immutable audit trail.

For architecture, data model, request lifecycle sequence and the reasoning behind every hard decision.

### Find: [Design Document](Design.md)

---

## Prerequisites

- Java 17+
- Node 18+
- Docker (required — integration tests use Testcontainers to spin up a real PostgreSQL instance)

---

## Setup & Running

### Backend

```bash
git clone https://github.com/shyakadev/bank-licensing-compliance-portal
cd bank-licensing-compliance-portal
```

### Environment Variables

This project uses environment variables for sensitive configuration. Create a `.env` file in the project root:

**Example:**
```bash
# .env
DB_URL=jdbc:postgresql://localhost:5432/licensing_service?stringtype=unspecified
DB_USERNAME=license_app
DB_PASSWORD=password
JWT_SECRET=K8l9eV8mQ2xKNiX3T1uWc8RbHs3Jd6ZaXv0LgBnCyEk=
JWT_ACCESS_TOKEN_EXPIRATION=600000
JWT_REFRESH_TOKEN_EXPIRATION=604800000

````
```bash
Then load the variables and start the application:

**Linux / macOS**
```bash
export $(cat .env | xargs)
./gradlew bootRun
```

**Windows (Command Prompt)**
```cmd
set JWT_SECRET=mySuperSecretJWTKey
set JWT_ACCESS_TOKEN_EXPIRATION=600000
set JWT_REFRESH_TOKEN_EXPIRATION=604800000
gradlew bootRun
```

**Windows (PowerShell)**
```powershell
$env:JWT_SECRET="mySuperSecretJWTKey"
$env:JWT_ACCESS_TOKEN_EXPIRATION="600000"
$env:JWT_REFRESH_TOKEN_EXPIRATION="604800000"
./gradlew bootRun
```

## Running Tests

```bash
./gradlew test
```

Flyway runs automatically on startup schema is created and seed data is loaded. 
No manual database setup required.

---

## Seeded Users

| Role      | Username  | Password | Name            | Email               | Type      | Title      |
|-----------|-----------|----------|-----------------|---------------------|-----------|------------|
| Admin     | admin     | password | John Doe        | d.john@bnr.rw       | Staff     | IT Admin   |
| Reviewer  | reviewer  | password | Eric Marc       | m.eric@bnr.rw       | Staff     | Analyst    |
| Approver  | approver  | password | Rebecca Brenda  | b.rebecca@bnr.rw    | Staff     | Sr Analyst |
| Applicant | applicant | password | Mary Grace      | g.mary@bnr.rw       | Applicant | CFO        |

> **Note:** Change all credentials before deploying to any shared environment.
 
---

## Seeded Applications

| ID | Status | Notes |
|----|--------|-------|
| 1 | PENDING | Available for reviewer to claim |
| 2 | UNDER_REVIEW | Assigned to seeded reviewer |
| 3 | REVIEWED | Assigned to seeded approver, ready for decision |
| 4 | AWAITING_RESUBMISSION | Resubmission reason populated, document uploaded |
 
---


**Docker must be running.** Integration tests spin up a real PostgreSQL container via Testcontainers. Tests will fail if Docker is not available.


