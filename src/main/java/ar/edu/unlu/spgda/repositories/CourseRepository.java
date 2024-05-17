package ar.edu.unlu.spgda.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ar.edu.unlu.spgda.models.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Course getById(Long id);
}
