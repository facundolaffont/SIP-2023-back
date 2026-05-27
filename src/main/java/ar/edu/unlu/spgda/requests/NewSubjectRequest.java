package ar.edu.unlu.spgda.requests;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class NewSubjectRequest {
    
    @NotNull(message="Error: La carrera es obligatoria")
    private Long careerId;
    
    @NotNull(message="Error: El código de la asignatura es obligatorio")
    @Min(value=1, message="El código de la asignatura debe ser mayor o igual a 1")
    private Integer subjectCode;
    
    @NotBlank(message="Error: El nombre de la asignatura es obligatorio")
    private String subjectName;
}
