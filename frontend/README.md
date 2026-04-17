# User Management Frontend

React frontend for the Spring backend in this repository.

## Features Implemented
- UI-first landing page and responsive design system
- Register, login, logout
- JWT access token + refresh token flow
- Auth-protected routing
- Admin route protection
- Service catalog list + detail
- Admin service create, edit, status toggle
- User purchases flow (create/list)
- User dashboard with date filters
- Admin purchases and dashboard metrics
- Global API error handling

## Backend Base URL
Create `frontend/.env`:

```env
VITE_API_BASE_URL=/api
```

With this value, Vite proxy forwards requests to `http://localhost:8080`.

For deployment, set:

```env
VITE_API_BASE_URL=https://your-backend-host
```

## Run
```bash
cd frontend
npm install
npm run dev
```

## Build
```bash
cd frontend
npm run build
npm run preview
```

## Notes
- The backend JWT includes `role` claim and frontend reads it for admin route visibility.
- Admin list uses `/service/user/services` because no admin read-list endpoint exists yet in backend.
