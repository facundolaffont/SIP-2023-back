package ar.edu.unlu.spgda.controllers;

import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.auth0.exception.APIException;
import com.auth0.exception.Auth0Exception;
import ar.edu.unlu.spgda.models.Exceptions.NotValidAttributeException;
import ar.edu.unlu.spgda.models.Exceptions.NullAttributeException;
import ar.edu.unlu.spgda.requests.NewUserRequest;
import ar.edu.unlu.spgda.services.ProfessorService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "https://spgda.fl.com.ar/")
public class UserController {
    
    @PostMapping("/add")
    //@PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Object> add(@RequestBody NewUserRequest newUserRequest)
        throws Exception
    {
        
        logger.info("POST /api/v1/users/add");
        logger.debug(newUserRequest.toString());

        Object newUser = null;
      //  switch(newUserRequest.getRol().toLowerCase()) {

        //     // Se quiere dar de alta un docente.
             //case "docente":
                 try {
                    newUser = professorService.create(newUserRequest);
                 }
                 catch (APIException e) {
                    return new ResponseEntity<>(
                        e,
                        HttpStatus.INTERNAL_SERVER_ERROR
                    );
                 }
                 catch (NotValidAttributeException | NullAttributeException | SQLException | Auth0Exception e) {
                    return new ResponseEntity<>(
                        e,
                        HttpStatus.INTERNAL_SERVER_ERROR
                    );
                 }
            // break;
            
        //     // Se quiere dar de alta un administrador.
             /*case "administrador":
                logger.debug("Se solicita el alta de un administrador.");
             break;
             default:
                throw new NotValidAttributeException("El valor del rol no es válido."); */
      //  }

        return ResponseEntity.status(HttpStatus.OK).body(newUser);    

    }


    /* Private */

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final ProfessorService professorService;
    
}
