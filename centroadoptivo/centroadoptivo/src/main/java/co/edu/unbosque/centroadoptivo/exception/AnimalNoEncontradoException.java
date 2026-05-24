package co.edu.unbosque.centroadoptivo.exception;

/**
 * Excepción lanzada cuando no se encuentra un animal en el sistema.
 * <p>
 * Esta excepción es verificada ({@code checked}) y se utiliza para indicar que
 * el animal buscado no existe en el repositorio del centro adoptivo.
 * </p>
 */
public class AnimalNoEncontradoException extends Exception {

    /**
     * Constructor por defecto.
     * <p>
     * Inicializa la excepción con el mensaje predeterminado:
     * {@code "Animal no encontrado"}.
     * </p>
     */
    public AnimalNoEncontradoException() {
        super("Animal no encontrado");
    }
}