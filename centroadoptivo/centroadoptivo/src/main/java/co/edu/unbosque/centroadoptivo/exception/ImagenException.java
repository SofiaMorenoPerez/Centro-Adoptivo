package co.edu.unbosque.centroadoptivo.exception;

public class ImagenException extends Exception {
    public ImagenException() {
        super("La imagen no es válida. Solo se permiten archivos JPG, JPEG o PNG de máximo 5MB.");
    }
}