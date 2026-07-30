package com.fincore.transaction.service;

import com.fincore.common.dto.TransactionDto;
import com.fincore.transaction.dto.DepositRequest;
import com.fincore.transaction.dto.TransferRequest;

import java.util.List;

public interface TransactionService {
    TransactionDto transfer(TransferRequest request, String idempotencyKey);
    TransactionDto deposit(DepositRequest request, String idempotencyKey);
    List<TransactionDto> getAccountTransactionHistory(String accountNumber);
    TransactionDto getTransactionByReference(String reference);
}