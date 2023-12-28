package com.example.helloworld.services;

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
import com.example.helloworld.models.Student;
import com.example.helloworld.models.Exceptions.EmptyQueryException;
import com.example.helloworld.repositories.CourseRepository;
import com.example.helloworld.repositories.StudentRepository;
import com.example.helloworld.repositories.CourseStudentRepository;
import com.example.helloworld.requests.NewDossiersCheckRequest;
import com.example.helloworld.requests.NewStudentRequest;
import com.example.helloworld.requests.NewStudentsRequest;
import com.example.helloworld.requests.CourseAndDossiersListRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Service
public class StudentService {

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
         *      surname: # <texto>
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
                String name,
                String surname
            ) {
                ok.add(
                    new Ok(
                        dossier,
                        id,
                        name,
                        surname
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
                private String surname;
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
                    student.getApellido()
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
         * 2.Guarda en notExistingDossiersList los legajos que no existen
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
            com.example.helloworld.requests.NewStudentRequest.NewStudentRegister student: 
            newStudentRequest.getStudents()
        ) {

            // Insertamos en la tabla alumno si no existe dicho alumno
            
            if (!studentRepository.findById(student.getLegajo()).isPresent()) {
                var newStudent = new Student();
                newStudent.setNombre(student.getNombre());
                newStudent.setApellido(student.getApellido());
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
    
    // Alta masiva de estudiantes.
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
            com.example.helloworld.requests.NewStudentsRequest.NewStudentRegister studentRegister: 
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
                newStudent.setApellido(studentRegister.getSurname());
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

