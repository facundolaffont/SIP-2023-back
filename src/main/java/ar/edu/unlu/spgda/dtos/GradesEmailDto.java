package ar.edu.unlu.spgda.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO con los datos necesarios para enviar el email de calificaciones.
 * Se arma en CourseService (hilo principal, sesión JPA activa) y se pasa a
 * EmailService (@Async), que trabaja sin sesión JPA.
 */
@Data
@AllArgsConstructor
public class GradesEmailDto {

    /** Email del alumno destinatario. */
    private String studentEmail;

    /** Nombre del alumno. */
    private String studentName;

    /** Calificación obtenida (nota). */
    private String grade;

    /** Nombre del evento (ej: "Parcial 1" o tipo de evento si no tiene nombre propio). */
    private String eventName;

    /** Info de la cursada (ej: "Análisis Matemático (Comisión 1)"). */
    private String courseInfo;

}
