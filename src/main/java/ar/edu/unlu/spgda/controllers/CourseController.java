package ar.edu.unlu.spgda.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import ar.edu.unlu.spgda.models.CourseDto;
import ar.edu.unlu.spgda.models.ErrorHandler;
import ar.edu.unlu.spgda.models.Exceptions.EmptyQueryException;
import ar.edu.unlu.spgda.models.Exceptions.NotAuthorizedException;
import ar.edu.unlu.spgda.models.Exceptions.NonValidAttributeException;
import ar.edu.unlu.spgda.models.Exceptions.NullAttributeException;
import ar.edu.unlu.spgda.requests.AttendanceRegistrationRequest;
import ar.edu.unlu.spgda.requests.CalificationRegistrationRequest;
import ar.edu.unlu.spgda.requests.CourseAndDossiersListRequest;
import ar.edu.unlu.spgda.requests.StudentsRegistrationRequest;
import ar.edu.unlu.spgda.requests.DossiersAndEventRequest;
import ar.edu.unlu.spgda.requests.FinalConditions;
import ar.edu.unlu.spgda.services.CourseEventService;
import ar.edu.unlu.spgda.services.CourseService;
import ar.edu.unlu.spgda.services.StudentService;
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

    @GetMapping(path = "/get-all-events", produces = "application/json")
    public ResponseEntity<Object> getAllEvents(
        @RequestParam("course-id") Long courseId,
        @RequestHeader("Authorization") String authorizationHeader)
    {

        logger.debug(
                "Se ejecutó el método getAllEvents. [courseId = %s, authorizationHeader = %s]"
                        .formatted(courseId, authorizationHeader));
        logger.info("GET /api/v1/course/get-all-events");

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
                    .body(courseService.getEvents(
                        courseId,
                        0, // Devuelve todos los eventos.
                        0 // N/A.
                    ));
        } catch (NotAuthorizedException e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.FORBIDDEN, e, 2);
        } catch (EmptyQueryException e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.NOT_FOUND, e, 1);
        }

    }

    @GetMapping(path = "/get-class-events", produces = "application/json")
    public ResponseEntity<Object> getClassEvents(
        @RequestParam("course-id") Long courseId,
        @RequestHeader("Authorization") String authorizationHeader)
    {

        logger.debug(
            "Se ejecutó el método getClassEvents. [courseId = %s, authorizationHeader = %s]"
                .formatted(courseId, authorizationHeader));
        logger.info("GET /api/v1/course/get-class-events");

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
                .body(courseService.getEvents(
                    courseId,
                    1, // Devuelve todos los eventos con el tipo de clase
                            // especificado a continuación.
                    1 // Eventos de clase.
                ));
                    
        } catch (NotAuthorizedException e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.FORBIDDEN, e, 2);
        } catch (EmptyQueryException e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.NOT_FOUND, e, 1);
        }

    }

    @GetMapping(path = "/get-evaluation-events", produces = "application/json")
    public ResponseEntity<Object> getEvaluationEvents(
            @RequestParam("course-id") Long courseId,
            @RequestHeader("Authorization") String authorizationHeader) {

        logger.debug(
                "Se ejecutó el método getEvaluationEvents. [courseId = %s, authorizationHeader = %s]"
                        .formatted(courseId, authorizationHeader));
        logger.info("GET /api/v1/course/get-evaluation-events");

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
                    .body(courseService.getEvents(
                        courseId,
                        2, // Devuelve todos los eventos,
                                // excepto los del tipo especificado a continuación.
                        1 // Eventos de clase.
                    ));
        } catch (NotAuthorizedException e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.FORBIDDEN, e, 2);
        } catch (EmptyQueryException e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.NOT_FOUND, e, 1);
        }

    }

    @GetMapping(path = "/condition", produces = "application/json")
    public ResponseEntity<Object> getCondition(
            @RequestParam("courseId") long courseId,
            @RequestParam("isFinal") boolean isFinal
    ) throws NullAttributeException, SQLException, NonValidAttributeException {

        logger.debug(String.format(
                "Se ejecuta el método getCondition. [courseId = %d, isFinal = %b]",
                courseId, isFinal
        ));
        logger.info("GET /api/v1/course/condition");

        try {
            // Le pasamos el flag para que el service decida la lógica
            return courseService.calculateFinalCondition(courseId, isFinal);

        } catch (EmptyQueryException e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e, -1);
        }
    }

    @GetMapping(path = "/get-professor-courses", produces = "application/json")
    public ResponseEntity<Object> getProfessorCourses(
            @RequestHeader("Authorization") String authorizationHeader)
            throws SQLException {

        logger.debug(
                "Se ejecuta el método get. [authorizationHeader = %s]"
                        .formatted(authorizationHeader));
        logger.info("GET /api/v1/course/get-professor-courses");

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

    @GetMapping(path = "/get-students", produces = "application/json")
    public ResponseEntity<Object> getStudents(
        @RequestParam("courseId") long courseId
    )
        throws NullAttributeException, SQLException, NonValidAttributeException, EmptyQueryException
    {

        logger.debug(String.format(
            "Se ejecuta el método get-students. [courseId = %d]",
            courseId
        ));
        logger.info("GET /api/v1/course/get-students");

        try {

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(courseService.getStudents(courseId));

        } catch (EmptyQueryException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorHandler.returnErrorAsJson(e));
        } catch (Exception e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e, -1);
        }

    }

    @GetMapping(path = "/getStudent", produces = "application/json")
    public ResponseEntity<Object> getStudentState(
            @RequestParam("courseId") long courseId, @RequestParam("dossier") int dossier)
            throws NullAttributeException,
            SQLException,
            NonValidAttributeException, EmptyQueryException {

        logger.debug(String.format(
                "Se ejecuta el método getStudentState. [courseId = %d, dossier = %s]",
                courseId, dossier));
        logger.info("GET /api/v1/course/getStudent");

        return courseService.getStudentState(courseId, dossier);
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

    @PostMapping("/register-califications")
    public ResponseEntity<Object> registerCalifications(
        @RequestBody CalificationRegistrationRequest calificationRegistrationRequest
    ) {

        logger.info("POST /api/v1/course/register-califications");
        logger.debug(
            "Se ejecuta el método registerCalifications. [calificationRegistrationRequest = %s]".formatted(
                calificationRegistrationRequest.toString()));

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(courseService.registerCalification(calificationRegistrationRequest));

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

    @PostMapping("/saveFinalConditions")
    public ResponseEntity<Object> saveFinalConditions(
            @RequestBody FinalConditions finalConditions) {

        logger.info("POST /api/v1/course/saveFinalConditions");
        logger.debug(
                "Se ejecuta el método saveFinalConditions. [studentsRegistrationRequest = %s]"
                    );

        boolean success = courseService.saveFinalConditions(finalConditions);

        if (success) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/get-events-summary")
    public ResponseEntity<Object> getEventsSummary(
        @RequestParam("course-id") Long courseId
    ) {

        logger.info("POST /api/v1/course/get-events-summary");
        logger.debug(
            "Se ejecuta el método getEventsSummary. [course-id = %s]".formatted(courseId.toString())
        );

        try {

            return ResponseEntity
            .status(HttpStatus.OK)
            .body(courseService.getEventsSummary(courseId));

        } catch (Exception e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e, -1);
        }

    }

    @GetMapping("/get-criteria-summary")
    public ResponseEntity<Object> getCriteriaSummary(
        @RequestParam("course-id") Long courseId
    ) {

        logger.info("POST /api/v1/course/get-criteria-summary");
        logger.debug(
            "Se ejecuta el método getCriteriaSummary. [course-id = %s]".formatted(courseId.toString())
        );

        try {
            return ResponseEntity
            .status(HttpStatus.OK)
            .body(courseService.getCriteriaSummary(courseId));
        } catch (Exception e) {
            return ErrorHandler.returnErrorAsResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e, -1);
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
