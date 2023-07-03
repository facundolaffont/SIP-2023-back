package com.example.helloworld.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.helloworld.models.Course;
import com.example.helloworld.models.CourseStudent;
import com.example.helloworld.models.Student;

import java.util.List;
import java.util.Optional;

public interface CourseStudentRepository extends JpaRepository<CourseStudent, Long> {

    // Devuelve el objeto que representa a la cursada del alumno, si existe.
    Optional<CourseStudent> findByAlumnoAndCursada(Student alumno, Course cursada);

    Optional<List<CourseStudent>> findByAlumnoInAndCursada(List<Student> alumno, Course cursada);
    boolean existsByAlumnoAndCursada(Student alumno, Course cursada);

}
