package com.example.helloworld.requests;

import java.util.List;
import lombok.Data;

@Data
public class StudentsRegistrationCheckRequest {
    Long courseId;
    List<Integer> dossierList;
}
