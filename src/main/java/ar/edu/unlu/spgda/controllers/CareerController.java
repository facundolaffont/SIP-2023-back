package ar.edu.unlu.spgda.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unlu.spgda.models.Career;
import ar.edu.unlu.spgda.services.CareerService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/career")
public class CareerController {
    
    @GetMapping(path = "/all", produces = "application/json")
    public ResponseEntity<Object> getAll() {
        logger.info("GET /api/v1/career/all");
        logger.debug("Se ejecuta el método getAllCareers.");

        try {
            List<Career> carreras = careerService.getAllCareers();
            if (carreras == null) {
                return ResponseEntity.ok(List.of());
            }
            return ResponseEntity.ok(carreras);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error interno del servidor al obtener las carreras: " + e.getMessage());
        }
    }


    /* Private */
    private static final Logger logger = LoggerFactory.getLogger(CareerController.class);
    private final CareerService careerService;
}
