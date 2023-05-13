package com.example.helloworld.controllers;

import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.auth0.exception.APIException;
import com.auth0.exception.Auth0Exception;
import com.example.helloworld.models.Professor;
import com.example.helloworld.models.Exceptions.NotValidAttributeException;
import com.example.helloworld.models.Exceptions.NullAttributeException;
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
    public Object add(@RequestBody ProfessorRequest professorRequest) {
        logger.info("POST /api/v1/professors/add");

        // Llama al método que inserta un registro de profesor en la BD.
        Professor professor = null;
        try {
            professor = service.create(
                professorRequest.getEmail(),
                professorRequest.getNombre(), 
                professorRequest.getApellido(),
                professorRequest.getLegajo(),
                professorRequest.getPassword(),
                professorRequest.getRol()
            );
        }
        catch (APIException e) {
            logger.error(String.format(
                "%s: %s",
                e.getClass(),
                e.getMessage()
            )); 
            return new JSONObject()
                .append("Excepción", e.getClass())
                .append("Mensaje", e.getMessage())
                .toString();
        }
        catch (NotValidAttributeException | NullAttributeException | SQLException | Auth0Exception e) {
            logger.error(String.format(
                "%s: %s",
                e.getClass(),
                e.getMessage()
            ));
            return new JSONObject()
                .append("Excepción", e.getClass())    
                .append("Mensaje", e.getMessage())
                .toString();
        }

        return professor;
    }


    /* Private */

    private static final Logger logger = LogManager.getLogger(ProfessorController.class);
    private final ProfessorService service;
    
}
