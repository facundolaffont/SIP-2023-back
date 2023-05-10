package com.example.helloworld.controllers;

import java.sql.SQLException;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.helloworld.ProfessorRequest;
import com.example.helloworld.models.Professor;
import com.example.helloworld.services.ProfessorService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/professors")
public class ProfessorController {
    private final ProfessorService service;

    @PostMapping("/add")
    @CrossOrigin(origins = "*")
    public Professor add(@RequestBody ProfessorRequest professorRequest) throws SQLException {
        // Recuperar datos del body...
        // Si hay algún dato faltante o inválido, error 400...
        System.out.println("add");
        return service.create(professorRequest.getEmail(), professorRequest.getNombre(), 
        professorRequest.getApellido(), professorRequest.getLegajo(), professorRequest.getPassword(), professorRequest.getRol());    
    }
}
