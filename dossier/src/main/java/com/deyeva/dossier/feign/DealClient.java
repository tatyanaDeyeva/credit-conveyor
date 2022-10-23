package com.deyeva.dossier.feign;

import com.deyeva.dossier.model.Application;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "deal", url = "${msa.services.deal.url}")
public interface DealClient {

    @RequestMapping(method = RequestMethod.GET, value = "/deal/admin/application/{applicationId}")
    Application getApplicationById(@PathVariable String applicationId);
}
