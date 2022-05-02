package com.example.sloan.services;

import com.example.sloan.Repositories.AccountRepository;
import com.example.sloan.Repositories.TransactionRepository;
import com.example.sloan.exceptions.ErrorException;
import com.example.sloan.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Random;

@Service
public class TransactionService {
    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accountService;

    public AccountTransaction findById(Long id){
        return transactionRepository.findById(id).orElse(null);
    }

    public List<AccountTransaction> findAll(){
        return transactionRepository.findAll();
    }

//    public AccountTransaction findByRef(String tRef){
//        return transactionRepository.findByTRef(tRef);
//    }

    public AccountTransaction saveTransaction(AccountTransaction accountTransaction) throws ErrorException{
        Account account = accountService.findById(accountTransaction.getAccountId());
        if (account == null){
            throw new ErrorException("Account not found!");
        }
        Random randN = new Random( System.currentTimeMillis() );
        int randomNumber = (1 + randN.nextInt(2)) * 10000 + randN.nextInt(10000);
        String tRef = "Ref-" + accountTransaction.getTChannel().name().toLowerCase(Locale.ROOT) + "-" + randomNumber;

        if (accountTransaction.getTChannel().equals(TChannel.SAVE)){
            account.setBalance(account.getBalance() + accountTransaction.getAmount());
            accountTransaction.setTStatus(TStatus.SUCCESSFUL);
            accountTransaction.setTType(TType.CREDIT);
        }
        if (accountTransaction.getTChannel().equals(TChannel.WITHDRAW)){
            if (account.getBalance() < accountTransaction.getAmount()){
                accountTransaction.setTRef(tRef);
                accountTransaction.setTStatus(TStatus.FAILED);
                accountTransaction.setTType(TType.DEBIT);
                transactionRepository.save(accountTransaction);
                throw new ErrorException("Insufficient Balance!");
            }
            account.setBalance(account.getBalance() - accountTransaction.getAmount());
            accountTransaction.setTStatus(TStatus.SUCCESSFUL);
            accountTransaction.setTType(TType.DEBIT);
        }
        accountRepository.save(account);
        accountTransaction.setTRef(tRef);
        return transactionRepository.save(accountTransaction);
    }
}
