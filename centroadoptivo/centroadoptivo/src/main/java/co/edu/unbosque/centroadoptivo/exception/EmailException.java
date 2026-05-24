package co.edu.unbosque.centroadoptivo.exception;

/**
 * Excepción lanzada cuando el correo electrónico ingresado no tiene un formato válido.
 * <p>
 * Esta excepción es verificada ({@code checked}) y se utiliza para indicar que
 * el correo electrónico proporcionado no cumple con el formato estándar esperado
 * (por ejemplo: {@code usuario@dominio.com}).
 * </p>
 */
public class EmailException extends Exception {

    /**
     * Constructor por defecto.
     * <p>
     * Inicializa la excepción con el mensaje predeterminado:
     * {@code "El correo electrónico no es válido. Asegúrate de que tenga un formato correcto (ejemplo: usuario@dominio.com)."}.
     * </p>
     */
    public EmailException() {
        super("El correo electrónico no es válido. Asegúrate de que tenga un formato correcto (ejemplo: usuario@dominio.com).");
    }
}