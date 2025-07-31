package com.bank.customer_service.service;

import com.bank.customer_service.dto.*;
import com.bank.customer_service.entity.Customer;
import com.bank.customer_service.repository.CustomerRepository;
import com.bank.customer_service.security.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Override
    public CustomerResponseDTO register(CustomerRegisterDTO dto) {
        Customer customer = new Customer();
        customer.setUsername(dto.getUsername());
        customer.setEmail(dto.getEmail());
        customer.setPassword(passwordEncoder.encode(dto.getPassword()));

        Customer saved = customerRepository.save(customer);

        return mapToDTO(saved);
    }

    @Override
    public String login(CustomerLoginDTO dto) {
        Optional<Customer> customerOpt = customerRepository.findByUsername(dto.getUsername());

        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            if (passwordEncoder.matches(dto.getPassword(), customer.getPassword())) {
                return jwtService.generateToken(customer.getUsername());
            }
        }
        throw new RuntimeException("Invalid username or password");
    }

    @Override
    public CustomerResponseDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return mapToDTO(customer);
    }

    @Override
    public List<CustomerResponseDTO> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponseDTO createCustomer(Customer customer) {
        // Optional: encode password if it's not null
        if (customer.getPassword() != null) {
            customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        }
        Customer saved = customerRepository.save(customer);
        return mapToDTO(saved);
    }

    @Override
    public CustomerResponseDTO updateCustomer(Long id, Customer updatedCustomer) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        existing.setUsername(updatedCustomer.getUsername());
        existing.setEmail(updatedCustomer.getEmail());
        existing.setPassword(passwordEncoder.encode(updatedCustomer.getPassword()));


        Customer saved = customerRepository.save(existing);
        return mapToDTO(saved);
    }

    @Override
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer not found");
        }
        customerRepository.deleteById(id);
    }

    @Override
    public CustomerResponseDTO updateMyProfile(String username, Customer updatedCustomer) {
        Customer existing = customerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Allow updating only safe fields (NOT username or role)
        existing.setEmail(updatedCustomer.getEmail());

        // Update password only if provided
        if (updatedCustomer.getPassword() != null && !updatedCustomer.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(updatedCustomer.getPassword()));
        }

        Customer saved = customerRepository.save(existing);
        return mapToDTO(saved);
    }

    @Override
    public CustomerResponseDTO getMyProfile(String username) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return mapToDTO(customer);
    }

    @Override
    public Customer getCustomerByUsername(String username) {
        return customerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    public void changeEmail(String username, String newEmail) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customer.setEmail(newEmail);
        customerRepository.save(customer);
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (!passwordEncoder.matches(oldPassword, customer.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        customer.setPassword(passwordEncoder.encode(newPassword));
        customerRepository.save(customer);
    }






    private CustomerResponseDTO mapToDTO(Customer customer) {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setAadhaar(customer.getAadhaar());
        dto.setPan(customer.getPan());
        dto.setGender(customer.getGender());
        dto.setUsername(customer.getUsername());
        dto.setAccountNumber(customer.getAccountNumber());
        dto.setBalance(customer.getBalance());
        dto.setBranchId(customer.getBranchId());
        return dto;
    }

}
