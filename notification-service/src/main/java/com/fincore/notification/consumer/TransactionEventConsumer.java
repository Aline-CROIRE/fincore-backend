package com.fincore.notification.consumer;

import com.fincore.common.event.TransactionCompletedEvent;
import com.fincore.notification.config.RabbitMQConfig;
import com.fincore.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionEventConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void handleTransactionCompleted(TransactionCompletedEvent event) {
        notificationService.processTransactionCompletedEvent(event);
    }
}