package com.example.Usermangement.Exceptions;

public class InvalidRefreshTokenException extends AuthException{

    public InvalidRefreshTokenException(String message){
        super(message);
    }

}
