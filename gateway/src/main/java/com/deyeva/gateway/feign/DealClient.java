package com.deyeva.gateway.feign;

import com.deyeva.gateway.model.Application;
import com.deyeva.gateway.model.FinishRegistrationRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(value = "deal", url = "${msa.services.deal.url}")
public interface DealClient {

    @RequestMapping(method = RequestMethod.GET, value = "/deal/admin/application/{applicationId}")
    Application getApplicationById(@PathVariable String applicationId);

    @RequestMapping(method = RequestMethod.GET, value = "/deal/admin/application")
    List<Application> getAllApplications();

    @RequestMapping(method = RequestMethod.PUT, value = "/deal/calculate/{applicationId}")
    void calculatedLoanParameters(@PathVariable String applicationId, @RequestBody FinishRegistrationRequestDTO finishRegistrationRequestDTO);

    @RequestMapping(method = RequestMethod.POST, value = "/deal/document/{applicationId}/send")
    void toSendDocuments(@PathVariable String applicationId);

    @RequestMapping(method = RequestMethod.POST, value = "/deal/document/{applicationId}/sign")
    void toSignDocuments(@PathVariable String applicationId);

    @RequestMapping(method = RequestMethod.POST, value = "/deal/document/{applicationId}/code")
    void toSendCode(@PathVariable String applicationId, @RequestBody String code);

}
