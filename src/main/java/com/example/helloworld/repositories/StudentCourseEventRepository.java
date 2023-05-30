package com.example.helloworld.repositories;

import org.springframework.data.repository.CrudRepository;
import com.example.helloworld.models.StudentCourseEvent;

public interface StudentCourseEventRepository extends CrudRepository<StudentCourseEvent, Long> {}