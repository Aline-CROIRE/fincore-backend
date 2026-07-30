package com.fincore.account.repository;

import com.fincore.account.model.Account;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends MongoRepository<Account, String> {
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findByUserId(String userId);
    Boolean existsByAccountNumber(String accountNumber);
}