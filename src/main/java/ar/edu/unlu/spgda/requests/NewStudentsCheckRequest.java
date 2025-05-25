package ar.edu.unlu.spgda.requests;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class NewStudentsCheckRequest implements Serializable {

    @Data
    public static class Student implements Serializable {

        private Integer dossier;
        private Integer id;
        private String email;
        
    }

    private Long courseId;
    private List<Student> studentsList;
    
}