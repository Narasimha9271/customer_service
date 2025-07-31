package com.bank.customer_service.service;

import com.bank.customer_service.dto.AccountDTO;
import com.bank.customer_service.entity.Account;
import com.bank.customer_service.repository.AccountRepository;
import com.bank.customer_service.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public List<AccountDTO> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AccountDTO getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return convertToDTO(account);
    }

    public AccountDTO createAccount(AccountDTO accountDTO) {
        Account account = convertToEntity(accountDTO);
        return convertToDTO(accountRepository.save(account));
    }

    public AccountDTO updateAccount(Long id, AccountDTO updated) {
        Account acc = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        acc.setType(updated.getType());
        acc.setMinBalance(updated.getMinBalance());
        acc.setAccountNumber(updated.getAccountNumber());
        acc.setStatus(updated.getStatus()); // ✅ Include status

        return convertToDTO(accountRepository.save(acc));
    }

    public void deleteAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setStatus("CLOSED");
        accountRepository.save(account);
    }

    private Account convertToEntity(AccountDTO dto) {
        Account account = new Account();
        account.setType(dto.getType());
        account.setMinBalance(dto.getMinBalance());
        account.setAccountNumber(dto.getAccountNumber());
        account.setStatus(dto.getStatus()); // ✅ Include status
        return account;
    }

    private AccountDTO convertToDTO(Account account) {
        return new AccountDTO(
                account.getId(),
                account.getAccountNumber(),
                account.getType(),
                account.getMinBalance(),
                account.getStatus()
        );
    }

    public Optional<Account> getByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    public AccountDTO getMyAccount(String username) {
        var customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return convertToDTO(customer.getAccountType());
    }
}
