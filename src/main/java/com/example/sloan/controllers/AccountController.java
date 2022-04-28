package com.example.sloan.controllers;

import com.example.sloan.exceptions.ErrorException;
import com.example.sloan.models.Account;
import com.example.sloan.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("account")
public class AccountController {
    @Autowired
    AccountService accountService;

    @PostMapping()
    public ResponseEntity<Account> createAccount(@RequestBody Account account) throws ErrorException {
        return ResponseEntity.ok(accountService.createAccount(account));
    }
}
