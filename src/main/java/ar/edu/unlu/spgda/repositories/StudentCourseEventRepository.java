package ar.edu.unlu.spgda.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ar.edu.unlu.spgda.models.CourseEvent;
import ar.edu.unlu.spgda.models.Student;
import ar.edu.unlu.spgda.models.StudentCourseEvent;
import java.util.List;
import java.util.Optional;

public interface StudentCourseEventRepository extends JpaRepository<StudentCourseEvent, Long> {

    Optional<List<StudentCourseEvent>> findByAlumnoAndEventoCursadaIn(Student alumno, List<CourseEvent> courseEventList);
    Optional<List<StudentCourseEvent>> findByEventoCursada(CourseEvent eventoCursada);
    Optional<List<StudentCourseEvent>> findByEventoCursadaIn(List<CourseEvent> courseEventList);
    Optional<StudentCourseEvent> findByEventoCursadaAndAlumno(CourseEvent courseEvent, Student student);
    Optional<List<StudentCourseEvent>> findByEventoCursadaAndAlumnoIn(CourseEvent courseEvent, List<Student> studentsList);

}
