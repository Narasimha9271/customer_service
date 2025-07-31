package com.bank.customer_service.service;

import com.bank.customer_service.dto.CreditRequestDTO;
import com.bank.customer_service.dto.DebitRequestDTO;
import com.bank.customer_service.dto.TransactionResponseDTO;
import com.bank.customer_service.dto.TransferRequestDTO;
import com.bank.customer_service.entity.Customer;
import com.bank.customer_service.entity.Transaction;
import com.bank.customer_service.kafka.TransactionEventDTO;
import com.bank.customer_service.kafka.TransactionKafkaProducer;
import com.bank.customer_service.repository.CustomerRepository;
import com.bank.customer_service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    private TransactionKafkaProducer kafkaProducer;

    /**
     * ✅ Helper method to check if account is closed
     */
    private void validateAccountStatus(Customer customer) {
        if (customer.getAccountType() != null &&
                "CLOSED".equalsIgnoreCase(customer.getAccountType().getStatus())) {
            throw new RuntimeException("Account is closed. No transactions allowed.");
        }
    }

    /**
     * ✅ CREDIT money to the logged-in customer's account
     */
    @Transactional
    public String credit(String username, CreditRequestDTO dto) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (dto.getAmount() <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }


        validateAccountStatus(customer); // 🚨 check before proceeding

        // Add balance
        customer.setBalance(customer.getBalance() + dto.getAmount());
        customerRepository.save(customer);

        // Record transaction
        Transaction transaction = new Transaction();
        transaction.setCustomer(customer);
        transaction.setCredit(dto.getAmount());
        transaction.setType("CREDIT");
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);

        // Kafka event
        TransactionEventDTO event = new TransactionEventDTO(
                "CREDIT",
                customer.getAccountNumber(),
                dto.getAmount(),
                LocalDateTime.now().toString()
        );
        kafkaProducer.sendTransactionEvent(event);

        return "Amount credited successfully";
    }

    /**
     * ✅ DEBIT money from the logged-in customer's account
     */
    @Transactional
    public String debit(String username, DebitRequestDTO dto) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (dto.getAmount() <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }


        validateAccountStatus(customer); // 🚨 check before proceeding

        if (customer.getBalance() < dto.getAmount()) {
            throw new RuntimeException("Insufficient balance");
        }

        // Deduct balance
        customer.setBalance(customer.getBalance() - dto.getAmount());
        customerRepository.save(customer);

        // Record transaction
        Transaction transaction = new Transaction();
        transaction.setCustomer(customer);
        transaction.setDebit(dto.getAmount());
        transaction.setType("DEBIT");
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);

        // Kafka event
        TransactionEventDTO event = new TransactionEventDTO(
                "DEBIT",
                customer.getAccountNumber(),
                dto.getAmount(),
                LocalDateTime.now().toString()
        );
        kafkaProducer.sendTransactionEvent(event);

        return "Amount debited successfully";
    }

    /**
     * ✅ TRANSFER money from logged-in customer (sender) to another customer (receiver)
     */
    @Transactional
    public String transfer(String senderUsername, TransferRequestDTO dto) {
        // Find sender by JWT username
        Customer fromCustomer = customerRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        if (dto.getAmount() <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }


        // Find receiver by username from request body
        Customer toCustomer = customerRepository.findByUsername(dto.getToUsername())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        if (senderUsername.equals(dto.getToUsername())) {
            throw new RuntimeException("You cannot transfer money to yourself");
        }


        // 🚨 Check both accounts before proceeding
        validateAccountStatus(fromCustomer);
        validateAccountStatus(toCustomer);

        if (fromCustomer.getBalance() < dto.getAmount()) {
            throw new RuntimeException("Insufficient balance");
        }

        // Update balances
        fromCustomer.setBalance(fromCustomer.getBalance() - dto.getAmount());
        toCustomer.setBalance(toCustomer.getBalance() + dto.getAmount());
        customerRepository.save(fromCustomer);
        customerRepository.save(toCustomer);

        // Create DEBIT transaction for sender
        Transaction debitTx = new Transaction();
        debitTx.setCustomer(fromCustomer);
        debitTx.setDebit(dto.getAmount());
        debitTx.setType("TRANSFER_DEBIT");
        debitTx.setTimestamp(LocalDateTime.now());
        transactionRepository.save(debitTx);

        // Create CREDIT transaction for receiver
        Transaction creditTx = new Transaction();
        creditTx.setCustomer(toCustomer);
        creditTx.setCredit(dto.getAmount());
        creditTx.setType("TRANSFER_CREDIT");
        creditTx.setTimestamp(LocalDateTime.now());
        creditTx.setRefTransaction(debitTx); // optional link between transactions
        transactionRepository.save(creditTx);

        // Send Kafka events
        TransactionEventDTO debitEvent = new TransactionEventDTO(
                "TRANSFER_DEBIT",
                fromCustomer.getAccountNumber(),
                dto.getAmount(),
                LocalDateTime.now().toString()
        );
        kafkaProducer.sendTransactionEvent(debitEvent);

        TransactionEventDTO creditEvent = new TransactionEventDTO(
                "TRANSFER_CREDIT",
                toCustomer.getAccountNumber(),
                dto.getAmount(),
                LocalDateTime.now().toString()
        );
        kafkaProducer.sendTransactionEvent(creditEvent);

        return "Transfer successful";
    }

    /**
     * ✅ Get transaction by ID
     */
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    /**
     * ✅ ADMIN – Get all transactions
     */
    public List<TransactionResponseDTO> getAllTransactions() {
        return transactionRepository.findAll()
                .stream()
                .map(tx -> new TransactionResponseDTO(
                        tx.getId(),
                        tx.getType(),
                        tx.getCredit(),
                        tx.getDebit(),
                        tx.getTimestamp(),
                        tx.getCustomer() != null ? tx.getCustomer().getId() : null,
                        tx.getCustomer() != null ? tx.getCustomer().getUsername() : null
                ))
                .toList();
    }

    /**
     * ✅ Delete a transaction by ID (ADMIN only)
     */
    @Transactional
    public void deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new RuntimeException("Transaction not found");
        }
        transactionRepository.deleteById(id);
    }

    /**
     * ✅ Get transactions for a specific username (CUSTOMER)
     */
    public List<Transaction> getTransactionsByUsername(String username) {
        return transactionRepository.findByCustomer_Username(username);
    }

}
