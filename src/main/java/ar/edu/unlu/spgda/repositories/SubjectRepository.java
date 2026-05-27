package ar.edu.unlu.spgda.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.unlu.spgda.models.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findById(Long id);
    boolean existsByCodigoAsignatura(Integer codigoAsignatura);
}