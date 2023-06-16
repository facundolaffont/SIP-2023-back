package com.example.helloworld.repositories;

import org.springframework.data.repository.CrudRepository;
import com.example.helloworld.models.Student;

public interface StudentRepository extends CrudRepository<Student, Integer> {}
