package co.edu.unbosque.centroadoptivo.exception;

public class EdadException extends Exception {

	public EdadException() {
        super("La edad no es válida. Debe ser mayor de 18 años para adoptar animales.");
    }
}