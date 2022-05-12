package com.example.sloan.services;

import com.example.sloan.Repositories.AccountRepository;
import com.example.sloan.Repositories.LoanRepository;
import com.example.sloan.dtos.LoanDto;
import com.example.sloan.dtos.RepayDto;
import com.example.sloan.dtos.TransactionDto;
import com.example.sloan.exceptions.ErrorException;
import com.example.sloan.models.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@Service
public class LoanService {
    @Autowired
    LoanRepository loanRepository;

    @Autowired
    TransactionService transactionService;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    LoanTypePriceService loanTypePriceService;

    public Loan findById(Long id){
        Loan loan = loanRepository.findById(id).orElse(null);
        if (loan == null){
            return null;
        }
        LocalDateTime repayDate =  LocalDateTime.now();
        Long daysDiff = ChronoUnit.DAYS.between(loan.getExpectedRepayDate().toLocalDate(), repayDate.toLocalDate());
        if (daysDiff > 0) {
            double overdueInterest = (0.1 * loan.getAmount() * daysDiff)/100;
            loan.setOverdueInterest(overdueInterest);
            loan.setTotalInterest(loan.getTotalInterest() + overdueInterest);
            loan.setTotalAmount(loan.getTotalAmount() + overdueInterest);
            loan.setAmountLeftToPay(loan.getAmountLeftToPay() + overdueInterest);
            return loanRepository.save(loan);
//            BeanUtils.copyProperties(loanRepository.save(loan), updatedLoan);
        }
        return loan;
//        return loanRepository.findById(id).orElse(null);
    }

    public List<Loan> findAll(){
        return loanRepository.findAll();
    }

    public Loan saveLoan(LoanDto loanDto) throws ErrorException {
        // get the accounts
        Account userAccount = accountService.accountValidationByNumber(loanDto.getAccountNumber());
        Account companyAccount = accountService.findById(1L);
        // create a new loan object
        Loan loan = new Loan();
        BeanUtils.copyProperties(loanDto, loan);
        loan.setChannel(Channel.LOAN);
        loan.setAccountId(userAccount.getId());
        // generate loan reference
        Random randN = new Random( System.currentTimeMillis() );
        int randomNumber = (1 + randN.nextInt(2)) * 10000 + randN.nextInt(10000);
        String loanRef = "Ref-" + userAccount.getFirstName().toLowerCase(Locale.ROOT) + "-" + userAccount.getLastName().toLowerCase(Locale.ROOT) + "-" + randomNumber;
        loan.setLoanRef(loanRef);
        // set loan type
        LoanTypePrice loanTypePrice = loanTypePriceService.getLoanPriceByLoanType(loanDto.getLoanType());
        loan.setLoanTypePrice(loanTypePrice);
        // calculate interest
        Double interest = (loanTypePrice.getInterestRate() * loan.getAmount())/100;
        loan.setInterest(interest);
        loan.setTotalInterest(interest + loan.getOverdueInterest());
        loan.setTotalAmount(loanDto.getAmount() + loan.getTotalInterest());
        loan.setAmountLeftToPay(loan.getTotalAmount());
        // set expected return date
        loan.setDateBorrowed(LocalDateTime.now());
        LocalDateTime date = ChronoUnit.DAYS.addTo(loan.getDateBorrowed().toLocalDate(), loanTypePrice.getNoOfDays()).atStartOfDay();
        loan.setExpectedRepayDate(date);
        loan.setTType(TType.CREDIT);
        loan.setLoanStatus(LStatus.PENDING);
        // check if customer is eligible to borrow amount
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAccountNo(loanDto.getAccountNumber());
        if (loan.getAmount() > ((userAccount.getSavingsBalance() * 2) + userAccount.getLoanBalance())){
            // if not eligible
            loan.setTStatus(TStatus.FAILED);
            loan.setNarration("Ineligible to borrow amount");
            loanRepository.save(loan);
            BeanUtils.copyProperties(loan, transactionDto);
            transactionDto.setMessage("You are not eligible to borrow that amount, please try with a lesser amount.");
            transactionService.saveTransaction(transactionDto);
        }
        // if eligible
        Double companyBal = companyAccount.getSavingsBalance();
        if (companyBal < loanDto.getAmount()) {
            loan.setTStatus(TStatus.FAILED);
            loan.setNarration("Operation failed");
            loanRepository.save(loan);
            BeanUtils.copyProperties(loan, transactionDto);
            transactionDto.setMessage("Something went wrong!");
            transactionService.saveTransaction(transactionDto);
        }
//        companyAccount.setSavingsBalance(companyBal - loanDto.getAmount());
//        accountRepository.save(companyAccount);
        loan.setTStatus(TStatus.SUCCESSFUL);
        loan.setNarration("Loan successful");
        BeanUtils.copyProperties(loan, transactionDto);
        transactionService.saveTransaction(transactionDto);
        transactionDto.setChannel(Channel.WITHDRAW);
        transactionDto.setAccountNo(7665125013L);
        transactionService.saveTransaction(transactionDto);
        return loanRepository.save(loan);
    }

    public Loan repayLoan(RepayDto repayDto) throws ErrorException{
        Loan loan = findById(repayDto.getLoanId());
        Account userAccount = accountService.accountValidationById(loan.getAccountId());
//        Loan updatedLoan = new Loan();
//        BeanUtils.copyProperties(loan, updatedLoan);
        LocalDateTime repayDate =  LocalDateTime.now();
//        Long daysDiff = ChronoUnit.DAYS.between(loan.getExpectedRepayDate().toLocalDate(), repayDate.toLocalDate());
//        if (daysDiff > 0) {
//            double overdueInterest = (0.1 * loan.getAmount() * daysDiff)/100;
//            loan.setOverdueInterest(overdueInterest);
//            loan.setTotalInterest(loan.getTotalInterest() + overdueInterest);
//            loan.setTotalAmount(loan.getTotalAmount() + overdueInterest);
//            loan.setAmountLeftToPay(loan.getAmountLeftToPay() + overdueInterest);
//            BeanUtils.copyProperties(loanRepository.save(loan), updatedLoan);
//        }
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setLoanToRepay(repayDto.getLoanToRepay());
        transactionDto.setAmount(repayDto.getAmountToSave());
        transactionDto.setDescription(repayDto.getDescription());
        transactionDto.setAccountNo(userAccount.getAccountNumber());
        if (repayDto.getAmountToSave() > 0) {
            transactionDto.setChannel(Channel.SAVE);
            transactionService.saveTransaction(transactionDto);
        }
        System.out.println(userAccount.getSavingsBalance());
        transactionDto.setChannel(Channel.REPAY);
        transactionService.repayLoanTransaction(transactionDto);
        loan.setRepayDate(repayDate);
        loan.setLoanStatus(LStatus.REPAID);
        if (repayDto.getLoanToRepay() < loan.getAmountLeftToPay()) {
            loan.setLoanStatus(LStatus.PARTIAL_PAYMENT);
        }
        loan.setAmountLeftToPay(loan.getAmountLeftToPay() - repayDto.getLoanToRepay());
        loan.setAmountPaid(loan.getAmountPaid() + repayDto.getLoanToRepay());
        return loanRepository.save(loan);
    }
}
