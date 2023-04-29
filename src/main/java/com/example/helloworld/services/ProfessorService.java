package com.example.helloworld.services;

import org.springframework.stereotype.Service;

import com.example.helloworld.models.Professor;

@Service
public class ProfessorService {
    public Professor create(long id, String email, String first_name, String last_name, int legajo, String password, String role) {
        // crear profesor y guardar en la BD.  si falla, aborta.
        // si ok: petición a Auth0 para crear el usuario.    si falla, rollback de lo anterior.
        // si ok: return Professor.
        return new Professor(id, email, first_name, last_name, legajo);
    }
}
