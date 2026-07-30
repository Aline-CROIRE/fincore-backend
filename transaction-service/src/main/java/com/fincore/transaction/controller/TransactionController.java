package com.fincore.transaction.controller;

import com.fincore.common.dto.ApiResponse;
import com.fincore.common.dto.TransactionDto;
import com.fincore.transaction.dto.DepositRequest;
import com.fincore.transaction.dto.TransferRequest;
import com.fincore.transaction.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction API", description = "Endpoints for funds transfer, cash deposits, and transaction audit records")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    @Operation(summary = "Execute money transfer between accounts with balance verification and RabbitMQ event dispatch")
    public ResponseEntity<ApiResponse<TransactionDto>> transfer(
            @Valid @RequestBody TransferRequest request,
            @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey) {
        TransactionDto transaction = transactionService.transfer(request, idempotencyKey);
        return ResponseEntity.ok(ApiResponse.<TransactionDto>builder()
                .success(true)
                .message("Transfer completed successfully")
                .data(transaction)
                .build());
    }

    @PostMapping("/deposit")
    @Operation(summary = "Deposit funds into a bank account")
    public ResponseEntity<ApiResponse<TransactionDto>> deposit(
            @Valid @RequestBody DepositRequest request,
            @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey) {
        TransactionDto transaction = transactionService.deposit(request, idempotencyKey);
        return ResponseEntity.ok(ApiResponse.<TransactionDto>builder()
                .success(true)
                .message("Deposit completed successfully")
                .data(transaction)
                .build());
    }

    @GetMapping("/account/{accountNumber}")
    @Operation(summary = "Get complete transaction history for an account")
    public ResponseEntity<ApiResponse<List<TransactionDto>>> getAccountHistory(@PathVariable String accountNumber) {
        List<TransactionDto> history = transactionService.getAccountTransactionHistory(accountNumber);
        return ResponseEntity.ok(ApiResponse.<List<TransactionDto>>builder()
                .success(true)
                .message("Transaction history retrieved successfully")
                .data(history)
                .build());
    }

    @GetMapping("/{reference}")
    @Operation(summary = "Get transaction details by unique reference")
    public ResponseEntity<ApiResponse<TransactionDto>> getTransactionByReference(@PathVariable String reference) {
        TransactionDto transaction = transactionService.getTransactionByReference(reference);
        return ResponseEntity.ok(ApiResponse.<TransactionDto>builder()
                .success(true)
                .message("Transaction retrieved successfully")
                .data(transaction)
                .build());
    }
}