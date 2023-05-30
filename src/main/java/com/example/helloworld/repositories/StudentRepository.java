package com.example.helloworld.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.example.helloworld.models.Student;

@Repository
public interface StudentRepository extends CrudRepository<Student, Integer> {}
