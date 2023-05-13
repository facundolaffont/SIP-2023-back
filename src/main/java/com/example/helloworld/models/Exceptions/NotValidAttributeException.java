package com.example.helloworld.models.Exceptions;

public class NotValidAttributeException extends RuntimeException {
    public NotValidAttributeException(String msg) {
        super(msg);
    }
}