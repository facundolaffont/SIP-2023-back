package ar.edu.unlu.spgda.controllers;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.exception.APIException;
import com.auth0.exception.Auth0Exception;

import ar.edu.unlu.spgda.models.Userr;
import ar.edu.unlu.spgda.models.Exceptions.ExternalServiceException;
import ar.edu.unlu.spgda.models.Exceptions.HasDependenciesException;
import ar.edu.unlu.spgda.models.Exceptions.NonValidAttributeException;
import ar.edu.unlu.spgda.models.Exceptions.NullAttributeException;
import ar.edu.unlu.spgda.models.Exceptions.ResourceNotFoundException;
import ar.edu.unlu.spgda.requests.NewProfessorRequest;
import ar.edu.unlu.spgda.requests.NewUserRequest;
import ar.edu.unlu.spgda.requests.UpdateProfessorRequest;
import ar.edu.unlu.spgda.responses.UserResponse;
import ar.edu.unlu.spgda.services.ProfessorService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "https://spgda.fl.com.ar/")
public class UserController {

    // Método viejo, lo dejo porque lo sigue usando la parte de gestión de USUARIOS
    @PostMapping("/add")
    //@PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Object> add(@RequestBody NewUserRequest newUserRequest)
        throws Exception
    {
        
        logger.info("POST /api/v1/users/add");
        logger.debug(newUserRequest.toString());

        Object newUser = null;
        // switch(newUserRequest.getRol().toLowerCase()) {

        //     // Se quiere dar de alta un docente.
             //case "docente":
                 try {
                    newUser = professorService.create(newUserRequest);
                 }
                 catch (APIException e) {
                    return new ResponseEntity<>(
                        e,
                        HttpStatus.INTERNAL_SERVER_ERROR
                    );
                 }
                 catch (NonValidAttributeException | NullAttributeException | SQLException | Auth0Exception e) {
                    return new ResponseEntity<>(
                        e,
                        HttpStatus.INTERNAL_SERVER_ERROR
                    );
                 }
            // break;
            
        //     // Se quiere dar de alta un administrador.
             /*case "administrador":
                logger.debug("Se solicita el alta de un administrador.");
             break;
             default:
                throw new NonValidAttributeException("El valor del rol no es válido."); */
      //  }

        return ResponseEntity.status(HttpStatus.OK).body(newUser);    

    }

    @PostMapping(path = "/add-professor")
    public ResponseEntity<Object> add(@Valid @RequestBody NewProfessorRequest newProfessorRequest) {
        logger.info("POST /api/v1/users/add-professor");
        logger.debug(newProfessorRequest.toString());

        try {
            // Llamamos al servicio para crear el profesor y obtener los datos persistidos
            UserResponse response = professorService.createProfessor(newProfessorRequest);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (APIException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "errorCode", "INTERNAL_SERVER_ERROR",
                "message", e.getMessage()
            ));
        } catch (NonValidAttributeException | NullAttributeException | SQLException | Auth0Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "errorCode", "INTERNAL_SERVER_ERROR",
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "errorCode", "BAD_REQUEST",
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping(path = "/get-all-professors", produces = "application/json")
    public ResponseEntity<Object> getAllProfessors() {
        logger.info("GET /api/v1/users/get-all-professors");
        logger.debug("Se ejecuta el método getAllProfessors");

        try {
            List<Userr> docentes = professorService.getAllProfessors();
            List<UserResponse> responses = docentes.stream()
                .map(UserResponse::fromEntity)
                .toList();
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            logger.error("Error interno del servidor al obtener los profesores", e);
            return ResponseEntity.internalServerError().body(Map.of("errorCode", "INTERNAL_SERVER_ERROR"));
        }
    }

    @PutMapping(path = "/update-professor", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> updateProfessor(@RequestBody UpdateProfessorRequest request) {
        logger.info("PUT /api/v1/users/update-professor");
        logger.debug("Se ejecuta el método updateProfessor.");

        try {
            // AGREGAR VALIDACIÓN PARA VERIFICAR QUE HAYAN LLEGADO TODOS LOS DATOS NECESARIOS CORRECTAMENTE
            Userr professorUpdated = professorService.updateProfessor(request);
            return ResponseEntity.ok("Profesor modificado correctamente (ID: " + professorUpdated.getId() + ")");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al modificar el profesor: " + e.getMessage());
        }
    }

    @DeleteMapping(path = "/delete-professor", produces = "application/json")
    public ResponseEntity<Object> deleteProfessor(@RequestParam("id") String id) {
        logger.info("DELETE /api/v1/users/delete-professor");
        logger.debug("Se ejecuta el método deleteProfessor con id: " + id);

        try {
            professorService.deleteProfessor(id);
            return ResponseEntity.ok().body(Map.of(
                "message", "Docente eliminado correctamente."
            ));

        } catch (ResourceNotFoundException e) {
            logger.warn(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "errorCode", "NOT_FOUND",
                "message", e.getMessage()
            ));
            
        } catch (HasDependenciesException e) {
            logger.warn(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "errorCode", "HAS_DEPENDENCIES", 
                "message", e.getMessage()
            ));
            
        } catch (ExternalServiceException e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "errorCode", "AUTH0_ERROR",
                "message", e.getMessage()
            ));
            
        } catch (Exception e) {
            // Cualquier otro error inesperado (por ejemplo, si falla la BD al hacer el flush)
            logger.error("Error inesperado eliminando docente: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "errorCode", "INTERNAL_SERVER_ERROR",
                "message", "Ocurrió un error inesperado. Intentá nuevamente en unos minutos."
            ));
        }
    }

    

    /* Private */

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final ProfessorService professorService;
    
}
