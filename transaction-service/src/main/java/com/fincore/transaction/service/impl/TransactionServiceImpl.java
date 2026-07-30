package com.fincore.transaction.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fincore.common.dto.AccountDto;
import com.fincore.common.dto.ApiResponse;
import com.fincore.common.dto.TransactionDto;
import com.fincore.common.enums.AccountStatus;
import com.fincore.common.enums.TransactionStatus;
import com.fincore.common.enums.TransactionType;
import com.fincore.common.event.TransactionCompletedEvent;
import com.fincore.common.exception.BadRequestException;
import com.fincore.common.exception.InsufficientBalanceException;
import com.fincore.common.exception.ResourceNotFoundException;
import com.fincore.transaction.client.AccountServiceClient;
import com.fincore.transaction.config.RabbitMQConfig;
import com.fincore.transaction.dto.DepositRequest;
import com.fincore.transaction.dto.TransferRequest;
import com.fincore.transaction.model.IdempotencyRecord;
import com.fincore.transaction.model.Transaction;
import com.fincore.transaction.repository.IdempotencyRecordRepository;
import com.fincore.transaction.repository.TransactionRepository;
import com.fincore.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final IdempotencyRecordRepository idempotencyRepository;
    private final AccountServiceClient accountServiceClient;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public TransactionDto transfer(TransferRequest request, String idempotencyKey) {
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<IdempotencyRecord> record = idempotencyRepository.findByIdempotencyKey(idempotencyKey);
            if (record.isPresent()) {
                try {
                    return objectMapper.readValue(record.get().getResponseBody(), TransactionDto.class);
                } catch (JsonProcessingException e) {
                    throw new BadRequestException("Failed to read cached idempotency response");
                }
            }
        }

        if (request.getSenderAccountNumber().equals(request.getReceiverAccountNumber())) {
            throw new BadRequestException("Sender and receiver accounts cannot be identical");
        }

        ApiResponse<AccountDto> senderRes = accountServiceClient.getAccountByNumber(request.getSenderAccountNumber());
        ApiResponse<AccountDto> receiverRes = accountServiceClient.getAccountByNumber(request.getReceiverAccountNumber());

        if (senderRes == null || senderRes.getData() == null) {
            throw new ResourceNotFoundException("Sender account not found");
        }
        if (receiverRes == null || receiverRes.getData() == null) {
            throw new ResourceNotFoundException("Receiver account not found");
        }

        AccountDto sender = senderRes.getData();
        AccountDto receiver = receiverRes.getData();

        if (sender.getStatus() != AccountStatus.ACTIVE || receiver.getStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("One or both accounts are not ACTIVE");
        }

        if (sender.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Sender has insufficient balance");
        }

        Map<String, Object> debitReq = new HashMap<>();
        debitReq.put("amount", request.getAmount());
        debitReq.put("operation", "DEBIT");
        accountServiceClient.updateBalance(request.getSenderAccountNumber(), debitReq);

        Map<String, Object> creditReq = new HashMap<>();
        creditReq.put("amount", request.getAmount());
        creditReq.put("operation", "CREDIT");
        accountServiceClient.updateBalance(request.getReceiverAccountNumber(), creditReq);

        String reference = "TRX-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();

        Transaction transaction = Transaction.builder()
                .transactionReference(reference)
                .senderAccountNumber(request.getSenderAccountNumber())
                .receiverAccountNumber(request.getReceiverAccountNumber())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.SUCCESS)
                .description(request.getDescription())
                .timestamp(LocalDateTime.now())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);
        TransactionDto dto = mapToDto(savedTransaction);

        TransactionCompletedEvent event = TransactionCompletedEvent.builder()
                .transactionReference(reference)
                .senderAccountNumber(request.getSenderAccountNumber())
                .receiverAccountNumber(request.getReceiverAccountNumber())
                .userId(sender.getUserId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .type(TransactionType.TRANSFER.name())
                .status(TransactionStatus.SUCCESS.name())
                .timestamp(LocalDateTime.now())
                .build();

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, event);

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            try {
                IdempotencyRecord rec = IdempotencyRecord.builder()
                        .idempotencyKey(idempotencyKey)
                        .responseBody(objectMapper.writeValueAsString(dto))
                        .createdAt(LocalDateTime.now())
                        .build();
                idempotencyRepository.save(rec);
            } catch (JsonProcessingException ignored) {
            }
        }

        return dto;
    }

    @Override
    public TransactionDto deposit(DepositRequest request, String idempotencyKey) {
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<IdempotencyRecord> record = idempotencyRepository.findByIdempotencyKey(idempotencyKey);
            if (record.isPresent()) {
                try {
                    return objectMapper.readValue(record.get().getResponseBody(), TransactionDto.class);
                } catch (JsonProcessingException e) {
                    throw new BadRequestException("Failed to read cached idempotency response");
                }
            }
        }

        ApiResponse<AccountDto> accountRes = accountServiceClient.getAccountByNumber(request.getAccountNumber());
        if (accountRes == null || accountRes.getData() == null) {
            throw new ResourceNotFoundException("Account not found");
        }

        Map<String, Object> creditReq = new HashMap<>();
        creditReq.put("amount", request.getAmount());
        creditReq.put("operation", "CREDIT");
        accountServiceClient.updateBalance(request.getAccountNumber(), creditReq);

        String reference = "DEP-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();

        Transaction transaction = Transaction.builder()
                .transactionReference(reference)
                .senderAccountNumber("EXTERNAL_DEPOSIT")
                .receiverAccountNumber(request.getAccountNumber())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.SUCCESS)
                .description("Account cash deposit")
                .timestamp(LocalDateTime.now())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);
        TransactionDto dto = mapToDto(savedTransaction);

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            try {
                IdempotencyRecord rec = IdempotencyRecord.builder()
                        .idempotencyKey(idempotencyKey)
                        .responseBody(objectMapper.writeValueAsString(dto))
                        .createdAt(LocalDateTime.now())
                        .build();
                idempotencyRepository.save(rec);
            } catch (JsonProcessingException ignored) {
            }
        }

        return dto;
    }

    @Override
    public List<TransactionDto> getAccountTransactionHistory(String accountNumber) {
        return transactionRepository.findBySenderAccountNumberOrReceiverAccountNumber(accountNumber, accountNumber)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionDto getTransactionByReference(String reference) {
        Transaction transaction = transactionRepository.findByTransactionReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + reference));
        return mapToDto(transaction);
    }

    private TransactionDto mapToDto(Transaction transaction) {
        return TransactionDto.builder()
                .transactionReference(transaction.getTransactionReference())
                .senderAccountNumber(transaction.getSenderAccountNumber())
                .receiverAccountNumber(transaction.getReceiverAccountNumber())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .description(transaction.getDescription())
                .timestamp(transaction.getTimestamp())
                .build();
    }
}