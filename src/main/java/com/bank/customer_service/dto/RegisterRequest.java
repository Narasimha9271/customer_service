package com.bank.customer_service.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String aadhaar;
    private String dob; // will be parsed
    private String pan;
    private String gender;
    private String username;
    private String password;
    private String accountType;
    private String accountNumber;
    private Double balance;
    private Long branchId;
}
