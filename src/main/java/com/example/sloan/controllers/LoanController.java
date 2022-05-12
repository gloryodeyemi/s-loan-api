package com.example.sloan.controllers;

import com.example.sloan.dtos.LoanDto;
import com.example.sloan.dtos.RepayDto;
import com.example.sloan.dtos.TransactionDto;
import com.example.sloan.exceptions.ErrorException;
import com.example.sloan.models.AccountTransaction;
import com.example.sloan.models.Loan;
import com.example.sloan.services.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("loan")
public class LoanController {
    @Autowired
    LoanService loanService;

    @PostMapping()
    public ResponseEntity<Loan> saveLoan(@RequestBody LoanDto loanDto) throws ErrorException {
        return ResponseEntity.ok(loanService.saveLoan(loanDto));
    }

    @PostMapping("/repay")
    public ResponseEntity<Loan> repayLoan(@RequestBody RepayDto repayDto) throws ErrorException {
        return ResponseEntity.ok(loanService.repayLoan(repayDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Loan> getById(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.findById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Loan>> getAll() {
        return ResponseEntity.ok(loanService.findAll());
    }
}
