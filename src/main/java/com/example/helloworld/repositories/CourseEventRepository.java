package com.example.helloworld.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.helloworld.models.CourseEvent;
import java.util.List;
import java.sql.Date;


@Repository
public interface CourseEventRepository extends JpaRepository<CourseEvent, Long> {
    List<CourseEvent> findByFechaHoraInicioBetween(Date startDate, Date endDate);
}