package co.edu.unbosque.centroadoptivo.exception;

public class NombreException extends Exception {

    public NombreException() {
        super("El nombre ingresado no es válido");
    }
}