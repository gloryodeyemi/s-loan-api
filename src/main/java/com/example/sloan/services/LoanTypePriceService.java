package com.example.sloan.services;

import com.example.sloan.Repositories.LoanTypePriceRepository;
import com.example.sloan.dtos.UpdateLoanPriceDto;
import com.example.sloan.models.LoanType;
import com.example.sloan.models.LoanTypePrice;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoanTypePriceService {
    @Autowired
    LoanTypePriceRepository loanTypePriceRepository;

    public LoanTypePrice addPrice(LoanTypePrice loanTypePrice){
        return loanTypePriceRepository.save(loanTypePrice);
    }

    public LoanTypePrice getLoanPriceById(Long id){
        return loanTypePriceRepository.findById(id).orElse(null);
    }

    public LoanTypePrice getLoanPriceByLoanType(LoanType loanType){
        return loanTypePriceRepository.findByLoanType(loanType);
    }

    public List<LoanTypePrice> getAll(){
        return loanTypePriceRepository.findAll();
    }

    public LoanTypePrice updateLoanPrice(UpdateLoanPriceDto update){
        LoanTypePrice loanTypePrice = getLoanPriceById(update.getId());
        BeanUtils.copyProperties(update, loanTypePrice);
        return loanTypePriceRepository.save(loanTypePrice);
    }
}
