package co.edu.unbosque.centroadoptivo.exception;

/**
 * Excepción lanzada cuando no se encuentra un usuario
 * con el identificador o nombre de usuario proporcionado.
 *
 * @author Centro Adoptivo Unbosque
 * @version 1.0
 */
public class UserNotFoundException extends Exception {

    /**
     * Constructor que inicializa la excepción con un mensaje predeterminado.
     */
    public UserNotFoundException() {
        super("User not found in the system.");
    }
}