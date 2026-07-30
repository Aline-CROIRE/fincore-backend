package com.fincore.notification.service;

import com.fincore.common.event.TransactionCompletedEvent;
import com.fincore.notification.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {
    void processTransactionCompletedEvent(TransactionCompletedEvent event);
    List<NotificationResponse> getNotificationsByUserId(String userId);
}