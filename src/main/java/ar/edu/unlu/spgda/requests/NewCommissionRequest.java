package ar.edu.unlu.spgda.requests;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class NewCommissionRequest {
    
    @NotNull(message="Error: La asignatura es obligatoria")
    private Long subjectId;
    
    @NotNull(message="Error: El número de comisión es obligatorio")
    @Min(value=1, message="El número de comisión debe ser mayor a 0")
    private Integer commissionNumber;
}
