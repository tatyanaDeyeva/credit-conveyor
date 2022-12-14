package com.deyeva.deal.controller;

import com.deyeva.deal.api.DealApi;
import com.deyeva.deal.kafka.KafkaSender;
import com.deyeva.deal.model.EmailMessage;
import com.deyeva.deal.model.FinishRegistrationRequestDTO;
import com.deyeva.deal.model.LoanApplicationRequestDTO;
import com.deyeva.deal.model.LoanOfferDTO;
import com.deyeva.deal.model.entity.Application;
import com.deyeva.deal.service.DealService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DealController implements DealApi {

    private final DealService dealService;
    private final KafkaSender kafkaSender;

    public ResponseEntity<List<LoanOfferDTO>> getListOfPossibleLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return ResponseEntity.ok(dealService.getListOfPossibleLoanOffers(loanApplicationRequestDTO));
    }

    public ResponseEntity<Void> choiceLoanOffer(LoanOfferDTO loanOfferDTO){
        dealService.choiceLoanOffer(loanOfferDTO);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> calculatedLoanParameters(Long applicationId, FinishRegistrationRequestDTO finishRegistrationRequestDTO){
        dealService.calculatedLoanParameters(applicationId, finishRegistrationRequestDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/deal/admin/application/{applicationId}")
    public ResponseEntity<Application> getApplicationById(@PathVariable Long applicationId){
        return ResponseEntity.ok(dealService.getApplicationById(applicationId));
    }

    @GetMapping("/deal/admin/application")
    public ResponseEntity<List<Application>> getAllApplications(){
        return ResponseEntity.ok(dealService.getAllApplications());
    }

    public ResponseEntity<Void> toSendMessage(EmailMessage message){   //test api
        kafkaSender.sendMessage(message.getTheme(), message);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> sendDocuments(Long applicationId) {
        dealService.sendDocuments(applicationId);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> signDocuments(Long applicationId) {
        dealService.signDocuments(applicationId);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> sendCode(Long applicationId, String code) {
        dealService.sendCode(applicationId, code);
        return ResponseEntity.ok().build();
    }
}
