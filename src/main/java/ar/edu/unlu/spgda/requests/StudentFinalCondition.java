package ar.edu.unlu.spgda.requests;

import lombok.Data;

@Data
public class StudentFinalCondition {

    private int legajo;
    private String nota;
    private String observaciones;
}
