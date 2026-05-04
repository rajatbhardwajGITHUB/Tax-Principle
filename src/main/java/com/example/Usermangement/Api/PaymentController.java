package com.example.Usermangement.Api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Usermangement.Model.PaymentOrderRequest;
import com.example.Usermangement.Model.PaymentOrderResponse;
import com.example.Usermangement.Model.PaymentVerificationRequest;
import com.example.Usermangement.Model.PurchaseResponse;
import com.example.Usermangement.Service.PaymentGatewayService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentGatewayService paymentGatewayService;

    public PaymentController(PaymentGatewayService paymentGatewayService) {
        this.paymentGatewayService = paymentGatewayService;
    }

    @PostMapping("/orders")
    public ResponseEntity<PaymentOrderResponse> createOrder(
            Authentication authentication,
            @Valid @RequestBody PaymentOrderRequest request) {
        return ResponseEntity.ok(paymentGatewayService.createOrder(authentication.getName(), request));
    }

    @PostMapping("/verify")
    public ResponseEntity<PurchaseResponse> verifyPayment(
            Authentication authentication,
            @Valid @RequestBody PaymentVerificationRequest request) {
        return ResponseEntity.ok(paymentGatewayService.verifyPayment(authentication.getName(), request));
    }
}
