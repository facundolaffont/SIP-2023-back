package ar.edu.unlu.spgda.controllers;

import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import ar.edu.unlu.spgda.models.CourseEvent;
import ar.edu.unlu.spgda.models.ErrorHandler;
import ar.edu.unlu.spgda.models.Exceptions.EmptyQueryException;
import ar.edu.unlu.spgda.models.Exceptions.NotAuthorizedException;
import ar.edu.unlu.spgda.models.Exceptions.NonValidAttributeException;
import ar.edu.unlu.spgda.models.Exceptions.NullAttributeException;
import ar.edu.unlu.spgda.models.Exceptions.OperationNotPermittedException;
import ar.edu.unlu.spgda.requests.AttendanceRegistrationOnEvent_Request;
import ar.edu.unlu.spgda.requests.CalificationsRegistrationOnEvent_Request;
import ar.edu.unlu.spgda.requests.DeleteEventRegisterRequest;
import ar.edu.unlu.spgda.requests.DeleteEventRequest;
import ar.edu.unlu.spgda.requests.EventsRegistrationCheckRequest;
import ar.edu.unlu.spgda.requests.NewCourseEventRequest;
import ar.edu.unlu.spgda.requests.NewEventsBulkRequest;
import ar.edu.unlu.spgda.requests.UpdateEventRegisterAttendanceRequest;
import ar.edu.unlu.spgda.requests.UpdateEventRegisterNoteRequest;
import ar.edu.unlu.spgda.requests.UpdateEventRequest;
import ar.edu.unlu.spgda.services.CourseEventService;
import ar.edu.unlu.spgda.services.CourseService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
@CrossOrigin(origins = "https://spgda.fl.com.ar/")
public class EventController {
    
    @PostMapping("/add-attendance-on-event")
    public ResponseEntity<Object> addAttendanceOnEvent(@RequestBody AttendanceRegistrationOnEvent_Request attendanceRegistrationOnEvent_Request) {

        logger.info("POST /api/v1/events/add-attendance-on-event");
        logger.debug(
            String.format(
                "Se ejecuta el método addAttendanceOnEvent. [attendanceRegistrationOnEvent_Request = %s]",
                attendanceRegistrationOnEvent_Request.toString()
            )
        );

        try {
            return courseEventService.registerAttendanceOnEvent(
                attendanceRegistrationOnEvent_Request
            );
        }
        catch (SQLException e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e, -1);
        }
    }

    @PostMapping("/add-califications-on-event")
    public ResponseEntity<Object> addCalificationsOnEvent(@RequestBody CalificationsRegistrationOnEvent_Request calificationsRegistrationOnEvent_Request) {

        logger.info("POST /api/v1/events/add-califications-on-event");
        logger.debug(
            String.format(
                "Se ejecuta el método addCalificationsOnEvent. [calificationsRegistrationOnEvent_Request = %s]",
                calificationsRegistrationOnEvent_Request.toString()
            )
        );

        try {
            return courseEventService.registerCalificationsOnEvent(
                calificationsRegistrationOnEvent_Request
            );
        }
        catch (SQLException e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e, -1);
        }
        
    }
    
    @GetMapping("/check-event-register-final-condition")
    public ResponseEntity<Object> checkEventRegisterFinalCondition(
        @RequestParam("event-register-id") Long eventRegisterId
    ) {

        logger.info("POST /api/v1/events/check-event-register-final-condition");
        logger.debug("Se ejecuta el método checkEventRegisterFinalCondition."
            
            + " [eventRegisterId = %s]"
            .formatted(
                eventRegisterId
            )
            
        );

        try {

            boolean result = courseService.checkEventRegisterFinalCondition(eventRegisterId);

            return ResponseEntity.status(HttpStatus.OK).body(result);

        }
        
        catch (EmptyQueryException e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.NOT_FOUND, e, 1);
        }
        
        catch (Exception e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e, -1);
        }
        
    }

    @PostMapping("/create")
    public ResponseEntity<Object> create(@RequestBody NewCourseEventRequest newCourseEventRequest)
        throws NullAttributeException, SQLException, NonValidAttributeException
    {

        logger.info("POST /api/v1/events/create");
        logger.debug(String.format(
            "Se ejecutó el método create. [newCourseEventRequest = %s]",
            newCourseEventRequest
        ));

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(courseEventService.create(newCourseEventRequest));

    }

    @PostMapping("/create-events-bulk")
    public ResponseEntity<Object> createEventsBulk(
        @RequestBody NewEventsBulkRequest newEventsBulkRequest,
        @RequestHeader("Authorization") String authorizationHeader
    ) {

        logger.info("POST /api/v1/events/create-events-bulk");
        logger.debug(String.format(
            "Se ejecutó el método create-events-bulk. [newEventsBulkRequest = %s]",
            newEventsBulkRequest
        ));

        try {
        
            // Verifica que el evento exista; si no, arroja una excepción.
            courseService.checkIfCourseExists(newEventsBulkRequest.getCourseId());

            // Extrae el ID de usuario del JWT.
            String token = authorizationHeader.substring(7);
            DecodedJWT decodedJwt = JWT.decode(token);
            String userId = decodedJwt.getSubject();
            
            // Verifica si el docente pertenece a la cursada.
            courseService.checkProfessorInCourse(userId, newEventsBulkRequest.getCourseId());

            return ResponseEntity
                .status(HttpStatus.OK)
                .body(courseEventService.createEventsBulk(newEventsBulkRequest));

        } catch (NotAuthorizedException e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.FORBIDDEN, e, 2);
        } catch (EmptyQueryException e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.NOT_FOUND, e, 1);
        } catch (Exception e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e, -1);
        }

    }

    @PostMapping("/delete-event")
    @ResponseBody
    public ResponseEntity<Object> deleteEvent(@RequestBody DeleteEventRequest deleteEventRequest) {
        logger.info("POST /api/v1/events/delete-event");
        logger.debug("Se ejecuta el método deleteEvent. [deleteEventRequest = %s]");

        String message = courseService.deleteEvent(deleteEventRequest);

        if (message.equals("El evento se ha eliminado correctamente.")) {
            return ResponseEntity.status(HttpStatus.OK).body("{\"success\": true, \"message\": \"" + message + "\"}");
        } else if (message.equals("No se puede eliminar el evento porque tiene registros asociados.")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"success\": false, \"message\": \"" + message + "\"}");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"success\": false, \"message\": \"Hubo un error al eliminar el evento.\"}");
        }
    }

    @PostMapping("/delete-event-register")
    @ResponseBody
    public ResponseEntity<Object> deleteEventRegister(@RequestBody DeleteEventRegisterRequest deleteEventRegisterRequest) {
        
        logger.info("POST /api/v1/events/delete-event-register");
        logger.debug("Se ejecuta el método deleteEventRegister."

            + " [deleteEventRegisterRequest = %s]"
            .formatted(
                deleteEventRegisterRequest.toString()
            )

        );

        try {
            
            courseService.deleteEventRegister(deleteEventRegisterRequest);

            return ResponseEntity.status(HttpStatus.OK).build();

        }
        
        // Si el ID de registro de evento no existe, se devuelve 1 como código de error.
        catch (EmptyQueryException e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.NOT_FOUND, e, 1);
        }

        // Si el legajo tiene registrada una condición final, se devuelve 2 como código de error.
        catch (OperationNotPermittedException e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.FORBIDDEN, e, 2);
        }
        
        // Por cualquier otro error, se devuelve -1 como código de error.
        catch (Exception e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e, -1);
        }

    }

    @PostMapping("/events-registration-check")
    public ResponseEntity<Object> eventsRegistrationCheck(
        @RequestBody EventsRegistrationCheckRequest eventsRegistrationCheckRequest,
        @RequestHeader("Authorization") String authorizationHeader
    ) {

        logger.info("POST /api/v1/events/events-registration-check");
        logger.debug(
            String.format(
                "Se ejecuta el método eventsRegistrationCheck. [eventsRegistrationCheckRequest = %s]",
                eventsRegistrationCheckRequest.toString()
            )
        );

        try {

            // Verifica que el evento exista; si no, arroja una excepción.
            courseService.checkIfCourseExists(eventsRegistrationCheckRequest.getCourseId());

            // Extrae el ID de usuario del JWT.
            String token = authorizationHeader.substring(7);
            DecodedJWT decodedJwt = JWT.decode(token);
            String userId = decodedJwt.getSubject();
            
            // Verifica si el docente pertenece a la cursada.
            courseService.checkProfessorInCourse(userId, eventsRegistrationCheckRequest.getCourseId());

            return ResponseEntity
                .status(HttpStatus.OK)
                .body(courseEventService.eventsRegistrationCheck(
                    eventsRegistrationCheckRequest
                ));

        } catch (NotAuthorizedException e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.FORBIDDEN, e, 2);
        } catch (EmptyQueryException e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.NOT_FOUND, e, 1);
        } catch (Exception e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e, -1);
        }
        
    }

    @GetMapping("/get")
    public List<CourseEvent> getEventsByDate(@RequestParam("date") String date) {
        // Llama al servicio para obtener los eventos de la cursada correspondientes a la fecha
        return courseEventService.getEvents(date);
    }

    @GetMapping("/get-event-info")
    public ResponseEntity<Object> getEventInfo(
        @RequestParam("event-id") Long eventId
    ) {

        logger.info("GET /api/v1/events/get-event-info");
        logger.debug("Se ejecuta el método getEventInfo."

            + " [eventId = %s]"
                .formatted(
                    eventId.toString()
                )

        );

        try {

            Object result = courseEventService.getEventInfo(eventId);

            return ResponseEntity.status(HttpStatus.OK).body(result);

        }
        
        catch (EmptyQueryException e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.NOT_FOUND, e, 1);
        }
        
        catch (Exception e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e, -1);
        }

    }

    @GetMapping("/get-event-types")
    public ResponseEntity<Object> getEventTypes() {

        logger.info("GET /api/v1/events/get-event-types");
        logger.debug("Se ejecuta el método getEventTypes.");

        try {

            return ResponseEntity
            .status(HttpStatus.OK)
            .body(courseEventService.getEventTypes());

        } catch (Exception e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e, -1);
        }

    }

    @GetMapping("/get-events-details")
    public ResponseEntity<Object> getEventsDetails(
        @RequestParam("course-id") Long courseId
    ) {

        logger.info("GET /api/v1/events/get-events-details");
        logger.debug(
            String.format(
                "Se ejecuta el método getEventsDetails. [courseId = %s]"
                , courseId.toString()
            )
        );

        try {

            return ResponseEntity
            .status(HttpStatus.OK)
            .body(courseEventService.getAllEventsInfo(
                courseId
            ));

        } catch (EmptyQueryException e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.NOT_FOUND, e, 1);
        } catch (Exception e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e, -1);
        }

    }

    @PostMapping("/update-event")
    public ResponseEntity<Object> updateEvent(
            @RequestBody UpdateEventRequest updateEventRequest) {

        logger.info("POST /api/v1/events/update-event");
        logger.debug("Se ejecuta el método updateEvent. [updateEventRequest = %s]");
        
        boolean success = courseService.updateEvent(updateEventRequest);

        if (success) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/update-event-register-attendance")
    public ResponseEntity<Object> updateEventRegisterAttendance(
            @RequestBody UpdateEventRegisterAttendanceRequest updateEventRegisterAttendanceRequest) {

        logger.info("POST /api/v1/events/update-event-register-attendance");
        logger.debug("Se ejecuta el método updateEventRegisterAttendance."

                + " [UpdateEventRegisterAttendanceRequest = %s]"
                .formatted(
                    updateEventRegisterAttendanceRequest.toString()
                )
            );
        
        try { // G1.B
            
            courseService.updateEventRegisterAttendance(updateEventRegisterAttendanceRequest);

            return ResponseEntity.status(HttpStatus.OK).build();

        }
        
        // Si el ID de registro de evento no existe, se devuelve 1 como código de error.
        catch (EmptyQueryException e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.NOT_FOUND, e, 1);
        }

        // Si el legajo tiene registrada una condición final, se devuelve 2 como código de error.
        catch (OperationNotPermittedException e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.FORBIDDEN, e, 2);
        }
        
        // Por cualquier otro error, se devuelve -1 como código de error.
        catch (Exception e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e, -1);
        }

    }

    @PostMapping("/update-event-register-note")
    public ResponseEntity<Object> updateEventRegisterNote(
            @RequestBody UpdateEventRegisterNoteRequest updateEventRegisterNoteRequest) {

        logger.info("POST /api/v1/events/update-event-register-note");
        logger.debug("Se ejecuta el método updateEventRegisterNote."

                + " [UpdateEventRegisterNoteRequest = %s]"
                .formatted(
                    updateEventRegisterNoteRequest.toString()
                )

            );
        
        try {
            
            courseService.updateEventRegisterNote(updateEventRegisterNoteRequest);

            return ResponseEntity.status(HttpStatus.OK).build();

        }
        
        // Si el ID de registro de evento no existe, se devuelve 1 como código de error.
        catch (EmptyQueryException e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.NOT_FOUND, e, 1);
        }

        // Si el legajo tiene registrada una condición final, se devuelve 2 como código de error.
        catch (OperationNotPermittedException e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.FORBIDDEN, e, 2);
        }
        
        // Por cualquier otro error, se devuelve -1 como código de error.
        catch (Exception e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e, -1);
        }
    }


    /* Private */

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);
    private final CourseEventService courseEventService;
    private final CourseService courseService;

}

