package com.example.helloworld.models.Exceptions;

public class NotValidAttributeException extends Exception {
    public NotValidAttributeException(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return msg;
    }


    /* Private */

    private String msg;
}