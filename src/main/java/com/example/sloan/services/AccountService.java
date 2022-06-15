package com.example.sloan.services;

import com.example.sloan.Repositories.AccountRepository;
import com.example.sloan.exceptions.AccountException;
import com.example.sloan.models.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Account createAccount (Account account) throws AccountException {
        if (accountRepository.existsByEmailAddress(account.getEmailAddress())){
            throw new AccountException("Email exists!");
        }
        if (accountRepository.existsByPhoneNumber(account.getPhoneNumber())){
            throw new AccountException("Phone number exists!");
        }
        long number = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
        account.setAccountNumber(number);
        String password = passwordEncoder.encode(account.getPassword());
        account.setPassword(password);
        return  accountRepository.save(account);
    }

    public Account findById(Long id){
        return accountRepository.findById(id).orElse(null);
    }

    public List<Account> findAll(){
        return accountRepository.findAll();
    }

    public Account findByEmailAddress(String emailAddress) {
        return accountRepository.findByEmailAddress(emailAddress);
    }

    public Account findByAccountNumber(Long accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    public Account accountValidationById(Long accountId) throws AccountException {
        Account account = findById(accountId);
        if (account == null){
            throw new AccountException("Account error-Account not found!");
        }
        return account;
    }

    public Account accountValidationByNumber(Long accountNumber) throws AccountException {
        Account account = findByAccountNumber(accountNumber);
        if (account == null){
            throw new AccountException("Account error-Account not found!");
        }
        return account;
    }

    /*
    public Account updateAccountDetails(Account account) {
        Account accountToUpdate = findById(account.getId());
        BeanUtils.copyProperties(account, accountToUpdate);
        accountToUpdate.setAccountNumber(accountToUpdate.getAccountNumber());
        return accountRepository.save(accountToUpdate);
    }
     */
}
