package com.example.helloworld;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.helloworld.models.Course;
import com.example.helloworld.models.CourseEvaluationCriteria;
import com.example.helloworld.models.CourseEvent;
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
import java.util.Random;
import java.util.random.RandomGenerator;

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

        Course course = generarCursada();

        Student student = generarEstudiante();

        EvaluationCriteria evaluationCriteria = generarCriterioEvaluacion("Asistencias");

        CourseEvaluationCriteria courseEvaluationCriteria = generarCourseEvaluationCriteria(course, evaluationCriteria, 50, 75);
        
        EventType clase = generarTipoEvento("Clase");

        // Crear eventos del curso usando un bucle for
        List<CourseEvent> eventosList = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            CourseEvent evento = new CourseEvent();
            evento.setCursada(course);
            evento.setId(i);
            evento.setTipoEvento(clase);
            evento.setObligatorio(true); // Los primeros 3 eventos son obligatorios
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
            eventoAlumno.get().setAsistencia(true);
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
        resultadoEsperado.add("P");

        assertEquals(resultadoEsperado, courseService.evaluarAsistencia(course, student));

    }

    @Test
    public void testParcialesAprobados() {

        Course course = generarCursada();
        
        Student student = generarEstudiante();

        EvaluationCriteria evaluationCriteria = generarCriterioEvaluacion("Parciales Aprobados");

        CourseEvaluationCriteria courseEvaluationCriteria = generarCourseEvaluationCriteria(course, evaluationCriteria, 50, 75);
        
        EventType parcial = generarTipoEvento("Parcial");
        
        EventType recParcial = generarTipoEvento("Recuperatorio Parcial");

        // Crear eventos del curso usando un bucle for
        List<CourseEvent> eventosList = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            CourseEvent evento = new CourseEvent();
            evento.setCursada(course);
            evento.setId(i);
            evento.setTipoEvento(parcial);
            evento.setObligatorio(true);
            evento.setFechaHoraInicio(Timestamp.valueOf("2024-04-" + (20 + i) + " 10:00:00"));
            evento.setFechaHoraFin(Timestamp.valueOf("2024-04-" + (20 + i) + " 12:00:00"));
            eventosList.add(evento);
        }

        // Crear un Optional que contenga la lista de eventos
        Optional<List<CourseEvent>> eventosOptional = Optional.of(eventosList);

        // Crear todos los StudentCourseEvent de una vez
        List<Optional<StudentCourseEvent>> eventosAlumnos = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < eventosList.size(); i++) {
            CourseEvent evento = eventosList.get(i);
            Optional<StudentCourseEvent> eventoAlumno = Optional.of(new StudentCourseEvent());
            eventoAlumno.get().setId(i + 1);
            eventoAlumno.get().setAlumno(student);
            eventoAlumno.get().setEventoCursada(evento);
            eventoAlumno.get().setAsistencia(true);
            eventoAlumno.get().setNota(String.valueOf(rand.nextInt(7) + 4));
            eventosAlumnos.add(eventoAlumno);
        }

        when(eventTypeRepository.findByNombre("Parcial")).thenReturn(Optional.of(parcial));

        when(courseEventRepository.findByCursadaAndTipoEvento(course, parcial)).thenReturn(eventosOptional);

        when(eventTypeRepository.findByNombre("Recuperatorio Parcial")).thenReturn(Optional.of(recParcial));

        when(courseEventRepository.findByCursadaAndTipoEvento(course, recParcial)).thenReturn(Optional.empty());

        for (int i = 0; i < eventosList.size(); i++) {
            CourseEvent evento = eventosList.get(i);
            Optional<StudentCourseEvent> eventoAlumno = eventosAlumnos.get(i);
            when(studentCourseEventRepository.findByEventoCursadaAndAlumno(evento, student)).thenReturn(eventoAlumno);
        }

        when(evaluationCriteriaRepository.findByName("Parciales aprobados")).thenReturn(evaluationCriteria);

        when(courseEvaluationCriteriaRepository.findByCriteriaAndCourse(evaluationCriteria, course)).thenReturn(courseEvaluationCriteria);

        assertEquals("P", courseService.evaluarParcialesAprobados(course, student));

    }

    private Course generarCursada() {

        Course course = new Course();
        course.setId(1);
        return course;

    }

    private Student generarEstudiante() {

        Student student = new Student();
        student.setLegajo(1);
        student.setNombre("Leo");
        student.setApellido("Duville");
        student.setDni(43186516);
        student.setEmail("student@gmail.com");
        return student;

    }

    private EvaluationCriteria generarCriterioEvaluacion(String name) {

        EvaluationCriteria evaluationCriteria = new EvaluationCriteria();
        evaluationCriteria.setId(1);
        evaluationCriteria.setName(name);
        return evaluationCriteria;

    }

    private CourseEvaluationCriteria generarCourseEvaluationCriteria (Course course, EvaluationCriteria evaluationCriteria, long value_to_regulate, long value_to_promote ) {

        CourseEvaluationCriteria courseEvaluationCriteria = new CourseEvaluationCriteria();
        courseEvaluationCriteria.setId(1);
        courseEvaluationCriteria.setCourse(course);
        courseEvaluationCriteria.setCriteria(evaluationCriteria);
        courseEvaluationCriteria.setValue_to_regulate(50);
        courseEvaluationCriteria.setValue_to_promote(75);
        return courseEvaluationCriteria;

    }

    private EventType generarTipoEvento(String name) {

        Random random = new Random();
        EventType parcial = new EventType();
        parcial.setId(random.nextInt(100));
        parcial.setNombre(name);
        return parcial;

    }



}