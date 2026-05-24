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

@ExtendWith(MockitoExtension.class)
class AuditoriaServiceTest {

    @Mock private AuditoriaRepository auditoriaRepo;
    @InjectMocks private AuditoriaService auditoriaService;

    // ─── Helper ──────────────────────────────────────────────────────────────

    private Auditoria sampleAuditoria(boolean exitoso) {
        return new Auditoria("admin", "CREATE_USER", "Creo usuario juan", exitoso);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // registrar()
    // ═══════════════════════════════════════════════════════════════════════

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

    @Test
    @DisplayName("registrar: accion fallida → guarda con exitoso=false")
    void registrar_fallido_guardaConExitosoFalse() {
        ArgumentCaptor<Auditoria> captor = ArgumentCaptor.forClass(Auditoria.class);

        auditoriaService.registrar("juan", "DELETE_USER", "Intento fallido", false);

        verify(auditoriaRepo).save(captor.capture());
        assertFalse(captor.getValue().isExitoso());
    }

    @Test
    @DisplayName("registrar: multiples acciones → multiples saves")
    void registrar_multiplesAcciones_multiplesGuardados() {
        auditoriaService.registrar("u1", "LOGIN", "Login exitoso", true);
        auditoriaService.registrar("u2", "LOGIN", "Login fallido", false);
        verify(auditoriaRepo, times(2)).save(any(Auditoria.class));
    }

    @Test
    @DisplayName("registrar: usuario nulo → igual guarda (sin validacion en service)")
    void registrar_usuarioNulo_guardaSinExcepcion() {
        auditoriaService.registrar(null, "ACTION", "Descripcion", true);
        verify(auditoriaRepo).save(any(Auditoria.class));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // getAll()
    // ═══════════════════════════════════════════════════════════════════════

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

    @Test
    @DisplayName("getAll: repositorio vacio → lista vacia")
    void getAll_vacio_retornaListaVacia() {
        when(auditoriaRepo.findAll()).thenReturn(List.of());
        assertTrue(auditoriaService.getAll().isEmpty());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // getByUsuario()
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("getByUsuario: con registros del usuario → retorna lista filtrada")
    void getByUsuario_conRegistros_retornaLista() {
        Auditoria a = sampleAuditoria(true);
        when(auditoriaRepo.findByUsuarioEjecutor("admin")).thenReturn(List.of(a));

        List<Auditoria> result = auditoriaService.getByUsuario("admin");

        assertEquals(1, result.size());
        assertEquals("admin", result.get(0).getUsuarioEjecutor());
    }

    @Test
    @DisplayName("getByUsuario: sin registros → lista vacia")
    void getByUsuario_sinRegistros_listaVacia() {
        when(auditoriaRepo.findByUsuarioEjecutor("inexistente")).thenReturn(List.of());
        assertTrue(auditoriaService.getByUsuario("inexistente").isEmpty());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // getByAccion()
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("getByAccion: accion existente → retorna lista")
    void getByAccion_existente_retornaLista() {
        Auditoria a = sampleAuditoria(true);
        when(auditoriaRepo.findByAccion("CREATE_USER")).thenReturn(List.of(a));

        List<Auditoria> result = auditoriaService.getByAccion("CREATE_USER");

        assertEquals(1, result.size());
        assertEquals("CREATE_USER", result.get(0).getAccion());
    }

    @Test
    @DisplayName("getByAccion: accion inexistente → lista vacia")
    void getByAccion_inexistente_listaVacia() {
        when(auditoriaRepo.findByAccion("INEXISTENTE")).thenReturn(List.of());
        assertTrue(auditoriaService.getByAccion("INEXISTENTE").isEmpty());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // getByExitoso()
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("getByExitoso: true → retorna solo exitosas")
    void getByExitoso_true_retornaExitosas() {
        Auditoria a = sampleAuditoria(true);
        when(auditoriaRepo.findByExitoso(true)).thenReturn(List.of(a));

        List<Auditoria> result = auditoriaService.getByExitoso(true);

        assertEquals(1, result.size());
        assertTrue(result.get(0).isExitoso());
    }

    @Test
    @DisplayName("getByExitoso: false → retorna solo fallidas")
    void getByExitoso_false_retornaFallidas() {
        Auditoria a = sampleAuditoria(false);
        when(auditoriaRepo.findByExitoso(false)).thenReturn(List.of(a));

        List<Auditoria> result = auditoriaService.getByExitoso(false);

        assertEquals(1, result.size());
        assertFalse(result.get(0).isExitoso());
    }

    @Test
    @DisplayName("getByExitoso: sin coincidencias → lista vacia")
    void getByExitoso_sinCoincidencias_listaVacia() {
        when(auditoriaRepo.findByExitoso(true)).thenReturn(List.of());
        assertTrue(auditoriaService.getByExitoso(true).isEmpty());
    }
}