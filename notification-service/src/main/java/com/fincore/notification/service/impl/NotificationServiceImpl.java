package com.fincore.notification.service.impl;

import com.fincore.common.event.TransactionCompletedEvent;
import com.fincore.notification.dto.NotificationResponse;
import com.fincore.notification.model.NotificationRecord;
import com.fincore.notification.repository.NotificationRecordRepository;
import com.fincore.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRecordRepository notificationRepository;

    @Override
    public void processTransactionCompletedEvent(TransactionCompletedEvent event) {
        String alertMessage = String.format("Transaction completed. Amount: %s %s. Reference: %s",
                event.getAmount(), event.getCurrency(), event.getTransactionReference());

        NotificationRecord record = NotificationRecord.builder()
                .notificationId("NOTIF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .recipientUserId(event.getUserId() != null ? event.getUserId() : "SYSTEM")
                .channel("EMAIL")
                .subject("Transaction Alert Confirmation")
                .message(alertMessage)
                .status("SENT")
                .sentAt(LocalDateTime.now())
                .build();

        notificationRepository.save(record);
    }

    @Override
    public List<NotificationResponse> getNotificationsByUserId(String userId) {
        return notificationRepository.findByRecipientUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private NotificationResponse mapToResponse(NotificationRecord record) {
        return NotificationResponse.builder()
                .notificationId(record.getNotificationId())
                .recipientUserId(record.getRecipientUserId())
                .channel(record.getChannel())
                .subject(record.getSubject())
                .message(record.getMessage())
                .status(record.getStatus())
                .sentAt(record.getSentAt())
                .build();
    }
}