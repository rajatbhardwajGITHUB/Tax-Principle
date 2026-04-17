package com.example.Usermangement.Exceptions;

public class PurchaseAccessDeniedException extends RuntimeException {
    public PurchaseAccessDeniedException(String message) {
        super(message);
    }
}
