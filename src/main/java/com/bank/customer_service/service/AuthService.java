package com.bank.customer_service.service;

import com.bank.customer_service.dto.*;
import com.bank.customer_service.entity.*;
import com.bank.customer_service.repository.*;
import com.bank.customer_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomerRepository customerRepo;
    private final AccountRepository accountRepo;
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String register(RegisterRequest req) {
        if (customerRepo.existsByUsername(req.getUsername()))
            throw new RuntimeException("Username already exists");

        if (customerRepo.existsByEmail(req.getEmail()))
            throw new RuntimeException("Email already registered");

        Account accountType = accountRepo.findByType(req.getAccountType())
                .orElseThrow(() -> new RuntimeException("Invalid account type"));

        Customer customer = new Customer();
        customer.setName(req.getName());
        customer.setEmail(req.getEmail());
        customer.setAadhaar(req.getAadhaar());
        customer.setDob(LocalDate.parse(req.getDob()));
        customer.setPan(req.getPan());
        customer.setGender(req.getGender());
        customer.setUsername(req.getUsername());
        customer.setPassword(passwordEncoder.encode(req.getPassword()));
        customer.setBranchId(req.getBranchId());
        customer.setAccountNumber(req.getAccountNumber());
        customer.setBalance(req.getBalance());
        customer.setAccountType(accountType);

        customerRepo.save(customer);

        return "Registration successful!";
    }

    public JwtResponse login(LoginRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        String token = jwtService.generateToken(req.getUsername());
        return new JwtResponse(token, req.getUsername());
    }
}
