package com.example.helloworld.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.helloworld.models.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Course getById(Long id);
}
