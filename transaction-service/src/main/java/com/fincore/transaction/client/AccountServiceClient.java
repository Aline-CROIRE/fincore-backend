package com.fincore.transaction.client;

import com.fincore.common.dto.AccountDto;
import com.fincore.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.Map;

@FeignClient(name = "account-service", url = "${account-service.url}")
public interface AccountServiceClient {

    @GetMapping("/api/accounts/{accountNumber}")
    ApiResponse<AccountDto> getAccountByNumber(@PathVariable("accountNumber") String accountNumber);

    @PutMapping("/api/accounts/{accountNumber}/balance")
    ApiResponse<AccountDto> updateBalance(
            @PathVariable("accountNumber") String accountNumber,
            @RequestBody Map<String, Object> request
    );
}