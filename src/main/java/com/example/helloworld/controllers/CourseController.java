package com.example.helloworld.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.helloworld.models.CourseDto;
import com.example.helloworld.models.ErrorHandler;
import com.example.helloworld.models.Exceptions.EmptyQueryException;
import com.example.helloworld.models.Exceptions.NotAuthorizedException;
import com.example.helloworld.models.Exceptions.NotValidAttributeException;
import com.example.helloworld.models.Exceptions.NullAttributeException;
import com.example.helloworld.requests.AttendanceRegistrationRequest;
import com.example.helloworld.requests.CourseAndDossiersListRequest;
import com.example.helloworld.requests.StudentsRegistrationRequest;
import com.example.helloworld.requests.DossiersAndEventRequest;
import com.example.helloworld.services.CourseEventService;
import com.example.helloworld.services.CourseService;
import com.example.helloworld.services.StudentService;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/course")
public class CourseController {

    // @PostMapping("/check-dossiers-in-course")
    // public ResponseEntity<Object> checkDossiersInCourse(
    // @RequestBody CourseAndDossiersListRequest courseAndDossiersListRequest
    // ) {

    // logger.info("POST /api/v1/course/check-dossiers-in-course");
    // logger.debug(
    // "Se ejecuta el método checkDossiersInCourse. [courseAndDossiersListRequest =
    // %s]".formatted(
    // courseAndDossiersListRequest.toString()
    // )
    // );

    // // try {
    // // return ResponseEntity
    // // .status(HttpStatus.OK)
    // // .body(
    // //
    // studentService.checkInCourseStudentsRegistration(courseAndDossiersListRequest)
    // // );
    // // }
    // // catch (EmptyQueryException e) {
    // // return ResponseEntity
    // // .status(HttpStatus.NOT_FOUND)
    // // .body(ErrorHandler.returnErrorAsJson(e));
    // // }

    // }

    @PostMapping(path = "/check-dossiers-in-event", produces = "application/json")
    public ResponseEntity<Object> checkDossiersInEvent(
        @RequestBody DossiersAndEventRequest dossiersAndEventRequest,
        @RequestHeader("Authorization") String authorizationHeader
    ) {

        logger.debug(
            "Se ejecuta el método checkDossiersInEvent. [dossiersAndEventRequest = %s]"
                .formatted(dossiersAndEventRequest));
        logger.info("POST /api/v1/course/check-dossiers-in-event");

        try {
            
            // Verifica que el evento exista; si no, arroja una excepción.
            courseEventService.checkIfEventExists(dossiersAndEventRequest.getEventId());

            // Extrae el ID de usuario del JWT.
            String token = authorizationHeader.substring(7);
            DecodedJWT decodedJwt = JWT.decode(token);
            String userId = decodedJwt.getSubject();
            
            // Verifica si el docente pertenece a la cursada del evento.
            courseService.checkProfessorInCourseFromEvent(userId, dossiersAndEventRequest.getEventId());

            // Devuelve una lista de legajos con información sobre su condición frente a un evento.
            return ResponseEntity
                .status(HttpStatus.OK)
                .body(courseService.checkDossiersInEvent(dossiersAndEventRequest));

        } catch (NotAuthorizedException e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.FORBIDDEN, e, 2);
        } catch (EmptyQueryException e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.NOT_FOUND, e, 1);
        }

    }

    @GetMapping(path = "/get-events", produces = "application/json")
    public ResponseEntity<Object> getEvents(
            @RequestParam("course-id") Long courseId,
            @RequestHeader("Authorization") String authorizationHeader) {

        logger.debug(
                "Se ejecuta el método getEvents. [courseId = %s, authorizationHeader = %s]"
                        .formatted(courseId, authorizationHeader));
        logger.info("GET /api/v1/course/get-events");

        try {

            // Verifica que la cursada exista.
            courseService.checkIfCourseExists(courseId);

            // Extrae el ID de usuario del JWT.
            String token = authorizationHeader.substring(7);
            DecodedJWT decodedJwt = JWT.decode(token);
            String userId = decodedJwt.getSubject();

            // Verifica si el docente pertenece a la cursada.
            courseService.checkProfessorInCourse(userId, courseId);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(courseService.getEvents(courseId));
        } catch (NotAuthorizedException e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.FORBIDDEN, e, 2);
        } catch (EmptyQueryException e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.NOT_FOUND, e, 1);
        }

    }

    @GetMapping(path = "/getProfessorCourses", produces = "application/json")
    public ResponseEntity<Object> getProfessorCourses(
            @RequestHeader("Authorization") String authorizationHeader)
            throws SQLException {

        logger.debug(
                "Se ejecuta el método get. [authorizationHeader = %s]"
                        .formatted(authorizationHeader));
        logger.info("GET /api/v1/course/getProfessorCourses");

        // Extrae el ID de usuario del JWT.
        String token = authorizationHeader.substring(7);
        DecodedJWT decodedJWT = JWT.decode(token);
        String userId = decodedJWT.getSubject();

        try {

            // Obtiene la información de las cursadas.
            List<CourseDto> cursadas = courseService.getProfessorCourses(userId);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(cursadas);

        } catch (EmptyQueryException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ErrorHandler.returnErrorAsJson(e));
        }

    }

    @GetMapping(path = "/finalCondition", produces = "application/json")
    public ResponseEntity<Object> getFinalCondition(
            @RequestParam("courseId") long courseId)
            throws NullAttributeException,
            SQLException,
            NotValidAttributeException {

        logger.debug(String.format(
                "Se ejecuta el método getFinalCondition. [courseId = %d]",
                courseId));
        logger.info("GET /api/v1/course/finalCondition");

        try {
            return courseService.calculateFinalCondition(courseId);
        } catch (EmptyQueryException e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e, -1);
        }

    }

    @PostMapping("/register-attendance")
    public ResponseEntity<Object> registerAttendance(
        @RequestBody AttendanceRegistrationRequest attendanceRegistrationRequest
    ) {

        logger.info("POST /api/v1/course/register-attendance");
        logger.debug(
            "Se ejecuta el método registerAttendance. [attendanceRegistrationRequest = %s]".formatted(
                attendanceRegistrationRequest.toString()));

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(courseService.registerAttendance(attendanceRegistrationRequest));

    }

    @PostMapping("/register-students")
    public ResponseEntity<Object> registerStudents(
            @RequestBody StudentsRegistrationRequest studentsRegistrationRequest) {

        logger.info("POST /api/v1/course/register-students");
        logger.debug(
                "Se ejecuta el método registerStudents. [studentsRegistrationRequest = %s]".formatted(
                        studentsRegistrationRequest.toString()));

        try {
            var result = courseService.registerStudents(studentsRegistrationRequest);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(result);
        } catch (EmptyQueryException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ErrorHandler.returnErrorAsJson(e));
        }

    }

    @PostMapping("/students-registration-check")
    public ResponseEntity<Object> studentsRegistrationCheck(
            @RequestBody CourseAndDossiersListRequest courseAndDossiersListRequest) {

        logger.info("POST /api/v1/course/students-registration-check");
        logger.debug(
                "Se ejecuta el método studentsRegistrationCheck. [courseAndDossiersListRequest = %s]".formatted(
                        courseAndDossiersListRequest.toString()));

        try {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(
                            studentService.checkInCourseStudentsRegistration(courseAndDossiersListRequest));
        } catch (EmptyQueryException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ErrorHandler.returnErrorAsJson(e));
        }

    }

    /* Private */

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);
    private final CourseEventService courseEventService;
    private final CourseService courseService;
    private final StudentService studentService;

}
