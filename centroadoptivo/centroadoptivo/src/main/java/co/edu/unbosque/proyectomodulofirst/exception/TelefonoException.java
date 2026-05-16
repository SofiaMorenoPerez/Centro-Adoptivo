package co.edu.unbosque.proyectomodulofirst.exception;

public class TelefonoException extends Exception {

    public TelefonoException() {
        super("El teléfono ingresado no es válido");
    }
}