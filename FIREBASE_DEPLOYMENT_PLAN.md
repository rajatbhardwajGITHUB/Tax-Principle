# Firebase Deployment Plan

This document is the working roadmap for hosting this project with Firebase and GitHub so production deploys are repeatable, trackable, and easy to maintain.

## Status Legend
- `[ ]` Not started
- `[-]` In progress
- `[x]` Done
- `[!]` Blocked

## Project Summary
This repository has two parts:
- `frontend/` is a React + Vite single-page app
- the root backend is a Spring Boot API with JWT, JPA, and MariaDB

Because of that split, the best Firebase setup is:
- Firebase Hosting for the React frontend
- Cloud Run for the Spring Boot backend
- GitHub Actions for continuous integration and deployment

## Why This Setup
We are using this structure because each tool matches the job it does best.

- Firebase Hosting is a very good fit for a Vite frontend because it serves static assets quickly over a CDN.
- Cloud Run is a better fit for Spring Boot than Firebase App Hosting because the backend is a Java service with database access, security, and email support.
- GitHub Actions gives us a repeatable pipeline so code can be tested before it is deployed.
- Keeping frontend and backend deployment separate reduces risk. A frontend-only change should not force a backend redeploy, and vice versa.
- Using a tracked plan file prevents the setup from becoming tribal knowledge. Anyone can open this file and see the next step.

## Recommended Architecture

### Production Flow
1. Developer pushes code to a feature branch.
2. A pull request is opened against `main`.
3. GitHub Actions runs lint, tests, and build checks.
4. Firebase Hosting preview is created for the frontend if needed.
5. After review, the PR is merged into `main`.
6. GitHub Actions deploys the frontend to Firebase Hosting.
7. GitHub Actions or Cloud Run deploys the backend if backend files changed.
8. The live app points to the production backend URL.

### Why This Flow
- PR checks catch broken code before production.
- Preview deployments let us verify UI changes safely.
- `main` stays the single source of truth for production.
- Auto deploy keeps releases simple and reduces manual mistakes.

## Phase 0: Audit The Current Project

### Goal
Confirm the current codebase, build commands, and runtime dependencies before connecting deployment.

### Tasks
- [ ] Confirm the frontend build works locally with `npm run build`
- [ ] Confirm the backend build works locally with `./mvnw test` or `./mvnw package`
- [ ] Verify the frontend uses a configurable API base URL
- [ ] Verify the backend can read production settings from environment variables
- [ ] Identify all secrets currently stored in local config files

### Why This Step Exists
- If the app does not build locally, CI/CD will fail later.
- Deployment only works smoothly when environment variables are separated from source code.
- Auditing secrets now avoids accidentally pushing passwords or API keys to GitHub.

### Exit Criteria
- Both frontend and backend build successfully on the local machine.
- No production secrets are hardcoded in source-controlled files.

## Phase 1: Prepare GitHub As The Source Of Truth

### Goal
Make GitHub the canonical place where code changes are reviewed and merged.

### Tasks
- [ ] Create or confirm the GitHub repository for this project
- [ ] Push the current local code to GitHub
- [ ] Set `main` as the protected production branch
- [ ] Create a branch strategy for feature work
- [ ] Add branch protection rules for `main`
- [ ] Require pull requests before merging
- [ ] Require status checks before merging

### Why This Step Exists
- GitHub is the control center for the deployment pipeline.
- Branch protection prevents accidental direct pushes to production.
- Pull requests create a reviewable history of changes.
- Status checks make sure broken code cannot be merged by mistake.

### Recommended Branch Strategy
- `main` for production
- `feature/*` for new work
- `fix/*` for bug fixes
- `chore/*` for pipeline or maintenance tasks

### Exit Criteria
- The repository is on GitHub.
- `main` is protected.
- All future work happens through pull requests.

## Phase 2: Create The Firebase Project

### Goal
Set up Firebase as the hosting layer for the frontend and as the entry point for deployment tooling.

### Tasks
- [ ] Create a Firebase project
- [ ] Link the Firebase project to the correct Google account
- [ ] Enable Firebase Hosting
- [ ] Decide whether a single Hosting site is enough or whether multiple environments are needed
- [ ] Record the Firebase project ID in this plan
- [ ] Install the Firebase CLI locally

### Why This Step Exists
- Firebase Hosting cannot deploy until the project exists.
- The Firebase CLI is needed to initialize hosting config and generate the GitHub workflow.
- Having the project ID written down helps when configuring CI and secrets.

### Recommended Decision
Start with one Firebase project and one production Hosting site.

### Why Start Simple
- One environment is easier to deploy and debug.
- You can add staging later once the production path is stable.

### Exit Criteria
- Firebase project exists.
- Hosting is enabled.
- Firebase CLI is installed and authenticated.

## Phase 3: Set Up Firebase Hosting For The Frontend

### Goal
Deploy the React frontend from `frontend/` to Firebase Hosting.

### Tasks
- [ ] Run Firebase initialization for Hosting
- [ ] Point Hosting to the frontend build output directory
- [ ] Configure SPA fallback to `index.html`
- [ ] Confirm the frontend build command is correct
- [ ] Confirm the deploy target points to the right project
- [ ] Test a manual deploy from local machine

### Suggested Configuration
- Frontend build command: `npm run build`
- Frontend output directory: `frontend/dist`
- SPA rewrite: all routes should serve `index.html`

### Why This Step Exists
- Vite generates static files that Firebase Hosting can serve efficiently.
- SPA fallback is necessary because React Router handles routes on the client side.
- A manual deploy proves the config works before automating anything.

### Important Note
If this app uses client-side routing, direct visits to `/login`, `/admin/services`, or similar paths must still load the app. The rewrite rule is what makes that work.

### Exit Criteria
- Firebase Hosting serves the frontend.
- Refreshing a nested route does not produce a 404.
- The deployed frontend loads correctly from Firebase.

## Phase 4: Keep The Spring Backend On Cloud Run

### Goal
Deploy the Spring Boot backend to Cloud Run without requiring Docker on the local machine.

### Tasks
- [ ] Create a production build for the backend
- [ ] Deploy the backend from source to Cloud Run using Google Cloud buildpacks
- [ ] Configure Cloud Run environment variables
- [ ] Connect the backend to the production MariaDB database
- [ ] Verify the backend health endpoint or startup logs

### Why This Step Exists
- Spring Boot is a server application, not a static site.
- Cloud Run can build and run the backend from source, so Docker does not need to be installed locally.
- Cloud Run still gives us a managed runtime that works well with Java.
- This keeps Firebase Hosting focused on frontend delivery.
- Using Cloud Run keeps the backend reproducible across environments without adding a local Docker dependency.

### Backend Configuration Notes
- Database connection values should come from environment variables.
- JWT secrets should come from environment variables.
- Mail credentials should come from environment variables.
- Any origin restrictions or CORS rules must include the Firebase domain.

### Exit Criteria
- The backend has a stable public HTTPS URL.
- The backend starts cleanly in production.
- The database and auth features work against the deployed backend.

## Phase 5: Connect Frontend To The Production Backend

### Goal
Make the frontend talk to the deployed backend instead of localhost.

### Tasks
- [ ] Set production API base URL in frontend environment settings
- [ ] Keep local API base URL for development
- [ ] Verify axios or fetch uses the environment value consistently
- [ ] Update CORS settings on the backend
- [ ] Test login, service listing, admin actions, and purchases in production mode

### Why This Step Exists
- Localhost URLs only work on a developer machine.
- Environment-based API URLs let the same code run locally and in production.
- CORS must allow browser requests from the hosted frontend domain.

### Recommended Pattern
- Local: `http://localhost:8080` or a Vite proxy
- Production: the Cloud Run backend URL

### Exit Criteria
- Frontend production build uses the deployed backend URL.
- Browser requests succeed without CORS errors.

## Phase 6: Add GitHub Actions CI

### Goal
Make every pull request prove that the app still builds and passes checks.

### Tasks
- [ ] Add a workflow for frontend lint and build
- [ ] Add a workflow for backend build and tests
- [ ] Add caching for Node and Maven dependencies
- [ ] Fail the workflow if tests or builds fail
- [ ] Make the required checks mandatory for `main`

### Why This Step Exists
- CI catches problems before a deploy happens.
- Automatic builds are faster and more reliable than manual checks.
- Required checks protect production quality.

### Suggested CI Checks
- Frontend:
  - `npm install`
  - `npm run lint`
  - `npm run build`
- Backend:
  - `./mvnw test`
  - `./mvnw package` if needed

### Exit Criteria
- Pull requests show pass/fail status automatically.
- `main` cannot be merged unless checks pass.

## Phase 7: Add Automated Deployment

### Goal
Make deploys happen automatically after code is merged.

### Tasks
- [ ] Add Firebase Hosting deployment workflow for the frontend
- [ ] Add Cloud Run deployment workflow for the backend
- [ ] Store deploy credentials in GitHub Secrets
- [ ] Restrict production deploys to `main`
- [ ] Optionally add preview deploys for pull requests

### Why This Step Exists
- Manual deployment is slow and error-prone.
- Automating deploys gives consistent releases.
- PR previews reduce the chance of shipping a broken UI.

### Recommended Deploy Policy
- Pull requests: test only, optionally preview frontend
- `main`: deploy to production

### Exit Criteria
- A merge to `main` triggers a real production deploy.
- The deployment happens without manual copying or server login.

## Phase 8: Use Firebase Preview Channels

### Goal
Review frontend changes before they reach production.

### Tasks
- [ ] Enable Firebase Hosting preview deploys for pull requests
- [ ] Verify preview URL comments appear on PRs
- [ ] Test route handling and API calls in the preview environment

### Why This Step Exists
- Preview URLs make it easier to validate UI and routing changes.
- Reviewers can test the exact code from the PR.
- It reduces the chance of deploying a broken frontend.

### Important Note
Preview channels still talk to real backend resources unless you intentionally point them elsewhere.

### Exit Criteria
- Every PR can generate a preview version of the frontend.

## Phase 9: Add Custom Domain And Production Branding

### Goal
Put the app on a real domain once the deployment pipeline is stable.

### Tasks
- [ ] Buy or choose a domain
- [ ] Configure the Firebase Hosting custom domain
- [ ] Configure the backend domain or subdomain if needed
- [ ] Update frontend environment variables to use the production API domain
- [ ] Confirm SSL is active on both frontend and backend

### Why This Step Exists
- A custom domain is better for users and production credibility.
- It gives stable URLs that do not change when infrastructure changes.
- SSL keeps browser traffic secure.

### Suggested Domain Pattern
- Frontend: `app.yourdomain.com`
- Backend: `api.yourdomain.com`

### Exit Criteria
- The app is reachable from the custom domain.
- SSL works correctly on all production endpoints.

## Phase 10: Monitoring, Logging, And Rollback

### Goal
Make production safer after deployment.

### Tasks
- [ ] Confirm Firebase Hosting deploy history
- [ ] Confirm Cloud Run logs are visible
- [ ] Set up alerting for backend failures if needed
- [ ] Define a rollback process for frontend and backend
- [ ] Document who owns production fixes

### Why This Step Exists
- Deploying is only half the job.
- If production breaks, we need a fast way to see what happened.
- Rollback capability shortens downtime and reduces stress.

### Recommended Rollback Strategy
- Frontend: redeploy a previous known-good Hosting version
- Backend: redeploy a previous Cloud Run revision

### Exit Criteria
- Logs are accessible.
- Rollback steps are known before an incident happens.

## Phase 11: Security And Secrets

### Goal
Keep sensitive values out of source control and production safe.

### Tasks
- [ ] Move database credentials to environment variables
- [ ] Move JWT secrets to environment variables
- [ ] Move mail credentials to environment variables
- [ ] Audit `.env` files
- [ ] Make sure no secrets are committed to GitHub
- [ ] Add `.gitignore` entries if needed

### Why This Step Exists
- Secrets in Git history are hard to remove once leaked.
- Environment variables are the standard way to inject credentials at runtime.
- Safer secret handling is required before production deployment.

### Exit Criteria
- No secret values are stored in tracked source files.

## Phase 12: Final Validation Before Go-Live

### Goal
Prove the complete system works end to end before relying on it.

### Tasks
- [ ] Register a new user
- [ ] Log in and verify token flow
- [ ] Load the service catalog
- [ ] Test admin service actions
- [ ] Test purchase flow
- [ ] Test dashboard pages
- [ ] Confirm logout and session cleanup
- [ ] Verify page refresh on nested routes
- [ ] Test the app on mobile and desktop

### Why This Step Exists
- Deployment success does not always mean application success.
- End-to-end validation catches CORS, auth, and routing issues.
- Testing the real production URLs confirms the full stack is healthy.

### Exit Criteria
- Core user and admin flows work in production.
- No critical UI or API errors remain.

## Suggested Implementation Order

1. Audit local build and environment setup
2. Push the repo to GitHub
3. Create the Firebase project
4. Deploy the frontend to Firebase Hosting
5. Deploy the backend to Cloud Run
6. Connect frontend to backend
7. Add GitHub Actions CI
8. Add automatic deploys
9. Enable previews
10. Add custom domain
11. Add monitoring and rollback

### Why This Order
- It starts with the simplest working path.
- It avoids automation before the basic deployment path is proven.
- It reduces the chance of debugging multiple new systems at the same time.

## Tracking Checklist

### Repository
- [ ] Repo pushed to GitHub
- [ ] `main` branch protected
- [ ] PR review required

### Firebase
- [ ] Firebase project created
- [ ] Hosting enabled
- [ ] Frontend deployed
- [ ] Preview deploys enabled

### Backend
- [ ] Backend deployed to Cloud Run
- [ ] Cloud Run service created
- [ ] Production backend URL verified
- [ ] Database connected

### CI/CD
- [ ] Frontend build workflow added
- [ ] Backend build workflow added
- [ ] Production deploy workflow added
- [ ] Secrets stored in GitHub

### Production
- [ ] Custom domain connected
- [ ] SSL active
- [ ] Logs visible
- [ ] Rollback tested

## Notes To Keep Updated

Use this section while implementing:
- Firebase project ID:
- Frontend Hosting site name:
- Cloud Run service name:
- Production backend URL:
- Production frontend URL:
- Database host:
- Secrets stored in:
- Custom domain:
- Deployment owner:

## Definition Of Done
This plan is complete when:
- the code is in GitHub
- the frontend is hosted on Firebase Hosting
- the backend is deployed on Cloud Run from source
- deploys happen through GitHub Actions
- production uses environment variables instead of hardcoded secrets
- the app can be updated safely without manual server work
