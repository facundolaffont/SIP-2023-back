package com.example.helloworld.repositories;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.example.helloworld.models.EventType;

@Repository
public interface EventTypeRepository extends CrudRepository<EventType, Long> {
    Optional<EventType> findByNombre(String nombre);
}
