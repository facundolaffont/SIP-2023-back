package com.example.helloworld.repositories;

import com.example.helloworld.models.EvaluationCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationCriteriaRepository extends JpaRepository<EvaluationCriteria, Integer> {
    EvaluationCriteria findByName(String name);
}
