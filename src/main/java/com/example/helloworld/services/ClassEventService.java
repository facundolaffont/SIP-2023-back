package com.example.helloworld.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import com.auth0.exception.APIException;
import com.auth0.exception.Auth0Exception;
import com.example.helloworld.models.ClassEvent;
import com.example.helloworld.models.DatabaseHandler;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import com.example.helloworld.models.Validator;
import com.example.helloworld.models.Exceptions.NotValidAttributeException;
import com.example.helloworld.models.Exceptions.NullAttributeException;

@Service
public class ClassEventService {

    // Crea un docente y lo guarda en la BD.
    public ClassEvent getEventByDateAndCommission (
        String dateOfEvent,
        int idClassEvent
    ) throws
        NullAttributeException,
        NotValidAttributeException,
        SQLException,
        APIException,
        Auth0Exception
    {

        // Loguea los datos de consulta.
        logger.info( // logger.debug
            String.format(
                "getClassEvent(dateOfEvent: %s)",
                dateOfEvent
            )
        );

        // Valida el atributo. Arroja una excepción si hubo
        // una validación no exitosa.452

        Validator validator = new Validator();
        var attributes = new HashMap<String, String>();
        attributes.put("email", dateOfEvent);
        validator.validateIfAnyNull(attributes)
            .validateDateFormat(dateOfEvent);
        
        // Intenta insertar el registro del docente en la tabla.
        // Arroja una excepción si no fue posible.
        var atributos = new ArrayList<Object>();
        atributos.add(dateOfEvent);
        ResultSet resultSet = DatabaseHandler
            .getInstance()
            .executeQuery(
                "SELECT *" +
                    " FROM Evento_Cursada" +
                    " WHERE DATE(fecha_hora_inicio) = " + dateOfEvent +
                    " AND idEventoCursada = " + Integer.toString(idClassEvent)
            );

        // Todo salió OK; se devuelven los eventos 
        return null; // TODO: devolver 
    }


    /* Private */

    private static final Logger logger = LogManager.getLogger(ProfessorService.class);
}
