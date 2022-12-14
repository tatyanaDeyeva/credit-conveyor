package com.deyeva.application.controller;

import com.deyeva.application.api.ApplicationApi;
import com.deyeva.application.model.LoanApplicationRequestDTO;
import com.deyeva.application.model.LoanOfferDTO;
import com.deyeva.application.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ApplicationController implements ApplicationApi {

    private final ApplicationService applicationService;

    @Override
    public ResponseEntity<List<LoanOfferDTO>> getListOfPossibleLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return ResponseEntity.ok(applicationService.getListOfPossibleLoanOffers(loanApplicationRequestDTO));
    }

    @Override
    public ResponseEntity<Void> choiceLoanOffer(LoanOfferDTO loanOfferDTO){
        applicationService.choiceLoanOffer(loanOfferDTO);
        return ResponseEntity.ok().build();
    }
}
