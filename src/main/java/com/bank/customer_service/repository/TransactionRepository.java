package com.bank.customer_service.repository;

import com.bank.customer_service.entity.Transaction;
import com.bank.customer_service.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByCustomer(Customer customer);

    @Query("SELECT COALESCE(SUM(t.credit), 0) FROM Transaction t WHERE t.customer.id = :customerId")
    Double getTotalCredit(Long customerId);

    @Query("SELECT COALESCE(SUM(t.debit), 0) FROM Transaction t WHERE t.customer.id = :customerId")
    Double getTotalDebit(Long customerId);
}
