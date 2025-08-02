package com.bank.customer_service.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEventDTO {
    private String transactionType;
    private String accountNumber;
    private double amount;
    private String timestamp;
}
