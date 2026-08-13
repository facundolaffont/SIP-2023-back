package ar.edu.unlu.spgda.repositories;

import ar.edu.unlu.spgda.models.Course;
import ar.edu.unlu.spgda.models.CourseGroup;
import ar.edu.unlu.spgda.models.CourseGroupStudent;
import ar.edu.unlu.spgda.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseGroupStudentRepository extends JpaRepository<CourseGroupStudent, Long> {

    Optional<CourseGroupStudent> findByCursadaAndAlumno(Course cursada, Student alumno);

    List<CourseGroupStudent> findByCursada(Course cursada);
    
    List<CourseGroupStudent> findByGrupo(CourseGroup grupo);

    @Transactional
    void deleteByGrupo(CourseGroup grupo);
}
