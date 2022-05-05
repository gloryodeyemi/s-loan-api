package com.example.sloan.dtos;

import com.example.sloan.models.Channel;
import com.example.sloan.models.TStatus;
import lombok.Data;

@Data
public class TransactionDto {
    private Long accountId;
    private Channel channel;
    private Double amount;
    private TStatus tStatus;
    private String message;
    private String description;
}
