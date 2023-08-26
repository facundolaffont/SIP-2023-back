package com.example.helloworld.requests;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class NewStudentRequest implements Serializable{
    
    @Data
    public class NewStudentRegister implements Serializable {

        private Integer legajo;
        private Integer dni;
        private String nombre;
        private String apellido;
        private String email;
        private char condicion;
        private boolean recursante;
        
    }

    private long course;
    private List<NewStudentRegister> students;
    
}