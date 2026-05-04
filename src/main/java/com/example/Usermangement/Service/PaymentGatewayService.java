package com.example.Usermangement.Service;

import com.example.Usermangement.Model.PaymentOrderRequest;
import com.example.Usermangement.Model.PaymentOrderResponse;
import com.example.Usermangement.Model.PaymentVerificationRequest;
import com.example.Usermangement.Model.PurchaseResponse;

public interface PaymentGatewayService {

    PaymentOrderResponse createOrder(String email, PaymentOrderRequest request);

    PurchaseResponse verifyPayment(String email, PaymentVerificationRequest request);
}
