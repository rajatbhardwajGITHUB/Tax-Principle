package com.example.Usermangement.Model;

import jakarta.validation.constraints.NotBlank;

public class PaymentVerificationRequest {

    @NotBlank
    private String orderId;

    @NotBlank
    private String paymentId;

    @NotBlank
    private String signature;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
