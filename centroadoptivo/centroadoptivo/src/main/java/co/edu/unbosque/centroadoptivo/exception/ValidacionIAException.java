package co.edu.unbosque.centroadoptivo.exception;

/**
 * Excepción lanzada cuando un animal no supera el proceso de validación
 * realizado por las inteligencias artificiales del sistema.
 *
 * @author Centro Adoptivo Unbosque
 * @version 1.0
 */
public class ValidacionIAException extends Exception {

    /**
     * Constructor que inicializa la excepción con un mensaje predeterminado.
     */
    public ValidacionIAException() {
        super("El animal no pasó la validación de las IAs");
    }

    /**
     * Constructor que inicializa la excepción con un mensaje personalizado.
     *
     * @param mensaje mensaje descriptivo del motivo del rechazo
     */
    public ValidacionIAException(String mensaje) {
        super(mensaje);
    }
}