package co.edu.unbosque.centroadoptivo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.centroadoptivo.dto.SolicitudAdopcionDTO;
import co.edu.unbosque.centroadoptivo.entity.Animal;
import co.edu.unbosque.centroadoptivo.entity.Animal.AnimalStatus;
import co.edu.unbosque.centroadoptivo.entity.SolicitudAdopcion;
import co.edu.unbosque.centroadoptivo.entity.SolicitudAdopcion.RequestStatus;
import co.edu.unbosque.centroadoptivo.entity.User;
import co.edu.unbosque.centroadoptivo.exception.AnimalNoDisponibleException;
import co.edu.unbosque.centroadoptivo.exception.AnimalNoEncontradoException;
import co.edu.unbosque.centroadoptivo.exception.LanzadorDeExcepcion;
import co.edu.unbosque.centroadoptivo.exception.SolicitudDuplicadaException;
import co.edu.unbosque.centroadoptivo.exception.SolicitudNoEncontradaException;
import co.edu.unbosque.centroadoptivo.exception.SolicitudNoPendienteException;
import co.edu.unbosque.centroadoptivo.exception.UserNotFoundException;
import co.edu.unbosque.centroadoptivo.repository.AnimalRepository;
import co.edu.unbosque.centroadoptivo.repository.SolicitudAdopcionRepository;
import co.edu.unbosque.centroadoptivo.repository.UserRepository;

/**
 * Servicio para gestionar las solicitudes de adopción del sistema.
 * Maneja el ciclo completo de una solicitud: creación, aprobación y rechazo,
 * notificando a los usuarios involucrados en cada etapa.
 *
 * @author Centro Adoptivo Unbosque
 * @version 1.0
 */
@Service
public class SolicitudAdopcionService {

    @Autowired
    private SolicitudAdopcionRepository solicitudRepository;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificacionService notificacionService;

    /**
     * Obtiene el username de un usuario.
     *
     * @param user usuario del que se obtiene el username
     * @return username del usuario
     */
    private String decryptUsername(User user) {
        return user.getUsername();
    }

    /**
     * Crea una solicitud de adopción para un animal específico.
     * Verifica que el animal esté disponible, que el usuario no sea
     * el mismo que publicó el animal, y que no exista una solicitud
     * pendiente previa. Notifica tanto al adoptante como al publicador.
     *
     * @param animalId       identificador del animal a adoptar
     * @param usernameActual username del usuario que solicita la adopción
     * @return {@link SolicitudAdopcionDTO} con los datos de la solicitud creada
     * @throws AnimalNoEncontradoException  si el animal no existe
     * @throws AnimalNoDisponibleException  si el animal no está disponible
     *                                      o si el adoptante es el mismo publicador
     * @throws SolicitudDuplicadaException  si ya existe una solicitud pendiente
     *                                      del mismo usuario para ese animal
     * @throws UserNotFoundException        si el usuario no existe
     */
    public SolicitudAdopcionDTO crearSolicitud(Long animalId, String usernameActual)
            throws AnimalNoEncontradoException, AnimalNoDisponibleException,
            SolicitudDuplicadaException, UserNotFoundException {

        LanzadorDeExcepcion.verificarAnimalExiste(
                animalRepository.existsById(animalId));
        Animal animal = animalRepository.findById(animalId).get();

        LanzadorDeExcepcion.verificarAnimalDisponible(
                animal.getStatus().equals(AnimalStatus.AVAILABLE));

        LanzadorDeExcepcion.verificarUsuarioExiste(
                userRepository.existsByUsername(usernameActual));
        User adopter = userRepository.findByUsername(usernameActual).get();

        if (animal.getPublisher().getId().equals(adopter.getId())) {
            throw new AnimalNoDisponibleException();
        }

        LanzadorDeExcepcion.verificarSolicitudDuplicada(
                solicitudRepository.existsByAnimalIdAndAdopterIdAndStatus(
                        animalId, adopter.getId(), RequestStatus.PENDING));

        animal.setStatus(AnimalStatus.PENDING);
        animalRepository.save(animal);

        SolicitudAdopcion solicitud = new SolicitudAdopcion(
                animal, adopter, LocalDateTime.now(), RequestStatus.PENDING);
        SolicitudAdopcion guardada = solicitudRepository.save(solicitud);

        notificacionService.enviar(
                "El usuario " + decryptUsername(adopter)
                + " ha solicitado adoptar a " + animal.getName(),
                animal.getPublisher()
        );

        notificacionService.enviar(
                "Tu solicitud para adoptar a " + animal.getName()
                + " ha sido enviada y está pendiente de aprobación",
                adopter
        );

        return convertirADTO(guardada);
    }

    /**
     * Aprueba una solicitud de adopción pendiente.
     * Cambia el estado del animal a ADOPTED, asigna el adoptante
     * y notifica a ambos usuarios involucrados.
     *
     * @param solicitudId identificador de la solicitud a aprobar
     * @return {@link SolicitudAdopcionDTO} con los datos actualizados
     * @throws SolicitudNoEncontradaException si la solicitud no existe
     * @throws SolicitudNoPendienteException  si la solicitud no está en estado PENDING
     */
    public SolicitudAdopcionDTO aprobarSolicitud(Long solicitudId)
            throws SolicitudNoEncontradaException, SolicitudNoPendienteException {

        LanzadorDeExcepcion.verificarSolicitudExiste(
                solicitudRepository.existsById(solicitudId));
        SolicitudAdopcion solicitud =
                solicitudRepository.findById(solicitudId).get();

        LanzadorDeExcepcion.verificarSolicitudPendiente(
                solicitud.getStatus().equals(RequestStatus.PENDING));

        solicitud.setStatus(RequestStatus.APPROVED);
        solicitud.setResolutionDate(LocalDateTime.now());

        Animal animal = solicitud.getAnimal();
        animal.setStatus(AnimalStatus.ADOPTED);
        animal.setAdopter(solicitud.getAdopter());
        animalRepository.save(animal);

        SolicitudAdopcion guardada = solicitudRepository.save(solicitud);

        notificacionService.enviar(
                "¡Felicitaciones! Tu solicitud para adoptar a "
                + animal.getName() + " fue aprobada",
                solicitud.getAdopter()
        );

        notificacionService.enviar(
                "La adopción de " + animal.getName()
                + " por el usuario " + decryptUsername(solicitud.getAdopter())
                + " fue aprobada exitosamente",
                animal.getPublisher()
        );

        return convertirADTO(guardada);
    }

    /**
     * Rechaza una solicitud de adopción pendiente.
     * Devuelve el animal al estado AVAILABLE, registra el motivo
     * de rechazo y notifica a ambos usuarios involucrados.
     *
     * @param solicitudId     identificador de la solicitud a rechazar
     * @param rejectionReason motivo del rechazo
     * @return {@link SolicitudAdopcionDTO} con los datos actualizados
     * @throws SolicitudNoEncontradaException si la solicitud no existe
     * @throws SolicitudNoPendienteException  si la solicitud no está en estado PENDING
     */
    public SolicitudAdopcionDTO rechazarSolicitud(Long solicitudId, String rejectionReason)
            throws SolicitudNoEncontradaException, SolicitudNoPendienteException {

        LanzadorDeExcepcion.verificarSolicitudExiste(
                solicitudRepository.existsById(solicitudId));
        SolicitudAdopcion solicitud =
                solicitudRepository.findById(solicitudId).get();

        LanzadorDeExcepcion.verificarSolicitudPendiente(
                solicitud.getStatus().equals(RequestStatus.PENDING));

        solicitud.setStatus(RequestStatus.REJECTED);
        solicitud.setRejectionReason(rejectionReason);
        solicitud.setResolutionDate(LocalDateTime.now());

        Animal animal = solicitud.getAnimal();
        animal.setStatus(AnimalStatus.AVAILABLE);
        animalRepository.save(animal);

        SolicitudAdopcion guardada = solicitudRepository.save(solicitud);

        notificacionService.enviar(
                "Tu solicitud para adoptar a " + animal.getName()
                + " fue rechazada. Motivo: " + rejectionReason,
                solicitud.getAdopter()
        );

        notificacionService.enviar(
                "La solicitud de adopción de " + animal.getName()
                + " por el usuario " + decryptUsername(solicitud.getAdopter())
                + " fue rechazada",
                animal.getPublisher()
        );

        return convertirADTO(guardada);
    }

    /**
     * Obtiene todas las solicitudes de adopción en estado PENDING.
     * Usado por el administrador para revisar y gestionar las solicitudes.
     *
     * @return lista de {@link SolicitudAdopcionDTO} con estado PENDING
     */
    public List<SolicitudAdopcionDTO> obtenerPendientes() {
        return solicitudRepository.findByStatus(RequestStatus.PENDING)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * Obtiene todas las solicitudes de adopción de un adoptante específico.
     *
     * @param adopterId identificador del adoptante
     * @return lista de {@link SolicitudAdopcionDTO} del adoptante
     */
    public List<SolicitudAdopcionDTO> obtenerPorAdoptante(Long adopterId) {
        return solicitudRepository.findByAdopterId(adopterId)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * Obtiene todas las solicitudes de adopción asociadas a un animal específico.
     *
     * @param animalId identificador del animal
     * @return lista de {@link SolicitudAdopcionDTO} del animal
     */
    public List<SolicitudAdopcionDTO> obtenerPorAnimal(Long animalId) {
        return solicitudRepository.findByAnimalId(animalId)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * Obtiene el identificador de un usuario a partir de su username.
     *
     * @param username username del usuario
     * @return identificador del usuario
     * @throws UserNotFoundException si el usuario no existe
     */
    public Long obtenerIdPorUsername(String username) throws UserNotFoundException {
        LanzadorDeExcepcion.verificarUsuarioExiste(
                userRepository.existsByUsername(username));
        return userRepository.findByUsername(username).get().getId();
    }

    /**
     * Convierte una entidad {@link SolicitudAdopcion} a su correspondiente
     * {@link SolicitudAdopcionDTO} para ser enviado al frontend.
     *
     * @param solicitud entidad de solicitud a convertir
     * @return {@link SolicitudAdopcionDTO} con los datos de la solicitud
     */
    private SolicitudAdopcionDTO convertirADTO(SolicitudAdopcion solicitud) {
        SolicitudAdopcionDTO dto = new SolicitudAdopcionDTO();
        dto.setId(solicitud.getId());
        dto.setAnimalId(solicitud.getAnimal().getId());
        dto.setAnimalName(solicitud.getAnimal().getName());
        dto.setAdopterId(solicitud.getAdopter().getId());
        dto.setAdopterUsername(decryptUsername(solicitud.getAdopter()));
        dto.setRequestDate(solicitud.getRequestDate());
        dto.setStatus(solicitud.getStatus());
        dto.setRejectionReason(solicitud.getRejectionReason());
        dto.setResolutionDate(solicitud.getResolutionDate());
        return dto;
    }
}