package com.example.sloan.Repositories;

import com.example.sloan.models.AccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<AccountTransaction, Long> {
//    AccountTransaction findByTRef(String tRef);
    List<AccountTransaction> findAllByAccountId(Long accountId);
}
