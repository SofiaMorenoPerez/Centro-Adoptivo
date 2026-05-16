package co.edu.unbosque.proyectomodulofirst.exception;

public class LanzadorDeExcepcion {

    public static void verificarTipoPaquete(Object tipo) throws TipoPaqueteException {
        if (tipo == null) throw new TipoPaqueteException();
    }

    public static void verificarTipoPaqueteTexto(String tipo) throws TipoPaqueteException {
        if (tipo == null || tipo.isEmpty()) throw new TipoPaqueteException();
        if (!tipo.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")) throw new TipoPaqueteException();
    }

    public static void verificarPeso(double peso) throws PesoException {
        if (peso <= 0) throw new PesoException();
    }

    public static void verificarCiudad(String ciudad, String nombreCampo) throws CiudadException {
        if (ciudad == null || ciudad.isEmpty()) throw new CiudadException();
        if (!ciudad.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")) throw new CiudadException();
    }

    public static void verificarDireccion(String direccion, String nombreCampo) throws DireccionException {
        if (direccion == null || direccion.isEmpty()) throw new DireccionException();
        if (!direccion.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z0-9 #\\-]+$")) throw new DireccionException();
    }

    public static void verificarIdNegativo(long id) throws InvalidDataException {
        if (id <= 0) throw new InvalidDataException();
    }

    public static void verificarNombre(String nombre) throws NombreException {
        if (nombre == null || nombre.isBlank()) throw new NombreException();
        if (!nombre.trim().matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")) throw new NombreException();
    }

    public static void verificarEdad(int edad) throws EdadException {
        if (edad <= 0 || edad > 120) throw new EdadException();
    }

    public static void verificarTelefono(long telefono) throws TelefonoException {
        if (telefono <= 0) throw new TelefonoException();
    }

    public static void verificarTipoVehiculo(String tipoVehiculo) throws TipoVehiculoException {
        if (tipoVehiculo == null || tipoVehiculo.isEmpty()) throw new TipoVehiculoException();
        if (!tipoVehiculo.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")) throw new TipoVehiculoException();
    }

    public static void verificarZonaAsignada(String zona) throws ZonaAsignadaException {
        if (zona == null || zona.isEmpty()) throw new ZonaAsignadaException();
        if (!zona.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")) throw new ZonaAsignadaException();
    }
}