package co.edu.unicauca.saberpro.infrastructure.adapter.out.memory;

import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.ports.outbound.PreguntaRepositoryPort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * ADAPTADOR DE SALIDA (memoria): implementa PreguntaRepositoryPort con un HashMap.
 * Se comporta igual que el adaptador JPA: genera ids consecutivos, devuelve
 * copias (no el mismo objeto) y no modifica el objeto recibido en save().
 */
public class InMemoryQuestionAdapter implements PreguntaRepositoryPort {

    private final Map<Long, Pregunta> almacen = new HashMap<>();
    private long secuencia = 0;

    @Override
    public synchronized Pregunta save(Pregunta pregunta) {
        Pregunta copia = copiar(pregunta);
        if (copia.getId() == null) {
            copia.asignarId(++secuencia);
        }
        almacen.put(copia.getId(), copiar(copia));
        return copiar(copia);
    }

    @Override
    public synchronized Optional<Pregunta> findById(Long id) {
        return Optional.ofNullable(almacen.get(id)).map(InMemoryQuestionAdapter::copiar);
    }

    @Override
    public synchronized List<Pregunta> findAll() {
        List<Pregunta> todas = new ArrayList<>();
        for (Pregunta p : almacen.values()) {
            todas.add(copiar(p));
        }
        todas.sort(Comparator.comparing(Pregunta::getId));
        return todas;
    }

    private static Pregunta copiar(Pregunta p) {
        return Pregunta.reconstituir(p.getId(), p.getEnunciado(), p.getJustificacion(),
                p.getOpciones(), p.getEstado());
    }
}
