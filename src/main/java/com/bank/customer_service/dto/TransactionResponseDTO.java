package com.bank.customer_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionResponseDTO {
    private Long transactionId;
    private Long accountId;
    private Double amount;
    private String type;
    private String refId;
    private String description;
    private LocalDateTime timestamp;
}
