package com.deyeva.gateway.controller;

import com.deyeva.gateway.api.DocumentApi;
import com.deyeva.gateway.service.GatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DocumentController implements DocumentApi {

    private final GatewayService gatewayService;

    @Override
    public ResponseEntity<Void> sendCode(Long applicationId, String body) {
        gatewayService.sendCode(applicationId, body);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> sendDocuments(Long applicationId) {
        gatewayService.sendDocuments(applicationId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> signDocuments(Long applicationId) {
        gatewayService.signDocuments(applicationId);
        return ResponseEntity.ok().build();
    }
}
