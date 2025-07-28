package com.bank.customer_service.dto;

import lombok.Data;

@Data
public class TransferRequestDTO {
    private Long fromCustomerId;
    private Long toCustomerId;
    private Double amount;
}
