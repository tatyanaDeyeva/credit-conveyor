package com.deyeva.gateway.controller;

import com.deyeva.gateway.api.AdminApi;
import com.deyeva.gateway.model.Application;
import com.deyeva.gateway.service.GatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminController implements AdminApi {

    private final GatewayService gatewayService;

    @Override
    public ResponseEntity<List<Application>> getAllApplications() {
        return ResponseEntity.ok(gatewayService.getAllApplications());
    }

    @Override
    public ResponseEntity<Application> getApplicationById(Long applicationId) {
        return ResponseEntity.ok(gatewayService.getApplicationById(applicationId));
    }
}
