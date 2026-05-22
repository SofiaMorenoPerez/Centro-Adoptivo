package co.edu.unbosque.centroadoptivo.service;

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
import co.edu.unbosque.centroadoptivo.util.AESUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    public SolicitudAdopcionDTO crearSolicitud(Long animalId, String usernameActual)
            throws AnimalNoEncontradoException, AnimalNoDisponibleException,
            SolicitudDuplicadaException, UserNotFoundException {

        LanzadorDeExcepcion.verificarAnimalExiste(animalRepository.existsById(animalId));

        Animal animal = animalRepository.findById(animalId).get();

        LanzadorDeExcepcion.verificarAnimalDisponible(
                animal.getStatus().equals(AnimalStatus.AVAILABLE));

        String encryptedUsername = AESUtil.encrypt(usernameActual);
        LanzadorDeExcepcion.verificarUsuarioExiste(
                userRepository.existsByUsername(encryptedUsername));
        User adopter = userRepository.findByUsername(encryptedUsername).get();

        LanzadorDeExcepcion.verificarSolicitudDuplicada(
                solicitudRepository.existsByAnimalIdAndAdopterIdAndStatus(
                        animalId, adopter.getId(), RequestStatus.PENDING));

        animal.setStatus(AnimalStatus.PENDING);
        animalRepository.save(animal);

        SolicitudAdopcion solicitud = new SolicitudAdopcion(
                animal, adopter, LocalDateTime.now(), RequestStatus.PENDING);

        SolicitudAdopcion guardada = solicitudRepository.save(solicitud);

        notificacionService.enviar(
                "El usuario " + adopter.getUsername()
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

    public SolicitudAdopcionDTO aprobarSolicitud(Long solicitudId)
            throws SolicitudNoEncontradaException, SolicitudNoPendienteException {

        LanzadorDeExcepcion.verificarSolicitudExiste(
                solicitudRepository.existsById(solicitudId));

        SolicitudAdopcion solicitud = solicitudRepository.findById(solicitudId).get();

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
                + " por el usuario " + solicitud.getAdopter().getUsername()
                + " fue aprobada exitosamente",
                animal.getPublisher()
        );

        return convertirADTO(guardada);
    }

    public SolicitudAdopcionDTO rechazarSolicitud(Long solicitudId, String rejectionReason)
            throws SolicitudNoEncontradaException, SolicitudNoPendienteException {

        LanzadorDeExcepcion.verificarSolicitudExiste(
                solicitudRepository.existsById(solicitudId));

        SolicitudAdopcion solicitud = solicitudRepository.findById(solicitudId).get();

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
                + " fue rechazada. Razón: " + rejectionReason,
                solicitud.getAdopter()
        );

        return convertirADTO(guardada);
    }

    public List<SolicitudAdopcionDTO> obtenerPendientes() {
        return solicitudRepository.findByStatus(RequestStatus.PENDING)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public List<SolicitudAdopcionDTO> obtenerPorAdoptante(Long adopterId) {
        return solicitudRepository.findByAdopterId(adopterId)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public List<SolicitudAdopcionDTO> obtenerPorAnimal(Long animalId) {
        return solicitudRepository.findByAnimalId(animalId)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public Long obtenerIdPorUsername(String username) throws UserNotFoundException {
        String encryptedUsername = AESUtil.encrypt(username);
        LanzadorDeExcepcion.verificarUsuarioExiste(
                userRepository.existsByUsername(encryptedUsername));
        return userRepository.findByUsername(encryptedUsername).get().getId();
    }

    private SolicitudAdopcionDTO convertirADTO(SolicitudAdopcion solicitud) {
        SolicitudAdopcionDTO dto = new SolicitudAdopcionDTO();
        dto.setId(solicitud.getId());
        dto.setAnimalId(solicitud.getAnimal().getId());
        dto.setAnimalName(solicitud.getAnimal().getName());
        dto.setAdopterId(solicitud.getAdopter().getId());
        dto.setAdopterUsername(solicitud.getAdopter().getUsername());
        dto.setRequestDate(solicitud.getRequestDate());
        dto.setStatus(solicitud.getStatus());
        dto.setRejectionReason(solicitud.getRejectionReason());
        dto.setResolutionDate(solicitud.getResolutionDate());
        return dto;
    }
}