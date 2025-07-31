package com.bank.customer_service.dto;

import lombok.Data;

@Data
public class CustomerResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String aadhaar;
    private String pan;
    private String gender;
    private String username;
    private String accountNumber;
    private Double balance;
    private Long branchId;
}
