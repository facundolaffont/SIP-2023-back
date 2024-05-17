package ar.edu.unlu.spgda.requests;

import java.io.Serializable;
import java.util.List;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * Representa un pedido para registrar calificaciones
 * en un evento de instancia de evaluación, que se
 * registrará en la tabla 'evento_cursada_alumno'.
 */
@Data
@Component
public class CalificationsRegistrationOnEvent_Request implements Serializable {

    // PK de tabla Evento_Cursada.    
    private Long courseEventId;

    private List<Calification> califications;

}