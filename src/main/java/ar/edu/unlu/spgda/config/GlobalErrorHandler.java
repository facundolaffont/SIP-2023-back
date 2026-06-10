package ar.edu.unlu.spgda.config;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import ar.edu.unlu.spgda.models.ErrorMessage;

@RestControllerAdvice
public class GlobalErrorHandler {

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(NoHandlerFoundException.class)
  public ErrorMessage handleNotFound(final HttpServletRequest request, final Exception error) {
    return ErrorMessage.from("Not Found");
  }

  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler(AccessDeniedException.class)
  public ErrorMessage handleAccessDenied(final HttpServletRequest request, final Exception error) {
    return ErrorMessage.from("Permission denied");
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Throwable.class)
  public ErrorMessage handleInternalError(final HttpServletRequest request, final Exception error) {
    return ErrorMessage.from(error.getMessage());
  }

  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler(InvalidFormatException.class)
  public ErrorMessage handleJsonParseError(final HttpServletRequest request, final Exception error) {
    logger.error("handleJsonParseException");
    return ErrorMessage.from(error.getMessage());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        
        // Creamos un Map para devolver un JSON estructurado: { "campo": "mensaje de error" }
        Map<String, String> errors = new HashMap<>();
        
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        
        // Devolvemos un 400 Bad Request con el JSON de errores
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

  /* Private */

  private static final Logger logger = LoggerFactory.getLogger(GlobalErrorHandler.class);
  
}
