package com.example.helloworld.controllers;

import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.helloworld.models.ErrorHandler;
import com.example.helloworld.requests.CalificationsRegistrationOnEvent_Request;
import com.example.helloworld.services.ClassEventService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
public class EventController {
    
    /**
     * Permite agregar calificaciones a un evento de instancia de evaluación.
     * 
     * @param calificationsRegistrationRequest
     * @return
     */
    @PostMapping("/add-califications-on-event")
    //@PreAuthorize("hasAuthority('docente')")
    //@CrossOrigin(origins = "http://localhost:4040")
    @CrossOrigin(origins = "*") // DEBUG: para hacer peticiones sin problemas con CORS.
    public Object addCalifications(@RequestBody CalificationsRegistrationOnEvent_Request calificationsRegistrationOnEvent_Request) {
        logger.info("POST /api/v1/events/add-califications-on-event");
        logger.info(calificationsRegistrationOnEvent_Request); // logger.debug

        try {
            classEventService.registerCalificationsOnEvent(
                calificationsRegistrationOnEvent_Request
            );
        }
        catch (SQLException e) {
            return ErrorHandler.returnError(e);
        }

        return null; // DEBUG.
    }


    /* Private */

    private static final Logger logger = LogManager.getLogger(UserController.class);
    private final ClassEventService classEventService;
}
