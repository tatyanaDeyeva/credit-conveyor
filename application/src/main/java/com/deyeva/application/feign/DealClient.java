package com.deyeva.application.feign;

import com.deyeva.application.model.LoanApplicationRequestDTO;
import com.deyeva.application.model.LoanOfferDTO;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(value = "deal", url = "${msa.services.deal.url}")
public interface DealClient {

    @RequestMapping(method = RequestMethod.POST, value = "/deal/application")
    List<LoanOfferDTO> getOffers(@RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO);

    @RequestMapping(method = RequestMethod.PUT, value = "/deal/offer")
    void getLoanOffer(@RequestBody LoanOfferDTO loanOfferDTO);
}
