package com.example.helloworld.controllers;

import java.sql.SQLException;
import java.util.List;
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
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.helloworld.models.CourseDto;
import com.example.helloworld.models.ErrorHandler;
import com.example.helloworld.models.Exceptions.EmptyQueryException;
import com.example.helloworld.models.Exceptions.NotValidAttributeException;
import com.example.helloworld.models.Exceptions.NullAttributeException;
import com.example.helloworld.requests.CheckStudentsRegistrationStatusRequest;
import com.example.helloworld.requests.StudentsRegistrationCheckRequest;
import com.example.helloworld.requests.StudentsRegistrationRequest;
import com.example.helloworld.services.CourseService;
import com.example.helloworld.services.StudentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/course")
public class CourseController {
    
    @GetMapping(
        path="/getProfessorCourses",
        produces="application/json"
    )
    public ResponseEntity<Object> getProfessorCourses(
        @RequestHeader("Authorization") String authorizationHeader
    )
        throws SQLException
    {

        logger.debug(
            "Se ejecuta el método get. [authorizationHeader = %s]"
            .formatted(authorizationHeader)
        );
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

        }
        catch (EmptyQueryException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorHandler.returnErrorAsJson(e));
        }

    }

    @GetMapping(
        path="/finalCondition",
        produces="application/json"
    )
    public ResponseEntity<String> getFinalCondition(
        @RequestParam("courseId") long courseId
    )
        throws
            NullAttributeException,
            SQLException,
            NotValidAttributeException 
    {

        logger.debug(String.format(
            "Se ejecuta el método getFinalCondition. [courseId = %d]",
            courseId
        ));
        logger.info("GET /api/v1/course/finalCondition");

        try {
            return courseService.calculateFinalCondition(courseId);
        }
        catch (EmptyQueryException e) {
            return ErrorHandler.returnErrorAsResponseEntity(e);
        }

    }
    // /**
    //  * Devuelve información que ayuda a determinar si cada estudiante de la lista
    //  * está registrado actualmente en el sistema, o no, y si está vinculado o no a la
    //  * comisión.
    //  *  
    //  * @param checkStudentsRegistrationStatusRequest Lista de estudiantes por los cuales consultar, junto con la
    //  * comisión.
    //  * @return La lista de legajos sin registrar en sistema y/o los que están registrados en sistema pero no en
    //  * la cursada y/o los que están registrados en sistema y en la cursada.
    //  */
    // @PostMapping("/check-students-registration-status")
    // public ResponseEntity<String> checkStudentsRegistrationStatus(@RequestBody CheckStudentsRegistrationStatusRequest checkStudentsRegistrationStatusRequest) {

    //     logger.info("POST /api/v1/course/check-students-registration-status");
    //     logger.debug(
    //         "Se ejecuta el método checkStudentsRegistrationStatus. [checkStudentsRegistrationStatusRequest = %s]".formatted(
    //             checkStudentsRegistrationStatusRequest.toString()
    //         )
    //     );

    //     return courseService.getStudentsRegistrationStatus(checkStudentsRegistrationStatusRequest);

    // }

    @PostMapping("/register-students")
    public ResponseEntity<Object> registerStudents(@RequestBody StudentsRegistrationRequest studentsRegistrationRequest) {

        logger.info("POST /api/v1/course/register-students");
        logger.debug(
            "Se ejecuta el método registerStudents. [studentsRegistrationRequest = %s]".formatted(
                studentsRegistrationRequest.toString()
            )
        );

        try {
            var result = courseService.registerStudents(studentsRegistrationRequest);
            return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
        }
        catch (EmptyQueryException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorHandler.returnErrorAsJson(e));
        }

    }
    
    @PostMapping("/students-registration-check")
    public ResponseEntity<Object> studentsRegistrationCheck(
        @RequestBody StudentsRegistrationCheckRequest studentsRegistrationCheckRequest
    ) {

        logger.info("POST /api/v1/course/students-registration-check");
        logger.debug(
            "Se ejecuta el método studentsRegistrationCheck. [studentsRegistrationCheckRequest = %s]".formatted(
                studentsRegistrationCheckRequest.toString()
            )
        );

        try {
            return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                    studentService.checkInCourseStudentsRegistration(studentsRegistrationCheckRequest)
                );
        }
        catch (EmptyQueryException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorHandler.returnErrorAsJson(e));
        }

    }


    /* Private */

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);
    private final CourseService courseService;
    private final StudentService studentService;
    
}
