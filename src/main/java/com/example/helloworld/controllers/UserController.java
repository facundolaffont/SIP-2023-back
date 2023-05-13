package com.example.helloworld.controllers;

import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.auth0.exception.APIException;
import com.auth0.exception.Auth0Exception;
import com.example.helloworld.models.ErrorHandler;
import com.example.helloworld.models.Exceptions.NotValidAttributeException;
import com.example.helloworld.models.Exceptions.NullAttributeException;
import com.example.helloworld.requests.NewUserRequest;
import com.example.helloworld.services.ProfessorService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    
    @PostMapping("/add")
    //@PreAuthorize("hasAuthority('admin')")
    //@CrossOrigin(origins = "http://localhost:4040")
    @CrossOrigin(origins = "*") // DEBUG: para hacer peticiones sin problemas con CORS.
    public Object add(@RequestBody NewUserRequest newUserRequest)
        throws NotValidAttributeException
    {
        logger.info("POST /api/v1/users/add");

        Object newUser = null;
        switch(newUserRequest.getRol().toLowerCase()) {

            // Se quiere dar de alta un docente.
            case "docente":
                try {
                    newUser = professorService.create(
                        newUserRequest.getEmail(),
                        newUserRequest.getNombre(),
                        newUserRequest.getApellido(),
                        newUserRequest.getLegajo(),
                        newUserRequest.getPassword(),
                        newUserRequest.getRol()
                    );
                }
                catch (APIException e) {
                    return ErrorHandler.returnError(e);
                }
                catch (NotValidAttributeException | NullAttributeException | SQLException | Auth0Exception e) {
                    return ErrorHandler.returnError(e);
                }
            break;
            
            // Se quiere dar de alta un administrador.
            case "administrador":
                logger.debug("Se solicita el alta de un administrador.");
            break;
            default:
                throw new NotValidAttributeException("El valor del rol no es válido.");
        }

        return newUser;
    }


    /* Private */

    private static final Logger logger = LogManager.getLogger(UserController.class);
    private final ProfessorService professorService;
    
}
