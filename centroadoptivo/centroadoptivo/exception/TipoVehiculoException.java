package co.edu.unbosque.proyectomodulofirst.exception;

public class TipoVehiculoException extends Exception {

    public TipoVehiculoException() {
        super("El tipo de vehículo es obligatorio");
    }
}