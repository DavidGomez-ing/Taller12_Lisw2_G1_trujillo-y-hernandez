package co.edu.unicauca.saberpro.infrastructure.adapter.out;

import co.edu.unicauca.saberpro.domain.model.EstadoPregunta;
import co.edu.unicauca.saberpro.domain.model.OpcionRespuesta;
import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.ports.outbound.PreguntaRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PRUEBA DE CONTRATO del puerto PreguntaRepositoryPort. Es abstracta: cada
 * adaptador (JPA, memoria) la hereda y debe pasar EXACTAMENTE las mismas pruebas.
 * Esa es la evidencia automatica de la intercambiabilidad.
 */
public abstract class PreguntaRepositoryPortContractTest {

    protected PreguntaRepositoryPort repositorio;

    /** Cada adaptador entrega su implementacion. */
    protected abstract PreguntaRepositoryPort crearRepositorio();

    /** Gancho para adaptadores con cache (JPA): fuerza ir realmente a la base de datos. */
    protected void sincronizar() {
    }

    @BeforeEach
    void preparar() {
        repositorio = crearRepositorio();
    }

    private static Pregunta borradorCompleto(String enunciado) {
        Pregunta p = new Pregunta();
        p.definirEnunciado(enunciado);
        p.definirJustificacion("MVC separa modelo, vista y controlador.");
        p.agregarOpcion("MVC", true);
        p.agregarOpcion("Singleton", false);
        p.agregarOpcion("Factory", false);
        p.agregarOpcion("Observer", false);
        return p;
    }

    @Test
    void saveAsignaIdALaPreguntaNueva() {
        Pregunta guardada = repositorio.save(borradorCompleto("¿Qué patrón separa capas?"));
        assertNotNull(guardada.getId());
    }

    @Test
    void saveNoModificaElObjetoRecibido() {
        Pregunta original = borradorCompleto("¿Qué patrón separa capas?");
        repositorio.save(original);
        assertNull(original.getId());
    }

    @Test
    void findByIdDevuelveElAgregadoCompletoYConLasOpcionesEnOrden() {
        Long id = repositorio.save(borradorCompleto("¿Qué patrón separa capas?")).getId();
        sincronizar();

        Pregunta leida = repositorio.findById(id).orElseThrow();
        assertEquals("¿Qué patrón separa capas?", leida.getEnunciado().getTexto());
        assertEquals("MVC separa modelo, vista y controlador.", leida.getJustificacion().getTexto());
        assertEquals(EstadoPregunta.BORRADOR, leida.getEstado());
        assertEquals(List.of(new OpcionRespuesta("MVC", true), new OpcionRespuesta("Singleton", false),
                        new OpcionRespuesta("Factory", false), new OpcionRespuesta("Observer", false)),
                leida.getOpciones());
    }

    @Test
    void findByIdDeUnIdInexistenteDevuelveVacio() {
        assertEquals(Optional.empty(), repositorio.findById(123456L));
    }

    @Test
    void saveSobreUnaPreguntaExistenteActualizaSinDuplicar() {
        Pregunta guardada = repositorio.save(borradorCompleto("¿Qué patrón separa capas?"));
        guardada.enviarARevision();
        repositorio.save(guardada);
        sincronizar();

        assertEquals(1, repositorio.findAll().size());
        assertEquals(EstadoPregunta.PENDIENTE_REVISION,
                repositorio.findById(guardada.getId()).orElseThrow().getEstado());
    }

    @Test
    void findAllDevuelveTodasEnOrdenDeIdentificador() {
        Long a = repositorio.save(borradorCompleto("Primera")).getId();
        Long b = repositorio.save(borradorCompleto("Segunda")).getId();
        sincronizar();

        assertEquals(List.of(a, b), repositorio.findAll().stream().map(Pregunta::getId).toList());
    }

    @Test
    void guardaUnBorradorIncompletoSinEnunciadoNiOpciones() {
        Pregunta vacia = new Pregunta();
        Long id = repositorio.save(vacia).getId();
        sincronizar();

        Pregunta leida = repositorio.findById(id).orElseThrow();
        assertNull(leida.getEnunciado());
        assertNull(leida.getJustificacion());
        assertTrue(leida.getOpciones().isEmpty());
        assertEquals(EstadoPregunta.BORRADOR, leida.getEstado());
    }
}
