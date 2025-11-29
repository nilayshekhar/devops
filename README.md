# 📁 Smart Appointment Booking System

🤖 API-Driven Appointment Management for Modern Businesses

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)
![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)
![Issues](https://img.shields.io/github/issues/BITSSAP2025AugAPIBP3Sections/APIBP-20234YC-Team-15)
![Contributors](https://img.shields.io/github/contributors/BITSSAP2025AugAPIBP3Sections/APIBP-20234YC-Team-15)

---

## 📋 Table of Contents

- [Overview](#overview)
- [System Architecture](#️-system-architecture)
- [Features](#-features)
- [Tech Stack](#️-tech-stack)
- [Getting Started](#-getting-started)
- [API Endpoints](#-api-endpoints)
- [Data Models](#-data-models)
- [Development](#-development)
- [Deployment](#-deployment)
- [Code Quality & PR Guidelines](#-code-quality--pr-guidelines)
- [Future Enhancements](#-future-enhancements)
- [Contributors](#-contributors)
- [License](#-license)

---

## Overview

### 📌 The Problem

Service providers and customers need a seamless, reliable, and secure way to manage appointments, users, and business flows. Manual scheduling leads to:
- ⏰ Inefficiency and wasted time
- 📅 Double bookings and conflicts
- 😞 Poor customer experience
- 📉 Lost revenue opportunities

### 💡 The Solution

**Smart Appointment Booking Platform** is a robust, API-first system that provides:
- ✅ Complete appointment lifecycle management via RESTful APIs
- 👥 User management with role-based access (Customer, Provider, Admin)
- 🗄️ PostgreSQL integration for reliable data storage
- 🩺 Health checks, logging, and monitoring for production readiness
- 🐳 Containerized deployment (Docker, Kubernetes) for scalability
- ⚛️ React-based frontend for intuitive user interaction

---

## 🏗️ System Architecture

| Service | Description | Key Tech | Port |
|---------|-------------|----------|------|
| **Backend API** | Core business logic, appointment/user APIs | Java, Spring Boot, PostgreSQL | 8080 |
| **Frontend** | Basic UI for booking and management | React, Nginx | 3000/80 |
| **Database** | Persistent storage for all entities | PostgreSQL | 5432 |
| **Logging/Monitoring** | Centralized logs, health checks | Logstash, Kibana, Elasticsearch | 9200/5601/5000 |

### 🔄 System Flow

```
┌─────────┐         ┌──────────────┐         ┌──────────┐
│  User   │ ──────> │ Frontend/API │ ──────> │ Database │
└─────────┘         └──────────────┘         └──────────┘
                           │
                           ▼
                    ┌──────────────┐
                    │   Logging    │
                    │  Monitoring  │
                    └──────────────┘
```

1. **User** interacts with the frontend or directly with the backend APIs
2. **Backend API** processes requests, manages appointments, users, and business logic
3. **Database** stores all persistent data
4. **Logging/Monitoring** captures events and health status for observability
5. **Deployment** is managed via Docker Compose and Kubernetes manifests

---

## 🚀 Features

### Core Features

- 📅 **Appointment Management** - CRUD operations, status updates, provider/customer filtering, statistics
- 👤 **User Management** - Registration, authentication, search, provider/customer roles
- 🔄 **Business Flow APIs** - Custom business logic endpoints
- 🩺 **Health Checks** - Spring Boot actuator, Docker healthchecks
- 📈 **Centralized Logging** - Logstash, Kibana integration
- 🛡️ **Containerized Deployment** - Docker Compose, Kubernetes

### Additional Features

- ⚛️ React frontend for booking and user flows
- 📖 Integration-ready API design (Swagger/OpenAPI)
- 🤖 Automated builds and deployment via Jenkins
- 🔒 Security best practices (environment variables, Docker isolation)
- 🧩 Modular codebase for easy extension

---

## 🛠️ Tech Stack

| Category | Technologies |
|----------|-------------|
| **Backend** | Java 17+, Spring Boot, Maven |
| **Database** | PostgreSQL |
| **Frontend** | React, Nginx |
| **Containerization** | Docker, Docker Compose, Kubernetes |
| **Logging/Monitoring** | Logstash, Kibana, Elasticsearch |
| **CI/CD** | Jenkins |
| **Version Control** | GitHub |

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- Node.js 16+
- Docker & Docker Compose
- PostgreSQL 13+

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/BITSSAP2025AugAPIBP3Sections/APIBP-20234YC-Team-15.git
   cd APIBP-20234YC-Team-15
   ```

2. **Set up the database**
   ```bash
   # Create PostgreSQL database
   createdb appointment_booking
   ```

3. **Configure environment variables**
   ```bash
   # Copy example env file
   cp .env.example .env
   
   # Edit with your configuration
   nano .env
   ```

4. **Build and run the backend**
   ```bash
   cd backend
   mvn clean install
   mvn spring-boot:run
   ```

5. **Build and run the frontend**
   ```bash
   cd frontend
   npm install
   npm start
   ```

6. **Access the application**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080
   - API Documentation: http://localhost:8080/swagger-ui.html

### Docker Deployment

```bash
# Build and start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### Kubernetes Deployment

```bash
# Apply Kubernetes manifests
kubectl apply -f k8s/

# Check deployment status
kubectl get pods
kubectl get services

# Access logs
kubectl logs -f <pod-name>
```

---

## 📡 API Endpoints

### 🗂️ User Management

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/users` | GET | Get all users |
| `/api/v1/users/{id}` | GET | Get user by ID |
| `/api/v1/users/search` | GET | Search users |
| `/api/v1/users/providers` | GET | Get service providers |
| `/api/v1/users/email/{email}` | GET | Get user by email |
| `/api/v1/users` | POST | Create user |
| `/api/v1/users/{id}` | PUT | Update user |
| `/api/v1/users/{id}` | DELETE | Delete user |

**User Data Model:**
- `id`, `name`, `email`, `phone`
- `role` (CUSTOMER, SERVICE_PROVIDER, ADMIN)
- `active`, `createdAt`, `updatedAt`

### 🗂️ Appointment Management

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/appointments` | GET | Get all appointments |
| `/api/v1/appointments/{id}` | GET | Get appointment by ID |
| `/api/v1/appointments/status/{status}` | GET | Get appointments by status |
| `/api/v1/appointments/stats` | GET | Get statistics |
| `/api/v1/appointments/search` | GET | Search appointments |
| `/api/v1/appointments/provider/{providerId}` | GET | Get provider appointments |
| `/api/v1/appointments/map` | GET | Get all appointments as map |
| `/api/v1/appointments/date-range` | GET | Get appointments by date range |
| `/api/v1/appointments/customer/{customerId}` | GET | Get customer appointments |
| `/api/v1/appointments/customer/{customerId}/upcoming` | GET | Get upcoming appointments |
| `/api/v1/appointments/{id}/status` | PATCH | Update status |
| `/api/v1/appointments` | POST | Create appointment |
| `/api/v1/appointments/{id}` | PUT | Update appointment |
| `/api/v1/appointments/{id}` | DELETE | Delete appointment |

**Appointment Data Model:**
- `id`, `customerId`, `serviceProviderId`
- `serviceType` (DOCTOR, DENTIST, BARBER, etc.)
- `appointmentDateTime`, `notes`
- `status` (PENDING, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW)
- `createdAt`, `updatedAt`, `past`, `upcoming`

### 🗂️ Authentication

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/auth/login` | POST | User login |

---

## 🧩 Data Models

### Request Models

**UserRequest:**
```json
{
  "name": "string",
  "email": "string",
  "password": "string",
  "phone": "string",
  "role": "CUSTOMER | SERVICE_PROVIDER | ADMIN"
}
```

**AppointmentRequest:**
```json
{
  "customerId": "long",
  "serviceProviderId": "long",
  "serviceType": "DOCTOR | DENTIST | BARBER | ...",
  "appointmentDateTime": "datetime",
  "notes": "string"
}
```

### Response Models

**UserResponse:**
```json
{
  "id": "long",
  "name": "string",
  "email": "string",
  "phone": "string",
  "role": "string",
  "active": "boolean",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

**AppointmentResponse:**
```json
{
  "id": "long",
  "customerId": "long",
  "customerName": "string",
  "customerEmail": "string",
  "customerPhone": "string",
  "serviceProviderId": "long",
  "serviceProviderName": "string",
  "serviceProviderEmail": "string",
  "serviceProviderPhone": "string",
  "serviceType": "string",
  "appointmentDateTime": "datetime",
  "notes": "string",
  "status": "string",
  "createdAt": "datetime",
  "updatedAt": "datetime",
  "past": "boolean",
  "upcoming": "boolean"
}
```

### Standard API Response Wrapper

All API responses are wrapped in a standard object:

```json
{
  "success": "boolean",
  "message": "string",
  "data": "object",
  "timestamp": "datetime",
  "statusCode": "integer",
  "errors": "array"
}
```

---

## 💻 Development

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run with coverage
mvn clean test jacoco:report
```

### Code Style

This project follows standard Java conventions and Spring Boot best practices.

```bash
# Format code
mvn spotless:apply

# Check code style
mvn spotless:check
```

---

## 🚀 Deployment

### Environment Variables

```env
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=appointment_booking
DB_USER=your_username
DB_PASSWORD=your_password

# Application
SERVER_PORT=8080
JWT_SECRET=your_jwt_secret

# Logging
LOG_LEVEL=INFO
```

### Production Checklist

- [ ] Environment variables configured
- [ ] Database migrations applied
- [ ] SSL/TLS certificates installed
- [ ] Health checks configured
- [ ] Monitoring and alerting set up
- [ ] Backup strategy in place
- [ ] Security audit completed

---

## 🧱 Code Quality & PR Guidelines

### Before Committing

Run tests before commits:
```bash
mvn clean test
mvn verify
npm test
docker-compose up --build
```

### Commit Message Format

Use meaningful commit messages:
```
feat: add appointment cancellation feature
fix: resolve double booking issue
docs: update API documentation
refactor: optimize database queries
```

### Pull Request Guidelines

- **Keep PRs small and focused** - One feature or fix per PR
- **PR title format:**
  ```
  [Type] Brief description
  
  Examples:
  [Feature] Add email notifications
  [Fix] Resolve timezone bug
  [Docs] Update README
  [Refactor] Improve error handling
  ```
- **Include tests** for new features
- **Update documentation** as needed
- **Request reviews** from at least one team member

---

## 🔮 Future Enhancements

- 📅 **Calendar integration** - Google Calendar, Outlook
- 📊 **Advanced analytics dashboards** - Business insights and trends
- 🔐 **Enhanced RBAC** - Fine-grained permissions
- 🤖 **AI-powered scheduling** - Smart suggestions and optimization
- 💬 **Real-time notifications** - WebSocket support
- 🌍 **Multi-language support** - Internationalization
- 📱 **Mobile apps** - Native iOS and Android apps
- 🔗 **Third-party integrations** - Zoom, Stripe, Twilio

---

## 👥 Contributors

<a href="https://github.com/BITSSAP2025AugAPIBP3Sections/APIBP-20234YC-Team-15/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=BITSSAP2025AugAPIBP3Sections/APIBP-20234YC-Team-15" />
</a>

### 🙌 Acknowledgments

Special thanks to:
- [Spring Boot](https://spring.io/projects/spring-boot)
- [React](https://reactjs.org/)
- [PostgreSQL](https://www.postgresql.org/)
- [Docker](https://www.docker.com/)
- [Kubernetes](https://kubernetes.io/)
- [Jenkins](https://www.jenkins.io/)
- [Logstash](https://www.elastic.co/logstash)
- [Kibana](https://www.elastic.co/kibana)

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

<div align="center">

### 🌟 Empowering Businesses through API Automation

**Developed by Nilay Shekhar**

</div>
