package com.fincore.notification.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class NotificationRecord {
    @Id
    private String id;

    @Indexed
    private String notificationId;

    @Indexed
    private String recipientUserId;

    private String channel;
    private String subject;
    private String message;
    private String status;
    private LocalDateTime sentAt;
}