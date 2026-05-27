package ar.edu.unlu.spgda.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.unlu.spgda.models.Career;

public interface CareerRepository extends JpaRepository<Career, Long> {
    
}  

