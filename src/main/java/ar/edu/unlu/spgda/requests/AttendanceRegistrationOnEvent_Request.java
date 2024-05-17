package ar.edu.unlu.spgda.requests;

import java.io.Serializable;
import java.util.List;
import org.springframework.stereotype.Component;
import lombok.Data;

/**
 * Representa un pedido para registrar asistencias
 * en un evento de instancia de evaluación, que se
 * registrará en la tabla 'evento_cursada_alumno'.
 */
@Data
@Component
public class AttendanceRegistrationOnEvent_Request implements Serializable {

    // PK de tabla Evento_Cursada.    
    private Long courseEventId;

    private List<Attendance> attendance;

}