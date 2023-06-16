package com.example.helloworld.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.helloworld.models.CourseProfessor;
import com.example.helloworld.models.Userr;


public interface CourseProfessorRepository extends JpaRepository<CourseProfessor, Long> {
   List<CourseProfessor> findByIdDocente(Optional<Userr> docente);
}