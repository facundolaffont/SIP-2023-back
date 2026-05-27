package ar.edu.unlu.spgda.models.Exceptions;

public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String mensaje) {
        super(mensaje);
    }
}