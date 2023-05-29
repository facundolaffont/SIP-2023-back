package com.example.helloworld.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.example.helloworld.models.DatabaseHandler;
import com.example.helloworld.models.Event;
import com.example.helloworld.models.Validator;
import com.example.helloworld.models.Exceptions.NotValidAttributeException;
import com.example.helloworld.models.Exceptions.NullAttributeException;

@Service
public class EventService {

    public Event create(int id, String tipo, String fecha_inicio, String fecha_fin) throws NullAttributeException, SQLException, NotValidAttributeException {
        
        // Loguea los datos que se quieren insertar.
        logger.info(
            String.format(
                "create(id: %s, tipo: %s, fecha_inicio: %s, fecha_fin: %s)",
                id,
                tipo,
                fecha_inicio,
                fecha_fin
            )
        );

        // Valida los atributos. Arroja una excepción si hubo
        // una validación no exitosa.
        Validator validator = new Validator();
        var attributes = new HashMap<String, String>();
        attributes.put("tipo", tipo);
        attributes.put("fecha_inicio", fecha_inicio);
        attributes.put("fecha_fin", fecha_fin);
        validator.validateIfAnyNull(attributes);
        
        // Todo: verifica si docente existe en la BD.
        
        // Intenta insertar el registro del docente en la tabla.
        // Arroja una excepción si no fue posible.
        var atributos = new ArrayList<Object>();
        atributos.add(id);
        atributos.add(tipo);
        atributos.add(fecha_inicio);
        atributos.add(fecha_fin);
        DatabaseHandler
            .getInstance()
            .executeStatement(
                "INSERT" +
                    " INTO evento (id, tipo, fecha_inicio, fecha_fin)" +
                    " VALUES (?, ?, ?, ?)",
                atributos
            );

        
        
        
        
        return null;
    }
    

    /* Private */

    private static final Logger logger = LoggerFactory.getLogger(ClassEventService.class);

}
