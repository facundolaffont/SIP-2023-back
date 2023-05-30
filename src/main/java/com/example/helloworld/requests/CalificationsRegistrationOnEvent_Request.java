package com.example.helloworld.requests;

import java.util.List;
import org.springframework.stereotype.Component;
import com.example.helloworld.models.Calification;
import lombok.Data;

/**
 * Representa un pedido para registrar calificaciones
 * en un evento de instancia de evaluación, que se
 * registrará en la tabla 'evento_cursada_alumno'.
 */
@Data
@Component
public class CalificationsRegistrationOnEvent_Request {

    // PK de tabla Evento_Cursada.    
    private Long courseEventId;

    private List<Calification> califications;

}