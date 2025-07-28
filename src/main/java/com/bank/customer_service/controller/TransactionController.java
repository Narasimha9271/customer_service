package com.bank.customer_service.controller;

import com.bank.customer_service.dto.CreditRequestDTO;
import com.bank.customer_service.dto.DebitRequestDTO;
import com.bank.customer_service.dto.TransferRequestDTO;
import com.bank.customer_service.entity.Transaction;
import com.bank.customer_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/credit")
    public ResponseEntity<String> credit(@RequestBody CreditRequestDTO dto) {
        return ResponseEntity.ok(transactionService.credit(dto));
    }

    @PostMapping("/debit")
    public ResponseEntity<String> debit(@RequestBody DebitRequestDTO dto) {
        return ResponseEntity.ok(transactionService.debit(dto));
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequestDTO dto) {
        return ResponseEntity.ok(transactionService.transfer(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok("Transaction deleted successfully");
    }
}
