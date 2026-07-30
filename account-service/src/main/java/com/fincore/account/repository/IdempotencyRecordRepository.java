package com.fincore.account.repository;

import com.fincore.account.model.IdempotencyRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdempotencyRecordRepository extends MongoRepository<IdempotencyRecord, String> {
    Optional<IdempotencyRecord> findByIdempotencyKey(String idempotencyKey);
    Boolean existsByIdempotencyKey(String idempotencyKey);
}