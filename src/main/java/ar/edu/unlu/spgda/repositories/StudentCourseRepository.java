package ar.edu.unlu.spgda.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.unlu.spgda.models.Course;
import ar.edu.unlu.spgda.models.CourseStudent;
import ar.edu.unlu.spgda.models.Student;

import java.util.List;
import java.util.Optional;

public interface StudentCourseRepository extends JpaRepository<CourseStudent, Long> {

    Optional<CourseStudent> getByCursadaAndAlumno(Course course, Student student);
    
    Optional<List<CourseStudent>> findByCursada(Course course);
    Optional<CourseStudent> findByCursadaAndAlumno(Course course, Student student);
    Optional<CourseStudent> findByCursadaAndAlumnoNotIn(Course course, List<Student> studentList);
    Long countByCursadaAndAlumnoNotIn(Course course, List<Student> studentList);

}