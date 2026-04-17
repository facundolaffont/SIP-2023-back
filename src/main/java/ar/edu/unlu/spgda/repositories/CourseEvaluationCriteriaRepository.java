package ar.edu.unlu.spgda.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.unlu.spgda.models.Course;
import ar.edu.unlu.spgda.models.CourseEvaluationCriteria;
import ar.edu.unlu.spgda.models.EvaluationCriteria;


public interface CourseEvaluationCriteriaRepository extends JpaRepository<CourseEvaluationCriteria, Long> {

    Optional<List<CourseEvaluationCriteria>> findById(long id);
    Optional<List<CourseEvaluationCriteria>> findByCourse(Course course);
    List<CourseEvaluationCriteria> findByCourseOrderByOrdenAsc(Course course);
    Optional<List<CourseEvaluationCriteria>> findByCourseAndCriteria(Course course, EvaluationCriteria criteria);
    CourseEvaluationCriteria findByCriteriaAndCourse(EvaluationCriteria evaluationCriteria, Course cursada);
}
