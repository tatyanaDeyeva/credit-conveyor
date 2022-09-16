package com.deyeva.creditconveyor.controller;

import com.deyeva.creditconveyor.api.ConveyorApi;
import com.deyeva.creditconveyor.model.CreditDTO;
import com.deyeva.creditconveyor.model.LoanApplicationRequestDTO;
import com.deyeva.creditconveyor.model.LoanOfferDTO;
import com.deyeva.creditconveyor.model.ScoringDataDTO;
import com.deyeva.creditconveyor.service.ConveyorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ConveyorController implements ConveyorApi {

    private final ConveyorService conveyorService;

    public ConveyorController(ConveyorService conveyorService) {
        this.conveyorService = conveyorService;
    }

    @Override
    public ResponseEntity<List<LoanOfferDTO>> listOfPossibleLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return ResponseEntity.ok(conveyorService.listOfPossibleLoanOffers(loanApplicationRequestDTO));
    }

    @Override
    public ResponseEntity<CreditDTO> loanParameters(ScoringDataDTO scoringDataDTO) {
        return ResponseEntity.ok(conveyorService.loanParameters(scoringDataDTO));
    }
}
