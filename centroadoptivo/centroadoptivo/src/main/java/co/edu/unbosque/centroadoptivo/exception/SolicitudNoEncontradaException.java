package co.edu.unbosque.centroadoptivo.exception;

public class SolicitudNoEncontradaException extends Exception {
	
    public SolicitudNoEncontradaException() {
    	
        super("Solicitud de adopción no encontrada");
    }
}