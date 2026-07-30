code
Markdown
# MongoDB Collections Schema

## Auth Database (`auth_db`)
Collection: `users`
```json
{
  "_id": "65d1f8a2e4b0a12345678901",
  "userId": "USR-982341",
  "email": "customer@fincore.com",
  "password": "$2a$10$e8N0Vz5C4QjW5pL6qK7v8e1r2t3y4u5i6o7p8a9b0c1d2e3f4g5h6",
  "role": "ROLE_CUSTOMER",
  "active": true,
  "createdAt": "2026-03-30T10:00:00Z",
  "updatedAt": "2026-03-30T10:00:00Z"
}
User Database (user_db)
Collection: profiles
code
JSON
{
  "_id": "65d1f8a2e4b0a12345678902",
  "userId": "USR-982341",
  "firstName": "Aline",
  "lastName": "Niyonizera",
  "phoneNumber": "+250790000000",
  "nationalId": "1199880011223344",
  "address": {
    "street": "123 Main St",
    "city": "Kigali",
    "country": "Rwanda"
  },
  "status": "ACTIVE",
  "createdAt": "2026-03-30T10:05:00Z"
}
Account Database (account_db)
Collection: accounts
code
JSON
{
  "_id": "65d1f8a2e4b0a12345678903",
  "accountNumber": "FC-100023",
  "userId": "USR-982341",
  "type": "SAVINGS",
  "balance": 500000.00,
  "currency": "RWF",
  "status": "ACTIVE",
  "createdAt": "2026-03-30T10:10:00Z"
}
Transaction Database (transaction_db)
Collection: transactions
code
JSON
{
  "_id": "65d1f8a2e4b0a12345678904",
  "transactionReference": "TRX-8392928374",
  "senderAccountNumber": "FC-100023",
  "receiverAccountNumber": "FC-100024",
  "amount": 50000.00,
  "currency": "RWF",
  "type": "TRANSFER",
  "status": "SUCCESS",
  "description": "Payment for services",
  "timestamp": "2026-03-30T10:15:00Z"
}
Notification Database (notification_db)
Collection: notifications
code
JSON
{
  "_id": "65d1f8a2e4b0a12345678905",
  "notificationId": "NOTIF-991283",
  "recipientUserId": "USR-982341",
  "channel": "EMAIL",
  "subject": "Transaction Confirmation",
  "message": "Transaction completed. Amount: 50,000 RWF. Reference: TRX-8392928374",
  "status": "SENT",
  "sentAt": "2026-03-30T10:15:02Z"
}