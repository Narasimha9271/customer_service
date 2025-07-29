package com.bank.customer_service.dto;

import lombok.Data;

@Data
public class TransferRequestDTO {
    private String toUsername;
    private Double amount;
}
