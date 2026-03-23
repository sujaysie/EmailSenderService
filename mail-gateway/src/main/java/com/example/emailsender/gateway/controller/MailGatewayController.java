package com.example.emailsender.gateway.controller;

import com.example.emailsender.gateway.dto.MailGatewayDtos.BatchSendRequest;
import com.example.emailsender.gateway.dto.MailGatewayDtos.BatchSendResponse;
import com.example.emailsender.gateway.dto.MailGatewayDtos.DeliveryStatusResponse;
import com.example.emailsender.gateway.dto.MailGatewayDtos.SendMailRequest;
import com.example.emailsender.gateway.dto.MailGatewayDtos.SendResponse;
import com.example.emailsender.gateway.service.MailGatewayService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mail")
public class MailGatewayController {
    private final MailGatewayService mailGatewayService;

    public MailGatewayController(MailGatewayService mailGatewayService) {
        this.mailGatewayService = mailGatewayService;
    }

    @PostMapping("/send")
    public ResponseEntity<SendResponse> send(@Valid @RequestBody SendMailRequest request,
                                             @RequestHeader("X-Correlation-Id") String correlationId) {
        return ResponseEntity.accepted().body(mailGatewayService.send(request, correlationId));
    }

    @PostMapping("/send/batch")
    public ResponseEntity<BatchSendResponse> sendBatch(@Valid @RequestBody BatchSendRequest request,
                                                       @RequestHeader("X-Correlation-Id") String correlationId) {
        return ResponseEntity.accepted().body(mailGatewayService.sendBatch(request, correlationId));
    }

    @GetMapping("/status/{eventId}")
    public ResponseEntity<DeliveryStatusResponse> getStatus(@PathVariable String eventId) {
        return ResponseEntity.ok(mailGatewayService.getStatus(eventId));
    }
}
