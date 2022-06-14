package com.example.sloan.dtos;

import com.example.sloan.models.LoanType;
import lombok.Data;

@Data
public class UpdateLoanPriceDto {
    private Long id;
    private LoanType loanType;
    private Double interestRate;
    private int noOfDays;
    private Double minAmount;
    private Double maxAmount;
}
