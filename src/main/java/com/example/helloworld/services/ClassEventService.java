package com.example.helloworld.services;

import java.sql.SQLException;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.example.helloworld.models.Calification;
import com.example.helloworld.models.DatabaseHandler;
import com.example.helloworld.requests.CalificationsRegistrationOnEvent_Request;

@Service
public class ClassEventService {

    /**
     * Registra las calificaciones en un evento específico.
     */
    public void registerCalificationsOnEvent(
        CalificationsRegistrationOnEvent_Request calificationsRegistrationOnEvent_Request
    )
        throws SQLException
    {
        logger.debug("registerCalificationsOnEvent(...)");

        /**
         * Inserta los registro en la tabla 'evento_cursada_alumno', que tiene el
         * siguiente formato:
         */

        // Intenta insertar el registro del docente en la tabla.
        // Arroja una excepción si no fue posible.
        // TODO: refactorizar el método DatabaseHandler.insert para que acepte un Map igual que acepta Validator.
        ArrayList<Object> atributos;
        var databaseHandler = DatabaseHandler.getInstance();
        for(Calification calification: calificationsRegistrationOnEvent_Request.getCalifications()) {
            atributos = new ArrayList<Object>();
            atributos.add(calificationsRegistrationOnEvent_Request.getIdAsignatura());
            atributos.add(calificationsRegistrationOnEvent_Request.getCommissionNumber());
            atributos.add(calificationsRegistrationOnEvent_Request.getClassYear());
            atributos.add(calificationsRegistrationOnEvent_Request.getEventID());
            atributos.add(calification.getDossier());
            atributos.add(true);
            atributos.add(calification.getCalification());
            databaseHandler
                .executeStatement(
                    "INSERT" +
                        " INTO Eve_Cur_Alum (idAsignatura, numeroComision, anioCursada, idEvento, legajoAlumno, asistencia, nota)" +
                        " VALUES (?, ?, ?, ?, ?, ?, ?)",
                    atributos
                );
            logger.debug("Se guardó un registro.");
        }
        
    }


    /* Private */

    private static final Logger logger = LoggerFactory.getLogger(ClassEventService.class);

}
