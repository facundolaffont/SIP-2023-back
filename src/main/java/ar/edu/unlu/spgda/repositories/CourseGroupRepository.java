package ar.edu.unlu.spgda.repositories;

import ar.edu.unlu.spgda.models.Course;
import ar.edu.unlu.spgda.models.CourseGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseGroupRepository extends JpaRepository<CourseGroup, Long> {
    
    List<CourseGroup> findByCursada(Course cursada);
    
    Optional<CourseGroup> findByCursadaAndNombre(Course cursada, String nombre);
}
