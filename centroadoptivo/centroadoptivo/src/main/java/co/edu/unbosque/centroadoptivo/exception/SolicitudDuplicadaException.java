package co.edu.unbosque.centroadoptivo.exception;

/**
 * Excepción lanzada cuando el usuario intenta crear una solicitud de adopción
 * para un animal sobre el cual ya tiene una solicitud pendiente.
 *
 * @author Centro Adoptivo Unbosque
 * @version 1.0
 */
public class SolicitudDuplicadaException extends Exception {

    /**
     * Constructor que inicializa la excepción con un mensaje predeterminado.
     */
    public SolicitudDuplicadaException() {
        super("Ya tienes una solicitud pendiente para este animal");
    }
}