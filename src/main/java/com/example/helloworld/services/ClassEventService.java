package com.example.helloworld.services;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.helloworld.models.CourseEvent;
import com.example.helloworld.models.Student;
import com.example.helloworld.models.StudentCourseEvent;
import com.example.helloworld.repositories.CourseEventRepository;
import com.example.helloworld.repositories.CourseRepository;
import com.example.helloworld.repositories.EventTypeRepository;
import com.example.helloworld.repositories.StudentCourseEventRepository;
import com.example.helloworld.repositories.StudentRepository;
import com.example.helloworld.requests.Attendance;
import com.example.helloworld.requests.AttendanceRegistrationOnEvent_Request;
import com.example.helloworld.requests.Calification;
import com.example.helloworld.requests.CalificationsRegistrationOnEvent_Request;
import com.example.helloworld.requests.NewCourseEventRequest;

@Service
public class ClassEventService {

    /**
     * Registra las calificaciones en un evento específico.
     */
    public ResponseEntity<String> registerCalificationsOnEvent(
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

        /**
         * Obtiene el objeto CourseEvent (A), y por cada calificación obtiene el objeto
         * Student (B), correspondientes a los datos que son enviados en la petición,
         * para luego insertar los registros en la tabla 'evento_cursada_alumno' (C).
         * 
         * TODO:
         *  + Validación de (A).
         *  + Validación de (B)
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
            studentCourseEvent.setEventoCursada(courseEventRegister.get());
            studentCourseEvent.setAlumno(studentRegister.get());
            studentCourseEvent.setAsistencia(true);
            studentCourseEvent.setNota(calification.getCalification());
            studentCourseEvent = studentCourseEventRepository.save(studentCourseEvent);

        }

        var returningJson = (new JSONObject()).put("Respuesta", "OK.");
        var statusCode = HttpStatus.OK;

        return ResponseEntity
            .status(statusCode)
            .body(
                returningJson.toString()
            );
        
    }

    /**
     * Registra las calificaciones en un evento específico.
     */
    public ResponseEntity<String> registerAttendanceOnEvent(
        AttendanceRegistrationOnEvent_Request attendanceRegistrationOnEvent_Request
    )
        throws SQLException
    {

        logger.debug(
            String.format(
                "Se ejecuta el método registerAttendanceOnEvent. [attendanceRegistrationOnEvent_Request = %s]",
                attendanceRegistrationOnEvent_Request.toString()
            )
        );

        // /**
        //  * Obtiene el objeto CourseEvent (A), y por cada calificación obtiene el objeto
        //  * Student (B), correspondientes a los datos que son enviados en la petición,
        //  * para luego insertar los registros en la tabla 'evento_cursada_alumno' (C).
        //  * 
        //  * TODO:
        //  *  + Validación de (A).
        //  *  + Validación de (B)
        //  */
        // (A)
        Optional<CourseEvent> courseEventRegister = courseEventRepository.findById(
            attendanceRegistrationOnEvent_Request.getCourseEventId()
        );

        for (Attendance attendance: attendanceRegistrationOnEvent_Request.getAttendance()) {
            
            // (B)
            Optional<Student> studentRegister = studentRepository.findById(
                attendance.getStudentDossier()
            );

            // (C)
            var studentCourseEvent = new StudentCourseEvent();
            studentCourseEvent.setEventoCursada(courseEventRegister.get());
            studentCourseEvent.setAlumno(studentRegister.get());
            studentCourseEvent.setAsistencia(attendance.getAttendance());
            studentCourseEvent = studentCourseEventRepository.save(studentCourseEvent);

        }

        var returningJson = (new JSONObject()).put("Respuesta", "OK.");
        var statusCode = HttpStatus.OK;

        return ResponseEntity
            .status(statusCode)
            .body(
                returningJson.toString()
            );
        
    }

    public ResponseEntity<String> create(
        NewCourseEventRequest newCourseEventRequest
    )  {
        
        // Loguea los datos que se quieren insertar.
        logger.debug(
            String.format(
                "Se ejecuta el método registerCalificationsOnEvent. [newEventRequest = %s]",
                newCourseEventRequest
            )
        );

        // Obtiene cursada.
        var linkedCourse = courseRepository.findById(
            newCourseEventRequest.getIdCursada()
        );

        // Obtiene tipo de evento.
        var linkedEventType = eventTypeRepository.findById(
            newCourseEventRequest.getTipoEvento()
        );

        //  Crea y guarda evento.
        var newCourseEvent = new CourseEvent();
        newCourseEvent.setCursada(linkedCourse.get());
        newCourseEvent.setObligatorio(newCourseEventRequest.getObligatorio());
        newCourseEvent.setTipoEvento(linkedEventType.get());
        newCourseEvent.setFechaHoraInicio(
            Timestamp.valueOf(newCourseEventRequest.getFechaInicio())
        );
        newCourseEvent.setFechaHoraFin(
            Timestamp.valueOf(newCourseEventRequest.getFechaFin())
        );
        courseEventRepository.save(newCourseEvent);

        var returningJson = (new JSONObject()).put("Respuesta", "OK.");
        var statusCode = HttpStatus.OK;

        return ResponseEntity
            .status(statusCode)
            .body(
                returningJson.toString()
            );

    }

    public List<CourseEvent> getEvents(String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            LocalDateTime startDateTime = parsedDate.atTime(LocalTime.MIN);
            LocalDateTime endDateTime = parsedDate.atTime(LocalTime.MAX);
            long startTimestamp = startDateTime.toEpochSecond(ZoneOffset.UTC);
            long endTimestamp = endDateTime.toEpochSecond(ZoneOffset.UTC);
            Date startDate = new Date(startTimestamp * 1000); // Conversión a java.util.Date
            Date endDate = new Date(endTimestamp * 1000); // Conversión a java.util.Date
            return courseEventRepository.findByFechaHoraInicioBetween(startDate, endDate);
        } catch (DateTimeParseException e) {
            // Manejar la excepción si ocurre un error al analizar la fecha
            // Puedes lanzar una excepción personalizada, retornar una lista vacía, etc.
            return null;
        }
    }

    /* Private */

    private static final Logger logger = LoggerFactory.getLogger(ClassEventService.class);

    @Autowired private CourseEventRepository courseEventRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private EventTypeRepository eventTypeRepository;
    @Autowired private StudentCourseEventRepository studentCourseEventRepository;
    @Autowired private StudentRepository studentRepository;



}