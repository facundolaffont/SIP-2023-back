package com.example.helloworld.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.helloworld.models.Comission;
import com.example.helloworld.models.Course;
import com.example.helloworld.models.CourseDto;
import com.example.helloworld.models.CourseProfessor;
import com.example.helloworld.models.DatabaseHandler;
import com.example.helloworld.models.Subject;
import com.example.helloworld.models.Userr;
import com.example.helloworld.repositories.CourseProfessorRepository;
import com.example.helloworld.repositories.UserRepository;

@Service
public class CourseService {

    @Autowired
    private CourseProfessorRepository courseProfessorRepository;
    
    @Autowired
    private UserRepository userRepository;
        
    public List<CourseDto> getProfessorCourses(String userId) throws SQLException {
        //return DatabaseHandler.getInstance().select();
        Optional<Userr> docente = userRepository.findById(userId);
        List<CourseProfessor> courseProfessors = courseProfessorRepository.findByIdDocente(docente);
        List<CourseDto> cursadas = new ArrayList<>();
        for (CourseProfessor courseProfessor : courseProfessors) {
            // Accede a la información de CourseProfessor y Course
            Course course = courseProfessor.getIdCursada();
            Comission comission = course.getIdComision();
            Subject asignatura = comission.getIdAsignatura();
            // Realiza acciones con los objetos CourseProfessor y Course encontrados
            CourseDto cursada = new CourseDto();
            cursada.setNombreAsignatura(asignatura.getNombre());
            cursada.setNumeroComision(comission.getId());
            cursada.setAnio(course.getAnio());
            cursadas.add(cursada);
        }
        return cursadas;
    }
    
}
