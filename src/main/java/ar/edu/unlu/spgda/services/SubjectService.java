package ar.edu.unlu.spgda.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.unlu.spgda.models.Career;
import ar.edu.unlu.spgda.models.Comission;
import ar.edu.unlu.spgda.models.Subject;
import ar.edu.unlu.spgda.models.Exceptions.ConflictException;
import ar.edu.unlu.spgda.models.Exceptions.HasDependenciesException;
import ar.edu.unlu.spgda.models.Exceptions.ResourceNotFoundException;
import ar.edu.unlu.spgda.models.Exceptions.NonValidAttributeException;
import ar.edu.unlu.spgda.repositories.CareerRepository;
import ar.edu.unlu.spgda.repositories.CommissionRepository;
import ar.edu.unlu.spgda.repositories.SubjectRepository;
import ar.edu.unlu.spgda.requests.NewSubjectRequest;
import ar.edu.unlu.spgda.requests.UpdateSubjectRequest;
import ar.edu.unlu.spgda.responses.SubjectResponse;

@Service
public class SubjectService {

    @Autowired private SubjectRepository subjectRepository;
    @Autowired private CareerRepository careerRepository;
    @Autowired private CommissionRepository commissionRepository;

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    @Transactional
    public SubjectResponse createSubject(NewSubjectRequest newSubjectRequest) {
        // 1. Validar que exista la Carrera
        Career career = careerRepository.findById(newSubjectRequest.getCareerId())
            .orElseThrow(() -> new NonValidAttributeException("La carrera ingresada no existe"));
            
        // 2. Validar que no exista el código de asignatura
        if (subjectRepository.existsByCodigoAsignatura(newSubjectRequest.getSubjectCode())) {
            throw new ConflictException("El código de asignatura " + newSubjectRequest.getSubjectCode() + " ya se encuentra en uso");
        }
        
        // 3. Guardar la Asignatura
        Subject subject = new Subject();
        subject.setIdCarrera(career);
        subject.setCodigoAsignatura(newSubjectRequest.getSubjectCode());
        subject.setNombre(newSubjectRequest.getSubjectName());
        Subject savedSubject = subjectRepository.save(subject);
        
        return SubjectResponse.fromEntity(savedSubject);
    }

    @Transactional
    public Subject updateSubject(UpdateSubjectRequest request) throws Exception {
        // 1. Validar que exista la Asignatura a modificar
        Subject subject = subjectRepository.findById(request.getId())
                .orElseThrow(() -> new Exception("Asignatura no encontrada"));

        // 2. Validar que exista la Carrera (Si cambió)
        Career career = careerRepository.findById(request.getCareerId())
                .orElseThrow(() -> new Exception("Carrera no encontrada"));
        
        // 3. Actualizamos los datos simples y la RELACIÓN de carrera (si es que se modificó)
        subject.setCodigoAsignatura(request.getSubjectCode());
        subject.setNombre(request.getSubjectName());
        subject.setIdCarrera(career);

        // 4. Guardamos los cambios
        Subject updatedSubject = subjectRepository.save(subject);

        return updatedSubject;
    }   

    @Transactional
    public void deleteSubject(Long id) {
        // 1. Validar que exista la asignatura a eliminar
        Subject subject = subjectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("La asignatura con id " + id + " no existe"));

        // 2. Validar que no tenga dependencias (comisiones)
        Optional<List<Comission>> commissionsOptional = commissionRepository.findByAsignatura(subject);
        if (commissionsOptional.isPresent() && !commissionsOptional.get().isEmpty()) {
            throw new HasDependenciesException("No se puede eliminar la asignatura con id " + id + " porque tiene comisiones asociadas.");
        }

        // 3. Eliminar la asignatura
        subjectRepository.delete(subject);
    }

}