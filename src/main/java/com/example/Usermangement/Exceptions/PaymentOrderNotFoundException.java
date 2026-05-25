package com.example.Usermangement.Exceptions;

public class PaymentOrderNotFoundException extends RuntimeException {
    public PaymentOrderNotFoundException(String message) {
        super(message);
    }
}
