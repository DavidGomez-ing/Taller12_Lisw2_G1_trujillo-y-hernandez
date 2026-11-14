package co.edu.unicauca.saberpro.domain.model;

import co.edu.unicauca.saberpro.domain.exception.ReglaNegocioException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PreguntaReconstitucionTest {

    private static List<OpcionRespuesta> cuatro() {
        return List.of(new OpcionRespuesta("A", true), new OpcionRespuesta("B", false),
                new OpcionRespuesta("C", false), new OpcionRespuesta("D", false));
    }

    @Test
    void debeRestaurarTodoElEstadoDelAgregado() {
        Pregunta p = Pregunta.reconstituir(9L, new Enunciado("¿Qué es un puerto?"),
                new Justificacion("Un puerto es un contrato del núcleo."), cuatro(),
                EstadoPregunta.PENDIENTE_REVISION);
        assertEquals(Long.valueOf(9L), p.getId());
        assertEquals("¿Qué es un puerto?", p.getEnunciado().getTexto());
        assertEquals(4, p.getOpciones().size());
        assertEquals(EstadoPregunta.PENDIENTE_REVISION, p.getEstado());
    }

    @Test
    void noDebePermitirReconstituirConMasDeCuatroOpciones() {
        List<OpcionRespuesta> cinco = new java.util.ArrayList<>(cuatro());
        cinco.add(new OpcionRespuesta("E", false));
        assertThrows(ReglaNegocioException.class,
                () -> Pregunta.reconstituir(1L, null, null, cinco, EstadoPregunta.BORRADOR));
    }

    @Test
    void laPreguntaReconstituidaSigueRespetandoElCicloDeVida() {
        Pregunta p = Pregunta.reconstituir(1L, new Enunciado("Enunciado"),
                new Justificacion("Justificación suficientemente larga."), cuatro(), EstadoPregunta.APROBADA);
        assertThrows(ReglaNegocioException.class, p::rechazar);
    }
}
