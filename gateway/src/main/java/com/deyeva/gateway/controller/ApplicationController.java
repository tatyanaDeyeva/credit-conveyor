package com.deyeva.gateway.controller;

import com.deyeva.gateway.api.ApplicationApi;
import com.deyeva.gateway.model.LoanApplicationRequestDTO;
import com.deyeva.gateway.model.LoanOfferDTO;
import com.deyeva.gateway.service.GatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ApplicationController implements ApplicationApi {

    private final GatewayService gatewayService;

    @Override
    public ResponseEntity<Void> choiceLoanOffer(LoanOfferDTO loanOfferDTO) {
        gatewayService.choiceLoanOffer(loanOfferDTO);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<LoanOfferDTO>> getListOfPossibleLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return ResponseEntity.ok(gatewayService.getListOfPossibleLoanOffers(loanApplicationRequestDTO));
    }
}
