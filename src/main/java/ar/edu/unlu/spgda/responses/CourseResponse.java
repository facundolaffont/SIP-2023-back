package ar.edu.unlu.spgda.responses;

import java.time.LocalDate;
import ar.edu.unlu.spgda.models.Course;

public record CourseResponse(
    Long id,
    Integer anio,
    LocalDate fechaInicio,
    LocalDate fechaFin,

    Integer idComision,
    Integer numeroComision,

    Long idAsignatura,
    String nombreAsignatura,
    Integer codigoAsignatura,

    Long idCarrera,
    String nombreCarrera
) {
    public static CourseResponse fromEntity(Course c) {
        return new CourseResponse(
            c.getId(),
            c.getAnio(),
            c.getFechaInicio() != null ? c.getFechaInicio().toLocalDate() : null,
            c.getFechaFin() != null ? c.getFechaFin().toLocalDate() : null,
            c.getComision().getId(),
            c.getComision().getNumero(),
            c.getComision().getAsignatura().getId(),
            c.getComision().getAsignatura().getNombre(),
            c.getComision().getAsignatura().getCodigoAsignatura(),
            c.getComision().getAsignatura().getIdCarrera().getId(),
            c.getComision().getAsignatura().getIdCarrera().getNombre()
        );
    }
}