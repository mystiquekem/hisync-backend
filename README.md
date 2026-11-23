# hisync — Spring Boot Backend

## Overview
hisync backend is a RESTful API server for the hisync band practice management app. It handles authentication, band management, lineups, sessions, tasks, and user profiles.

---

## Tech Stack
- **Language:** Java 21
- **Framework:** Spring Boot 3.x
- **Build tool:** Gradle
- **Database:** PostgreSQL
- **ORM:** Spring Data JPA + Hibernate
- **Security:** Spring Security (BCrypt password hashing, all routes permitted for now)
- **Email:** Spring Mail (Gmail SMTP) for OTP delivery
- **Schema management:** `ddl-auto=validate` — schema managed manually in pgAdmin

---

## Prerequisites
Before running the backend, make sure you have the following installed:

| Tool | Version | Download |
|------|---------|----------|
| Java JDK | 21 | https://aws.amazon.com/corretto/ |
| PostgreSQL | 15+ | https://www.postgresql.org/download/ |
| pgAdmin | 4 | https://www.pgadmin.org/download/ |
| Gradle | bundled via `gradlew` | — |

---

## Installation & Setup

### Step 1 — Clone or open the project
Open the backend project folder in IntelliJ IDEA or VS Code.

### Step 2 — Create the PostgreSQL database
1. Open pgAdmin and connect to your local PostgreSQL server
2. Right-click **Databases** → **Create** → **Database**
3. Name it `hisync` → Save

### Step 3 — Create enums and tables
Open the pgAdmin **Query Tool** for the `hisync` database and run the following SQL in order:

```sql
-- Enums
CREATE TYPE user_role AS ENUM ('member', 'leader', 'admin');
CREATE TYPE task_status AS ENUM ('pending', 'submitted', 'approved', 'rerecord');

-- Users
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR UNIQUE NOT NULL,
    password VARCHAR NOT NULL,
    display_name VARCHAR,
    role user_role DEFAULT 'member',
    created_at TIMESTAMP DEFAULT NOW()
);

-- User instruments
CREATE TABLE user_instruments (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    instrument VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, instrument)
);

-- Bands
CREATE TABLE bands (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    description VARCHAR,
    invite_code VARCHAR UNIQUE NOT NULL,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT NOW()
);

-- Band members
CREATE TABLE band_members (
    band_id BIGINT NOT NULL REFERENCES bands(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role user_role DEFAULT 'member',
    joined_at TIMESTAMP DEFAULT NOW(),
    PRIMARY KEY (band_id, user_id)
);

-- Lineups
CREATE TABLE lineups (
    id BIGSERIAL PRIMARY KEY,
    band_id BIGINT NOT NULL REFERENCES bands(id) ON DELETE CASCADE,
    song_title VARCHAR(255) NOT NULL,
    youtube_id VARCHAR(50),
    thumbnail_url VARCHAR(500),
    created_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Lineup members
CREATE TABLE lineup_members (
    lineup_id BIGINT NOT NULL REFERENCES lineups(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    instrument VARCHAR(50) NOT NULL,
    PRIMARY KEY (lineup_id, user_id)
);

-- Sessions
CREATE TABLE sessions (
    id BIGSERIAL PRIMARY KEY,
    band_id BIGINT REFERENCES bands(id),
    lineup_id BIGINT REFERENCES lineups(id) ON DELETE SET NULL,
    date TIMESTAMP,
    duration_minutes INT DEFAULT 60,
    created_by BIGINT REFERENCES users(id)
);

-- Tasks
CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT REFERENCES sessions(id),
    assigned_to BIGINT REFERENCES users(id),
    title VARCHAR,
    status task_status DEFAULT 'pending',
    recording_url VARCHAR
);
```

### Step 4 — Configure `application.properties`
Open `src/main/resources/application.properties` and update:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/hisync
spring.datasource.username=YOUR_POSTGRES_USERNAME
spring.datasource.password=YOUR_POSTGRES_PASSWORD

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.type.preferred_enum_jdbc_type=NAMED_ENUM

# Gmail SMTP for OTP emails
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=YOUR_GMAIL_ADDRESS
spring.mail.password=YOUR_GMAIL_APP_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

> **Note on Gmail App Password:** Go to your Google Account → Security → 2-Step Verification → App Passwords → generate one for "Mail". Use that 16-character password, not your normal Gmail password.

### Step 5 — Find your local IP address
The Android app connects to the backend over your local network (hotspot or WiFi). You need your machine's local IP:

- **Windows:** open Command Prompt → run `ipconfig` → look for `IPv4 Address`
- **macOS/Linux:** run `ifconfig` or `ip addr`

Example: `192.168.1.5` or `172.20.10.2` (hotspot)

You'll need this IP in the Android setup step.

### Step 6 — Run the backend
In the project root, run:

```bash
./gradlew bootRun       # macOS / Linux
gradlew.bat bootRun     # Windows
```

Or run `HisyncApplication.java` directly from IntelliJ IDEA by clicking the green ▶ button.

The server starts at: `http://localhost:8080`

You can verify it's running by visiting `http://localhost:8080/api/bands` in your browser — it should return `[]` or a JSON response.

---

## Project Structure

```
src/main/java/com/example/hisync/
│
├── config/
│   └── SecurityConfig.java
├── controller/
│   ├── AuthController.java
│   ├── UserController.java
│   ├── BandController.java
│   ├── LineupController.java
│   ├── SessionController.java
│   ├── TaskController.java
│   └── AdminController.java
├── dto/                            # Request/response data shapes
├── model/                          # JPA entities
├── repository/                     # Spring Data JPA interfaces
└── service/                        # Business logic
    ├── AuthService.java
    ├── BandService.java
    ├── LineupService.java
    ├── SessionService.java
    └── OtpStore.java               # In-memory OTP (15-min expiry)
```

---

## Database Schema Summary

### Enums
```
user_role:   member | leader | admin
task_status: pending | submitted | approved | rerecord
```

### Table relationships
```
users ──< user_instruments
users ──< band_members >── bands
bands ──< lineups ──< lineup_members >── users
bands ──< sessions >── lineups
sessions ──< tasks >── users
```

---

## API Endpoints

### Auth — `/api/auth`
| Method | Path | Description |
|--------|------|-------------|
| POST | `/register` | Register new user |
| POST | `/login` | Login |
| POST | `/forgot-password` | Send OTP to email |
| POST | `/reset-password` | Reset password with OTP |

### Users — `/api/users`
| Method | Path | Description |
|--------|------|-------------|
| PATCH | `/{id}` | Update displayName and/or instruments |
| GET | `/{id}/instruments` | Get user's instruments |

### Bands — `/api/bands`
| Method | Path | Description |
|--------|------|-------------|
| POST | `/` | Create band |
| POST | `/join` | Join by invite code |
| GET | `/` | Get bands for user (`?userId=`) |
| GET | `/{id}` | Get band with members |

### Lineups — `/api/lineups`
| Method | Path | Description |
|--------|------|-------------|
| POST | `/` | Create lineup |
| GET | `/` | Get lineups (`?bandId=`) |
| GET | `/{id}` | Get lineup detail |
| PATCH | `/{id}` | Update lineup |
| DELETE | `/{id}` | Delete lineup |

### Sessions — `/api/sessions`
| Method | Path | Description |
|--------|------|-------------|
| GET | `/` | Get sessions (`?userId=` or `?bandId=`) + `from` + `to` |
| GET | `/{id}` | Get session detail |
| POST | `/` | Create session |
| PATCH | `/{id}` | Update session |
| DELETE | `/{id}` | Delete session |

### Tasks — `/api/tasks`
| Method | Path | Description |
|--------|------|-------------|
| GET | `/` | Get my tasks (`?userId=`) |
| GET | `/band/{bandId}` | Get all band tasks grouped by session |
| POST | `/` | Create task |
| PATCH | `/{id}` | Update task |
| DELETE | `/{id}` | Delete task |
| PATCH | `/{id}/status` | Update status |
| PATCH | `/{id}/recording` | Submit recording URL |

---

## Task Status Flow
```
pending ──(member uploads)──► submitted
                                   │
                    ┌──────────────┴──────────────┐
                    ▼                              ▼
                 approved                       rerecord
             (task complete)            (member must redo)
```

---

## Pending Phases
| Phase | Feature |
|-------|---------|
| 4 | Recording upload + Cloudinary + WorkManager background sync |
| 5 | Submissions review screen |
| 6 | Band settings: edit, kick members, transfer ownership, delete band |
