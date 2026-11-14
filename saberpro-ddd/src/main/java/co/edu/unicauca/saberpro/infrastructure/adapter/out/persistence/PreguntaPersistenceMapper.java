package co.edu.unicauca.saberpro.infrastructure.adapter.out.persistence;

import co.edu.unicauca.saberpro.domain.model.Enunciado;
import co.edu.unicauca.saberpro.domain.model.Justificacion;
import co.edu.unicauca.saberpro.domain.model.OpcionRespuesta;
import co.edu.unicauca.saberpro.domain.model.Pregunta;

import java.util.ArrayList;
import java.util.List;

/** Traduce entre el agregado de dominio y su registro JPA (en ambos sentidos). */
final class PreguntaPersistenceMapper {

    private PreguntaPersistenceMapper() {
    }

    static PreguntaJpaEntity toEntity(Pregunta p) {
        PreguntaJpaEntity e = new PreguntaJpaEntity();
        e.id = p.getId();
        e.enunciado = (p.getEnunciado() == null) ? null : p.getEnunciado().getTexto();
        e.justificacion = (p.getJustificacion() == null) ? null : p.getJustificacion().getTexto();
        e.estado = p.getEstado();
        List<OpcionRespuestaJpa> opciones = new ArrayList<>();
        for (OpcionRespuesta o : p.getOpciones()) {
            opciones.add(new OpcionRespuestaJpa(o.getTexto(), o.isEsCorrecta()));
        }
        e.opciones = opciones;
        return e;
    }

    static Pregunta toDomain(PreguntaJpaEntity e) {
        List<OpcionRespuesta> opciones = new ArrayList<>();
        for (OpcionRespuestaJpa o : e.opciones) {
            opciones.add(new OpcionRespuesta(o.texto, o.esCorrecta));
        }
        return Pregunta.reconstituir(
                e.id,
                e.enunciado == null ? null : new Enunciado(e.enunciado),
                e.justificacion == null ? null : new Justificacion(e.justificacion),
                opciones,
                e.estado);
    }
}
