package com.fincore.account.dto;

import com.fincore.common.enums.AccountStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatusUpdateRequest {
    @NotNull(message = "Status is required")
    private AccountStatus status;
}