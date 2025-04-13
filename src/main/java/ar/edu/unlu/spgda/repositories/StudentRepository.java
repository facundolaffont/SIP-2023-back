package ar.edu.unlu.spgda.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import ar.edu.unlu.spgda.models.Student;

public interface StudentRepository extends CrudRepository<Student, Integer> {
    public Student getByLegajo(Integer dossier);
    public List<Student> getByLegajoIn(List<Integer> dossiersList);
    public Optional<List<Student>> findByLegajoIn(List<Integer> studentList);

    public Boolean existsByDni(Integer id);

    public Boolean existsByEmail(String email);
    public Optional<List<Student>> findByEmailIn(List<String> email);
}
