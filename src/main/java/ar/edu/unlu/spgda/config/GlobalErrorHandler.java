package ar.edu.unlu.spgda.config;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import ar.edu.unlu.spgda.models.ErrorMessage;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

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


  /* Private */

  private static final Logger logger = LoggerFactory.getLogger(GlobalErrorHandler.class);
  
}
