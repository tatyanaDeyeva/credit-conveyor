package com.deyeva.deal.feign;

import com.deyeva.deal.model.CreditDTO;
import com.deyeva.deal.model.LoanApplicationRequestDTO;
import com.deyeva.deal.model.LoanOfferDTO;
import com.deyeva.deal.model.ScoringDataDTO;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(value = "conveyor", url = "${msa.services.conveyor.url}")
public interface CreditConveyorClient {

    @RequestMapping(method = RequestMethod.POST, value = "/conveyor/offers")
    List<LoanOfferDTO> getOffers(@RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO);

    @RequestMapping(method = RequestMethod.POST, value = "/conveyor/calculation")
    CreditDTO getLoanOffer(@RequestBody ScoringDataDTO scoringDataDTO);

}
