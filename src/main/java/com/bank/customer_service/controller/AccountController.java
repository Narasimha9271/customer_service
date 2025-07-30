package com.bank.customer_service.controller;

import com.bank.customer_service.dto.AccountDTO;
import com.bank.customer_service.entity.Account;
import com.bank.customer_service.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")  // ✅ Only ADMIN can get all accounts
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")  // ✅ Only ADMIN can create accounts
    public ResponseEntity<Account> createAccount(@RequestBody AccountDTO dto) {
        return ResponseEntity.ok(accountService.createAccount(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")  // ✅ Only ADMIN can update accounts
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @RequestBody AccountDTO dto) {
        return ResponseEntity.ok(accountService.updateAccount(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")  // ✅ Only ADMIN can soft-delete
    public ResponseEntity<String> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.ok("Account status changed to CLOSED");
    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<Account> getByAccountNumber(@PathVariable String accountNumber) {
        return accountService.getByAccountNumber(accountNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ NEW ENDPOINT for Customers
    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Account> getMyAccount(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(accountService.getMyAccount(username));
    }
}
