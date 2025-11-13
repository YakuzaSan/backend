# Repository Overview

A full-stack application with a Spring Boot backend and React Router frontend.

## Architecture

### Backend
- **Framework**: Spring Boot 3.5.7
- **Language**: Java 21
- **Database**: PostgreSQL
- **Build System**: Gradle
- **Key Dependencies**:
  - Spring Security with OAuth2 Client support
  - Spring Web
  - Spring Development Tools

### Frontend
- **Framework**: React Router 7.9.2
- **Language**: TypeScript 5.9.2
- **UI Framework**: React 19.1.1
- **Styling**: Tailwind CSS 4.1.13
- **Build Tool**: Vite 7.1.7
- **Runtime**: Node.js

## Project Structure

```
.
├── backend/
│   ├── src/main/java/gr/backend/
│   │   ├── SecurityConfig.java
│   │   ├── LoginController.java
│   │   ├── GithubController.java
│   │   └── BackendApplication.java
│   ├── src/test/
│   ├── build.gradle
│   └── gradlew
├── frontend/
│   ├── src/
│   ├── public/
│   ├── package.json
│   ├── vite.config.ts
│   ├── react-router.config.ts
│   ├── Dockerfile
│   └── README.md
├── .env.example
├── build.gradle
├── settings.gradle
└── Configuration files (nixpacks.toml, railway.json, vercel.json)
```

## Backend Features

- **Authentication**: Spring Security with OAuth2 support
- **GitHub Integration**: GitHub OAuth controller
- **Login Management**: Custom login controller
- **Security Configuration**: Centralized security configuration

## Frontend Features

- Server-side rendering with React Router
- Hot Module Replacement (HMR) for development
- TypeScript support
- TailwindCSS for styling
- React Icons integration
- Production-ready build optimization

## Development Scripts

### Frontend
- `npm run dev` - Start development server (http://localhost:5173)
- `npm run build` - Create production build
- `npm run start` - Start production server
- `npm run typecheck` - Run TypeScript type checking

## Deployment

The project supports multiple deployment platforms:
- Docker containerization
- AWS ECS
- Google Cloud Run
- Azure Container Apps
- Digital Ocean
- Fly.io
- Railway
- Vercel (configuration available)

## Key Technologies

- **Backend**: Spring Boot, Spring Security, PostgreSQL
- **Frontend**: React, React Router, TypeScript, Tailwind CSS
- **Build & Deploy**: Gradle, Vite, Docker, Nixpacks
