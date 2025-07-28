package com.bank.customer_service.dto;

import lombok.Data;

@Data
public class TransactionRequestDTO {
    private Long accountId;
    private Double amount;
    private String type; // "CREDIT" or "DEBIT"
    private String description;
}
