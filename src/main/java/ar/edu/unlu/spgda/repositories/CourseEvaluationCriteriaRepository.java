package ar.edu.unlu.spgda.repositories;

import ar.edu.unlu.spgda.models.Course;
import ar.edu.unlu.spgda.models.CourseEvaluationCriteria;
import ar.edu.unlu.spgda.models.EvaluationCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;


public interface CourseEvaluationCriteriaRepository extends JpaRepository<CourseEvaluationCriteria, Integer> {

    Optional<List<CourseEvaluationCriteria>> findById(long id);
    Optional<List<CourseEvaluationCriteria>> findByCourse(Course course);
    Optional<List<CourseEvaluationCriteria>> findByCourseAndCriteria(Course course, EvaluationCriteria criteria);
    CourseEvaluationCriteria findByCriteriaAndCourse(EvaluationCriteria evaluationCriteria, Course cursada);
}
