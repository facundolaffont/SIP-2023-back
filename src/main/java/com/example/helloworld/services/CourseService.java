package com.example.helloworld.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.helloworld.HelloWorldApplication;
import com.example.helloworld.models.Comission;
import com.example.helloworld.models.Course;
import com.example.helloworld.models.CourseDto;
import com.example.helloworld.models.CourseEvaluationCriteria;
import com.example.helloworld.models.CourseEvent;
import com.example.helloworld.models.CourseProfessor;
import com.example.helloworld.models.CourseStudent;
import com.example.helloworld.models.EvaluationCriteria;
import com.example.helloworld.models.EventType;
import com.example.helloworld.models.Student;
import com.example.helloworld.models.StudentCourseEvent;
import com.example.helloworld.models.Subject;
import com.example.helloworld.models.Userr;
import com.example.helloworld.models.Exceptions.EmptyQueryException;
import com.example.helloworld.repositories.CourseEvaluationCriteriaRepository;
import com.example.helloworld.repositories.CourseEventRepository;
import com.example.helloworld.repositories.CourseProfessorRepository;
import com.example.helloworld.repositories.CourseRepository;
import com.example.helloworld.repositories.EvaluationCriteriaRepository;
import com.example.helloworld.repositories.EventTypeRepository;
import com.example.helloworld.repositories.StudentCourseEventRepository;
import com.example.helloworld.repositories.StudentCourseRepository;
import com.example.helloworld.repositories.UserRepository;

@Service
public class CourseService {

    public List<CourseDto> getProfessorCourses(String userId) throws SQLException {

        logger.debug(String.format(
            "Se ejecuta el método getProfessorCourses. [userId = %s]",
            userId
        ));

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
    public ResponseEntity<String> calculateFinalCondition(long courseId)
        throws EmptyQueryException
    {

        logger.debug(String.format(
            "Se ejecuta el método calculateFinalCondition. [courseId = %d]",
            courseId
        ));

        // Calcular Condicion Final de los alumnos de la cursada del docente.

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
            
        // Evaluamos a cada alumno.
        var returningJson = new JSONArray();
        for (CourseStudent alumnoCursada : courseStudentList) {

            // Iteramos por cada criterio de la cursada.
            var newStudentRegister = (new JSONObject())    
                .put("Legajo", alumnoCursada.getAlumno().getLegajo());
            String lowestCondition = "";
            for (CourseEvaluationCriteria criterioCursada : criteriosCursada) {
                
                switch (criterioCursada.getCriteria().getName()) {

                    case "Asistencias":
                        String attendanceCondition = evaluarAsistencia(course, alumnoCursada.getAlumno());
                        lowestCondition =
                            lowestCondition.isEmpty()
                            ? attendanceCondition
                            : getMinimalCondition(lowestCondition, attendanceCondition);
                    break;

                    case "Trabajos prácticos aprobados":
                        String condicionTPsAprobados = evaluarTPsAprobados(
                            course,
                            alumnoCursada.getAlumno()
                        );
                        lowestCondition =
                            lowestCondition.isEmpty()
                            ? condicionTPsAprobados
                            : getMinimalCondition(lowestCondition, condicionTPsAprobados);
                    break;

                    case "Trabajos prácticos recuperados":
                        String condicionTPsRecuperados = evaluarTPsRecupeados(courseId, alumnoCursada.getAlumno());
                        lowestCondition =
                            lowestCondition.isEmpty()
                            ? condicionTPsRecuperados
                            : getMinimalCondition(lowestCondition, condicionTPsRecuperados);
                    break;

                    // SEGUIR
                    case "Parciales recuperados":
                        String condicionParcialesAprobados = evaluarParcialesAprobados(courseId, alumnoCursada.getAlumno());
                        lowestCondition =
                            lowestCondition.isEmpty()
                            ? condicionParcialesAprobados
                            : getMinimalCondition(lowestCondition, condicionParcialesAprobados);
                    break;

                    case "Parciales aprobados":
                        String condicionParcialesRecuperados = evaluarParcialesAprobados(courseId, alumnoCursada.getAlumno());
                        lowestCondition =
                            lowestCondition.isEmpty()
                            ? condicionParcialesRecuperados
                            : getMinimalCondition(lowestCondition, condicionParcialesRecuperados);
                    break;

                    case "Promedio de Parciales":
                        String condicionPromedioParciales = evaluarPromedioParciales(courseId, alumnoCursada.getAlumno());
                        lowestCondition =
                            lowestCondition.isEmpty()
                            ? condicionPromedioParciales
                            : getMinimalCondition(lowestCondition, condicionPromedioParciales);
                    break;

                    case "Autoevaluaciones aprobadas":
                        String condicionAEAprobadas = evaluarAEAprobadas(courseId, alumnoCursada.getAlumno());
                        lowestCondition =
                            lowestCondition.isEmpty()
                            ? condicionAEAprobadas
                            : getMinimalCondition(lowestCondition, condicionAEAprobadas);
                    break;

                    case "Autoevaluaciones recuperadas":
                        String condicionAERecuperadas = evaluarAERecuperadas(courseId, alumnoCursada.getAlumno());
                        lowestCondition =
                            lowestCondition.isEmpty()
                            ? condicionAERecuperadas
                            : getMinimalCondition(lowestCondition, condicionAERecuperadas);
                    break;
                
                }

                if (lowestCondition.equals("L")) break;

            }

            newStudentRegister
                .put(
                    "Condición",
                    lowestCondition
                );
            returningJson.put(newStudentRegister);

        }

        return (ResponseEntity
            .status(HttpStatus.OK)
            .header("Content-Type", "application/json")
            .body(returningJson.toString())
        );

    }


    /* Private */ 

    private static final Logger logger = LoggerFactory.getLogger(HelloWorldApplication.class);
    @Autowired private CourseProfessorRepository courseProfessorRepository;
    @Autowired private StudentCourseRepository studentCourseRepository;
    @Autowired private StudentCourseEventRepository studentCourseEventRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private EvaluationCriteriaRepository evaluationCriteriaRepository;
    @Autowired private EventTypeRepository eventTypeRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private CourseEventRepository courseEventRepository;
    @Autowired private CourseEvaluationCriteriaRepository courseEvaluationCriteriaRepository;

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

    private String evaluarTPsAprobados(Course course, Student alumno)
        throws EmptyQueryException
    {
 
        /**
         * Obtener todos los registros de la tabla evento_cursada_alumno
         * cuyo id_evento corresponda a registros de la tabla evento_cursada (AA),
         * que, a su vez, cuyo id_cursada sea la de la cursada actual y cuyo
         * id_tipo corresponda al registro de la tabla tipo_evento (AB), que,
         * a su vez, cuyo nombre sea "Trabajo práctico" (AC)
         * -> trabajos_practicos_alumno (A).
         * 
         * Calcular el porcentaje de registros de [A] que tengan nota "4", "A"
         * o "A-" (B).
         * 
         * Obtener el registro de la tabla criterio_cursada cuyo
         * atributo id_cursada sea igual a la cursada actual y cuyo id_criterio
         * corresponda al registro de la tabla criterio_evaluacion (CA), que, a su vez,
         * cuyo nombre sea "Trabajos prácticos aprobados" (CB).
         * 
         * Si el porcentaje calculado en [B] es mayor o igual al porcentaje de
         * valor_promovido, devuelve "P" (DA); si es mayor o igual al porcentaj de
         * valor_regular, devuelve "R" (DB); si no, devuelve "L" (DC).
         */

        String nota = "L"; // (DC): se devuelve esto si no se cumple con (DA) ni (DB).

        // (A)
        
        // (AC)
        Optional<EventType> eventType =
            eventTypeRepository
            .findByNombre("Trabajo práctico");

        // (AB)
        List<CourseEvent> courseEventList =
            courseEventRepository
            .findByCursadaAndTipoEvento(course, eventType.get())
            .orElseThrow(
                () -> new EmptyQueryException("No hubo instancias de evaluación de trabajo práctico.")
            );

        // (AA)
        Optional<List<StudentCourseEvent>> studentCourseEventList
            = studentCourseEventRepository
            .findByEventoCursadaIn(courseEventList);

        // (B)
        int tpsAprobados = 0;
        int tpsTotales = 0;
        for (StudentCourseEvent studentCourseEvent : studentCourseEventList.get()) {
            tpsTotales++;
            if (studentCourseEvent
                .getNota()
                .matches("^([4-9]|10|A-?)$")
            ) tpsAprobados++;
        }

        // (CB)
        EvaluationCriteria evaluationCriteria =
            evaluationCriteriaRepository
            .findByName("Trabajos prácticos aprobados");

        // (CA)
        // EvaluationCriteria criteria
        // Course course
        CourseEvaluationCriteria courseEvaluationCriteria =
            courseEvaluationCriteriaRepository
            .findByCriteriaAndCourse(evaluationCriteria, course);

        // (DA)
        if (tpsAprobados / tpsTotales >= courseEvaluationCriteria.getValue_to_promote())
            nota = "P";
        else if (tpsAprobados / tpsTotales >= courseEvaluationCriteria.getValue_to_regulate())
            nota = "R";

        return nota;
    }

    private String evaluarAsistencia(Course cursada, Student alumno) {
        
        // Recupero los eventos de la cursada

        List<CourseEvent> eventos = courseEventRepository.findByCursada(cursada);

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
                logger.debug("entre aca");
                // Recupero el 'Evento_Cursada_Alumno' correspondiente
                logger.debug(String.format(
                    "findByEventoCursadaAndAlumno. [evento = %s] [alumno = %s]",
                    evento.toString(),
                    alumno.toString()
                ));
                StudentCourseEvent eventoClaseAlumno = studentCourseEventRepository.findByEventoCursadaAndAlumno(evento, alumno);
                 
                // Si el campo de asistencia es true, incremento las presencias del alumno
                if (eventoClaseAlumno != null && eventoClaseAlumno.isAsistencia()) {
                    presenciasAlumno++;
                }

                eventosAsistencias++;
            }

        }
        float porcentajeAlumno = (float) presenciasAlumno / (float) eventosAsistencias * 100;

        if (porcentajeAlumno >= valorPromovido)
            return "P";
        else
            if (porcentajeAlumno >= valorRegular)
                return "R";
            else
                return "L";
    }
    
}
