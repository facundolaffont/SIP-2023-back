package com.example.helloworld.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.helloworld.models.Course;
import com.example.helloworld.models.CourseProfessor;
import com.example.helloworld.models.Userr;

public interface CourseProfessorRepository extends JpaRepository<CourseProfessor, Long> {
   Optional<List<CourseProfessor>> findByIdDocente(Userr docente);
   Optional<CourseProfessor> findByCursadaAndIdDocente(Course cursada, Userr docente);
}
