# Backend Delivery Tracker

Use this file as the single source of truth for progress. Update checkboxes as you complete items.

## Status Legend
- `[ ]` Not started
- `[-]` In progress
- `[x]` Done
- `[!]` Blocked

## Project Goal
Build backend APIs for:
- User login/register with role-based access
- Service listing for users
- Service purchase flow
- Purchase tracking/audit
- User dashboard (own purchases)
- Admin dashboard (all users and purchases)

---

## Phase 0: Foundation Fixes
Owner: Backend
Target: Week 1 Day 1

- [x] Fix `User` entity mapping and fields
- [x] Add missing getters/setters (`refreshToken`, etc.)
- [x] Add `@Enumerated(EnumType.STRING)` for role
- [x] Fix `CustomUserDetails#getUsername` and `getPassword`
- [x] Fix authority prefix to `ROLE_`
- [x] Confirm app context loads without errors
- [x] Remove insecure hardcoded default credentials from `application.properties`

Definition of done:
- App starts cleanly
- `mvn test` passes base context test

---

## Phase 1: Authentication APIs
Owner: Backend
Target: Week 1

### Endpoints
- [x] `POST /auth/register`
- [x] `POST /auth/login`
- [x] `POST /auth/refresh`
- [x] `POST /auth/logout`

### Tasks
- [x] Create auth request/response DTOs
- [x] Implement password hashing with BCrypt
- [x] Implement JWT access token generation
- [x] Implement refresh token persistence/rotation
- [X] Add auth exception handling
- [x] Add validation (`@Valid`) for all auth payloads

### Security Rules
- [x] Permit `/auth/**`
- [x] Require authentication for all other APIs
- [x] Add JWT filter in security chain

Definition of done:
- Register/login works from API client
- Protected route rejects without bearer token

---

## Phase 2: Service Catalog APIs
Owner: Backend
Target: Week 2

### Data Model
- [x] Create `ServiceItem` entity
- [x] Add fields: `code`, `name`, `description`, `price`, `active`, timestamps
- [x] Add unique constraints for `code`

### Endpoints
User:
- [x] `GET /service/user/services`
- [x] `GET /service/user/services/{id}`

Admin:
- [x] `POST /service/admin/services`
- [x] `PUT /service/admin/services/updateService/{id}`
- [x] `PATCH /service/admin/services/updatedStatus/{id}/`

### Tasks
- [x] Add filtering (active only for users)
- [x] Add pagination for list endpoint
- [x] Add request/response DTO mapping

Definition of done:
- User can view active services
- Admin can create/update/disable services

---

## Phase 3: Purchase APIs + Tracking
Owner: Backend
Target: Week 3

### Data Model
- [x] Create `Purchase` entity
- [x] Create `PurchaseEvent` audit entity
- [x] Add purchase status enum (`CREATED`, `SUCCESS`, `FAILED`, `REFUNDED`)

### Endpoints
User:
- [x] `POST /purchases`
- [x] `GET /purchases/me`
- [x] `GET /purchases/me/{id}`

Admin:
- [x] `GET /admin/purchases`
- [x] `GET /admin/purchases/{id}`

### Tasks
- [x] Validate service is active before purchase
- [x] Record purchase event on create/status change
- [x] Prevent user from accessing other user purchases
- [x] Add pagination and filtering by date/status/service

Definition of done:
- User purchase is persisted and auditable
- Admin can inspect all purchases

---

## Phase 4: Dashboard APIs
Owner: Backend
Target: Week 4

### Endpoints
User:
- [x] `GET /dashboard/me/purchases`

Admin:
- [x] `GET /admin/dashboard/purchases`
- [x] `GET /admin/dashboard/metrics`

### User Dashboard Table Fields
- [x] `purchaseId`
- [x] `serviceName`
- [x] `amount`
- [x] `status`
- [x] `purchasedAt`

### Admin Dashboard Extra Fields
- [x] `userId`
- [x] `userEmail`

### Tasks
- [x] Add date range filters (`from`, `to`)
- [x] Add metrics (total purchases, success count, revenue)

Definition of done:
- User sees own purchase table data
- Admin sees full system purchase overview

---

## Phase 5: Reliability, Quality, and Docs
Owner: Backend
Target: Week 5

- [-] Add global exception handler and standard error response
- [ ] Add OpenAPI/Swagger documentation
- [ ] Add integration tests for auth/service/purchase/dashboard flows
- [ ] Add role-based authorization tests (`USER` vs `ADMIN`)
- [ ] Add database migrations (Flyway/Liquibase)
- [ ] Add seed data script for service catalog
- [ ] Add logging for critical actions (login, purchase, admin updates)

Definition of done:
- API behavior is documented and tested
- Schema is migration-driven (not manual)

---

## Non-Functional Checklist
- [ ] Use UTC timestamps in DB and API
- [ ] Add indexes for high-usage queries
- [x] Add request validation to every write endpoint
- [x] Avoid returning internal entities directly (DTO only)
- [ ] Keep secrets out of repo (`application.properties` -> env vars)
- [ ] Enforce consistent API response shape

---

## Suggested Branch Plan
- [ ] `feature/auth`
- [ ] `feature/service-catalog`
- [ ] `feature/purchase-tracking`
- [ ] `feature/dashboard`
- [ ] `chore/tests-docs-hardening`

---

## Weekly Milestone Check
Week 1:
- [ ] Foundation + Auth complete

Week 2:
- [x] Services complete

Week 3:
- [x] Purchases + tracking complete

Week 4:
- [x] Dashboard complete

Week 5:
- [ ] Tests, docs, hardening complete

---

## Notes / Blockers
- [ ] (Add blockers here with date and owner)

Example:
- [!] 2026-03-07 - Waiting for payment gateway sandbox credentials - Owner: Backend
