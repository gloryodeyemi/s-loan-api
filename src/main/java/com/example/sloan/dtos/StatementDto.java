package com.example.sloan.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StatementDto {
    private Long accountId;
    private LocalDate fromDate;
    private LocalDate toDate;
}
