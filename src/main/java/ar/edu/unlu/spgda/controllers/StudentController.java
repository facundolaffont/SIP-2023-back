package ar.edu.unlu.spgda.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unlu.spgda.models.ErrorHandler;
import ar.edu.unlu.spgda.models.Exceptions.EmptyQueryException;
import ar.edu.unlu.spgda.requests.NewDossiersCheckRequest;
import ar.edu.unlu.spgda.requests.NewStudentRequest;
import ar.edu.unlu.spgda.requests.NewStudentsCheckRequest;
import ar.edu.unlu.spgda.requests.NewStudentsRequest;
import ar.edu.unlu.spgda.requests.StudentsRegistrationRequest;
import ar.edu.unlu.spgda.services.CourseService;
import ar.edu.unlu.spgda.services.StudentService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/students")
@CrossOrigin(origins = "https://spgda.fl.com.ar/")
public class StudentController {
    
    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody NewStudentRequest newStudentRequest) {

        logger.info("POST /api/v1/students/create");
        logger.debug(
            String.format(
                "Se ejecuta el método create. [newStudentRequest = %s]",
                newStudentRequest.toString()
            )
        );

        return studentService.create(newStudentRequest);

    }

    @PostMapping("/new-dossiers-check")
    public ResponseEntity<Object> newDossiersCheck(@RequestBody NewDossiersCheckRequest newDossiersCheckRequest) {

        logger.info("POST /api/v1/students/new-dossiers-check");
        logger.debug(
            String.format(
                "Se ejecuta el método newDossiersCheck. [newDossiersCheckRequest = %s]",
                newDossiersCheckRequest.toString()
            )
        );

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(
                studentService.checkNewDossiersRegistration(newDossiersCheckRequest)
            );

    }

    // Indica si los legajos recibidos pueden registrarse o no en el sistema.
    @PostMapping("/new-students-check")
    public ResponseEntity<Object> newStudentsCheck(@RequestBody NewStudentsCheckRequest newStudentsCheckRequest) {

        logger.info("POST /api/v1/students/new-students-check");
        logger.debug(
            String.format(
                "Se ejecuta el método newStudentsCheck. [newStudentsCheckRequest = %s]",
                newStudentsCheckRequest.toString()
            )
        );

        try {
        
            return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                    studentService.checkNewStudentsRegistration(newStudentsCheckRequest)
                );
        
        } catch (EmptyQueryException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ErrorHandler.returnErrorAsJson(e));
        }

    }

    // Registra estudiantes en el sistema y en la cursada seleccionada.
    @PostMapping("/register-students")
    public ResponseEntity<Object> registerStudents(@RequestBody NewStudentsRequest newStudentsRequest) {

        logger.info("POST /api/v1/students/register-students");
        logger.debug(
            String.format(
                "Se ejecuta el método registerStudents. [newStudentsRequest = %s]",
                newStudentsRequest.toString()
            )
        );

        try {
        
            // Registra en el sistema solo los estudiantes que todavía no fueron registrados.
            studentService.registerOnlyNonExistingStudents(newStudentsRequest);

            // Vincula los estudiantes con la cursada seleccionada.
            var studentsRegistrationRequest = new StudentsRegistrationRequest(newStudentsRequest.getCourseId());
            for (NewStudentsRequest.NewStudentRegister studentRegister : newStudentsRequest.getNewStudentsList()) {
                studentsRegistrationRequest.addStudentRegistrationInfo(
                    studentRegister.getDossier(),
                    studentRegister.getAllPreviousSubjectsApproved(),
                    studentRegister.getAlreadyStudied());
            }
            Object courseRegisteringResult = courseService.registerStudents(studentsRegistrationRequest);

            return ResponseEntity
                .status(HttpStatus.OK)
                .body(courseRegisteringResult);
        
        } catch (EmptyQueryException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ErrorHandler.returnErrorAsJson(e));
        }

    }


    /* Private */

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);
    private final StudentService studentService;
    private final CourseService courseService;

}
