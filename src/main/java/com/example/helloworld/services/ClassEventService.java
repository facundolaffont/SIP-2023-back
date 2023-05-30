package com.example.helloworld.services;

import java.sql.SQLException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.helloworld.models.Calification;
import com.example.helloworld.models.CourseEvent;
import com.example.helloworld.models.Student;
import com.example.helloworld.models.StudentCourseEvent;
import com.example.helloworld.repositories.CourseEventRepository;
import com.example.helloworld.repositories.StudentCourseEventRepository;
import com.example.helloworld.repositories.StudentRepository;
import com.example.helloworld.requests.CalificationsRegistrationOnEvent_Request;

@Service
public class ClassEventService {

    /**
     * Registra las calificaciones en un evento específico.
     */
    public void registerCalificationsOnEvent(
        CalificationsRegistrationOnEvent_Request calificationsRegistrationOnEvent_Request
    )
        throws SQLException
    {

        logger.debug(
            String.format(
                "Se ejecuta el método registerCalificationsOnEvent. [calificationsRegistrationOnEvent_Request = %s]",
                calificationsRegistrationOnEvent_Request.toString()
            )
        );
        logger.debug("courseEventRepository = " + courseEventRepository.toString());
        logger.debug("studentCourseEventRepository = " + studentCourseEventRepository.toString());
        logger.debug("studentRepository = " + studentRepository.toString());

        /**
         * Obtiene el objeto CourseEvent (A), y por cada calificación obtiene el objeto
         * Student (B), correspondientes a los datos que son enviados en la petición,
         * para luego insertar los registros en la tabla 'evento_cursada_alumno' (C).
         * 
         * TODO:
         *  + Validación de (A).
         *  + Validación de (B).
         */
        // (A)
        Optional<CourseEvent> courseEventRegister = courseEventRepository.findById(
            calificationsRegistrationOnEvent_Request.getCourseEventId()
        );

        for (Calification calification: calificationsRegistrationOnEvent_Request.getCalifications()) {
            
            // (B)
            Optional<Student> studentRegister = studentRepository.findById(
                calification.getStudentDossier()
            );

            // (C)
            var studentCourseEvent = new StudentCourseEvent();
            studentCourseEvent.setIdEvento(courseEventRegister.get());
            studentCourseEvent.setIdAlumno(studentRegister.get());
            studentCourseEvent.setAsistencia(true);
            studentCourseEvent.setNota(calification.getCalification());
            studentCourseEvent = studentCourseEventRepository.save(studentCourseEvent);

        }
        
    }


    /* Private */

    private static final Logger logger = LoggerFactory.getLogger(ClassEventService.class);

    @Autowired private CourseEventRepository courseEventRepository;
    @Autowired private StudentCourseEventRepository studentCourseEventRepository;
    @Autowired private StudentRepository studentRepository;

}
