package ar.edu.unlu.spgda.controllers;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unlu.spgda.models.Subject;
import ar.edu.unlu.spgda.models.Exceptions.HasDependenciesException;
import ar.edu.unlu.spgda.models.Exceptions.ResourceNotFoundException;
import ar.edu.unlu.spgda.models.Exceptions.NonValidAttributeException;
import ar.edu.unlu.spgda.requests.NewSubjectRequest;
import ar.edu.unlu.spgda.requests.UpdateSubjectRequest;
import ar.edu.unlu.spgda.responses.SubjectResponse;
import ar.edu.unlu.spgda.services.SubjectService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/subject")
public class SubjectController {

    @GetMapping(path = "/all", produces = "application/json")
    public ResponseEntity<Object> getAll() {
        logger.info("GET /api/v1/subject/all");
        logger.debug("Se ejecuta el método getAll");

        try {
            List<Subject> asignaturas = subjectService.getAllSubjects();
            List<SubjectResponse> responses = asignaturas.stream()
                .map(SubjectResponse::fromEntity)
                .toList();
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            logger.error("Error interno del servidor al obtener las asignaturas", e);
            return ResponseEntity.internalServerError().body(Map.of("errorCode", "INTERNAL_SERVER_ERROR"));
        }
    }

    @PostMapping(path = "/add")
    public ResponseEntity<Object> add(@Valid @RequestBody NewSubjectRequest newSubjectRequest)
    {
        logger.info("POST /api/v1/subject/add");
        logger.debug(newSubjectRequest.toString());

        try {
            SubjectResponse response = subjectService.createSubject(newSubjectRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (NonValidAttributeException e) {
            logger.warn(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of(
                "errorCode", "UNPROCESSABLE_ENTITY",
                "message", e.getMessage()
            ));
        } catch (ar.edu.unlu.spgda.models.Exceptions.ConflictException e) {
            logger.warn(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "errorCode", "CONFLICT",
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "errorCode", "INTERNAL_SERVER_ERROR",
                "message", "Ocurrió un error inesperado. Intentá nuevamente en unos minutos"
            ));
        }
    }

    @PutMapping(path = "/update", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> updateSubject(@RequestBody UpdateSubjectRequest request) {
        logger.info("PUT /api/v1/subject/update");
        logger.debug("Se ejecuta el método updateSubject.");
        
        try {
            // AGREGAR VALIDACIÓN PARA VERIFICAR QUE HAYAN LLEGADO TODOS LOS DATOS NECESARIOS CORRECTAMENTE
            Subject subjectUpdated = subjectService.updateSubject(request);
            return ResponseEntity.ok("Asignatura modificada correctamente (ID: " + subjectUpdated.getId() + ")");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al modificar la asignatura: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteSubject(@RequestParam Long id) throws Exception {
        logger.info("DELETE /api/v1/subject/delete");
        logger.debug("Se ejecuta el método deleteSubject. [id = %s]", id);

        try {
            subjectService.deleteSubject(id);
            return ResponseEntity.ok(Map.of("status", "SUCCESS"));
        } catch (HasDependenciesException e) {
            logger.warn(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("errorCode", "HAS_DEPENDENCIES"));
        } catch (ResourceNotFoundException e) {
            logger.warn(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("errorCode", "RESOURCE_NOT_FOUND"));
        } catch (Exception e) {
            logger.error("Error crítico al eliminar la asignatura con id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("errorCode", "INTERNAL_ERROR"));
        }
    }

    /* Private */
    private static final Logger logger = LoggerFactory.getLogger(SubjectController.class);
    private final SubjectService subjectService;
}