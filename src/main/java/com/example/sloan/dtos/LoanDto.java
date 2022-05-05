package com.example.sloan.dtos;

import com.example.sloan.models.Channel;
import com.example.sloan.models.LoanType;
import com.example.sloan.models.TStatus;
import lombok.Data;

@Data
public class LoanDto {
    private Long accountId;
    private Channel channel;
    private Double amount;
    private LoanType loanType;
    private String description;
}
