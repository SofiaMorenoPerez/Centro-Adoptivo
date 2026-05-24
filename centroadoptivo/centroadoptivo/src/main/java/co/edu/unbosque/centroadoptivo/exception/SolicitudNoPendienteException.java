package co.edu.unbosque.centroadoptivo.exception;

/**
 * Excepción lanzada cuando se intenta aprobar o rechazar una solicitud
 * de adopción que no se encuentra en estado pendiente.
 *
 * @author Centro Adoptivo Unbosque
 * @version 1.0
 */
public class SolicitudNoPendienteException extends Exception {

    /**
     * Constructor que inicializa la excepción con un mensaje predeterminado.
     */
    public SolicitudNoPendienteException() {
        super("La solicitud no está en estado pendiente");
    }
}