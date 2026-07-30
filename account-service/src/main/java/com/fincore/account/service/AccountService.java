package com.fincore.account.service;

import com.fincore.account.dto.AccountStatusUpdateRequest;
import com.fincore.account.dto.CreateAccountRequest;
import com.fincore.account.dto.UpdateBalanceRequest;
import com.fincore.common.dto.AccountDto;

import java.util.List;

public interface AccountService {
    AccountDto createAccount(CreateAccountRequest request, String idempotencyKey);
    AccountDto getAccountByNumber(String accountNumber);
    List<AccountDto> getAccountsByUserId(String userId);
    AccountDto updateBalance(String accountNumber, UpdateBalanceRequest request, String idempotencyKey);
    AccountDto updateStatus(String accountNumber, AccountStatusUpdateRequest request);
}