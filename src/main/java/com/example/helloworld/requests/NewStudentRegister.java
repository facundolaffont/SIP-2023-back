package com.example.helloworld.requests;

import lombok.Data;

@Data
public class NewStudentRegister {

    private Integer legajo;
    private Integer dni;
    private String nombre;
    private String apellido;
    private String email;
    
}