package com.example.sloan.services;

import com.example.sloan.Repositories.AccountRepository;
import com.example.sloan.exceptions.ErrorException;
import com.example.sloan.models.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Account createAccount (Account account) throws ErrorException {
        if (accountRepository.existsByEmailAddress(account.getEmailAddress())){
            throw new ErrorException("Email exists!");
        }
        if (accountRepository.existsByPhoneNumber(account.getPhoneNumber())){
            throw new ErrorException("Phone number exists!");
        }
        long number = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
        account.setAccountNumber(number);
        String password = passwordEncoder.encode(account.getPassword());
        account.setPassword(password);
        return  accountRepository.save(account);
    }
}
