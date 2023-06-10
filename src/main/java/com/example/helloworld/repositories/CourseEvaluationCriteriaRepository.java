package com.example.helloworld.repositories;

import com.example.helloworld.models.Course;
import com.example.helloworld.models.CourseEvaluationCriteria;
import com.example.helloworld.models.EvaluationCriteria;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface CourseEvaluationCriteriaRepository extends JpaRepository<CourseEvaluationCriteria, Integer> {

    List<CourseEvaluationCriteria> findById(long id);
    List<CourseEvaluationCriteria> findByCourse(Optional<Course> course);
    CourseEvaluationCriteria findByCriteriaAndCourse(EvaluationCriteria evaluationCriteria, Optional<Course> cursada);
}
