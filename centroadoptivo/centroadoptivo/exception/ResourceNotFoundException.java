package co.edu.unbosque.proyectomodulofirst.exception;

/**
 * Excepción personalizada para cuando un recurso no es encontrado.
 */
public class ResourceNotFoundException extends RuntimeException{

    /**
     * Constructor que recibe el mensaje de error.
     * 
     * @param mensaje descripción del error
     */
    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }
}