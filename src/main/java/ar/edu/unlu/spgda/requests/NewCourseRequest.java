package ar.edu.unlu.spgda.requests;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class NewCourseRequest {

    @NotNull(message="La comisión es obligatoria")
    private Integer commissionId;

    @NotNull(message="El año es obligatorio")
    @Min(value=2000, message="El año debe ser mayor o igual a 2000")
    @Max(value=2100, message="El año debe ser menor o igual a 2100")
    private Integer anio;

    @NotNull(message="La fecha de inicio es obligatoria")
    private LocalDate initialDate;

    @NotNull(message="La fecha de fin es obligatoria")
    private LocalDate endDate;

    private List<String> professorIds;

    @AssertTrue(message = "La fecha de inicio no puede ser mayor a la fecha de fin")
    private boolean isFechasEnOrden() {
        if (initialDate == null || endDate == null) return true; 
        return !initialDate.isAfter(endDate);
    }

    @AssertTrue(message = "Las fechas deben pertenecer al año especificado")
    private boolean isAñoCoincideConFechas() {
        if (anio == null || initialDate == null || endDate == null) return true;
        return initialDate.getYear() == anio && endDate.getYear() == anio;
    }

}

