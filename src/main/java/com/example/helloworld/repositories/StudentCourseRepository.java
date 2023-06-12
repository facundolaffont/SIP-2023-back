package com.example.helloworld.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.helloworld.models.Course;
import com.example.helloworld.models.CourseStudent;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentCourseRepository extends JpaRepository<CourseStudent, Long> {

    Optional<List<CourseStudent>> findByCursada(Course cursada);

}