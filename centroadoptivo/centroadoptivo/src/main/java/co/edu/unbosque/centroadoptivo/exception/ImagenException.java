package co.edu.unbosque.centroadoptivo.exception;

/**
 * Excepción lanzada cuando la imagen proporcionada no cumple con los requisitos del sistema.
 * <p>
 * Esta excepción es verificada ({@code checked}) y se utiliza para indicar que
 * el archivo de imagen no corresponde a un formato permitido (JPG, JPEG o PNG)
 * o excede el tamaño máximo permitido de 5MB.
 * </p>
 */
public class ImagenException extends Exception {

    /**
     * Constructor por defecto.
     * <p>
     * Inicializa la excepción con el mensaje predeterminado:
     * {@code "La imagen no es válida. Solo se permiten archivos JPG, JPEG o PNG de máximo 5MB."}.
     * </p>
     */
    public ImagenException() {
        super("La imagen no es válida. Solo se permiten archivos JPG, JPEG o PNG de máximo 5MB.");
    }
}