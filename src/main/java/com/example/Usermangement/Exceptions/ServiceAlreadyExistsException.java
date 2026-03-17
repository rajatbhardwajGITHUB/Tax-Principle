package com.example.Usermangement.Exceptions;

public class ServiceAlreadyExistsException extends AuthException{

    public ServiceAlreadyExistsException(String message){
        super(message);
    }
}
