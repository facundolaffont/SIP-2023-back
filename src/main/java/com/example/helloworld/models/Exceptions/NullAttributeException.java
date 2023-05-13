package com.example.helloworld.models.Exceptions;

public class NullAttributeException extends RuntimeException {
    public NullAttributeException(String msg) {
        super(msg);
    }
}