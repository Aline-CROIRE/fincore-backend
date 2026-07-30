
Path: `fincore-backend/documentation/api-documentation.md`

```markdown
# REST API Specifications

## Auth Service APIs
- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/refresh`

## User Service APIs
- `POST /api/users`
- `GET /api/users/{userId}`
- `PUT /api/users/{userId}`
- `DELETE /api/users/{userId}`

## Account Service APIs
- `POST /api/accounts`
- `GET /api/accounts/{accountNumber}`
- `GET /api/accounts/user/{userId}`
- `PUT /api/accounts/{accountNumber}/status`

## Transaction Service APIs
- `POST /api/transactions/transfer`
- `POST /api/transactions/deposit`
- `POST /api/transactions/withdraw`
- `GET /api/transactions/account/{accountNumber}`

## Notification Service APIs
- `GET /api/notifications/user/{userId}