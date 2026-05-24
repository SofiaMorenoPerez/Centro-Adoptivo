package co.edu.unbosque.centroadoptivo;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import co.edu.unbosque.centroadoptivo.exception.AnimalNoDisponibleException;
import co.edu.unbosque.centroadoptivo.exception.AnimalNoEncontradoException;
import co.edu.unbosque.centroadoptivo.exception.CiudadException;
import co.edu.unbosque.centroadoptivo.exception.DireccionException;
import co.edu.unbosque.centroadoptivo.exception.EdadException;
import co.edu.unbosque.centroadoptivo.exception.EmailException;
import co.edu.unbosque.centroadoptivo.exception.EspecieException;
import co.edu.unbosque.centroadoptivo.exception.ImagenException;
import co.edu.unbosque.centroadoptivo.exception.LanzadorDeExcepcion;
import co.edu.unbosque.centroadoptivo.exception.NombreException;
import co.edu.unbosque.centroadoptivo.exception.NotificacionNoEncontradaException;
import co.edu.unbosque.centroadoptivo.exception.ObservacionesException;
import co.edu.unbosque.centroadoptivo.exception.PasswordNotValidException;
import co.edu.unbosque.centroadoptivo.exception.RazaException;
import co.edu.unbosque.centroadoptivo.exception.SolicitudDuplicadaException;
import co.edu.unbosque.centroadoptivo.exception.SolicitudNoEncontradaException;
import co.edu.unbosque.centroadoptivo.exception.SolicitudNoPendienteException;
import co.edu.unbosque.centroadoptivo.exception.TelefonoException;
import co.edu.unbosque.centroadoptivo.exception.UserNotFoundException;
import co.edu.unbosque.centroadoptivo.exception.UsernameException;
import co.edu.unbosque.centroadoptivo.exception.ValidacionIAException;

@DisplayName("LanzadorDeExcepcion — Validaciones de negocio")
class LanzadorDeExcepcionTest {

    // ═══════════════════════════════════════════════════════════
    // USERNAME
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("verificarUsername()")
    class VerificarUsernameTest {

        @Test
        @DisplayName("Debe pasar con username válido simple")
        void username_valido() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarUsername("juan123"));
        }

        @Test
        @DisplayName("Debe pasar con punto y guión bajo")
        void username_con_punto_y_guion() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarUsername("juan.perez_99"));
        }

        @Test
        @DisplayName("Debe pasar con exactamente 3 caracteres (límite mínimo)")
        void username_exactamente_tres() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarUsername("abc"));
        }

        @Test
        @DisplayName("Debe lanzar UsernameException si es null")
        void username_null() {
            assertThrows(UsernameException.class, () ->
                LanzadorDeExcepcion.verificarUsername(null));
        }

        @Test
        @DisplayName("Debe lanzar UsernameException si está vacío")
        void username_vacio() {
            assertThrows(UsernameException.class, () ->
                LanzadorDeExcepcion.verificarUsername(""));
        }

        @Test
        @DisplayName("Debe lanzar UsernameException si solo hay espacios")
        void username_solo_espacios() {
            assertThrows(UsernameException.class, () ->
                LanzadorDeExcepcion.verificarUsername("   "));
        }

        @Test
        @DisplayName("Debe lanzar UsernameException si tiene menos de 3 caracteres")
        void username_muy_corto() {
            assertThrows(UsernameException.class, () ->
                LanzadorDeExcepcion.verificarUsername("ab"));
        }

        @Test
        @DisplayName("Debe lanzar UsernameException con @ (carácter no permitido)")
        void username_con_arroba() {
            assertThrows(UsernameException.class, () ->
                LanzadorDeExcepcion.verificarUsername("juan@perez"));
        }

        @Test
        @DisplayName("Debe lanzar UsernameException con espacios internos")
        void username_con_espacios() {
            assertThrows(UsernameException.class, () ->
                LanzadorDeExcepcion.verificarUsername("juan perez"));
        }

        @Test
        @DisplayName("Debe lanzar UsernameException con guión medio")
        void username_con_guion_medio() {
            assertThrows(UsernameException.class, () ->
                LanzadorDeExcepcion.verificarUsername("juan-perez"));
        }
    }

    // ═══════════════════════════════════════════════════════════
    // PASSWORD
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("verificarPassword()")
    class VerificarPasswordTest {

        @Test
        @DisplayName("Debe pasar con contraseña válida compleja")
        void password_valido() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarPassword("Segura1@"));
        }

        @Test
        @DisplayName("Debe pasar con contraseña larga y variada")
        void password_largo_valido() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarPassword("MiClaveSegura123!"));
        }

        @Test
        @DisplayName("Debe lanzar PasswordNotValidException si es null")
        void password_null() {
            assertThrows(PasswordNotValidException.class, () ->
                LanzadorDeExcepcion.verificarPassword(null));
        }

        @Test
        @DisplayName("Debe lanzar PasswordNotValidException si está vacío")
        void password_vacio() {
            assertThrows(PasswordNotValidException.class, () ->
                LanzadorDeExcepcion.verificarPassword(""));
        }

        @Test
        @DisplayName("Debe lanzar PasswordNotValidException si tiene menos de 8 caracteres")
        void password_muy_corto() {
            assertThrows(PasswordNotValidException.class, () ->
                LanzadorDeExcepcion.verificarPassword("Ab1@"));
        }

        @Test
        @DisplayName("Debe lanzar PasswordNotValidException sin mayúscula")
        void password_sin_mayuscula() {
            assertThrows(PasswordNotValidException.class, () ->
                LanzadorDeExcepcion.verificarPassword("segura1@abc"));
        }

        @Test
        @DisplayName("Debe lanzar PasswordNotValidException sin minúscula")
        void password_sin_minuscula() {
            assertThrows(PasswordNotValidException.class, () ->
                LanzadorDeExcepcion.verificarPassword("SEGURA1@ABC"));
        }

        @Test
        @DisplayName("Debe lanzar PasswordNotValidException sin número")
        void password_sin_numero() {
            assertThrows(PasswordNotValidException.class, () ->
                LanzadorDeExcepcion.verificarPassword("Segura@ABC"));
        }

        @Test
        @DisplayName("Debe lanzar PasswordNotValidException sin carácter especial")
        void password_sin_especial() {
            assertThrows(PasswordNotValidException.class, () ->
                LanzadorDeExcepcion.verificarPassword("Segura123ABC"));
        }

        @Test
        @DisplayName("Debe lanzar PasswordNotValidException con menos de 4 caracteres únicos")
        void password_pocos_unicos() {
            assertThrows(PasswordNotValidException.class, () ->
                LanzadorDeExcepcion.verificarPassword("AAAA1!1!"));
        }
    }

    // ═══════════════════════════════════════════════════════════
    // EMAIL
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("verificarEmail()")
    class VerificarEmailTest {

        @Test
        @DisplayName("Debe pasar con email válido estándar")
        void email_valido() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarEmail("usuario@dominio.com"));
        }

        @Test
        @DisplayName("Debe pasar con email con subdominio")
        void email_con_subdominio() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarEmail("user@mail.empresa.co"));
        }

        @Test
        @DisplayName("Debe lanzar EmailException si es null")
        void email_null() {
            assertThrows(EmailException.class, () ->
                LanzadorDeExcepcion.verificarEmail(null));
        }

        @Test
        @DisplayName("Debe lanzar EmailException si está vacío")
        void email_vacio() {
            assertThrows(EmailException.class, () ->
                LanzadorDeExcepcion.verificarEmail(""));
        }

        @Test
        @DisplayName("Debe lanzar EmailException sin @")
        void email_sin_arroba() {
            assertThrows(EmailException.class, () ->
                LanzadorDeExcepcion.verificarEmail("usuariodominio.com"));
        }

        @Test
        @DisplayName("Debe lanzar EmailException con doble punto")
        void email_con_doble_punto() {
            assertThrows(EmailException.class, () ->
                LanzadorDeExcepcion.verificarEmail("usuario..doble@dominio.com"));
        }

        @Test
        @DisplayName("Debe lanzar EmailException con espacio")
        void email_con_espacio() {
            assertThrows(EmailException.class, () ->
                LanzadorDeExcepcion.verificarEmail("usuario @dominio.com"));
        }

        @Test
        @DisplayName("Debe lanzar EmailException si dominio empieza con guión")
        void email_dominio_guion_inicio() {
            assertThrows(EmailException.class, () ->
                LanzadorDeExcepcion.verificarEmail("usuario@-dominio.com"));
        }

        @Test
        @DisplayName("Debe lanzar EmailException si parte local empieza con punto")
        void email_local_punto_inicio() {
            assertThrows(EmailException.class, () ->
                LanzadorDeExcepcion.verificarEmail(".usuario@dominio.com"));
        }
    }

    // ═══════════════════════════════════════════════════════════
    // NOMBRE (persona)
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("verificarNombre()")
    class VerificarNombreTest {

        @Test
        @DisplayName("Debe pasar con nombre compuesto válido")
        void nombre_valido() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarNombre("Carlos Alberto"));
        }

        @Test
        @DisplayName("Debe pasar con tildes y ñ")
        void nombre_con_tildes() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarNombre("María José Núñez"));
        }

        @Test
        @DisplayName("Debe pasar con exactamente 2 caracteres (límite mínimo)")
        void nombre_dos_caracteres() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarNombre("Li"));
        }

        @Test
        @DisplayName("Debe lanzar NombreException si es null")
        void nombre_null() {
            assertThrows(NombreException.class, () ->
                LanzadorDeExcepcion.verificarNombre(null));
        }

        @Test
        @DisplayName("Debe lanzar NombreException con un solo carácter")
        void nombre_un_caracter() {
            assertThrows(NombreException.class, () ->
                LanzadorDeExcepcion.verificarNombre("A"));
        }

        @Test
        @DisplayName("Debe lanzar NombreException con números")
        void nombre_con_numeros() {
            assertThrows(NombreException.class, () ->
                LanzadorDeExcepcion.verificarNombre("Carlos123"));
        }

        @Test
        @DisplayName("Debe lanzar NombreException con caracteres especiales")
        void nombre_con_especiales() {
            assertThrows(NombreException.class, () ->
                LanzadorDeExcepcion.verificarNombre("Carlos@Pérez"));
        }
    }

    // ═══════════════════════════════════════════════════════════
    // TELÉFONO
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("verificarTelefono()")
    class VerificarTelefonoTest {

        @Test
        @DisplayName("Debe pasar con teléfono colombiano válido")
        void telefono_valido() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarTelefono("3101234567"));
        }

        @Test
        @DisplayName("Debe pasar con prefijo internacional")
        void telefono_con_prefijo() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarTelefono("+573101234567"));
        }

        @Test
        @DisplayName("Debe pasar con 7 dígitos (límite mínimo)")
        void telefono_minimo() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarTelefono("1234567"));
        }

        @Test
        @DisplayName("Debe lanzar TelefonoException si es null")
        void telefono_null() {
            assertThrows(TelefonoException.class, () ->
                LanzadorDeExcepcion.verificarTelefono(null));
        }

        @Test
        @DisplayName("Debe lanzar TelefonoException con letras")
        void telefono_con_letras() {
            assertThrows(TelefonoException.class, () ->
                LanzadorDeExcepcion.verificarTelefono("310ABC4567"));
        }

        @Test
        @DisplayName("Debe lanzar TelefonoException muy corto (menos de 7)")
        void telefono_muy_corto() {
            assertThrows(TelefonoException.class, () ->
                LanzadorDeExcepcion.verificarTelefono("123"));
        }

        @Test
        @DisplayName("Debe lanzar TelefonoException muy largo (más de 15)")
        void telefono_muy_largo() {
            assertThrows(TelefonoException.class, () ->
                LanzadorDeExcepcion.verificarTelefono("1234567890123456"));
        }

        @Test
        @DisplayName("Debe lanzar TelefonoException vacío")
        void telefono_vacio() {
            assertThrows(TelefonoException.class, () ->
                LanzadorDeExcepcion.verificarTelefono(""));
        }
    }

    // ═══════════════════════════════════════════════════════════
    // CIUDAD
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("verificarCiudad()")
    class VerificarCiudadTest {

        @Test
        @DisplayName("Debe pasar con ciudad válida")
        void ciudad_valida() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarCiudad("Bogotá"));
        }

        @Test
        @DisplayName("Debe pasar con ciudad de dos palabras")
        void ciudad_dos_palabras() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarCiudad("Bucaramanga Norte"));
        }

        @Test
        @DisplayName("Debe lanzar CiudadException si es null")
        void ciudad_null() {
            assertThrows(CiudadException.class, () ->
                LanzadorDeExcepcion.verificarCiudad(null));
        }

        @Test
        @DisplayName("Debe lanzar CiudadException con números")
        void ciudad_con_numeros() {
            assertThrows(CiudadException.class, () ->
                LanzadorDeExcepcion.verificarCiudad("Bogotá123"));
        }

        @Test
        @DisplayName("Debe lanzar CiudadException con un carácter")
        void ciudad_muy_corta() {
            assertThrows(CiudadException.class, () ->
                LanzadorDeExcepcion.verificarCiudad("B"));
        }

        @Test
        @DisplayName("Debe lanzar CiudadException vacía")
        void ciudad_vacia() {
            assertThrows(CiudadException.class, () ->
                LanzadorDeExcepcion.verificarCiudad(""));
        }
    }

    // ═══════════════════════════════════════════════════════════
    // DIRECCIÓN
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("verificarDireccion()")
    class VerificarDireccionTest {

        @Test
        @DisplayName("Debe pasar con dirección válida colombiana")
        void direccion_valida() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarDireccion("Calle 123 #45-67"));
        }

        @Test
        @DisplayName("Debe pasar con dirección mínima válida")
        void direccion_minima() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarDireccion("Cr 12"));
        }

        @Test
        @DisplayName("Debe lanzar DireccionException si es null")
        void direccion_null() {
            assertThrows(DireccionException.class, () ->
                LanzadorDeExcepcion.verificarDireccion(null));
        }

        @Test
        @DisplayName("Debe lanzar DireccionException muy corta")
        void direccion_muy_corta() {
            assertThrows(DireccionException.class, () ->
                LanzadorDeExcepcion.verificarDireccion("C1"));
        }

        @Test
        @DisplayName("Debe lanzar DireccionException solo con números")
        void direccion_solo_numeros() {
            assertThrows(DireccionException.class, () ->
                LanzadorDeExcepcion.verificarDireccion("12345678"));
        }

        @Test
        @DisplayName("Debe lanzar DireccionException solo con letras sin número")
        void direccion_solo_letras() {
            assertThrows(DireccionException.class, () ->
                LanzadorDeExcepcion.verificarDireccion("CalleSinNumero"));
        }

        @Test
        @DisplayName("Debe lanzar DireccionException vacía")
        void direccion_vacia() {
            assertThrows(DireccionException.class, () ->
                LanzadorDeExcepcion.verificarDireccion(""));
        }
    }

    // ═══════════════════════════════════════════════════════════
    // EDAD
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("verificarEdad()")
    class VerificarEdadTest {

        @Test
        @DisplayName("Debe pasar con edad mínima exacta (18)")
        void edad_18() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarEdad(18));
        }

        @Test
        @DisplayName("Debe pasar con edad máxima exacta (100)")
        void edad_100() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarEdad(100));
        }

        @Test
        @DisplayName("Debe pasar con edad intermedia")
        void edad_intermedia() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarEdad(35));
        }

        @Test
        @DisplayName("Debe lanzar EdadException si es menor a 18")
        void edad_menor_18() {
            assertThrows(EdadException.class, () ->
                LanzadorDeExcepcion.verificarEdad(17));
        }

        @Test
        @DisplayName("Debe lanzar EdadException si es 0")
        void edad_cero() {
            assertThrows(EdadException.class, () ->
                LanzadorDeExcepcion.verificarEdad(0));
        }

        @Test
        @DisplayName("Debe lanzar EdadException si supera 100")
        void edad_mayor_100() {
            assertThrows(EdadException.class, () ->
                LanzadorDeExcepcion.verificarEdad(101));
        }

        @Test
        @DisplayName("Debe lanzar EdadException con edad negativa")
        void edad_negativa() {
            assertThrows(EdadException.class, () ->
                LanzadorDeExcepcion.verificarEdad(-5));
        }
    }

    // ═══════════════════════════════════════════════════════════
    // NOMBRE ANIMAL
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("verificarNombreAnimal()")
    class VerificarNombreAnimalTest {

        @Test
        @DisplayName("Debe pasar con nombre de animal válido")
        void nombre_animal_valido() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarNombreAnimal("Firulais"));
        }

        @Test
        @DisplayName("Debe pasar con nombre con tildes")
        void nombre_animal_con_tildes() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarNombreAnimal("Ñoño"));
        }

        @Test
        @DisplayName("Debe pasar con nombre compuesto")
        void nombre_animal_compuesto() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarNombreAnimal("Pelusa Bonita"));
        }

        @Test
        @DisplayName("Debe lanzar NombreException si es null")
        void nombre_animal_null() {
            assertThrows(NombreException.class, () ->
                LanzadorDeExcepcion.verificarNombreAnimal(null));
        }

        @Test
        @DisplayName("Debe lanzar NombreException vacío")
        void nombre_animal_vacio() {
            assertThrows(NombreException.class, () ->
                LanzadorDeExcepcion.verificarNombreAnimal(""));
        }

        @Test
        @DisplayName("Debe lanzar NombreException con números")
        void nombre_animal_con_numeros() {
            assertThrows(NombreException.class, () ->
                LanzadorDeExcepcion.verificarNombreAnimal("Firulais2"));
        }

        @Test
        @DisplayName("Debe lanzar NombreException con un solo carácter")
        void nombre_animal_un_caracter() {
            assertThrows(NombreException.class, () ->
                LanzadorDeExcepcion.verificarNombreAnimal("F"));
        }
    }

    // ═══════════════════════════════════════════════════════════
    // ESPECIE
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("verificarEspecie()")
    class VerificarEspecieTest {

        @Test
        @DisplayName("Debe pasar con especie válida")
        void especie_valida() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarEspecie("Perro"));
        }

        @Test
        @DisplayName("Debe pasar con especie con tildes")
        void especie_con_tildes() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarEspecie("Pájaro"));
        }

        @Test
        @DisplayName("Debe lanzar EspecieException si es null")
        void especie_null() {
            assertThrows(EspecieException.class, () ->
                LanzadorDeExcepcion.verificarEspecie(null));
        }

        @Test
        @DisplayName("Debe lanzar EspecieException vacía")
        void especie_vacia() {
            assertThrows(EspecieException.class, () ->
                LanzadorDeExcepcion.verificarEspecie(""));
        }

        @Test
        @DisplayName("Debe lanzar EspecieException con números")
        void especie_con_numeros() {
            assertThrows(EspecieException.class, () ->
                LanzadorDeExcepcion.verificarEspecie("Perro2"));
        }

        @Test
        @DisplayName("Debe lanzar EspecieException con un carácter")
        void especie_un_caracter() {
            assertThrows(EspecieException.class, () ->
                LanzadorDeExcepcion.verificarEspecie("P"));
        }
    }

    // ═══════════════════════════════════════════════════════════
    // RAZA
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("verificarRaza()")
    class VerificarRazaTest {

        @Test
        @DisplayName("Debe pasar con raza válida")
        void raza_valida() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarRaza("Cocker Spaniel"));
        }

        @Test
        @DisplayName("Debe pasar con raza de dos caracteres (límite mínimo)")
        void raza_minima() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarRaza("Pi"));
        }

        @Test
        @DisplayName("Debe lanzar RazaException si es null")
        void raza_null() {
            assertThrows(RazaException.class, () ->
                LanzadorDeExcepcion.verificarRaza(null));
        }

        @Test
        @DisplayName("Debe lanzar RazaException vacía")
        void raza_vacia() {
            assertThrows(RazaException.class, () ->
                LanzadorDeExcepcion.verificarRaza(""));
        }

        @Test
        @DisplayName("Debe lanzar RazaException con un carácter")
        void raza_un_caracter() {
            assertThrows(RazaException.class, () ->
                LanzadorDeExcepcion.verificarRaza("A"));
        }
    }

    // ═══════════════════════════════════════════════════════════
    // OBSERVACIONES
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("verificarObservaciones()")
    class VerificarObservacionesTest {

        @Test
        @DisplayName("Debe pasar con observaciones válidas")
        void observaciones_validas() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarObservaciones(
                    "Perro muy amigable y juguetón."));
        }

        @Test
        @DisplayName("Debe pasar con exactamente 10 caracteres (límite mínimo)")
        void observaciones_diez_chars() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarObservaciones("1234567890"));
        }

        @Test
        @DisplayName("Debe pasar con exactamente 500 caracteres (límite máximo)")
        void observaciones_quinientos_chars() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarObservaciones("A".repeat(500)));
        }

        @Test
        @DisplayName("Debe lanzar ObservacionesException si es null")
        void observaciones_null() {
            assertThrows(ObservacionesException.class, () ->
                LanzadorDeExcepcion.verificarObservaciones(null));
        }

        @Test
        @DisplayName("Debe lanzar ObservacionesException vacías")
        void observaciones_vacias() {
            assertThrows(ObservacionesException.class, () ->
                LanzadorDeExcepcion.verificarObservaciones(""));
        }

        @Test
        @DisplayName("Debe lanzar ObservacionesException con menos de 10 caracteres")
        void observaciones_muy_cortas() {
            assertThrows(ObservacionesException.class, () ->
                LanzadorDeExcepcion.verificarObservaciones("Corta"));
        }

        @Test
        @DisplayName("Debe lanzar ObservacionesException con 501 caracteres")
        void observaciones_muy_largas() {
            assertThrows(ObservacionesException.class, () ->
                LanzadorDeExcepcion.verificarObservaciones("A".repeat(501)));
        }
    }

    // ═══════════════════════════════════════════════════════════
    // IMAGEN
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("verificarImagen()")
    class VerificarImagenTest {

        @Test
        @DisplayName("Debe pasar con imagen JPG válida")
        void imagen_jpg_valida() {
            MultipartFile mock = Mockito.mock(MultipartFile.class);
            Mockito.when(mock.isEmpty()).thenReturn(false);
            Mockito.when(mock.getContentType()).thenReturn("image/jpeg");
            Mockito.when(mock.getSize()).thenReturn(1024L);
            assertDoesNotThrow(() -> LanzadorDeExcepcion.verificarImagen(mock));
        }

        @Test
        @DisplayName("Debe pasar con imagen PNG válida")
        void imagen_png_valida() {
            MultipartFile mock = Mockito.mock(MultipartFile.class);
            Mockito.when(mock.isEmpty()).thenReturn(false);
            Mockito.when(mock.getContentType()).thenReturn("image/png");
            Mockito.when(mock.getSize()).thenReturn(2 * 1024 * 1024L);
            assertDoesNotThrow(() -> LanzadorDeExcepcion.verificarImagen(mock));
        }

        @Test
        @DisplayName("Debe pasar con imagen en el límite exacto de 5MB")
        void imagen_exactamente_5mb() {
            MultipartFile mock = Mockito.mock(MultipartFile.class);
            Mockito.when(mock.isEmpty()).thenReturn(false);
            Mockito.when(mock.getContentType()).thenReturn("image/jpeg");
            Mockito.when(mock.getSize()).thenReturn(5L * 1024 * 1024);
            assertDoesNotThrow(() -> LanzadorDeExcepcion.verificarImagen(mock));
        }

        @Test
        @DisplayName("Debe lanzar ImagenException si imagen es null")
        void imagen_null() {
            assertThrows(ImagenException.class, () ->
                LanzadorDeExcepcion.verificarImagen(null));
        }

        @Test
        @DisplayName("Debe lanzar ImagenException si imagen está vacía")
        void imagen_vacia() {
            MultipartFile mock = Mockito.mock(MultipartFile.class);
            Mockito.when(mock.isEmpty()).thenReturn(true);
            assertThrows(ImagenException.class, () ->
                LanzadorDeExcepcion.verificarImagen(mock));
        }

        @Test
        @DisplayName("Debe lanzar ImagenException con GIF (tipo no permitido)")
        void imagen_gif() {
            MultipartFile mock = Mockito.mock(MultipartFile.class);
            Mockito.when(mock.isEmpty()).thenReturn(false);
            Mockito.when(mock.getContentType()).thenReturn("image/gif");
            assertThrows(ImagenException.class, () ->
                LanzadorDeExcepcion.verificarImagen(mock));
        }

        @Test
        @DisplayName("Debe lanzar ImagenException con PDF")
        void imagen_pdf() {
            MultipartFile mock = Mockito.mock(MultipartFile.class);
            Mockito.when(mock.isEmpty()).thenReturn(false);
            Mockito.when(mock.getContentType()).thenReturn("application/pdf");
            assertThrows(ImagenException.class, () ->
                LanzadorDeExcepcion.verificarImagen(mock));
        }

        @Test
        @DisplayName("Debe lanzar ImagenException si supera 5MB")
        void imagen_supera_5mb() {
            MultipartFile mock = Mockito.mock(MultipartFile.class);
            Mockito.when(mock.isEmpty()).thenReturn(false);
            Mockito.when(mock.getContentType()).thenReturn("image/jpeg");
            Mockito.when(mock.getSize()).thenReturn(6L * 1024 * 1024);
            assertThrows(ImagenException.class, () ->
                LanzadorDeExcepcion.verificarImagen(mock));
        }

        @Test
        @DisplayName("Debe lanzar ImagenException con contentType null")
        void imagen_content_type_null() {
            MultipartFile mock = Mockito.mock(MultipartFile.class);
            Mockito.when(mock.isEmpty()).thenReturn(false);
            Mockito.when(mock.getContentType()).thenReturn(null);
            assertThrows(ImagenException.class, () ->
                LanzadorDeExcepcion.verificarImagen(mock));
        }
    }

    // ═══════════════════════════════════════════════════════════
    // ESPECIE DOMÉSTICA
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("verificarEspecieDomestica()")
    class VerificarEspecieDomesticaTest {

        @Test
        @DisplayName("Debe pasar con especie permitida: perro")
        void especie_perro() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarEspecieDomestica("perro"));
        }

        @Test
        @DisplayName("Debe pasar con especie permitida: gato")
        void especie_gato() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarEspecieDomestica("gato"));
        }

        @Test
        @DisplayName("Debe pasar con especie permitida: conejo")
        void especie_conejo() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarEspecieDomestica("conejo"));
        }

        @Test
        @DisplayName("Debe pasar con especie permitida: loro")
        void especie_loro() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarEspecieDomestica("loro verde"));
        }

        @Test
        @DisplayName("Debe pasar con especie en mayúsculas (case-insensitive)")
        void especie_mayusculas() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarEspecieDomestica("PERRO"));
        }

        @Test
        @DisplayName("Debe lanzar ValidacionIAException con especie no permitida: tigre")
        void especie_tigre() {
            assertThrows(ValidacionIAException.class, () ->
                LanzadorDeExcepcion.verificarEspecieDomestica("tigre"));
        }

        @Test
        @DisplayName("Debe lanzar ValidacionIAException con especie no permitida: cocodrilo")
        void especie_cocodrilo() {
            assertThrows(ValidacionIAException.class, () ->
                LanzadorDeExcepcion.verificarEspecieDomestica("cocodrilo"));
        }

        @Test
        @DisplayName("Debe lanzar ValidacionIAException con especie no permitida: serpiente")
        void especie_serpiente() {
            assertThrows(ValidacionIAException.class, () ->
                LanzadorDeExcepcion.verificarEspecieDomestica("serpiente"));
        }

        @Test
        @DisplayName("Debe lanzar ValidacionIAException con especie desconocida")
        void especie_desconocida() {
            assertThrows(ValidacionIAException.class, () ->
                LanzadorDeExcepcion.verificarEspecieDomestica("dragón"));
        }
    }

    // ═══════════════════════════════════════════════════════════
    // VERIFICACIONES BOOLEANAS
    // ═══════════════════════════════════════════════════════════
    @Nested
    @DisplayName("Verificaciones booleanas")
    class VerificacionesBooleanas {

        @Test
        @DisplayName("verificarUsuarioExiste — no lanza si existe")
        void usuario_existe() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarUsuarioExiste(true));
        }

        @Test
        @DisplayName("verificarUsuarioExiste — lanza UserNotFoundException si no existe")
        void usuario_no_existe() {
            assertThrows(UserNotFoundException.class, () ->
                LanzadorDeExcepcion.verificarUsuarioExiste(false));
        }

        @Test
        @DisplayName("verificarAnimalExiste — no lanza si existe")
        void animal_existe() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarAnimalExiste(true));
        }

        @Test
        @DisplayName("verificarAnimalExiste — lanza AnimalNoEncontradoException si no existe")
        void animal_no_existe() {
            assertThrows(AnimalNoEncontradoException.class, () ->
                LanzadorDeExcepcion.verificarAnimalExiste(false));
        }

        @Test
        @DisplayName("verificarAnimalDisponible — no lanza si disponible")
        void animal_disponible() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarAnimalDisponible(true));
        }

        @Test
        @DisplayName("verificarAnimalDisponible — lanza AnimalNoDisponibleException")
        void animal_no_disponible() {
            assertThrows(AnimalNoDisponibleException.class, () ->
                LanzadorDeExcepcion.verificarAnimalDisponible(false));
        }

        @Test
        @DisplayName("verificarSolicitudExiste — no lanza si existe")
        void solicitud_existe() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarSolicitudExiste(true));
        }

        @Test
        @DisplayName("verificarSolicitudExiste — lanza SolicitudNoEncontradaException")
        void solicitud_no_existe() {
            assertThrows(SolicitudNoEncontradaException.class, () ->
                LanzadorDeExcepcion.verificarSolicitudExiste(false));
        }

        @Test
        @DisplayName("verificarSolicitudPendiente — no lanza si pendiente")
        void solicitud_pendiente() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarSolicitudPendiente(true));
        }

        @Test
        @DisplayName("verificarSolicitudPendiente — lanza SolicitudNoPendienteException")
        void solicitud_no_pendiente() {
            assertThrows(SolicitudNoPendienteException.class, () ->
                LanzadorDeExcepcion.verificarSolicitudPendiente(false));
        }

        @Test
        @DisplayName("verificarSolicitudDuplicada — lanza SolicitudDuplicadaException si existe")
        void solicitud_duplicada() {
            assertThrows(SolicitudDuplicadaException.class, () ->
                LanzadorDeExcepcion.verificarSolicitudDuplicada(true));
        }

        @Test
        @DisplayName("verificarSolicitudDuplicada — no lanza si no existe")
        void solicitud_no_duplicada() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarSolicitudDuplicada(false));
        }

        @Test
        @DisplayName("verificarValidacionIA — no lanza si aprobado")
        void validacion_ia_aprobada() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarValidacionIA(true));
        }

        @Test
        @DisplayName("verificarValidacionIA — lanza ValidacionIAException si rechazado")
        void validacion_ia_rechazada() {
            assertThrows(ValidacionIAException.class, () ->
                LanzadorDeExcepcion.verificarValidacionIA(false));
        }

        @Test
        @DisplayName("verificarNotificacionExiste — no lanza si existe")
        void notificacion_existe() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarNotificacionExiste(true));
        }

        @Test
        @DisplayName("verificarNotificacionExiste — lanza NotificacionNoEncontradaException")
        void notificacion_no_existe() {
            assertThrows(NotificacionNoEncontradaException.class, () ->
                LanzadorDeExcepcion.verificarNotificacionExiste(false));
        }
        
        @Test
        @DisplayName("Debe pasar con Hámster con tilde (normalización)")
        void especie_hamster_con_tilde() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarEspecieDomestica("Hámster"));
        }

        @Test
        @DisplayName("Debe pasar con Pájaro con tilde (normalización)")
        void especie_pajaro_con_tilde() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarEspecieDomestica("Pájaro"));
        }

        @Test
        @DisplayName("Debe pasar con Hurón con tilde (normalización)")
        void especie_huron_con_tilde() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarEspecieDomestica("Hurón"));
        }

        @Test
        @DisplayName("Debe pasar con Cobaya (variación de cobayo)")
        void especie_cobaya() {
            assertDoesNotThrow(() ->
                LanzadorDeExcepcion.verificarEspecieDomestica("Cobaya"));
        }
    }
}