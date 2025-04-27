package ar.edu.unlu.spgda.services;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ar.edu.unlu.spgda.models.Career;
import ar.edu.unlu.spgda.models.Comission;
import ar.edu.unlu.spgda.models.Course;
import ar.edu.unlu.spgda.models.CourseDto;
import ar.edu.unlu.spgda.models.CourseEvaluationCriteria;
import ar.edu.unlu.spgda.models.CourseEvent;
import ar.edu.unlu.spgda.models.CourseProfessor;
import ar.edu.unlu.spgda.models.CourseStudent;
import ar.edu.unlu.spgda.models.EvaluationCriteria;
import ar.edu.unlu.spgda.models.EventType;
import ar.edu.unlu.spgda.models.Student;
import ar.edu.unlu.spgda.models.StudentCourseEvent;
import ar.edu.unlu.spgda.models.Subject;
import ar.edu.unlu.spgda.models.Userr;
import ar.edu.unlu.spgda.models.Exceptions.EmptyQueryException;
import ar.edu.unlu.spgda.models.Exceptions.NotAuthorizedException;
import ar.edu.unlu.spgda.repositories.CourseEvaluationCriteriaRepository;
import ar.edu.unlu.spgda.repositories.CourseEventRepository;
import ar.edu.unlu.spgda.repositories.CourseProfessorRepository;
import ar.edu.unlu.spgda.repositories.CourseRepository;
import ar.edu.unlu.spgda.repositories.CourseStudentRepository;
import ar.edu.unlu.spgda.repositories.EvaluationCriteriaRepository;
import ar.edu.unlu.spgda.repositories.EventTypeRepository;
import ar.edu.unlu.spgda.repositories.StudentCourseEventRepository;
import ar.edu.unlu.spgda.repositories.StudentCourseRepository;
import ar.edu.unlu.spgda.repositories.StudentRepository;
import ar.edu.unlu.spgda.repositories.UserRepository;
import ar.edu.unlu.spgda.requests.AttendanceRegistrationRequest;
import ar.edu.unlu.spgda.requests.CalificationRegistrationRequest;
import ar.edu.unlu.spgda.requests.DeleteEventRequest;
import ar.edu.unlu.spgda.requests.DossiersAndEventRequest;
import ar.edu.unlu.spgda.requests.FinalConditions;
import ar.edu.unlu.spgda.requests.StudentFinalCondition;
import ar.edu.unlu.spgda.requests.StudentsRegistrationRequest;
import ar.edu.unlu.spgda.requests.UpdateEventRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Service
public class CourseService {

    /**
     * Calcula la condición final de los alumnos de una cursada.
     * 
     * Para el retorno, genera un arreglo JSON con la siguiente estructura:
     * 
     * <pre>
     *      [
     *          {
     *              "Legajo":150647,
     *              "Condición":"P"
     *          },
     *          ...
     *      ]
     * </pre>
     * 
     * @param courseId - ID de la cursada de la cual se calculará la condición final.
     * @return Como cuerpo del ResponseEntity, devuelve el arreglo JSON
     * descrito anteriormente.
     * @throws EmptyQueryException
     */
    public ResponseEntity<Object> calculateFinalCondition(long courseId)
        throws EmptyQueryException
    {

        logger.debug(String.format(
            "Se ejecuta el método calculateFinalCondition. [courseId = %d]",
            courseId
        ));

        Course course = recuperarCursada(courseId);
        List<CourseEvaluationCriteria> criteriosCursada = recuperarCriteriosCursada(course);
        List<CourseStudent> courseStudentList = recuperarAlumnosCursada(course);

        // Evaluamos a cada alumno.
        JSONArray returningJson = new JSONArray();

        for (CourseStudent alumnoCursada : courseStudentList) {
            boolean ausente = evaluarAusencia(alumnoCursada);
            JSONObject studentResult = evaluarAlumno(course, criteriosCursada, alumnoCursada, ausente);
            returningJson.put(studentResult);
        }

        return (ResponseEntity
            .status(HttpStatus.OK)
            .header("Content-Type", "application/json")
            .body(returningJson.toString())
        );

    }

    private Course recuperarCursada(Long courseId) throws EmptyQueryException {

        // Recuperamos la cursada asociada.
        Course course =
            courseRepository // Tabla 'course'.
            .findById(courseId)
            .orElseThrow(
                () -> new EmptyQueryException(
                    String.valueOf(String.format(
                        "No se encontró ningún registro con el ID de cursada %d",
                        courseId
                    ))
                )
            );
        return course;
    }

    private List<CourseEvaluationCriteria> recuperarCriteriosCursada(Course course) throws EmptyQueryException {
        
        // Recuperamos los criterios de evaluacion asociados a dicha cursada.
        List<CourseEvaluationCriteria> criteriosCursada =
        courseEvaluationCriteriaRepository // Tabla 'criterio_cursada'.
        .findByCourse(course)
        .orElseThrow(
            () -> new EmptyQueryException(String.format(
                "No se encontró ningún registro con la cursada proporcionada. [cursada = %s]",
                course.toString()
            ))
        );

        return criteriosCursada;
    }

    private List<CourseStudent> recuperarAlumnosCursada(Course course) throws EmptyQueryException {
        
        // Recuperamos los alumnos asociados a dicha cursada.
        List<CourseStudent> courseStudentList =
            studentCourseRepository // Tabla 'cursada_alumno'.
            .findByCursada(course)
            .orElseThrow(
                () -> new EmptyQueryException(String.format(
                    "No se encontró ningún registro con la cursada proporcionada. [cursada = %s]",
                    course.toString()
                ))
            );

        return courseStudentList;
    }

    public boolean evaluarAusencia(CourseStudent alumnoCursada) {

        Optional<EventType> eventType =
        eventTypeRepository
        .findByNombre("Clase");
        
        Optional<List<CourseEvent>> eventosCursadaNotClase = courseEventRepository.findByCursadaAndTipoEventoNot
            (alumnoCursada.getCursada(), eventType.get());
        
        int contadorEventosAlumno = 0;

        for (CourseEvent eventoCursadaNotClase: eventosCursadaNotClase.get()) {
            
            Optional<StudentCourseEvent> studentCourseEvent
            = studentCourseEventRepository
            .findByEventoCursadaAndAlumno(eventoCursadaNotClase, alumnoCursada.getAlumno());

            if (studentCourseEvent.isPresent())
                contadorEventosAlumno++;

        }

        if (contadorEventosAlumno > 0)
            return false;
        else
            return true;
    }

    public JSONObject evaluarAlumno(Course course, List<CourseEvaluationCriteria> criteriosCursada, CourseStudent alumnoCursada, boolean ausente) {

        // Iteramos por cada criterio de la cursada.
        var newStudentRegister = (new JSONObject())    
        .put("Legajo", alumnoCursada.getAlumno().getLegajo());
        newStudentRegister.put("Correlativas", alumnoCursada.isPreviousSubjectsApproved());
        JSONArray detalle = new JSONArray();
        String lowestCondition = "";
        if (!ausente) {
            for (CourseEvaluationCriteria criterioCursada : criteriosCursada) {
                
                try {

                    switch (criterioCursada.getCriteria().getName()) {

                        case "Asistencias":
                            ArrayList<String> results = evaluarAsistencia(course, alumnoCursada.getAlumno());
                            JSONObject resultadoAsistencia = new JSONObject();
                            resultadoAsistencia.put("Criterio", "Asistencias");
                            resultadoAsistencia.put("Condición", results.get(1));
                            resultadoAsistencia.put("Porcentaje de Asistencia", results.get(0));
                            detalle.put(resultadoAsistencia);
                            lowestCondition =
                                lowestCondition.isEmpty()
                                ? results.get(1)
                                : getMinimalCondition(lowestCondition, results.get(1));
                        break;

                        case "Trabajos prácticos aprobados":
                            String condicionTPsAprobados = evaluarTPsAprobados(course, alumnoCursada.getAlumno());
                            JSONObject resultadoTPSA = new JSONObject();
                            resultadoTPSA.put("Criterio", "Trabajos prácticos aprobados");
                            resultadoTPSA.put("Condición", condicionTPsAprobados);
                            detalle.put(resultadoTPSA);
                            if (condicionTPsAprobados != null) {
                            lowestCondition =
                                lowestCondition.isEmpty()
                                ? condicionTPsAprobados
                                : getMinimalCondition(lowestCondition, condicionTPsAprobados);
                            }
                        break;

                        case "Trabajos prácticos recuperados":
                            String condicionTPsRecuperados = evaluarTPsRecuperados(course, alumnoCursada.getAlumno());
                            JSONObject resultadoTPSR = new JSONObject();
                            resultadoTPSR.put("Criterio", "Trabajos prácticos recuperados");
                            resultadoTPSR.put("Condición", condicionTPsRecuperados);
                            detalle.put(resultadoTPSR);
                            if (condicionTPsRecuperados != null) {
                            lowestCondition =
                                lowestCondition.isEmpty()
                                ? condicionTPsRecuperados
                                : getMinimalCondition(lowestCondition, condicionTPsRecuperados);
                            }
                        break;

                        case "Parciales recuperados":
                            String condicionParcialesRecuperados = evaluarParcialesRecuperados(course, alumnoCursada.getAlumno());
                            JSONObject resultadoParcialesR = new JSONObject();
                            resultadoParcialesR.put("Criterio", "Parciales recuperados");
                            resultadoParcialesR.put("Condición", condicionParcialesRecuperados);
                            detalle.put(resultadoParcialesR);
                            if (condicionParcialesRecuperados != null) {
                            lowestCondition =
                                lowestCondition.isEmpty()
                                ? condicionParcialesRecuperados
                                : getMinimalCondition(lowestCondition, condicionParcialesRecuperados);
                            }
                        break;

                        case "Parciales aprobados":
                            String condicionParcialesAprobados = evaluarParcialesAprobados(course, alumnoCursada.getAlumno());
                            JSONObject resultadoParcialesA = new JSONObject();
                            resultadoParcialesA.put("Criterio", "Parciales aprobados");
                            resultadoParcialesA.put("Condición", condicionParcialesAprobados);
                            detalle.put(resultadoParcialesA);
                            if (condicionParcialesAprobados != null) {
                            lowestCondition =
                                lowestCondition.isEmpty()
                                ? condicionParcialesAprobados
                                : getMinimalCondition(lowestCondition, condicionParcialesAprobados);
                            }
                        break;

                        case "Promedio de parciales":
                            String condicionPromedioParciales = evaluarPromedioParciales(course, alumnoCursada.getAlumno());
                            JSONObject resultadoPromedios = new JSONObject();
                            resultadoPromedios.put("Criterio", "Promedio de parciales");
                            resultadoPromedios.put("Condición", condicionPromedioParciales);
                            detalle.put(resultadoPromedios);
                            if (condicionPromedioParciales != null) {
                            lowestCondition =
                                lowestCondition.isEmpty()
                                ? condicionPromedioParciales
                                : getMinimalCondition(lowestCondition, condicionPromedioParciales);
                            }
                        break;

                        case "Autoevaluaciones aprobadas":
                            String condicionAEAprobadas = evaluarAEAprobadas(course, alumnoCursada.getAlumno());
                            JSONObject resultadosAEA = new JSONObject();
                            resultadosAEA.put("Criterio", "Autoevaluaciones aprobadas");
                            resultadosAEA.put("Condición", condicionAEAprobadas);
                            detalle.put(resultadosAEA);
                            if (condicionAEAprobadas != null) {
                            lowestCondition =
                                lowestCondition.isEmpty()
                                ? condicionAEAprobadas
                                : getMinimalCondition(lowestCondition, condicionAEAprobadas);
                            }
                        break;

                        case "Autoevaluaciones recuperadas":
                            String condicionAERecuperadas = evaluarAERecuperadas(course, alumnoCursada.getAlumno());
                            JSONObject resultadosAER = new JSONObject();
                            resultadosAER.put("Criterio", "Autoevaluaciones recuperadas");
                            resultadosAER.put("Condición", condicionAERecuperadas);
                            detalle.put(resultadosAER);
                            if (condicionAERecuperadas != null) {
                            lowestCondition =
                                lowestCondition.isEmpty()
                                ? condicionAERecuperadas
                                : getMinimalCondition(lowestCondition, condicionAERecuperadas);
                            }
                        break;

                    }

                    //if (lowestCondition.equals("L")) break;

                } catch (Exception e) {
                    System.out.println(e);
                }

            }
        } else 
            lowestCondition = "A";

        newStudentRegister
            .put(
                "Condición",
                lowestCondition
            );

        newStudentRegister
            .put(
                "Detalle",
                detalle
            );

        return newStudentRegister;
    } 

    /**
     * Devuelve una lista de legajos con información sobre su condición frente a un evento.
     * 
     * @param dossiersAndEventRequest
     * @return Una lista de legajos que pueden ser registrados en el evento y una lista de
     * legajos que no pueden ser registrados, junto con el motivo.
     * @throws EmptyQueryException
     */
    public Object checkDossiersInEvent(DossiersAndEventRequest dossiersAndEventRequest)
        throws EmptyQueryException
    {

        /*
         * 1. Obtiene los registros de la tabla evento_cursada_alumno que pertenecen al
         * al evento almacenado en dossiersAndEventRequest.
         * 
         * 1b. Selecciona los alumnos recibidos por parámetro que no existan.
         *
         * 1c. Selecciona, del resto de los alumnos, los que no están asociados con la cursada
         * del evento.
         *
         * 1d. Selecciona, del resto de los alumnos, los que están registrados ya en el evento.
         *
         * 1e. Selecciona el resto de los alumnos, que serán los que pueden registrarse en el evento.
         *
         * 4. Devuelve un objeto con una propiedad ok que se trate de una lista de objetos con los registros
         * de (1e), con propiedades dossier, id y name. También tendrá otra propiedad nok, que se trate
         * de una lista de objetos con los registros de (1b), (1c) y (1d), con propiedades dossier, errorCode y
         * description.
         */

        // Obtiene el objeto EventoCursada en base al ID de evento enviado por parámetro.
        CourseEvent courseEvent = courseEventRepository
            .findById(dossiersAndEventRequest.getEventId())
            .orElseThrow(() -> 
                new EmptyQueryException("El evento con ID %s no existe en el sistema.".formatted(
                    dossiersAndEventRequest.getEventId()
                ))
            );

        // // Obtiene la lista de alumnos de los legajos recibidos por parámetro.
        // List<Student> receivedStudentsList = studentRepository
        //     .getByLegajoIn(
        //         dossiersAndEventRequest.getDossiersList()
        //     );

        // // Obtiene todos los registros de alumnos que haya en un ID de evento.
        // List<StudentCourseEvent> studentCourseEventList = studentCourseEventRepository
        //     .findByEventoCursadaAndAlumnoIn(
        //         courseEvent,
        //         receivedStudentsList
        //     )
        //     .orElse(null);

        // // Obtiene el legajo de dichos registros.
        // List<Integer> dossiersInEventList = studentCourseEventList
        //     .stream()
        //     .map(studentCourseEvent ->
        //         studentCourseEvent
        //             .getAlumno()
        //             .getLegajo()
        //     )
        //     .collect(Collectors.toList());
         
        // Obtiene legajos recibidos por parámetro.
        List<Integer> receivedDossiersList = dossiersAndEventRequest.getDossiersList();

        List<Integer> existingDossiersList = studentService
            .getExistingDossiersFromDossiersList(receivedDossiersList);

        // 1b
        List<Integer> nonExistingDossiersList = receivedDossiersList
            .stream()
            .collect(Collectors.toList());
        nonExistingDossiersList.removeAll(existingDossiersList);

        // 1c
        List<Integer> dossiersInCourse = this
            .getRegisteredDossiersFromDossiersList(
                courseEvent.getCursada(),
                existingDossiersList
            );
        List<Integer> dossiersNotInCourse = existingDossiersList
            .stream()
            .collect(Collectors.toList());
        dossiersNotInCourse.removeAll(dossiersInCourse);

        // 1d
        List<Integer> dossiersInEvent = courseEventService
            .getRegisteredDossiersInEvent(
                dossiersInCourse,
                dossiersAndEventRequest.getEventId()
            );
        List<Integer> dossiersNotInEvent = dossiersInCourse
            .stream()
            .collect(Collectors.toList());

        // 1e
        dossiersNotInEvent.removeAll(dossiersInEvent);

        // // 2.4. Obtiene los registros de estudiante de los legajos recibidos.
        // // (estoy suponiendo que todos los legajos recibidos están registrados en el sistema).
        // List<Student> receivedStudentsList = studentRepository
        //     .findByLegajoIn(receivedDossiersList)
        //     .get();

        // // 2.4. Obtiene legajos recibidos en (2.2) que no estén
        // // en dossiersInEventList.
        // List<Integer> dossiersWithoutAttendanceInEvent = receivedDossiersList 
        //     .stream()
        //     .filter(dossier -> 
        //         !dossiersInEventList.contains(dossier)
        //     )
        //     .collect(Collectors.toList());

        /* 4 */

        // Definición de la clase del objeto que se va a retornar.
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        class Result {

            public void addOk(
                Integer dossier,
                Integer id,
                String name
            ) {
                ok.add(
                    new Student(
                        dossier,
                        id,
                        name
                    )
                );
            }

            public void addNok(
                Integer dossier,
                Integer errorCode,
                String errorDescription
            ) {
                nok.add(
                    new Error(
                        dossier,
                        errorCode,
                        errorDescription
                    )
                );
            }

            @Data
            @AllArgsConstructor
            static class Student {
                private Integer dossier;
                private Integer id;
                private String name;
            }

            @Data
            @AllArgsConstructor
            static class Error {
                private Integer dossier;
                private Integer errorCode;
                private String errorDescription;
            }

            private List<Student> ok = new ArrayList<Student>();
            private List<Error> nok = new ArrayList<Error>();
        }

        // Obtiene la lista de estudiantes de la lista de legajos registrables.
        List<Student> registrableStudents = studentRepository
            .findByLegajoIn(dossiersNotInEvent)
            .orElse(null);

        // Construye la respuesta.
        Result result = new Result();
        registrableStudents
            .forEach(registrableStudent -> {
                result.addOk(
                    registrableStudent.getLegajo(),
                    registrableStudent.getDni(),
                    registrableStudent.getNombre()
                );
            });
        dossiersInEvent
            .forEach(dossier -> {
                result.addNok(
                    dossier,
                    3,
                    "El legajo ya está registrado en el evento."
                );
            });
        dossiersNotInCourse
            .forEach(dossier -> {
                result.addNok(
                    dossier,
                    2,
                    "El legajo no está asociado con la cursada."
                );
            });
        nonExistingDossiersList
            .forEach(dossier -> {
                result.addNok(
                    dossier,
                    1,
                    "El legajo no está registrado en el sistema."
                );
            });


        return result;

        /***/

    }

    /**
     * Verifica que la cursada exista.
     * 
     * Si no existe, devuelve una excepción.
     * 
     * @param courseId
     * 
     * @throws EmptyQueryException
     */
    public void checkIfCourseExists(Long courseId)
        throws EmptyQueryException
    {
        courseRepository.findById(courseId)
            .orElseThrow(() -> new EmptyQueryException("La cursada no existe."));
    }

    /**
     * Verifica que un docente pertenezca a una cursada.
     *
     * Si no pertenece a la cursada, arroja una excepción.
     * 
     * Precondiciones: la cursada y el usuario deben existir.
     *
     * @param userId
     * @param courseId
     *
     * @throws NotAuthorizedException
     */
    public void checkProfessorInCourse(String userId, Long courseId)
        throws NotAuthorizedException
    {
        Course course = courseRepository.getById(courseId);
        Userr user = userRepository.getById(userId);
        courseProfessorRepository
            .findByCursadaAndIdDocente(course, user)
            .orElseThrow(() -> 
                new NotAuthorizedException("El docente no pertenece a la cursada.")
            );
    }

    /**
     * Verifica que un docente pertenezca a la cursada de un evento.
     *
     * Si no pertenece a la cursada, arroja una excepción.
     * 
     * Precondiciones: el evento y el usuario deben existir.
     *
     * @param userId
     * @param eventId
     *
     * @throws NotAuthorizedException
     */
    public void checkProfessorInCourseFromEvent(String userId, Long eventId)
        throws NotAuthorizedException
    {

        // Obtiene el objeto de la cursada.
        CourseEvent courseEvent = courseEventRepository.getById(eventId);
        Course course = courseEvent.getCursada();

        // Obtiene el objeto del usuario.
        Userr user = userRepository.getById(userId);

        // Checkea si el docente pertenece a la cursada. Si no pertenece, arroja
        // una excepción NotAuthorizedException.
        courseProfessorRepository
            .findByCursadaAndIdDocente(course, user)
            .orElseThrow(() -> 
                new NotAuthorizedException("El docente no pertenece a la cursada.")
            );

    }

    /**
     * Devuelve los eventos de una cursada.
     * 
     * @param courseId - Identificador único de cursada.
     * @param mode - Controla los valores devueltos, de forma tal que: si es 0,
     * devuelve todos los eventos; si es 1, devuelve sólo los eventos cuyo tipo
     * está especificado en el parámetro {@param eventType}; y si es 2, devuelve
     * todos los eventos, excepto aquellos cuyo tipo está especificado en el
     * parámetro {@param eventType}.
     * @param eventType - Especifica el tipo de evento cuando el parámetro
     * {@param mode} es 1 o 2. Si el valor de dicho parámetro es 0, el valor
     * de este parámetro es indistinto.
     * 
     * @return La lista de eventos creados.
     * 
     * @throws EmptyQueryException
     */
    public Object getEvents(Long courseId, Integer mode, Integer eventType)
        throws EmptyQueryException
    {

        /*
         * 0.Si el docente no pertenece a la cursada, devuelve mensaje de error.
         *
         * 1.Busca todos los eventos de courseId.
         *
         * 2.Construye la respuesta según HU002.007.001/CU01.0c y la devuelve.
         */

        // 1.
        // Busca todos los eventos de courseId utilizando courseEventRepository.findByCursada.
        Course course = courseRepository
            .findById(courseId)
            .orElseThrow(() -> new EmptyQueryException("No existe cursada con el ID " + courseId + "."));
        List<CourseEvent> courseEventList = courseEventRepository
            .findByCursada(course)
            .orElse(null);

        // 2.
        // Construye la respuesta y la devuelve.
        @Data class Result {

            public void addEventInfo(
                Long eventId,
                String type,
                String name,
                Timestamp initialDateTime,
                Timestamp endDateTime,
                boolean mandatory
            ) {
                eventList.add(new EventInfo(
                    eventId,
                    type,
                    name,
                    initialDateTime,
                    endDateTime,
                    mandatory
                ));
            }

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            class EventInfo {
                private Long eventId;
                private String type;
                private String name;
                private Timestamp initialDateTime;
                private Timestamp endDateTime;
                private boolean mandatory;
            }

            private List<EventInfo> eventList = new ArrayList<EventInfo>();

        }
        var result = new Result();
        courseEventList
            .stream()
            .forEach(event -> {
                if(
                    mode == 0
                    || (mode == 1 && event.getTipoEvento().getId() == eventType)
                    || (mode == 2 && event.getTipoEvento().getId() != eventType)
                )
                    result.addEventInfo(
                        event.getId(),
                        event.getTipoEvento().getNombre(),
                        event.getNombre(),
                        event.getFechaHoraInicio(),
                        event.getFechaHoraFin(),
                        event.isObligatorio()
                    );
            });
        return result;

    }

    /* public Object getEvaluationEvents(long courseId)
        throws EmptyQueryException
    {

        /*
         * 0.Si el docente no pertenece a la cursada, devuelve mensaje de error.
         *
         * 1.Busca todos los eventos de courseId.
         *
         * 2.Construye la respuesta según HU002.007.001/CU01.0c y la devuelve.
         */

        /* // 1.
        // Busca todos los eventos de courseId utilizando courseEventRepository.findByCursada.
        Course course = courseRepository
            .findById(courseId)
            .orElseThrow(() -> new EmptyQueryException("No existe cursada con el ID " + courseId + "."));
        List<CourseEvent> courseEventList = courseEventRepository
            .findByCursada(course)
            .orElse(null);

        // 2.
        // Construye la respuesta y la devuelve.
        @Data class Result {

            public void addEventInfo(
                Long eventId,
                String type,
                Timestamp initialDateTime,
                Timestamp endDateTime,
                boolean mandatory
            ) {
                eventList.add(new EventInfo(
                    eventId,
                    type,
                    initialDateTime,
                    endDateTime,
                    mandatory
                ));
            }

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            class EventInfo {
                private Long eventId;
                private String type;
                private Timestamp initialDateTime;
                private Timestamp endDateTime;
                private boolean mandatory;
            }

            private List<EventInfo> eventList = new ArrayList<EventInfo>();

        }
        var result = new Result();
        courseEventList
            .stream()
            .forEach(event -> {

                // Guarda el evento si no es un evento de clase; es decir,
                // si sólo es un evento de evaluación.
                if(event.getTipoEvento().getId() != 1) 
                    result.addEventInfo(
                        event.getId(),
                        event.getTipoEvento().getNombre(),
                        event.getFechaHoraInicio(),
                        event.getFechaHoraFin(),
                        event.isObligatorio()
                    );
                
            });
        return result;

    } */

    public List<CourseDto> getProfessorCourses(String userId)
        throws SQLException, EmptyQueryException
    {

        logger.debug(String.format(
            "Se ejecuta el método getProfessorCourses. [userId = %s]",
            userId
        ));

        // Obtiene el objeto del docente.
        Userr docente = userRepository
            .findById(userId)
            .orElseThrow(() ->
                new EmptyQueryException("El docente con ID %s no existe.".formatted(
                    userId
                ))
            );

        // Obtiene las cursadas del docente.
        List<CourseProfessor> courseProfessors = courseProfessorRepository
            .findByIdDocenteOrderByIdDesc(docente)
            .orElse(null);

        // Agrega la información de la cursada en el arreglo a retornar.
        List<CourseDto> cursadas = new ArrayList<>();
        for (CourseProfessor courseProfessor : courseProfessors) {

            Course course = courseProfessor.getCursada();
            Comission comission = course.getComision();
            Subject asignatura = comission.getAsignatura();
            Career carrera = asignatura.getIdCarrera();
            CourseDto cursada = new CourseDto();
            cursada.setId(course.getId());
            cursada.setCodigoAsignatura(asignatura.getCodigoAsignatura());
            cursada.setNombreAsignatura(asignatura.getNombre());
            cursada.setNombreCarrera(carrera.getNombre());
            cursada.setNumeroComision(comission.getNumero());
            cursada.setAnio(course.getAnio());
            cursada.setNivelPermiso(courseProfessor.getNivelPermiso());
            cursadas.add(cursada);

        }

        return cursadas;

    }

    /**
     * Devuelve, partiendo de una lista de legajos de estudiante, una sublista de aquellos legajos que están
     * registrados en una cursada.
     *
     * Precondiciones: los legajos deben pertenecer a alumnos que existan en el sistema.
     *
     * @param course La cursada sobre la que se va a verificar la registración de los estudiantes dentro de
     * {@code studentDossiersList}.
     * @param studentDossiersList La lista de legajos de estudiante de la cual se obtendrá la sublista de legajos registrados
     * en la cursada referenciada por {@code course}.
     */
    public List<Integer> getRegisteredDossiersFromDossiersList(Course course, List<Integer> studentDossiersList) {

        // Obtiene los estudiantes a partir de los legajos.
        List<Student> receivedStudentsList = studentRepository
            .findByLegajoIn(studentDossiersList)
            .orElse(null);

        // Obtiene los estudiantes que pertenecen a la cursada.
        var courseStudentsList = courseStudentRepository
            .findByAlumnoInAndCursada(receivedStudentsList, course)
            .orElse(null);

        return courseStudentsList
            .stream()
            .map(courseStudent ->
                courseStudent
                    .getAlumno()
                    .getLegajo()
            )
            .collect(Collectors.toList());

    }

    /**
     * Devuelve, partiendo de una lista de estudiantes, una sublista de aquellos estudiantes que están
     * registrados en una cursada.
     *
     * @param course La cursada sobre la que se va a verificar la registración de los estudiantes dentro de {@code studentList}.
     * @param studentList La lista de estudiantes de la cual se obtendrá la sublista de estudiantes registrados en la cursada
     * referenciada por {@code course}.
     */
    public List<Student> getRegisteredStudentsFromStudentsList(Course course, List<Student> studentList) {
        
        var courseStudentsList = courseStudentRepository.findByAlumnoInAndCursada(studentList, course)
            .orElse(null);
        
        return courseStudentsList
            .stream()
            .map(courseStudent ->
                courseStudent.getAlumno()
            )
            .collect(Collectors.toList());

    }
    
    /**
     * Registra asistencias de alumnos en un evento de cursada.
     *
     * @param attendanceRegistrationRequest La lista de la información de asistencia junto
     * con el ID de evento.
     * @return La lista de legajos cuya asistencia pudo ser registrada.
     */
    public Object registerAttendance(AttendanceRegistrationRequest attendanceRegistrationRequest) {

        /* Para cada legajo, crea un nuevo objeto StudentAttendanceRegister, o lo obtiene 
        de la BD y lo modifica si ya existe. */

        CourseEvent courseEvent = courseEventRepository
            .findById(attendanceRegistrationRequest.getEventId())
            .get();

        List<StudentCourseEvent> studentCourseEventListToSave = attendanceRegistrationRequest
            .getAttendanceList()
            .stream()
            .map(attendanceRegister -> {

                Student student = studentRepository
                    .getByLegajo(attendanceRegister.getDossier());

                StudentCourseEvent returningStudentCourseEvent = studentCourseEventRepository
                    .findByEventoCursadaAndAlumno(
                        courseEvent,
                        student
                    )
                    .map(studentCourseEvent -> {

                        studentCourseEvent.setAsistencia(attendanceRegister.isAttendance());

                        return studentCourseEvent;
                    })
                    .orElseGet(() -> {
                        var newStudentCourseEvent = new StudentCourseEvent();

                        newStudentCourseEvent.setEventoCursada(courseEvent);
                        newStudentCourseEvent.setAlumno(student);
                        newStudentCourseEvent.setAsistencia(attendanceRegister.isAttendance());

                        return newStudentCourseEvent;
                    });

                return returningStudentCourseEvent;

            })
            .collect(Collectors.toList());

        // Guarda los cambios en la base de datos.
        
        studentCourseEventRepository.saveAllAndFlush(studentCourseEventListToSave);

        // Devuelve la respuesta.

        @Data
        class Result {

            public void addToOk(Integer dossier) {
                ok.add(dossier);
            }

            private List<Integer> ok = new ArrayList<Integer>();

        }

        var result = new Result();
        studentCourseEventListToSave
            .forEach(studentCourseEvent -> {
                result.addToOk(studentCourseEvent
                    .getAlumno()
                    .getLegajo()
                );
            });

        return result;

    }

    /**
     * Registra calificaciones de alumnos en un evento de cursada.
     *
     * @param calificationRegistrationRequest La lista de la información de calificaciones junto
     * con el ID de evento.
     * @return La lista de legajos cuya calificación pudo ser registrada.
     */
    public Object registerCalification(CalificationRegistrationRequest calificationRegistrationRequest) {

        /* Para cada legajo, crea un nuevo objeto StudentCalificationRegister, o lo obtiene 
        de la BD y lo modifica si ya existe. */

        CourseEvent courseEvent = courseEventRepository
            .findById(calificationRegistrationRequest.getEventId())
            .get();

        List<StudentCourseEvent> studentCourseEventListToSave = calificationRegistrationRequest
            .getCalificationList()
            .stream()
            .map(calificationRegister -> {

                Student student = studentRepository
                    .getByLegajo(calificationRegister.getDossier());

                // Si ya existe un registro del alumno en el evento, lo modifica para incluir
                // la nota; si no, genera un registro nuevo.
                StudentCourseEvent returningStudentCourseEvent = studentCourseEventRepository

                    // Si ya existe un registro del alumno en el evento, modifica el registro para agregar la nota.
                    .findByEventoCursadaAndAlumno(
                        courseEvent,
                        student
                    )
                    .map(studentCourseEvent -> {

                        // Modificación que ocurre cuando el alumno no asistió al evento.
                        if(calificationRegister.getCalification().equals("AUSENTE")) {
                            studentCourseEvent.setNota(null);
                            studentCourseEvent.setAsistencia(false);
                        }

                        // Modificación que ocurre cuando el alumno asistió al evento.
                        else {
                            studentCourseEvent.setNota(calificationRegister.getCalification());
                            studentCourseEvent.setAsistencia(true);
                        }

                        return studentCourseEvent;
                    })

                    // Si no existe, crea un registro nuevo con la nota.
                    .orElseGet(() -> {
                        var newStudentCourseEvent = new StudentCourseEvent();

                        newStudentCourseEvent.setEventoCursada(courseEvent);
                        newStudentCourseEvent.setAlumno(student);

                        // Modificación que ocurre cuando el alumno no asistió al evento.
                        if(calificationRegister.getCalification().equals("AUSENTE")) {
                            newStudentCourseEvent.setNota(null);
                            newStudentCourseEvent.setAsistencia(false);
                        }

                        // Modificación que ocurre cuando el alumno asistió al evento.
                        else {
                            newStudentCourseEvent.setNota(calificationRegister.getCalification());
                            newStudentCourseEvent.setAsistencia(true);
                        }
                        
                        return newStudentCourseEvent;
                    });

                return returningStudentCourseEvent;

            })
            .collect(Collectors.toList());

        // Guarda los cambios en la base de datos.
        studentCourseEventRepository.saveAllAndFlush(studentCourseEventListToSave);

        /* [1] Devuelve la respuesta. */

        @Data
        class Result {

            public void addToOk(Integer dossier) {
                ok.add(dossier);
            }

            private List<Integer> ok = new ArrayList<Integer>();

        }

        var result = new Result();
        studentCourseEventListToSave
            .forEach(studentCourseEvent -> {
                result.addToOk(studentCourseEvent
                    .getAlumno()
                    .getLegajo()
                );
            });

        return result;

        /* [1] */

    }

    public Object registerStudents(StudentsRegistrationRequest studentsRegistrationRequest)
        throws EmptyQueryException
    {

        /* 
         * 1. Separa los legajos recibidos en tres listas:
         *
         * 1.1. los que no existen en sistema (se puede llamar a [StudentService.getExistingStudentsFromDossiersList
         * -> existingStudentsList] y luego guardar en una lista nonExistentStudentsList los que no estén en la
         * lista de retorno),
         *
         * 1.2. los que existen y están registrados en la cursada (llamo a
         * CourseService.getRegisteredStudentsFromStudentsList(course, existingStudentsList) -> registeredStudentsList)
         *
         * 1.3. y los que existen y no están registrados en la cursada (extraigo de existingStudentsList los estudiantes
         * que no existen en registeredStudentsList, y los guardo en notRegisteredStudentsList).
         *
         * 1b. Registra los alumnos en la cursada.
         *
         *     1b.2. Construye un arreglo de objectos CourseStudent. Cada uno tendrá el objeto Course obtenido en
         *     el paso anterior, un objeto Student que se construirá a partir de la info del parámetro,
         *     los valores del parámetro para 'condicion' y 'recursante' y null en 'condicionFinal'.
         *
         *     1b.3. Se guarda el arreglo con un saveAllAndFlush del repositorio CourseStudentRepository.
         *
         * 2. Devuelve el siguiente objeto (expresado en YAML):
         *
         * ok:
         * - # <numérico> - Legajo
         * # ...
         * nok:
         * - dossier: # <numérico> - Legajo
         *   errorCode: # <numérico> - Número que representa la razón
         *              # por la que no se puede registrar el legajo.
         *              # Posibles valores:
         *              # - 1: el legajo no existe en el sistema.
         *              # - 2: el legajo ya está registrado en la cursada.
         * # ...
         */

        // Devuelve el objeto de la cursada o arroja una excepción si no existe.
        var course = courseRepository
            .findById(studentsRegistrationRequest.getCourseId())
            .orElseThrow(() ->
                new EmptyQueryException("No existe la cursada con ID %s".formatted(
                    studentsRegistrationRequest.getCourseId() 
                ))
            );

        /* (1.1) */
        /*
         * los que no existen en sistema (se puede llamar a [StudentService.getExistingStudentsFromDossiersList
         * -> existingStudentsList] y luego guardar en una lista nonExistentStudentsList los que no estén en la
         * lista de retorno),
         */
        var dossierListOfStudentsRegistrationRequest = studentsRegistrationRequest
            .getStudentsRegistrationList()
            .stream()
            .map(studentRegistrationInfo ->
                studentRegistrationInfo.getDossier()
            )
            .collect(Collectors.toList());
        var existingStudentsList = studentService
            .getExistingStudentsFromDossiersList(
                dossierListOfStudentsRegistrationRequest
            );

        // Cada legajo en studentsRegistrationRequest se compara con cada legajo de existingStudentsList y se agrega
        // a una nueva lista nonExistentStudentsList sólo si no existe en la segunda lista comparada.
        var nonExistentStudentsList = new ArrayList<Integer>();
        for (Integer dossier : dossierListOfStudentsRegistrationRequest) {
            boolean found = false;
            for (Student student : existingStudentsList) {
                if (student.getLegajo() == dossier) {
                    found = true;
                    break;
                }
            }
            if (!found) nonExistentStudentsList.add(dossier);
        }

        /*
         * 1.2. los que existen y están registrados en la cursada (llamo a
         * CourseService.getRegisteredStudentsFromStudentsList(course, existingStudentsList) -> registeredStudentsList)
         */
        List<Student> registeredStudentsList = this
            .getRegisteredStudentsFromStudentsList(course, existingStudentsList);

        /*
         * 1.3. y los que existen y no están registrados en la cursada (extraigo de existingStudentsList los estudiantes
         * que no existen en registeredStudentsList, y los guardo en notRegisteredStudentsList).
         */
        var notRegisteredStudentsList = existingStudentsList
            .stream()
            .filter(student ->
                !registeredStudentsList.contains(student)
            )
            .collect(Collectors.toList());

        /* (1b) */
        
        /*
         *     1b.2. Construye un arreglo de objectos CourseStudent, denominado listOfStudentsToRegister.
         *     Cada uno tendrá el objeto Course obtenido en
         *     el paso anterior, un objeto Student que se construirá a partir de la info del parámetro,
         *     los valores del parámetro para 'condicion' y 'recursante' y null en 'condicionFinal'.
         */
        var listOfStudentsToRegister = notRegisteredStudentsList
            .stream()
            .map(student -> {
                
                // Obtiene las marcas de condición y recursante del correspondiente legajo.
                StudentsRegistrationRequest.StudentRegistrationRequest studentRegistrationInfo = studentsRegistrationRequest
                    .searchFirstByDossier(student.getLegajo());
                
                // Crea el objeto que se grabará en la BD.
                var courseStudent = new CourseStudent();
                courseStudent.setCursada(course);
                courseStudent.setAlumno(student);
                courseStudent.setPreviousSubjectsApproved(studentRegistrationInfo.hasPreviousSubjectsApproved());
                courseStudent.setRecursante(studentRegistrationInfo.hasStudiedItPreviously());
                courseStudent.setCondicionFinal(null);
                return courseStudent;

            })
            .collect(Collectors.toList());

        /*
         *     1b.3. Se guarda el arreglo con un saveAllAndFlush del repositorio CourseStudentRepository.
         */
        var newCourseStudentList = courseStudentRepository
            .saveAllAndFlush(listOfStudentsToRegister);

        // Definición de la clase del objeto que se devolverá.
        @Data class Result {

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            static class NotOk {
                private Integer dossier;
                private Integer errorCode;
            }

            private List<Integer> ok;
            private List<NotOk> nok;

        }

        // Construcción del objeto a ser devuelto.
        var result = new Result();
        var okList = new ArrayList<Integer>();
        for (CourseStudent newCourseStudent: newCourseStudentList) {
            okList.add(newCourseStudent.getAlumno().getLegajo());
        }
        result.setOk(okList);
        var nokList = new ArrayList<Result.NotOk>();
        nonExistentStudentsList
            .forEach(dossier ->
                nokList.add(new Result.NotOk(
                    dossier,
                    1
                ))
            );
        registeredStudentsList
            .forEach(student ->
                nokList.add(new Result.NotOk(
                    student.getLegajo(),
                    2
                ))
            );
        result.setNok(nokList);

        // Retorno.
        return result;

    }

    // Devuelve un resumen de los criterios de la cursada.
    public Object getCriteriaSummary(Long courseId) {

        // Por cada alumno de la cursada, y por cada criterio,
        // obtiene la condición actual. Luego, devuelve la cantidad
        // de promovidos, de regulares y de libres.

        return null;
    }

    // Devuelve un resumen de los eventos de la cursada.
    public Object getEventsSummary(Long courseId) {

        /** Define la clase del objeto que se retorna. */

        @Data class Response {

            public void addClassEventSummaryRegister(
                Long eventId,
                String eventType,
                String eventName,
                Timestamp initialDatetime,
                Timestamp endDateTime,
                Boolean obligatory,
                Integer attended,
                Double attendedPercentage,
                Integer notAttended,
                Double notAttendedPercentage,
                Long missingRegisters
            ) {
                classEventsSummaryList.add(
                    new ClassEventSummary(
                        eventId,
                        eventType,
                        eventName,
                        initialDatetime,
                        endDateTime,
                        obligatory,
                        attended,
                        attendedPercentage,
                        notAttended,
                        notAttendedPercentage,
                        missingRegisters
                    )
                );
            }

            @Data
            @AllArgsConstructor
            static class NotesSummaryListClass {
                
                public NotesSummaryListClass() {
                    notesSummaryList = new ArrayList<NoteSummary>();
                }

                public void addNoteSummary(
                    String value
                ) {

                    // Si existe el valor, aumenta la cantidad; si no, lo crea.
                    // Using streams and lambda expressions
                    Optional<NoteSummary> searchResult = notesSummaryList
                    .stream()
                    .filter(register -> register.getValue().equals(value))
                    .findFirst();

                    if (searchResult.isPresent()) {
                        searchResult.get().augmentQuantity();
                    } else {
                        notesSummaryList.add(
                            new NoteSummary(
                                value,
                                1
                            )
                        );
                    }

                }


                /* Private */

                @Data
                @AllArgsConstructor
                @NoArgsConstructor
                static class NoteSummary {

                    public void augmentQuantity() {quantity++;}

                    private String value;
                    private Integer quantity;
                }
                
                private List<NoteSummary> notesSummaryList;

            }

            public void addEvaluationEventByNoteSummaryRegister(
                Long eventId,
                String eventType,
                String eventName,
                Timestamp initialDatetime,
                Timestamp endDateTime,
                Boolean obligatory,
                NotesSummaryListClass notesSummaryList,
                Long missingRegisters
            ) {
                evaluationEventsByNoteSummaryList.add(
                    new EvaluationEventByNoteSummary(
                        eventId,
                        eventType,
                        eventName,
                        initialDatetime,
                        endDateTime,
                        obligatory,
                        notesSummaryList.getNotesSummaryList(),
                        missingRegisters
                    )
                );
            }

            public void addEvaluationEventByApprovalSummaryRegister(
                Long eventId,
                String eventType,
                String eventName,
                Timestamp initialDatetime,
                Timestamp endDateTime,
                Boolean obligatory,
                Integer approvedStudents,
                Double approvedStudentsPercentage,
                Integer disapprovedStudents,
                Double disapprovedStudentsPercentage,
                Integer nonAttendingStudents,
                Double nonAttendingStudentsPercentage,
                Long missingRegisters
            ) {
                evaluationEventsByApprovalRateSummaryList.add(
                    new EvaluationEventByApprovalRateSummary(
                        eventId,
                        eventType,
                        eventName,
                        initialDatetime,
                        endDateTime,
                        obligatory,
                        approvedStudents,
                        approvedStudentsPercentage,
                        disapprovedStudents,
                        disapprovedStudentsPercentage,
                        nonAttendingStudents,
                        nonAttendingStudentsPercentage,
                        missingRegisters
                    )
                );
            }


            /* Private */

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            static class ClassEventSummary {
                private Long eventId;
                private String eventType;
                private String eventName;
                private Timestamp initialDatetime;
                private Timestamp endDatetime;
                private Boolean obligatory;
                private Integer attended;
                private Double attendedPercentage;
                private Integer notAttended;
                private Double notAttendedPercentage;
                private Long missingRegisters;
            }

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            static class EvaluationEventByNoteSummary {
                private Long eventId;
                private String eventType;
                private String eventName;
                private Timestamp initialDatetime;
                private Timestamp endDatetime;
                private Boolean obligatory;
                private List<NotesSummaryListClass.NoteSummary> notesSummaryList;
                private Long missingRegisters;
            }

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            static class EvaluationEventByApprovalRateSummary {
                private Long eventId;
                private String eventType;
                private String eventName;
                private Timestamp initialDatetime;
                private Timestamp endDatetime;
                private Boolean obligatory;
                private Integer approvedStudents;
                private Double approvedStudentsPercentage;
                private Integer disapprovedStudents;
                private Double disapprovedStudentsPercentage;
                private Integer nonAttendingStudents;
                private Double nonAttendingStudentsPercentage;
                private Long missingRegisters;
            }

            private List<ClassEventSummary> classEventsSummaryList = new ArrayList<ClassEventSummary>();
            private List<EvaluationEventByNoteSummary> evaluationEventsByNoteSummaryList = new ArrayList<EvaluationEventByNoteSummary>();
            private List<EvaluationEventByApprovalRateSummary> evaluationEventsByApprovalRateSummaryList = new ArrayList<EvaluationEventByApprovalRateSummary>();

        }

        /**
         * Por cada evento de clase obtiene la asistencia.
         * Luego, construye un arreglo con la cantidad de asistencias,
         * la cantidad de inasistencias y la cantidad de alumnos que
         * no tienen registro en el evento.
         */

        // Inicializa el objeto que se va a devolver.
        var response = new Response();

        // Obtiene todos los eventos de clase de la cursada seleccionada.
        Course course = courseRepository
            .findById(courseId)
            .get();
        EventType classEventType = new EventType();
        classEventType.setId(1);
        classEventType.setNombre("Clase");
        List<CourseEvent> classCourseEventList = courseEventRepository
            .findByCursadaAndTipoEvento(
                course,
                classEventType
            ).orElse(null);

        // Obtiene, de cada evento, la cantidad de alumnos que asistieron, la cantidad que
        // no asistieron y aquellos que no tienen valor en sus registros, y los guarda en
        // el objeto que se va a devolver.
        for (CourseEvent classCourseEvent : classCourseEventList) {

            // Obtiene la lista de objetos estudiante-evento del evento actual.
            List<StudentCourseEvent> classStudentCourseEventList = studentCourseEventRepository
            .findByEventoCursada(classCourseEvent)
            .orElse(null);

            // Calcula los valores de asistencia recorriendo los registros, pertenecientes al
            // evento, de todos los alumnos.
            Long eventId = classCourseEvent.getId();
            String eventType = classCourseEvent.getTipoEvento().getNombre();
            String eventName = classCourseEvent.getNombre();
            Timestamp initialDatetime = classCourseEvent.getFechaHoraInicio();
            Timestamp endDatetime = classCourseEvent.getFechaHoraFin();
            Boolean obligatory = classCourseEvent.isObligatorio();
            Integer attended = 0;
            Double attendedPercentage = 0D;
            Integer notAttended = 0;
            Double notAttendedPercentage = 0D;
            Long missingRegisters = 0L;
            for (StudentCourseEvent classStudentCourseEvent : classStudentCourseEventList) {
                if (classStudentCourseEvent.getAsistencia() == null) missingRegisters++;
                else if (classStudentCourseEvent.getAsistencia()) attended++;
                else if (!classStudentCourseEvent.getAsistencia()) notAttended++;
            }

            /**
             * Determina si hay algún alumno, vinculado con la cursada, que no tiene registros
             * en este evento, y aumenta el correspondiente contador.
             */

            // Obtiene la lista de alumnos que no tienen registro en el evento, y aumenta
            // el contador de los registros.
            List<Student> classStudentList = classStudentCourseEventList
            .stream()
            .map(studentCourseEventRegister -> 
                studentCourseEventRegister.getAlumno()
            )
            .collect(Collectors.toList());
            Long studentsWithoutRegisterCounter = studentCourseRepository
            .countByCursadaAndAlumnoNotIn(
                course,
                classStudentList
            );

            missingRegisters += studentsWithoutRegisterCounter;

            // Calcula los porcentajes.
            Double total = (double) attended + notAttended;
            if(total > 0) {
                attendedPercentage = 
                    attended == 0
                    ? 0
                    : (Math.round(attended / total * 10000.0) / 100.0);
                notAttendedPercentage = 
                    notAttended == 0
                    ? 0
                    : (Math.round(notAttended / total * 10000.0) / 100.0);
            }

            // Registra el resumen del evento en el arreglo que se va a devolver.
            response.addClassEventSummaryRegister(
                eventId,
                eventType,
                eventName,
                initialDatetime,
                endDatetime,
                obligatory,
                attended,
                attendedPercentage,
                notAttended,
                notAttendedPercentage,
                missingRegisters
            );

        }


        /**
         * Por cada evento de evaluación, obtiene, por un lado, la cantidad
         * de alumnos por nota y la cantidad de alumnos que no asistieron, y,
         * por otro lado, la cantidad de aprobados, desaprobados y ausentes.
         * Luego, construye un arreglo para cada grupo de información, agregando
         * la cantidad de alumnos que no tienen registro en el evento.
         */

        // Obtiene todos los eventos de evaluación de la cursada seleccionada.
        List<CourseEvent> evaluationCourseEventList = courseEventRepository
        .findByCursadaAndTipoEventoNot(
            course,
            classEventType
        ).orElse(null);

        // Por cada evento de evaluación...
        for (CourseEvent evaluationCourseEvent : evaluationCourseEventList) {

            // Obtiene la lista de objetos estudiante-evento del evento actual.
            List<StudentCourseEvent> evaluationStudentCourseEventList = studentCourseEventRepository
                .findByEventoCursada(evaluationCourseEvent)
                .orElse(null);

            // Calcula los dos grupos de información, recorriendo los registros de todos
            // los alumnos pertenecientes al evento.
            Long eventId = evaluationCourseEvent.getId();
            String eventType = evaluationCourseEvent.getTipoEvento().getNombre();
            String eventName = evaluationCourseEvent.getNombre();
            Timestamp initialDatetime = evaluationCourseEvent.getFechaHoraInicio();
            Timestamp endDatetime = evaluationCourseEvent.getFechaHoraFin();
            Boolean obligatory = evaluationCourseEvent.isObligatorio();
            var notesSummaryList = new Response.NotesSummaryListClass();
            Integer approvedStudents = 0;
            Double approvedStudentsPercentage = 0D;
            Integer disapprovedStudents = 0;
            Double disapprovedStudentsPercentage = 0D;
            Integer nonAttendingStudents = 0;
            Double nonAttendingStudentsPercentage = 0D;
            Long missingRegisters = 0L;
            for (StudentCourseEvent evaluationStudentCourseEvent : evaluationStudentCourseEventList) {

                // Modificaciones cuando no hay registro del alumno en el evento.
                if (
                    evaluationStudentCourseEvent.getAsistencia() == null
                    && evaluationStudentCourseEvent.getNota() == null
                ) missingRegisters++;

                // Modificaciones cuando el alumno estuvo ausente en el evento o no entregó la evaluación.
                else if (evaluationStudentCourseEvent.getAsistencia() == null || !evaluationStudentCourseEvent.getAsistencia()) {

                    // Si existe el valor "AUSENTE", aumenta la cantidad; si no, crea el valor con
                    // cantidad 1.
                    notesSummaryList.addNoteSummary("AUSENTE");
                    nonAttendingStudents++;

                // Modificaciones cuando el alumno entregó una evaluación.
                } else {

                    // Si existe el valor de la nota, aumenta la cantidad de alumnos con dicha nota;
                    // si no, crea el contador con cantidad 1.
                    notesSummaryList.addNoteSummary(evaluationStudentCourseEvent.getNota());

                    // Para el segundo arreglo, aumenta el contador de aprobados o desaprobados, según corresponda.
                    if (evaluationStudentCourseEvent.getNota().toUpperCase().matches("^([4-9]|10|A-?)$"))
                        approvedStudents++;
                    else disapprovedStudents++;

                }
            }

            /**
             * Determina si hay algún alumno, vinculado con la cursada, que no tiene registros
             * en este evento, y aumenta el correspondiente contador.
             */

            // Obtiene la lista de alumnos que no tienen registro en el evento, y aumenta
            // el contador de los registros.
            List<Student> evaluationStudentList = evaluationStudentCourseEventList
            .stream()
            .map(studentCourseEventRegister -> 
                studentCourseEventRegister.getAlumno()
            )
            .collect(Collectors.toList());
            Long studentsWithoutRegisterCounter = studentCourseRepository
            .countByCursadaAndAlumnoNotIn(
                course,
                evaluationStudentList
            );

            missingRegisters += studentsWithoutRegisterCounter;

            // Calcula los porcentajes.
            Double total = (double) approvedStudents + disapprovedStudents + nonAttendingStudents;
            if(total > 0) {
                approvedStudentsPercentage = 
                    approvedStudents == 0
                    ? 0
                    : (Math.round(approvedStudents / total * 10000.0) / 100.0);
                disapprovedStudentsPercentage = 
                    disapprovedStudents == 0
                    ? 0
                    : (Math.round(disapprovedStudents / total * 10000.0) / 100.0);
                nonAttendingStudentsPercentage = 
                    nonAttendingStudents == 0
                    ? 0
                    : (Math.round(nonAttendingStudents / total * 10000.0) / 100.0);
            }

            // Registra el resumen del evento en los arreglos que se van a devolver.
            response.addEvaluationEventByNoteSummaryRegister(
                eventId,
                eventType,
                eventName,
                initialDatetime,
                endDatetime,
                obligatory,
                notesSummaryList,
                missingRegisters
            );
            response.addEvaluationEventByApprovalSummaryRegister(
                eventId,
                eventType,
                eventName,
                initialDatetime,
                endDatetime,
                obligatory,
                approvedStudents,
                approvedStudentsPercentage,
                disapprovedStudents,
                disapprovedStudentsPercentage,
                nonAttendingStudents,
                nonAttendingStudentsPercentage,
                missingRegisters
            );

        }


        /**
         * {
         *      "classEventsSummaryList": [
         *          {
         *              "eventId": ...
         *              "eventType": ...
         *              "eventName": ...
         *              "initialDatetime": ...
         *              "endDatetime": ...
         *              "obligatory": ...
         *              "attended": ...
         *              "attendedPercentage": ...
         *              "notAttended": ...
         *              "notAttendedPercentage": ...
         *              "missingRegisters": ...
         *          },
         *      ],
         *      "evaluationEventsByNoteSummaryList": [
         *          {
         *              "eventId": ...
         *              "eventType": ...
         *              "eventName": ...
         *              "initialDatetime": ...
         *              "endDatetime": ...
         *              "obligatory": ...
         *              "notesSummaryList": [
         *                  {
         *                      "value": <0-10, A/A+/D, AUSENTE>
         *                      "quantity": ...
         *                  },
         *                  ...
         *              ]
         *              "missingRegisters": ...
         *          }
         *          },
         *          ...
         *      ],
         *      "evaluationEventsByApprovalRateSummaryList": [
         *          {
         *              "eventId": ...
         *              "eventType": ...
         *              "eventName": ...
         *              "initialDatetime": ...
         *              "endDatetime": ...
         *              "obligatory": ...
         *              "approvedStudents": ...
         *              "approvedStudentsPercentage": ...
         *              "disapprovedStudents": ...
         *              "disapprovedStudentsPercentage": ...
         *              "nonAttendingStudents": ...
         *              "nonAttendingStudentsPercentage": ...
         *              "missingRegisters": ...
         *          },
         *          ...
         *      ]
         * }
         */
        return response;

    }


    /* Private */ 

    private static final Logger logger = LoggerFactory.getLogger(CourseService.class);

    @Autowired private CourseEvaluationCriteriaRepository courseEvaluationCriteriaRepository;
    @Autowired private CourseEventRepository courseEventRepository;
    @Autowired private CourseProfessorRepository courseProfessorRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private CourseStudentRepository courseStudentRepository;
    @Autowired private EvaluationCriteriaRepository evaluationCriteriaRepository;
    @Autowired private EventTypeRepository eventTypeRepository;
    @Autowired private StudentCourseEventRepository studentCourseEventRepository;
    @Autowired private StudentCourseRepository studentCourseRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private UserRepository userRepository;

    @Autowired private CourseEventService courseEventService;
    @Autowired private StudentService studentService;

    private String evaluarParcialesRecuperados(Course course, Student alumno) {
                       /**
         * Obtener todos los registros de la tabla evento_cursada_alumno
         * cuyo id_evento corresponda a registros de la tabla evento_cursada (AA),
         * que, a su vez, cuyo id_cursada sea la de la cursada actual y cuyo
         * id_tipo corresponda al registro de la tabla tipo_evento (AB), que,
         * a su vez, cuyo nombre sea "Recuperatorio Trabajo práctico" (AC)
         * -> trabajos_practicos_alumno (A).
         * 
         * Calcular el porcentaje de registros de [A] que tengan nota "4", "A"
         * o "A-" (B).
         * 
         * Obtener el registro de la tabla criterio_cursada cuyo
         * atributo id_cursada sea igual a la cursada actual y cuyo id_criterio
         * corresponda al registro de la tabla criterio_evaluacion (CA), que, a su vez,
         * cuyo nombre sea "Trabajos prácticos recuperados" (CB).
         * 
         * Si el porcentaje calculado en [B] es menor o igual al porcentaje de
         * valor_promovido, devuelve "P" (DA); si es menor o igual al porcentaj de
         * valor_regular, devuelve "R" (DB); si no, devuelve "L" (DC).
         *
         * IMPORTANTE: Este criterio considera todos los parciales para el cómputo,
         * es decir no filtra por aquellos que sean obligatorios, dado que en la
         * práctica no deberían haber parciales opcionales.
         */

        String nota = null; // (DC): se devuelve esto si no se cumple con (DA) ni (DB).

        // (A)
        
        // (AC)
        Optional<EventType> eventType =
            eventTypeRepository
            .findByNombre("Recuperatorio Parcial");

        // (AB)
        Optional<List<CourseEvent>> courseEventList =
            courseEventRepository
            .findByCursadaAndTipoEvento(course, eventType.get());

        // (B)

        Optional<EventType> eventTypeParcial =
        eventTypeRepository
        .findByNombre("Parcial");

        Optional<List<CourseEvent>> courseEventListParcial =
        courseEventRepository
        .findByCursadaAndTipoEvento(course, eventTypeParcial.get());

        int parcialesRecuperados = 0;
        int parcialesTotales = courseEventListParcial.get().size();
        
        for (CourseEvent courseEvent : courseEventList.get()) {

            // (AA)
            Optional<StudentCourseEvent> studentCourseEvent
                = studentCourseEventRepository
                .findByEventoCursadaAndAlumno(courseEvent, alumno);

            /*if (studentCourseEvent != null && studentCourseEvent
                .getNota()
                .matches("^([4-9]|10|A|a).*$")
            ) parcialesRecuperados++;*/

            if (studentCourseEvent.isPresent() && studentCourseEvent.get().getAsistencia().booleanValue())
                parcialesRecuperados++;

        }

        // (DA)

        if (parcialesTotales != 0) {

            // (CB)
            EvaluationCriteria evaluationCriteria =
                evaluationCriteriaRepository
                .findByName("Parciales recuperados");

            // (CA)
            // EvaluationCriteria criteria
            // Course course
            CourseEvaluationCriteria courseEvaluationCriteria =
                courseEvaluationCriteriaRepository
                .findByCriteriaAndCourse(evaluationCriteria, course);

            float porcentajeTps = (float) parcialesRecuperados / (float) parcialesTotales * 100;

            if (porcentajeTps > courseEvaluationCriteria.getValue_to_regulate())
                nota = "L";

            else if (porcentajeTps <= courseEvaluationCriteria.getValue_to_regulate() && porcentajeTps > courseEvaluationCriteria.getValue_to_promote() )
                nota = "R";

            else nota = "P";
        }

        return nota;
    }

    private String getMinimalCondition(String lowestCondition, String lastCondition) {
        switch(lowestCondition) {
            case "P": return lastCondition;
            case "R": return
                lastCondition != "P"
                ? lastCondition
                : lowestCondition;
            default: return lowestCondition;
        }
    }

    private String evaluarAERecuperadas(Course course, Student alumno) {
        /**
         * Obtener todos los registros de la tabla evento_cursada_alumno
         * cuyo id_evento corresponda a registros de la tabla evento_cursada (AA),
         * que, a su vez, cuyo id_cursada sea la de la cursada actual y cuyo
         * id_tipo corresponda al registro de la tabla tipo_evento (AB), que,
         * a su vez, cuyo nombre sea "Autoevaluación" (AC)
         * -> trabajos_practicos_alumno (A).
         * 
         * Calcular el porcentaje de registros de [A] que tengan nota "4", "A"
         * o "A-" (B).
         * 
         * Obtener el registro de la tabla criterio_cursada cuyo
         * atributo id_cursada sea igual a la cursada actual y cuyo id_criterio
         * corresponda al registro de la tabla criterio_evaluacion (CA), que, a su vez,
         * cuyo nombre sea "Autoevaluaciones recuperadas" (CB).
         * 
         * Si el porcentaje calculado en [B] es menor o igual al porcentaje de
         * valor_promovido, devuelve "P" (DA); si es menor o igual al porcentaj de
         * valor_regular, devuelve "R" (DB); si no, devuelve "L" (DC).
         */

        String nota = null; // (DC): se devuelve esto si no se cumple con (DA) ni (DB).

        // (A)
        
        // (AC)
        Optional<EventType> eventType =
            eventTypeRepository
            .findByNombre("Recuperatorio Autoevaluación");

        // (AB)
        Optional<List<CourseEvent>> courseEventList =
            courseEventRepository
            .findByCursadaAndTipoEvento(course, eventType.get());

        Optional<EventType> eventTypeAE =
        eventTypeRepository
        .findByNombre("Autoevaluación");

        Optional<List<CourseEvent>> courseEventListAE =
        courseEventRepository
        .findByCursadaAndTipoEvento(course, eventTypeAE.get());

        // (B)
        int autoevaluacionesRecuperadas = 0;
        int autoevaluacionesTotales = 0;

        for (CourseEvent courseEvent : courseEventListAE.get()) {
            if (courseEvent.isObligatorio()) autoevaluacionesTotales++;
        }

        for (CourseEvent courseEvent : courseEventList.get()) {
            if (courseEvent.isObligatorio()) {

                // (AA)
                Optional<StudentCourseEvent> studentCourseEvent
                    = studentCourseEventRepository
                    .findByEventoCursadaAndAlumno(courseEvent, alumno);

                /*if (studentCourseEvent != null && studentCourseEvent
                    .getNota()
                    .matches("^([4-9]|10|A|a).*$")
                ) autoevaluacionesRecuperadas++;*/

                if (studentCourseEvent.isPresent() && studentCourseEvent.get().getAsistencia().booleanValue())
                    autoevaluacionesRecuperadas++;

            }
        }

        // (DA)

        if (autoevaluacionesTotales != 0) {

            // (CB)
            EvaluationCriteria evaluationCriteria =
            evaluationCriteriaRepository
            .findByName("Autoevaluaciones recuperadas");

            // (CA)
            // EvaluationCriteria criteria
            // Course course
            CourseEvaluationCriteria courseEvaluationCriteria =
            courseEvaluationCriteriaRepository
            .findByCriteriaAndCourse(evaluationCriteria, course);

            float porcentajeAutoevaluaciones = (float) autoevaluacionesRecuperadas / (float) autoevaluacionesTotales * 100;    

            if (porcentajeAutoevaluaciones > courseEvaluationCriteria.getValue_to_regulate())
                nota = "L";

            else if (porcentajeAutoevaluaciones <= courseEvaluationCriteria.getValue_to_regulate() && porcentajeAutoevaluaciones > courseEvaluationCriteria.getValue_to_promote() )
                nota = "R";

            else nota = "P";
        }
        return nota;
    }

    private String evaluarAEAprobadas(Course course, Student alumno) {
        /**
         * Obtener todos los registros de la tabla evento_cursada_alumno
         * cuyo id_evento corresponda a registros de la tabla evento_cursada (AA),
         * que, a su vez, cuyo id_cursada sea la de la cursada actual y cuyo
         * id_tipo corresponda al registro de la tabla tipo_evento (AB), que,
         * a su vez, cuyo nombre sea "Autoevaluación" (AC)
         * -> trabajos_practicos_alumno (A).
         * 
         * Calcular el porcentaje de registros de [A] que tengan nota "4", "A"
         * o "A-" (B).
         * 
         * Obtener el registro de la tabla criterio_cursada cuyo
         * atributo id_cursada sea igual a la cursada actual y cuyo id_criterio
         * corresponda al registro de la tabla criterio_evaluacion (CA), que, a su vez,
         * cuyo nombre sea "Autoevaluaciones aprobadas" (CB).
         * 
         * Si el porcentaje calculado en [B] es mayor o igual al porcentaje de
         * valor_promovido, devuelve "P" (DA); si es mayor o igual al porcentaj de
         * valor_regular, devuelve "R" (DB); si no, devuelve "L" (DC).
         */

        String nota = null; // (DC): se devuelve esto si no se cumple con (DA) ni (DB).

        // (A)
        
        // (AC)
        Optional<EventType> eventType =
            eventTypeRepository
            .findByNombre("Autoevaluación");

        // (AB)
        Optional<List<CourseEvent>> courseEventList =
            courseEventRepository
            .findByCursadaAndTipoEvento(course, eventType.get());

        // (B)

        Optional<EventType> eventTypeRec =
        eventTypeRepository
        .findByNombre("Recuperatorio Autoevaluación");

        Optional<List<CourseEvent>> courseEventListRec =
        courseEventRepository
        .findByCursadaAndTipoEvento(course, eventTypeRec.get());

        int autoevaluacionesAprobadas = 0;
        int autoevaluacionesTotales = 0;
        for (CourseEvent courseEvent : courseEventList.get()) {
            if (courseEvent.isObligatorio()) {

                autoevaluacionesTotales++;

                // (AA)
                Optional<StudentCourseEvent> studentCourseEvent
                    = studentCourseEventRepository
                    .findByEventoCursadaAndAlumno(courseEvent, alumno);

                if (studentCourseEvent.isPresent() && studentCourseEvent.get().getAsistencia().booleanValue()
                        && studentCourseEvent.get().getNota().matches("^([4-9]|10|A|a).*$")) autoevaluacionesAprobadas++;

            }
        }

        for (CourseEvent courseEvent : courseEventListRec.get()) {
            if (courseEvent.isObligatorio()) {

                // (A)
                Optional<StudentCourseEvent> studentCourseEvent
                    = studentCourseEventRepository
                    .findByEventoCursadaAndAlumno(courseEvent, alumno);

                if (studentCourseEvent.isPresent() && studentCourseEvent.get().getAsistencia().booleanValue()
                        && studentCourseEvent.get().getNota().matches("^([4-9]|10|A|a).*$")) autoevaluacionesAprobadas++;

            }
        }

        // (DA)

        if (autoevaluacionesTotales != 0) {

            // (CB)
            EvaluationCriteria evaluationCriteria =
                evaluationCriteriaRepository
                .findByName("Autoevaluaciones aprobadas");

            // (CA)
            CourseEvaluationCriteria courseEvaluationCriteria =
                courseEvaluationCriteriaRepository
                .findByCriteriaAndCourse(evaluationCriteria, course);

            float porcentajeAutoevaluaciones = (float) autoevaluacionesAprobadas / (float) autoevaluacionesTotales * 100;    

            // (DA)
            if (porcentajeAutoevaluaciones >= courseEvaluationCriteria.getValue_to_promote())
                nota = "P";

            // (DB)
            else if (porcentajeAutoevaluaciones >= courseEvaluationCriteria.getValue_to_regulate())
                nota = "R";

            // (DC)
            else nota = "L";

        }

        return nota;

    }

    private String evaluarPromedioParciales(Course course, Student alumno) {
        /**
         * Obtener todos los registros de la tabla evento_cursada_alumno
         * cuyo id_evento corresponda a registros de la tabla evento_cursada (AA),
         * que, a su vez, cuyo id_cursada sea la de la cursada actual y cuyo
         * id_tipo corresponda al registro de la tabla tipo_evento (AB), que,
         * a su vez, cuyo nombre sea "Parcial" (AC)
         * -> trabajos_practicos_alumno (A).
         * 
         * Calcular el promedio de registros de [A] que tengan nota "4", "A"
         * o "A-" (B).
         * 
         * Obtener el registro de la tabla criterio_cursada cuyo
         * atributo id_cursada sea igual a la cursada actual y cuyo id_criterio
         * corresponda al registro de la tabla criterio_evaluacion (CA), que, a su vez,
         * cuyo nombre sea "Parciales aprobados" (CB).
         * 
         * Si el promedio calculado en [B] es mayor o igual al porcentaje de
         * valor_promovido, devuelve "P" (DA); si es mayor o igual al porcentaj de
         * valor_regular, devuelve "R" (DB); si no, devuelve "L" (DC).
         *
         * IMPORTANTE: Este criterio considera todos los parciales para el cómputo,
         * es decir no filtra por aquellos que sean obligatorios, dado que en la
         * práctica no deberían haber parciales opcionales.
         *
         * IMPORTANTE: Este criterio no espera que las calificaciones de los
         * parciales sean alfanuméricas ("A", "A-", etc.). Si encuentra un dato
         * en este formato lo descartará del promedio.
         */

        String nota = "L"; // (DC): se devuelve esto si no se cumple con (DA) ni (DB).

        // (A)
        
        // (AC)
        Optional<EventType> eventType =
            eventTypeRepository
            .findByNombre("Parcial");

        // (AB)
        Optional<List<CourseEvent>> courseEventList =
            courseEventRepository
            .findByCursadaAndTipoEvento(course, eventType.get());

        // (B)
        float sumaNotasParciales = 0;
        int parcialesTotales = 0;
        for (CourseEvent courseEvent : courseEventList.get()) {

            // (AA)
            Optional<StudentCourseEvent> studentCourseEvent
                = studentCourseEventRepository
                .findByEventoCursadaAndAlumno(courseEvent, alumno);

            if (studentCourseEvent.isPresent() && studentCourseEvent.get().getAsistencia().booleanValue())
                try {
                    sumaNotasParciales += Float.parseFloat(studentCourseEvent.get().getNota());
                    parcialesTotales++;
                } catch (NumberFormatException ex) {
                    logger.debug(String.format(
                        "No se pudo convertir %s a un float en el cómputo del promedio de un alumno. Se descarta el valor.",
                        studentCourseEvent.get().getNota()
                    ));
                }

        }

        if (parcialesTotales != 0) {

            // (CB)
            EvaluationCriteria evaluationCriteria =
            evaluationCriteriaRepository
            .findByName("Promedio de parciales");

            // (CA)
            // EvaluationCriteria criteria
            // Course course
            CourseEvaluationCriteria courseEvaluationCriteria =
            courseEvaluationCriteriaRepository
            .findByCriteriaAndCourse(evaluationCriteria, course);
            
            float promedioParciales = sumaNotasParciales / (float) parcialesTotales;    
            System.out.println(promedioParciales);

            if (promedioParciales >= courseEvaluationCriteria.getValue_to_promote())
                nota = "P";
            else if (promedioParciales >= courseEvaluationCriteria.getValue_to_regulate())
                nota = "R";
            else nota = "L";
        }
        return nota;
    }

    public String  evaluarParcialesAprobados(Course course, Student alumno) {
                        /**
         * Obtener todos los registros de la tabla evento_cursada_alumno
         * cuyo id_evento corresponda a registros de la tabla evento_cursada (AA),
         * que, a su vez, cuyo id_cursada sea la de la cursada actual y cuyo
         * id_tipo corresponda al registro de la tabla tipo_evento (AB), que,
         * a su vez, cuyo nombre sea "Parcial" (AC)
         * -> trabajos_practicos_alumno (A).
         * 
         * Calcular el porcentaje de registros de [A] que tengan nota "4", "A"
         * o "A-" (B).
         * 
         * Obtener el registro de la tabla criterio_cursada cuyo
         * atributo id_cursada sea igual a la cursada actual y cuyo id_criterio
         * corresponda al registro de la tabla criterio_evaluacion (CA), que, a su vez,
         * cuyo nombre sea "Parciales aprobados" (CB).
         * 
         * Si el porcentaje calculado en [B] es mayor o igual al porcentaje de
         * valor_promovido, devuelve "P" (DA); si es mayor o igual al porcentaj de
         * valor_regular, devuelve "R" (DB); si no, devuelve "L" (DC).
         *
         * IMPORTANTE: Este criterio considera todos los parciales para el cómputo,
         * es decir no filtra por aquellos que sean obligatorios, dado que en la
         * práctica no deberían haber parciales opcionales.
         */

        String nota = null; // (DC): se devuelve esto si no se cumple con (DA) ni (DB).

        // (A)
        
        // (AC)
        Optional<EventType> eventType =
            eventTypeRepository
            .findByNombre("Parcial");

        // (AB)
        Optional<List<CourseEvent>> courseEventList =
            courseEventRepository
            .findByCursadaAndTipoEvento(course, eventType.get());

        // (B)

        Optional<EventType> eventTypeRec =
        eventTypeRepository
        .findByNombre("Recuperatorio Parcial");

        Optional<List<CourseEvent>> courseEventListRec =
        courseEventRepository
        .findByCursadaAndTipoEvento(course, eventTypeRec.get());

        int parcialesAprobados = 0;
        int parcialesTotales = 0;
        for (CourseEvent courseEvent : courseEventList.get()) {

            parcialesTotales++;

            // (AA)
            Optional<StudentCourseEvent> studentCourseEvent
                = studentCourseEventRepository
                .findByEventoCursadaAndAlumno(courseEvent, alumno);

            if (studentCourseEvent.isPresent() && studentCourseEvent.get().getAsistencia().booleanValue()
                    && studentCourseEvent.get().getNota().matches("^([4-9]|10|A|a).*$")) parcialesAprobados++;

        }

        if (courseEventListRec.isPresent()) {
            for (CourseEvent courseEvent : courseEventListRec.get()) {

                // (A)
                Optional<StudentCourseEvent> studentCourseEvent
                    = studentCourseEventRepository
                    .findByEventoCursadaAndAlumno(courseEvent, alumno);

                if (studentCourseEvent.isPresent() && studentCourseEvent.get().getAsistencia().booleanValue()
                        && studentCourseEvent.get().getNota().matches("^([4-9]|10|A|a).*$")) parcialesAprobados++;

            }
        }

        // (DA)

        if (parcialesTotales != 0) {

            // (CB)
            EvaluationCriteria evaluationCriteria =
                evaluationCriteriaRepository
                .findByName("Parciales aprobados");

            // (CA)
            CourseEvaluationCriteria courseEvaluationCriteria =
                courseEvaluationCriteriaRepository
                .findByCriteriaAndCourse(evaluationCriteria, course);

            float porcentajeParciales = (float) parcialesAprobados / (float) parcialesTotales * 100;    

            if (porcentajeParciales >= courseEvaluationCriteria.getValue_to_promote())
                nota = "P";

            else if (porcentajeParciales >= courseEvaluationCriteria.getValue_to_regulate())
                nota = "R";

            else nota = "L";

        }

        return nota;

    }

    private String evaluarTPsRecuperados(Course course, Student alumno) {
        /**
         * Obtener todos los registros de la tabla evento_cursada_alumno
         * cuyo id_evento corresponda a registros de la tabla evento_cursada (AA),
         * que, a su vez, cuyo id_cursada sea la de la cursada actual y cuyo
         * id_tipo corresponda al registro de la tabla tipo_evento (AB), que,
         * a su vez, cuyo nombre sea "Recuperatorio Trabajo práctico" (AC)
         * -> trabajos_practicos_alumno (A).
         * 
         * Calcular el porcentaje de registros de [A] que tengan nota "4", "A"
         * o "A-" (B).
         * 
         * Obtener el registro de la tabla criterio_cursada cuyo
         * atributo id_cursada sea igual a la cursada actual y cuyo id_criterio
         * corresponda al registro de la tabla criterio_evaluacion (CA), que, a su vez,
         * cuyo nombre sea "Trabajos prácticos recuperados" (CB).
         * 
         * Si el porcentaje calculado en [B] es menor o igual al porcentaje de
         * valor_promovido, devuelve "P" (DA); si es menor o igual al porcentaj de
         * valor_regular, devuelve "R" (DB); si no, devuelve "L" (DC).
         */

        String nota = null; // (DC): se devuelve esto si no se cumple con (DA) ni (DB).

        // (A)
        
        // (AC)
        Optional<EventType> eventType =
            eventTypeRepository
            .findByNombre("Recuperatorio Trabajo práctico");

        // (AB)
        Optional<List<CourseEvent>> courseEventList =
            courseEventRepository
            .findByCursadaAndTipoEvento(course, eventType.get());

        Optional<EventType> eventTypeTP =
            eventTypeRepository
            .findByNombre("Trabajo práctico");

        Optional<List<CourseEvent>> courseEventListTP =
            courseEventRepository
            .findByCursadaAndTipoEvento(course, eventTypeTP.get());

        // (B)
        int tpsRecuperados = 0;
        int tpsTotales = 0;

        for (CourseEvent courseEvent : courseEventListTP.get()) {
            if (courseEvent.isObligatorio()) tpsTotales++;
        }

        for (CourseEvent courseEvent : courseEventList.get()) {
            if (courseEvent.isObligatorio()) {

                // (AA)
                Optional<StudentCourseEvent> studentCourseEvent
                    = studentCourseEventRepository
                    .findByEventoCursadaAndAlumno(courseEvent, alumno);

                //if (!studentCourseEvent.getNota().matches("NULL"))
                //    tpsTotales++;

                //if (studentCourseEvent != null && studentCourseEvent
                //    .getNota()
                //   .matches("^([4-9]|10|A|a).*$")
                //)
                if (studentCourseEvent.isPresent() && studentCourseEvent.get().getAsistencia().booleanValue())
                    tpsRecuperados++;

            }
        }

        // (DA)

        if (tpsTotales != 0) {

            // (CB)
            EvaluationCriteria evaluationCriteria =
                evaluationCriteriaRepository
                .findByName("Trabajos prácticos recuperados");

            // (CA)
            CourseEvaluationCriteria courseEvaluationCriteria =
                courseEvaluationCriteriaRepository
                .findByCriteriaAndCourse(evaluationCriteria, course);

            float porcentajeTps = (float) tpsRecuperados / (float) tpsTotales * 100;

        /*    if (porcentajeTps <= courseEvaluationCriteria.getValue_to_promote())
                nota = "P";

            else if (porcentajeTps <= courseEvaluationCriteria.getValue_to_regulate())
                nota = "R";

            else nota = "L"; */ 

            if (porcentajeTps > courseEvaluationCriteria.getValue_to_regulate())
                nota = "L";

            else if (porcentajeTps <= courseEvaluationCriteria.getValue_to_regulate() && porcentajeTps > courseEvaluationCriteria.getValue_to_promote() )
                nota = "R";

            else nota = "P";

        }

        return nota;

    }

    private String evaluarTPsAprobados(Course course, Student alumno) {
 
        /**
         * (A) Obtener todos los registros de la tabla evento_cursada_alumno
         * cuyo id_evento corresponda a registros de la tabla evento_cursada,
         * que, a su vez, cuyo id_cursada sea la de la cursada actual y cuyo
         * id_tipo corresponda al registro de la tabla tipo_evento,
         * que, a su vez, cuyo nombre sea "Trabajo práctico".
         *
         * (E) Si no hubo eventos de evaluación de trabajos prácticos, se devuelve
         * 'null'.
         * 
         * (B) Calcular el porcentaje de registros de [A] que tengan nota "4", "A"
         * o "A-".
         * 
         * (C) Obtener el registro de la tabla criterio_cursada cuyo
         * atributo id_cursada sea igual a la cursada actual y cuyo id_criterio
         * corresponda al registro de la tabla criterio_evaluacion, que, a su vez,
         * cuyo nombre sea "Trabajos prácticos aprobados".
         * 
         * (DA) Si el porcentaje calculado en [B] es mayor o igual al porcentaje de
         * valor_promovido, devuelve "P";
         *
         * (DB) si es mayor o igual al porcentaje de
         * valor_regular, devuelve "R";
         *
         * (DC) si no, devuelve "L".
         */
        
        // (E): se devuelve esto si no hay eventos de evaluación de trabajos prácticos.
        String nota = null;

        // Obtiene el tipo de evento "Trabajos prácticos".
        Optional<EventType> eventType =
            eventTypeRepository
            .findByNombre("Trabajo práctico");

        // Obtiene los eventos de cursada, de un tipo específico,
        // que pertenecen a una cursada específica.
        Optional<List<CourseEvent>> courseEventList =
            courseEventRepository
            .findByCursadaAndTipoEvento(course, eventType.get());

        Optional<EventType> eventTypeRec =
            eventTypeRepository
            .findByNombre("Recuperatorio Trabajo práctico");

        Optional<List<CourseEvent>> courseEventListRec =
            courseEventRepository
            .findByCursadaAndTipoEvento(course, eventTypeRec.get());

        // (B)
        int tpsAprobados = 0;
        int tpsTotales = 0;
        for (CourseEvent courseEvent : courseEventList.get()) {
            if (courseEvent.isObligatorio()) {

                tpsTotales++;

                // (A)
                Optional<StudentCourseEvent> studentCourseEvent
                    = studentCourseEventRepository
                    .findByEventoCursadaAndAlumno(courseEvent, alumno);

                if (studentCourseEvent.isPresent() && studentCourseEvent.get().getAsistencia().booleanValue()
                        && studentCourseEvent.get().getNota().matches("^([4-9]|10|A|a).*$")) tpsAprobados++;

            }
        }

        for (CourseEvent courseEvent : courseEventListRec.get()) {
            if (courseEvent.isObligatorio()) {

                // (A)
                Optional<StudentCourseEvent> studentCourseEvent
                    = studentCourseEventRepository
                    .findByEventoCursadaAndAlumno(courseEvent, alumno);

                if (studentCourseEvent.isPresent() && studentCourseEvent.get().getAsistencia().booleanValue()
                        && studentCourseEvent.get().getNota().matches("^([4-9]|10|A|a).*$")) tpsAprobados++;

            }
        }

        // (D)
        if (tpsTotales != 0) {

            // Obtiene el criterio de evaluación de los
            // trabajos prácticos aprobados.
            EvaluationCriteria evaluationCriteria =
                evaluationCriteriaRepository
                .findByName("Trabajos prácticos aprobados");

            // (C)
            CourseEvaluationCriteria courseEvaluationCriteria =
                courseEvaluationCriteriaRepository
                .findByCriteriaAndCourse(evaluationCriteria, course);

            float porcentajeTps = (float) tpsAprobados / (float) tpsTotales * 100;

            // (DA)
            if (porcentajeTps >= courseEvaluationCriteria.getValue_to_promote())
                nota = "P";

            // (DB)
            else if (porcentajeTps >= courseEvaluationCriteria.getValue_to_regulate())
                nota = "R";

            // (DC)
            else nota = "L";

        }

        return nota;

    }

    public ArrayList<String> evaluarAsistencia(Course cursada, Student alumno) {
        
        // Recupero los eventos de la cursada

        List<CourseEvent> eventos = courseEventRepository.findByCursada(cursada).get();
        
        // Busco el criterio de la cursada donde coincida con la cursada solicitada y el criterio correspondiente

        EvaluationCriteria criterioAsistencia = evaluationCriteriaRepository.findByName("Asistencias");

        CourseEvaluationCriteria criterioCursadaAsistencia = courseEvaluationCriteriaRepository.findByCriteriaAndCourse(criterioAsistencia, cursada);

        // Recupero los valores para quedar regular y para promover

        long valorRegular = criterioCursadaAsistencia.getValue_to_regulate();

        long valorPromovido = criterioCursadaAsistencia.getValue_to_promote();

        int presenciasAlumno = 0;

        int eventosAsistencias = 0;

        // Itero por cada evento

        for (CourseEvent evento : eventos) {

            logger.debug(evento.getTipoEvento().getNombre());

            // Verifico que se trate de un evento 'Clase'

            if (evento.getTipoEvento().getNombre().equals("Clase")) {

                // Verifico que se trate de una clase obligatoria.

                if (evento.isObligatorio()) {

                    logger.debug("entre aca");
                
                    // Recupero el 'Evento_Cursada_Alumno' correspondiente
                
                    logger.debug(String.format(
                        "findByEventoCursadaAndAlumno. [evento = %s] [alumno = %s]",
                        evento.toString(),
                        alumno.toString()
                    ));
                
                    Optional<StudentCourseEvent> eventoClaseAlumno = studentCourseEventRepository
                        .findByEventoCursadaAndAlumno(evento, alumno);
                 
                    // Si el campo de asistencia es true, incremento las presencias del alumno
                
                    if (eventoClaseAlumno.isPresent() && eventoClaseAlumno.get().getAsistencia().booleanValue()) {
                        presenciasAlumno++;
                    }

                    eventosAsistencias++;
                }
            }

        }
        
        ArrayList<String> resultados = new ArrayList<>();

        if (eventosAsistencias != 0) {

            float porcentajeAlumno = (float) presenciasAlumno / (float) eventosAsistencias * 100;

            resultados.add(String.valueOf(porcentajeAlumno));

            if (porcentajeAlumno >= valorPromovido)
                resultados.add("P");
            else
                if (porcentajeAlumno >= valorRegular)
                    resultados.add("R");
                else
                    resultados.add("L");
            
            return resultados;

        } else return null;
    }

    public ResponseEntity<Object> getStudentState(long courseId, int dossier) throws EmptyQueryException {
        
        // Recuperamos la cursada asociada.
        Course course =
        courseRepository // Tabla 'course'.
        .findById(courseId)
        .orElseThrow(
            () -> new EmptyQueryException(
                String.valueOf(String.format(
                    "No se encontró ningún registro con el ID de cursada %d",
                    courseId
                ))
            )
        );
        
        // Recuperamos al alumno.
        Student student =
        studentRepository // Tabla 'course'.
        .findById(dossier)
        .orElseThrow(
            () -> new EmptyQueryException(
                String.valueOf(String.format(
                    "No se encontró ningún registro con el legajo de alumno %d",
                    dossier
                ))
            )
        );

        // Recuperamos el objeto StudentCourse
        CourseStudent courseStudent =
        courseStudentRepository // Tabla 'course'.
        .findByAlumnoAndCursada(student, course)
        .orElseThrow(
            () -> new EmptyQueryException(
                String.valueOf(String.format(
                    "No se encontró ningún registro de cursada con el legajo de alumno %d",
                    dossier
                ))
            )
        );

        // Recuperamos los eventos de la cursada
        Optional<List<CourseEvent>> eventosCursada = courseEventRepository.findByCursada(course);
        
        // Recuperamos los eventos de la cursada del alumno
        Optional<List<StudentCourseEvent>> eventosCursadaAlumno = studentCourseEventRepository.findByAlumnoAndEventoCursadaIn(student, eventosCursada.get());

        Object response = new Object() {
            public Student estudiante = student;
            public CourseStudent datosCursada = courseStudent;
            public List<StudentCourseEvent> eventos = eventosCursadaAlumno.isPresent() ? eventosCursadaAlumno.get() : null;
        };

        // Devolver la respuesta
        return ResponseEntity.status(HttpStatus.OK).body(response);
        
    }

    public Object getStudents(long courseId) throws EmptyQueryException {
        
        // Obtiene el objeto que representa la cursada.
        Course course = courseRepository
        .findById(courseId)
        .orElseThrow(() -> 
            new EmptyQueryException("No existe la cursada.")
        );

        // Obtiene los registros que vinculan a los estudiantes con la cursada.
        List<CourseStudent> studentsCourseList = studentCourseRepository
        .findByCursada(course)
        .orElse(null);

        /* [030524232635] Genera la respuesta y la devuelve */

        @Data class Response {

            public void addStudentRegister(
                Integer dossier,
                Integer id,
                String name,
                String email,
                Boolean alreadyStudied,
                Boolean allPreviousSubjectsApproved,
                String finalCondition
            ) {
                studentsList.add(
                    new StudentRegister(
                        dossier,
                        id,
                        name,
                        email,
                        alreadyStudied,
                        allPreviousSubjectsApproved,
                        finalCondition
                    )
                );
            }


            /* Private */

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            static class StudentRegister {
                private Integer dossier;
                private Integer id;
                private String name;
                private String email;
                private Boolean alreadyStudied;
                private Boolean allPreviousSubjectsApproved;
                private String finalCondition;
            }

            private List<StudentRegister> studentsList = new ArrayList<StudentRegister>();

        }
        
        Response response = new Response();
        for (CourseStudent courseStudent : studentsCourseList) {

            response.addStudentRegister(
                courseStudent.getAlumno().getLegajo(),
                courseStudent.getAlumno().getDni(),
                courseStudent.getAlumno().getNombre(),
                courseStudent.getAlumno().getEmail(),
                courseStudent.isRecursante(),
                courseStudent.isPreviousSubjectsApproved(),
                courseStudent.getCondicionFinal()
            );
            
        }

        return response;

        /* Fin [030524232635] */

    }

    @SuppressWarnings("null")
    public boolean saveFinalConditions(FinalConditions finalConditions) {
        
        try {

            Course course = courseRepository.getById(finalConditions.getCourseId());

            for (StudentFinalCondition studentFinalCondition : finalConditions.getFinalConditions()) {
                // Buscar el alumno por su legajo
                
                Student student = studentRepository.getByLegajo(studentFinalCondition.getLegajo());
                
                if (student != null) {

                    Optional<CourseStudent> courseStudent = courseStudentRepository.findByAlumnoAndCursada(student, course);

                    // Actualizar la nota del alumno
                    courseStudent.get().setCondicionFinal(studentFinalCondition.getNota());
                    
                    // Guardar el alumno actualizado
                    courseStudentRepository.save(courseStudent.get());
                } else {
                    // Manejar caso donde el alumno no se encuentra
                }
            }
            return true;
        } catch (Exception e) {
            // Manejar cualquier excepción que ocurra durante la actualización
            return false;
        }
    }

    public boolean updateEvent(UpdateEventRequest updateEventRequest) {
        try {
            long eventId = updateEventRequest.getEventId();
    
            Optional<CourseEvent> courseEvent = courseEventRepository.findById(eventId);
    
            if (courseEvent.isPresent()) {
                courseEvent.get().setObligatorio(updateEventRequest.isNewMandatory());
                courseEvent.get().setFechaHoraInicio(updateEventRequest.getNewInitialDate());
                courseEvent.get().setFechaHoraFin(updateEventRequest.getNewEndDate());
                courseEventRepository.save(courseEvent.get());
    
                return true; // Devuelve true si se actualiza correctamente
            } else {
                return false; // Devuelve false si no se encuentra el evento con el ID proporcionado
            }
        } catch (Exception e) {
            // Maneja cualquier excepción y devuelve false si ocurre un error
            e.printStackTrace(); // Opcional: imprime la pila de llamadas para depuración
            return false;
        }
    }

    public String deleteEvent(DeleteEventRequest deleteEventRequest) {
        try {
            long eventId = deleteEventRequest.getEventId();
    
            Optional<CourseEvent> courseEvent = courseEventRepository.findById(eventId);
    
            if (courseEvent.isPresent()) {
                Optional<List<StudentCourseEvent>> registrosEvento = studentCourseEventRepository.findByEventoCursada(courseEvent.get());
                if (registrosEvento.isPresent()) {
                    if (registrosEvento.get().size() > 0) {
                        return "No se puede eliminar el evento porque tiene registros asociados.";
                    }
                } else {
                    return "Error al verificar los registros asociados al evento.";
                }
    
                courseEventRepository.delete(courseEvent.get());
                
                return "El evento se ha eliminado correctamente."; // Devuelve un mensaje de éxito
            } else {
                return "No se encontró el evento con el ID proporcionado."; // Devuelve un mensaje de error
            }
        } catch (Exception e) {
            // Maneja cualquier excepción y devuelve un mensaje de error
            e.printStackTrace(); // Opcional: imprime la pila de llamadas para depuración
            return "Error al eliminar el evento: " + e.getMessage();
        }
    }
    
    
}
