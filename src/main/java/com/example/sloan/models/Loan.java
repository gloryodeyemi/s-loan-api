package com.example.sloan.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class Loan {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long accountId;
    private TType tType;
    private TStatus tStatus;
    private Channel channel;
    private Double amount;
    private LoanType loanType;

    @ManyToOne
    @JsonIgnoreProperties({"id", "loanType", "dateCreated", "dateUpdated"})
    private LoanTypePrice loanTypePrice;

    private Double interest;
    private Double overdueInterest = 0.0D;
    private Double totalInterest;
    private String description;
    private String narration;
    private String loanRef;
    private LStatus loanStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(updatable = false)
    private LocalDateTime dateBorrowed;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(updatable = false)
    private LocalDateTime expectedRepayDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(updatable = false)
    private LocalDateTime repayDate;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateUpdated;
}
