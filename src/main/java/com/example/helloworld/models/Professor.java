package com.example.helloworld.models;

import lombok.Value;

@Value
public class Professor {
    private long id;
    private String email;
    private String first_name;
    private String last_name;
    private int legajo;

    public Professor (long id, String email, String first_name, String last_name, int legajo) {
        this.id = id;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.legajo = legajo;
    }
    
}
