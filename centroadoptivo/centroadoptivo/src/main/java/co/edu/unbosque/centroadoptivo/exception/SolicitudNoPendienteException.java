package co.edu.unbosque.centroadoptivo.exception;

public class SolicitudNoPendienteException extends Exception {
	
	    public SolicitudNoPendienteException() {
	        super("La solicitud no está en estado pendiente");
	    }
	}