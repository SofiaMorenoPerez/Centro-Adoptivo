package co.edu.unbosque.centroadoptivo.exception;

/**
 * Excepción lanzada cuando la raza ingresada no cumple
 * con los criterios de validación del sistema.
 *
 * @author Centro Adoptivo Unbosque
 * @version 1.0
 */
public class RazaException extends Exception {

    /**
     * Constructor que inicializa la excepción con un mensaje predeterminado.
     */
    public RazaException() {
        super("La raza ingresada no es válida");
    }
}