package com.example.sloan.dtos;

import com.example.sloan.models.Channel;
import lombok.Data;

@Data
public class TransactionDto {
    private Long accountId;
    private Channel channel;
    private Double amount;
    private String description;
}
