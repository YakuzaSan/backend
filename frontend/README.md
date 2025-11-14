# Email/Password & GitHub OAuth Authentication System

Full-stack authentication system with Spring Boot backend and React Router frontend.

## Features

- ✅ Email/Password registration & login with BCrypt hashing
- ✅ GitHub OAuth2 integration
- ✅ Session-based authentication with Spring Security
- ✅ Protected dashboard routes
- ✅ Responsive UI with Tailwind CSS
- ✅ TypeScript for type safety

## Tech Stack

**Backend:**
- Spring Boot 3.5.7
- Spring Security with OAuth2
- Supabase (User storage)
- H2 Database (Sessions)

**Frontend:**
- React 19.1.1 with TypeScript
- React Router 7.9.2
- Tailwind CSS 4.1.13
- Vite 7.1.7

## Setup & Development

### Prerequisites
- Node.js 20+
- Java 21+
- Gradle

### Installation

```bash
# Backend
cd backend
./gradlew build

# Frontend
cd frontend
npm install
```

### Environment Variables

**Backend:** `src/main/resources/application.properties`
```properties
supabase.url=<YOUR_SUPABASE_URL>
supabase.api-key=<YOUR_SUPABASE_API_KEY>
spring.security.oauth2.client.registration.github.client-id=<YOUR_GITHUB_CLIENT_ID>
spring.security.oauth2.client.registration.github.client-secret=<YOUR_GITHUB_SECRET>
```

**Frontend:** `frontend/.env`
```env
VITE_API_URL=http://localhost:8080
```

### Development

```bash
# Terminal 1: Backend (port 8080)
cd backend
./gradlew bootRun

# Terminal 2: Frontend (port 5173)
cd frontend
npm run dev
```

Visit `http://localhost:5173`

## API Endpoints

- `POST /api/register` - Register with email/password
- `POST /api/login` - Login with email/password
- `GET /api/user` - Get current user info (authenticated)
- `POST /api/logout` - Logout
- `GET /oauth2/authorization/github` - GitHub OAuth redirect

## Project Structure

```
frontend/
├── app/
│   ├── dashboard.tsx      # Protected dashboard
│   ├── login.tsx          # Login page
│   ├── register.tsx       # Registration page
│   ├── utils/
│   │   ├── api.ts         # API client
│   │   └── csrf.ts        # CSRF token utilities
│   └── routes.ts
├── package.json
└── vite.config.ts
```

## Security Features

- **CSRF Protection**: Cookie-based CSRF tokens
- **Password Hashing**: BCrypt with Spring Security
- **Session Management**: JDBC-based sessions
- **CORS**: Restricted to localhost:5173
- **OAuth2**: GitHub identity provider

## Build for Production

```bash
# Backend
./gradlew build -x test

# Frontend
npm run build
npm start
```

## Deployment

Supports Docker, AWS, Google Cloud, Azure, Digital Ocean, Railway, Fly.io, and more.

See deployment configs in root directory.
