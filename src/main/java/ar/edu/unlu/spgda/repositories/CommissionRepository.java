package ar.edu.unlu.spgda.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.unlu.spgda.models.Comission;
import ar.edu.unlu.spgda.models.Subject;

public interface CommissionRepository extends JpaRepository<Comission, Integer> {
    Comission getById(Integer id);
    Optional<Comission> findByAsignaturaAndNumero(Subject asignatura, Integer numero);
    boolean existsByAsignaturaAndNumero(Subject asignatura, Integer numero);
    Optional<List<Comission>> findByAsignatura(Subject asignatura);
}