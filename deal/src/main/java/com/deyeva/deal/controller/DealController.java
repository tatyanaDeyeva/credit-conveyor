package com.deyeva.deal.controller;

import com.deyeva.deal.api.DealApi;
import com.deyeva.deal.kafka.KafkaSender;
import com.deyeva.deal.model.EmailMessage;
import com.deyeva.deal.model.FinishRegistrationRequestDTO;
import com.deyeva.deal.model.LoanApplicationRequestDTO;
import com.deyeva.deal.model.LoanOfferDTO;
import com.deyeva.deal.model.entity.Application;
import com.deyeva.deal.repository.ApplicationRepository;
import com.deyeva.deal.service.DealService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
public class DealController implements DealApi {

    private final DealService dealService;
    private final KafkaSender kafkaSender;
    private final ApplicationRepository applicationRepository;

    public DealController(DealService dealService, KafkaSender kafkaSender, ApplicationRepository applicationRepository) {
        this.dealService = dealService;
        this.kafkaSender = kafkaSender;
        this.applicationRepository = applicationRepository;
    }

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

    @GetMapping("deal/application/{applicationId}")
    public ResponseEntity<Application> getApplicationById(@PathVariable String applicationId){
        return ResponseEntity.ok(applicationRepository.findById(Long.valueOf(applicationId))
                .orElseThrow(() -> new EntityNotFoundException("Application with id = "+ applicationId +" not found.")));
    }


    public ResponseEntity<Void> toSendMessage(EmailMessage message){   //test api
        kafkaSender.sendMessage(message.getTheme(), message);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> toSendDocuments(Long applicationId) {
        dealService.toSendDocuments(applicationId);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> toSignDocuments(Long applicationId) {
        dealService.toSignDocuments(applicationId);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> toSendCode(Long applicationId, String code) {
        dealService.toSendCode(applicationId, code);
        return ResponseEntity.ok().build();
    }
}
