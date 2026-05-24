package co.edu.unbosque.centroadoptivo.exception;

/**
 * Excepción lanzada cuando el número de teléfono ingresado no cumple
 * con los criterios de validación del sistema.
 *
 * @author Centro Adoptivo Unbosque
 * @version 1.0
 */
public class TelefonoException extends Exception {

    /**
     * Constructor que inicializa la excepción con un mensaje predeterminado.
     */
    public TelefonoException() {
        super("El teléfono ingresado no es válido");
    }
}