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
    public ResponseEntity<Void> toSendCode(Long applicationId, String body) {
        gatewayService.toSendCode(applicationId, body);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> toSendDocuments(Long applicationId) {
        gatewayService.toSendDocuments(applicationId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> toSignDocuments(Long applicationId) {
        gatewayService.toSignDocuments(applicationId);
        return ResponseEntity.ok().build();
    }
}
