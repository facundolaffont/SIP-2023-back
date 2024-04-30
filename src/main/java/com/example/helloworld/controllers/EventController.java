package com.example.helloworld.controllers;

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
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.helloworld.models.CourseEvent;
import com.example.helloworld.models.ErrorHandler;
import com.example.helloworld.models.Exceptions.EmptyQueryException;
import com.example.helloworld.models.Exceptions.NotAuthorizedException;
import com.example.helloworld.models.Exceptions.NotValidAttributeException;
import com.example.helloworld.models.Exceptions.NullAttributeException;
import com.example.helloworld.requests.AttendanceRegistrationOnEvent_Request;
import com.example.helloworld.requests.CalificationsRegistrationOnEvent_Request;
import com.example.helloworld.requests.EventsRegistrationCheckRequest;
import com.example.helloworld.requests.NewCourseEventRequest;
import com.example.helloworld.requests.NewEventsBulkRequest;
import com.example.helloworld.services.CourseEventService;
import com.example.helloworld.services.CourseService;

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
    
    @PostMapping("/create")
    public ResponseEntity<Object> create(@RequestBody NewCourseEventRequest newCourseEventRequest)
        throws NullAttributeException, SQLException, NotValidAttributeException
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

    @GetMapping("/get-event-info")
    public ResponseEntity<Object> getEventInfo(
        @RequestParam("event-id") Long eventId
    ) {

        logger.info("GET /api/v1/events/get-event-info");
        logger.debug(
            String.format(
                "Se ejecuta el método getEventInfo. [eventId = %s]",
                eventId.toString()
            )
        );

        try {

            return ResponseEntity
            .status(HttpStatus.OK)
            .body(courseEventService.getEventInfo(
                eventId
            ));

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

    
    /* Private */

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);
    private final CourseEventService courseEventService;
    private final CourseService courseService;

}

