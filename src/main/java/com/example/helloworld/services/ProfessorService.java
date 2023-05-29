package com.example.helloworld.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.auth0.exception.APIException;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;
import com.example.helloworld.models.Auth0Handler;
import com.example.helloworld.models.DatabaseHandler;
import com.example.helloworld.models.Professor;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import io.github.cdimascio.dotenv.Dotenv;
import com.example.helloworld.models.Validator;
import com.example.helloworld.models.Exceptions.NotValidAttributeException;
import com.example.helloworld.models.Exceptions.NullAttributeException;

@Service
public class ProfessorService {

    // Crea un docente y lo guarda en la BD.
    public Professor create (
        String email,
        String first_name,
        String last_name,
        int legajo,
        String password,
        String role
    ) throws
        NullAttributeException,
        NotValidAttributeException,
        SQLException,
        APIException,
        Auth0Exception
    {

        // Loguea los datos que se quieren insertar.
        logger.debug(
            String.format(
                "create(email: %s, first_name: %s, last_name: %s, legajo: %s, password: %s, role: %s)",
                email,
                first_name,
                last_name,
                String.valueOf(legajo),
                password,
                role
            )
        );

        // Valida los atributos. Arroja una excepción si hubo
        // una validación no exitosa.
        Validator validator = new Validator();
        var attributes = new HashMap<String, String>();
        attributes.put("email", email);
        attributes.put("first_name", first_name);
        attributes.put("last_name", last_name);
        attributes.put("password", password);
        attributes.put("role", role);
        validator.validateIfAnyNull(attributes)
            .validateEmailFormat(email)
            .validateProperNameFormat(first_name)
            .validateProperNameFormat(last_name)
            .validateDossierFormat(legajo)
            .validatePasswordFormat(password)
            .validateProperNameFormat(role);
        
        // Intenta insertar el registro del docente en la tabla.
        // Arroja una excepción si no fue posible.
        // TODO: refactorizar el método DatabaseHandler.insert para que acepte un Map igual que acepta Validator.
        var atributos = new ArrayList<Object>();
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
        
        // Configura los datos del usuario que se quiere crear en Auth0.
        Dotenv dotenv = Dotenv.load();
        User newUser = new User(dotenv.get("AUTH0_DB_CONNECTION"));
        newUser.setEmail(email);
        newUser.setName(first_name);
        newUser.setFamilyName(last_name);
        newUser.setPassword(password.toCharArray());

        // Obtiene token Auth0.
        Auth0Handler
            .getInstance()
            .createProfessor(newUser);

        // Todo salió OK; se devuelve el docente creado.
        return new Professor(email, first_name, last_name, legajo);
    }


    /* Private */

    private static final Logger logger = LoggerFactory.getLogger(ClassEventService.class);
    
}
