package co.edu.unbosque.centroadoptivo.exception;

/**
 * Excepción lanzada cuando la edad del usuario no cumple el requisito mínimo para adoptar.
 * <p>
 * Esta excepción es verificada ({@code checked}) y se utiliza para indicar que
 * el usuario es menor de 18 años y por tanto no está habilitado para realizar
 * una adopción en el centro adoptivo.
 * </p>
 */
public class EdadException extends Exception {

    /**
     * Constructor por defecto.
     * <p>
     * Inicializa la excepción con el mensaje predeterminado:
     * {@code "La edad no es válida. Debe ser mayor de 18 años para adoptar animales."}.
     * </p>
     */
    public EdadException() {
        super("La edad no es válida. Debe ser mayor de 18 años para adoptar animales.");
    }
}