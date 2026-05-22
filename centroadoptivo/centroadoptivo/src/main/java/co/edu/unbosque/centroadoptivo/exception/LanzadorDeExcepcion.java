package co.edu.unbosque.centroadoptivo.exception;

import org.springframework.web.multipart.MultipartFile;

public class LanzadorDeExcepcion {
	
	public static void verificarUsuarioExiste(boolean existe) throws UserNotFoundException {
        if (!existe) throw new UserNotFoundException();
    }


    public static void verificarUsername(String username) throws UsernameException {
        if (username == null || username.isBlank()) throw new UsernameException();
        if (username.trim().length() < 3) throw new UsernameException();
        if (!username.matches("^[A-Za-z0-9._]+$")) throw new UsernameException();
    }

    public static void verificarPassword(String password) throws PasswordNotValidException {
        if (password == null || password.isBlank()) throw new PasswordNotValidException();
        if (password.length() < 8) throw new PasswordNotValidException();
        if (!password.matches(".*[A-Z].*")) throw new PasswordNotValidException();
        if (!password.matches(".*[a-z].*")) throw new PasswordNotValidException();
        if (!password.matches(".*[0-9].*")) throw new PasswordNotValidException();
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) throw new PasswordNotValidException();
        if (password.chars().distinct().count() < 4) throw new PasswordNotValidException();
    }

    public static void verificarEmail(String email) throws EmailException {
        if (email == null || email.isBlank()) throw new EmailException();
        if (email.contains(" ")) throw new EmailException();
        if (email.contains("..")) throw new EmailException();
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) throw new EmailException();
        String[] partes = email.split("@");
        if (partes.length != 2) throw new EmailException();
        if (partes[0].startsWith(".") || partes[0].endsWith(".")) throw new EmailException();
        if (partes[1].startsWith("-") || partes[1].endsWith("-")) throw new EmailException();
        if (partes[0].length() < 1) throw new EmailException();
    }

    public static void verificarNombre(String nombre) throws NombreException {
        if (nombre == null || nombre.isBlank()) throw new NombreException();
        if (!nombre.trim().matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")) throw new NombreException();
        if (nombre.trim().length() < 2) throw new NombreException();
    }

    public static void verificarTelefono(String telefono) throws TelefonoException {
        if (telefono == null || telefono.isBlank()) throw new TelefonoException();
        if (!telefono.matches("^\\+?[0-9]{7,15}$")) throw new TelefonoException();
    }

    public static void verificarCiudad(String ciudad) throws CiudadException {
        if (ciudad == null || ciudad.isBlank()) throw new CiudadException();
        if (!ciudad.trim().matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")) throw new CiudadException();
        if (ciudad.trim().length() < 2) throw new CiudadException();
    }

    public static void verificarDireccion(String direccion) throws DireccionException {
        if (direccion == null || direccion.isBlank()) throw new DireccionException();
        if (!direccion.trim().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z0-9 #\\-]+$")) throw new DireccionException();
        if (direccion.trim().length() < 5) throw new DireccionException();
    }

    public static void verificarEdad(int edad) throws EdadException {
        if (edad < 18) throw new EdadException();
        if (edad > 100) throw new EdadException();
    }


    public static void verificarNombreAnimal(String nombre) throws NombreException {
        if (nombre == null || nombre.isBlank()) throw new NombreException();
        if (!nombre.trim().matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")) throw new NombreException();
        if (nombre.trim().length() < 2) throw new NombreException();
    }

    public static void verificarEspecie(String especie) throws EspecieException {
        if (especie == null || especie.isBlank()) throw new EspecieException();
        if (!especie.trim().matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")) throw new EspecieException();
        if (especie.trim().length() < 2) throw new EspecieException();
    }

    public static void verificarRaza(String raza) throws RazaException {
        if (raza == null || raza.isBlank()) throw new RazaException();
        if (raza.trim().length() < 2) throw new RazaException();
    }

    public static void verificarObservaciones(String observaciones) throws ObservacionesException {
        if (observaciones == null || observaciones.isBlank()) throw new ObservacionesException();
        if (observaciones.trim().length() < 10) throw new ObservacionesException();
        if (observaciones.trim().length() > 500) throw new ObservacionesException();
    }

    public static void verificarImagen(MultipartFile imagen) throws ImagenException {
        if (imagen == null || imagen.isEmpty()) throw new ImagenException();
        String contentType = imagen.getContentType();
        if (contentType == null ||
            (!contentType.equals("image/jpeg") &&
             !contentType.equals("image/png"))) throw new ImagenException();
        if (imagen.getSize() > 5 * 1024 * 1024) throw new ImagenException();
    }

    public static void verificarAnimalExiste(boolean existe) throws AnimalNoEncontradoException {
        if (!existe) throw new AnimalNoEncontradoException();
    }

    public static void verificarValidacionIA(boolean aprobado) throws ValidacionIAException {
        if (!aprobado) throw new ValidacionIAException();
    }
    
    public static void verificarSolicitudExiste(boolean existe) throws SolicitudNoEncontradaException {
        if (!existe) throw new SolicitudNoEncontradaException();
    }

    public static void verificarAnimalDisponible(boolean disponible) throws AnimalNoDisponibleException {
        if (!disponible) throw new AnimalNoDisponibleException();
    }

    public static void verificarSolicitudPendiente(boolean pendiente) throws SolicitudNoPendienteException {
        if (!pendiente) throw new SolicitudNoPendienteException();
    }

    public static void verificarSolicitudDuplicada(boolean existe) throws SolicitudDuplicadaException {
        if (existe) throw new SolicitudDuplicadaException();
    }
}