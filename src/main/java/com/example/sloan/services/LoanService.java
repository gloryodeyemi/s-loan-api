package com.example.sloan.services;

import com.example.sloan.Repositories.AccountRepository;
import com.example.sloan.Repositories.LoanRepository;
import com.example.sloan.dtos.LoanDto;
import com.example.sloan.dtos.RepayDto;
import com.example.sloan.dtos.TransactionDto;
import com.example.sloan.exceptions.AccountException;
import com.example.sloan.exceptions.LoanException;
import com.example.sloan.exceptions.TransactionException;
import com.example.sloan.models.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
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

//    private static final DecimalFormat df = new DecimalFormat("0.00");
//    df.setRoundingMode(RoundingMode.DOWN);

    public Loan findById(Long id){
       return loanRepository.findById(id).orElse(null);
    }

//    @Scheduled(cron = "0/20 * * * * ?")
//    public void scheduledTest(){
//    }

    public List<Loan> findAll(){
        return loanRepository.findAll();
    }

    public Loan saveLoan(LoanDto loanDto) throws AccountException, TransactionException {
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
        String loanRef = "Ref-" + userAccount.getFirstName().toLowerCase(Locale.ROOT) + "-" + userAccount.getLastName().toLowerCase(Locale.ROOT) +
                "-" + randomNumber;
        loan.setLoanRef(loanRef);
        // set loan type
        LoanTypePrice loanTypePrice = loanTypePriceService.getLoanPriceByLoanType(loanDto.getLoanType());
        loan.setLoanTypePrice(loanTypePrice);
        // set other entities
        loan.setTotalAmount(loanDto.getAmount() + loan.getTotalInterest());
        loan.setAmountLeftToPay(loan.getTotalAmount());
        // set expected return date
        loan.setDateBorrowed(LocalDateTime.now());
        LocalDateTime date = ChronoUnit.DAYS.addTo(loan.getDateBorrowed().toLocalDate(), loanTypePrice.getNoOfDays()).atStartOfDay();
        loan.setExpectedRepayDate(date);
        loan.setTType(TType.CREDIT);
        loan.setLoanStatus(LStatus.PENDING);
        // check if amount is within loan type limits
        switch (loanDto.getLoanType()){
            case ANNUAL:
                if (loanDto.getAmount() < loanTypePrice.getMinAmount()){
                    failedLoan(loan, userAccount, "Amount limit error", "Amount limit error-Amount is lesser than the minimum amount for this loan type." +
                            " Please enter a bigger amount.");
                }
                break;
            case WEEKLY: case BI_WEEKLY: case MONTHLY: case QUARTERLY: case BI_ANNUAL:
                if (loanDto.getAmount() < loanTypePrice.getMinAmount() || loanDto.getAmount() > loanTypePrice.getMaxAmount()) {
                    failedLoan(loan, userAccount, "Amount limit error", "Amount limit error-Please enter an amount within the limits of this loan type.");
                }
                break;
            default:
                failedLoan(loan, userAccount, "Loan type error", "Loan type error-Please choose a valid loan type");
        }
        // check if customer is eligible to borrow amount
        if (loan.getAmount() > ((userAccount.getSavingsBalance() * 2) + userAccount.getLoanBalance())){
            // if not eligible
            failedLoan(loan, userAccount, "Ineligible to borrow amount", "Amount limit error-You are not eligible to borrow that amount, please try with" +
                    " a lesser amount.");
        }
        // if eligible
        Double companyBal = companyAccount.getSavingsBalance();
        if (companyBal < loanDto.getAmount()) {
            failedLoan(loan, userAccount, "Operation failed", "Operation error-Something went wrong!");
        }
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAccountNo(loanDto.getAccountNumber());
        loan.setTStatus(TStatus.SUCCESSFUL);
        loan.setNarration("Loan successful");
        BeanUtils.copyProperties(loan, transactionDto);
        transactionService.saveTransaction(transactionDto);
        transactionDto.setChannel(Channel.WITHDRAW);
        transactionDto.setAccountNo(7665125013L);
        transactionService.saveTransaction(transactionDto);
        return loanRepository.save(loan);
    }

    public void failedLoan(Loan loan, Account account, String narration, String message) throws AccountException, TransactionException{
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAccountNo(account.getAccountNumber());
        loan.setTStatus(TStatus.FAILED);
        loan.setNarration(narration);
        loanRepository.save(loan);
        BeanUtils.copyProperties(loan, transactionDto);
        transactionDto.setMessage(message);
        transactionService.saveTransaction(transactionDto);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduledLoanInterestCalc(){
        List<Loan> allLoans = findAll();
        for (Loan loan : allLoans) {
            Account userAccount = accountService.findById(loan.getAccountId());
            if ((!loan.getLoanStatus().equals(LStatus.REPAID)) && loan.getTStatus().equals(TStatus.SUCCESSFUL)){
                LoanTypePrice loanType = loan.getLoanTypePrice();
                Double interestPerDay = (loanType.getInterestRate()/100) / loanType.getNoOfDays();
                Double interest = roundedFigure(interestPerDay * loan.getAmount());
                System.out.println(interest);
                LocalDateTime currentDate =  LocalDateTime.now();
                Long daysDiff = ChronoUnit.DAYS.between(loan.getExpectedRepayDate().toLocalDate(), currentDate.toLocalDate());
                if (daysDiff > 0){
                    loan.setOverdueInterest(roundedFigure(loan.getOverdueInterest() + interest));
                } else {
                    loan.setInterest(roundedFigure(loan.getInterest() + interest));
                }
                loan.setTotalInterest(loan.getInterest() + loan.getOverdueInterest());
                loan.setTotalAmount(loan.getAmount() + loan.getTotalInterest());
                loan.setAmountLeftToPay(loan.getTotalAmount() - loan.getAmountPaid());
                userAccount.setLoanBalance(roundedFigure(userAccount.getLoanBalance() - interest));
                accountRepository.save(userAccount);
                loanRepository.save(loan);
            }
        }
        System.out.println("running scheduled task");
    }

    @Scheduled(cron = "0 0 8 * * ?")
    public void scheduledAutomaticLoanRepayment() {
        List<Loan> allLoans = findAll();
        for (Loan loan : allLoans) {
            if ((!loan.getLoanStatus().equals(LStatus.REPAID)) && loan.getTStatus().equals(TStatus.SUCCESSFUL)) {
                LocalDate todayDate = LocalDate.now();
                if (loan.getExpectedRepayDate().toLocalDate().equals(todayDate)) {
                    repayLoan(getDto(loan));
                }
            }
        }
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void scheduledAutomaticLoanRepaymentThreeTrials() {
        List<Loan> allLoans = findAll();
        for (Loan loan : allLoans) {
            if ((!loan.getLoanStatus().equals(LStatus.REPAID)) && loan.getTStatus().equals(TStatus.SUCCESSFUL)) {
                LocalDate todayDate = LocalDate.now();
                LocalDate repayDate = loan.getExpectedRepayDate().toLocalDate();
                LocalDate dateLimit = repayDate.plusDays(3);
                if (todayDate.isAfter(repayDate) && (todayDate.isBefore(dateLimit) || todayDate.isEqual(dateLimit))) {
                    repayLoan(getDto(loan));
                }
            }
        }
    }

    @Scheduled(cron = "0 0 8 * * ?")
    public void scheduledAutomaticLoanRepaymentAfterThreeTrials() {
        List<Loan> allLoans = findAll();
        for (Loan loan : allLoans) {
            // find user account
            Account userAccount = accountService.accountValidationById(loan.getAccountId());
            if ((!loan.getLoanStatus().equals(LStatus.REPAID)) && loan.getTStatus().equals(TStatus.SUCCESSFUL)) {
                LocalDate todayDate = LocalDate.now();
                LocalDate dateLimit = loan.getExpectedRepayDate().toLocalDate().plusDays(3);
                if (todayDate.isAfter(dateLimit)) {
                    // get user savings bal
                    Double bal = userAccount.getSavingsBalance();
                    RepayDto repayDto = getDto(loan);
                    if (bal > loan.getAmountLeftToPay()){
                        repayLoan(repayDto);
                    } else {
                        repayDto.setLoanToRepay(bal);
                        repayLoan(repayDto);
                    }
                }
            }
        }
    }

    public RepayDto getDto(Loan loan){
        RepayDto repayDto = new RepayDto();
        repayDto.setLoanToRepay(loan.getAmountLeftToPay());
        repayDto.setLoanId(loan.getId());
        repayDto.setAmountToSave(0D);
        repayDto.setDescription("Automatic loan repayment");
        return repayDto;
    }

    public Loan repayLoan(RepayDto repayDto) throws AccountException, LoanException, TransactionException {
        //find loan
        Loan loan = findById(repayDto.getLoanId());
        // find user account
        Account userAccount = accountService.accountValidationById(loan.getAccountId());
        // create new instance of transaction
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setLoanToRepay(repayDto.getLoanToRepay());
        transactionDto.setAmount(repayDto.getAmountToSave());
        transactionDto.setDescription(repayDto.getDescription());
        transactionDto.setAccountNo(userAccount.getAccountNumber());
        // save amount if greater than 0
        if (repayDto.getAmountToSave() > 0) {
            transactionDto.setChannel(Channel.SAVE);
            transactionService.saveTransaction(transactionDto);
        }
        // repay loan
        transactionDto.setChannel(Channel.REPAY);
        transactionService.repayLoanTransaction(transactionDto);
        // get repay date
        LocalDateTime repayDate =  LocalDateTime.now();
        loan.setRepayDate(repayDate);
        loan.setLoanStatus(LStatus.REPAID);
        if (repayDto.getLoanToRepay() < loan.getAmountLeftToPay()) {
            loan.setLoanStatus(LStatus.PARTIAL_PAYMENT);
        }
        loan.setAmountLeftToPay(loan.getAmountLeftToPay() - repayDto.getLoanToRepay());
        loan.setAmountPaid(loan.getAmountPaid() + repayDto.getLoanToRepay());
        return loanRepository.save(loan);
    }

    public Double roundedFigure(Double figureToRound) {
        return (double) Math.round((figureToRound) * 100) / 100;
    }
}
