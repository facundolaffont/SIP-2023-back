package com.example.helloworld.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.helloworld.models.Course;
import com.example.helloworld.models.CourseStudent;
import com.example.helloworld.models.Student;

import java.util.List;

public interface CourseStudentRepository extends JpaRepository<CourseStudent, Long> {
    List<CourseStudent> findByAlumnoAndCursada(Student alumno, Course cursada);
}
