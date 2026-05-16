package co.edu.unbosque.centroadoptivo.exception;

public class InvalidDataException extends Exception {

    public InvalidDataException() {
        super("El dato ingresado no es válido");
    }
}