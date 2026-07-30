package com.fincore.common.dto;

import com.fincore.common.enums.AccountStatus;
import com.fincore.common.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private String accountNumber;
    private String userId;
    private AccountType type;
    private BigDecimal balance;
    private String currency;
    private AccountStatus status;
}