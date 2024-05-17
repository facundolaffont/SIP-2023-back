package ar.edu.unlu.spgda.requests;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class NewCourseEventRequest {

    private Long idCursada;
    private Long tipoEvento;
    private Boolean obligatorio;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

}