package com.example.helloworld;

public class ProfessorRequest {
    private String email;
    private String password;
    private String rol;
    private String nombre;
    private String apellido;
    private int legajo;

    public ProfessorRequest (String email, String nombre, String apellido, int legajo, String password, String rol) {
        this.email = email;
        this.nombre = nombre;
        this.apellido = apellido;
        this.legajo = legajo;
        this.password = password;
        this.rol = rol;
    }

    public String getEmail() {
        return this.email;
    }
    public String getPassword() {
        return this.password;
    }
    public String getRol() {
        return this.rol;
    }
    public String getApellido() {
        return this.apellido;
    }
    public String getNombre() {
        return this.nombre;
    }
    public int getLegajo() {
        return this.legajo;
    }


}
