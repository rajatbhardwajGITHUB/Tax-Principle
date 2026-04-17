package com.example.Usermangement.Exceptions;

public class DuplicatePaymentReferenceException extends RuntimeException {
    public DuplicatePaymentReferenceException(String message) {
        super(message);
    }
}
