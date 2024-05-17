package ar.edu.unlu.spgda.requests;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor public class StudentsRegistrationRequest {

    public StudentsRegistrationRequest(Long courseId) {
        this.courseId = courseId;
        this.studentsRegistrationList = new ArrayList<StudentRegistrationRequest>();
    }

    @Data @AllArgsConstructor public static class StudentRegistrationRequest {
        private Integer dossier;
        private Boolean previousSubjectsApproved;
        private Boolean studiedPreviously;

        public Boolean hasPreviousSubjectsApproved() {
            return previousSubjectsApproved;
        }

        public Boolean hasStudiedItPreviously() {
            return studiedPreviously;
        }
    }

    public void addStudentRegistrationInfo(
        Integer dossier,
        Boolean previousSubjectsApproved,
        Boolean studiedPreviously
    ) {
        studentsRegistrationList.add(new StudentRegistrationRequest(
            dossier,
            previousSubjectsApproved,
            studiedPreviously
        ));
    }

    public StudentRegistrationRequest searchFirstByDossier(
        Integer dossier
    ) {
        for (StudentRegistrationRequest studentRegistrationRequest : studentsRegistrationList) {
            if (studentRegistrationRequest.getDossier().equals(dossier)) {
                return studentRegistrationRequest;
            }
        }
        return null;
    }

    private Long courseId;
    private List<StudentRegistrationRequest> studentsRegistrationList;

}
