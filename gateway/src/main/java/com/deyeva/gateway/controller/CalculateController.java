package com.deyeva.gateway.controller;

import com.deyeva.gateway.api.CalculateApi;
import com.deyeva.gateway.model.FinishRegistrationRequestDTO;
import com.deyeva.gateway.service.GatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CalculateController implements CalculateApi {

    private final GatewayService gatewayService;

    @Override
    public ResponseEntity<Void> calculatedLoanParameters(Long applicationId, FinishRegistrationRequestDTO finishRegistrationRequestDTO) {
        gatewayService.calculatedLoanParameters(applicationId, finishRegistrationRequestDTO);
        return ResponseEntity.ok().build();
    }
}
