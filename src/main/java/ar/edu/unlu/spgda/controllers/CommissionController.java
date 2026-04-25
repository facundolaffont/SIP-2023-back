package ar.edu.unlu.spgda.controllers;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unlu.spgda.models.Comission;
import ar.edu.unlu.spgda.models.Exceptions.HasDependenciesException;
import ar.edu.unlu.spgda.models.Exceptions.ResourceNotFoundException;
import ar.edu.unlu.spgda.models.Exceptions.ConflictException;
import ar.edu.unlu.spgda.requests.NewCommissionRequest;
import ar.edu.unlu.spgda.requests.UpdateCommissionRequest;
import ar.edu.unlu.spgda.responses.CommissionResponse;
import ar.edu.unlu.spgda.services.CommissionService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/commission")
public class CommissionController {
    
    @GetMapping(path = "/all", produces = "application/json")
    public ResponseEntity<Object> getAll() {
        logger.info("GET /api/v1/commission/all");
        logger.debug("Se ejecuta el método getAll");
        
        try {
            List<Comission> comisiones = commissionService.getAllComissions();
            List<CommissionResponse> responses = comisiones.stream()
                .map(CommissionResponse::fromEntity)
                .toList();
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            logger.error("Error interno del servidor al obtener las comisiones", e);
            return ResponseEntity.internalServerError().body(Map.of("errorCode", "INTERNAL_SERVER_ERROR"));
        }
    }

    @PostMapping(path = "/add")
    public ResponseEntity<Object> add(@Valid @RequestBody NewCommissionRequest newCommissionRequest)
    {
        logger.info("POST /api/v1/commission/add");
        logger.debug(newCommissionRequest.toString());

        try {
            CommissionResponse response = commissionService.createCommission(newCommissionRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ResourceNotFoundException e) {
            logger.warn(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "errorCode", "RESOURCE_NOT_FOUND",
                "message", e.getMessage()
            ));
        } catch (ConflictException e) {
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
    public ResponseEntity<Object> updateCommission(@RequestBody UpdateCommissionRequest request) {
        logger.info("PUT /api/v1/commission/update");
        logger.debug("Se ejecuta el método updateCommission.");
        
        try {
            // AGREGAR VALIDACIÓN PARA VERIFICAR QUE HAYAN LLEGADO TODOS LOS DATOS NECESARIOS CORRECTAMENTE
            Comission commissionUpdated = commissionService.updateCommission(request);
            return ResponseEntity.ok("Comisión modificada correctamente (ID: " + commissionUpdated.getId() + ")");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al modificar la comisión: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteCommission(@RequestParam Integer id) throws Exception {
        logger.info("DELETE /api/v1/commission/delete");
        logger.debug("Se ejecuta el método deleteCommission. [id = %s]", id);

        try {
            commissionService.deleteCommission(id);
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
            logger.error("Error crítico al eliminar la comisión con id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("errorCode", "INTERNAL_ERROR"));
        }
    }


    /* Private */
    private static final Logger logger = LoggerFactory.getLogger(CommissionController.class);
    private final CommissionService commissionService;
}