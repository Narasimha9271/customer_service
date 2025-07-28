package com.bank.customer_service.repository;

import com.bank.customer_service.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByType(String type);
    Optional<Account> findByAccountNumber(String accountNumber);

}
