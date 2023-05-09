package com.example.helloworld.controllers;

import java.sql.SQLException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.helloworld.ProfessorRequest;
import com.example.helloworld.models.Professor;
import com.example.helloworld.services.ProfessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2 // Agregar un logger llamado log.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/professors")
public class ProfessorController {
    private final ProfessorService service;

    // Recuperar datos del body...
    // Si hay algún dato faltante o inválido, error 400...
    @PostMapping("/add")
    @CrossOrigin(origins = "http://localhost:4040")
    public Professor add(@RequestBody ProfessorRequest professorRequest) throws SQLException {
        log.debug("POST /api/v1/professors/add");

        return service.create(
            professorRequest.getEmail(),
            professorRequest.getNombre(), 
            professorRequest.getApellido(),
            professorRequest.getLegajo(),
            professorRequest.getPassword(),
            professorRequest.getRol()
        );
    }
}
