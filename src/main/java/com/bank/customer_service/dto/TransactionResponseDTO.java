package com.bank.customer_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransactionResponseDTO {
    private Long id;
    private String type;
    private Double credit;
    private Double debit;
    private LocalDateTime timestamp;
    private Long customerId;
    private String customerUsername;
}
