package com.example.helloworld.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.auth0.exception.APIException;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;
import com.example.helloworld.models.Auth0Handler;
import com.example.helloworld.models.Professor;
import com.example.helloworld.models.Userr;
import java.sql.SQLException;
import java.util.HashMap;
import io.github.cdimascio.dotenv.Dotenv;
import com.example.helloworld.models.Validator;
import com.example.helloworld.models.Exceptions.NotValidAttributeException;
import com.example.helloworld.models.Exceptions.NullAttributeException;
import com.example.helloworld.repositories.UserRepository;
import com.example.helloworld.requests.NewUserRequest;

@Service
public class ProfessorService {

    private final UserRepository userRepository;
    
    public ProfessorService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Crea un docente y lo guarda en la BD.
    public Userr create (
        NewUserRequest newUserRequest
    ) throws
        NullAttributeException,
        NotValidAttributeException,
        SQLException,
        APIException,
        Auth0Exception
    {

        logger.debug(
            String.format(
                "Se ejecuta el método create. [newUserRequest = %s]",
                newUserRequest.toString()
            )
        );

        // // Valida los atributos. Arroja una excepción si hubo
        // // una validación no exitosa.
        // Validator validator = new Validator();
        // var attributes = new HashMap<String, String>();
        // attributes.put("email", newUserRequest.getEmail());
        // attributes.put("first_name", newUserRequest.getNombre());
        // attributes.put("last_name", newUserRequest.getApellido());
        // attributes.put("password", newUserRequest.getPassword());
        // validator.validateIfAnyNull(attributes)
        //     .validateEmailFormat(newUserRequest.getEmail())
        //     .validateProperNameFormat(newUserRequest.getNombre())
        //     .validateProperNameFormat(newUserRequest.getApellido())
        //     .validateDossierFormat(newUserRequest.getLegajo())
        //     .validatePasswordFormat(newUserRequest.getPassword());
        
        // Intenta insertar el registro del docente en la tabla.
        // Arroja una excepción si no fue posible.
        // TODO: refactorizar el método DatabaseHandler.insert para que acepte un Map igual que acepta Validator.
        /*var atributos = new ArrayList<Object>();
        atributos.add(legajo);
        atributos.add(first_name);
        atributos.add(last_name);
        atributos.add(email);
        atributos.add(role);
        DatabaseHandler
            .getInstance()
            .executeStatement(
                "INSERT" +
                    " INTO usuario (legajo, nombre, apellido, email, rol)" +
                    " VALUES (?, ?, ?, ?, ?)",
                atributos
            );
        */

        String email = newUserRequest.getEmail();
        String first_name = newUserRequest.getNombre();
        String last_name = newUserRequest.getApellido();
        String password = newUserRequest.getPassword();
        Integer legajo = newUserRequest.getLegajo();
        String role = newUserRequest.getRol();

        // Configura los datos del usuario que se quiere crear en Auth0.
        Dotenv dotenv = Dotenv.load();
        User newUser = new User(dotenv.get("AUTH0_DB_CONNECTION"));
        newUser.setEmail(email);
        newUser.setName(first_name);
        newUser.setFamilyName(last_name);
        newUser.setPassword(password.toCharArray());

        // // Obtiene token Auth0.
        // Auth0Handler
        //     .getInstance()
        //     .createProfessor(newUser);

        // Creamos objeto usuario y lo guardamos en repositorio JPA (usamos id de Auth0).
        Userr user = new Userr();
        user.setId(Auth0Handler.getInstance().getUserIdByEmail(email));
        user.setEmail(email);
        user.setNombre(first_name);
        user.setApellido(last_name);
        user.setLegajo(legajo);
        user.setRol(role);

        userRepository.save(user);

        // Todo salió OK; se devuelve el docente creado.
        return user;
    }


    /* Private */

    private static final Logger logger = LoggerFactory.getLogger(ClassEventService.class);
    
}
