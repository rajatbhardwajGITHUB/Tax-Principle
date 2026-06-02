# Railway Backend Setup

This project can deploy the Spring Boot backend on Railway while the frontend stays on Firebase Hosting.

## What Railway Needs

Railway will run the backend as a normal Java service. The app is already prepared for that because it:
- listens on `PORT`
- binds to `0.0.0.0`
- exposes a `/health` endpoint
- reads production settings from environment variables
- allows CORS from the Firebase Hosting domains

## Railway Service Setup

1. Create a new Railway project.
2. Connect this GitHub repository.
3. Choose the root of the repo as the service source.
4. Let Railway auto-detect the Java build.
5. Leave the start command empty unless Railway asks for one.
6. Deploy from the `main` branch.
7. Enable automatic deploys if you want Railway to redeploy on every merge.

## Environment Variables

Set these in the Railway service variables panel:

```env
SPRING_DATASOURCE_URL=jdbc:mariadb://<db-host>:3306/<db-name>
SPRING_DATASOURCE_USERNAME=<db-username>
SPRING_DATASOURCE_PASSWORD=<db-password>
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.MariaDBDialect

APP_JWT_SECRET=<strong-secret>
APP_JWT_ACCESS_EXPIRATION_MS=900000
APP_JWT_REFRESH_EXPIRATION_MS=604800000

APP_OTP_EXPIRATION_MINUTES=10
APP_OTP_RETURN_IN_RESPONSE=true

APP_MAIL_ENABLED=true
APP_MAIL_FROM=<from-email>
SPRING_MAIL_HOST=<smtp-host>
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=<smtp-username>
SPRING_MAIL_PASSWORD=<smtp-password>
SPRING_MAIL_SMTP_AUTH=true
SPRING_MAIL_SMTP_STARTTLS_ENABLE=true
SPRING_MAIL_DEBUG=false

RAZORPAY_KEY_ID=<razorpay-key-id>
RAZORPAY_KEY_SECRET=<razorpay-key-secret>
RAZORPAY_CURRENCY=INR

APP_CORS_ALLOWED_ORIGINS=https://firm-no-01-tax.web.app,https://firm-no-01-tax.firebaseapp.com,http://localhost:5173,http://127.0.0.1:5173
```

## Notes

- Do not set `PORT` manually. Railway injects it at runtime.
- The app already defaults to `0.0.0.0:8080`, so Railway can override the port safely.
- If the backend talks to a database outside Railway, make sure the host allows Railway traffic.
- Keep secrets out of GitHub repository files. Put them in Railway variables instead.

## Health Check

After deploy, verify the service with:

```bash
curl https://<your-railway-domain>/health
```

Expected response:

```json
{"status":"UP"}
```

## Frontend Follow-Up

After Railway gives you the public backend URL, update the frontend production env to point at it:

```env
VITE_API_BASE_URL=https://<your-railway-domain>
```

Then rebuild and redeploy the Firebase frontend.
