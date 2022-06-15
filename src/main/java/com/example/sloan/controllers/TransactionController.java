package com.example.sloan.controllers;

import com.example.sloan.dtos.TransactionDto;
import com.example.sloan.exceptions.AccountException;
import com.example.sloan.exceptions.TransactionException;
import com.example.sloan.models.AccountTransaction;
import com.example.sloan.models.TransactionStatement;
import com.example.sloan.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("account-transaction")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping()
    public ResponseEntity<AccountTransaction> saveTransaction(@RequestBody TransactionDto transactionDto) throws TransactionException, AccountException {
        return ResponseEntity.ok(transactionService.saveTransaction(transactionDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountTransaction> getById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.findById(id));
    }

//    @GetMapping("/ref/{tRef}")
//    public ResponseEntity<AccountTransaction> getByTRef(@PathVariable String tRef) {
//        return ResponseEntity.ok(transactionService.findByRef(tRef));
//    }

    @GetMapping("/all")
    public ResponseEntity<List<AccountTransaction>> getAll() {
        return ResponseEntity.ok(transactionService.findAll());
    }

    @GetMapping("/all/{accountId}")
    public ResponseEntity<List<AccountTransaction>> getAllTransactionsByAccountId(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.findAllTransactionsByAccountId(accountId));
    }

    @GetMapping("/statement")
    public ResponseEntity<TransactionStatement> generateStatement(@RequestParam Long accountId, @RequestParam String fromDate, @RequestParam String toDate){
        return ResponseEntity.ok(transactionService.generateStatement(accountId, fromDate, toDate));
    }
}
