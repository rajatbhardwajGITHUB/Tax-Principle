package com.example.Usermangement.Exceptions;



public class EmailAlreadyRegisteredException extends AuthException{
    //create a constrctor 

    public EmailAlreadyRegisteredException(String message){
        super(message);
    }
}

    