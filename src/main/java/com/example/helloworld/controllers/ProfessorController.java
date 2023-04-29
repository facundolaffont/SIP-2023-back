package com.example.helloworld.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.helloworld.models.Professor;
import com.example.helloworld.services.ProfessorService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/professors")
public class ProfessorController {
    private final ProfessorService service;

    @PostMapping("/add")
    @PreAuthorize("permitAll()")
    public Professor add() {
        // Recuperar datos del body...
        // Si hay algún dato faltante o inválido, error 400...
        return service.create(1, "franco@example.com", "franco", "parzanese", 149112, "12345678", "admin");
    }
}
