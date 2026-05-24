package co.edu.unbosque.centroadoptivo.exception;

/**
 * Excepción lanzada cuando las observaciones ingresadas no cumplen
 * con los criterios de validación del sistema.
 *
 * @author Centro Adoptivo Unbosque
 * @version 1.0
 */
public class ObservacionesException extends Exception {

    /**
     * Constructor que inicializa la excepción con un mensaje predeterminado.
     */
    public ObservacionesException() {
        super("Las observaciones no son válidas");
    }
}