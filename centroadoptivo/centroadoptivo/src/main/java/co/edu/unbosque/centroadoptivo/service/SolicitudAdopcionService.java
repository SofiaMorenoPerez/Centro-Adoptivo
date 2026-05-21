package co.edu.unbosque.centroadoptivo.service;

import co.edu.unbosque.centroadoptivo.dto.SolicitudAdopcionDTO;
import co.edu.unbosque.centroadoptivo.entity.Animal;
import co.edu.unbosque.centroadoptivo.entity.Animal.AnimalStatus;
import co.edu.unbosque.centroadoptivo.entity.SolicitudAdopcion;
import co.edu.unbosque.centroadoptivo.entity.SolicitudAdopcion.RequestStatus;
import co.edu.unbosque.centroadoptivo.entity.User;
import co.edu.unbosque.centroadoptivo.repository.AnimalRepository;
import co.edu.unbosque.centroadoptivo.repository.SolicitudAdopcionRepository;
import co.edu.unbosque.centroadoptivo.repository.UserRepository;
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

    // Adoptante crea una solicitud
    public SolicitudAdopcionDTO crearSolicitud(Long animalId, Long adopterId) {

        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new RuntimeException("Animal no encontrado"));

        User adopter = userRepository.findById(adopterId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!animal.getStatus().equals(AnimalStatus.AVAILABLE)) {
            throw new RuntimeException("El animal no está disponible para adopción");
        }

        if (solicitudRepository.existsByAnimalIdAndAdopterIdAndStatus(
                animalId, adopterId, RequestStatus.PENDING)) {
            throw new RuntimeException("Ya tienes una solicitud pendiente para este animal");
        }

        animal.setStatus(AnimalStatus.PENDING);
        animalRepository.save(animal);

        SolicitudAdopcion solicitud = new SolicitudAdopcion(
                animal, adopter, LocalDateTime.now(), RequestStatus.PENDING);

        return convertirADTO(solicitudRepository.save(solicitud));
    }

    public SolicitudAdopcionDTO aprobarSolicitud(Long solicitudId) {

        SolicitudAdopcion solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (!solicitud.getStatus().equals(RequestStatus.PENDING)) {
            throw new RuntimeException("La solicitud no está en estado pendiente");
        }

        solicitud.setStatus(RequestStatus.APPROVED);
        solicitud.setResolutionDate(LocalDateTime.now());

        Animal animal = solicitud.getAnimal();
        animal.setStatus(AnimalStatus.ADOPTED);
        animal.setAdopter(solicitud.getAdopter());
        animalRepository.save(animal);

        return convertirADTO(solicitudRepository.save(solicitud));
    }

    public SolicitudAdopcionDTO rechazarSolicitud(Long solicitudId, String rejectionReason) {

        SolicitudAdopcion solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (!solicitud.getStatus().equals(RequestStatus.PENDING)) {
            throw new RuntimeException("La solicitud no está en estado pendiente");
        }

        solicitud.setStatus(RequestStatus.REJECTED);
        solicitud.setRejectionReason(rejectionReason);
        solicitud.setResolutionDate(LocalDateTime.now());

        Animal animal = solicitud.getAnimal();
        animal.setStatus(AnimalStatus.AVAILABLE);
        animalRepository.save(animal);

        return convertirADTO(solicitudRepository.save(solicitud));
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