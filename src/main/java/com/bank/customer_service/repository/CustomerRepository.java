package com.bank.customer_service.repository;

import com.bank.customer_service.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByUsername(String username);

    Optional<Customer> findByAccountNumber(String accountNumber);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
