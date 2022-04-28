package com.example.sloan.controllers;

import com.example.sloan.dtos.UpdateLoanPriceDto;
import com.example.sloan.models.LoanType;
import com.example.sloan.models.LoanTypePrice;
import com.example.sloan.services.LoanTypePriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("loan-rate")
public class LoanTypePriceController {
    @Autowired
    LoanTypePriceService loanTypePriceService;

    @PostMapping()
    public ResponseEntity<LoanTypePrice> addRate(@RequestBody LoanTypePrice loanTypePrice){
        return ResponseEntity.ok(loanTypePriceService.addPrice(loanTypePrice));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanTypePrice> getLoanRateById(@PathVariable Long id){
        return ResponseEntity.ok(loanTypePriceService.getLoanPriceById(id));
    }

    @GetMapping("/type/{loanType}")
    public ResponseEntity<LoanTypePrice> getLoanRateByLoanType(@PathVariable LoanType loanType){
        return ResponseEntity.ok(loanTypePriceService.getLoanPriceByLoanType(loanType));
    }

    @GetMapping("/all")
    public ResponseEntity<List<LoanTypePrice>> getAll(){
        return ResponseEntity.ok(loanTypePriceService.getAll());
    }

    @PatchMapping("/update")
    public ResponseEntity<LoanTypePrice> updateRate(@RequestBody UpdateLoanPriceDto update){
        return ResponseEntity.ok(loanTypePriceService.updateLoanPrice(update));
    }
}
