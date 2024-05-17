package ar.edu.unlu.spgda.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ar.edu.unlu.spgda.models.Userr;

public interface UserRepository extends JpaRepository<Userr, String> {
    Userr getById(String id);
}
