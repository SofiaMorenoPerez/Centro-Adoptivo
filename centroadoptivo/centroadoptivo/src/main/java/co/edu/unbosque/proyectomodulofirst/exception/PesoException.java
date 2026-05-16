package co.edu.unbosque.proyectomodulofirst.exception;

public class PesoException extends Exception {

    public PesoException() {
        super("El peso debe ser mayor a 0");
    }
}