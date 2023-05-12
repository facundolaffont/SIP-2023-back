package com.example.helloworld.models.Exceptions;

public class NullAttributeException extends Exception {
    public NullAttributeException(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return msg;
    }


    /* Private */

    private String msg;
}