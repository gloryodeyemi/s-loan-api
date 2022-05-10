package com.example.sloan.dtos;

import lombok.Data;

@Data
public class RepayDto {
    private Long loanId;
    private Double amount;
    private Double loanToRepay;
    private String description;
}
