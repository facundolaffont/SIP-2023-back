package com.example.helloworld.requests;

import lombok.Getter;

public class ProfessorRequest {
    @Getter private String email;
    @Getter private String password;
    @Getter private String rol;
    @Getter private String nombre;
    @Getter private String apellido;
    @Getter private int legajo;

    public ProfessorRequest (String email, String nombre, String apellido, int legajo, String password, String rol) {
        this.email = email;
        this.nombre = nombre;
        this.apellido = apellido;
        this.legajo = legajo;
        this.password = password;
        this.rol = rol;
    }
}