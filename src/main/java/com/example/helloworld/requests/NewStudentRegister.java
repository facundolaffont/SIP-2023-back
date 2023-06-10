package com.example.helloworld.requests;

import java.io.Serializable;

import lombok.Data;

@Data
public class NewStudentRegister implements Serializable {

    private Integer legajo;
    private Integer dni;
    private String nombre;
    private String apellido;
    private String email;
    
}