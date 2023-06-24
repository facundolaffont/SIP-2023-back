package com.example.helloworld.requests;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class NewStudentRequest implements Serializable{
    
    private long course;
    private List<NewStudentRegister> students;
    
}