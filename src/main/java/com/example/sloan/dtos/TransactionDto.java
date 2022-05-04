package com.example.sloan.dtos;

import com.example.sloan.models.TChannel;
import lombok.Data;

@Data
public class TransactionDto {
    private Long accountId;
    private TChannel tChannel;
    private Double amount;
    private String description;
}
