package com.example.helloworld.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.auth0.exception.APIException;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;
import com.example.helloworld.models.Auth0Handler;
import com.example.helloworld.models.Userr;
import java.sql.SQLException;
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

        String email = newUserRequest.getEmail();
        String first_name = newUserRequest.getNombre();
        String last_name = newUserRequest.getApellido();
        String password = newUserRequest.getPassword();
        Integer legajo = newUserRequest.getLegajo();
        String role = newUserRequest.getRol();

        // Configura los datos del usuario que se quiere crear en Auth0.
        User newUser = new User(System.getenv("AUTH0_DB_CONNECTION"));
        newUser.setEmail(email);
        newUser.setName(first_name);
        newUser.setFamilyName(last_name);
        newUser.setPassword(password.toCharArray());

        // Obtiene token Auth0.
        Auth0Handler
            .getInstance()
            .createProfessor(newUser);

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
