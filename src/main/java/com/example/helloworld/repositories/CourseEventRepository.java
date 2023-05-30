package com.example.helloworld.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.example.helloworld.models.CourseEvent;

@Repository
public interface CourseEventRepository extends CrudRepository<CourseEvent, Long> {}