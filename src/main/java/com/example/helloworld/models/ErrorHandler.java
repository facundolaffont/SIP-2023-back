package com.example.helloworld.models;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorHandler {

    // Loguea y devuelve un error al front.
    public static String returnError(Exception e) {
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
