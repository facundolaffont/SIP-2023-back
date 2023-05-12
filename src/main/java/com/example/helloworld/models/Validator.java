package com.example.helloworld.models;

import com.example.helloworld.models.Exceptions.NotValidAttributeException;
import com.example.helloworld.models.Exceptions.NullAttributeException;

public class Validator {
    
    public Validator validateIfAnyNull(String... attrs) throws NullAttributeException {
        if (attrs.length > 0)
            for (String attr : attrs)
                if (attr == null)
                    throw new NullAttributeException(String.format("Atributo '%s' es null.", attr));

        return this;
    }

    public Validator validateEmailFormat(String email) throws NotValidAttributeException {
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
        if (
            !date.matches(
                "/^\\d{4}-\\d{1,2}-\\d{1,2}$/"
            )
        ) throw new NotValidAttributeException(String.format("El atributo '%s' no tiene un formato válido.", date));

        return this;
    }

    public Validator validateTimeFormat(String time) throws NotValidAttributeException {
        if (
            !time.matches(
                "/^\\d{2}:\\d{2}$/"
            )
        ) throw new NotValidAttributeException(String.format("El atributo '%s' no tiene un formato válido.", time));

        return this;
    }

    public Validator validateDNIFormat(String dni) throws NotValidAttributeException {
        if (!dni.matches("/^\\d+$/"))
            throw new NotValidAttributeException(String.format("El atributo '%s' no tiene un formato válido.", dni));

        return this;
    }

    public Validator validateUserNameFormat(String userName) throws NotValidAttributeException {
        if (
            userName.length() > 30
        ) throw new NotValidAttributeException(String.format("El atributo '%s' no tiene un formato válido.", userName));

        return this;
    }

    public Validator validatePasswordFormat(String pass) throws NotValidAttributeException {
        if (pass.length() < 10)
            throw new NotValidAttributeException(String.format("El atributo '%s' no tiene un formato válido.", pass));

        return this;
    }

    public Validator validateDossierFormat(int dossier) throws NotValidAttributeException {
        if (dossier < 1)
            throw new NotValidAttributeException(String.format("El atributo '%s' no tiene un formato válido.", dossier));

        return this;
    }

}