package com.example.helloworld.controllers;

import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.helloworld.models.Professor;
import com.example.helloworld.requests.ProfessorRequest;
import com.example.helloworld.services.ProfessorService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/professors")
public class ProfessorController {
    
    @PostMapping("/add")
    //@PreAuthorize("hasAuthority('admin')")
    //@CrossOrigin(origins = "http://localhost:4040")
    @CrossOrigin(origins = "*") // DEBUG: para hacer peticiones sin problemas con CORS.
    public Professor add(@RequestBody ProfessorRequest professorRequest) throws SQLException {
        logger.info("POST /api/v1/professors/add");

        // Llama al método que inserta un registro de profesor en la BD.
        return service.create(
            professorRequest.getEmail(),
            professorRequest.getNombre(), 
            professorRequest.getApellido(),
            professorRequest.getLegajo(),
            professorRequest.getPassword(),
            professorRequest.getRol()
        );
    }


    /* Private */

    private static final Logger logger = LogManager.getLogger(ProfessorController.class);
    private final ProfessorService service;
}
