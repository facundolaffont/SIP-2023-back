package com.example.helloworld.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.helloworld.requests.NewStudentRequest;
import com.example.helloworld.services.StudentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/students")
@CrossOrigin(origins = "https://spgda.fl.com.ar/")
public class StudentController {
    
    @PostMapping("/create")
    //@PreAuthorize("hasAuthority('docente')")
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


    /* Private */

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);
    private final StudentService studentService;

}