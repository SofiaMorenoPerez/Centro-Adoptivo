package co.edu.unbosque.centroadoptivo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import co.edu.unbosque.centroadoptivo.dto.UserDTO;
import co.edu.unbosque.centroadoptivo.entity.User;
import co.edu.unbosque.centroadoptivo.entity.User.Role;
import co.edu.unbosque.centroadoptivo.repository.UserRepository;
import co.edu.unbosque.centroadoptivo.service.UserService;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService — Pruebas unitarias")
class UserServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    // ── Helpers ───────────────────────────────────────────────
    private UserDTO dtoCorrecto() {
        UserDTO dto = new UserDTO();
        dto.setUsername("juanito99");
        dto.setPassword("Segura1@");
        dto.setEmail("juan@correo.com");
        dto.setFullName("Juan Pérez");
        dto.setPhone("3101234567");
        dto.setCity("Bogotá");
        dto.setAddress("Calle 12 #34-56");
        dto.setAge(25);
        return dto;
    }

    private User usuarioEntidad() {
        User u = new User();
        u.setId(1L);
        u.setUsername("juanito99");
        u.setPassword("encoded");
        u.setEmail("juan@correo.com");
        u.setRole(Role.USER);
        return u;
    }

    // ═══════════════════════════════════════════════════════════
    // CREATE
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("create()")
    class CreateTest {

        @Test
        @DisplayName("Debe retornar 0 cuando todos los datos son válidos")
        void create_exitoso() {
            UserDTO dto = dtoCorrecto();
            User entidad = usuarioEntidad();

            when(userRepo.findByUsername(dto.getUsername())).thenReturn(Optional.empty());
            when(userRepo.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
            when(modelMapper.map(dto, User.class)).thenReturn(entidad);
            when(passwordEncoder.encode(dto.getPassword())).thenReturn("encoded");
            when(userRepo.save(any(User.class))).thenReturn(entidad);

            int resultado = userService.create(dto);

            assertEquals(0, resultado);
            verify(userRepo).save(any(User.class));
        }

        @Test
        @DisplayName("Debe retornar 1 con username inválido (null)")
        void create_username_null() {
            UserDTO dto = dtoCorrecto();
            dto.setUsername(null);

            int resultado = userService.create(dto);

            assertEquals(1, resultado);
            verify(userRepo, never()).save(any());
        }

        @Test
        @DisplayName("Debe retornar 1 con username muy corto")
        void create_username_muy_corto() {
            UserDTO dto = dtoCorrecto();
            dto.setUsername("ab");

            int resultado = userService.create(dto);

            assertEquals(1, resultado);
        }

        @Test
        @DisplayName("Debe retornar 2 con password inválida")
        void create_password_invalida() {
            UserDTO dto = dtoCorrecto();
            dto.setPassword("sinmayuscula");

            int resultado = userService.create(dto);

            assertEquals(2, resultado);
        }

        @Test
        @DisplayName("Debe retornar 3 con email inválido")
        void create_email_invalido() {
            UserDTO dto = dtoCorrecto();
            dto.setEmail("noesuncorreo");

            int resultado = userService.create(dto);

            assertEquals(3, resultado);
        }

        @Test
        @DisplayName("Debe retornar 4 con nombre completo inválido")
        void create_nombre_invalido() {
            UserDTO dto = dtoCorrecto();
            dto.setFullName("123NoEsNombre");

            int resultado = userService.create(dto);

            assertEquals(4, resultado);
        }

        @Test
        @DisplayName("Debe retornar 5 con teléfono inválido")
        void create_telefono_invalido() {
            UserDTO dto = dtoCorrecto();
            dto.setPhone("ABC123");

            int resultado = userService.create(dto);

            assertEquals(5, resultado);
        }

        @Test
        @DisplayName("Debe retornar 6 con ciudad inválida")
        void create_ciudad_invalida() {
            UserDTO dto = dtoCorrecto();
            dto.setCity("Ciudad123");

            int resultado = userService.create(dto);

            assertEquals(6, resultado);
        }

        @Test
        @DisplayName("Debe retornar 7 con dirección inválida")
        void create_direccion_invalida() {
            UserDTO dto = dtoCorrecto();
            dto.setAddress("sinNumero");

            int resultado = userService.create(dto);

            assertEquals(7, resultado);
        }

        @Test
        @DisplayName("Debe retornar 8 con edad menor a 18")
        void create_edad_invalida() {
            UserDTO dto = dtoCorrecto();
            dto.setAge(15);

            int resultado = userService.create(dto);

            assertEquals(8, resultado);
        }

        @Test
        @DisplayName("Debe retornar 9 si el username ya está en uso")
        void create_username_duplicado() {
            UserDTO dto = dtoCorrecto();

            when(userRepo.findByUsername(dto.getUsername()))
                .thenReturn(Optional.of(usuarioEntidad()));

            int resultado = userService.create(dto);

            assertEquals(9, resultado);
            verify(userRepo, never()).save(any());
        }

        @Test
        @DisplayName("Debe retornar 10 si el email ya está en uso")
        void create_email_duplicado() {
            UserDTO dto = dtoCorrecto();

            when(userRepo.findByUsername(dto.getUsername())).thenReturn(Optional.empty());
            when(userRepo.findByEmail(dto.getEmail()))
                .thenReturn(Optional.of(usuarioEntidad()));

            int resultado = userService.create(dto);

            assertEquals(10, resultado);
            verify(userRepo, never()).save(any());
        }

        @Test
        @DisplayName("El rol siempre debe ser USER al crear")
        void create_rol_siempre_user() {
            UserDTO dto = dtoCorrecto();
            dto.setRole(Role.ADMIN); // intenta ser admin

            User entidad = usuarioEntidad();
            when(userRepo.findByUsername(dto.getUsername())).thenReturn(Optional.empty());
            when(userRepo.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
            when(modelMapper.map(dto, User.class)).thenReturn(entidad);
            when(passwordEncoder.encode(anyString())).thenReturn("encoded");

            userService.create(dto);

            // Verifica que se guardó con rol USER
            verify(userRepo).save(argThat(u -> u.getRole() == Role.USER));
        }
    }

    // ═══════════════════════════════════════════════════════════
    // GET ALL
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("getAll()")
    class GetAllTest {

        @Test
        @DisplayName("Debe retornar lista con todos los usuarios")
        void getAll_con_usuarios() {
            User u1 = usuarioEntidad();
            User u2 = new User();
            u2.setId(2L);
            u2.setUsername("otro");

            UserDTO dto1 = new UserDTO();
            dto1.setId(1L);
            UserDTO dto2 = new UserDTO();
            dto2.setId(2L);

            when(userRepo.findAll()).thenReturn(List.of(u1, u2));
            when(modelMapper.map(u1, UserDTO.class)).thenReturn(dto1);
            when(modelMapper.map(u2, UserDTO.class)).thenReturn(dto2);

            List<UserDTO> resultado = userService.getAll();

            assertEquals(2, resultado.size());
        }

        @Test
        @DisplayName("Debe retornar lista vacía si no hay usuarios")
        void getAll_vacio() {
            when(userRepo.findAll()).thenReturn(List.of());

            List<UserDTO> resultado = userService.getAll();

            assertTrue(resultado.isEmpty());
        }
    }

    // ═══════════════════════════════════════════════════════════
    // GET BY ID
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("getById()")
    class GetByIdTest {

        @Test
        @DisplayName("Debe retornar UserDTO si existe el usuario")
        void getById_encontrado() {
            User entidad = usuarioEntidad();
            UserDTO dto = new UserDTO();
            dto.setId(1L);

            when(userRepo.findById(1L)).thenReturn(Optional.of(entidad));
            when(modelMapper.map(entidad, UserDTO.class)).thenReturn(dto);

            UserDTO resultado = userService.getById(1L);

            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
        }

        @Test
        @DisplayName("Debe retornar null si el usuario no existe")
        void getById_no_encontrado() {
            when(userRepo.findById(99L)).thenReturn(Optional.empty());

            UserDTO resultado = userService.getById(99L);

            assertNull(resultado);
        }
    }

    // ═══════════════════════════════════════════════════════════
    // GET BY USERNAME
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("getByUsername()")
    class GetByUsernameTest {

        @Test
        @DisplayName("Debe retornar UserDTO si el username existe")
        void getByUsername_encontrado() {
            User entidad = usuarioEntidad();
            UserDTO dto = new UserDTO();
            dto.setUsername("juanito99");

            when(userRepo.findByUsername("juanito99")).thenReturn(Optional.of(entidad));
            when(modelMapper.map(entidad, UserDTO.class)).thenReturn(dto);

            UserDTO resultado = userService.getByUsername("juanito99");

            assertNotNull(resultado);
            assertEquals("juanito99", resultado.getUsername());
        }

        @Test
        @DisplayName("Debe retornar null si el username no existe")
        void getByUsername_no_encontrado() {
            when(userRepo.findByUsername("noexiste")).thenReturn(Optional.empty());

            UserDTO resultado = userService.getByUsername("noexiste");

            assertNull(resultado);
        }
    }

    // ═══════════════════════════════════════════════════════════
    // UPDATE BY ID
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("updateById()")
    class UpdateByIdTest {

        @Test
        @DisplayName("Debe retornar 0 con actualización válida")
        void update_exitoso() {
            User existente = usuarioEntidad();
            UserDTO nuevoDato = new UserDTO();
            nuevoDato.setEmail("nuevo@correo.com");

            when(userRepo.findById(1L)).thenReturn(Optional.of(existente));
            when(userRepo.save(any(User.class))).thenReturn(existente);

            int resultado = userService.updateById(1L, nuevoDato);

            assertEquals(0, resultado);
        }

        @Test
        @DisplayName("Debe retornar 2 si el usuario no existe")
        void update_usuario_no_existe() {
            when(userRepo.findById(99L)).thenReturn(Optional.empty());

            int resultado = userService.updateById(99L, new UserDTO());

            assertEquals(2, resultado);
        }

        @Test
        @DisplayName("Debe retornar 3 si la nueva password es inválida")
        void update_password_invalida() {
            User existente = usuarioEntidad();
            UserDTO nuevoDato = new UserDTO();
            nuevoDato.setPassword("sinmayuscula");

            when(userRepo.findById(1L)).thenReturn(Optional.of(existente));

            int resultado = userService.updateById(1L, nuevoDato);

            assertEquals(3, resultado);
        }

        @Test
        @DisplayName("Debe retornar 4 si el nuevo email es inválido")
        void update_email_invalido() {
            User existente = usuarioEntidad();
            UserDTO nuevoDato = new UserDTO();
            nuevoDato.setEmail("noesmail");

            when(userRepo.findById(1L)).thenReturn(Optional.of(existente));

            int resultado = userService.updateById(1L, nuevoDato);

            assertEquals(4, resultado);
        }

        @Test
        @DisplayName("Debe retornar 5 si la nueva edad es inválida")
        void update_edad_invalida() {
            User existente = usuarioEntidad();
            UserDTO nuevoDato = new UserDTO();
            nuevoDato.setAge(10);

            when(userRepo.findById(1L)).thenReturn(Optional.of(existente));

            int resultado = userService.updateById(1L, nuevoDato);

            assertEquals(5, resultado);
        }

        @Test
        @DisplayName("Debe retornar 1 si el username ya lo usa otro usuario")
        void update_username_duplicado() {
            User existente = usuarioEntidad(); // id=1
            User otro = new User();
            otro.setId(2L);
            otro.setUsername("ocupado");

            UserDTO nuevoDato = new UserDTO();
            nuevoDato.setUsername("ocupado");

            when(userRepo.findById(1L)).thenReturn(Optional.of(existente));
            when(userRepo.findByUsername("ocupado")).thenReturn(Optional.of(otro));

            int resultado = userService.updateById(1L, nuevoDato);

            assertEquals(1, resultado);
        }

        @Test
        @DisplayName("Debe permitir actualizar con el mismo username propio")
        void update_mismo_username_propio() {
            User existente = usuarioEntidad(); // id=1, username=juanito99
            UserDTO nuevoDato = new UserDTO();
            nuevoDato.setUsername("juanito99"); // mismo username

            when(userRepo.findById(1L)).thenReturn(Optional.of(existente));
            // findByUsername devuelve el mismo usuario (mismo id)
            when(userRepo.findByUsername("juanito99")).thenReturn(Optional.of(existente));
            when(userRepo.save(any())).thenReturn(existente);

            int resultado = userService.updateById(1L, nuevoDato);

            assertEquals(0, resultado);
        }
    }

    // ═══════════════════════════════════════════════════════════
    // DELETE BY ID
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTest {

        @Test
        @DisplayName("Debe retornar 0 si el usuario existe y se elimina")
        void delete_exitoso() {
            User existente = usuarioEntidad();
            when(userRepo.findById(1L)).thenReturn(Optional.of(existente));

            int resultado = userService.deleteById(1L);

            assertEquals(0, resultado);
            verify(userRepo).delete(existente);
        }

        @Test
        @DisplayName("Debe retornar 1 si el usuario no existe")
        void delete_no_encontrado() {
            when(userRepo.findById(99L)).thenReturn(Optional.empty());

            int resultado = userService.deleteById(99L);

            assertEquals(1, resultado);
            verify(userRepo, never()).delete(any());
        }
    }

    // ═══════════════════════════════════════════════════════════
    // DELETE BY USERNAME
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("deleteByUsername()")
    class DeleteByUsernameTest {

        @Test
        @DisplayName("Debe retornar 0 si el username existe y se elimina")
        void deleteByUsername_exitoso() {
            User existente = usuarioEntidad();
            when(userRepo.findByUsername("juanito99")).thenReturn(Optional.of(existente));

            int resultado = userService.deleteByUsername("juanito99");

            assertEquals(0, resultado);
            verify(userRepo).delete(existente);
        }

        @Test
        @DisplayName("Debe retornar 1 si el username no existe")
        void deleteByUsername_no_encontrado() {
            when(userRepo.findByUsername("noexiste")).thenReturn(Optional.empty());

            int resultado = userService.deleteByUsername("noexiste");

            assertEquals(1, resultado);
            verify(userRepo, never()).delete(any());
        }
    }

    // ═══════════════════════════════════════════════════════════
    // COUNT Y EXIST
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("count() y exist()")
    class CountExistTest {

        @Test
        @DisplayName("count() debe retornar el número total de usuarios")
        void count_correcto() {
            when(userRepo.count()).thenReturn(5L);

            long resultado = userService.count();

            assertEquals(5L, resultado);
        }

        @Test
        @DisplayName("exist() debe retornar true si el usuario existe")
        void exist_true() {
            when(userRepo.existsById(1L)).thenReturn(true);

            assertTrue(userService.exist(1L));
        }

        @Test
        @DisplayName("exist() debe retornar false si el usuario no existe")
        void exist_false() {
            when(userRepo.existsById(99L)).thenReturn(false);

            assertFalse(userService.exist(99L));
        }
    }

    // ═══════════════════════════════════════════════════════════
    // FIND USERNAME ALREADY TAKEN
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("findUsernameAlreadyTaken()")
    class FindUsernameTakenTest {

        @Test
        @DisplayName("Debe retornar true si el username ya está tomado")
        void username_tomado() {
            when(userRepo.findByUsername("ocupado"))
                .thenReturn(Optional.of(usuarioEntidad()));

            assertTrue(userService.findUsernameAlreadyTaken("ocupado"));
        }

        @Test
        @DisplayName("Debe retornar false si el username está disponible")
        void username_disponible() {
            when(userRepo.findByUsername("libre")).thenReturn(Optional.empty());

            assertFalse(userService.findUsernameAlreadyTaken("libre"));
        }
    }
}