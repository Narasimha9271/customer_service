package com.bank.customer_service.service;

import com.bank.customer_service.dto.AccountDTO;
import com.bank.customer_service.entity.Account;
import com.bank.customer_service.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public Account createAccount(AccountDTO accountDTO) {
        Account account = convertToEntity(accountDTO);
        return accountRepository.save(account);
    }


    public Account updateAccount(Long id, AccountDTO updated) {
        Account acc = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        acc.setType(updated.getType());
        acc.setMinBalance(updated.getMinBalance());
        acc.setAccountNumber(updated.getAccountNumber());

        return accountRepository.save(acc);
    }

    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }
    private Account convertToEntity(AccountDTO dto) {
        Account account = new Account();
        account.setType(dto.getType());
        account.setMinBalance(dto.getMinBalance());
        account.setAccountNumber(dto.getAccountNumber());
        return account;
    }

    public Optional<Account> getByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

}
