package ar.edu.unlu.spgda.responses;

import ar.edu.unlu.spgda.models.Comission;

public record CommissionResponse(
    Integer id,
    Integer numero,

    Long idAsignatura,
    String nombreAsignatura,
    Integer codigoAsignatura,

    Long idCarrera,
    String nombreCarrera
) {
    public static CommissionResponse fromEntity(Comission c) {
        return new CommissionResponse(
            c.getId(),
            c.getNumero(),
            c.getAsignatura().getId(),
            c.getAsignatura().getNombre(),
            c.getAsignatura().getCodigoAsignatura(),
            c.getAsignatura().getIdCarrera().getId(),
            c.getAsignatura().getIdCarrera().getNombre()
        );
    }
}
