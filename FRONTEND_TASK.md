# Frontend Delivery Tracker (React)

Use this file as the execution plan for building the React frontend against the current Spring backend.

## Status Legend
- `[ ]` Not started
- `[-]` In progress
- `[x]` Done
- `[!]` Blocked

## Scope (Based on Current Backend)
Implemented backend modules:
- Auth (`/auth/register`, `/auth/login`, `/auth/refresh`, `/auth/logout`)
- Service Catalog (`/service/user/services`, `/service/user/services/{id}`, admin service CRUD/status)

Not implemented yet in backend (frontend should keep placeholders):
- Purchases
- Dashboards

## API Contracts You Must Follow

Base URL:
- `http://localhost:8080`

Auth:
1. `POST /auth/register`
```json
{
  "name": "User One",
  "email": "user1@example.com",
  "password": "User@12345",
  "phoneNumber": "9999999999",
  "role": "USER"
}
```
2. `POST /auth/login`
```json
{
  "email": "user1@example.com",
  "password": "User@12345"
}
```
3. `POST /auth/refresh`
```json
{
  "refreshToken": "<refreshToken>"
}
```
4. `POST /auth/logout` with `Authorization: Bearer <accessToken>`

Auth response shape:
```json
{
  "accessToken": "...",
  "refreshToken": "...",
  "tokenType": "Bearer"
}
```

Service APIs:
1. `GET /service/user/services?page=0&size=10` (authenticated USER/ADMIN)
2. `GET /service/user/services/{id}`
3. `POST /service/admin/services` (ADMIN)
4. `PUT /service/admin/services/updateService/{id}` (ADMIN)
5. `PATCH /service/admin/services/updatedStatus/{id}?status=false` (ADMIN)

Service response shape:
```json
{
  "id": 1,
  "code": "GST-FILING",
  "name": "GST Filing",
  "description": "Monthly GST filing support",
  "price": 499.00,
  "active": true
}
```

Backend error shape (use globally in UI):
```json
{
  "timestamp": "2026-03-25T10:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid credentials",
  "path": "/auth/login"
}
```

## Recommended Frontend Stack
- React + Vite (JavaScript or TypeScript)
- React Router
- Axios
- React Hook Form + Zod/Yup
- Zustand or Redux Toolkit (optional; Context is enough for this scope)
- Tailwind CSS or plain CSS modules
- React Query (recommended for pagination + cache)

## Phase 0: Project Bootstrap
Owner: Frontend
Target: Day 1

- [ ] Create app using Vite
- [ ] Setup folder structure:
  - `src/app`
  - `src/pages`
  - `src/components`
  - `src/features/auth`
  - `src/features/services`
  - `src/api`
  - `src/hooks`
  - `src/utils`
- [ ] Install dependencies (router, axios, form, validation)
- [ ] Add `.env`:
  - `VITE_API_BASE_URL=http://localhost:8080`
- [ ] Create API client with base URL + timeout
- [ ] Add global layout and navigation shell

Definition of done:
- App runs locally and has route skeleton + API client ready.

## Phase 1: Authentication Flow
Owner: Frontend
Target: Day 2-3

### Pages
- [ ] `/login`
- [ ] `/register`

### Tasks
- [ ] Build register form (`name`, `email`, `password`, `phoneNumber`, `role`)
- [ ] Build login form (`email`, `password`)
- [ ] Store `accessToken` + `refreshToken` after login/register
- [ ] Decode role from JWT payload or keep selected role from login context
- [ ] Build `AuthContext` (`user`, `tokens`, `login`, `logout`, `refresh`)
- [ ] Auto-attach bearer token through Axios request interceptor
- [ ] On `401`, call `/auth/refresh`; retry failed request once
- [ ] If refresh fails, clear session and redirect to `/login`
- [ ] Implement `/auth/logout` on sign-out

### Route Protection
- [ ] Protected route wrapper for authenticated users
- [ ] Role route wrapper for admin pages (`ADMIN` only)

Definition of done:
- User can register/login/logout and stay authenticated across page refresh.

## Phase 2: User Service Catalog UI
Owner: Frontend
Target: Day 4-5

### Pages
- [ ] `/services` (list)
- [ ] `/services/:id` (detail)

### Tasks
- [ ] Fetch paginated services from `/service/user/services?page=0&size=10`
- [ ] Render service cards/table with `code`, `name`, `description`, `price`
- [ ] Add pagination controls (next/prev + page size)
- [ ] Add empty/error/loading states
- [ ] Detail page using `/service/user/services/{id}`

Definition of done:
- Logged-in user can browse active services with pagination and detail view.

## Phase 3: Admin Service Management UI
Owner: Frontend
Target: Day 6-7

### Pages
- [ ] `/admin/services`
- [ ] `/admin/services/new`
- [ ] `/admin/services/:id/edit`

### Tasks
- [ ] Build create form for `POST /service/admin/services`
- [ ] Build update form for `PUT /service/admin/services/updateService/{id}`
- [ ] Add activate/deactivate toggle for `PATCH /service/admin/services/updatedStatus/{id}?status=`
- [ ] Show action feedback (success/error toast)
- [ ] Hide admin menus for non-admin users

Definition of done:
- Admin can create, edit, activate/deactivate services from UI.

## Phase 4: Error Handling, UX, and Hardening
Owner: Frontend
Target: Day 8

- [ ] Centralized API error parser for `ApiError` shape
- [ ] Friendly inline form validation messages
- [ ] Global 401/403/404 pages
- [ ] Skeleton loaders and optimistic UI where safe
- [ ] Add confirmation dialog before status toggle/logout
- [ ] Accessibility checks (labels, keyboard nav, contrast)

Definition of done:
- UI handles expected backend and validation errors cleanly.

## Phase 5: Testing and Delivery
Owner: Frontend
Target: Day 9-10

- [ ] Unit tests for auth utilities and interceptors
- [ ] Component tests for login/register/service forms
- [ ] Integration test for login -> service list flow
- [ ] Build production bundle
- [ ] Add deployment env docs (`VITE_API_BASE_URL`)

Definition of done:
- Core user and admin flows are test-covered and deployable.

## Suggested Routing Map
- `/login`
- `/register`
- `/services`
- `/services/:id`
- `/admin/services`
- `/admin/services/new`
- `/admin/services/:id/edit`
- `/unauthorized`
- `*` (not found)

## Suggested Frontend API Layer
Implement these methods in `src/api`:
- `authApi.register(payload)`
- `authApi.login(payload)`
- `authApi.refresh(refreshToken)`
- `authApi.logout()`
- `serviceApi.list({ page, size })`
- `serviceApi.getById(id)`
- `serviceApi.create(payload)`
- `serviceApi.update(id, payload)`
- `serviceApi.setStatus(id, status)`

## Known Backend Considerations (Important)
- [ ] CORS is not configured in backend security config. If frontend runs on `http://localhost:5173`, add backend CORS config or dev proxy.
- [ ] `ServiceCreateRequest` currently uses `@DecimalMax("0.0")` for `price`, which may reject valid positive prices. Confirm/fix backend to `@DecimalMin("0.0")`.
- [ ] Access token expiry is ~15 minutes (`900000 ms`), refresh token is 7 days (`604800000 ms`). Frontend refresh flow is mandatory.
- [ ] Current JWT contains subject (email), not explicit role claim. UI role may need separate role source (login context, backend enhancement, or decode from secured profile endpoint when added).

## Future Placeholders (Until Backend Is Ready)
- [ ] Purchases module routes and APIs
- [ ] User dashboard tables
- [ ] Admin metrics dashboard

## Final Acceptance Checklist
- [ ] Auth flow works end-to-end (register/login/refresh/logout)
- [ ] User can list and view services
- [ ] Admin can create/update/toggle service status
- [ ] Token refresh works seamlessly on expired access token
- [ ] Unauthorized access is blocked and redirected cleanly
- [ ] Build succeeds and app can be deployed with env-based API URL
