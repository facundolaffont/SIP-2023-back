package com.example.helloworld.services;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.helloworld.models.Student;
import com.example.helloworld.repositories.StudentRepository;
import com.example.helloworld.requests.NewStudentRegister;
import com.example.helloworld.requests.NewStudentRequest;

@Service
public class StudentService {

    // Alta masiva de estudiantes.
    public ResponseEntity<String> create (
        NewStudentRequest newStudentRequest
    ) {

        logger.debug(
            String.format(
                "Se ejecuta el método create. [newStudentRequest = %s]",
                newStudentRequest.toString()
            )
        );

        for (NewStudentRegister student: newStudentRequest.getStudents()) {

            var newStudent = new Student();
            newStudent.setNombre(student.getNombre());
            newStudent.setApellido(student.getApellido());
            newStudent.setDni(student.getDni());
            newStudent.setLegajo(student.getLegajo());
            newStudent.setEmail(student.getEmail());
            newStudent = studentRepository.save(newStudent);

        }

        var returningJson = (new JSONObject()).put("Respuesta", "OK.");
        var statusCode = HttpStatus.OK;

        return ResponseEntity
            .status(statusCode)
            .body(
                returningJson.toString()
            );
            
    }


    /* Private */

    private static final Logger logger = LoggerFactory.getLogger(ClassEventService.class);
    @Autowired private StudentRepository studentRepository;
    
}

