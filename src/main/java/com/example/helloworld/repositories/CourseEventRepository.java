package com.example.helloworld.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.example.helloworld.models.Course;
import com.example.helloworld.models.CourseEvent;
import com.example.helloworld.models.EventType;
import java.util.List;
import java.util.Optional;
import java.sql.Date;

@Repository
public interface CourseEventRepository extends CrudRepository<CourseEvent, Long> {
    List<CourseEvent> findByFechaHoraInicioBetween(Date startDate, Date endDate);
    Optional<List<CourseEvent>> findByCursadaAndTipoEvento(Course cursada, EventType tipoEvento);
    List<CourseEvent> findByCursada(Course cursada);
}