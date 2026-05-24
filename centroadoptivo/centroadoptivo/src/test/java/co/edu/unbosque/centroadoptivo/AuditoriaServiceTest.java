package co.edu.unbosque.centroadoptivo;

import co.edu.unbosque.centroadoptivo.entity.Auditoria;
import co.edu.unbosque.centroadoptivo.repository.AuditoriaRepository;
import co.edu.unbosque.centroadoptivo.service.AuditoriaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para {@link AuditoriaService}.
 * <p>
 * Verifica el comportamiento del servicio de auditoría usando Mockito para
 * simular el repositorio {@link AuditoriaRepository}, sin necesidad de
 * levantar el contexto de Spring.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class AuditoriaServiceTest {

    /** Mock del repositorio de auditoría inyectado en el servicio. */
    @Mock private AuditoriaRepository auditoriaRepo;

    /** Instancia del servicio bajo prueba con dependencias mockeadas. */
    @InjectMocks private AuditoriaService auditoriaService;

    

    /**
     * Crea una instancia de {@link Auditoria} de muestra para usar en los tests.
     *
     * @param exitoso {@code true} si la auditoría representa una acción exitosa
     * @return instancia de {@link Auditoria} con datos predefinidos
     */
    private Auditoria sampleAuditoria(boolean exitoso) {
        return new Auditoria("admin", "CREATE_USER", "Creo usuario juan", exitoso);
    }

    
    /**
     * Verifica que al registrar una acción exitosa, se guarda una auditoría
     * con todos los datos correctamente asignados, incluyendo la fecha.
     */
    @Test
    @DisplayName("registrar: accion exitosa → guarda auditoria con datos correctos")
    void registrar_exitoso_guardaAuditoria() {
        ArgumentCaptor<Auditoria> captor = ArgumentCaptor.forClass(Auditoria.class);

        auditoriaService.registrar("admin", "CREATE_USER", "Creo usuario juan", true);

        verify(auditoriaRepo).save(captor.capture());
        Auditoria guardada = captor.getValue();
        assertEquals("admin", guardada.getUsuarioEjecutor());
        assertEquals("CREATE_USER", guardada.getAccion());
        assertEquals("Creo usuario juan", guardada.getDescripcion());
        assertTrue(guardada.isExitoso());
        assertNotNull(guardada.getFecha());
    }

    /**
     * Verifica que al registrar una acción fallida, se guarda la auditoría
     * con el campo {@code exitoso} en {@code false}.
     */
    @Test
    @DisplayName("registrar: accion fallida → guarda con exitoso=false")
    void registrar_fallido_guardaConExitosoFalse() {
        ArgumentCaptor<Auditoria> captor = ArgumentCaptor.forClass(Auditoria.class);

        auditoriaService.registrar("juan", "DELETE_USER", "Intento fallido", false);

        verify(auditoriaRepo).save(captor.capture());
        assertFalse(captor.getValue().isExitoso());
    }

    /**
     * Verifica que al registrar múltiples acciones, el repositorio
     * invoca {@code save} una vez por cada acción registrada.
     */
    @Test
    @DisplayName("registrar: multiples acciones → multiples saves")
    void registrar_multiplesAcciones_multiplesGuardados() {
        auditoriaService.registrar("u1", "LOGIN", "Login exitoso", true);
        auditoriaService.registrar("u2", "LOGIN", "Login fallido", false);
        verify(auditoriaRepo, times(2)).save(any(Auditoria.class));
    }

    /**
     * Verifica que el servicio guarda la auditoría sin lanzar excepción
     * aunque el usuario sea {@code null}, ya que no hay validación en esta capa.
     */
    @Test
    @DisplayName("registrar: usuario nulo → igual guarda (sin validacion en service)")
    void registrar_usuarioNulo_guardaSinExcepcion() {
        auditoriaService.registrar(null, "ACTION", "Descripcion", true);
        verify(auditoriaRepo).save(any(Auditoria.class));
    }

   

    /**
     * Verifica que {@code getAll} retorna todos los registros de auditoría
     * cuando el repositorio contiene elementos.
     */
    @Test
    @DisplayName("getAll: repositorio con registros → retorna lista completa")
    void getAll_conRegistros_retornaLista() {
        List<Auditoria> lista = List.of(
                sampleAuditoria(true),
                sampleAuditoria(false)
        );
        when(auditoriaRepo.findAll()).thenReturn(lista);

        List<Auditoria> result = auditoriaService.getAll();

        assertEquals(2, result.size());
    }

    /**
     * Verifica que {@code getAll} retorna una lista vacía cuando
     * el repositorio no tiene registros.
     */
    @Test
    @DisplayName("getAll: repositorio vacio → lista vacia")
    void getAll_vacio_retornaListaVacia() {
        when(auditoriaRepo.findAll()).thenReturn(List.of());
        assertTrue(auditoriaService.getAll().isEmpty());
    }

    
    /**
     * Verifica que {@code getByUsuario} retorna los registros filtrados
     * por el nombre del usuario ejecutor cuando existen coincidencias.
     */
    @Test
    @DisplayName("getByUsuario: con registros del usuario → retorna lista filtrada")
    void getByUsuario_conRegistros_retornaLista() {
        Auditoria a = sampleAuditoria(true);
        when(auditoriaRepo.findByUsuarioEjecutor("admin")).thenReturn(List.of(a));

        List<Auditoria> result = auditoriaService.getByUsuario("admin");

        assertEquals(1, result.size());
        assertEquals("admin", result.get(0).getUsuarioEjecutor());
    }

    /**
     * Verifica que {@code getByUsuario} retorna una lista vacía
     * cuando no existen registros para el usuario indicado.
     */
    @Test
    @DisplayName("getByUsuario: sin registros → lista vacia")
    void getByUsuario_sinRegistros_listaVacia() {
        when(auditoriaRepo.findByUsuarioEjecutor("inexistente")).thenReturn(List.of());
        assertTrue(auditoriaService.getByUsuario("inexistente").isEmpty());
    }

    

    /**
     * Verifica que {@code getByAccion} retorna los registros correspondientes
     * cuando existe al menos una auditoría con la acción indicada.
     */
    @Test
    @DisplayName("getByAccion: accion existente → retorna lista")
    void getByAccion_existente_retornaLista() {
        Auditoria a = sampleAuditoria(true);
        when(auditoriaRepo.findByAccion("CREATE_USER")).thenReturn(List.of(a));

        List<Auditoria> result = auditoriaService.getByAccion("CREATE_USER");

        assertEquals(1, result.size());
        assertEquals("CREATE_USER", result.get(0).getAccion());
    }

    /**
     * Verifica que {@code getByAccion} retorna una lista vacía
     * cuando no existen registros con la acción indicada.
     */
    @Test
    @DisplayName("getByAccion: accion inexistente → lista vacia")
    void getByAccion_inexistente_listaVacia() {
        when(auditoriaRepo.findByAccion("INEXISTENTE")).thenReturn(List.of());
        assertTrue(auditoriaService.getByAccion("INEXISTENTE").isEmpty());
    }

    

    /**
     * Verifica que {@code getByExitoso(true)} retorna únicamente
     * las auditorías marcadas como exitosas.
     */
    @Test
    @DisplayName("getByExitoso: true → retorna solo exitosas")
    void getByExitoso_true_retornaExitosas() {
        Auditoria a = sampleAuditoria(true);
        when(auditoriaRepo.findByExitoso(true)).thenReturn(List.of(a));

        List<Auditoria> result = auditoriaService.getByExitoso(true);

        assertEquals(1, result.size());
        assertTrue(result.get(0).isExitoso());
    }

    /**
     * Verifica que {@code getByExitoso(false)} retorna únicamente
     * las auditorías marcadas como fallidas.
     */
    @Test
    @DisplayName("getByExitoso: false → retorna solo fallidas")
    void getByExitoso_false_retornaFallidas() {
        Auditoria a = sampleAuditoria(false);
        when(auditoriaRepo.findByExitoso(false)).thenReturn(List.of(a));

        List<Auditoria> result = auditoriaService.getByExitoso(false);

        assertEquals(1, result.size());
        assertFalse(result.get(0).isExitoso());
    }

    /**
     * Verifica que {@code getByExitoso} retorna una lista vacía
     * cuando no existen registros que coincidan con el criterio indicado.
     */
    @Test
    @DisplayName("getByExitoso: sin coincidencias → lista vacia")
    void getByExitoso_sinCoincidencias_listaVacia() {
        when(auditoriaRepo.findByExitoso(true)).thenReturn(List.of());
        assertTrue(auditoriaService.getByExitoso(true).isEmpty());
    }
}