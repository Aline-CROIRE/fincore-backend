# FinCore Backend - Enterprise Digital Banking Platform

FinCore is a production-grade digital banking microservices backend built with Java 21, Spring Boot 3, MongoDB, RabbitMQ, and Spring Cloud Gateway.

## Architecture Highlights

- Single point of entry via API Gateway
- Stateless JWT authentication and Role-Based Access Control (RBAC)
- Isolated MongoDB instances per microservice
- Event-driven notifications via RabbitMQ
- OpenAPI 3.0 / Swagger documentation per service
- Containerized deployment ready via Docker and Docker Compose

## Service Breakdown

| Service | Port | Database | Description |
| :--- | :--- | :--- | :--- |
| API Gateway | 8080 | None | Security filtering, request routing, rate limiting |
| Auth Service | 8081 | auth_db | User registration, login, JWT token issuance |
| User Service | 8082 | user_db | Profile creation, updates, and customer management |
| Account Service | 8083 | account_db | Bank accounts, balance updates, status management |
| Transaction Service | 8084 | transaction_db | Funds transfer, deposits, withdrawals, history |
| Notification Service | 8085 | notification_db | Email alerts and transaction logging via RabbitMQ |

## Technology Stack

- **Java Version**: 21
- **Framework**: Spring Boot 3.2.x, Spring Cloud 2023.0.x
- **Database**: MongoDB (Individual Database per service)
- **Messaging**: RabbitMQ
- **Security**: Spring Security, JWT (JSON Web Tokens), BCrypt
- **Documentation**: Springdoc OpenAPI 3.0 (Swagger UI)
- **Containerization**: Docker, Docker Compose

## Prerequisites

- JDK 21
- Apache Maven 3.9+
- Docker & Docker Compose
- MongoDB (Local or Atlas)
- RabbitMQ

## Getting Started

1. Clone the repository:
```bash
git clone https://github.com/YOUR_GITHUB_USERNAME/fincore-backend.git
cd fincore-backend