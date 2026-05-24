package co.edu.unbosque.centroadoptivo.exception;

/**
 * Excepción lanzada cuando el nombre de usuario ingresado no cumple
 * con los criterios de validación del sistema.
 *
 * @author Centro Adoptivo Unbosque
 * @version 1.0
 */
public class UsernameException extends Exception {

    /**
     * Constructor que inicializa la excepción con un mensaje predeterminado.
     */
    public UsernameException() {
        super("El nombre de usuario ingresado no es válido");
    }
}