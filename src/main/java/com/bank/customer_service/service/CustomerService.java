package com.bank.customer_service.service;

import com.bank.customer_service.dto.*;
import com.bank.customer_service.entity.Customer;

import java.util.List;

public interface CustomerService {
    CustomerResponseDTO register(CustomerRegisterDTO dto);
    String login(CustomerLoginDTO dto);

    CustomerResponseDTO getCustomerById(Long id);
    List<CustomerResponseDTO> getAllCustomers();
    CustomerResponseDTO createCustomer(Customer customer);
    CustomerResponseDTO updateCustomer(Long id, Customer customer);
    void deleteCustomer(Long id);
    CustomerResponseDTO updateMyProfile(String username, Customer updatedCustomer);
    CustomerResponseDTO getMyProfile(String username);
    Customer getCustomerByUsername(String username);
    void changeEmail(String username, String newEmail);
    void changePassword(String username, String oldPassword, String newPassword);
}
