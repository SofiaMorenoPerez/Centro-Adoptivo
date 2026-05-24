package co.edu.unbosque.centroadoptivo.exception;

/**
 * Excepción lanzada cuando la especie ingresada para un animal no es válida.
 * <p>
 * Esta excepción es verificada ({@code checked}) y se utiliza para indicar que
 * la especie proporcionada no corresponde a una especie reconocida o permitida
 * dentro del sistema del centro adoptivo.
 * </p>
 */
public class EspecieException extends Exception {

    /**
     * Constructor por defecto.
     * <p>
     * Inicializa la excepción con el mensaje predeterminado:
     * {@code "La especie ingresada no es válida"}.
     * </p>
     */
    public EspecieException() {
        super("La especie ingresada no es válida");
    }
}