package com.example.helloworld.controllers;

import com.example.helloworld.models.CourseEvaluationCriteria;
import com.example.helloworld.models.ErrorHandler;
import com.example.helloworld.models.Exceptions.EmptyQueryException;
import com.example.helloworld.models.Exceptions.NotValidAttributeException;
import com.example.helloworld.models.Exceptions.NullAttributeException;
import com.example.helloworld.services.CourseEvaluationCriteriaService;

import lombok.RequiredArgsConstructor;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/criterion-course")
@RequiredArgsConstructor
@RestController
public class CourseEvaluationCriteriaController {

    @PostMapping("/add")
    public ResponseEntity<Object> add(@RequestBody CourseEvaluationCriteria criteria) {
        try {
            //CourseEvaluationCriteria saved = service.save(criteria);
            //return new ResponseEntity<>(saved, HttpStatus.OK);
            return new ResponseEntity<>("hola", HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(
                "El servicio está teniendo problemas. Por favor intente más tarde.",
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/evaluationCriterias")
    @CrossOrigin(origins = "*") // DEBUG: para hacer peticiones sin problemas con CORS.
    public List<CourseEvaluationCriteria> getEvaluationCriterias(
        @RequestParam("courseId") long courseId
    )
        throws
            NullAttributeException,
            SQLException,
            NotValidAttributeException 
    {

        logger.debug(String.format(
            "Se ejecuta el método getEvaluationCriterias. [courseId = %d]",
            courseId
        ));
        logger.info("GET /api/v1/course/evaluationCriterias");

        return courseEvaluationCriteriaService.getCourseEvaluationCriterias(courseId);

    }

    private final CourseEvaluationCriteriaService courseEvaluationCriteriaService;
    private static final Logger logger = LoggerFactory.getLogger(CourseEvaluationCriteriaController.class);

}
