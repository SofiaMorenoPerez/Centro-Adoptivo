package co.edu.unbosque.proyectomodulofirst.exception;

public class TipoPaqueteException extends Exception {

    public TipoPaqueteException() {
        super("El tipo de paquete es obligatorio");
    }
}