package com.bank.customer_service.dto;

import lombok.Data;

@Data
public class CustomerRegisterDTO {
    private String username;
    private String password;
    private String email;
}
