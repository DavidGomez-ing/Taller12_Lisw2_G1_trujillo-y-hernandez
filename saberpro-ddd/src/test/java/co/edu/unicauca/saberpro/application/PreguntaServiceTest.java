package co.edu.unicauca.saberpro.application;

import co.edu.unicauca.saberpro.application.service.PreguntaService;
import co.edu.unicauca.saberpro.domain.exception.PreguntaNoEncontradaException;
import co.edu.unicauca.saberpro.domain.exception.ReglaNegocioException;
import co.edu.unicauca.saberpro.domain.model.EstadoPregunta;
import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.ports.inbound.CrearPreguntaCommand;
import co.edu.unicauca.saberpro.domain.ports.inbound.CrearPreguntaCommand.OpcionCommand;
import co.edu.unicauca.saberpro.domain.ports.outbound.NotificacionPort;
import co.edu.unicauca.saberpro.infrastructure.adapter.out.memory.InMemoryQuestionAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Prueba del servicio de aplicacion SIN Spring: puertos de salida con adaptadores de prueba. */
class PreguntaServiceTest {

    /** Notificador de prueba: recuerda a quien se aviso. */
    static class NotificadorEspia implements NotificacionPort {
        final List<Long> avisados = new ArrayList<>();

        @Override
        public void notificarEnvioARevision(Pregunta pregunta) {
            avisados.add(pregunta.getId());
        }
    }

    private InMemoryQuestionAdapter repositorio;
    private NotificadorEspia notificador;
    private PreguntaService servicio;

    @BeforeEach
    void preparar() {
        repositorio = new InMemoryQuestionAdapter();
        notificador = new NotificadorEspia();
        servicio = new PreguntaService(repositorio, notificador);
    }

    private static CrearPreguntaCommand comando(int opciones, int correctas) {
        List<OpcionCommand> lista = new ArrayList<>();
        for (int i = 0; i < opciones; i++) {
            lista.add(new OpcionCommand("Opción " + (char) ('A' + i), i < correctas));
        }
        return new CrearPreguntaCommand("¿Qué es un adaptador?", "Un adaptador implementa un puerto.", lista);
    }

    @Test
    void crearPreguntaLaDejaEnBorradorConIdAsignado() {
        Pregunta p = servicio.crearPregunta(comando(4, 1));
        assertNotNull(p.getId());
        assertEquals(EstadoPregunta.BORRADOR, p.getEstado());
        assertEquals(4, p.getOpciones().size());
    }

    @Test
    void crearPreguntaConJustificacionCortaLanzaReglaNegocio() {
        CrearPreguntaCommand corto = new CrearPreguntaCommand("Enunciado", "corta", List.of());
        assertThrows(ReglaNegocioException.class, () -> servicio.crearPregunta(corto));
        assertTrue(servicio.listarPreguntas().isEmpty());
    }

    @Test
    void enviarARevisionCambiaElEstadoPersistidoYNotifica() {
        Pregunta creada = servicio.crearPregunta(comando(4, 1));
        Pregunta enviada = servicio.enviarARevision(creada.getId());

        assertEquals(EstadoPregunta.PENDIENTE_REVISION, enviada.getEstado());
        assertEquals(EstadoPregunta.PENDIENTE_REVISION,
                servicio.obtenerPregunta(creada.getId()).getEstado());
        assertEquals(List.of(creada.getId()), notificador.avisados);
    }

    @Test
    void noDebeEnviarARevisionConTresOpcionesNiNotificar() {
        Pregunta creada = servicio.crearPregunta(comando(3, 1));
        assertThrows(ReglaNegocioException.class, () -> servicio.enviarARevision(creada.getId()));
        assertEquals(EstadoPregunta.BORRADOR, servicio.obtenerPregunta(creada.getId()).getEstado());
        assertTrue(notificador.avisados.isEmpty());
    }

    @Test
    void noDebeEnviarARevisionSinClaveCorrecta() {
        Pregunta creada = servicio.crearPregunta(comando(4, 0));
        assertThrows(ReglaNegocioException.class, () -> servicio.enviarARevision(creada.getId()));
    }

    @Test
    void noDebeEnviarARevisionConDosClaves() {
        Pregunta creada = servicio.crearPregunta(comando(4, 2));
        assertThrows(ReglaNegocioException.class, () -> servicio.enviarARevision(creada.getId()));
    }

    @Test
    void enviarUnaPreguntaInexistenteLanzaNoEncontrada() {
        assertThrows(PreguntaNoEncontradaException.class, () -> servicio.enviarARevision(99L));
        assertThrows(PreguntaNoEncontradaException.class, () -> servicio.obtenerPregunta(99L));
    }

    @Test
    void listarDevuelveLasPreguntasEnOrdenDeCreacion() {
        Pregunta a = servicio.crearPregunta(comando(4, 1));
        Pregunta b = servicio.crearPregunta(comando(4, 1));
        List<Pregunta> todas = servicio.listarPreguntas();
        assertEquals(List.of(a.getId(), b.getId()),
                todas.stream().map(Pregunta::getId).toList());
    }
}
