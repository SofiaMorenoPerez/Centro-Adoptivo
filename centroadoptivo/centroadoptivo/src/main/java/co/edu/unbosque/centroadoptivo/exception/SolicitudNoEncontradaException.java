package co.edu.unbosque.centroadoptivo.exception;

/**
 * Excepción lanzada cuando no se encuentra una solicitud de adopción
 * con el identificador proporcionado.
 *
 * @author Centro Adoptivo Unbosque
 * @version 1.0
 */
public class SolicitudNoEncontradaException extends Exception {

    /**
     * Constructor que inicializa la excepción con un mensaje predeterminado.
     */
    public SolicitudNoEncontradaException() {
        super("Solicitud de adopción no encontrada");
    }
}