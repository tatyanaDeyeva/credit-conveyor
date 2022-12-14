package com.deyeva.gateway.service;

import com.deyeva.gateway.feign.ApplicationClient;
import com.deyeva.gateway.feign.DealClient;
import com.deyeva.gateway.model.Application;
import com.deyeva.gateway.model.FinishRegistrationRequestDTO;
import com.deyeva.gateway.model.LoanApplicationRequestDTO;
import com.deyeva.gateway.model.LoanOfferDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GatewayService {

    private final ApplicationClient applicationClient;
    private final DealClient dealClient;

    public List<LoanOfferDTO> getListOfPossibleLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO){
        return applicationClient.getListOfPossibleLoanOffers(loanApplicationRequestDTO);
    }

    public void choiceLoanOffer(LoanOfferDTO loanOfferDTO){
        applicationClient.choiceLoanOffer(loanOfferDTO);
    }

    public void calculatedLoanParameters(Long applicationId, FinishRegistrationRequestDTO finishRegistrationRequestDTO){
        dealClient.calculatedLoanParameters(String.valueOf(applicationId), finishRegistrationRequestDTO);
    }

    public void sendDocuments(Long applicationId) {
        dealClient.sendDocuments(String.valueOf(applicationId));
    }

    public void signDocuments(Long applicationId) {
        dealClient.signDocuments(String.valueOf(applicationId));
    }

    public void sendCode(Long applicationId, String code) {
        dealClient.sendCode(String.valueOf(applicationId), code);
    }

    public Application getApplicationById(Long applicationId) {
        return dealClient.getApplicationById(String.valueOf(applicationId));
    }

    public List<Application> getAllApplications() {
        return dealClient.getAllApplications();
    }
}
