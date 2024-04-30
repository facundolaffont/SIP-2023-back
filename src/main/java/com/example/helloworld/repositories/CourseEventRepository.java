package com.example.helloworld.repositories;

import org.springframework.data.repository.CrudRepository;
import com.example.helloworld.models.Course;
import com.example.helloworld.models.CourseEvent;
import com.example.helloworld.models.EventType;
import java.util.List;
import java.util.Optional;
import java.sql.Date;

public interface CourseEventRepository extends CrudRepository<CourseEvent, Long> {

    List<CourseEvent> findByFechaHoraInicioBetween(Date startDate, Date endDate);
    Optional<List<CourseEvent>> findByCursadaAndTipoEvento(Course cursada, EventType tipoEvento);
    Optional<List<CourseEvent>> findByCursadaAndTipoEventoNot(Course cursada, EventType tipoEvento);
    Optional<List<CourseEvent>> findByCursada(Course cursada);
    Optional<List<CourseEvent>> findByCursadaAndTipoEventoNot(Course cursada, EventType tipoEvento);
    CourseEvent getById(Long id);

}
