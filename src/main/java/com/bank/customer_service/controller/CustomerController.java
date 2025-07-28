package com.bank.customer_service.controller;

import com.bank.customer_service.dto.CustomerResponseDTO;
import com.bank.customer_service.entity.Customer;
import com.bank.customer_service.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    // 🔹 Only ADMIN can view all customers
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    // 🔹 Only ADMIN can view a specific customer by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponseDTO> getCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    // 🔹 Only ADMIN can manually create customers
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponseDTO> createCustomer(@RequestBody Customer customer) {
        return ResponseEntity.ok(customerService.createCustomer(customer));
    }

    // 🔹 Only ADMIN can update any customer
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        return ResponseEntity.ok(customerService.updateCustomer(id, customer));
    }

    // 🔹 Only ADMIN can delete a customer
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Customer deleted successfully.");
    }

    // 🔹 NEW: Customers can edit their own profile
    @PutMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerResponseDTO> updateMyProfile(@RequestBody Customer updatedCustomer, Authentication authentication) {
        String username = authentication.getName(); // Extract logged-in username from JWT
        return ResponseEntity.ok(customerService.updateMyProfile(username, updatedCustomer));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerResponseDTO> getMyProfile(Authentication authentication) {
        String username = authentication.getName();  // Extract logged-in username from JWT
        return ResponseEntity.ok(customerService.getMyProfile(username));
    }

}
