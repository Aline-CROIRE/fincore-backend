package com.fincore.account.controller;

import com.fincore.account.dto.AccountStatusUpdateRequest;
import com.fincore.account.dto.CreateAccountRequest;
import com.fincore.account.dto.UpdateBalanceRequest;
import com.fincore.account.service.AccountService;
import com.fincore.common.dto.AccountDto;
import com.fincore.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Bank Account API", description = "Endpoints for bank account creation, balance operations, and idempotency protection")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Create a new bank account with idempotency key support")
    public ResponseEntity<ApiResponse<AccountDto>> createAccount(
            @Valid @RequestBody CreateAccountRequest request,
            @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey) {
        AccountDto createdAccount = accountService.createAccount(request, idempotencyKey);
        return new ResponseEntity<>(ApiResponse.<AccountDto>builder()
                .success(true)
                .message("Account created successfully")
                .data(createdAccount)
                .build(), HttpStatus.CREATED);
    }

    @GetMapping("/{accountNumber}")
    @Operation(summary = "Get account details by Account Number")
    public ResponseEntity<ApiResponse<AccountDto>> getAccountByNumber(@PathVariable String accountNumber) {
        AccountDto account = accountService.getAccountByNumber(accountNumber);
        return ResponseEntity.ok(ApiResponse.<AccountDto>builder()
                .success(true)
                .message("Account retrieved successfully")
                .data(account)
                .build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all accounts owned by a user")
    public ResponseEntity<ApiResponse<List<AccountDto>>> getAccountsByUserId(@PathVariable String userId) {
        List<AccountDto> accounts = accountService.getAccountsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.<List<AccountDto>>builder()
                .success(true)
                .message("Accounts retrieved successfully")
                .data(accounts)
                .build());
    }

    @PutMapping("/{accountNumber}/balance")
    @Operation(summary = "Update account balance (CREDIT/DEBIT) with idempotency protection")
    public ResponseEntity<ApiResponse<AccountDto>> updateBalance(
            @PathVariable String accountNumber,
            @Valid @RequestBody UpdateBalanceRequest request,
            @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey) {
        AccountDto account = accountService.updateBalance(accountNumber, request, idempotencyKey);
        return ResponseEntity.ok(ApiResponse.<AccountDto>builder()
                .success(true)
                .message("Balance updated successfully")
                .data(account)
                .build());
    }

    @PutMapping("/{accountNumber}/status")
    @Operation(summary = "Update account status (ACTIVE, BLOCKED, CLOSED)")
    public ResponseEntity<ApiResponse<AccountDto>> updateStatus(
            @PathVariable String accountNumber,
            @Valid @RequestBody AccountStatusUpdateRequest request) {
        AccountDto account = accountService.updateStatus(accountNumber, request);
        return ResponseEntity.ok(ApiResponse.<AccountDto>builder()
                .success(true)
                .message("Account status updated successfully")
                .data(account)
                .build());
    }
}