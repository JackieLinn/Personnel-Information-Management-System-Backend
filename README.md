# Personnel Information Management System - Backend

A RESTful backend service for managing personnel, teams, and internal communications within an organization.

## 🚀 Tech Stack

| Category | Technology |
|----------|------------|
| Framework | Spring Boot 3.5.8 |
| Language | Java 17 |
| ORM | MyBatis-Plus 3.5.7 |
| Database | MySQL |
| Cache | Redis |
| Search Engine | Elasticsearch |
| File Storage | Aliyun OSS |
| Authentication | JWT (jjwt 0.11.5) |
| API Documentation | Knife4j (OpenAPI 3) |
| Real-time | WebSocket |
| Utilities | Hutool, Lombok |

## 📁 Project Structure

```
src/main/java/ynu/edu/pims/
├── common/             # Common utilities (UserContext)
├── config/             # Configuration classes
│   ├── CorsConfig      # CORS settings
│   ├── Knife4jConfig   # API documentation
│   ├── OssConfig       # Aliyun OSS settings
│   ├── RedisConfig     # Redis settings
│   └── WebConfig       # Web MVC settings
├── controller/         # REST API endpoints
│   ├── AuthController  # Authentication APIs
│   ├── FileController  # File upload APIs
│   ├── TeamController  # Team management APIs
│   ├── TweetController # Tweet/Post APIs
│   └── UserController  # User management APIs
├── ES/                 # Elasticsearch services
├── interceptor/        # JWT authentication interceptor
├── mapper/             # MyBatis-Plus mappers
├── OSS/                # Aliyun OSS service
├── pojo/               # Data models
│   ├── DTO/            # Data Transfer Objects
│   ├── document/       # Elasticsearch documents
│   └── entity/         # Database entities
├── redis/              # Redis service
├── service/            # Business logic layer
│   └── impl/           # Service implementations
└── utils/              # Utility classes (JWT, Email)
```

## ✨ Features

### 👤 User Management
- User registration with email verification
- Login authentication (standard & YNU OAuth2)
- Password change functionality
- User profile updates
- User search with multiple criteria

### 👥 Team Management
- Create and manage teams/organizations
- Team member management with roles
- Join request workflow (apply → approve/reject)
- Member status tracking:
  - `0` - Pending approval
  - `1` - Active member
  - `2` - Rejected
  - `3` - Removed/Expelled

### 📝 Tweet/Post System
- Create and delete tweets within teams
- Image attachment support via OSS
- Chronological tweet listing
- Permission-based deletion (owner/admin only)

### 🔍 Search
- Full-text search powered by Elasticsearch
- Search users by team, name, or position
- Combination search queries

### 📁 File Upload
- Aliyun OSS integration for image storage
- Support for tweet image attachments

## 🛠️ Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- Elasticsearch 8.x
- Aliyun OSS account

### Configuration

1. Clone the repository:
```bash
git clone https://github.com/your-repo/Personnel_Information_Management_System_Backend.git
cd Personnel_Information_Management_System_Backend
```

2. Configure `application.yaml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_database
    username: your_username
    password: your_password

  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password

  elasticsearch:
    uris: http://localhost:9200

aliyun:
  oss:
    endpoint: oss-cn-your-region.aliyuncs.com
    accessKeyId: your_access_key
    accessKeySecret: your_secret_key
    bucketName: your_bucket

jwt:
  secret: your_jwt_secret_key
  token-expiration: 604800000  # 7 days
```

3. Build and run:
```bash
mvn clean package
java -jar target/pims-0.0.1-SNAPSHOT.jar
```

### API Documentation

Once the application is running, access the API documentation at:
- Knife4j UI: `http://localhost:19090/doc.html`

## 📚 API Overview

| Module | Endpoint | Description |
|--------|----------|-------------|
| Auth | `POST /api/auth/register` | User registration |
| Auth | `POST /api/auth/login` | User login |
| User | `GET /api/user/info` | Get user info |
| User | `PUT /api/user/update` | Update user profile |
| Team | `POST /api/team/create` | Create team |
| Team | `GET /api/team/members` | List team members |
| Team | `POST /api/team/apply` | Apply to join team |
| Tweet | `POST /api/tweet/send` | Create tweet |
| Tweet | `DELETE /api/tweet/{id}` | Delete tweet |
| File | `POST /api/file/upload` | Upload file to OSS |

## 🔐 Authentication

The API uses JWT (JSON Web Token) for authentication. Include the token in the `Authorization` header:

```
Authorization: Bearer <your_jwt_token>
```

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

*Backend project for YNU Programming Skills Enhancement Course*
