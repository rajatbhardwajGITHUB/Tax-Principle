package com.example.Usermangement.Exceptions;

public class PhoneNumberAlreadyRegisteredException extends RuntimeException {

    public PhoneNumberAlreadyRegisteredException(String message) {
        super(message);
    }
}
