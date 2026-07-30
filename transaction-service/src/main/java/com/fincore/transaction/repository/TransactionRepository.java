package com.fincore.transaction.repository;

import com.fincore.transaction.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    Optional<Transaction> findByTransactionReference(String transactionReference);
    List<Transaction> findBySenderAccountNumberOrReceiverAccountNumber(String sender, String receiver);
}