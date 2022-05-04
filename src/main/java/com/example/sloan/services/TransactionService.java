package com.example.sloan.services;

import com.example.sloan.Repositories.AccountRepository;
import com.example.sloan.Repositories.TransactionRepository;
import com.example.sloan.dtos.TransactionDto;
import com.example.sloan.exceptions.ErrorException;
import com.example.sloan.models.*;
import org.springframework.beans.BeanUtils;
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

    public AccountTransaction saveTransaction(TransactionDto transactionDto) throws ErrorException{
        Account account = accountService.findById(transactionDto.getAccountId());
        if (account == null){
            throw new ErrorException("Account not found!");
        }
        AccountTransaction accountTransaction = new AccountTransaction();
//        accountTransaction.setAccountId(transactionDto.getAccountId());
////        TChannel tChannel = TChannel.valueOf(transactionDto.getTChannel());
//        accountTransaction.setTChannel(transactionDto.getTChannel());
//        accountTransaction.setAmount(transactionDto.getAmount());
//        accountTransaction.setDescription(transactionDto.getDescription());
        BeanUtils.copyProperties(transactionDto, accountTransaction);
        TChannel tChannel = accountTransaction.getTChannel();

        Random randN = new Random( System.currentTimeMillis() );
        int randomNumber = (1 + randN.nextInt(2)) * 10000 + randN.nextInt(10000);
        System.out.println("random number = " + randomNumber);
        String tRef = "Ref-" + tChannel.name() + "-" + randomNumber;
        System.out.println("transaction reference = " + tRef);
        accountTransaction.setTRef(tRef);
//        System.out.println(accountTransaction.getTChannel());

        if (tChannel.equals(TChannel.SAVE)){
            account.setBalance(account.getBalance() + accountTransaction.getAmount());
            accountTransaction.setTStatus(TStatus.SUCCESSFUL);
            accountTransaction.setTType(TType.CREDIT);
        }
        if (tChannel.equals(TChannel.WITHDRAW)){
            if (account.getBalance() < accountTransaction.getAmount()){
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
        return transactionRepository.save(accountTransaction);
    }
}
