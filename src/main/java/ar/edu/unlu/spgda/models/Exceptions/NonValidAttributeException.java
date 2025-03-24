package ar.edu.unlu.spgda.models.Exceptions;

public class NonValidAttributeException extends RuntimeException {
    public NonValidAttributeException(String msg) {
        super(msg);
    }
}