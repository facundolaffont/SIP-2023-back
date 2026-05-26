package ar.edu.unlu.spgda.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.unlu.spgda.models.Comission;
import ar.edu.unlu.spgda.models.Course;
import ar.edu.unlu.spgda.models.Subject;
import ar.edu.unlu.spgda.models.Exceptions.HasDependenciesException;
import ar.edu.unlu.spgda.models.Exceptions.ResourceNotFoundException;
import ar.edu.unlu.spgda.models.Exceptions.ConflictException;
import ar.edu.unlu.spgda.responses.CommissionResponse;
import ar.edu.unlu.spgda.repositories.CommissionRepository;
import ar.edu.unlu.spgda.repositories.SubjectRepository;
import ar.edu.unlu.spgda.repositories.CourseRepository;
import ar.edu.unlu.spgda.requests.NewCommissionRequest;
import ar.edu.unlu.spgda.requests.UpdateCommissionRequest;

@Service
public class CommissionService {
    
    @Autowired private CommissionRepository commissionRepository;
    @Autowired private SubjectRepository subjectRepository;
    @Autowired private CourseRepository courseRepository;

    public List<Comission> getAllComissions() {
        return commissionRepository.findAll();
    }

    @Transactional
    public CommissionResponse createCommission(NewCommissionRequest newCommissionRequest) {
        // 1. Validar que exista la Asignatura
        Subject subject = subjectRepository.findById(newCommissionRequest.getSubjectId())
            .orElseThrow(() -> new ResourceNotFoundException("La asignatura ingresada no existe"));
        
        // 2. Validar que no exista una comisión con el mismo número para esa asignatura
        if (commissionRepository.existsByAsignaturaAndNumero(subject, newCommissionRequest.getCommissionNumber())) {
            throw new ConflictException("Ya existe una comisión con el número " + newCommissionRequest.getCommissionNumber() + " para la asignatura ingresada");
        }
        
        // 3. Crear y guardar la Comisión
        Comission comision = new Comission();
        comision.setAsignatura(subject);
        comision.setNumero(newCommissionRequest.getCommissionNumber());
        Comission savedCommission = commissionRepository.save(comision);
        
        return CommissionResponse.fromEntity(savedCommission);
    }

    @Transactional
    public Comission updateCommission(UpdateCommissionRequest request) throws Exception {
        // 1. Validar que exista la Comisión a modificar
        Comission commission = commissionRepository.findById(request.getId())
                .orElseThrow(() -> new Exception("Comisión no encontrada"));

        // 2. Validar que exista la Asignatura (Si cambió)
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new Exception("Asignatura no encontrada"));
        
        // 3. Actualizamos los datos simples y la RELACIÓN de asignatura (si es que se modificó)
        commission.setNumero(request.getCommissionNumber());
        commission.setAsignatura(subject);

        // 4. Guardamos los cambios
        Comission updatedCommission = commissionRepository.save(commission);

        return updatedCommission;
    }   

    @Transactional
    public void deleteCommission(Integer id) {
        // 1. Validar que exista la comisión a eliminar
        Comission commission = commissionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("La comisión con id " + id + " no existe"));

        // 2. Validar que no tenga dependencias (cursadas)
        Optional<List<Course>> coursesOptional = courseRepository.findByComision(commission);
        if (coursesOptional.isPresent() && !coursesOptional.get().isEmpty()) {
            throw new HasDependenciesException("No se puede eliminar la comisión con id " + id + " porque tiene cursadas asociadas.");
        }

        // 3. Eliminar la comisión
        commissionRepository.delete(commission);
    }

}