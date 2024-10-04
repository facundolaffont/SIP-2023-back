package ar.edu.unlu.spgda.requests;

import lombok.Data;

@Data
public class NewUserRequest {

    private String email;
    private String password;
    private String rol;
    private String nombre;
    private Integer legajo;
    
}