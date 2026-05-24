package co.edu.unbosque.centroadoptivo;

import co.edu.unbosque.centroadoptivo.dto.UserDTO;
import co.edu.unbosque.centroadoptivo.entity.User;
import co.edu.unbosque.centroadoptivo.repository.UserRepository;
import co.edu.unbosque.centroadoptivo.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para {@link UserService}.
 * <p>
 * Verifica el comportamiento del servicio de usuarios usando Mockito para
 * simular el repositorio {@link UserRepository}, el mapeador {@link ModelMapper}
 * y el codificador de contraseñas {@link PasswordEncoder}, sin necesidad de
 * levantar el contexto de Spring.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    /** Mock del repositorio de usuarios. */
    @Mock private UserRepository userRepo;

    /** Mock del mapeador de entidades a DTOs. */
    @Mock private ModelMapper modelMapper;

    /** Mock del codificador de contraseñas. */
    @Mock private PasswordEncoder passwordEncoder;

    /** Instancia del servicio bajo prueba con dependencias mockeadas. */
    @InjectMocks private UserService userService;

    // ─── Helpers ─────────────────────────────────────────────────────────────

    /**
     * Crea un {@link UserDTO} con datos válidos para usar en los tests.
     * <p>
     * La edad se establece en {@code 0} para omitir la validación de edad
     * en los casos donde no se requiere probarla.
     * </p>
     *
     * @return instancia de {@link UserDTO} con datos válidos predefinidos
     */
    private UserDTO validDTO() {
        UserDTO dto = new UserDTO();
        dto.setUsername("juan123");
        dto.setPassword("Secure#1234");
        dto.setEmail("juan@example.com");
        dto.setFullName("Juan Perez");
        dto.setPhone("+573001234567");
        dto.setCity("Bogota");
        dto.setAddress("Calle 1 #2-34");
        dto.setAge(0);
        return dto;
    }

    /**
     * Crea una entidad {@link User} de muestra con id {@code 1},
     * username {@code "juan123"} y contraseña hasheada.
     *
     * @return instancia de {@link User} con datos predefinidos
     */
    private User sampleEntity() {
        User u = new User();
        u.setId(1L);
        u.setUsername("juan123");
        u.setPassword("hashed");
        return u;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // create()
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Verifica que {@code create} retorna {@code 1} cuando el username está vacío,
     * sin interactuar con el repositorio.
     */
    @Test
    @DisplayName("create: username vacio → 1")
    void create_usernameVacio_returns1() {
        UserDTO dto = validDTO();
        dto.setUsername("");
        assertEquals(1, userService.create(dto));
        verifyNoInteractions(userRepo);
    }

    /**
     * Verifica que {@code create} retorna {@code 1} cuando el username
     * tiene menos de 3 caracteres.
     */
    @Test
    @DisplayName("create: username demasiado corto → 1")
    void create_usernameCorto_returns1() {
        UserDTO dto = validDTO();
        dto.setUsername("ab");
        assertEquals(1, userService.create(dto));
    }

    /**
     * Verifica que {@code create} retorna {@code 1} cuando el username
     * contiene caracteres no permitidos.
     */
    @Test
    @DisplayName("create: username con caracteres invalidos → 1")
    void create_usernameCaracteresInvalidos_returns1() {
        UserDTO dto = validDTO();
        dto.setUsername("ju@n!");
        assertEquals(1, userService.create(dto));
    }

    /**
     * Verifica que {@code create} retorna {@code 2} cuando la contraseña
     * no contiene ninguna letra mayúscula.
     */
    @Test
    @DisplayName("create: password sin mayuscula → 2")
    void create_passwordSinMayuscula_returns2() {
        UserDTO dto = validDTO();
        dto.setPassword("secure#1234");
        assertEquals(2, userService.create(dto));
    }

    /**
     * Verifica que {@code create} retorna {@code 2} cuando la contraseña
     * tiene menos de 8 caracteres.
     */
    @Test
    @DisplayName("create: password muy corta → 2")
    void create_passwordCorta_returns2() {
        UserDTO dto = validDTO();
        dto.setPassword("Ab1!");
        assertEquals(2, userService.create(dto));
    }

    /**
     * Verifica que {@code create} retorna {@code 3} cuando el correo electrónico
     * no tiene un formato válido.
     */
    @Test
    @DisplayName("create: email con formato invalido → 3")
    void create_emailInvalido_returns3() {
        UserDTO dto = validDTO();
        dto.setEmail("no-es-email");
        assertEquals(3, userService.create(dto));
    }

    /**
     * Verifica que {@code create} retorna {@code 4} cuando el nombre completo
     * contiene caracteres numéricos.
     */
    @Test
    @DisplayName("create: nombre con numeros → 4")
    void create_nombreConNumeros_returns4() {
        UserDTO dto = validDTO();
        dto.setFullName("Juan123");
        assertEquals(4, userService.create(dto));
    }

    /**
     * Verifica que {@code create} retorna {@code 5} cuando el teléfono
     * no tiene un formato válido.
     */
    @Test
    @DisplayName("create: telefono invalido → 5")
    void create_telefonoInvalido_returns5() {
        UserDTO dto = validDTO();
        dto.setFullName(null);
        dto.setPhone("abc");
        assertEquals(5, userService.create(dto));
    }

    /**
     * Verifica que {@code create} retorna {@code 6} cuando la ciudad
     * contiene caracteres numéricos.
     */
    @Test
    @DisplayName("create: ciudad con numeros → 6")
    void create_ciudadInvalida_returns6() {
        UserDTO dto = validDTO();
        dto.setFullName(null);
        dto.setPhone(null);
        dto.setCity("Bogota123");
        assertEquals(6, userService.create(dto));
    }

    /**
     * Verifica que {@code create} retorna {@code 7} cuando la dirección
     * tiene menos de 5 caracteres.
     */
    @Test
    @DisplayName("create: direccion muy corta → 7")
    void create_direccionInvalida_returns7() {
        UserDTO dto = validDTO();
        dto.setFullName(null);
        dto.setPhone(null);
        dto.setCity(null);
        dto.setAddress("ab");
        assertEquals(7, userService.create(dto));
    }

    /**
     * Verifica que {@code create} retorna {@code 8} cuando la edad del usuario
     * es menor de 18 años.
     */
    @Test
    @DisplayName("create: edad menor de 18 → 8")
    void create_edadMenorDeEeighteen_returns8() {
        UserDTO dto = validDTO();
        dto.setFullName(null);
        dto.setPhone(null);
        dto.setCity(null);
        dto.setAddress(null);
        dto.setAge(15);
        assertEquals(8, userService.create(dto));
    }

    /**
     * Verifica que {@code create} retorna {@code 9} cuando el username
     * ya está registrado en el sistema, sin guardar ningún usuario.
     */
    @Test
    @DisplayName("create: username ya tomado → 9")
    void create_usernameTomado_returns9() {
        UserDTO dto = validDTO();
        when(userRepo.findByUsername("juan123")).thenReturn(Optional.of(sampleEntity()));
        assertEquals(9, userService.create(dto));
        verify(userRepo, never()).save(any());
    }

    /**
     * Verifica que {@code create} retorna {@code 10} cuando el correo electrónico
     * ya está registrado en el sistema, sin guardar ningún usuario.
     */
    @Test
    @DisplayName("create: email ya registrado → 10")
    void create_emailDuplicado_returns10() {
        UserDTO dto = validDTO();
        when(userRepo.findByUsername("juan123")).thenReturn(Optional.empty());
        when(userRepo.findByEmail("juan@example.com")).thenReturn(Optional.of(sampleEntity()));
        assertEquals(10, userService.create(dto));
        verify(userRepo, never()).save(any());
    }

    /**
     * Verifica que {@code create} retorna {@code 0} con datos válidos, guarda el usuario
     * con todos los campos de seguridad activos, el rol {@link User.Role#USER},
     * fecha de registro asignada y la contraseña codificada.
     */
    @Test
    @DisplayName("create: datos validos → 0 y guarda con campos de seguridad")
    void create_datosValidos_returns0YGuarda() {
        UserDTO dto = validDTO();
        dto.setEmail(null);
        User entity = sampleEntity();
        when(userRepo.findByUsername("juan123")).thenReturn(Optional.empty());
        when(modelMapper.map(dto, User.class)).thenReturn(entity);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("hashed");

        int result = userService.create(dto);

        assertEquals(0, result);
        verify(userRepo).save(entity);
        assertTrue(entity.isAccountNonExpired());
        assertTrue(entity.isAccountNonLocked());
        assertTrue(entity.isCredentialsNonExpired());
        assertTrue(entity.isEnabled());
        assertEquals(User.Role.USER, entity.getRole());
        assertNotNull(entity.getRegistrationDate());
        assertEquals("hashed", entity.getPassword());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // getAll()
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Verifica que {@code getAll} retorna una lista con el DTO mapeado
     * cuando el repositorio contiene un usuario.
     */
    @Test
    @DisplayName("getAll: repositorio con un usuario → lista con un DTO")
    void getAll_conUsuario_retornaLista() {
        User u = sampleEntity();
        UserDTO dto = new UserDTO();
        dto.setUsername("juan123");
        when(userRepo.findAll()).thenReturn(List.of(u));
        when(modelMapper.map(u, UserDTO.class)).thenReturn(dto);

        List<UserDTO> result = userService.getAll();

        assertEquals(1, result.size());
        assertEquals("juan123", result.get(0).getUsername());
    }

    /**
     * Verifica que {@code getAll} retorna una lista vacía
     * cuando el repositorio no tiene usuarios.
     */
    @Test
    @DisplayName("getAll: repositorio vacio → lista vacia")
    void getAll_vacio_retornaListaVacia() {
        when(userRepo.findAll()).thenReturn(List.of());
        assertTrue(userService.getAll().isEmpty());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // getById() / getByUsername()
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Verifica que {@code getById} retorna un DTO no nulo cuando el usuario existe.
     */
    @Test
    @DisplayName("getById: usuario existe → retorna DTO")
    void getById_existe_retornaDTO() {
        User u = sampleEntity();
        UserDTO dto = new UserDTO();
        when(userRepo.findById(1L)).thenReturn(Optional.of(u));
        when(modelMapper.map(u, UserDTO.class)).thenReturn(dto);
        assertNotNull(userService.getById(1L));
    }

    /**
     * Verifica que {@code getById} retorna {@code null}
     * cuando el usuario no existe en el repositorio.
     */
    @Test
    @DisplayName("getById: no existe → null")
    void getById_noExiste_retornaNull() {
        when(userRepo.findById(99L)).thenReturn(Optional.empty());
        assertNull(userService.getById(99L));
    }

    /**
     * Verifica que {@code getByUsername} retorna un DTO no nulo
     * cuando el usuario existe en el repositorio.
     */
    @Test
    @DisplayName("getByUsername: encontrado → DTO")
    void getByUsername_encontrado_retornaDTO() {
        User u = sampleEntity();
        UserDTO dto = new UserDTO();
        when(userRepo.findByUsername("juan123")).thenReturn(Optional.of(u));
        when(modelMapper.map(u, UserDTO.class)).thenReturn(dto);
        assertNotNull(userService.getByUsername("juan123"));
    }

    /**
     * Verifica que {@code getByUsername} retorna {@code null}
     * cuando el usuario no existe en el repositorio.
     */
    @Test
    @DisplayName("getByUsername: no encontrado → null")
    void getByUsername_noEncontrado_retornaNull() {
        when(userRepo.findByUsername("nadie")).thenReturn(Optional.empty());
        assertNull(userService.getByUsername("nadie"));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // updateById()
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Verifica que {@code updateById} retorna {@code 2}
     * cuando el id del usuario no existe en el repositorio.
     */
    @Test
    @DisplayName("updateById: id no encontrado → 2")
    void updateById_noEncontrado_returns2() {
        when(userRepo.findById(99L)).thenReturn(Optional.empty());
        assertEquals(2, userService.updateById(99L, new UserDTO()));
    }

    /**
     * Verifica que {@code updateById} retorna {@code 3}
     * cuando la nueva contraseña no cumple los requisitos de validación.
     */
    @Test
    @DisplayName("updateById: nueva password invalida → 3")
    void updateById_passwordInvalida_returns3() {
        UserDTO newData = new UserDTO();
        newData.setPassword("weak");
        when(userRepo.findById(1L)).thenReturn(Optional.of(sampleEntity()));
        assertEquals(3, userService.updateById(1L, newData));
    }

    /**
     * Verifica que {@code updateById} retorna {@code 4}
     * cuando el nuevo correo electrónico no tiene un formato válido.
     */
    @Test
    @DisplayName("updateById: email invalido → 4")
    void updateById_emailInvalido_returns4() {
        UserDTO newData = new UserDTO();
        newData.setEmail("mal-email");
        when(userRepo.findById(1L)).thenReturn(Optional.of(sampleEntity()));
        assertEquals(4, userService.updateById(1L, newData));
    }

    /**
     * Verifica que {@code updateById} retorna {@code 5}
     * cuando la nueva edad es menor de 18 años.
     */
    @Test
    @DisplayName("updateById: edad invalida → 5")
    void updateById_edadInvalida_returns5() {
        UserDTO newData = new UserDTO();
        newData.setAge(10);
        when(userRepo.findById(1L)).thenReturn(Optional.of(sampleEntity()));
        assertEquals(5, userService.updateById(1L, newData));
    }

    /**
     * Verifica que {@code updateById} retorna {@code 1} y no guarda cambios
     * cuando el nuevo username ya está en uso por otro usuario diferente.
     */
    @Test
    @DisplayName("updateById: username en conflicto con otro usuario → 1")
    void updateById_usernameConflicto_returns1() {
        User existing = sampleEntity();
        User otro = new User();
        otro.setId(2L);
        otro.setUsername("pepe99");

        UserDTO newData = new UserDTO();
        newData.setUsername("pepe99");

        when(userRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepo.findByUsername("pepe99")).thenReturn(Optional.of(otro));

        assertEquals(1, userService.updateById(1L, newData));
        verify(userRepo, never()).save(any());
    }

    /**
     * Verifica que {@code updateById} retorna {@code 0} y guarda los cambios
     * cuando el nuevo username pertenece al mismo usuario (sin conflicto).
     */
    @Test
    @DisplayName("updateById: username mismo usuario (no conflicto) → 0")
    void updateById_usernameMismoUsuario_returns0() {
        User existing = sampleEntity();

        UserDTO newData = new UserDTO();
        newData.setUsername("juan123");

        when(userRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepo.findByUsername("juan123")).thenReturn(Optional.of(existing));

        assertEquals(0, userService.updateById(1L, newData));
        verify(userRepo).save(existing);
    }

    /**
     * Verifica que {@code updateById} retorna {@code 0}, guarda los cambios
     * y actualiza el campo {@code fullName} cuando solo se modifica el nombre.
     */
    @Test
    @DisplayName("updateById: solo nombre → 0 y actualiza campo")
    void updateById_soloNombre_returns0YActualiza() {
        User existing = sampleEntity();
        UserDTO newData = new UserDTO();
        newData.setFullName("Juan Camilo");

        when(userRepo.findById(1L)).thenReturn(Optional.of(existing));

        assertEquals(0, userService.updateById(1L, newData));
        verify(userRepo).save(existing);
        assertEquals("Juan Camilo", existing.getFullName());
    }

    /**
     * Verifica que {@code updateById} codifica y guarda la nueva contraseña
     * cuando esta es válida.
     */
    @Test
    @DisplayName("updateById: password valida → se codifica y guarda")
    void updateById_passwordValida_seCodifica() {
        User existing = sampleEntity();
        UserDTO newData = new UserDTO();
        newData.setPassword("NuevaPass#99");

        when(userRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("NuevaPass#99")).thenReturn("newHashed");

        assertEquals(0, userService.updateById(1L, newData));
        assertEquals("newHashed", existing.getPassword());
        verify(userRepo).save(existing);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // deleteById()
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Verifica que {@code deleteById} retorna {@code 0} e invoca {@code delete}
     * cuando el usuario con el id indicado existe.
     */
    @Test
    @DisplayName("deleteById: existe → 0 y llama delete")
    void deleteById_existe_returns0() {
        User u = sampleEntity();
        when(userRepo.findById(1L)).thenReturn(Optional.of(u));
        assertEquals(0, userService.deleteById(1L));
        verify(userRepo).delete(u);
    }

    /**
     * Verifica que {@code deleteById} retorna {@code 1} y no invoca {@code delete}
     * cuando el usuario no existe en el repositorio.
     */
    @Test
    @DisplayName("deleteById: no existe → 1 sin delete")
    void deleteById_noExiste_returns1() {
        when(userRepo.findById(99L)).thenReturn(Optional.empty());
        assertEquals(1, userService.deleteById(99L));
        verify(userRepo, never()).delete(any());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // deleteByUsername()
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Verifica que {@code deleteByUsername} retorna {@code 0} e invoca {@code delete}
     * cuando el usuario con el username indicado existe.
     */
    @Test
    @DisplayName("deleteByUsername: existe → 0")
    void deleteByUsername_existe_returns0() {
        User u = sampleEntity();
        when(userRepo.findByUsername("juan123")).thenReturn(Optional.of(u));
        assertEquals(0, userService.deleteByUsername("juan123"));
        verify(userRepo).delete(u);
    }

    /**
     * Verifica que {@code deleteByUsername} retorna {@code 1} y no invoca {@code delete}
     * cuando el usuario no existe en el repositorio.
     */
    @Test
    @DisplayName("deleteByUsername: no existe → 1")
    void deleteByUsername_noExiste_returns1() {
        when(userRepo.findByUsername("nadie")).thenReturn(Optional.empty());
        assertEquals(1, userService.deleteByUsername("nadie"));
        verify(userRepo, never()).delete(any());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // count() / exist() / findUsernameAlreadyTaken()
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Verifica que {@code count} retorna el valor proporcionado por el repositorio.
     */
    @Test
    @DisplayName("count: retorna valor del repo")
    void count_retornaValor() {
        when(userRepo.count()).thenReturn(7L);
        assertEquals(7L, userService.count());
    }

    /**
     * Verifica que {@code exist} retorna {@code true}
     * cuando el id existe en el repositorio.
     */
    @Test
    @DisplayName("exist: id presente → true")
    void exist_presente_returnsTrue() {
        when(userRepo.existsById(1L)).thenReturn(true);
        assertTrue(userService.exist(1L));
    }

    /**
     * Verifica que {@code exist} retorna {@code false}
     * cuando el id no existe en el repositorio.
     */
    @Test
    @DisplayName("exist: id ausente → false")
    void exist_ausente_returnsFalse() {
        when(userRepo.existsById(99L)).thenReturn(false);
        assertFalse(userService.exist(99L));
    }

    /**
     * Verifica que {@code findUsernameAlreadyTaken} retorna {@code true}
     * cuando el username ya está en uso por algún usuario.
     */
    @Test
    @DisplayName("findUsernameAlreadyTaken: tomado → true")
    void findUsernameAlreadyTaken_tomado_returnsTrue() {
        when(userRepo.findByUsername("juan123")).thenReturn(Optional.of(sampleEntity()));
        assertTrue(userService.findUsernameAlreadyTaken("juan123"));
    }

    /**
     * Verifica que {@code findUsernameAlreadyTaken} retorna {@code false}
     * cuando el username está disponible.
     */
    @Test
    @DisplayName("findUsernameAlreadyTaken: libre → false")
    void findUsernameAlreadyTaken_libre_returnsFalse() {
        when(userRepo.findByUsername("libre")).thenReturn(Optional.empty());
        assertFalse(userService.findUsernameAlreadyTaken("libre"));
    }
}