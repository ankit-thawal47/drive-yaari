# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Architecture

Drive-Lah is a Spring Boot application with the following structure:

- **Backend**: Spring Boot 3.2.0 application using Java 17
  - Main application class: `com.drivelah.DriveLahApplication`
  - REST controllers in `com.api` package
  - Uses H2 in-memory database for development
  - Server runs on port 8080 with context path `/api`

- **Frontend**: Directory exists but appears empty (no package.json found)

## Development Commands

### Backend (Spring Boot)

```bash
# Navigate to backend directory
cd backend

# Build the application
./gradlew build

# Run the application
./gradlew bootRun

# Run tests
./gradlew test

# Clean build
./gradlew clean build
```

### Testing the API

The application exposes endpoints at `http://localhost:8080/api/`:
- Test endpoint: `GET /api/test` (returns "10")

H2 console is available at `http://localhost:8080/api/h2-console` with:
- JDBC URL: `jdbc:h2:mem:drivelah`
- Username: `sa`
- Password: (empty)

## Key Configuration

- **Database**: H2 in-memory database with JPA/Hibernate
- **Context Path**: All endpoints are prefixed with `/api`
- **Port**: Application runs on port 8080
- **Build Tool**: Gradle with wrapper scripts

## Note on API Controller

The current ApiController in `com.api` package has a mapping issue - the `@RestController` value parameter should use `@RequestMapping` instead for proper path mapping.