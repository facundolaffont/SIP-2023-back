package com.example.helloworld.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.helloworld.models.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
}
