package co.edu.unbosque.proyectomodulofirst.exception;

public class CiudadException extends Exception {

    public CiudadException() {
        super("La ciudad ingresada no es válida");
    }
}