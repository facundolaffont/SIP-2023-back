package ar.edu.unlu.spgda.models.Exceptions;

public class NullAttributeException extends RuntimeException {
    public NullAttributeException(String msg) {
        super(msg);
    }
}