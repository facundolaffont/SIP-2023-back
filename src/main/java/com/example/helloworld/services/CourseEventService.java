package com.example.helloworld.services;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.helloworld.models.Course;
import com.example.helloworld.models.CourseEvent;
import com.example.helloworld.models.EventType;
import com.example.helloworld.models.Student;
import com.example.helloworld.models.StudentCourseEvent;
import com.example.helloworld.models.Exceptions.EmptyQueryException;
import com.example.helloworld.repositories.CourseEventRepository;
import com.example.helloworld.repositories.CourseProfessorRepository;
import com.example.helloworld.repositories.CourseRepository;
import com.example.helloworld.repositories.EventTypeRepository;
import com.example.helloworld.repositories.StudentCourseEventRepository;
import com.example.helloworld.repositories.StudentRepository;
import com.example.helloworld.requests.Attendance;
import com.example.helloworld.requests.AttendanceRegistrationOnEvent_Request;
import com.example.helloworld.requests.Calification;
import com.example.helloworld.requests.CalificationsRegistrationOnEvent_Request;
import com.example.helloworld.requests.EventsRegistrationCheckRequest;
import com.example.helloworld.requests.NewCourseEventRequest;
import com.example.helloworld.requests.NewEventsBulkRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Service
public class CourseEventService {

    /**
     * Verifica si existe un ID de evento en sistema.
     * 
     * Si no existe, arroja una excepción.
     * 
     * @param eventId
     * 
     * @throws EmptyQueryException
     */
    public void checkIfEventExists(Long eventId) 
        throws EmptyQueryException
    {

        courseEventRepository
            .findById(eventId)
            .orElseThrow(() -> new EmptyQueryException(
                "No existe el evento con ID %s".formatted(eventId)
            ));

    }

    /**
     * Crea y registra un evento.
     * 
     * @param newCourseEventRequest
     * @return El evento creado.
     */
    public Object create(
        NewCourseEventRequest newCourseEventRequest
    )  {
        
        // Loguea los datos que se quieren insertar.
        logger.debug(
            String.format(
                "Se ejecutó el método create. [newCourseEventRequest = %s]",
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

        return newCourseEvent;

    }

    /**
     * Crea eventos de forma masiva.
     * 
     * @param newEventsBulkRequest
     * @return La lista de eventos que se crearon.
     */
    public Object createEventsBulk(
        NewEventsBulkRequest newEventsBulkRequest
    )  {
        
        logger.debug(
            String.format(
                "Se ejecutó el método createEventsBulk. [newEventsBulkRequest = %s]",
                newEventsBulkRequest
            )
        );

        // Obtiene cursada.
        Course course = courseRepository.findById(
            newEventsBulkRequest.getCourseId()
        ).get();

        /* [1] Guarda los eventos y crea la información que será devuelta al front */
        
        @Data class Response {

            public void addOk(
                Integer eventTempId
            ) {
                ok.add(
                    new Ok(
                        eventTempId
                    )
                );
            }

            /* public void addNotOk(
                Integer dossier,
                Integer errorCode
            ) {
                nok.add(
                    new NotOk(
                        dossier,
                        errorCode
                    )
                );
            } */


            /* Private */

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            static class Ok {
                private Integer eventTempId;
            }

            /* @Data
            @NoArgsConstructor
            @AllArgsConstructor
            static class NotOk {
                private Integer dossier;
                private Integer errorCode;
            } */

            private List<Ok> ok = new ArrayList<Ok>();
            //private List<NotOk> nok = new ArrayList<NotOk>();

        }

        Response response = new Response();
        for (NewEventsBulkRequest.Event event : newEventsBulkRequest.getEventsList()) {
        
            // Obtiene tipo de evento.
            EventType eventType = eventTypeRepository.findById(
                event.getEventTypeId()
            ).get();

            //  Crea el objeto del evento, que va a ser persistido en la BD.
            CourseEvent newCourseEvent = new CourseEvent();
            newCourseEvent.setCursada(course);
            newCourseEvent.setObligatorio(event.getObligatory());
            newCourseEvent.setTipoEvento(eventType);
            newCourseEvent.setFechaHoraInicio(
                Timestamp.valueOf(event.getInitialDatetime())
            );
            newCourseEvent.setFechaHoraFin(
                Timestamp.valueOf(event.getEndDatetime())
            );

            // Guarda el evento.
            courseEventRepository.save(newCourseEvent);

            // Guarda el evento en la lista de eventos registrados.
            response.addOk(event.getEventTempId());

        }

        /* [1] */

        return response;

    }

    // Devuelve una lista de eventos que pueden registrarse y una lista de eventos
    // que no se pueden registrar junto con la descripción de la razón.
    public Object eventsRegistrationCheck(
        EventsRegistrationCheckRequest eventsRegistrationCheckRequest
    ) {

        /**
         * 1. Separa los eventos que no se pueden registrar porque ya existen en la cursada.
         * 
         * 2. Construye la lista a devolver, asumiendo que todavía no se hacen chequeos.
         */

        /* 1 */

        

        /* 2 */

         @Data class Response {

            public void addOk(
                Integer eventTempId,
                String eventDescription
            ) {
                ok.add(
                    new Ok(
                        eventTempId,
                        eventDescription
                    )
                );
            }

            /* public void addNotOk(
                Integer dossier,
                Integer errorCode
            ) {
                nok.add(
                    new NotOk(
                        dossier,
                        errorCode
                    )
                );
            } */


            /* Private */

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            static class Ok {
                private Integer eventTempId;
                private String eventDescription;
            }

            /* @Data
            @NoArgsConstructor
            @AllArgsConstructor
            static class NotOk {
                private Integer dossier;
                private Integer errorCode;
            } */

            private List<Ok> ok = new ArrayList<Ok>();
            //private List<NotOk> nok = new ArrayList<NotOk>();

        }

        // 1.
        List<EventsRegistrationCheckRequest.Event> receivedEvents = new ArrayList<EventsRegistrationCheckRequest.Event>();
        List<CourseEvent> registrableEvents = new ArrayList<CourseEvent>();
        var response = new Response();
        for (EventsRegistrationCheckRequest.Event event : eventsRegistrationCheckRequest.getEventsList()) {
            
            // Obtiene el tipo de evento.
            EventType eventType = eventTypeRepository.getById(event.getEventTypeId());

            /* // Obtiene la cursada.
            Course course = courseRepository.getById(eventsRegistrationCheckRequest.getCourseId());

            // Crea el objeto que representa al evento.
            var newCourseEvent = new CourseEvent();
            newCourseEvent.setTipoEvento(eventType);
            newCourseEvent.setCursada(course);
            newCourseEvent.setFechaHoraInicio(
                Timestamp.valueOf(event.getInitialDatetime())
            );
            newCourseEvent.setFechaHoraFin(
                Timestamp.valueOf(event.getEndDatetime())
            );
            newCourseEvent.setObligatorio(event.getObligatory());

            // Agrega al objeto a la lista de eventos registrables.
            registrableEvents.add(newCourseEvent); */

            response.addOk(
                event.getEventTempId(),
                eventType.getNombre()
            );

            /* for (int index = 0; index < notExistentStudentsDossierList.size(); index++) {
                response.addNotOk(
                    notExistentStudentsDossierList.get(index),
                    1
                );
            }
            for (int index = 0; index < registeredStudentsList.size(); index++) {
                response.addNotOk(
                    registeredStudentsList
                        .get(index)
                        .getLegajo(),
                    2
                );
            } */

        }

        return response;

    }

    public Object getAllEventsInfo(Long courseId) throws EmptyQueryException {

        @Data class Response {

            public void addEventDetail(
                Long eventId,
                String eventType,
                Timestamp initialDatetime,
                Timestamp endDatetime,
                Integer studentDossier,
                Integer studentId,
                String studentName,
                String studentSurname,
                Boolean attendance,
                String note
            ) {
                eventsDetailsList.add(
                    new EventRegister(
                        eventId,
                        eventType,
                        initialDatetime,
                        endDatetime,
                        studentDossier,
                        studentId,
                        studentName,
                        studentSurname,
                        attendance,
                        note
                    )
                );
            }


            /* Private */

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            static class EventRegister {
                private Long eventId;
                private String eventType;
                private Timestamp initialDatetime;
                private Timestamp endDatetime;
                private Integer studentDossier;
                private Integer studentId;
                private String studentName;
                private String studentSurname;
                private Boolean attendance;
                private String note;
            }

            private List<EventRegister> eventsDetailsList = new ArrayList<EventRegister>();

        }

        // Obtiene la cursada.
        Course course = courseRepository
        .findById(courseId)
        .orElseThrow(() -> 
            new EmptyQueryException("No existe la cursada.")
        );

        // Obtiene una lista con todos los eventos de cursada.
        List<CourseEvent> courseEventList = courseEventRepository
        .findByCursada(course)
        .orElse(null);

        // Obtiene todos los registros de todos los eventos.
        List<StudentCourseEvent> studentCourseEventList = studentCourseEventRepository
        .findByEventoCursadaIn(courseEventList)
        .orElse(null);

        /* Prepara y devuelve la información. */
        Response response = new Response();
        for (StudentCourseEvent studentCourseEvent : studentCourseEventList) {
            response.addEventDetail(
                studentCourseEvent.getEventoCursada().getId(),
                studentCourseEvent.getEventoCursada().getTipoEvento().getNombre(),
                studentCourseEvent.getEventoCursada().getFechaHoraInicio(),
                studentCourseEvent.getEventoCursada().getFechaHoraFin(),
                studentCourseEvent.getAlumno().getLegajo(),
                studentCourseEvent.getAlumno().getDni(),
                studentCourseEvent.getAlumno().getNombre(),
                studentCourseEvent.getAlumno().getApellido(),
                studentCourseEvent.getAsistencia(),
                studentCourseEvent.getNota()
            );
        }

        return response;

    }

    public Object getEventInfo(Long eventId) throws EmptyQueryException {

        @Data class Response {

            public void addEventRegister(
                Integer studentDossier,
                Integer studentId,
                String studentName,
                String studentSurname,
                Boolean attendance,
                String note
            ) {
                eventRegistersList.add(
                    new EventRegister(
                        studentDossier,
                        studentId,
                        studentName,
                        studentSurname,
                        attendance,
                        note
                    )
                );
            }


            /* Private */

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            static class EventRegister {
                private Integer studentDossier;
                private Integer studentId;
                private String studentName;
                private String studentSurname;
                private Boolean attendance;
                private String note;
            }

            private List<EventRegister> eventRegistersList = new ArrayList<EventRegister>();

        }

        // Obtiene el objeto que representa al evento.
        CourseEvent courseEvent = courseEventRepository
        .findById(eventId)
        .orElseThrow(() -> 
            new EmptyQueryException("No existe el evento con ID %d".formatted(eventId))
        );

        // Obtiene todos los registros del evento.
        List<StudentCourseEvent> studentCourseEventList = studentCourseEventRepository
        .findByEventoCursada(courseEvent)
        .orElse(null);

        /* Prepara y devuelve la información. */
        Response response = new Response();
        for (StudentCourseEvent studentCourseEvent : studentCourseEventList) {
            response.addEventRegister(
                studentCourseEvent.getAlumno().getLegajo(),
                studentCourseEvent.getAlumno().getDni(),
                studentCourseEvent.getAlumno().getNombre(),
                studentCourseEvent.getAlumno().getApellido(),
                studentCourseEvent.getAsistencia(),
                studentCourseEvent.getNota()
            );
        }

        return response;

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

    /**
     * Devuelve la lista de legajos de estudiante de aquellos legajos pasados por parámetro que ya
     * estén registrados en el evento pasado por parámetro.
     *
     * Precondiciones: los legajos y el evento deben estar registrados en el sistema.
     *
     * @param dossiersList 
     * @param eventId
     */
    public List<Integer> getRegisteredDossiersInEvent(List<Integer> dossiersList, Long eventId) {

        // Obtiene la lista de estudiantes a partir de la lista de legajos.
        List<Student> studentsList = studentRepository
            .findByLegajoIn(dossiersList)
            .orElse(null);

        // Obtiene el evento de cursada.
        CourseEvent courseEvent = courseEventRepository
            .getById(eventId);

        // Obtiene la lista de legajos que están registrados en el evento, y lo devuelve.
        List<StudentCourseEvent> studentsCourseEventList = studentCourseEventRepository
            .findByEventoCursadaAndAlumnoIn(
                courseEvent,
                studentsList
            )
            .orElse(null);
        List<Integer> registeredStudentDossiers = studentsCourseEventList
            .stream()
            .map(studentCourseEvent ->
                studentCourseEvent
                    .getAlumno()
                    .getLegajo()
            )
            .collect(Collectors.toList());
        return registeredStudentDossiers; 

    }

    /**
     * Registra las calificaciones en un evento específico.
     */
    public ResponseEntity<Object> registerAttendanceOnEvent(
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

        return ResponseEntity
            .status(HttpStatus.OK)
            .build();
        
    }

    /**
     * Registra las calificaciones en un evento específico.
     */
    public ResponseEntity<Object> registerCalificationsOnEvent(
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

        return ResponseEntity
            .status(HttpStatus.OK)
            .build();
        
    }


    /* Private */

    private static final Logger logger = LoggerFactory.getLogger(CourseEventService.class);

    @Autowired private CourseEventRepository courseEventRepository;
    @Autowired private CourseProfessorRepository courseProfessorRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private EventTypeRepository eventTypeRepository;
    @Autowired private StudentCourseEventRepository studentCourseEventRepository;
    @Autowired private StudentRepository studentRepository;

}
