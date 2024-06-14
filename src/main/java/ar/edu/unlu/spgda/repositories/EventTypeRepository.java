package ar.edu.unlu.spgda.repositories;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import ar.edu.unlu.spgda.models.EventType;

public interface EventTypeRepository extends CrudRepository<EventType, Long> {
    Iterable<EventType> findAllByOrderById();
    Optional<EventType> findById(Long id);
    Optional<EventType> findByNombre(String nombre);
    EventType getById(Long id);
}
