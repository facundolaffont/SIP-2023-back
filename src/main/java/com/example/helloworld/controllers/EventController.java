package com.example.helloworld.controllers;

import java.sql.SQLException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.helloworld.models.ErrorHandler;
import com.example.helloworld.models.Event;
import com.example.helloworld.models.Exceptions.NotValidAttributeException;
import com.example.helloworld.models.Exceptions.NullAttributeException;
import com.example.helloworld.requests.CalificationsRegistrationOnEvent_Request;
import com.example.helloworld.requests.NewEventRequest;
import com.example.helloworld.services.ClassEventService;
import com.example.helloworld.services.EventService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
public class EventController {
    
    @PostMapping("/add-califications-on-event")
    //@PreAuthorize("hasAuthority('docente')")
    //@CrossOrigin(origins = "http://localhost:4040")
    @CrossOrigin(origins = "*") // DEBUG: para hacer peticiones sin problemas con CORS.
    public ResponseEntity<String> addCalificationsOnEvent(@RequestBody CalificationsRegistrationOnEvent_Request calificationsRegistrationOnEvent_Request) {

        logger.info("POST /api/v1/events/add-califications-on-event");
        logger.debug(
            String.format(
                "Se ejecuta el método addCalificationsOnEvent. [calificationsRegistrationOnEvent_Request = %s]",
                calificationsRegistrationOnEvent_Request.toString()
            )
        );

        try {
            classEventService.registerCalificationsOnEvent(
                calificationsRegistrationOnEvent_Request
            );
        }
        catch (SQLException e) {
            return ErrorHandler.returnErrorAsResponseEntity(e);
        }

        var returningJson = (new JSONObject())
            .append("Respuesta", "OK");
        var statusCode = HttpStatus.OK;

        return ResponseEntity
            .status(statusCode)
            .body(
                returningJson.toString()
            );
    }

    @PostMapping("/add")
    //@PreAuthorize("hasAuthority('admin')")
    //@CrossOrigin(origins = "http://localhost:4040")
    @CrossOrigin(origins = "*") // DEBUG: para hacer peticiones sin problemas con CORS.
    public Event add(@RequestBody NewEventRequest newEventRequest) throws NullAttributeException, SQLException, NotValidAttributeException 
    {

        logger.info("POST /api/v1/event/add");

        // Se quiere dar de alta un docente.

        Event newEvent = eventService.create(
            newEventRequest.getId(),
            newEventRequest.getTipo(),
            newEventRequest.getFecha_inicio(),
            newEventRequest.getFecha_fin()
        );

        return newEvent;
    }


    /* Private */

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);
    private final ClassEventService classEventService;
    private final EventService eventService;

}

