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
import com.example.helloworld.models.CourseEvaluationCriteria;
import com.example.helloworld.models.CourseProfessor;
import com.example.helloworld.models.CourseStudent;
import com.example.helloworld.models.Student;
import com.example.helloworld.models.Subject;
import com.example.helloworld.models.Userr;
import com.example.helloworld.repositories.CourseEvaluationCriteriaRepository;
import com.example.helloworld.repositories.CourseProfessorRepository;
import com.example.helloworld.repositories.CourseRepository;
import com.example.helloworld.repositories.StudentCourseRepository;
import com.example.helloworld.repositories.UserRepository;

@Service
public class CourseService {

    @Autowired
    private CourseProfessorRepository courseProfessorRepository;

    @Autowired
    private StudentCourseRepository studentCourseRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseEvaluationCriteriaRepository courseEvaluationCriteriaRepository;
        
    public List<CourseDto> getProfessorCourses(String userId) throws SQLException {
        //return DatabaseHandler.getInstance().select();
        Optional<Userr> docente = userRepository.findById(userId);
        List<CourseProfessor> courseProfessors = courseProfessorRepository.findByIdDocente(docente);
        List<CourseDto> cursadas = new ArrayList<>();
        for (CourseProfessor courseProfessor : courseProfessors) {
            // Accede a la información de CourseProfessor y Course
            Course course = courseProfessor.getCursada();
            Comission comission = course.getComision();
            Subject asignatura = comission.getAsignatura();
            // Realiza acciones con los objetos CourseProfessor y Course encontrados
            CourseDto cursada = new CourseDto();
            cursada.setNombreAsignatura(asignatura.getNombre());
            cursada.setNumeroComision(comission.getId());
            cursada.setAnio(course.getAnio());
            cursadas.add(cursada);
        }
        return cursadas;
    }

    public void calculateFinalCondition(long courseId) {

        // Calcular Condicion Final de los alumnos de la cursada del docente.

        // Recuperamos la cursada asociada.

        Optional<Course> cursada = courseRepository.findById(courseId);

        // Recuperamos los criterios de evaluacion asociados a dicha cursada.

        List<CourseEvaluationCriteria> criteriosCursada = courseEvaluationCriteriaRepository.findById(courseId);

        // Recuperamos los alumnos asociados a dicha cursada.
        
        List<CourseStudent> alumnosCursada = studentCourseRepository.findByCursada(cursada);
        
        
        // Evaluamos a cada alumno.
        
        for (CourseStudent alumnoCursada : alumnosCursada) {
        
            // Iteramos por cada criterio de la cursada.

            for (CourseEvaluationCriteria criterioCursada : criteriosCursada) {

                switch (criterioCursada.getCriteria().getName()) {
                    
                    case "Asistencias":
                    String condicionAsistencia = evaluarAsistencia(courseId, alumnoCursada.getAlumno());
                    break;

                    case "Trabajos prácticos aprobados":
                    String condicionTPsAprobados = evaluarTPsAprobados(courseId, alumnoCursada.getAlumno());
                    break;

                    case "Trabajos prácticos recuperados":
                    String condicionTPsRecuperados = evaluarTPsRecupeados(courseId, alumnoCursada.getAlumno());
                    break;

                    case "Parciales aprobados":
                    String condicionParcialesAprobados = evaluarParcialesAprobados(courseId, alumnoCursada.getAlumno());
                    break;

                    case "Promedio de Parciales":
                    String condicionPromedioParciales = evaluarPromedioParciales(courseId, alumnoCursada.getAlumno());
                    break;

                    case "Autoevaluaciones aprobadas":
                    String condicionAEAprobadas = evaluarAEAprobadas(courseId, alumnoCursada.getAlumno());
                    break;

                    case "Autoevaluaciones recuperadas":
                    String condicionAERecuperadas = evaluarAERecuperadas(courseId, alumnoCursada.getAlumno());
                    break;
                
                }


            }

        }

    }

    private String evaluarAERecuperadas(long courseId, Student alumno) {
        return null;
    }

    private String evaluarAEAprobadas(long courseId, Student alumno) {
        return null;
    }

    private String evaluarPromedioParciales(long courseId, Student alumno) {
        return null;
    }

    private String evaluarParcialesAprobados(long courseId, Student alumno) {
        return null;
    }

    private String evaluarTPsRecupeados(long courseId, Student alumno) {
        return null;
    }

    private String evaluarTPsAprobados(long courseId, Student alumno) {
        return null;
    }

    private String evaluarAsistencia(long courseId, Student alumno) {
        return null;
    }
    
}
