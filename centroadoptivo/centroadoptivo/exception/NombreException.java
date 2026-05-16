package co.edu.unbosque.proyectomodulofirst.exception;

public class NombreException extends Exception {

    public NombreException() {
        super("El nombre ingresado no es válido");
    }
}