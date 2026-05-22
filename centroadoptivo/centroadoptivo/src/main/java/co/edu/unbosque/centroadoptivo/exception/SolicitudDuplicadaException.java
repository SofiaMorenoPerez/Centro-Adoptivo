package co.edu.unbosque.centroadoptivo.exception;

public class SolicitudDuplicadaException extends Exception {
	
    public SolicitudDuplicadaException() {
        super("Ya tienes una solicitud pendiente para este animal");
    }
}