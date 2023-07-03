package com.example.helloworld.requests;

import java.util.List;
import lombok.Data;

@Data
public class StudentsRegistrationRequest {
    Long courseId;
    List<StudentRegistrationRequest> studentsRegistrationList;

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

}
