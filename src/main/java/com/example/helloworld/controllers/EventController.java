package com.example.helloworld.controllers;

import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.helloworld.models.Event;
import com.example.helloworld.models.Exceptions.NotValidAttributeException;
import com.example.helloworld.models.Exceptions.NullAttributeException;
import com.example.helloworld.requests.NewEventRequest;
import com.example.helloworld.services.EventService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/event")
public class EventController {
    
    @PostMapping("/add")
    //@PreAuthorize("hasAuthority('admin')")
    //@CrossOrigin(origins = "http://localhost:4040")
    @CrossOrigin(origins = "*") // DEBUG: para hacer peticiones sin problemas con CORS.
    public Object add(@RequestBody NewEventRequest newEventRequest) throws NullAttributeException, SQLException, NotValidAttributeException 
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

    private static final Logger logger = LogManager.getLogger(UserController.class);
    private final EventService eventService;
    
}
