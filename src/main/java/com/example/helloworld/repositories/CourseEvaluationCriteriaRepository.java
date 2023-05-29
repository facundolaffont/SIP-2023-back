package com.example.helloworld.repositories;

import com.example.helloworld.models.CourseEvaluationCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseEvaluationCriteriaRepository extends JpaRepository<CourseEvaluationCriteria, Integer> {
}
