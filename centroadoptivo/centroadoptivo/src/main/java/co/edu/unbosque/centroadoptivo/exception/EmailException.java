package co.edu.unbosque.centroadoptivo.exception;

public class EmailException extends Exception {
    public EmailException() {
        super("El correo electrónico no es válido. Asegúrate de que tenga un formato correcto (ejemplo: usuario@dominio.com).");
    }
}
