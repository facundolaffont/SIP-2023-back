package ar.edu.unlu.spgda.models.Exceptions;

public class NotValidAttributeException extends RuntimeException {
    public NotValidAttributeException(String msg) {
        super(msg);
    }
}