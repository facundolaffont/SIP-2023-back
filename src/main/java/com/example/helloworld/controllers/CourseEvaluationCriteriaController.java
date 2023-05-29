package com.example.helloworld.controllers;

import com.example.helloworld.models.CourseEvaluationCriteria;
import com.example.helloworld.services.CourseEvaluationCriteriaService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    private static final Logger logger = LoggerFactory.getLogger(CourseEvaluationCriteriaController.class);
    private final CourseEvaluationCriteriaService service;

}
