package com.bank.customer_service.controller;

import com.bank.customer_service.dto.BalanceDTO;
import com.bank.customer_service.dto.CustomerResponseDTO;
import com.bank.customer_service.entity.Customer;
import com.bank.customer_service.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponseDTO> getCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponseDTO> createCustomer(@RequestBody Customer customer) {
        return ResponseEntity.ok(customerService.createCustomer(customer));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        return ResponseEntity.ok(customerService.updateCustomer(id, customer));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Customer deleted successfully.");
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerResponseDTO> updateMyProfile(@RequestBody Customer updatedCustomer, Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(customerService.updateMyProfile(username, updatedCustomer));
    }

    @PutMapping("/me/change-email")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> changeEmail(
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        String username = authentication.getName();
        customerService.changeEmail(username, request.get("email"));
        return ResponseEntity.ok("Email updated successfully");
    }

    @PutMapping("/me/change-password")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> changePassword(
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        String username = authentication.getName();
        customerService.changePassword(username, request.get("oldPassword"), request.get("newPassword"));
        return ResponseEntity.ok("Password updated successfully");
    }


    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerResponseDTO> getMyProfile(Authentication authentication) {
        String username = authentication.getName();  // Extract logged-in username from JWT
        return ResponseEntity.ok(customerService.getMyProfile(username));
    }

    @GetMapping("/me/balance")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BalanceDTO> getMyBalance(Authentication authentication) {
        String username = authentication.getName();
        Double balance = customerService.getCustomerByUsername(username).getBalance();
        return ResponseEntity.ok(new BalanceDTO(balance));
    }



}
