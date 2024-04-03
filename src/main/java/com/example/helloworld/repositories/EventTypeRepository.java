package com.example.helloworld.repositories;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import com.example.helloworld.models.EventType;

public interface EventTypeRepository extends CrudRepository<EventType, Long> {
    Optional<EventType> findById(Long id);
    Optional<EventType> findByNombre(String nombre);
    EventType getById(Long id);
}
