# HiSync Backend 🎛️

Backend REST API cho **HiSync** — app quản lý lịch tập, repertoire bài hát và task luyện tập cho band nhạc / club âm nhạc.

> Đây là repo **backend (Spring Boot + PostgreSQL)**. Android client nằm ở repo riêng: [`hisync`](https://github.com/mystiquekem/hisync).

---

## 🛠️ Tech stack

| Layer | Công nghệ |
|---|---|
| Framework | Spring Boot |
| ORM | Spring Data JPA + Hibernate |
| Database | PostgreSQL (native `ENUM` cho role/status) |
| Auth | BCrypt password hashing, OTP qua email cho reset password |
| Mail | JavaMailSender (Gmail SMTP) |
| Media bài tập | Cloudinary (upload recording) |
| Repertoire | YouTube Data API v3 |
| Codegen | Lombok |

## 📂 Cấu trúc project

```
src/main/java/com/example/hisync/
├── config/      # SecurityConfig, CORS,...
├── controller/  # REST endpoints (Auth, Band, Session, Song, Task, User, Admin)
├── dto/         # Request/response payload
├── model/       # JPA Entity: User, Band, BandMember, Session, SessionMember, Song, Task
├── repository/  # Spring Data JPA repository interfaces
└── service/     # Business logic: AuthService, BandService, SessionService, SongService, OtpStore
```

## 🧩 Schema tổng quan

7 bảng chính: `users`, `bands`, `band_members` (composite PK), `sessions`, `session_members` (composite PK), `songs`, `tasks`.

- `users.email` và `bands.invite_code` là `UNIQUE`.
- `band_members` / `session_members` dùng composite primary key (`band_id + user_id`, `session_id + user_id`) để DB tự chặn join trùng.
- `role` (`member`/`leader`/`admin`) và `task.status` (`pending`/`done`/`rerecord`) là PostgreSQL native `ENUM`.

## 🚀 Setup & chạy thử

### 1. Yêu cầu
- JDK 17+
- PostgreSQL đang chạy local (hoặc remote)
- Tài khoản Cloudinary, YouTube Data API key, và một Gmail app password (cho gửi OTP)

### 2. Tạo database
```sql
CREATE DATABASE hisync;
```

### 3. Cấu hình `application.properties`

> ⚠️ **Không commit file này với giá trị thật lên git.** Repo hiện đang để credentials thật trong `application.properties` — xem mục [Bảo mật](#️-bảo-mật-đọc-trước-khi-public) bên dưới.

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/hisync
spring.datasource.username=postgres
spring.datasource.password=${DB_PASSWORD}

spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true

# YouTube
youtube.api.key=${YOUTUBE_API_KEY}

# Cloudinary
cloudinary.cloud-name=${CLOUDINARY_CLOUD_NAME}
cloudinary.upload-preset=${CLOUDINARY_UPLOAD_PRESET}

# Mail OTP
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_APP_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

Set các biến môi trường tương ứng (`DB_PASSWORD`, `YOUTUBE_API_KEY`, `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_UPLOAD_PRESET`, `MAIL_USERNAME`, `MAIL_APP_PASSWORD`) trước khi chạy, hoặc dùng file `application-local.properties` đã gitignore.

### 4. Build & run
```bash
./gradlew bootRun
```
API mặc định chạy ở `http://localhost:8080`.

### 5. Test API
Dùng Postman để gọi thử các endpoint (`/api/auth/*`, `/api/bands/*`, `/api/sessions/*`, `/api/songs/*`, `/api/tasks/*`, `/api/admin/*`).

## ⚠️ Bảo mật (đọc trước khi public!)

- **Rotate ngay** DB password, YouTube API key, và Gmail app password hiện có trong `application.properties` — nếu repo từng public với giá trị thật thì coi như đã leak, đổi key mới.
- Chuyển hết secret ra biến môi trường hoặc một file `application-local.properties` đã add vào `.gitignore`, đừng để giá trị thật nằm trong file commit.
- Security filter chain hiện đang `permitAll()` cho tất cả request — đang trong kế hoạch chuyển sang xác thực token-based (JWT) để mỗi endpoint tự verify identity/role thay vì tin client gửi user ID.

## 📄 License

TODO — thêm license phù hợp (MIT/Apache-2.0/...) trước khi public chính thức.

## 🙋 Liên hệ

Tác giả: [tên bạn] — đồ án tốt nghiệp, [tên trường], 2026.
