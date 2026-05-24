package co.edu.unbosque.centroadoptivo.exception;

/**
 * Excepción lanzada cuando la ciudad ingresada por el usuario no es válida.
 * <p>
 * Esta excepción es verificada ({@code checked}) y se utiliza para indicar que
 * la ciudad proporcionada no corresponde a una ciudad reconocida o permitida
 * dentro del sistema del centro adoptivo.
 * </p>
 */
public class CiudadException extends Exception {

    /**
     * Constructor por defecto.
     * <p>
     * Inicializa la excepción con el mensaje predeterminado:
     * {@code "La ciudad ingresada no es válida"}.
     * </p>
     */
    public CiudadException() {
        super("La ciudad ingresada no es válida");
    }
}