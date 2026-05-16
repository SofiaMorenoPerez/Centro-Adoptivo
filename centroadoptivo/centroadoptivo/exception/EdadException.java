package co.edu.unbosque.proyectomodulofirst.exception;

public class EdadException extends Exception {

    public EdadException() {
        super("La edad ingresada no es válida");
    }
}