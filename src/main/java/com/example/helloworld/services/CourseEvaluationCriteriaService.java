package com.example.helloworld.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.helloworld.models.Course;
import com.example.helloworld.models.CourseEvaluationCriteria;
import com.example.helloworld.repositories.CourseRepository;
import com.example.helloworld.repositories.CourseEvaluationCriteriaRepository;


@Service
public class CourseEvaluationCriteriaService {

    public List<CourseEvaluationCriteria> getCourseEvaluationCriterias(long courseId) {
        
        // Recupero la cursada asociada al ID

        Optional<Course> course = courseRepository.findById(courseId);

        // Recupero los criterios asociados a dicha cursada

        Optional<List<CourseEvaluationCriteria>> courseEvaluationCriterias = courseEvaluationCriteriaRepository.findByCourse(course.get());

        return courseEvaluationCriterias.get();
    }

    @Autowired private CourseRepository courseRepository;
    @Autowired private CourseEvaluationCriteriaRepository courseEvaluationCriteriaRepository;

}

