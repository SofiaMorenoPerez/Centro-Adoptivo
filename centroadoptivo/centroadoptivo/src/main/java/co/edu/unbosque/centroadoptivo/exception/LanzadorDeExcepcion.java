package co.edu.unbosque.centroadoptivo.exception;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

/**
 * Clase utilitaria que centraliza la lógica de validación y lanzamiento de excepciones
 * en el sistema del centro adoptivo.
 * <p>
 * Todos los métodos son estáticos y lanzan excepciones verificadas ({@code checked})
 * específicas según el tipo de validación que falle. Su uso evita duplicar
 * lógica de validación a lo largo de las capas del sistema.
 * </p>
 */
public class LanzadorDeExcepcion {

    /**
     * Verifica que un usuario exista en el sistema.
     *
     * @param existe {@code true} si el usuario existe
     * @throws UserNotFoundException si el usuario no existe
     */
    public static void verificarUsuarioExiste(boolean existe) throws UserNotFoundException {
        if (!existe) throw new UserNotFoundException();
    }

    /**
     * Verifica que el nombre de usuario sea válido.
     * <p>
     * El username no puede ser nulo ni vacío, debe tener al menos 3 caracteres
     * y solo puede contener letras, números, puntos y guiones bajos.
     * </p>
     *
     * @param username nombre de usuario a validar
     * @throws UsernameException si el username no cumple alguna de las reglas
     */
    public static void verificarUsername(String username) throws UsernameException {
        if (username == null || username.isBlank()) throw new UsernameException();
        if (username.trim().length() < 3) throw new UsernameException();
        if (!username.matches("^[A-Za-z0-9._]+$")) throw new UsernameException();
    }

    /**
     * Verifica que la contraseña sea válida.
     * <p>
     * La contraseña debe tener al menos 8 caracteres, contener mayúsculas,
     * minúsculas, números, un carácter especial y al menos 4 caracteres distintos.
     * </p>
     *
     * @param password contraseña a validar
     * @throws PasswordNotValidException si la contraseña no cumple alguna de las reglas
     */
    public static void verificarPassword(String password) throws PasswordNotValidException {
        if (password == null || password.isBlank()) throw new PasswordNotValidException();
        if (password.length() < 8) throw new PasswordNotValidException();
        if (!password.matches(".*[A-Z].*")) throw new PasswordNotValidException();
        if (!password.matches(".*[a-z].*")) throw new PasswordNotValidException();
        if (!password.matches(".*[0-9].*")) throw new PasswordNotValidException();
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) throw new PasswordNotValidException();
        if (password.chars().distinct().count() < 4) throw new PasswordNotValidException();
    }

    /**
     * Verifica que el correo electrónico sea válido.
     * <p>
     * El email no puede contener espacios ni puntos consecutivos, debe seguir
     * el formato estándar {@code usuario@dominio.ext} y cumplir reglas de
     * estructura en las partes local y de dominio.
     * </p>
     *
     * @param email correo electrónico a validar
     * @throws EmailException si el email no cumple alguna de las reglas
     */
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

    /**
     * Verifica que el nombre completo de una persona sea válido.
     * <p>
     * El nombre no puede ser nulo ni vacío, solo puede contener letras
     * (incluyendo tildes y ñ) y espacios, y debe tener al menos 2 caracteres.
     * </p>
     *
     * @param nombre nombre completo a validar
     * @throws NombreException si el nombre no cumple alguna de las reglas
     */
    public static void verificarNombre(String nombre) throws NombreException {
        if (nombre == null || nombre.isBlank()) throw new NombreException();
        if (!nombre.trim().matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")) throw new NombreException();
        if (nombre.trim().length() < 2) throw new NombreException();
    }

    /**
     * Verifica que el número de teléfono sea válido.
     * <p>
     * El teléfono puede iniciar con {@code +} seguido de entre 7 y 15 dígitos.
     * </p>
     *
     * @param telefono número de teléfono a validar
     * @throws TelefonoException si el teléfono no cumple el formato esperado
     */
    public static void verificarTelefono(String telefono) throws TelefonoException {
        if (telefono == null || telefono.isBlank()) throw new TelefonoException();
        if (!telefono.matches("^\\+?[0-9]{7,15}$")) throw new TelefonoException();
    }

    /**
     * Verifica que el nombre de la ciudad sea válido.
     * <p>
     * La ciudad no puede ser nula ni vacía, solo puede contener letras
     * (incluyendo tildes y ñ) y espacios, y debe tener al menos 2 caracteres.
     * </p>
     *
     * @param ciudad nombre de la ciudad a validar
     * @throws CiudadException si la ciudad no cumple alguna de las reglas
     */
    public static void verificarCiudad(String ciudad) throws CiudadException {
        if (ciudad == null || ciudad.isBlank()) throw new CiudadException();
        if (!ciudad.trim().matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")) throw new CiudadException();
        if (ciudad.trim().length() < 2) throw new CiudadException();
    }

    /**
     * Verifica que la dirección sea válida.
     * <p>
     * La dirección no puede ser nula ni vacía, debe contener al menos una letra
     * y un número, puede incluir espacios, almohadillas y guiones, y debe tener
     * al menos 5 caracteres.
     * </p>
     *
     * @param direccion dirección a validar
     * @throws DireccionException si la dirección no cumple alguna de las reglas
     */
    public static void verificarDireccion(String direccion) throws DireccionException {
        if (direccion == null || direccion.isBlank()) throw new DireccionException();
        if (!direccion.trim().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z0-9 #\\-]+$")) throw new DireccionException();
        if (direccion.trim().length() < 5) throw new DireccionException();
    }

    /**
     * Verifica que la edad del usuario sea válida para realizar una adopción.
     * <p>
     * La edad debe estar entre 18 y 100 años inclusive.
     * </p>
     *
     * @param edad edad del usuario a validar
     * @throws EdadException si la edad es menor de 18 o mayor de 100
     */
    public static void verificarEdad(int edad) throws EdadException {
        if (edad < 18) throw new EdadException();
        if (edad > 100) throw new EdadException();
    }

    /**
     * Verifica que el nombre del animal sea válido.
     * <p>
     * El nombre no puede ser nulo ni vacío, solo puede contener letras
     * (incluyendo tildes y ñ) y espacios, y debe tener al menos 2 caracteres.
     * </p>
     *
     * @param nombre nombre del animal a validar
     * @throws NombreException si el nombre no cumple alguna de las reglas
     */
    public static void verificarNombreAnimal(String nombre) throws NombreException {
        if (nombre == null || nombre.isBlank()) throw new NombreException();
        if (!nombre.trim().matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")) throw new NombreException();
        if (nombre.trim().length() < 2) throw new NombreException();
    }

    /**
     * Verifica que la especie del animal sea válida.
     * <p>
     * La especie no puede ser nula ni vacía, solo puede contener letras
     * (incluyendo tildes y ñ) y espacios, y debe tener al menos 2 caracteres.
     * </p>
     *
     * @param especie especie del animal a validar
     * @throws EspecieException si la especie no cumple alguna de las reglas
     */
    public static void verificarEspecie(String especie) throws EspecieException {
        if (especie == null || especie.isBlank()) throw new EspecieException();
        if (!especie.trim().matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")) throw new EspecieException();
        if (especie.trim().length() < 2) throw new EspecieException();
    }

    /**
     * Verifica que la raza del animal sea válida.
     * <p>
     * La raza no puede ser nula ni vacía y debe tener al menos 2 caracteres.
     * </p>
     *
     * @param raza raza del animal a validar
     * @throws RazaException si la raza no cumple alguna de las reglas
     */
    public static void verificarRaza(String raza) throws RazaException {
        if (raza == null || raza.isBlank()) throw new RazaException();
        if (raza.trim().length() < 2) throw new RazaException();
    }

    /**
     * Verifica que las observaciones sobre el animal sean válidas.
     * <p>
     * Las observaciones no pueden ser nulas ni vacías, y deben tener entre
     * 10 y 500 caracteres.
     * </p>
     *
     * @param observaciones observaciones a validar
     * @throws ObservacionesException si las observaciones no cumplen alguna de las reglas
     */
    public static void verificarObservaciones(String observaciones) throws ObservacionesException {
        if (observaciones == null || observaciones.isBlank()) throw new ObservacionesException();
        if (observaciones.trim().length() < 10) throw new ObservacionesException();
        if (observaciones.trim().length() > 500) throw new ObservacionesException();
    }

    /**
     * Verifica que la imagen del animal sea válida.
     * <p>
     * La imagen no puede ser nula ni estar vacía, debe ser de tipo
     * {@code image/jpeg} o {@code image/png}, y no puede superar los 5MB.
     * </p>
     *
     * @param imagen archivo de imagen a validar
     * @throws ImagenException si la imagen no cumple alguna de las reglas
     */
    public static void verificarImagen(MultipartFile imagen) throws ImagenException {
        if (imagen == null || imagen.isEmpty()) throw new ImagenException();
        String contentType = imagen.getContentType();
        if (contentType == null ||
            (!contentType.equals("image/jpeg") &&
             !contentType.equals("image/png"))) throw new ImagenException();
        if (imagen.getSize() > 5 * 1024 * 1024) throw new ImagenException();
    }

    /**
     * Verifica que un animal exista en el sistema.
     *
     * @param existe {@code true} si el animal existe
     * @throws AnimalNoEncontradoException si el animal no existe
     */
    public static void verificarAnimalExiste(boolean existe) throws AnimalNoEncontradoException {
        if (!existe) throw new AnimalNoEncontradoException();
    }

    /**
     * Verifica que el resultado de la validación por IA sea aprobado.
     *
     * @param aprobado {@code true} si la validación fue aprobada
     * @throws ValidacionIAException si la validación no fue aprobada
     */
    public static void verificarValidacionIA(boolean aprobado) throws ValidacionIAException {
        if (!aprobado) throw new ValidacionIAException();
    }

    /**
     * Verifica que una solicitud de adopción exista en el sistema.
     *
     * @param existe {@code true} si la solicitud existe
     * @throws SolicitudNoEncontradaException si la solicitud no existe
     */
    public static void verificarSolicitudExiste(boolean existe) throws SolicitudNoEncontradaException {
        if (!existe) throw new SolicitudNoEncontradaException();
    }

    /**
     * Verifica que un animal esté disponible para adopción.
     *
     * @param disponible {@code true} si el animal está disponible
     * @throws AnimalNoDisponibleException si el animal no está disponible
     */
    public static void verificarAnimalDisponible(boolean disponible) throws AnimalNoDisponibleException {
        if (!disponible) throw new AnimalNoDisponibleException();
    }

    /**
     * Verifica que una solicitud de adopción esté en estado pendiente.
     *
     * @param pendiente {@code true} si la solicitud está pendiente
     * @throws SolicitudNoPendienteException si la solicitud no está en estado pendiente
     */
    public static void verificarSolicitudPendiente(boolean pendiente) throws SolicitudNoPendienteException {
        if (!pendiente) throw new SolicitudNoPendienteException();
    }

    /**
     * Verifica que no exista una solicitud de adopción duplicada.
     *
     * @param existe {@code true} si ya existe una solicitud para ese animal y usuario
     * @throws SolicitudDuplicadaException si ya existe una solicitud duplicada
     */
    public static void verificarSolicitudDuplicada(boolean existe) throws SolicitudDuplicadaException {
        if (existe) throw new SolicitudDuplicadaException();
    }

    /**
     * Verifica que una notificación exista en el sistema.
     *
     * @param existe {@code true} si la notificación existe
     * @throws NotificacionNoEncontradaException si la notificación no existe
     */
    public static void verificarNotificacionExiste(boolean existe) throws NotificacionNoEncontradaException {
        if (!existe) throw new NotificacionNoEncontradaException();
    }

    /**
     * Verifica que la especie del animal detectada por IA sea una especie doméstica permitida.
     * <p>
     * Las especies permitidas son: perro, gato, conejo, hamster, ave, pájaro, loro,
     * pez, peces, tortuga, cobayo, cobaya, cuy y hurón. La comparación se realiza
     * normalizando tildes y convirtiendo a minúsculas.
     * </p>
     *
     * @param especie especie detectada por la IA a validar
     * @throws ValidacionIAException si la especie no corresponde a una especie doméstica permitida
     */
    public static void verificarEspecieDomestica(String especie) throws ValidacionIAException {
        List<String> especiesPermitidas = List.of(
            "perro", "gato", "conejo", "hamster", "ave", "pajaro",
            "loro", "pez", "peces", "tortuga", "cobayo", "cobaya", "cuy", "huron"
        );

        String especieNormalizada = especie.toLowerCase()
            .replace("á", "a").replace("é", "e")
            .replace("í", "i").replace("ó", "o")
            .replace("ú", "u").replace("ñ", "n");

        boolean especieValida = especiesPermitidas.stream()
            .anyMatch(e -> especieNormalizada.contains(e));

        if (!especieValida) {
            throw new ValidacionIAException(
                "El animal detectado (" + especie +
                ") no es una especie doméstica permitida para adopción.");
        }
    }
}