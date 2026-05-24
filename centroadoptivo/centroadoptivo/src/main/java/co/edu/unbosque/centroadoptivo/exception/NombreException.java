package co.edu.unbosque.centroadoptivo.exception;

/**
 * Excepción lanzada cuando el nombre ingresado no es válido.
 * <p>
 * Esta excepción es verificada ({@code checked}) y se utiliza para indicar que
 * el nombre proporcionado no cumple con las reglas de validación del sistema,
 * como contener caracteres no permitidos o no alcanzar la longitud mínima requerida.
 * </p>
 */
public class NombreException extends Exception {

    /**
     * Constructor por defecto.
     * <p>
     * Inicializa la excepción con el mensaje predeterminado:
     * {@code "El nombre ingresado no es válido"}.
     * </p>
     */
    public NombreException() {
        super("El nombre ingresado no es válido");
    }
}