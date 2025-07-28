package com.bank.customer_service.service;

import com.bank.customer_service.dto.CreditRequestDTO;
import com.bank.customer_service.dto.DebitRequestDTO;
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

    @Transactional
    public String credit(CreditRequestDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customer.setBalance(customer.getBalance() + dto.getAmount());
        customerRepository.save(customer);

        Transaction transaction = new Transaction();
        transaction.setCustomer(customer);
        transaction.setCredit(dto.getAmount());
        transaction.setType("CREDIT");
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);

        TransactionEventDTO event = new TransactionEventDTO("CREDIT", customer.getAccountNumber(), dto.getAmount(), LocalDateTime.now().toString());
        kafkaProducer.sendTransactionEvent(event);

        return "Amount credited successfully";
    }

    @Transactional
    public String debit(DebitRequestDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (customer.getBalance() < dto.getAmount()) {
            throw new RuntimeException("Insufficient balance");
        }

        customer.setBalance(customer.getBalance() - dto.getAmount());
        customerRepository.save(customer);

        Transaction transaction = new Transaction();
        transaction.setCustomer(customer);
        transaction.setDebit(dto.getAmount());
        transaction.setType("DEBIT");
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);

        TransactionEventDTO event = new TransactionEventDTO("DEBIT", customer.getAccountNumber(), dto.getAmount(), LocalDateTime.now().toString());
        kafkaProducer.sendTransactionEvent(event);

        return "Amount debited successfully";
    }

//    @Transactional
//    public String transfer(TransferRequestDTO dto) {
//        Customer fromCustomer = customerRepository.findById(dto.getFromCustomerId())
//                .orElseThrow(() -> new RuntimeException("Sender not found"));
//
//        Customer toCustomer = customerRepository.findById(dto.getToCustomerId())
//                .orElseThrow(() -> new RuntimeException("Receiver not found"));
//
//        if (fromCustomer.getBalance() < dto.getAmount()) {
//            throw new RuntimeException("Insufficient balance");
//        }
//
//        fromCustomer.setBalance(fromCustomer.getBalance() - dto.getAmount());
//        toCustomer.setBalance(toCustomer.getBalance() + dto.getAmount());
//
//        customerRepository.save(fromCustomer);
//        customerRepository.save(toCustomer);
//
//        Transaction debitTx = new Transaction();
//        debitTx.setCustomer(fromCustomer);
//        debitTx.setDebit(dto.getAmount());
//        debitTx.setType("DEBIT");
//        debitTx.setTimestamp(LocalDateTime.now());
//        transactionRepository.save(debitTx);
//
//        Transaction creditTx = new Transaction();
//        creditTx.setCustomer(toCustomer);
//        creditTx.setCredit(dto.getAmount());
//        creditTx.setType("CREDIT");
//        creditTx.setTimestamp(LocalDateTime.now());
//        creditTx.setRefTransaction(debitTx);
//        transactionRepository.save(creditTx);
//
//        TransactionEventDTO event = new TransactionEventDTO("TRANSFER", fromCustomer.getAccountNumber(), dto.getAmount(), LocalDateTime.now().toString());
//        kafkaProducer.sendTransactionEvent(event);
//
//        return "Transfer successful";
//    }

    @Transactional
    public String transfer(TransferRequestDTO dto) {
        Customer fromCustomer = customerRepository.findById(dto.getFromCustomerId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        Customer toCustomer = customerRepository.findById(dto.getToCustomerId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        if (fromCustomer.getBalance() < dto.getAmount()) {
            throw new RuntimeException("Insufficient balance");
        }

        fromCustomer.setBalance(fromCustomer.getBalance() - dto.getAmount());
        toCustomer.setBalance(toCustomer.getBalance() + dto.getAmount());

        customerRepository.save(fromCustomer);
        customerRepository.save(toCustomer);

        Transaction debitTx = new Transaction();
        debitTx.setCustomer(fromCustomer);
        debitTx.setDebit(dto.getAmount());
        debitTx.setType("DEBIT");
        debitTx.setTimestamp(LocalDateTime.now());
        transactionRepository.save(debitTx);

        Transaction creditTx = new Transaction();
        creditTx.setCustomer(toCustomer);
        creditTx.setCredit(dto.getAmount());
        creditTx.setType("CREDIT");
        creditTx.setTimestamp(LocalDateTime.now());
        creditTx.setRefTransaction(debitTx);
        transactionRepository.save(creditTx);

        // 🔴 Send event for DEBIT from sender
        TransactionEventDTO debitEvent = new TransactionEventDTO(
                "TRANSFER_DEBIT",
                fromCustomer.getAccountNumber(),
                dto.getAmount(),
                LocalDateTime.now().toString()
        );
        kafkaProducer.sendTransactionEvent(debitEvent);

        // 🔵 Send event for CREDIT to receiver
        TransactionEventDTO creditEvent = new TransactionEventDTO(
                "TRANSFER_CREDIT",
                toCustomer.getAccountNumber(),
                dto.getAmount(),
                LocalDateTime.now().toString()
        );
        kafkaProducer.sendTransactionEvent(creditEvent);

        return "Transfer successful";
    }


    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Transactional
    public void deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new RuntimeException("Transaction not found");
        }
        transactionRepository.deleteById(id);
    }
}
