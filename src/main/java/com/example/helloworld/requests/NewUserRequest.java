package com.example.helloworld.requests;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class NewUserRequest {
    private String email;
    private String password;
    private String rol;
    private String nombre;
    private String apellido;
    private Integer legajo;
}