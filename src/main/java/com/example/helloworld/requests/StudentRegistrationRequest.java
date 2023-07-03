package com.example.helloworld.requests;

import lombok.Data;

@Data
public class StudentRegistrationRequest {
    Integer dossier;
    boolean previousSubjectsApproved;
    boolean studiedPreviously;

    public boolean hasPreviousSubjectsApproved() {
        return previousSubjectsApproved;
    }

    public boolean hasStudiedItPreviously() {
        return studiedPreviously;
    }
}
