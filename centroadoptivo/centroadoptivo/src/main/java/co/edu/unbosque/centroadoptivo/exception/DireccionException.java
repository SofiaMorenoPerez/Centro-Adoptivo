package co.edu.unbosque.centroadoptivo.exception;

/**
 * Excepción lanzada cuando la dirección ingresada por el usuario no es válida.
 * <p>
 * Esta excepción es verificada ({@code checked}) y se utiliza para indicar que
 * la dirección proporcionada no cumple con el formato o los criterios de validación
 * requeridos por el sistema del centro adoptivo.
 * </p>
 */
public class DireccionException extends Exception {

    /**
     * Constructor por defecto.
     * <p>
     * Inicializa la excepción con el mensaje predeterminado:
     * {@code "La dirección ingresada no es válida"}.
     * </p>
     */
    public DireccionException() {
        super("La dirección ingresada no es válida");
    }
}