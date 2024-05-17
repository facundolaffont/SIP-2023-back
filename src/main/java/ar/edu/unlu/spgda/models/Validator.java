package ar.edu.unlu.spgda.models;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.edu.unlu.spgda.models.Exceptions.NotValidAttributeException;
import ar.edu.unlu.spgda.models.Exceptions.NullAttributeException;

public class Validator {
    
    public Validator validateIfAnyNull(HashMap<String, String> attrs)
        throws NullAttributeException
    {   
        logger.debug("Se ejecuta el método validateIfAnyNull.");

        attrs.forEach(
            (key, value) -> {
                if (value == null)
                    throw new NullAttributeException( // Acá dentro no se puede arrojar otra cosa que RuntimeException.
                        String.format("Atributo '%s' es null.", key)
                    );
            }
        );

        return this;
    }

    public Validator validateEmailFormat(String email) throws NotValidAttributeException {
        logger.debug("Se ejecuta el método validateEmailFormat.");
        
        if (!email.matches("^[a-zA-Z0-9.!#$%&'*+\\/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$"))
            throw new NotValidAttributeException(String.format("El atributo '%s' no tiene un formato válido.", email));

        int maxLength = 40;
        if (email.length() > maxLength)
            throw new NotValidAttributeException(String.format(
                "El atributo '%s' tiene más de %d caracteres.",
                email,
                maxLength
            ));

        return this;
    }

    public Validator validateProperNameFormat(String name) throws NotValidAttributeException {
        logger.debug("Se ejecuta el método validateProperNameFormat.");

        if (!name.matches("^[a-zA-ZÀ-ÿ\\x{00f1}\\x{00d1}][a-zA-ZÀ-ÿ\\x{00f1}\\x{00d1} ]*$"))
            throw new NotValidAttributeException(String.format("El atributo '%s' no tiene un formato válido.", name));
        
        int maxLength = 30;
        if(name.length() > maxLength)
            throw new NotValidAttributeException(String.format(
                "El atributo '%s' tiene más de %d caracteres.",
                name,
                maxLength
            ));

        return this;
    }

    public Validator validateDateFormat(String date) throws NotValidAttributeException {
        logger.debug("Se ejecuta el método validateDateFormat.");

        if (!date.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$"))
            throw new NotValidAttributeException(String.format("El atributo '%s' no tiene un formato válido.", date));

        return this;
    }

    public Validator validateTimeFormat(String time) throws NotValidAttributeException {
        logger.debug("Se ejecuta el método validateTimeFormat.");

        if (!time.matches("^\\d{2}:\\d{2}$"))
            throw new NotValidAttributeException(String.format("El atributo '%s' no tiene un formato válido.", time));

        return this;
    }

    public Validator validateDNIFormat(String dni) throws NotValidAttributeException {
        logger.debug("Se ejecuta el método validateDNIFormat.");

        if (!dni.matches("^\\d+$"))
            throw new NotValidAttributeException(String.format("El atributo '%s' no tiene un formato válido.", dni));

        return this;
    }

    public Validator validateUserNameFormat(String userName) throws NotValidAttributeException {
        logger.debug("Se ejecuta el método validateUserNameFormat.");

        int maxLength = 30;
        if (userName.length() > maxLength)
            throw new NotValidAttributeException(String.format(
                "El atributo '%s' tiene más de %d caracteres.",
                userName,
                maxLength
            ));

        return this;
    }

    public Validator validatePasswordFormat(String pass) throws NotValidAttributeException {
        logger.debug("Se ejecuta el método validatePasswordFormat.");

        int minLength = 10;
        if (pass.length() < minLength)
            throw new NotValidAttributeException(String.format(
                "El atributo '%s' tiene menos de %d caracteres.",
                pass,
                minLength
            ));

        return this;
    }

    public Validator validateDossierFormat(int dossier) throws NotValidAttributeException {
        logger.debug("Se ejecuta el método validateDossierFormat.");

        if (dossier < 1)
            throw new NotValidAttributeException(String.format(
                "El atributo '%s' es menor a 1.", dossier
            ));

        return this;
    }


    /* Private */

    private static final Logger logger = LoggerFactory.getLogger(Validator.class);

}