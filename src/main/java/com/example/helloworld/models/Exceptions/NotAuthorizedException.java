package com.example.helloworld.models.Exceptions;

public class NotAuthorizedException extends RuntimeException {

    public NotAuthorizedException(String msg) {
        super(msg);
    }

}
