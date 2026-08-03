#  FinCore Backend

## Enterprise Digital Banking Platform

---

#  Overview

**FinCore** is a production-grade digital banking backend platform built using a modern **event-driven microservices architecture**.

The system is designed to simulate the architecture of real-world financial platforms by separating banking domains into independent services with dedicated databases, secure communication patterns, and scalable infrastructure.

FinCore provides:

*  Stateless JWT authentication
*  Multi-account banking operations
*  Secure money transfers
*  Transaction idempotency protection
*  Ev Service-to-service communication using OpenFeign
* OpenAPI / Swagger API documentation
*  Containerized deployment using Docker

The platform follows enterprise banking principles including **database isolation, fault tolerance, scalability, and secure distributed communication**.

---

#  System Architecture

```
                 React + TypeScript Client
                           |
                           |
                           v

                 Spring Cloud Gateway
                      Port: 8080

                           |
      ------------------------------------------------
      |              |              |                |
      v              v              v                v

 Auth Service   User Service  Account Service  Transaction Service
   :8081          :8082          :8083              :8084

      |              |              |                |
      v              v              v                v

 auth_db       user_db       account_db      transaction_db


                           |
                           v

                    RabbitMQ Event Bus
                       Port: 5672

                           |
                           v

              Notification Service
                    :8085

                    notification_db
```

---

#  Microservices Architecture

| Service              | Port | Database        | Responsibility                                                    |
| -------------------- | ---- | --------------- | ----------------------------------------------------------------- |
| API Gateway          | 8080 | None            | Central entry point, routing, JWT validation, CORS handling       |
| Auth Service         | 8081 | auth_db         | Registration, authentication, password encryption, JWT generation |
| User Service         | 8082 | user_db         | Customer profiles, identity information management                |
| Account Service      | 8083 | account_db      | Account creation, balance management, account operations          |
| Transaction Service  | 8084 | transaction_db  | Transfers, deposits, transaction processing                       |
| Notification Service | 8085 | notification_db | Transaction events, alerts, notification history                  |

---

#  Core Features

##  Authentication & Authorization

FinCore implements secure authentication using:

* JWT-based stateless security
* BCrypt password hashing
* Role-based authorization
* Gateway-level token verification

Authentication flow:

```
Client
 |
 | Login Request
 |
 v
Auth Service
 |
 | Generate JWT
 |
 v
API Gateway
 |
 | Validate Token
 |
 v
Backend Services
```

---

#  Banking Operations

## Account Management

Supported operations:

* Create customer accounts
* Manage multiple accounts per user
* Track account balances
* Support different account types

Example:

```json
{
 "accountNumber": "FC-100023",
 "type": "SAVINGS",
 "currency": "RWF",
 "balance": 4605000,
 "status": "ACTIVE"
}
```

---

##  Transaction Processing

FinCore supports:

* Account-to-account transfers
* Deposits
* Transaction history
* Transaction references
* Status tracking

Example:

```json
{
 "transactionReference": "TRX-8392928374",
 "senderAccountNumber": "FC-100023",
 "receiverAccountNumber": "FC-100024",
 "amount": 50000,
 "currency": "RWF",
 "type": "TRANSFER",
 "status": "SUCCESS"
}
```

---

#  Transaction Idempotency Protection

Financial systems must prevent duplicate transactions caused by:

* Network retries
* Client refreshes
* Timeout recovery
* Duplicate API calls

FinCore implements idempotency using:

```
X-Idempotency-Key
```

Workflow:

```
Client Request
      |
      v
Check Idempotency Collection
      |
      |
 Exists?
  /    \
Yes     No
 |       |
Return   Process Transaction
Cached       |
Response     |
             v
       Save Result
```

Example:

```
POST /transactions

Headers:

X-Idempotency-Key:
8f91d7a9-transfer-payment
```

The same request will always return the original response without creating duplicate charges.

---

#  Event Driven Architecture

FinCore uses RabbitMQ for asynchronous communication.

Transaction flow:

```
Transaction Service

        |
        |
        v

TransactionCompletedEvent

        |
        |
        v

RabbitMQ Exchange

banking.exchange

        |
        |
        v

Notification Service
```

Example event:

```json
{
 "eventType":"TransactionCompleted",
 "transactionReference":"TRX-8392928374",
 "amount":50000,
 "status":"SUCCESS"
}
```

Benefits:

* Loose coupling
* Better scalability
* Faster processing
* Reliable event delivery

---

# 🔗 Service Communication

## OpenFeign Integration

Synchronous communication is handled through Spring Cloud OpenFeign.

Example:

```
Transaction Service
        |
        |
        v

Account Service

Verify Balance
      |
      |
Approve Transaction
```

Used for:

* Balance verification
* Account validation
* Customer information retrieval

---

#  Database Design

FinCore follows the:

## Database-per-Service Pattern

Each microservice owns its database.

Advantages:

 Independent scaling
 Improved security
 Service isolation
 Better maintainability

---

## Auth Database

`auth_db.users`

```json
{
 "_id":"65d1f8a2e4b0a12345678901",
 "userId":"USR-D5F2BADA",
 "email":"customer@fincore.com",
 "password":"encrypted_password",
 "role":"ROLE_CUSTOMER",
 "active":true
}
```

---

## Account Database

`account_db.accounts`

```json
{
 "_id":"65d1f8a2e4b0a12345678903",
 "accountNumber":"FC-100023",
 "userId":"USR-D5F2BADA",
 "type":"SAVINGS",
 "balance":4605000,
 "currency":"RWF",
 "status":"ACTIVE"
}
```

---

## Transaction Database

`transaction_db.transactions`

```json
{
 "_id":"65d1f8a2e4b0a12345678904",
 "transactionReference":"TRX-8392928374",
 "amount":50000,
 "type":"TRANSFER",
 "status":"SUCCESS",
 "timestamp":"2026-03-30T10:15:00Z"
}
```

---

#  Security Architecture

FinCore applies multiple security layers:

## API Gateway Security

* JWT validation
* Request filtering
* CORS configuration
* Secure routing

## Service Security

* Role-based access control
* Protected endpoints
* Secure internal communication

## Data Security

* BCrypt password encryption
* Isolated databases
* No shared credentials

---

#  API Documentation

Every microservice provides interactive Swagger documentation.

Available endpoints:

```
http://localhost:8081/swagger-ui.html

http://localhost:8082/swagger-ui.html

http://localhost:8083/swagger-ui.html

http://localhost:8084/swagger-ui.html

http://localhost:8085/swagger-ui.html
```

---

#  Running FinCore Locally

## Requirements

Install:

* Java 21+
* Maven 3.9+
* Docker Desktop
* MongoDB
* RabbitMQ

---

# Step 1: Start Infrastructure

Run:

```bash
docker-compose up -d mongodb rabbitmq
```

Check containers:

```bash
docker ps
```

---

# Step 2: Build All Services

```bash
mvn clean install -DskipTests
```

---

# Step 3: Start Complete Platform

```bash
docker-compose up -d
```

---

# Step 4: Verify Services

Gateway:

```
http://localhost:8080
```

Services:

```
Auth Service:
http://localhost:8081

User Service:
http://localhost:8082

Account Service:
http://localhost:8083

Transaction Service:
http://localhost:8084

Notification Service:
http://localhost:8085
```

---

#  Project Structure

```
fincore-backend

├── api-gateway
│
├── auth-service
│
├── user-service
│
├── account-service
│
├── transaction-service
│
├── notification-service
│
├── docker-compose.yml
│
└── pom.xml
```

---

#  Technology Stack

## Backend

* Java 21
* Spring Boot 3
* Spring Cloud
* Spring Security
* Spring Data MongoDB

## Communication

* REST APIs
* OpenFeign
* RabbitMQ

## Database

* MongoDB Atlas

## DevOps

* Docker
* Docker Compose
* Maven

---

#  Future Improvements

Planned enterprise enhancements:

* Kubernetes deployment
* Distributed tracing with Zipkin
* Centralized logging using ELK Stack
* Redis caching
* Payment gateway integration
* Two-factor authentication
* Fraud detection with Machine Learning
* CI/CD pipeline automation

---

#  Author

**FinCore Backend Engineering Team**

Built with enterprise software engineering principles focusing on:

* Scalability
* Reliability
* Security
* Maintainability

---

⭐ If you find this project useful, consider giving it a star.
