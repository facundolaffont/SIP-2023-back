package ar.edu.unlu.spgda.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ar.edu.unlu.spgda.models.Course;
import ar.edu.unlu.spgda.models.CourseProfessor;
import ar.edu.unlu.spgda.models.Userr;

public interface CourseProfessorRepository extends JpaRepository<CourseProfessor, Long> {
   Optional<List<CourseProfessor>> findByIdDocente(Userr docente);
   Optional<CourseProfessor> findByCursadaAndIdDocente(Course cursada, Userr docente);
}
