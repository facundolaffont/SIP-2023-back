package ar.edu.unlu.spgda.requests;

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
        private String email;
        private Boolean alreadyStudied;
        private Boolean allPreviousSubjectsApproved;
        
    }

    private Long courseId;
    private List<NewStudentRegister> newStudentsList;
    
}
