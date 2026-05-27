package ar.edu.unlu.spgda.responses;

import ar.edu.unlu.spgda.models.Subject;

public record SubjectResponse(
    Long id,
    String nombre,
    Integer codigo,

    Long idCarrera,
    String nombreCarrera
) {
    public static SubjectResponse fromEntity(Subject s) {
        return new SubjectResponse(
            s.getId(),
            s.getNombre(),
            s.getCodigoAsignatura(),
            s.getIdCarrera().getId(),
            s.getIdCarrera().getNombre()
        );
    }
}
