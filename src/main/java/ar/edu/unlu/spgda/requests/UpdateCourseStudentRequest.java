package ar.edu.unlu.spgda.requests;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.Data;

@Data
public class UpdateCourseStudentRequest {
    @NotNull(message="El ID de la cursada es obligatorio")
    private Long courseId; // Es vital saber en qué cursada lo estamos modificando

    @NotNull(message="El legajo es obligatorio")
    private Integer dossier; // El ID del alumno en tu sistema es el legajo (dossier), no el DNI

    @NotNull(message="El DNI es obligatorio")
    @Min(value=1, message="El DNI debe ser mayor a 0")
    private Integer id; // DNI

    @NotBlank(message="El nombre es obligatorio")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$", message="El nombre solo puede contener letras y espacios")
    private String name;

    @NotBlank(message="El email es obligatorio")
    @Email(message="El formato del email no es válido")
    private String email;

    // Recursante se calcula automáticamente por el sistema (campo ignorado en la actualización).
    private Boolean alreadyStudied; // Recursante

    @NotNull(message="El estado de las correlativas es obligatorio")
    private Boolean allPreviousSubjectsApproved; // Correlativas aprobadas

    @NotBlank(message="La condición final es obligatoria")
    @Pattern(regexp = "^[PRLA]$", message="La condición final solo puede ser P, R, L, o A")
    private String finalCondition;
}