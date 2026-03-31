# Coursify – E-Learning Platform API
> Group 7 Final Project | Spring Boot 3 · Java 17 · PostgreSQL · JWT

---

## Tech Stack
| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.2 |
| Language | Java 17 |
| Security | Spring Security + JWT (jjwt 0.11) |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| Validation | Jakarta Bean Validation |
| Docs | Springdoc OpenAPI / Swagger UI |
| Build | Maven |

---

## Project Structure
```
src/main/java/com/coursify/
├── CoursifyApplication.java
├── config/
│   ├── ApplicationConfig.java    # Beans: UserDetailsService, PasswordEncoder, AuthManager
│   └── SecurityConfig.java       # JWT filter chain, CORS, role guards
├── controller/                   # REST endpoints (one per domain)
├── domain/                       # JPA entities matching the ERD
├── dto/
│   ├── request/                  # Validated inbound records
│   └── response/                 # Outbound records
├── exception/                    # Custom exceptions + global handler
├── repository/                   # Spring Data JPA interfaces
├── security/
│   ├── JwtService.java           # Token generation & validation
│   └── JwtAuthenticationFilter.java
└── service/
    ├── *.java                    # Service interfaces
    └── impl/                     # Service implementations
```

---

## Getting Started

### 1. Create the database
```sql
CREATE DATABASE coursify_db;
```

### 2. Configure `application.properties`
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/coursify_db
spring.datasource.username=postgres
spring.datasource.password=your_password

# Generate a secret: openssl rand -base64 32
jwt.secret=your-base64-secret
jwt.expiration=86400000
```

### 3. Run
```bash
mvn spring-boot:run
```

### 4. Open Swagger UI
```
http://localhost:8080/swagger-ui.html
```

---

## API Endpoints Summary

### Auth
| Method | URL | Access |
|---|---|---|
| POST | `/api/auth/register` | Public |
| POST | `/api/auth/login` | Public |

### Courses
| Method | URL | Access |
|---|---|---|
| GET | `/api/courses` | Public (search + filter) |
| GET | `/api/courses/{id}` | Public |
| POST | `/api/courses` | TEACHER, ADMIN |
| PUT | `/api/courses/{id}` | TEACHER (own), ADMIN |
| PATCH | `/api/courses/{id}/publish` | TEACHER (own), ADMIN |
| DELETE | `/api/courses/{id}` | TEACHER (own), ADMIN |
| GET | `/api/courses/my-courses` | TEACHER |

### Lessons
| Method | URL | Access |
|---|---|---|
| GET | `/api/lessons/course/{courseId}` | Authenticated |
| GET | `/api/lessons/{id}` | Authenticated |
| POST | `/api/lessons` | TEACHER, ADMIN |
| PUT | `/api/lessons/{id}` | TEACHER (own), ADMIN |
| DELETE | `/api/lessons/{id}` | TEACHER (own), ADMIN |

### Enrollments
| Method | URL | Access |
|---|---|---|
| POST | `/api/enrollments` | STUDENT |
| GET | `/api/enrollments/my-enrollments` | STUDENT |
| GET | `/api/enrollments/course/{courseId}` | TEACHER, ADMIN |
| GET | `/api/enrollments/check?courseId=` | Authenticated |

### Progress
| Method | URL | Access |
|---|---|---|
| PUT | `/api/progress/course/{courseId}` | STUDENT |
| GET | `/api/progress/course/{courseId}` | STUDENT |
| GET | `/api/progress/my-progress` | STUDENT |

### Categories
| Method | URL | Access |
|---|---|---|
| GET | `/api/categories` | Public |
| GET | `/api/categories/{id}` | Public |
| POST | `/api/categories` | ADMIN |
| PUT | `/api/categories/{id}` | ADMIN |
| DELETE | `/api/categories/{id}` | ADMIN |

### Users
| Method | URL | Access |
|---|---|---|
| GET | `/api/users/me` | Authenticated |
| GET | `/api/users` | ADMIN |
| GET | `/api/users/{id}` | ADMIN |
| DELETE | `/api/users/{id}` | ADMIN |

### Admin Dashboard
| Method | URL | Access |
|---|---|---|
| GET | `/api/admin/stats` | ADMIN |
| DELETE | `/api/admin/users/{id}` | ADMIN |
| DELETE | `/api/admin/courses/{id}` | ADMIN |

---

## Authentication
All protected endpoints require a Bearer token in the `Authorization` header:
```
Authorization: Bearer <your-jwt-token>
```
Obtain the token from `/api/auth/login` or `/api/auth/register`.

---

## Roles
| Role | Can Do |
|---|---|
| `ADMIN` | Everything |
| `TEACHER` | Create/manage own courses and lessons |
| `STUDENT` | Browse, enroll, track progress |
