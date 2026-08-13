package ar.edu.unlu.spgda.requests;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class RegisterStudentGroupsRequest implements Serializable {

    @Data
    public static class StudentGroupEntry implements Serializable {

        private Integer dossier;
        private String groupName;

    }

    private Long courseId;
    private List<StudentGroupEntry> groupEntries;

}
