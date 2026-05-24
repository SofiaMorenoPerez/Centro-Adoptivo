package co.edu.unbosque.centroadoptivo.exception;

/**
 * Excepción lanzada cuando se intenta adoptar un animal que no está disponible.
 * <p>
 * Esta excepción es verificada ({@code checked}) y se utiliza para indicar que
 * el animal solicitado ya fue adoptado, está en proceso de adopción, o por alguna
 * otra razón no se encuentra disponible en el centro adoptivo.
 * </p>
 */
public class AnimalNoDisponibleException extends Exception {

    /**
     * Constructor por defecto.
     * <p>
     * Inicializa la excepción con el mensaje predeterminado:
     * {@code "El animal no está disponible para adopción"}.
     * </p>
     */
    public AnimalNoDisponibleException() {
        super("El animal no está disponible para adopción");
    }
}