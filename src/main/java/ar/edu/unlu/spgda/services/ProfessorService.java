package ar.edu.unlu.spgda.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;
import ar.edu.unlu.spgda.models.Auth0Handler;
import ar.edu.unlu.spgda.models.Userr;

import java.util.Objects;

import ar.edu.unlu.spgda.models.Exceptions.NotValidAttributeException;
import ar.edu.unlu.spgda.models.Exceptions.NullAttributeException;
import ar.edu.unlu.spgda.repositories.UserRepository;
import ar.edu.unlu.spgda.requests.NewUserRequest;
import io.github.cdimascio.dotenv.Dotenv;

@Service
public class ProfessorService {

    private final UserRepository userRepository;
    
    public ProfessorService(UserRepository userRepository) {
        
        dotenv = Dotenv.load();
        this.userRepository = userRepository;

    }

    // Crea un docente y lo guarda en la BD.
    public Userr create(NewUserRequest newUserRequest) throws Exception {
        Objects.requireNonNull(newUserRequest, "NewUserRequest must not be null");
    
        logger.debug(String.format("Se ejecuta el método create. [newUserRequest = %s]", newUserRequest));
    
        String email = newUserRequest.getEmail();
        String first_name = newUserRequest.getNombre();
        String last_name = newUserRequest.getApellido();
        String password = newUserRequest.getPassword();
        Integer legajo = newUserRequest.getLegajo();
        String role = newUserRequest.getRol();
    
        try {
            // Configura los datos del usuario que se quiere crear en Auth0.
            User newUser = new User(dotenv.get("AUTH0_DB_CONNECTION"));
            newUser.setEmail(email);
            newUser.setName(first_name);
            newUser.setFamilyName(last_name);
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
            user.setApellido(last_name);
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
    


    /* Private */

    private static final Logger logger = LoggerFactory.getLogger(CourseEventService.class);
    Dotenv dotenv;
    
}
