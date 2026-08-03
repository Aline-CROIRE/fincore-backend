

#  FinCore Backend - Enterprise Digital Banking Platform

### *Production-Grade Event-Driven Microservices Architecture built with Java 21, Spring Boot 3, MongoDB, and RabbitMQ*

![Java 21](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot 3](https://img.shields.io/badge/Spring_Boot-3.2-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-2023.0-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-Atlas-47A248?style=for-the-badge&logo=mongodb&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3.12-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Containerized-2496ED?style=for-the-badge&logo=docker&logoColor=white)

</div>

---

##  Executive Summary

**FinCore** is an enterprise-grade digital banking microservices platform designed to mirror real-world core financial systems. It isolates business domains into independent microservices, each managing its own dedicated MongoDB database.

The platform provides stateless **JWT authentication**, **Spring Cloud Gateway** request routing, synchronous **OpenFeign** balance verification, asynchronous **RabbitMQ** transaction notifications, and strict **Idempotency Key Protection** to eliminate duplicate charges.

---

##  System Architecture & Microservices Matrix

                              [ React + TypeScript Client ]
                                            |
                                            v
                                   [ API Gateway :8080 ]
                                            |
     +------------------+-------------------+------------------+-------------------+
     |                  |                   |                  |                   |
     v                  v                   v                  v                   v

[ Auth Service ] [ User Service ] [ Account Service ] [ Trans. Service ] [
Notification ] Port: 8081 Port: 8082 Port: 8083 Port: 8084 Port: 8085 | | | | ^
v v v v | ((auth_db)) ((user_db)) ((account_db)) ((transaction_db)) | MongoDB
MongoDB MongoDB MongoDB | | | +---> [ RabbitMQ ] -+ Port: 5672


### Microservices Breakdown

| Microservice | Port | Database | Primary Responsibility |
| :--- | :--- | :--- | :--- |
| **API Gateway** | `8080` | None | Single entry point, JWT verification, CORS, route filtering |
| **Auth Service** | `8081` | `auth_db` | User registration, login, BCrypt hashing, JWT issuance |
| **User Service** | `8082` | `user_db` | Customer profile management, phone numbers, national IDs |
| **Account Service**| `8083` | `account_db` | Multi-account creation, balance operations, idempotency storage |
| **Transaction Service** | `8084` | `transaction_db` | Money transfers, deposits, Feign checks, RabbitMQ publishing |
| **Notification Service** | `8085` | `notification_db` | Asynchronous RabbitMQ alert consumer & user notification log |

---

##  Key Engineering Highlights

- **Database-per-Service Isolation**: Zero shared databases across services. Data consistency is maintained via REST OpenFeign calls and event messaging.
- **Transaction Idempotency Protection**: Requests with `X-Idempotency-Key` headers store produced outputs in MongoDB `idempotency_keys` collections. Network retries return cached responses without executing double-charges.
- **Asynchronous Event-Driven Bus**: Transaction Service dispatches `TransactionCompletedEvent` to RabbitMQ topic exchange `banking.exchange`, which is consumed by Notification Service.
- **Stateless JWT Security**: API Gateway inspects `Authorization: Bearer <token>` headers, validates HMAC-SHA signatures, and attaches user claims (`X-User-Id`, `X-User-Role`) downstream.
- **OpenAPI 3.0 / Swagger Documentation**: Every service exposes live interactive Swagger UI endpoints (`/swagger-ui.html`).

---

##  Database Schemas (MongoDB)

### 1. `auth_db.users`
```json
{
  "_id": "65d1f8a2e4b0a12345678901",
  "userId": "USR-D5F2BADA",
  "email": "customer@fincore.com",
  "password": "$2a$10$e8N0Vz5C4QjW5pL6qK7v8e1r2t3y4u5i6o7p8a9b0c1d2e3f4g5h6",
  "role": "ROLE_CUSTOMER",
  "active": true
}

2. account_db.accounts

{
  "_id": "65d1f8a2e4b0a12345678903",
  "accountNumber": "FC-100023",
  "userId": "USR-D5F2BADA",
  "type": "SAVINGS",
  "balance": 4605000.00,
  "currency": "RWF",
  "status": "ACTIVE"
}

3. transaction_db.transactions

{
  "_id": "65d1f8a2e4b0a12345678904",
  "transactionReference": "TRX-8392928374",
  "senderAccountNumber": "FC-100023",
  "receiverAccountNumber": "FC-100024",
  "amount": 50000.00,
  "type": "TRANSFER",
  "status": "SUCCESS",
  "timestamp": "2026-03-30T10:15:00Z"
}

 Local Setup & Execution

1. Start Infrastructure Containers (MongoDB & RabbitMQ)

docker-compose up -d mongodb rabbitmq

2. Build All Microservices

mvn clean install -DskipTests

3. Run Stack via Docker Compose

docker-compose up -d

API Gateway will be active at http://localhost:8080.
