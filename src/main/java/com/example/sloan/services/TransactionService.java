package com.example.sloan.services;

import com.example.sloan.Repositories.AccountRepository;
import com.example.sloan.Repositories.TransactionRepository;
import com.example.sloan.dtos.TransactionDto;
import com.example.sloan.exceptions.ErrorException;
import com.example.sloan.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@Slf4j
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

    public AccountTransaction saveTransaction(TransactionDto transactionDto) throws ErrorException{
        log.info("transactionDto::{}", transactionDto);
        Account account = accountService.accountValidationByNumber(transactionDto.getAccountNo());
        AccountTransaction accountTransaction = new AccountTransaction();
        BeanUtils.copyProperties(transactionDto, accountTransaction);
        Channel tChannel = accountTransaction.getChannel();
        accountTransaction.setTRef(generateRef(tChannel));
        accountTransaction.setAccountId(account.getId());

        if (tChannel.equals(Channel.SAVE)){
            account.setSavingsBalance(account.getSavingsBalance() + accountTransaction.getAmount());
            accountTransaction.setTStatus(TStatus.SUCCESSFUL);
            accountTransaction.setTType(TType.CREDIT);
            accountTransaction.setNarration("Account deposited - SAVE");
        } else if (tChannel.equals(Channel.LOAN)){
            accountTransaction.setTType(TType.CREDIT);

            if (transactionDto.getTStatus().equals(TStatus.FAILED)){
                accountTransaction.setNarration("Account deposit failed - LOAN");
                accountTransaction.setSavingsBal(account.getSavingsBalance());
                accountTransaction.setLoanBal(account.getLoanBalance());
                transactionRepository.save(accountTransaction);
                throw new ErrorException(transactionDto.getMessage());
            }
            account.setLoanBalance(account.getLoanBalance() - accountTransaction.getAmount());
            accountTransaction.setNarration("Account deposited - LOAN");
        } else if (tChannel.equals(Channel.WITHDRAW)){
            if (account.getSavingsBalance() < accountTransaction.getAmount()){
                accountTransaction.setTStatus(TStatus.FAILED);
                accountTransaction.setTType(TType.DEBIT);
                accountTransaction.setNarration("Insufficient balance");
                accountTransaction.setSavingsBal(account.getSavingsBalance());
                accountTransaction.setLoanBal(account.getLoanBalance());
                transactionRepository.save(accountTransaction);
                throw new ErrorException("Insufficient balance!");
            }
            account.setSavingsBalance(account.getSavingsBalance() - accountTransaction.getAmount());
            accountTransaction.setTStatus(TStatus.SUCCESSFUL);
            accountTransaction.setTType(TType.DEBIT);
            accountTransaction.setNarration("Withdrawal successful");
        } else {
            throw new ErrorException("Invalid channel");
        }
        Account savedAccount = accountRepository.save(account);
        accountTransaction.setSavingsBal(savedAccount.getSavingsBalance());
        accountTransaction.setLoanBal(savedAccount.getLoanBalance());
        return transactionRepository.save(accountTransaction);
    }

    public AccountTransaction repayLoanTransaction(TransactionDto transactionDto) throws ErrorException{
        Account account = accountService.accountValidationByNumber(transactionDto.getAccountNo());
        AccountTransaction accountTransaction = new AccountTransaction();
        BeanUtils.copyProperties(transactionDto, accountTransaction);
        accountTransaction.setAccountId(account.getId());
        accountTransaction.setAmount(transactionDto.getLoanToRepay());
        accountTransaction.setTRef(generateRef(Channel.REPAY));
        if (account.getSavingsBalance() < transactionDto.getLoanToRepay()){
            accountTransaction.setTStatus(TStatus.FAILED);
            accountTransaction.setTType(TType.DEBIT);
            accountTransaction.setNarration("Loan repay failed");
            accountTransaction.setSavingsBal(account.getSavingsBalance());
            accountTransaction.setLoanBal(account.getLoanBalance());
            transactionRepository.save(accountTransaction);
            throw new ErrorException("Loan repay failed due to insufficient balance. Please, fund your savings account.");
        }
        account.setSavingsBalance(account.getSavingsBalance() - transactionDto.getLoanToRepay());
        account.setLoanBalance(account.getLoanBalance() + transactionDto.getLoanToRepay());
        Account savedAccount = accountRepository.save(account);
        accountTransaction.setTStatus(TStatus.SUCCESSFUL);
        accountTransaction.setTType(TType.DEBIT);
        accountTransaction.setNarration("Loan repaid successfully");
        accountTransaction.setSavingsBal(savedAccount.getSavingsBalance());
        accountTransaction.setLoanBal(savedAccount.getLoanBalance());
        return transactionRepository.save(accountTransaction);
    }

    public String generateRef(Channel channel){
        Random randN = new Random( System.currentTimeMillis() );
        int randomNumber = (1 + randN.nextInt(2)) * 10000 + randN.nextInt(10000);
        return "Ref-" + channel.name() + "-" + randomNumber;
    }

    public List<AccountTransaction> findAllTransactionsByAccountId(Long accountId){
        return transactionRepository.findAllByAccountId(accountId);
    }
}
