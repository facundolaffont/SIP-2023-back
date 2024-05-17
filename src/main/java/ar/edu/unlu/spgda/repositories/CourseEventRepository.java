package ar.edu.unlu.spgda.repositories;

import org.springframework.data.repository.CrudRepository;
import ar.edu.unlu.spgda.models.Course;
import ar.edu.unlu.spgda.models.CourseEvent;
import ar.edu.unlu.spgda.models.EventType;
import java.util.List;
import java.util.Optional;
import java.sql.Date;

public interface CourseEventRepository extends CrudRepository<CourseEvent, Long> {

    List<CourseEvent> findByFechaHoraInicioBetween(Date startDate, Date endDate);
    Optional<List<CourseEvent>> findByCursadaAndTipoEvento(Course cursada, EventType tipoEvento);
    Optional<List<CourseEvent>> findByCursadaAndTipoEventoNot(Course cursada, EventType tipoEvento);
    Optional<List<CourseEvent>> findByCursada(Course cursada);
    CourseEvent getById(Long id);

}
