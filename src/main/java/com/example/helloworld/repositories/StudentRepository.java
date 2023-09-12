package com.example.helloworld.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import com.example.helloworld.models.Student;

public interface StudentRepository extends CrudRepository<Student, Integer> {
    public Optional<List<Student>> findByLegajoIn(List<Integer> studentList);
    public Student getByLegajo(Integer dossier);
    public List<Student> getByLegajoIn(List<Integer> dossiersList);
}
