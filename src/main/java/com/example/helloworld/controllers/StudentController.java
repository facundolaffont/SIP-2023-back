package com.example.helloworld.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.helloworld.requests.NewDossiersCheckRequest;
import com.example.helloworld.requests.NewStudentRequest;
import com.example.helloworld.requests.NewStudentsRequest;
import com.example.helloworld.services.StudentService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/students")
@CrossOrigin(origins = "https://spgda.fl.com.ar/")
public class StudentController {
    
    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody NewStudentRequest newStudentRequest) {

        logger.info("POST /api/v1/students/create");
        logger.debug(
            String.format(
                "Se ejecuta el método create. [newStudentRequest = %s]",
                newStudentRequest.toString()
            )
        );

        return studentService.create(newStudentRequest);

    }

    @PostMapping("/new-dossiers-check")
    public ResponseEntity<Object> newDossiersCheck(@RequestBody NewDossiersCheckRequest newDossiersCheckRequest) {

        logger.info("POST /api/v1/students/new-dossiers-check");
        logger.debug(
            String.format(
                "Se ejecuta el método newDossiersCheck. [newDossiersCheckRequest = %s]",
                newDossiersCheckRequest.toString()
            )
        );

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(
                studentService.checkNewDossiersRegistration(newDossiersCheckRequest)
            );

    }

    @PostMapping("/register-students")
    public ResponseEntity<Object> registerStudents(@RequestBody NewStudentsRequest newStudentsRequest) {

        logger.info("POST /api/v1/students/register-students");
        logger.debug(
            String.format(
                "Se ejecuta el método registerStudents. [newStudentsRequest = %s]",
                newStudentsRequest.toString()
            )
        );

        Object result = studentService.registerStudents(newStudentsRequest);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(result);

    }


    /* Private */

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);
    private final StudentService studentService;

}
