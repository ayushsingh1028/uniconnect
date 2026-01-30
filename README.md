# UniConnect Backend - Spring Boot REST API

A production-ready backend for a University Social Media Platform built with Spring Boot, MySQL, JWT authentication, Google OAuth 2.0, and Cloudinary file storage.

## Features

✅ **Authentication & Authorization**
- Email/Password registration and login
- Google OAuth 2.0 integration
- JWT-based stateless authentication
- Role-based access control (STUDENT, ALUMNI, ADMIN)

✅ **Core Modules**
- **Posts & Feed**: University-specific feed with likes and comments
- **PYQ**: Upload and search previous year question papers (PDFs)
- **Alumni Connect**: Alumni profiles with career info and reviews
- **Marketplace**: Campus buy/sell items
- **Events & Clubs**: University events and student clubs
- **PG & Food**: PG listings and food court information

✅ **File Storage**
- Cloudinary integration for PDFs and images
- Secure file upload and management

## Tech Stack

- **Framework**: Spring Boot 3.2.1
- **Language**: Java 17
- **Database**: MySQL 8.x
- **ORM**: Spring Data JPA + Hibernate
- **Security**: Spring Security 6 + JWT
- **OAuth**: Google OAuth 2.0
- **File Storage**: Cloudinary
- **Build Tool**: Maven

## Prerequisites

- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Google Cloud Console account (for OAuth)
- Cloudinary account

## Setup Instructions

### 1. Clone and Navigate
```bash
cd c:/uniconnect/backend
```

### 2. Configure MySQL Database
Create a database in MySQL:
```sql
CREATE DATABASE uniconnect;
```

### 3. Configure Application Properties
Edit `src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/uniconnect?createDatabaseIfNotExist=true
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD

# JWT Secret (Generate a secure random key)
jwt.secret=YOUR_JWT_SECRET_KEY_MIN_256_BITS

# Google OAuth
spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET

# Cloudinary
cloudinary.cloud-name=YOUR_CLOUDINARY_CLOUD_NAME
cloudinary.api-key=YOUR_CLOUDINARY_API_KEY
cloudinary.api-secret=YOUR_CLOUDINARY_API_SECRET
```

### 4. Google OAuth Setup
1. Go to [Google Cloud Console](https://console.cloud.google.com)
2. Create a new project
3. Enable Google+ API
4. Create OAuth 2.0 credentials
5. Add authorized redirect URI: `http://localhost:8080/api/auth/oauth2/callback/google`
6. Copy Client ID and Client Secret to `application.properties`

### 5. Cloudinary Setup
1. Sign up at [Cloudinary](https://cloudinary.com/)
2. Get your Cloud Name, API Key, and API Secret from the dashboard
3. Add them to `application.properties`

### 6. Build and Run
```bash
mvn clean install
mvn spring-boot:run
```

Server will start at `http://localhost:8080`

## API Endpoints

### Authentication (`/api/auth`)

#### Register
```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "universityId": 1,
  "graduationYear": 2024
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "email": "john@example.com",
  "name": "John Doe",
  "role": "STUDENT"
}
```

#### Google OAuth
```http
GET /api/auth/oauth2/callback/google
```

### Posts (`/api/posts`)

#### Create Post
```http
POST /api/posts
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "content": "Has anyone found the OS notes?",
  "type": "NORMAL",
  "isAnonymous": false
}
```

#### Get Feed
```http
GET /api/posts/feed?universityId=1&page=0&size=10
Authorization: Bearer YOUR_JWT_TOKEN
```

#### Like Post
```http
POST /api/posts/{postId}/like
Authorization: Bearer YOUR_JWT_TOKEN
```

#### Add Comment
```http
POST /api/posts/{postId}/comment
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "content": "Check the library!"
}
```

### PYQ (`/api/pyq`)

#### Upload PYQ
```http
POST /api/pyq/upload
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: multipart/form-data

subject: Data Structures
year: 2023
examType: MID_SEM
file: [PDF file]
```

#### Search PYQs
```http
GET /api/pyq?universityId=1&subject=Data Structures&year=2023
Authorization: Bearer YOUR_JWT_TOKEN
```

### Alumni (`/api/alumni`)

#### Create Alumni Profile
```http
POST /api/alumni/profile
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "company": "Google",
  "jobRole": "Software Engineer",
  "yearsOfExperience": 3,
  "review": "Focus on DSA!",
  "linkedinUrl": "https://linkedin.com/in/johndoe"
}
```

#### Get Alumni List
```http
GET /api/alumni?universityId=1
Authorization: Bearer YOUR_JWT_TOKEN
```

### Marketplace (`/api/marketplace`)

#### Add Item
```http
POST /api/marketplace/items
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "title": "Hero Ranger Cycle",
  "description": "Used for 6 months",
  "price": 3500,
  "category": "BIKES"
}
```

#### Get Items
```http
GET /api/marketplace/items?universityId=1&category=BIKES
Authorization: Bearer YOUR_JWT_TOKEN
```

### Events (`/api/events`)

#### Create Event (Admin Only)
```http
POST /api/events
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "title": "Tech Fest 2024",
  "description": "Annual tech symposium",
  "eventDate": "2024-10-15T10:00:00",
  "venue": "Main Auditorium",
  "university": { "id": 1 }
}
```

#### Get Events
```http
GET /api/events?universityId=1
```

## Database Schema

### Core Tables
- `users` - User accounts
- `universities` - University information
- `posts` - Feed posts
- `comments` - Post comments
- `pyqs` - Previous year questions
- `alumni_profiles` - Alumni information
- `marketplace_items` - Campus marketplace
- `events` - University events
- `clubs` - Student clubs
- `pgs` - PG/Hostel listings
- `food_courts` - Food court information

## Security

- **Password Encryption**: BCrypt hashing
- **JWT Tokens**: Stateless authentication
- **OAuth**: Secure Google integration
- **Role-Based Access**: `@PreAuthorize` annotations
- **CORS**: Configured for frontend origins

## Error Handling

All errors return consistent JSON format:
```json
{
  "error": "Error message"
}
```

- `400 Bad Request` - Validation errors
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server errors

## Testing

Use Postman or any REST client to test the APIs.

1. Register a user
2. Login to get JWT token
3. Use token in Authorization header as `Bearer YOUR_TOKEN`
4. Test all endpoints

## Production Deployment

1. Update MySQL connection for production database
2. Generate a secure JWT secret key
3. Configure production Cloudinary credentials
4. Set OAuth redirect URIs for production domain
5. Build JAR: `mvn clean package`
6. Run JAR: `java -jar target/backend-1.0.0.jar`

## Project Structure

```
src/main/java/com/uniconnect/backend/
├── config/          # Security, CORS, Cloudinary config
├── controller/      # REST controllers
├── dto/             # Data Transfer Objects
├── entity/          # JPA entities
├── exception/       # Custom exceptions
├── repository/      # JPA repositories
├── security/        # JWT utilities, filters
└── service/         # Business logic
```


