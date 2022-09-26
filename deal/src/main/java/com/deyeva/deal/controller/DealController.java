package com.deyeva.deal.controller;

import com.deyeva.deal.api.DealApi;
import com.deyeva.deal.model.FinishRegistrationRequestDTO;
import com.deyeva.deal.model.LoanApplicationRequestDTO;
import com.deyeva.deal.model.LoanOfferDTO;
import com.deyeva.deal.service.DealService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DealController implements DealApi {

    private final DealService dealService;

    public DealController(DealService dealService) {
        this.dealService = dealService;
    }

    public ResponseEntity<List<LoanOfferDTO>> listOfPossibleLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return ResponseEntity.ok(dealService.listOfPossibleLoanOffers(loanApplicationRequestDTO));
    }

    public ResponseEntity<Void> loanOffer(LoanOfferDTO loanOfferDTO){
        dealService.loanOffer(loanOfferDTO);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> loanParameters(Long applicationId, FinishRegistrationRequestDTO finishRegistrationRequestDTO){
        dealService.loanParameters(applicationId, finishRegistrationRequestDTO);
        return ResponseEntity.ok().build();
    }
}
