package ar.edu.unlu.spgda.requests;

import java.util.List;

import lombok.Data;

@Data
public class FinalConditions {
    
    private long courseId;
    private List<StudentFinalCondition> finalConditions;

}
