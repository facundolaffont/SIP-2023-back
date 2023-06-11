package com.example.helloworld.models.Exceptions;

public class EmptyQueryException extends Exception {

    public EmptyQueryException(String mensaje) {
        super(mensaje);
    }

}
