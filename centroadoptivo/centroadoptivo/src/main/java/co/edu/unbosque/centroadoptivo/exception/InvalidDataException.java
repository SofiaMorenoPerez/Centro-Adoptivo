package co.edu.unbosque.centroadoptivo.exception;

/**
 * Excepción lanzada cuando un dato ingresado en el sistema no es válido.
 * <p>
 * Esta excepción es verificada ({@code checked}) y se utiliza de forma genérica
 * para indicar que uno o más datos proporcionados no cumplen con las reglas
 * de validación del centro adoptivo.
 * </p>
 */
public class InvalidDataException extends Exception {

    /**
     * Constructor por defecto.
     * <p>
     * Inicializa la excepción con el mensaje predeterminado:
     * {@code "El dato ingresado no es válido"}.
     * </p>
     */
    public InvalidDataException() {
        super("El dato ingresado no es válido");
    }
}