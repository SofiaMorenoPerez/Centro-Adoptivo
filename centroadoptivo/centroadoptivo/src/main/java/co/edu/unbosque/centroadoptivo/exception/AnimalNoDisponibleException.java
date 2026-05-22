package co.edu.unbosque.centroadoptivo.exception;

public class AnimalNoDisponibleException extends Exception {
	
    public AnimalNoDisponibleException() {
        super("El animal no está disponible para adopción");
    }
}