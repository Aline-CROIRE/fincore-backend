# FinCore Microservices Cloud Deployment Guide

## 1. MongoDB Atlas Setup (Database Cloud Hosting)

1. Create a free account at [MongoDB Atlas](https://www.mongodb.com/cloud/atlas).
2. Create a new **M0 Free Cluster**.
3. Under **Database Access**, create a user (e.g., `fincore_admin`) with a strong password.
4. Under **Network Access**, add IP Address `0.0.0.0/0` (Allow access from anywhere).
5. Click **Connect** -> **Drivers** -> Copy your Connection String.
   Format:
   `mongodb+srv://fincore_admin:<PASSWORD>@cluster0.mongodb.net/?retryWrites=true&w=majority`

---

## 2. CloudAMQP Setup (Free RabbitMQ Cloud Instance)

1. Create a free account at [CloudAMQP](https://www.cloudamqp.com/).
2. Click **Create New Instance**. Select the **Little Lemur (Free)** plan.
3. Choose your region and click **Create Instance**.
4. Open your instance dashboard and copy the **AMQP URL**.
   Format:
   `amqps://username:password@hostname.rmq.cloudamqp.com/vhost`

---

## 3. Microservices Deployment on Render / Railway

Deploy services in the following order:

### 1. Auth Service
- **Type**: Web Service (Docker or Java Runtime)
- **Environment Variables**:
  - `SPRING_DATA_MONGODB_URI`: `mongodb+srv://fincore_admin:<PASSWORD>@cluster0.mongodb.net/auth_db?retryWrites=true&w=majority`
  - `JWT_SECRET`: `404E635266556A586E3272357538782F413F4428472B4B6250655368566D5970`
  - `PORT`: `8081`

---

### 2. User Service
- **Type**: Web Service
- **Environment Variables**:
  - `SPRING_DATA_MONGODB_URI`: `mongodb+srv://fincore_admin:<PASSWORD>@cluster0.mongodb.net/user_db?retryWrites=true&w=majority`
  - `PORT`: `8082`

---

### 3. Account Service
- **Type**: Web Service
- **Environment Variables**:
  - `SPRING_DATA_MONGODB_URI`: `mongodb+srv://fincore_admin:<PASSWORD>@cluster0.mongodb.net/account_db?retryWrites=true&w=majority`
  - `PORT`: `8083`

---

### 4. Transaction Service
- **Type**: Web Service
- **Environment Variables**:
  - `SPRING_DATA_MONGODB_URI`: `mongodb+srv://fincore_admin:<PASSWORD>@cluster0.mongodb.net/transaction_db?retryWrites=true&w=majority`
  - `SPRING_RABBITMQ_ADDRESSES`: `<CLOUDAMQP_AMQP_URL>`
  - `ACCOUNT_SERVICE_URL`: `https://fincore-account-service.onrender.com`
  - `PORT`: `8084`

---

### 5. Notification Service
- **Type**: Web Service
- **Environment Variables**:
  - `SPRING_DATA_MONGODB_URI`: `mongodb+srv://fincore_admin:<PASSWORD>@cluster0.mongodb.net/notification_db?retryWrites=true&w=majority`
  - `SPRING_RABBITMQ_ADDRESSES`: `<CLOUDAMQP_AMQP_URL>`
  - `PORT`: `8085`

---

### 6. API Gateway (Public Production URL)
- **Type**: Web Service
- **Environment Variables**:
  - `AUTH_SERVICE_URL`: `https://fincore-auth-service.onrender.com`
  - `USER_SERVICE_URL`: `https://fincore-user-service.onrender.com`
  - `ACCOUNT_SERVICE_URL`: `https://fincore-account-service.onrender.com`
  - `TRANSACTION_SERVICE_URL`: `https://fincore-transaction-service.onrender.com`
  - `NOTIFICATION_SERVICE_URL`: `https://fincore-notification-service.onrender.com`
  - `JWT_SECRET`: `404E635266556A586E3272357538782F413F4428472B4B6250655368566D5970`
  - `PORT`: `8080`

Public Gateway URL will be:
`https://fincore-api-gateway.onrender.com`