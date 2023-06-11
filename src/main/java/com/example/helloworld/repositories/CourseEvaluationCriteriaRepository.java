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

    Optional<List<CourseEvaluationCriteria>> findById(long id);
    Optional<List<CourseEvaluationCriteria>> findByCourse(Course course);
    CourseEvaluationCriteria findByCriteriaAndCourse(EvaluationCriteria evaluationCriteria, Course cursada);
}
