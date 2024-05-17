package ar.edu.unlu.spgda.models;

import lombok.Data;

@Data
public class CourseDto {
    private long id;
    private int codigoAsignatura;
    private String nombreAsignatura;
    private String nombreCarrera;
    private long numeroComision;
    private int anio;
    private int nivelPermiso;
}
