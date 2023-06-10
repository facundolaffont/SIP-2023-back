package com.example.helloworld.repositories;

import org.springframework.data.repository.CrudRepository;

import com.example.helloworld.models.Course;
import com.example.helloworld.models.CourseStudent;
import java.util.List;
import java.util.Optional;


public interface StudentCourseRepository extends CrudRepository<CourseStudent, Long> {

    List<CourseStudent> findByCursada(Optional<Course> cursada);
}