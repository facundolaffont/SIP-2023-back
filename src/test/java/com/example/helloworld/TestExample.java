package com.example.helloworld;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.helloworld.models.Course;
import com.example.helloworld.models.CourseEvaluationCriteria;
import com.example.helloworld.models.CourseEvent;
import com.example.helloworld.models.CourseStudent;
import com.example.helloworld.models.EvaluationCriteria;
import com.example.helloworld.models.EventType;
import com.example.helloworld.models.Student;
import com.example.helloworld.models.StudentCourseEvent;
import com.example.helloworld.models.Exceptions.EmptyQueryException;
import com.example.helloworld.repositories.CourseEvaluationCriteriaRepository;
import com.example.helloworld.repositories.CourseEventRepository;
import com.example.helloworld.repositories.CourseRepository;
import com.example.helloworld.repositories.EvaluationCriteriaRepository;
import com.example.helloworld.repositories.EventTypeRepository;
import com.example.helloworld.repositories.StudentCourseEventRepository;
import com.example.helloworld.repositories.StudentCourseRepository;
import com.example.helloworld.services.CourseService;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TestExample {

    @Mock
    private CourseEventRepository courseEventRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseEvaluationCriteriaRepository courseEvaluationCriteriaRepository;

    @Mock
    private StudentCourseRepository studentCourseRepository;

    @Mock
    private EventTypeRepository eventTypeRepository;

    @Mock
    private EvaluationCriteriaRepository evaluationCriteriaRepository;

    @Mock
    private StudentCourseEventRepository studentCourseEventRepository;

    @InjectMocks
    private CourseService courseService;

    @Test
    public void testEvaluarAsistencia() throws EmptyQueryException {

        // Preparar datos de prueba

        long courseId = 1;
        Course course = new Course();
        course.setId(courseId);

        Student student = new Student();
        student.setLegajo(1);
        student.setNombre("Leo");
        student.setApellido("Duville");
        student.setDni(43186516);
        student.setEmail("student@gmail.com");

        EvaluationCriteria evaluationCriteria = new EvaluationCriteria();
        evaluationCriteria.setId(1);
        evaluationCriteria.setName("Asistencias");

        CourseEvaluationCriteria courseEvaluationCriteria = new CourseEvaluationCriteria();
        courseEvaluationCriteria.setId(1);
        courseEvaluationCriteria.setCourse(course);
        courseEvaluationCriteria.setCriteria(evaluationCriteria);
        courseEvaluationCriteria.setValue_to_regulate(50);
        courseEvaluationCriteria.setValue_to_promote(75);

        EventType clase = new EventType();
        clase.setId(1);
        clase.setNombre("Clase");

        // Crear eventos del curso usando un bucle for
        List<CourseEvent> eventosList = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            CourseEvent evento = new CourseEvent();
            evento.setCursada(course);
            evento.setId(i);
            evento.setTipoEvento(clase);
            evento.setObligatorio(i <= 3); // Los primeros 3 eventos son obligatorios
            evento.setFechaHoraInicio(Timestamp.valueOf("2024-04-" + (20 + i) + " 10:00:00"));
            evento.setFechaHoraFin(Timestamp.valueOf("2024-04-" + (20 + i) + " 12:00:00"));
            eventosList.add(evento);
        }

        // Crear un Optional que contenga la lista de eventos
        Optional<List<CourseEvent>> eventosOptional = Optional.of(eventosList);

        // Crear todos los StudentCourseEvent de una vez
        List<Optional<StudentCourseEvent>> eventosAlumnos = new ArrayList<>();
        for (int i = 0; i < eventosList.size(); i++) {
            CourseEvent evento = eventosList.get(i);
            Optional<StudentCourseEvent> eventoAlumno = Optional.of(new StudentCourseEvent());
            eventoAlumno.get().setId(i + 1);
            eventoAlumno.get().setAlumno(student);
            eventoAlumno.get().setEventoCursada(evento);
            eventoAlumno.get().setAsistencia(i % 2 == 0); // Asistencia alternada
            eventosAlumnos.add(eventoAlumno);
        }

        when(courseEventRepository.findByCursada(course)).thenReturn(eventosOptional);
        assertEquals(eventosOptional, courseEventRepository.findByCursada(course));

        when(evaluationCriteriaRepository.findByName("Asistencias")).thenReturn(evaluationCriteria);
        assertEquals(evaluationCriteria, evaluationCriteriaRepository.findByName("Asistencias"));

        when(courseEvaluationCriteriaRepository.findByCriteriaAndCourse(evaluationCriteria, course)).thenReturn(courseEvaluationCriteria);
        assertEquals(courseEvaluationCriteria, courseEvaluationCriteriaRepository.findByCriteriaAndCourse(evaluationCriteria, course));

        for (int i = 0; i < eventosList.size(); i++) {
            CourseEvent evento = eventosList.get(i);
            Optional<StudentCourseEvent> eventoAlumno = eventosAlumnos.get(i);
            when(studentCourseEventRepository.findByEventoCursadaAndAlumno(evento, student)).thenReturn(eventoAlumno);
        }

        ArrayList<String> resultadoEsperado = new ArrayList<>();
        resultadoEsperado.add("100.0");
        resultadoEsperado.add("R");

        assertEquals(resultadoEsperado, courseService.evaluarAsistencia(course, student));

    }
}