package com.example.helloworld.controllers;

import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.helloworld.models.ErrorHandler;
import com.example.helloworld.models.Exceptions.NotValidAttributeException;
import com.example.helloworld.models.Exceptions.NullAttributeException;
import com.example.helloworld.requests.CalificationsRegistrationOnEvent_Request;
import com.example.helloworld.requests.NewCourseEventRequest;
import com.example.helloworld.services.ClassEventService;
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
            return classEventService.registerCalificationsOnEvent(
                calificationsRegistrationOnEvent_Request
            );
        }
        catch (SQLException e) {
            return ErrorHandler.returnErrorAsResponseEntity(e);
        }
    }

    @PostMapping("/create")
    //@PreAuthorize("hasAuthority('admin')")
    //@CrossOrigin(origins = "http://localhost:4040")
    @CrossOrigin(origins = "*") // DEBUG: para hacer peticiones sin problemas con CORS.
    public ResponseEntity<String> create(@RequestBody NewCourseEventRequest newCourseEventRequest)
        throws NullAttributeException, SQLException, NotValidAttributeException
    {

        logger.info("POST /api/v1/event/add");
        logger.debug(String.format(
            "Se ejecuta el método add. [newEventRequest = %s]",
            newCourseEventRequest
        ));

        return classEventService.create(newCourseEventRequest);

    }


    /* Private */

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);
    private final ClassEventService classEventService;

}

