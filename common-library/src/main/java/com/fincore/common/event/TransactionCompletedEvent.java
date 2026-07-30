package com.fincore.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCompletedEvent {
    private String transactionReference;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private String userId;
    private BigDecimal amount;
    private String currency;
    private String type;
    private String status;
    private LocalDateTime timestamp;
}