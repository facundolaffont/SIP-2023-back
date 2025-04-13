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
import ar.edu.unlu.spgda.models.Exceptions.EmptyQueryException;
import ar.edu.unlu.spgda.repositories.CourseRepository;
import ar.edu.unlu.spgda.repositories.StudentRepository;
import ar.edu.unlu.spgda.repositories.CourseStudentRepository;
import ar.edu.unlu.spgda.requests.NewDossiersCheckRequest;
import ar.edu.unlu.spgda.requests.NewStudentRequest;
import ar.edu.unlu.spgda.requests.NewStudentsCheckRequest;
import ar.edu.unlu.spgda.requests.NewStudentsRequest;
import ar.edu.unlu.spgda.requests.CourseAndDossiersListRequest;

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
                String name
            ) {
                ok.add(
                    new Ok(
                        dossier,
                        id,
                        name
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
                    student.getNombre()
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

        /*
         * 1. Obtiene de la tabla 'alumno' los registros que coincidan
         * con los legajos en 'newStudentsCheckRequest' y los guarda
         * en la lista 'existingDossiersList'.
         * 
         * 1b. Separa en dos arreglos los registros de estudiantes que ya
         * están registrados en el sistema, de forma tal que, por un lado,
         * quedan los registros de estudiantes que no están vinculados a
         * la cursada en cuestión, y, por otro lado, quedan los que sí lo están.
         *
         * 2. Guarda en nonExistingDossiersList los legajos que no existen
         * en sistema; es decir, los legajos de 'newStudentsCheckRequest'
         * que no estén en 'existingDossiersList'.
         * 
         * 3. Obtiene de la tabla 'alumno' los registros que coincidan con
         * los mails en 'nonExistingDossiersList' y los guarda en la lista
         * 'dossiersListOfStudentsWithExistingMail'
         * 
         * 3b. Guarda en 'nonExistingDossiersList' los legajos que no están
         * en 'existingDossiersList' ni en 'dossiersListOfStudentsWithExistingMail'.
         *
         * 4. Devuelve un objeto con la siguiente estructura:
         *
         *      nonExistingDossiers:
         *      - # <numérico> - Legajo
         *      # ...
         *      existingStudents:
         *      - dossier: # <numérico>
         *        id: # <numérico>
         *        name: # <string>
         *        email: # <string>
         *      # ...
         *      nok:
         *      - dossier: # <numérico>
         *        errorCode: # <numérico> - Número que representa la razón
         *                   # por la que no se puede registrar el legajo.
         *                   # Posibles valores:
         *                   # - 1: el legajo ya existe en sistema, pero
         *                   # está vinculado con la cursada.
         *                   # - 2: el estudiante no existe todavía en el
         *                   # sistema, pero el DNI ya existe en el
         *                   # registro de otro alumno.
         *                   # - 4: el estudiante no existe todavía en el
         *                   # sistema, pero el email ya existe en el registro
         *                   # de otro alumno.
         *      # ...
         */

        // Definición de la clase para el objeto que se devuelve.
        @Data class Response {

            public void addNonExistingDossiers(Integer dossier) {
                nonExistingDossiers.add(dossier);
            }

            public void addExistingStudents(
                Integer dossier,
                Integer id,
                String name,
                String email
            ) {
                existingStudents.add(
                    new ExistingStudents(
                        dossier,
                        id,
                        name,
                        email
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
            static class ExistingStudents {
                private Integer dossier;
                private Integer id;
                private String name;
                private String email;
            }

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            static class NotOk {
                private Integer dossier;
                private Integer errorCode;
            }

            private List<Integer> nonExistingDossiers = new ArrayList<Integer>();
            private List<ExistingStudents> existingStudents = new ArrayList<ExistingStudents>();
            private List<NotOk> nok = new ArrayList<NotOk>();

        }

        // Obtiene la lista de legajos recibidos.
        List<Integer> dossiersList = newStudentsCheckRequest
            .getStudentsList()
            .stream()
            .map(NewStudentsCheckRequest.Student::getDossier)
            .collect(Collectors.toList());

        // Obtiene la lista de estudiantes que están registrados en el
        // sistema.
        List<Student> existingStudentsList = studentRepository
            .findByLegajoIn(dossiersList)
            .orElse(null);

        // Obtiene la lista de legajos que están registrados en el sistema.
        List<Integer> existingDossiersList = existingStudentsList
            .stream()
            .map(Student::getLegajo)
            .collect(Collectors.toList());

        // Obtiene la cursada.
        Course course = courseRepository
            .findById(newStudentsCheckRequest.getCourseId())
            .orElseThrow(() -> 
                new EmptyQueryException(
                    "No se encontró la cursada con ID %s".formatted(
                        newStudentsCheckRequest.getCourseId()
                    )
                )
            );
        
        // Obtiene la lista de estudiantes, registrados en el sistema, que también están
        // vinculados con la cursada.
        List<Student> studentsListOfAlreadyRegisteredInCourse = courseStudentRepository
            .findByAlumnoInAndCursada(existingStudentsList, course)
            .map(courseStudent -> courseStudent
                .stream()
                .map(element -> element.getAlumno())
                .collect(Collectors.toList())
            )
            .orElse(null);

        // Obtiene la lista de legajos, registrados en el sistema, que también están
        // vinculados con la cursada.
        List<Integer> dossiersListOfAlreadyRegisteredInCourse = studentsListOfAlreadyRegisteredInCourse
            .stream()
            .map(Student::getLegajo)
            .collect(Collectors.toList());

        // Obtiene los legajos que están registrados en sistema, pero que no están registrados en la cursada.
        List<Integer> dossiersListOfNotRegisteredInCourse = new ArrayList<>(existingDossiersList);
        dossiersListOfNotRegisteredInCourse.removeAll(dossiersListOfAlreadyRegisteredInCourse);

        // Obtiene los estudiantes que están registrados en sistema, pero que no están
        // registrados en la cursada.
        List<Student> studentListOfNotRegisteredInCourse = studentRepository
            .findByLegajoIn(dossiersListOfNotRegisteredInCourse)
            .orElse(null);

        // Obtiene los legajos que no están registrados en el sistema.
        List<Integer> nonExistentDossiersList = dossiersList
            .stream()
            .filter(dossier -> 
                !existingDossiersList.contains(dossier)
            )
            .collect(Collectors.toList());

        // Obtiene los registros recibidos de estudiantes que no estén registrados
        // en el sistema.
        List<NewStudentsCheckRequest.Student> receivedStudentsNotRegisteredInSystem = newStudentsCheckRequest
            .getStudentsList()
            .stream()
            .filter(receivedStudent -> nonExistentDossiersList.contains(receivedStudent.getDossier()))
            .collect(Collectors.toList());

        // Obtiene los legajos recibidos cuyo DNI ya existe
        // en el registro de otro alumno.
        List<Integer> receivedDossiersNotRegisteredInSystemWithExistingID = new ArrayList<Integer>();
        receivedStudentsNotRegisteredInSystem
            .forEach(receivedStudentNotRegisteredInSystem -> {
                if(studentRepository.existsByDni(receivedStudentNotRegisteredInSystem.getId()))
                    receivedDossiersNotRegisteredInSystemWithExistingID.add(receivedStudentNotRegisteredInSystem.getDossier());
        });

        // Obtiene los legajos recibidos cuyo email ya existe
        // en el registro de otro alumno.
        List<Integer> receivedDossiersNotRegisteredInSystemWithExistingEmail = new ArrayList<Integer>();
        receivedStudentsNotRegisteredInSystem
            .forEach(receivedStudentNotRegisteredInSystem -> {
                if(studentRepository.existsByEmail(receivedStudentNotRegisteredInSystem.getEmail()))
                    receivedDossiersNotRegisteredInSystemWithExistingEmail.add(receivedStudentNotRegisteredInSystem.getDossier());
        });

        // Si los legajos tienen duplicados tanto su DNI como su email, se prioriza notificar el primero,
        // ya que este método no devuelve legajos duplicados.
        receivedDossiersNotRegisteredInSystemWithExistingEmail.removeAll(receivedDossiersNotRegisteredInSystemWithExistingID);

        // Obtiene los legajos que no están registrados en sistema y que no tienen DNI ni mail duplicados.
        List<Integer> nonExistentDossiersListWithIdAndEmailNotDuplicated = new ArrayList<>(nonExistentDossiersList);
        nonExistentDossiersListWithIdAndEmailNotDuplicated.removeAll(receivedDossiersNotRegisteredInSystemWithExistingID);
        nonExistentDossiersListWithIdAndEmailNotDuplicated.removeAll(receivedDossiersNotRegisteredInSystemWithExistingEmail);

        // Crea el objeto que alojará la respuesta que será devuelta.
        var response = new Response();

        // Agrega a la respuesta los legajos que no existen en sistema.
        nonExistentDossiersListWithIdAndEmailNotDuplicated
            .stream()
            .forEach(dossier ->
                response.addNonExistingDossiers(dossier)
            );

        // Agrega a la respuesta la información de los estudiantes que
        // están registrados en el sistema, pero que no están vinculados
        // con la cursada.
        studentListOfNotRegisteredInCourse
            .stream()
            .forEach(courseStudent ->
                response.addExistingStudents(
                    courseStudent.getLegajo(),
                    courseStudent.getDni(),
                    courseStudent.getNombre(),
                    courseStudent.getEmail()
                )
            );

        // Agrega a la respuesta la información de los estudiantes que
        // existen en sistema y que también están vinculados con la cursada.
        dossiersListOfAlreadyRegisteredInCourse
            .stream()
            .forEach(dossier ->
                response.addNotOk(
                    dossier,
                    1
                )
            );

        // Agrega a la respuesta la información de los estudiantes que no
        // existen en sistema, pero cuyo DNI o email ya existen en el registro
        // de otro alumno.
        receivedDossiersNotRegisteredInSystemWithExistingID
            .stream()
            .forEach(dossier ->
                response.addNotOk(
                    dossier,
                    2
                )
            );
            receivedDossiersNotRegisteredInSystemWithExistingEmail
            .stream()
            .forEach(dossier ->
                response.addNotOk(
                    dossier,
                    4
                )
            );
        
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
    
}

