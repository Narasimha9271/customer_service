package com.bank.customer_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;


    @Column(name = "type", nullable = false, unique = true)
    private String type;

    @Column(name = "min_balance")
    private Double minBalance;

    @Column(name = "status")
    private String status = "ACTIVE";

}
