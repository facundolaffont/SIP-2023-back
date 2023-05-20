package com.example.helloworld.services;

import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
        logger.info("registerCalificationsOnEvent(...)"); // logger.debug

        /**
         * Inserta los registro en la tabla Eve_Cur_Alum, que tiene el
         * siguiente formato:
         * 
         *  idAsignatura INTEGER
         *  numeroComision INTEGER
         *  anioCursada INTEGER
         *  idEvento INTEGER
         *  legajoAlumno INTEGER
         *  asistencia boolean
         *  nota INTEGER
         *  CONSTRAINT pk_Eve_Cur_Alum PRIMARY KEY (idAsignatura, numeroComision, anioCursada, idEvento, legajoAlumno),
         *  CONSTRAINT fk_asistencia_evento_cursada FOREIGN KEY (idAsignatura, numeroComision, anioCursada, idEvento) REFERENCES EVENTO_CURSADA (idAsignatura, numeroComision, anioCursada, idEvento) ON DELETE CASCADE,
         *  CONSTRAINT fk_asistencia_cur_alum FOREIGN KEY (idAsignatura, numeroComision, anioCursada, legajoAlumno) REFERENCES CUR_ALUM (asignaturaId, comisionNro, anioCursada, legajo) ON DELETE CASCADE
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
            logger.info("Se guardó un registro."); // logger.debug
        }
        
    }


    /* Private */

    private static final Logger logger = LogManager.getLogger(ProfessorService.class);
}
