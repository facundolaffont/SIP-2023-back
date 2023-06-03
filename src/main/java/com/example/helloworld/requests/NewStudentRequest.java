package com.example.helloworld.requests;

import java.util.List;
import lombok.Data;

@Data
public class NewStudentRequest {
    
    private List<NewStudentRegister> students;
    
}