package ar.edu.unlu.spgda.models.Exceptions;

public class NotAuthorizedException extends RuntimeException {

    public NotAuthorizedException(String msg) {
        super(msg);
    }

}
