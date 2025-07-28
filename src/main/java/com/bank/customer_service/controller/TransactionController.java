package com.bank.customer_service.controller;

import com.bank.customer_service.dto.CreditRequestDTO;
import com.bank.customer_service.dto.DebitRequestDTO;
import com.bank.customer_service.dto.TransactionResponseDTO;
import com.bank.customer_service.dto.TransferRequestDTO;
import com.bank.customer_service.entity.Transaction;
import com.bank.customer_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/credit")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> credit(@RequestBody CreditRequestDTO dto) {
        return ResponseEntity.ok(transactionService.credit(dto));
    }

    @PostMapping("/debit")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> debit(@RequestBody DebitRequestDTO dto) {
        return ResponseEntity.ok(transactionService.debit(dto));
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> transfer(@RequestBody TransferRequestDTO dto) {
        return ResponseEntity.ok(transactionService.transfer(dto));
    }

    // ✅ ADMIN can see all transactions
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }


    // ✅ Customers see only their transactions, ADMIN can see anyone’s
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Long id, Authentication authentication) {
        Transaction tx = transactionService.getTransactionById(id);

        String loggedInUser = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !tx.getCustomer().getUsername().equals(loggedInUser)) {
            throw new RuntimeException("Access denied: You can only view your own transactions");
        }

        return ResponseEntity.ok(tx);
    }

    // ✅ Only ADMIN can delete
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok("Transaction deleted successfully");
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<Transaction>> getMyTransactions(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(transactionService.getTransactionsByUsername(username));
    }

}
