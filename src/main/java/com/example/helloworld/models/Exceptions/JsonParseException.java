package com.example.helloworld.models.Exceptions;

public class JsonParseException extends RuntimeException {
    public JsonParseException(String msg) {
        super(msg);
    }
}
