package com.deyeva.gateway.feign;

import com.deyeva.gateway.model.LoanApplicationRequestDTO;
import com.deyeva.gateway.model.LoanOfferDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(value = "application", url = "${msa.services.application.url}")
public interface ApplicationClient {

    @RequestMapping(method = RequestMethod.POST, value = "application")
    List<LoanOfferDTO> getListOfPossibleLoanOffers(@RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO);

    @RequestMapping(method = RequestMethod.PUT, value = "application/offer")
    void choiceLoanOffer(@RequestBody LoanOfferDTO loanOfferDTO);
}
