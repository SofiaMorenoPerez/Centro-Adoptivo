package co.edu.unbosque.centroadoptivo.exception;

/**
 * Excepción lanzada cuando no se encuentra una notificación en el sistema.
 * <p>
 * Esta excepción es verificada ({@code checked}) y se utiliza para indicar que
 * la notificación buscada no existe en el repositorio del centro adoptivo.
 * </p>
 */
public class NotificacionNoEncontradaException extends Exception {

    /**
     * Constructor por defecto.
     * <p>
     * Inicializa la excepción con el mensaje predeterminado:
     * {@code "Notificación no encontrada"}.
     * </p>
     */
    public NotificacionNoEncontradaException() { super("Notificación no encontrada"); }
}
