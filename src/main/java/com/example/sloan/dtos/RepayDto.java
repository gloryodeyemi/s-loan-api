package com.example.sloan.dtos;

import lombok.Data;

@Data
public class RepayDto {
    private Long loanId;
    private Double amountToSave;
    private Double loanToRepay;
    private String description;
}
