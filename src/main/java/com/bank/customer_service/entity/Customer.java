package com.bank.customer_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true)
    private String aadhaar;

    private LocalDate dob;

    @Column(unique = true)
    private String pan;

    private String gender;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    @Column(name = "branch_id")
    private Long branchId; // Reference to external Branch (Admin Service)

    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;

    private Double balance;

    @ManyToOne
    @JoinColumn(name = "account_type", referencedColumnName = "type")
    private Account accountType;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Transaction> transactions;
    private String role = "CUSTOMER";
}
