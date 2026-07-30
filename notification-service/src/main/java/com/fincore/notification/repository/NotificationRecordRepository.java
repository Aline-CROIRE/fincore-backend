package com.fincore.notification.repository;

import com.fincore.notification.model.NotificationRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRecordRepository extends MongoRepository<NotificationRecord, String> {
    List<NotificationRecord> findByRecipientUserId(String recipientUserId);
}