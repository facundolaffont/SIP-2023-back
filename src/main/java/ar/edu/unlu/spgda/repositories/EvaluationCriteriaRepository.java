package ar.edu.unlu.spgda.repositories;

import ar.edu.unlu.spgda.models.EvaluationCriteria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationCriteriaRepository extends JpaRepository<EvaluationCriteria, Integer> {
    EvaluationCriteria findByName(String name);
}
