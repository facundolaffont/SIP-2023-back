package ar.edu.unlu.spgda.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;

import ar.edu.unlu.spgda.models.Auth0Handler;
import ar.edu.unlu.spgda.models.CourseProfessor;
import ar.edu.unlu.spgda.models.Userr;
import ar.edu.unlu.spgda.models.Exceptions.ExternalServiceException;
import ar.edu.unlu.spgda.models.Exceptions.HasDependenciesException;
import ar.edu.unlu.spgda.models.Exceptions.ResourceNotFoundException;
import ar.edu.unlu.spgda.repositories.CourseProfessorRepository;
import ar.edu.unlu.spgda.repositories.UserRepository;
import ar.edu.unlu.spgda.requests.NewProfessorRequest;
import ar.edu.unlu.spgda.requests.NewUserRequest;
import ar.edu.unlu.spgda.requests.UpdateProfessorRequest;
import ar.edu.unlu.spgda.responses.UserResponse;
import io.github.cdimascio.dotenv.Dotenv;

@Service
public class ProfessorService {

    private final UserRepository userRepository;
    
    public ProfessorService(UserRepository userRepository) {
        dotenv = Dotenv.load();
        this.userRepository = userRepository;
    }

    // Crea un docente y lo guarda en la BD.
    // VIEJO, deberia actualizarse para la creación de usuarios (No docentes)
    public Userr create(NewUserRequest newUserRequest) throws Exception {
        Objects.requireNonNull(newUserRequest, "NewUserRequest must not be null");
    
        logger.debug(String.format("Se ejecuta el método create. [newUserRequest = %s]", newUserRequest));
    
        String email = newUserRequest.getEmail();
        String first_name = newUserRequest.getNombre();
        String password = newUserRequest.getPassword();
        Integer legajo = newUserRequest.getLegajo();
        String role = newUserRequest.getRol();
    
        try {
            // Configura los datos del usuario que se quiere crear en Auth0.
            User newUser = new User(dotenv.get("AUTH0_DB_CONNECTION"));
            newUser.setEmail(email);
            newUser.setName(first_name);
            newUser.setPassword(password.toCharArray());
    
            // Obtiene token Auth0.
            Auth0Handler.getInstance().createProfessor(newUser);
    
            // Get the ID from Auth0
            String auth0UserId = Auth0Handler.getInstance().getUserIdByEmail(email);
    
            // Creamos objeto usuario y lo guardamos en repositorio JPA (usamos id de Auth0).
            Userr user = new Userr();
            user.setId(auth0UserId);
            user.setEmail(email);
            user.setNombre(first_name);
            user.setLegajo(legajo);
            user.setRol(role);
    
            userRepository.save(user);
    
            // Todo salió OK; se devuelve el docente creado.
            return user;
        } catch (NullPointerException | IllegalArgumentException | Auth0Exception ex) {
            logger.error("Error creating user: " + ex.getMessage(), ex);
            throw new Exception("Error creating user: " + ex.getMessage());
        }
    }
    
    @Transactional
    // Crea un docente 
    public UserResponse createProfessor(NewProfessorRequest newProfessorRequest) throws Exception {
        Objects.requireNonNull(newProfessorRequest, "NewProfessorRequest must not be null");
    
        logger.debug(String.format("Se ejecuta el método create. [newProfessorRequest = %s]", newProfessorRequest));
        
        String email = newProfessorRequest.getEmail();
        String first_name = newProfessorRequest.getNombre();
        String last_name = newProfessorRequest.getApellido();
        String password = newProfessorRequest.getPassword();
        Integer legajo = newProfessorRequest.getLegajo();
        String role = newProfessorRequest.getRol();
    
        try {
            // Configura los datos del usuario que se quiere crear en Auth0.
            User newUser = new User(dotenv.get("AUTH0_DB_CONNECTION"));
            newUser.setEmail(email);
            String name = first_name + " " + last_name;
            newUser.setName(name);
            newUser.setPassword(password.toCharArray());
    
            // Obtiene id de Auth0
            String auth0UserId = Auth0Handler.getInstance().createProfessor(newUser);
        
            logger.debug(String.format("=== DEBUG: El ID de Auth0 es [newUser = %s] ===", auth0UserId));

            // Creamos objeto usuario y lo guardamos en repositorio JPA (usamos id de Auth0).
            Userr user = new Userr();
            user.setId(auth0UserId);
            user.setEmail(email);
            user.setNombre(first_name);
            user.setApellido(last_name);
            user.setLegajo(legajo);
            user.setRol(role);
    
            Userr savedUser = userRepository.save(user);
            return UserResponse.fromEntity(savedUser);
        } catch (NullPointerException | IllegalArgumentException | Auth0Exception ex) {
            logger.error("Error creating user: " + ex.getMessage(), ex);
            throw new Exception("Error creating user: " + ex.getMessage());
        }
    }

    public List<Userr> getAllProfessors() {
        return userRepository.findByRol("docente");
    }

    @Transactional
    public Userr updateProfessor(UpdateProfessorRequest request) throws Exception {
        // 1. Validar que exista el usuario a modificar y que sea profesor
        Userr professor = userRepository.findById(request.getId()) // CAmbiar para que busque rol docente
                .orElseThrow(() -> new Exception("Profesor no encontrado"));
        
        try {
            // 2. Preparamos el objeto para Auth0 SOLO con los datos a pisar
            User auth0UpdateData = new User();
            auth0UpdateData.setEmail(request.getEmail());
            auth0UpdateData.setName(request.getNombre() + " " + request.getApellido());
            // 3. Impactamos los cambios en Auth0 PRIMERO
            Auth0Handler.getInstance().updateProfessor(professor.getId(), auth0UpdateData);
        } catch (Auth0Exception ex) {
            logger.error("Error actualizando usuario en Auth0: " + ex.getMessage(), ex);
            throw new Exception("Error al sincronizar datos con el proveedor de identidad (Auth0).");
        }

        // 4. Actualizamos los datos simples si Auth0 no falló
        professor.setNombre(request.getNombre());
        professor.setApellido(request.getApellido());
        professor.setEmail(request.getEmail());
        professor.setLegajo(request.getLegajo());

        // 4. Guardamos los cambios
        Userr updatedProfessor = userRepository.save(professor);
        logger.debug("Profesor actualizado exitosamente en BD local.");

        return updatedProfessor;
    } 

    @Transactional
    public void deleteProfessor(String id) throws Exception {
        // 1. Validar que exista el usuario
        Userr professor = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("El profesor con id " + id + " no existe"));
        
        // 2. Validar que no tenga dependencias (cursadas)
        Optional<List<CourseProfessor>> coursesProfessorOptional = courseProfessorRepository.findByIdDocente(professor);
        if (coursesProfessorOptional.isPresent() && !coursesProfessorOptional.get().isEmpty()) {
            throw new HasDependenciesException("No se puede eliminar el profesor porque está asociado a una o más cursadas");
        }

        // 3. Eliminamos de la BD primero
        try {
            userRepository.delete(professor);
            // IMPORTANTE: flush() fuerza a que Hibernate envíe el comando DELETE a la BD 
            // en este momento exacto. Si la BD va a fallar, fallará aquí mismo.
            userRepository.flush();
            logger.debug("Profesor eliminado exitosamente de BD");
        } catch (Exception ex) {
            logger.error("Error al eliminar en la BD local: " + ex.getMessage(), ex);
            throw new RuntimeException("Error interno en la base de datos al intentar eliminar el registro.");
        }

        // 4. Si la BD local no falló, eliminamos de Auth0
        try {
            Auth0Handler.getInstance().deleteProfessor(id);
            logger.debug("Profesor eliminado exitosamente de Auth0.");
        } catch (Auth0Exception ex) {
            logger.error("Error eliminando usuario en Auth0: " + ex.getMessage(), ex);
            // Al lanzar esta excepción, el @Transactional de Spring entra en acción
            // y hace un ROLLBACK automático del userRepository.delete() que se hizo antes
            throw new ExternalServiceException("Error al sincronizar datos con Auth0. No se eliminó el docente.");
        }
    }

    

    /* Private */

    private static final Logger logger = LoggerFactory.getLogger(CourseEventService.class);
    Dotenv dotenv;
    @Autowired private CourseProfessorRepository courseProfessorRepository;
}
