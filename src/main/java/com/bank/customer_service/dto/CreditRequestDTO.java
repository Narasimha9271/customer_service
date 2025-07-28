package com.bank.customer_service.dto;

import lombok.Data;

@Data
public class CreditRequestDTO {
    private Long customerId;
    private Double amount;
}
