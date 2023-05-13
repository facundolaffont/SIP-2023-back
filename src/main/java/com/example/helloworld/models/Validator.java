package com.example.helloworld.models;

import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.example.helloworld.models.Exceptions.NotValidAttributeException;
import com.example.helloworld.models.Exceptions.NullAttributeException;

public class Validator {
    
    public Validator validateIfAnyNull(HashMap<String, String> attrs)
        throws NullAttributeException
    {   
        logger.debug("validateIfAnyNull(...)");

        try {
            attrs.forEach(
                (key, value) -> {
                    if (value == null)
                        throw new RuntimeException( // Acá dentro no se puede arrojar otra cosa que RuntimeException.
                            String.format("Atributo '%s' es null.", key)
                        );
                }
            );
        }
        catch (RuntimeException e) {
            throw new NullAttributeException(e.getMessage());
        }

        return this;
    }

    public Validator validateEmailFormat(String email) throws NotValidAttributeException {
        logger.debug("validateEmailFormat(...)");
        
        if (
            !email.matches(
                "/^[a-zA-Z0-9.!#$%&'*+\\/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$/"
            )
            ||
            email.length() > 40
        ) throw new NotValidAttributeException(String.format("El atributo '%s' no tiene un formato válido.", email));

        return this;
    }

    public Validator validateProperNameFormat(String name) throws NotValidAttributeException {
        logger.debug("validateProperNameFormat(...)");

        if (
            !name.matches(
                "/^[a-zA-ZÀ-ÿ\\x{00f1}\\x{00d1}][a-zA-ZÀ-ÿ\\x{00f1}\\x{00d1} ]*$/"
            )
            ||
            name.length() > 30
        ) throw new NotValidAttributeException(String.format("El atributo '%s' no tiene un formato válido.", name));

        return this;
    }

    public Validator validateDateFormat(String date) throws NotValidAttributeException {
        logger.debug("validateDateFormat(...)");

        if (
            !date.matches(
                "/^\\d{4}-\\d{1,2}-\\d{1,2}$/"
            )
        ) throw new NotValidAttributeException(String.format("El atributo '%s' no tiene un formato válido.", date));

        return this;
    }

    public Validator validateTimeFormat(String time) throws NotValidAttributeException {
        logger.debug("validateTimeFormat(...)");

        if (
            !time.matches(
                "/^\\d{2}:\\d{2}$/"
            )
        ) throw new NotValidAttributeException(String.format("El atributo '%s' no tiene un formato válido.", time));

        return this;
    }

    public Validator validateDNIFormat(String dni) throws NotValidAttributeException {
        logger.debug("validateDNIFormat(...)");

        if (!dni.matches("/^\\d+$/"))
            throw new NotValidAttributeException(String.format("El atributo '%s' no tiene un formato válido.", dni));

        return this;
    }

    public Validator validateUserNameFormat(String userName) throws NotValidAttributeException {
        logger.debug("validateUserNameFormat(...)");

        if (
            userName.length() > 30
        ) throw new NotValidAttributeException(String.format("El atributo '%s' no tiene un formato válido.", userName));

        return this;
    }

    public Validator validatePasswordFormat(String pass) throws NotValidAttributeException {
        logger.debug("validatePasswordFormat(...)");

        if (pass.length() < 10)
            throw new NotValidAttributeException(String.format("El atributo '%s' no tiene un formato válido.", pass));

        return this;
    }

    public Validator validateDossierFormat(int dossier) throws NotValidAttributeException {
        logger.debug("validateDossierFormat(...)");

        if (dossier < 1)
            throw new NotValidAttributeException(String.format("El atributo '%s' no tiene un formato válido.", dossier));

        return this;
    }


    /* Private */

    private static final Logger logger = LogManager.getLogger(Validator.class);

}