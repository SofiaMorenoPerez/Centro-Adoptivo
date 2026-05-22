package co.edu.unbosque.centroadoptivo.exception;

public class UserNotFoundException extends Exception {
    public UserNotFoundException() {
        super("User not found in the system.");
    }
}
