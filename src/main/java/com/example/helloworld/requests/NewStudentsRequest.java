package com.example.helloworld.requests;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class NewStudentsRequest implements Serializable {
    
    @Data
    public static class NewStudentRegister implements Serializable {

        private Integer dossier;
        private Integer id;
        private String name;
        private String surname;
        private String email;
        
    }

    private List<NewStudentRegister> newStudentsList;
    
}