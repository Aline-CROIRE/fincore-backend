package com.fincore.account.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fincore.account.dto.AccountStatusUpdateRequest;
import com.fincore.account.dto.CreateAccountRequest;
import com.fincore.account.dto.UpdateBalanceRequest;
import com.fincore.account.model.Account;
import com.fincore.account.model.IdempotencyRecord;
import com.fincore.account.repository.AccountRepository;
import com.fincore.account.repository.IdempotencyRecordRepository;
import com.fincore.account.service.AccountService;
import com.fincore.common.dto.AccountDto;
import com.fincore.common.enums.AccountStatus;
import com.fincore.common.exception.BadRequestException;
import com.fincore.common.exception.InsufficientBalanceException;
import com.fincore.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final IdempotencyRecordRepository idempotencyRepository;
    private final ObjectMapper objectMapper;

    @Override
    public AccountDto createAccount(CreateAccountRequest request, String idempotencyKey) {
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<IdempotencyRecord> existingRecord = idempotencyRepository.findByIdempotencyKey(idempotencyKey);
            if (existingRecord.isPresent()) {
                try {
                    return objectMapper.readValue(existingRecord.get().getResponseBody(), AccountDto.class);
                } catch (JsonProcessingException e) {
                    throw new BadRequestException("Failed to parse cached response for idempotency key");
                }
            }
        }

        String accountNumber = generateUniqueAccountNumber();

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .userId(request.getUserId())
                .type(request.getAccountType())
                .balance(request.getInitialBalance())
                .currency(request.getCurrency().toUpperCase())
                .status(AccountStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Account savedAccount = accountRepository.save(account);
        AccountDto dto = mapToDto(savedAccount);

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            try {
                IdempotencyRecord record = IdempotencyRecord.builder()
                        .idempotencyKey(idempotencyKey)
                        .responseBody(objectMapper.writeValueAsString(dto))
                        .statusCode(201)
                        .createdAt(LocalDateTime.now())
                        .build();
                idempotencyRepository.save(record);
            } catch (JsonProcessingException ignored) {
            }
        }

        return dto;
    }

    @Override
    public AccountDto getAccountByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));
        return mapToDto(account);
    }

    @Override
    public List<AccountDto> getAccountsByUserId(String userId) {
        return accountRepository.findByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public AccountDto updateBalance(String accountNumber, UpdateBalanceRequest request, String idempotencyKey) {
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<IdempotencyRecord> existingRecord = idempotencyRepository.findByIdempotencyKey(idempotencyKey);
            if (existingRecord.isPresent()) {
                try {
                    return objectMapper.readValue(existingRecord.get().getResponseBody(), AccountDto.class);
                } catch (JsonProcessingException e) {
                    throw new BadRequestException("Failed to parse cached idempotency result");
                }
            }
        }

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("Account is not ACTIVE");
        }

        if ("CREDIT".equalsIgnoreCase(request.getOperation())) {
            account.setBalance(account.getBalance().add(request.getAmount()));
        } else if ("DEBIT".equalsIgnoreCase(request.getOperation())) {
            if (account.getBalance().compareTo(request.getAmount()) < 0) {
                throw new InsufficientBalanceException("Insufficient balance for account: " + accountNumber);
            }
            account.setBalance(account.getBalance().subtract(request.getAmount()));
        } else {
            throw new BadRequestException("Invalid operation. Must be CREDIT or DEBIT");
        }

        account.setUpdatedAt(LocalDateTime.now());
        Account updatedAccount = accountRepository.save(account);
        AccountDto dto = mapToDto(updatedAccount);

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            try {
                IdempotencyRecord record = IdempotencyRecord.builder()
                        .idempotencyKey(idempotencyKey)
                        .responseBody(objectMapper.writeValueAsString(dto))
                        .statusCode(200)
                        .createdAt(LocalDateTime.now())
                        .build();
                idempotencyRepository.save(record);
            } catch (JsonProcessingException ignored) {
            }
        }

        return dto;
    }

    @Override
    public AccountDto updateStatus(String accountNumber, AccountStatusUpdateRequest request) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));

        account.setStatus(request.getStatus());
        account.setUpdatedAt(LocalDateTime.now());
        return mapToDto(accountRepository.save(account));
    }

    private String generateUniqueAccountNumber() {
        String accountNumber;
        Random random = new Random();
        do {
            accountNumber = "FC-" + (100000 + random.nextInt(900000));
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

    private AccountDto mapToDto(Account account) {
        return AccountDto.builder()
                .accountNumber(account.getAccountNumber())
                .userId(account.getUserId())
                .type(account.getType())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .status(account.getStatus())
                .build();
    }
}