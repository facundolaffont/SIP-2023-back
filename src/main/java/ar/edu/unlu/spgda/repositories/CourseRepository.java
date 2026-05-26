package ar.edu.unlu.spgda.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.unlu.spgda.models.Comission;
import ar.edu.unlu.spgda.models.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Course getById(Long id);
    Optional<List<Course>> findByComision(Comission comision);
    boolean existsByComisionAndAnio(Comission comision, Integer anio);
}
