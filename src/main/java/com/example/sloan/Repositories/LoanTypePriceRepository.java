package com.example.sloan.Repositories;

import com.example.sloan.models.LoanType;
import com.example.sloan.models.LoanTypePrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanTypePriceRepository extends JpaRepository<LoanTypePrice, Long> {
    LoanTypePrice findByLoanType(LoanType loanType);
}
