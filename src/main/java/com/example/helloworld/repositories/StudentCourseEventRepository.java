package com.example.helloworld.repositories;

import org.springframework.data.repository.CrudRepository;
import com.example.helloworld.models.CourseEvent;
import com.example.helloworld.models.Student;
import com.example.helloworld.models.StudentCourseEvent;
import java.util.List;
import java.util.Optional;

public interface StudentCourseEventRepository extends CrudRepository<StudentCourseEvent, Long> {
    StudentCourseEvent findByEventoCursadaAndAlumno(CourseEvent eventoCursada, Student alumno);
    Optional<List<StudentCourseEvent>> findByEventoCursada(CourseEvent eventoCursada);
    Optional<List<StudentCourseEvent>> findByAlumnoAndEventoCursadaIn(Student alumno, List<CourseEvent> courseEventList);
    Optional<List<StudentCourseEvent>> findByEventoCursadaIn(List<CourseEvent> courseEventList);
}