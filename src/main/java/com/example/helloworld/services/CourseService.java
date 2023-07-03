package com.example.helloworld.services;

import java.sql.SQLException;
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
import com.example.helloworld.models.Career;
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
import com.example.helloworld.repositories.CourseStudentRepository;
import com.example.helloworld.repositories.EvaluationCriteriaRepository;
import com.example.helloworld.repositories.EventTypeRepository;
import com.example.helloworld.repositories.StudentCourseEventRepository;
import com.example.helloworld.repositories.StudentCourseRepository;
import com.example.helloworld.repositories.StudentRepository;
import com.example.helloworld.repositories.UserRepository;
import com.example.helloworld.requests.CheckStudentsRegistrationStatusRequest;
import com.example.helloworld.requests.StudentRegistrationRequest;
import com.example.helloworld.requests.StudentsRegistrationRequest;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Service
public class CourseService {

    public List<CourseDto> getProfessorCourses(String userId)
        throws SQLException, EmptyQueryException
    {

        logger.debug(String.format(
            "Se ejecuta el método getProfessorCourses. [userId = %s]",
            userId
        ));

        Userr docente = userRepository
            .findById(userId)
            .orElseThrow(() ->
                new EmptyQueryException("El docente con ID %s no existe.".formatted(
                    userId
                ))
            );
        List<CourseProfessor> courseProfessors = courseProfessorRepository
            .findByIdDocente(docente)
            .orElse(null);
        List<CourseDto> cursadas = new ArrayList<>();
        for (CourseProfessor courseProfessor : courseProfessors) {

            // Accede a la información de CourseProfessor y Course
            Course course = courseProfessor.getCursada();
            Comission comission = course.getComision();
            Subject asignatura = comission.getAsignatura();
            Career carrera = asignatura.getIdCarrera();
            // Realiza acciones con los objetos CourseProfessor y Course encontrados
            CourseDto cursada = new CourseDto();
            cursada.setId(course.getId());
            cursada.setNombreAsignatura(asignatura.getNombre());
            cursada.setNombreCarrera(carrera.getNombre());
            cursada.setNumeroComision(comission.getId());
            cursada.setAnio(course.getAnio());
            cursada.setNivelPermiso(courseProfessor.getNivelPermiso());
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
                
                try {

                    switch (criterioCursada.getCriteria().getName()) {

                        case "Asistencias":
                            String attendanceCondition = evaluarAsistencia(course, alumnoCursada.getAlumno());
                            lowestCondition =
                                lowestCondition.isEmpty()
                                ? attendanceCondition
                                : getMinimalCondition(lowestCondition, attendanceCondition);
                        break;

                        case "Trabajos prácticos aprobados":
                            String condicionTPsAprobados = evaluarTPsAprobados(course, alumnoCursada.getAlumno());
                            if (condicionTPsAprobados != null) {
                                lowestCondition =
                                    lowestCondition.isEmpty()
                                    ? condicionTPsAprobados
                                    : getMinimalCondition(lowestCondition, condicionTPsAprobados);
                            }
                        break;

                        case "Trabajos prácticos recuperados":
                            String condicionTPsRecuperados = evaluarTPsRecuperados(course, alumnoCursada.getAlumno());
                            if (condicionTPsRecuperados != null) {
                                lowestCondition =
                                    lowestCondition.isEmpty()
                                    ? condicionTPsRecuperados
                                    : getMinimalCondition(lowestCondition, condicionTPsRecuperados);
                            }
                        break;

                        case "Parciales recuperados":
                            String condicionParcialesRecuperados = evaluarParcialesRecuperados(course, alumnoCursada.getAlumno());
                            if (condicionParcialesRecuperados != null) {
                                lowestCondition =
                                    lowestCondition.isEmpty()
                                    ? condicionParcialesRecuperados
                                    : getMinimalCondition(lowestCondition, condicionParcialesRecuperados);
                            }
                        break;

                        case "Parciales aprobados":
                            String condicionParcialesAprobados = evaluarParcialesAprobados(course, alumnoCursada.getAlumno());
                            if (condicionParcialesAprobados != null) {
                                lowestCondition =
                                    lowestCondition.isEmpty()
                                    ? condicionParcialesAprobados
                                    : getMinimalCondition(lowestCondition, condicionParcialesAprobados);
                            }
                        break;

                        case "Promedio de parciales":
                            String condicionPromedioParciales = evaluarPromedioParciales(course, alumnoCursada.getAlumno());
                            if (condicionPromedioParciales != null) {
                                lowestCondition =
                                    lowestCondition.isEmpty()
                                    ? condicionPromedioParciales
                                    : getMinimalCondition(lowestCondition, condicionPromedioParciales);
                            }
                        break;

                        case "Autoevaluaciones aprobadas":
                            String condicionAEAprobadas = evaluarAEAprobadas(course, alumnoCursada.getAlumno());
                            if (condicionAEAprobadas != null) {
                            lowestCondition =
                                lowestCondition.isEmpty()
                                ? condicionAEAprobadas
                                : getMinimalCondition(lowestCondition, condicionAEAprobadas);
                            }
                        break;

                        case "Autoevaluaciones recuperadas":
                            String condicionAERecuperadas = evaluarAERecuperadas(course, alumnoCursada.getAlumno());
                            if (condicionAERecuperadas != null) {
                            lowestCondition =
                                lowestCondition.isEmpty()
                                ? condicionAERecuperadas
                                : getMinimalCondition(lowestCondition, condicionAERecuperadas);
                            }
                        break;

                    }

                    if (lowestCondition.equals("L")) break;

                } catch (Exception e) {
                    System.out.println(e);
                }

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

    // public ResponseEntity<String> getStudentsRegistrationStatus(
    //     CheckStudentsRegistrationStatusRequest checkStudentsRegistrationStatusRequest
    // ) {

    //     /*
    //      * (A) Obtiene todos los registros de alumno que matcheen con los que están
    //      * almacenados en studentsRegistrationCheckRequest, específicamente el
    //      * legajo, dni, apellido, nombre y mail, y los coloca en lista registeredStudents.
    //      * Para ello se buscará en tabla alumno.
    //      *
    //      * (B) Construye la lista de estudiantes que no están registrados en sistema
    //      * (unregisteredStudents) y que es el resultado de restarle a
    //      * studentsRegistrationCheckRequest los que están en registeredStudents.
    //      *
    //      * (C) Determina cuáles legajos de registeredStudents están vinculados a la comisión,
    //      * obteniendo los datos que determinan si es recursante y si tiene todas las correlativas
    //      * aprobadas, y quita de registeredStudents los que matchean para colocarlos en lista
    //      * studentsInCourse. Además, se crea el alias registeredStudentsNotInCourse que apunta a
    //      * registeredStudents.
    //      *
    //      * (E) Devuelve un objeto equivalente al siguiente YAML:
    //      *  unregistered:
    //      *  - # <legajo>
    //      *  - # ...
    //      *
    //      *  registered:
    //      *
    //      *    inCourse:
    //      *    - dossier: # <integer>
    //      *      id: # <long>
    //      *      surname: # <string>
    //      *      name: # <string>
    //      *      mail: # <string>
    //      *
    //      *      # false si es recursante.
    //      *      firstTime: # true|false
    //      *      
    //      *      # true si tiene todas las correlativas aprobadas.
    //      *      previousSubjectsApproved: # true|false
    //      *      
    //      *    - ...
    //      *    
    //      *    notInCourse:
    //      *    - dossier: # <integer>
    //      *      id: # <long>
    //      *      surname: # <string>
    //      *      name: # <string>
    //      *      mail: # <string>
    //      *    - ...
    //      */
    //     // (A)
    //     List<Student> registeredStudents = studentRepository
    //         .findByLegajoIn(checkStudentsRegistrationStatusRequest.getDossierList())
    //         .get();

    //     // (B)
    //     List<Integer> registeredStudentsDossiers = registeredStudents
    //         .stream()
    //         .map(student -> student.getLegajo())
    //         .collect(Collectors.toList());
    //     List<Integer> unregisteredStudents = checkStudentsRegistrationStatusRequest
    //         .getDossierList()
    //         .stream()
    //         .filter(dossier -> 

    //             // Si no existe en algún legajo de registeredStudentsList, lo devuelve.
    //             !registeredStudentsDossiers.contains(dossier)

    //         )
    //         .collect(Collectors.toList());

    //     // (C)
    //     Course course = courseRepository
    //         .findById(checkStudentsRegistrationStatusRequest.getCourseId())
    //         .get();
    //     List<Student> studentsInCourse = registeredStudents
    //         .stream()
    //         .filter(student ->
    //             courseStudentRepository
    //                 .findByAlumnoAndCursada(student, course)
    //                 .isPresent()
    //         )
    //         .collect(Collectors.toList());
    //     List<CourseStudent> studentsCourseInfo = studentsInCourse
    //         .stream()
    //         .map(student ->
    //             courseStudentRepository
    //                 .findByAlumnoAndCursada(student, course)
    //                 .get()
    //         )
    //         .collect(Collectors.toList());
    //     registeredStudents = registeredStudents
    //         .stream()
    //         .filter(student ->
    //             !studentsInCourse.contains(student)
    //         )
    //         .collect(Collectors.toList());
    //     List<Student> registeredStudentsNotInCourse = registeredStudents;

    //    // (D)
    //    var registeredStudentsInCourseJson = new JSONArray();
    //    studentsCourseInfo
    //        .forEach(courseStudent -> {
    //            Student student = courseStudent.getAlumno();
    //            registeredStudentsInCourseJson.put(
    //                (new JSONObject())
    //                    .put("dossier", student.getLegajo())
    //                    .put("id", student.getDni())
    //                    .put("surname", student.getApellido())
    //                    .put("name", student.getNombre())
    //                    .put("mail", student.getEmail())
    //                    .put("firstTime", 
    //                        courseStudent.isRecursante()
    //                            ? true
    //                            : false
    //                    )
    //                    .put("previousSubjectsApproved",
    //                        courseStudent.getCondicionFinal() == "P"
    //                            ? true
    //                            : false
    //                    )
    //            );
    //        });

    //    // (F) Construye la lista registeredStudentsNotInCourseJson.
    //    var registeredStudentsNotInCourseJson = new JSONArray();
    //    registeredStudentsNotInCourse
    //        .forEach(student -> {
    //            registeredStudentsNotInCourseJson.put(
    //                (new JSONObject())
    //                    .put("dossier", student.getLegajo())
    //                    .put("id", student.getDni())
    //                    .put("surname", student.getApellido())
    //                    .put("name", student.getNombre())
    //                    .put("mail", student.getEmail())
    //            );
    //        });

    //    // (E)
    //    logger.debug(
    //        (new JSONObject())
    //                .put("unregistered", unregisteredStudents)
    //                .put("registered", (new JSONObject())
    //                    .put("inCourse", registeredStudentsInCourseJson)
    //                    .put("notInCourse", registeredStudentsNotInCourseJson)
    //                )
    //                .toString()
    //    );
    //    return (ResponseEntity
    //        .status(HttpStatus.OK)
    //        .header("Content-Type", "application/json")
    //        .body(
    //            (new JSONObject())
    //                .put("unregistered", unregisteredStudents)
    //                .put("registered", (new JSONObject())
    //                    .put("inCourse", registeredStudentsInCourseJson)
    //                    .put("notInCourse", registeredStudentsNotInCourseJson)
    //                )
    //                .toString()
    //        )
    //    );

    //     // -----------------------
        
    //     // // Definición de la clase del objeto que se devolverá.
    //     // @Data class Result {

    //     //     public addOk(Ok register) {
    //     //         ok.add(
    //     //             new Ok(
    //     //                 Integer dossier,
    //     //                 Long id,
    //     //                 String name,
    //     //                 String surname
    //     //             )
    //     //         );
    //     //     }

    //     //     public addNotOk(
    //     //         Integer dossier;
    //     //         Integer errorCode;
    //     //     ) {
    //     //         nok.add(
    //     //             new NotOk(
    //     //                 dossier,
    //     //                 errorCode
    //     //             )
    //     //         );
    //     //     }


    //     //     /* Private */

    //     //     @Data
    //     //     @NoArgsConstructor
    //     //     @AllArgsConstructor
    //     //     static class Unregistered {
    //     //         private Integer dossier;
    //     //         private Long id;
    //     //         private String name;
    //     //         private String surname;
    //     //     }

    //     //     @Data
    //     //     @NoArgsConstructor
    //     //     @AllArgsConstructor
    //     //     static class Registered {
    //     //         private Integer dossier;
    //     //         private Integer errorCode;
    //     //     }

    //     //     private List<Ok> ok;
    //     //     private List<NotOk> nok;

    //     // }
        
    //     // // (E)
    //     // var response = new Result();
    //     // studentsCourseInfo
    //     //     .forEach(courseStudent -> {
    //     //         Student student = courseStudent.getAlumno();
    //     //         response.addNotOk(
    //     //             student.getLegajo(),
    //     //             2
    //     //         );
    //     //     });
    //     // registeredStudentsNotInCourse
    //     //     .forEach(student -> {
    //     //         response.addOk(
    //     //             student.getLegajo(),
    //     //             student.getDni(),
    //     //             student.getApellido(),
    //     //             student.getNombre()
    //     //         )
    //     //     });
    //     // response.setOk(registeredStudentsNotInCourseList
    //     // logger.debug(
    //     //     (new JSONObject())
    //     //             .put("unregistered", unregisteredStudents)
    //     //             .put("registered", (new JSONObject())
    //     //                 .put("inCourse", registeredStudentsInCourseJson)
    //     //                 .put("notInCourse", registeredStudentsNotInCourseJson)
    //     //             )
    //     //             .toString()
    //     // );
    //     // return response;

    // }

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
                StudentRegistrationRequest studentRegistrationInfo = studentsRegistrationRequest
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

        // (AA)
        Optional<List<StudentCourseEvent>> studentCourseEventList
            = studentCourseEventRepository
            .findByAlumnoAndEventoCursadaIn(alumno, courseEventList.get());

        // (B)
        int parcialesRecuperados = 0;
        int parcialesTotales = 0;
        for (StudentCourseEvent studentCourseEvent : studentCourseEventList.get()) {
            parcialesTotales++;
            if (studentCourseEvent
                .getNota()
                .matches("^([4-9]|10|A-?)$")
            ) parcialesRecuperados++;
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

            if (porcentajeTps <= courseEvaluationCriteria.getValue_to_promote())
                nota = "P";
            else if (porcentajeTps <= courseEvaluationCriteria.getValue_to_regulate())
                nota = "R";
            else nota = "L";
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

        // (AA)
        Optional<List<StudentCourseEvent>> studentCourseEventList
            = studentCourseEventRepository
            .findByAlumnoAndEventoCursadaIn(alumno, courseEventList.get());

        // (B)
        int autoevaluacionesRecuperadas = 0;
        int autoevaluacionesTotales = 0;
        for (StudentCourseEvent studentCourseEvent : studentCourseEventList.get()) {
            autoevaluacionesTotales++;
            if (studentCourseEvent
                .getNota()
                .matches("^([4-9]|10|A-?)$")
            ) autoevaluacionesRecuperadas++;
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

            if (porcentajeAutoevaluaciones <= courseEvaluationCriteria.getValue_to_promote())
                nota = "P";
            else if (porcentajeAutoevaluaciones <= courseEvaluationCriteria.getValue_to_regulate())
                nota = "R";
            else nota = "L";
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
        int autoevaluacionesAprobadas = 0;
        int autoevaluacionesTotales = 0;
        for (CourseEvent courseEvent : courseEventList.get()) {

            autoevaluacionesTotales++;

            // (AA)
            StudentCourseEvent studentCourseEvent
                = studentCourseEventRepository
                .findByEventoCursadaAndAlumno(courseEvent, alumno);


            if (studentCourseEvent
                .getNota()
                .matches("^([4-9]|10|A-?)$")
            ) autoevaluacionesAprobadas++;

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
        int sumaNotasParciales = 0;
        int parcialesTotales = 0;
        for (CourseEvent courseEvent : courseEventList.get()) {
            
            parcialesTotales++;

            // (AA)
            StudentCourseEvent studentCourseEvent
                = studentCourseEventRepository
                .findByEventoCursadaAndAlumno(courseEvent, alumno);

            if (studentCourseEvent
                .getNota()
                .matches("^([4-9]|10|A-?)$")
            ) sumaNotasParciales += Integer.parseInt(studentCourseEvent.getNota());

        }

        // (DA)

        // Si tiene un parcial, podria ser que tenga un recuperatorio.
        if (parcialesTotales == 1) {

            Optional<EventType> eventTypeRec =
                eventTypeRepository
                .findByNombre("Recuperatorio Parcial");

            // (AB)
            Optional<List<CourseEvent>> courseEventListRec =
                courseEventRepository
                .findByCursadaAndTipoEvento(course, eventTypeRec.get());

            // (AA)
            Optional<List<StudentCourseEvent>> studentCourseEventListRec
                = studentCourseEventRepository
                .findByAlumnoAndEventoCursadaIn(alumno, courseEventListRec.get());

            if (studentCourseEventListRec.isPresent() && studentCourseEventListRec.get().size() != 0)
                sumaNotasParciales += Integer.parseInt(studentCourseEventListRec.get().get(0).getNota());

            parcialesTotales++;
        }

        if (parcialesTotales != 0) {

            // (CB)
            EvaluationCriteria evaluationCriteria =
            evaluationCriteriaRepository
            .findByName("Parciales aprobados");

            // (CA)
            // EvaluationCriteria criteria
            // Course course
            CourseEvaluationCriteria courseEvaluationCriteria =
            courseEvaluationCriteriaRepository
            .findByCriteriaAndCourse(evaluationCriteria, course);

            float promedioParciales = (float) sumaNotasParciales / (float) parcialesTotales * 100;    

            if (promedioParciales >= courseEvaluationCriteria.getValue_to_promote())
                nota = "P";
            else if (promedioParciales >= courseEvaluationCriteria.getValue_to_regulate())
                nota = "R";
            else nota = "L";
        }
        return nota;
    }

    private String evaluarParcialesAprobados(Course course, Student alumno) {
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
        int parcialesAprobados = 0;
        int parcialesTotales = 0;
        for (CourseEvent courseEvent : courseEventList.get()) {

            parcialesTotales++;

            // (AA)
            StudentCourseEvent studentCourseEvent
                = studentCourseEventRepository
                .findByEventoCursadaAndAlumno(courseEvent, alumno);

            if (studentCourseEvent
                .getNota()
                .matches("^([4-9]|10|A-?)$")
            ) parcialesAprobados++;

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

        // (B)
        int tpsRecuperados = 0;
        int tpsTotales = 0;
        for (CourseEvent courseEvent : courseEventList.get()) {

            tpsTotales++;

            // (AA)
            StudentCourseEvent studentCourseEvent
                = studentCourseEventRepository
                .findByEventoCursadaAndAlumno(courseEvent, alumno);

            if (studentCourseEvent
                .getNota()
                .matches("^([4-9]|10|A-?)$")
            ) tpsRecuperados++;

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

            if (porcentajeTps <= courseEvaluationCriteria.getValue_to_promote())
                nota = "P";

            else if (porcentajeTps <= courseEvaluationCriteria.getValue_to_regulate())
                nota = "R";

            else nota = "L";

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

        // (B)
        int tpsAprobados = 0;
        int tpsTotales = 0;
        for (CourseEvent courseEvent : courseEventList.get()) {

            tpsTotales++;

            // (A)
            StudentCourseEvent studentCourseEvent
                = studentCourseEventRepository
                .findByEventoCursadaAndAlumno(courseEvent, alumno);

            if (studentCourseEvent
                .getNota()
                .matches("^([4-9]|10|A-?)$")
            ) tpsAprobados++;

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
        
        if (eventosAsistencias != 0) {
            float porcentajeAlumno = (float) presenciasAlumno / (float) eventosAsistencias * 100;

            if (porcentajeAlumno >= valorPromovido)
                return "P";
            else
                if (porcentajeAlumno >= valorRegular)
                    return "R";
                else
                    return "L";
        } else return null;
    }
    
}

