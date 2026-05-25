package com.example.Usermangement.Exceptions;

public class InvalidPaymentSignatureException extends RuntimeException {
    public InvalidPaymentSignatureException(String message) {
        super(message);
    }
}
