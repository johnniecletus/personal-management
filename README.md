# Personal Management API

Personal Management is a Spring Boot backend for a personal finance and lifestyle manager.

The app helps a user:

- sign up and manage an account
- define savings clusters for different income sources
- split each income into savings buckets automatically
- track savings history and monthly overview totals
- manage shopping or market lists
- work with shared system currencies

## Core Idea

The main product concept is the **savings cluster**.

A savings cluster is a named rule set that tells the app how to split an incoming amount into multiple savings targets.

Example:

- Cluster: `salary`
- Emergency savings: `30%`
- Tithe: `10%`
- Children savings: `20%`
- Main savings: `15%`

That totals `75%`, so the app automatically keeps the remaining `25%` as a **remainder allocation**.

Another example:

- Cluster: `gift`
- Emergency savings: `40%`
- Children savings: `30%`
- Main savings: `20%`
- House savings: `10%`

That totals `100%`, so the remainder is `0%`.

## Important Business Rules

### 1. Cluster totals can never exceed 100%

If the total percentage is above `100`, the request is rejected.

### 2. Remainder is automatic

The app does not require the user to manually create a "remainder" item.

- `remainderPercentage = 100 - totalClusterPercentage`
- the remainder is returned in cluster previews and cluster responses
- when income is created, the remainder is stored in `savingshistories` with a nullable `cluster_item_id`

### 3. Cluster templates can prefill percentages

If a user creates items under a recognized cluster template and leaves percentage empty, the app suggests a default.

Built-in templates:

- `salary`
  - `emergency savings = 30`
  - `tithe = 10`
  - `children savings = 20`
  - `main savings = 15`
- `gift`
  - `emergency savings = 40`
  - `children savings = 30`
  - `main savings = 20`
  - `house savings = 10`

The user can still override any percentage manually.

### 4. Money calculations are balancing-safe

Money allocation is done with `BigDecimal`.

To avoid over-allocation caused by rounding:

- normal percentage allocations are rounded down to 2 decimal places
- if the cluster totals `100%`, the last user-defined bucket absorbs any remaining cents
- if the cluster totals less than `100%`, the remainder bucket absorbs the remaining cents

This guarantees:

- the total allocated amount always matches the income amount
- savings history and monthly overview totals remain consistent

### 5. Creating income also creates history and overview data

When an income is created, the app:

1. stores the income record
2. calculates all bucket allocations
3. stores savings history entries
4. updates or creates the matching monthly overview row for the income month and currency

## Modules

### Auth

- JWT access token
- rotating JWT refresh token
- HttpOnly cookie support
- silent refresh on protected requests
- database-backed session revocation
- current-user and admin session management endpoints
- logout endpoint
- CSRF token endpoint for browser clients

### Users

- current profile
- profile update
- password update
- delete current user
- admin-only list/get/delete endpoints

### Savings Clusters

- preview cluster before save
- create cluster
- update cluster
- get one cluster
- list user clusters
- delete cluster if it is not already linked to income

### Incomes

- preview allocation before save
- create income
- update income
- get one income
- list incomes
- delete income

### Reports

- monthly overview listing
- savings history listing

### Shopping Lists

- create task list
- update task list
- get one task list
- list task lists
- complete all task items
- delete task list

### Currencies

Currencies are shared system data, not user-owned data.

Seeded by migration:

- `NGN`
- `USD`
- `EUR`
- `GBP`

## Tech Stack

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- Flyway
- MySQL
- JJWT
- H2 for tests

## Configuration

Set these environment variables before running the app:

```bash
DB_URL=jdbc:mysql://127.0.0.1:3306/personal_management
DB_USERNAME=root
DB_PASSWORD=your_password

JWT_PRIVATE_KEY=base64_of_full_private_pem_text
JWT_PUBLIC_KEY=base64_of_full_public_pem_text
JWT_ACCESS_EXPIRATION=900000
JWT_REFRESH_EXPIRATION=604800000

APP_SECURITY_COOKIES_SECURE=false
```

Notes:

- `JWT_PRIVATE_KEY` and `JWT_PUBLIC_KEY` must be the base64-encoded PEM text, not only the raw key bytes.
- `JWT_ACCESS_EXPIRATION` and `JWT_REFRESH_EXPIRATION` are in milliseconds.
- `JWT_REFRESH_EXPIRATION` is the hard maximum lifetime of a login session. Silent refresh rotates the token but does not extend that lifetime forever.
- set `APP_SECURITY_COOKIES_SECURE=true` in HTTPS production environments.

Example key generation:

```bash
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 > private.pem
openssl pkey -in private.pem -pubout > public.pem
base64 < private.pem
base64 < public.pem
```

## Running Locally

```bash
./mvnw spring-boot:run
```

## Running Tests

```bash
./mvnw test
```

Tests run against H2 in MySQL compatibility mode and execute the same Flyway migrations used by the main app.

## API Overview

### Auth

- `GET /api/v1/auth/csrf`
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/logout`
- `GET /api/v1/auth/me`
- `GET /api/v1/auth/sessions`
- `DELETE /api/v1/auth/sessions/{sessionId}`
- `GET /api/v1/auth/users/{userId}/sessions` admin only
- `DELETE /api/v1/auth/users/{userId}/sessions/{sessionId}` admin only
- `DELETE /api/v1/auth/users/{userId}/sessions` admin only

### Users

- `GET /api/v1/users` admin only
- `GET /api/v1/users/{id}` admin only
- `GET /api/v1/users/me`
- `PATCH /api/v1/users/me`
- `PATCH /api/v1/users/me/password`
- `DELETE /api/v1/users/me`
- `DELETE /api/v1/users/{id}` admin only

### Currencies

- `GET /api/v1/currencies`
- `GET /api/v1/currencies/{id}`
- `POST /api/v1/currencies` admin only
- `PUT /api/v1/currencies/{id}` admin only
- `DELETE /api/v1/currencies/{id}` admin only

### Savings Clusters

- `POST /api/v1/savings-clusters/preview`
- `POST /api/v1/savings-clusters`
- `GET /api/v1/savings-clusters`
- `GET /api/v1/savings-clusters/{id}`
- `PUT /api/v1/savings-clusters/{id}`
- `DELETE /api/v1/savings-clusters/{id}`

### Incomes

- `POST /api/v1/incomes/preview`
- `POST /api/v1/incomes`
- `GET /api/v1/incomes`
- `GET /api/v1/incomes/{id}`
- `PUT /api/v1/incomes/{id}`
- `DELETE /api/v1/incomes/{id}`

### Reports

- `GET /api/v1/reports/monthly-overviews`
- `GET /api/v1/reports/savings-histories`

### Task Lists

- `POST /api/v1/task-lists`
- `GET /api/v1/task-lists`
- `GET /api/v1/task-lists/{id}`
- `PUT /api/v1/task-lists/{id}`
- `PATCH /api/v1/task-lists/{id}/complete-all`
- `DELETE /api/v1/task-lists/{id}`

## Browser Client Note

For browser clients using cookie-based auth, fetch a CSRF token first:

1. `GET /api/v1/auth/csrf`
2. read the CSRF token from the response
3. send it back on `POST`, `PUT`, `PATCH`, and `DELETE` requests

Also make sure the frontend sends requests with credentials enabled so the browser includes the auth cookies.

Silent refresh works like this:

1. the browser sends `access_token` and `refresh_token` cookies automatically
2. if the access token is still valid and the server-side session is active, the request continues normally
3. if the access token is expired or invalid but the refresh token is still valid and the session has not been revoked, the backend issues fresh cookies and continues the same request
4. if the session has been logged out, revoked, or has reached its hard expiry, the backend stops the refresh chain and returns `401`

Session state is tracked in the `auth_sessions` table. This is what makes logout, user session revocation, and admin session invalidation meaningful even without Redis.

## Contribution Notes

If you contribute to this project, please preserve these invariants:

- cluster percentage totals must never exceed `100`
- income allocation totals must always equal the source income amount
- monthly overview totals must stay in sync with income and savings history changes
- user-owned data must always be filtered by authenticated user ownership
- shared currencies must remain system-owned, not user-owned

## Current Test Coverage Focus

The included tests cover the highest-risk logic:

- silent refresh and session revocation flow
- cluster template autofill and remainder logic
- income allocation and monthly overview updates
