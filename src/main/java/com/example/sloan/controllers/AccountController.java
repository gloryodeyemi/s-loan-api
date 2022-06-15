package com.example.sloan.controllers;

import com.example.sloan.exceptions.AccountException;
import com.example.sloan.models.Account;
import com.example.sloan.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("account")
public class AccountController {
    @Autowired
    AccountService accountService;

    @PostMapping()
    public ResponseEntity<Account> createAccount(@RequestBody Account account) throws AccountException {
        return ResponseEntity.ok(accountService.createAccount(account));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.findById(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Account> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(accountService.findByEmailAddress(email));
    }

    @GetMapping("/number/{acNumber}")
    public ResponseEntity<Account> getByAcNumber(@PathVariable Long acNumber) {
        return ResponseEntity.ok(accountService.findByAccountNumber(acNumber));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Account>> getAll() {
        return ResponseEntity.ok(accountService.findAll());
    }

    /*
    @PatchMapping("/update")
    public ResponseEntity<Account> update(@RequestBody Account account) {
        return ResponseEntity.ok(accountService.updateAccountDetails(account));
    }
    */
}
