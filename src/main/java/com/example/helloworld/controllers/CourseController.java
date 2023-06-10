package com.example.helloworld.controllers;

import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.helloworld.models.CourseDto;
import com.example.helloworld.models.Exceptions.NotValidAttributeException;
import com.example.helloworld.models.Exceptions.NullAttributeException;
import com.example.helloworld.services.CourseService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/course")
public class CourseController {
    
    @GetMapping("/getProfessor")
    //@PreAuthorize("hasAuthority('admin')")
    //@CrossOrigin(origins = "http://localhost:4040")
    @CrossOrigin(origins = "*") // DEBUG: para hacer peticiones sin problemas con CORS.
    public ResponseEntity<List<CourseDto>> get(@RequestHeader("Authorization") String authorizationHeader) throws NullAttributeException, SQLException, NotValidAttributeException 
    {
        logger.info("GET /api/v1/course/getProfessor");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token de autorización no proporcionado");
        }

        // Extraer el token JWT
        String token = authorizationHeader.substring(7);
        
        DecodedJWT decodedJWT = JWT.decode(token);
        
        String userId = decodedJWT.getSubject();
        String email = decodedJWT.getClaim("email").asString();

        System.out.println(userId);
        System.out.println(email);

        List<CourseDto> cursadas = courseService.getProfessorCourses(userId);
        /*for (List<String> fila : datos) {
            System.out.println("Nombre Asignatura: " + fila.get(0));
            System.out.println("NRO Comision: " + fila.get(1));
            System.out.println("AÑO Cursada: " + fila.get(2));
        }*/
        
        return new ResponseEntity<>(cursadas, HttpStatus.OK);

        // Se quieren obtener los datos de un docente en una cursada.

    }

    @GetMapping("/finalCondition")
    @CrossOrigin(origins = "*") // DEBUG: para hacer peticiones sin problemas con CORS.
    public ResponseEntity<List<CourseDto>> getFinalCondition(@RequestParam("courseId") long courseId) throws NullAttributeException, SQLException, NotValidAttributeException 
    {
        logger.info("GET /api/v1/course/finalCondition");

        courseService.calculateFinalCondition(courseId);

        return new ResponseEntity<>(HttpStatus.OK);

    }

    /* Private */

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);
    private final CourseService courseService;
    
}
