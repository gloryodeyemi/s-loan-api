package com.example.sloan.startup;

import com.example.sloan.Repositories.AccountRepository;
import com.example.sloan.models.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BaseAccount {
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @EventListener
    public void appReady(ApplicationReadyEvent event) {
        if (!accountRepository.existsByEmailAddress("account@sloan.com")){
            Account account =  new Account();
            account.setFirstName("Sloan");
            account.setLastName("Ltd.");
            account.setEmailAddress("account@sloan.com");
            account.setPhoneNumber("08107402166");
            account.setAddress("59, Sloan street");
            account.setCity("Lagos");
            account.setCountry("Nigeria");
            long number = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
            account.setAccountNumber(number);
            account.setBalance(1000000.00D);
            String password = passwordEncoder.encode("Sloan989!");
            account.setPassword(password);
            account.setPin(1232);
            accountRepository.save(account);
        }
    }
}
