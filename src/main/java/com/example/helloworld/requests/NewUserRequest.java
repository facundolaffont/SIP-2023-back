package com.example.helloworld.requests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Getter;

public class NewUserRequest {
    @Getter private String email;
    @Getter private String password;
    @Getter private String rol;
    @Getter private String nombre;
    @Getter private String apellido;
    @Getter private Integer legajo;

    public NewUserRequest (String email, String nombre, String apellido, Integer legajo, String password, String rol) {
        this.email = email;
        this.nombre = nombre;
        this.apellido = apellido;
        this.legajo = legajo;
        this.password = password;
        this.rol = rol;
    }


    /* Private */

    private static final Logger logger = LogManager.getLogger(NewUserRequest.class);
}