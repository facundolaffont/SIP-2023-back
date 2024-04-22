package com.example.helloworld.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.helloworld.models.Course;
import com.example.helloworld.models.CourseStudent;
import com.example.helloworld.models.Student;

import java.util.List;
import java.util.Optional;

public interface StudentCourseRepository extends JpaRepository<CourseStudent, Long> {

    Optional<List<CourseStudent>> findByCursada(Course course);
    Optional<CourseStudent> findByCursadaAndAlumno(Course course, Student student);
    Optional<CourseStudent> findByCursadaAndAlumnoNotIn(Course course, List<Student> studentList);
    Long countByCursadaAndAlumnoNotIn(Course course, List<Student> studentList);

}