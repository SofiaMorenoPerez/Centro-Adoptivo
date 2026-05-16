package co.edu.unbosque.proyectomodulofirst.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Clase encargada de manejar excepciones globales en la aplicación.
 * Permite capturar errores específicos y retornar respuestas HTTP adecuadas.
 */
public class GlobalExceptionHandler {

	/**
	 * Maneja la excepción cuando un recurso no es encontrado.
	 * 
	 * @param ex excepción capturada
	 * @return respuesta con estado NOT_FOUND y mensaje de error
	 */
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<String> manejarRecursoNoEncontrado(ResourceNotFoundException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	/**
	 * Maneja la excepción cuando los datos son inválidos.
	 * 
	 * @param ex excepción capturada
	 * @return respuesta con estado NOT_FOUND y mensaje de error
	 */
	@ExceptionHandler(InvalidDataException.class)
	public ResponseEntity<String> manejarNotFound(InvalidDataException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}
}