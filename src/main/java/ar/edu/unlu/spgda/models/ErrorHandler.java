package ar.edu.unlu.spgda.models;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;
import lombok.Data;

public class ErrorHandler {

    // Loguea y devuelve un error al front en formato JSON.
    public static String returnErrorAsJson(Exception e) {
        logger.error(String.format(
            "%s: %s",
            e.getClass(),
            e.getMessage()
        ));

        return new JSONObject()
            .append("Excepción", e.getClass())
            .append("Mensaje", e.getMessage())
            .toString();
    }

    // Loguea y devuelve un error al front en format ResponseEntity.
    public static ResponseEntity<Object> returnErrorAsResponseEntity(
        HttpStatus httpStatusEnum,
        Exception e,
        int code
    ) {

        logger.error(String.format(
            "%s: %s",
            e.getClass(),
            e.getMessage()
        ));

        var returningObject = new ErrorObj(
            e.getClass(),
            code,
            e.getMessage()
        );

        return ResponseEntity
            .status(httpStatusEnum)
            .body(
                returningObject
            );
    }

    // Loguea y termina la ejecución de la aplicación.
    public static void exit(Exception e) {
        logger.error(String.format(
            "%s: %s",
            e.getClass(),
            e.getMessage()
        ));

        System.exit(1);
    }


    /* Private */

    private ErrorHandler() {};

    private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

    @Data
    @AllArgsConstructor
    private static class ErrorObj {
        private Class exceptionClass;
        private int errorCode;
        private String errorDescription;
    }
}
