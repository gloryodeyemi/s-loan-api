package com.example.sloan.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TransactionStatement {
    @JsonIgnoreProperties({"id", "emailAddress", "phoneNumber", "address", "city", "country", "pin", "password", "dateCreated", "dateUpdated"})
    private Account account;
    private Double totalCredit;
    private Double totalDebit;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fromDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate toDate;

    @JsonIgnoreProperties({"id", "accountId", "description", "dateUpdated"})
    private List<AccountTransaction> transactionList;

}
