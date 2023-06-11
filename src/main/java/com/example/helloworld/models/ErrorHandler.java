package com.example.helloworld.models;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    public static ResponseEntity<String> returnErrorAsResponseEntity(Exception e) {
        logger.error(String.format(
            "%s: %s",
            e.getClass(),
            e.getMessage()
        ));


        var returningJson = (new JSONObject())
            .append("Excepción", e.getClass())
            .append("Mensaje", e.getMessage());
        var statusCode = HttpStatus.INTERNAL_SERVER_ERROR;

        return ResponseEntity
            .status(statusCode)
            .header("Content-Type", "application/json")
            .body(
                returningJson.toString()
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
}
