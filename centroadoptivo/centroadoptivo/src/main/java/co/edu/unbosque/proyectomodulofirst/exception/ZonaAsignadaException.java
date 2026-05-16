package co.edu.unbosque.proyectomodulofirst.exception;

public class ZonaAsignadaException extends Exception {

    public ZonaAsignadaException() {
        super("La zona asignada es obligatoria");
    }
}