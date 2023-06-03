package com.example.helloworld.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.example.helloworld.models.Course;

@Repository
public interface CourseRepository extends CrudRepository<Course, Long> {}
