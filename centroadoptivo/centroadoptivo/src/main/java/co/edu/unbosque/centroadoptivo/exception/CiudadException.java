package co.edu.unbosque.centroadoptivo.exception;

public class CiudadException extends Exception {

    public CiudadException() {
        super("La ciudad ingresada no es válida");
    }
}