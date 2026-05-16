package co.edu.unbosque.centroadoptivo.exception;

public class DireccionException extends Exception {

    public DireccionException() {
        super("La dirección ingresada no es válida");
    }
}