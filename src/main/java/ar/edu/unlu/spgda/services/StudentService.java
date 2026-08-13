package ar.edu.unlu.spgda.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ar.edu.unlu.spgda.config.ApplicationConfig;
import ar.edu.unlu.spgda.models.Course;
import ar.edu.unlu.spgda.models.CourseStudent;
import ar.edu.unlu.spgda.models.Student;
import ar.edu.unlu.spgda.models.Subject;
import ar.edu.unlu.spgda.models.Exceptions.EmptyQueryException;
import ar.edu.unlu.spgda.repositories.CourseRepository;
import ar.edu.unlu.spgda.repositories.CourseStudentRepository;
import ar.edu.unlu.spgda.repositories.StudentRepository;
import ar.edu.unlu.spgda.requests.CourseAndDossiersListRequest;
import ar.edu.unlu.spgda.requests.NewDossiersCheckRequest;
import ar.edu.unlu.spgda.requests.NewStudentRequest;
import ar.edu.unlu.spgda.requests.NewStudentsCheckRequest;
import ar.edu.unlu.spgda.requests.NewStudentsRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Service
public class StudentService {

    private final ApplicationConfig applicationConfig;

    StudentService(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    /**
     * Busca en la BD los legajos recibidos en {@code courseAndDossiersListRequest}
     * y los devuelve divididos en dos listas: los legajos que pueden ser registrados y
     * los que no.
     *
     * @param courseAndDossiersListRequest - Contiene el identificador de cursada y
     * la lista de legajos a consultar.
     * @return Un POJO con una lista de los legajos que pueden ser registrados y
     * los que no. En el caso de los primeros estarán acompañados del DNI, nombre y apellido
     * del estudiante, y en el caso de los segundos tendrán el motivo adjunto.
     */
    public Object checkInCourseStudentsRegistration(
        CourseAndDossiersListRequest courseAndDossiersListRequest
    ) throws EmptyQueryException {

        logger.debug(
            "Se ejecuta el método checkStudentsRegistration. [courseAndDossiersListRequest = %s]".formatted(
                courseAndDossiersListRequest.toString()
            )
        );

        /*
         * 1. Determina si el legajo existe o no, y si existe, guarda
         * el nombre, apellido y dni.
         * 
         * 1b. Determina de los legajos que existen, cuáles están registrados y cuáles no.
         * 
         * 2. Envía al back el siguiente JSON (expresado en YAML):
         * 
         *    ok:
         *    - dossier: # <numérico> - Legajo
         *      id: # <numérico> - DNI
         *      name: # <texto>
         *    # ...
         *    nok:
         *    - dossier: # <numérico> - Legajo
         *    - errorCode: # <numérico> - Número que representa la razón
         *                 # por la que no se puede registrar el legajo.
         *                 # Posibles valores:
         *                 # - 1: el legajo no existe en el sistema.
         *                 # - 2: el legajo ya está registrado en la cursada.
         *    # ...
         */

        // (1)
        var existingStudentsList = studentRepository.findByLegajoIn(courseAndDossiersListRequest
            .getDossierList()
        ).orElse(null);

        var existingStudentsDossierList = existingStudentsList
            .stream()
            .map(student -> student.getLegajo())
            .collect(Collectors.toList());
        var notExistentStudentsDossierList = courseAndDossiersListRequest
            .getDossierList()
            .stream()
            .filter(dossier ->
                !existingStudentsDossierList
                    .contains(dossier)
            )
            .collect(Collectors.toList());

        // (1b)
        var course = courseRepository
            .findById(courseAndDossiersListRequest.getCourseId())
            .orElseThrow(() -> 
                new EmptyQueryException(
                    "No se encontró la cursada con ID %s".formatted(
                        courseAndDossiersListRequest.getCourseId()
                    )
                )
            );

        // Calcula automáticamente cuáles alumnos son recursantes.
        List<Integer> dossiersRecursantes = getDossiersRecursantes(existingStudentsList, course);

        var registeredStudents_CourseStudentList = courseStudentRepository
            .findByAlumnoInAndCursada(
                existingStudentsList,
                course
            ).orElse(null);
        var registeredStudentsList = registeredStudents_CourseStudentList
            .stream()
            .map(courseStudent ->
                courseStudent.getAlumno()
            )
            .collect(Collectors.toList());
        var existingButNotRegisteredStudentsList = existingStudentsList;
        existingButNotRegisteredStudentsList = existingStudentsList
            .stream()
            .filter(existingStudent ->
                !registeredStudentsList
                    .contains(existingStudent)
            )
            .collect(Collectors.toList());

        // (2)
        @Data class Response {

            public void addOk(
                Integer dossier,
                Integer id,
                String name,
                Boolean isRecursante
            ) {
                ok.add(
                    new Ok(
                        dossier,
                        id,
                        name,
                        isRecursante
                    )
                );
            }

            public void addNotOk(
                Integer dossier,
                Integer errorCode
            ) {
                nok.add(
                    new NotOk(
                        dossier,
                        errorCode
                    )
                );
            }


            /* Private */

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            static class Ok {
                private Integer dossier;
                private Integer id;
                private String name;
                private Boolean isRecursante;
            }

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            static class NotOk {
                private Integer dossier;
                private Integer errorCode;
            }

            private List<Ok> ok = new ArrayList<Ok>();
            private List<NotOk> nok = new ArrayList<NotOk>();

        }
        var response = new Response();
        existingButNotRegisteredStudentsList
            .forEach(student ->
                response.addOk(
                    student.getLegajo(),
                    student.getDni(),
                    student.getNombre(),
                    dossiersRecursantes.contains(student.getLegajo())
                )
            );
        for (int index = 0; index < notExistentStudentsDossierList.size(); index++) {
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
        }
        return response;

    }

    public Object checkNewDossiersRegistration(NewDossiersCheckRequest newDossiersCheckRequest) {

        /*
         * 1.Obtiene de la tabla 'alumno' los registros que coincidan
         * con los legajos en 'newDossiersCheckRequest' y los guarda
         * en la lista 'existingDossiersList'.
         *
         * 2.Guarda en nonExistingDossiersList los legajos que no existen
         * en sistema; es decir, los legajos de 'newDossiersCheckRequest'
         * que no estén en 'existingDossiersList'.
         *
         * 3.Devuelve un objeto con la siguiente estructura:
         *
         *      ok:
         *      - # <numérico> - Legajo
         *      # ...
         *      nok:
         *      - dossier: # <numérico> - Legajo
         *        errorCode: # <numérico> - Número que representa la razón
         *                   # por la que no se puede registrar el legajo.
         *                   # Posibles valores:
         *                   # - 1: el legajo ya existe en el sistema.
         *      # ...
         */

         @Data class Response {

            public void addOk(Integer dossier) {
                ok.add(dossier);
            }

            public void addNotOk(
                Integer dossier,
                Integer errorCode
            ) {
                nok.add(
                    new NotOk(
                        dossier,
                        errorCode
                    )
                );
            }


            /* Private */

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            static class NotOk {
                private Integer dossier;
                private Integer errorCode;
            }

            private List<Integer> ok = new ArrayList<Integer>();
            private List<NotOk> nok = new ArrayList<NotOk>();

        }

        // (1)
        List<Student> existingStudentsList = studentRepository
            .findByLegajoIn(newDossiersCheckRequest.getDossiersList())
            .orElse(null); 
        List<Integer> existingDossiersList = existingStudentsList
            .stream()
            .map(student ->
                student.getLegajo()
            )
            .collect(Collectors.toList());

        // (2)
        List<Integer> nonExistentDossiersList = newDossiersCheckRequest
            .getDossiersList()
            .stream()
            .filter(dossier -> 
                !existingDossiersList.contains(dossier)
            )
            .collect(Collectors.toList());

        // (3)
        var response = new Response();
        nonExistentDossiersList
            .stream()
            .forEach(dossier ->
                response.addOk(dossier)
            );
        existingDossiersList
            .stream()
            .forEach(dossier ->
                response.addNotOk(
                    dossier,
                    1
                )
            );
        return response;

    }

    /**
     * Verifica cuáles estudiantes pueden registrarse y cuáles no.
     * 
     * Verifica cada estudiante para saber si no se puede registrar en sistema
     * y/o vincular con la cursada, revisando:
     * a) cuáles estudiantes todavía no registrados en sistema tienen un DNI o mail
     * que ya existe en algún registro de otro estudiante,
     * b) y cuáles estudiantes que ya existen en sistema están vinculados con la cursada.
     * 
     * También verifica cuáles estudiantes pueden registrarse en sistema y/o vincularse
     * con la cursada, confirmando:
     * a) si el estudiante ya está registrado en sistema, pero todavía no está vinculado
     * con la comisión,
     * b) si el estudiante no está registrado en el sistema y si su DNI y email tampoco
     * existen en ningún registro de otro estudiante.
     * 
     * Precondiciones: no se recibirán legajos duplicados.
     * 
     * @return Un objeto que contiene: (a) un arreglo de los estudiantes que pueden registrarse
     * en sistema y vincularse con la cursada; (b) un arreglo de los estudiantes que pueden
     * vincularse con la cursada, porque ya están registrados en sistema; (c) un arreglo
     * con los estudiantes que no pueden registrarse de ninguna forma, junto con el código que
     * identifica la razón: (1) el legajo ya existe en sistema, pero está vinculado con la cursada;
     * (2) el estudiante no existe todavía en el sistema, pero el DNI ya existe en el registro de
     * otro alumno; (4) el estudiante no existe todavía en el sistema, pero el email ya existe en
     * el registro de otro alumno.
     */
    public Object checkNewStudentsRegistration(NewStudentsCheckRequest newStudentsCheckRequest)
    throws EmptyQueryException {
        // Definición de la clase para el objeto que se devuelve.
        @Data class Response {

            public void addNonExistingDossiers(Integer dossier) {
                nonExistingDossiers.add(dossier);
            }

            public void addExistingStudents(
                Integer dossier, Integer id, String name, String email, Boolean isRecursante
            ) {
                existingStudents.add(new ExistingStudents(dossier, id, name, email, isRecursante));
            }

            //MODIFICADO: Método para agregar con todos los datos
            public void addNotOk(
                Integer dossier, Integer errorCode, 
                Boolean oldAlreadyStudied, Boolean oldAllPreviousSubjectsApproved,
                String oldName, Integer oldDni, String oldEmail
            ) {
                nok.add(new NotOk(
                    dossier, errorCode, 
                    oldAlreadyStudied, oldAllPreviousSubjectsApproved, 
                    oldName, oldDni, oldEmail
                ));
            }

            // Mantenemos este para los errores comunes (ej: error 2 o 4)
            public void addNotOk(Integer dossier, Integer errorCode) {
                nok.add(new NotOk(dossier, errorCode, null, null, null, null, null));
            }

            /* Private */

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            static class ExistingStudents {
                private Integer dossier;
                private Integer id;
                private String name;
                private String email;
                private Boolean isRecursante;
            }

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            static class NotOk {
                private Integer dossier;
                private Integer errorCode;
                // NUEVO: Propiedades para mostrar en el Frontend al sobreescribir.
                private Boolean oldAlreadyStudied;
                private Boolean oldAllPreviousSubjectsApproved;
                private String oldName;
                private Integer oldDni;
                private String oldEmail;
            }

            private List<Integer> nonExistingDossiers = new ArrayList<Integer>();
            private List<ExistingStudents> existingStudents = new ArrayList<ExistingStudents>();
            private List<NotOk> nok = new ArrayList<NotOk>();

        }

        // Obtiene la lista de legajos recibidos.
        List<Integer> dossiersList = newStudentsCheckRequest.getStudentsList().stream()
            .map(NewStudentsCheckRequest.Student::getDossier)
            .collect(Collectors.toList());

        // Obtiene la lista de estudiantes registrados en sistema.
        List<Student> existingStudentsList = studentRepository.findByLegajoIn(dossiersList).orElse(null);

        // Obtiene la lista de legajos registrados en sistema.
        List<Integer> existingDossiersList = existingStudentsList.stream()
            .map(Student::getLegajo).collect(Collectors.toList());

        // Obtiene la cursada.
        Course course = courseRepository.findById(newStudentsCheckRequest.getCourseId())
            .orElseThrow(() -> new EmptyQueryException("No se encontró la cursada."));
        
        // Calcula automáticamente cuáles alumnos son recursantes.
        List<Integer> dossiersRecursantes = getDossiersRecursantes(existingStudentsList, course);
        
        // MODIFICADO: Necesitamos los objetos CourseStudent enteros para sacar isRecursante y isPreviousSubjectsApproved.
        List<CourseStudent> courseStudentsAlreadyRegistered = courseStudentRepository
            .findByAlumnoInAndCursada(existingStudentsList, course)
            .orElse(new ArrayList<>());

        List<Integer> dossiersListOfAlreadyRegisteredInCourse = courseStudentsAlreadyRegistered.stream()
            .map(cs -> cs.getAlumno().getLegajo())
            .collect(Collectors.toList());

        // Obtiene los legajos registrados en sistema, pero que no están en la cursada.
        List<Integer> dossiersListOfNotRegisteredInCourse = new ArrayList<>(existingDossiersList);
        dossiersListOfNotRegisteredInCourse.removeAll(dossiersListOfAlreadyRegisteredInCourse);

        // Obtiene los estudiantes registrados en sistema, pero que no están en la cursada.
        List<Student> studentListOfNotRegisteredInCourse = studentRepository
            .findByLegajoIn(dossiersListOfNotRegisteredInCourse).orElse(null);

        // Obtiene los legajos que no están registrados en el sistema.
        List<Integer> nonExistentDossiersList = dossiersList.stream()
            .filter(dossier -> !existingDossiersList.contains(dossier)).collect(Collectors.toList());

        List<NewStudentsCheckRequest.Student> receivedStudentsNotRegisteredInSystem = newStudentsCheckRequest
            .getStudentsList().stream()
            .filter(receivedStudent -> nonExistentDossiersList.contains(receivedStudent.getDossier()))
            .collect(Collectors.toList());

        // Validaciones de DNI e Email duplicados (se mantiene igual)
        List<Integer> receivedDossiersNotRegisteredInSystemWithExistingID = new ArrayList<Integer>();
        receivedStudentsNotRegisteredInSystem.forEach(receivedStudent -> {
            if(studentRepository.existsByDni(receivedStudent.getId()))
                receivedDossiersNotRegisteredInSystemWithExistingID.add(receivedStudent.getDossier());
        });

        List<Integer> receivedDossiersNotRegisteredInSystemWithExistingEmail = new ArrayList<Integer>();
        receivedStudentsNotRegisteredInSystem.forEach(receivedStudent -> {
            if(studentRepository.existsByEmail(receivedStudent.getEmail()))
                receivedDossiersNotRegisteredInSystemWithExistingEmail.add(receivedStudent.getDossier());
        });

        receivedDossiersNotRegisteredInSystemWithExistingEmail.removeAll(receivedDossiersNotRegisteredInSystemWithExistingID);

        List<Integer> nonExistentDossiersListWithIdAndEmailNotDuplicated = new ArrayList<>(nonExistentDossiersList);
        nonExistentDossiersListWithIdAndEmailNotDuplicated.removeAll(receivedDossiersNotRegisteredInSystemWithExistingID);
        nonExistentDossiersListWithIdAndEmailNotDuplicated.removeAll(receivedDossiersNotRegisteredInSystemWithExistingEmail);

        var response = new Response();

        nonExistentDossiersListWithIdAndEmailNotDuplicated.forEach(response::addNonExistingDossiers);

        studentListOfNotRegisteredInCourse.forEach(student ->
            response.addExistingStudents(
                student.getLegajo(), student.getDni(), student.getNombre(), student.getEmail(),
                dossiersRecursantes.contains(student.getLegajo())
            )
        );

        // MODIFICADO: Agrega los que YA están en la cursada (errorCode = 1), incluyendo sus datos viejos.
        courseStudentsAlreadyRegistered.forEach(cs ->
            response.addNotOk(
                cs.getAlumno().getLegajo(),
                1, 
                cs.isRecursante(), 
                cs.isPreviousSubjectsApproved(),
                cs.getAlumno().getNombre(),
                cs.getAlumno().getDni(),
                cs.getAlumno().getEmail()
            )
        );

        receivedDossiersNotRegisteredInSystemWithExistingID.forEach(dossier -> response.addNotOk(dossier, 2));
        receivedDossiersNotRegisteredInSystemWithExistingEmail.forEach(dossier -> response.addNotOk(dossier, 4));
        
        return response;
    }

    // Alta masiva de estudiantes.
    public ResponseEntity<String> create (
        NewStudentRequest newStudentRequest
    ) {

        logger.debug(
            String.format(
                "Se ejecuta el método create. [newStudentRequest = %s]",
                newStudentRequest.toString()
            )
        );

        for (
            ar.edu.unlu.spgda.requests.NewStudentRequest.NewStudentRegister student: 
            newStudentRequest.getStudents()
        ) {

            // Insertamos en la tabla alumno si no existe dicho alumno
            
            if (!studentRepository.findById(student.getLegajo()).isPresent()) {
                var newStudent = new Student();
                newStudent.setNombre(student.getNombre());
                newStudent.setDni(student.getDni());
                newStudent.setLegajo(student.getLegajo());
                newStudent.setEmail(student.getEmail());
                newStudent = studentRepository.save(newStudent);
            }

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
     * Devuelve la lista de legajos de estudiantes existentes de una lista de legajos.
     *
     * @param dossiersList La lista de legajos que se consultarán por su existencia.
     * @return Una lista de legajos que existen en el sistema.
     */
    public List<Integer> getExistingDossiersFromDossiersList(List<Integer> dossierList) {

        /*
         * 1. Consultar al repositorio de StudentRepository para obtener los
         * estudiantes que existen en sistema (método findByLegajoIn), y no devolver nada 
         * en caso de que no exista (método orElse(null)).
         */

        // (1)
        List<Student> studentsList = studentRepository
            .findByLegajoIn(dossierList)
            .orElse(null);

        List<Integer> dossiersList = studentsList
            .stream()
            .map(student -> student.getLegajo())
            .collect(Collectors.toList());

        return dossiersList;

    }

    /**
     * Devuelve la lista de estudiantes existentes de una lista de legajos.
     *
     * @param dossiersList La lista de legajos que se consultarán por su existencia.
     * @return Una lista de estudiantes que existen en el sistema.
     */
    public List<Student> getExistingStudentsFromDossiersList(List<Integer> dossierList) {

        /*
         * 1. Consultar al repositorio de StudentRepository para obtener los
         * estudiantes que existen en sistema (método findByLegajoIn), y no devolver nada 
         * en caso de que no exista (método orElse(null)).
         */

        // (1)
        return studentRepository
            .findByLegajoIn(dossierList)
            .orElse(null);

    }
    
    /**
     * Alta masiva solo de estudiantes no registrados.
     * 
     * Si el estudiante no está registrado en sistema, lo registra; mientras
     * que, si lo está, no hace nada.
     * 
     * @param newStudentsRequest - El ID de cursada y la lista de estudiantes
     * que se quiere registrar en sistema.
     */
    public void registerOnlyNonExistingStudents (
        NewStudentsRequest newStudentsRequest
    ) {

        logger.debug(
            String.format(
                "Se ejecuta el método registerOnlyNonExistingStudents. [newStudentsRequest = %s]",
                newStudentsRequest.toString()
            )
        );

        // Construye el arreglo de estudiantes que se registrarán en sistema.
        var newStudentsList = new ArrayList<Student>();
        for (
            ar.edu.unlu.spgda.requests.NewStudentsRequest.NewStudentRegister studentRegister: 
            newStudentsRequest.getNewStudentsList()
        ) {

            // Selecciona los estudiantes que no existan.
            if (
                !studentRepository
                    .findById(studentRegister.getDossier())
                    .isPresent()
            ) {

                // Agrega el estudiante al arreglo de nuevos registros.
                var newStudent = new Student();
                newStudent.setLegajo(studentRegister.getDossier());
                newStudent.setDni(studentRegister.getId());
                newStudent.setNombre(studentRegister.getName());
                newStudent.setEmail(studentRegister.getEmail());
                newStudentsList.add(newStudent);

            }
        }

        // Registra los estudiantes en el sistema.
        studentRepository.saveAll(newStudentsList);
            
    }

    /**
     * Alta masiva de estudiantes.
     * 
     * Si el estudiante ya está registrado en sistema, devuelve el error 1;
     * si no, lo registra y lo vincula a la cursada pasada por parámetro.
     * 
     * @param newStudentsRequest - El ID de cursada y la lista de estudiantes
     * que se quiere registrar.
     * @return La lista de legajos que fueron registrados correctamente,
     * y la lista de objetos que no pudieron ser registrados junto con el
     * código de error.
     */
    public Object registerStudents (
        NewStudentsRequest newStudentsRequest
    ) {

        // Genera el objeto de la respuesta.
        @Data class Response {

            public void addOk(Integer dossier) {
                ok.add(dossier);
            }

            public void addNotOk(
                Integer dossier,
                Integer errorCode
            ) {
                nok.add(
                    new NotOk(
                        dossier,
                        errorCode
                    )
                );
            }


            /* Private */

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            static class NotOk {
                private Integer dossier;
                private Integer errorCode;
            }

            private List<Integer> ok = new ArrayList<Integer>();
            private List<NotOk> nok = new ArrayList<NotOk>();

        }
        var response = new Response();

        logger.debug(
            String.format(
                "Se ejecuta el método registerStudents. [newStudentsRequest = %s]",
                newStudentsRequest.toString()
            )
        );

        // Construye el arreglo de objetos Student que se registrarán en sistema.
        var newStudentsList = new ArrayList<Student>();
        for (
            ar.edu.unlu.spgda.requests.NewStudentsRequest.NewStudentRegister studentRegister: 
            newStudentsRequest.getNewStudentsList()
        ) {
            if (
                studentRepository
                    .findById(studentRegister.getDossier())
                    .isPresent()
            ) {
                response.addNotOk(studentRegister.getDossier(), 1);
            } else {

                // Agrega el estudiante al arreglo de nuevos registros.
                var newStudent = new Student();
                newStudent.setLegajo(studentRegister.getDossier());
                newStudent.setDni(studentRegister.getId());
                newStudent.setNombre(studentRegister.getName());
                newStudent.setEmail(studentRegister.getEmail());
                newStudentsList.add(newStudent);

                // Agrega el legajo a la respuesta.
                response.addOk(studentRegister.getDossier());

            }
        }

        // Registra los estudiantes en el sistema.
        studentRepository.saveAll(newStudentsList);

        // Genera y devuelve la respuesta.
        return response;
            
    }


    /* Private */

    private static final Logger logger = LoggerFactory.getLogger(CourseEventService.class);
    @Autowired private CourseRepository courseRepository;
    @Autowired private CourseStudentRepository courseStudentRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private ar.edu.unlu.spgda.repositories.CommissionRepository commissionRepository;

    private List<Integer> getDossiersRecursantes(List<Student> alumnos, Course cursadaActual) {
        if (alumnos == null || alumnos.isEmpty()) return List.of();
        
        Subject asignatura = cursadaActual.getComision().getAsignatura();
        List<ar.edu.unlu.spgda.models.Comission> comisiones = commissionRepository.findByAsignatura(asignatura).orElse(List.of());
        
        List<Course> cursosAsignatura = new ArrayList<>();
        for (ar.edu.unlu.spgda.models.Comission c : comisiones) {
            cursosAsignatura.addAll(courseRepository.findByComision(c).orElse(List.of()));
        }
        cursosAsignatura.removeIf(c -> c.getId() == cursadaActual.getId());
        
        if (cursosAsignatura.isEmpty()) return List.of();
        
        List<CourseStudent> previos = courseStudentRepository.findByAlumnoInAndCursadaIn(alumnos, cursosAsignatura).orElse(List.of());
        return previos.stream()
            .map(cs -> cs.getAlumno().getLegajo())
            .distinct()
            .collect(Collectors.toList());
    }

}

