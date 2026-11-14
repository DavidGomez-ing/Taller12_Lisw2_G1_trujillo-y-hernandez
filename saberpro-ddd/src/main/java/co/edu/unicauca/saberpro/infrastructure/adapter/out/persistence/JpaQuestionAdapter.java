package co.edu.unicauca.saberpro.infrastructure.adapter.out.persistence;

import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.ports.outbound.PreguntaRepositoryPort;

import java.util.List;
import java.util.Optional;

/**
 * ADAPTADOR DE SALIDA (JPA): implementa PreguntaRepositoryPort con Spring Data
 * JPA sobre H2. Traduce agregado <-> PreguntaJpaEntity en cada operacion.
 */
public class JpaQuestionAdapter implements PreguntaRepositoryPort {

    private final SpringDataPreguntaRepository repositorio;

    public JpaQuestionAdapter(SpringDataPreguntaRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public Pregunta save(Pregunta pregunta) {
        PreguntaJpaEntity guardada = repositorio.save(PreguntaPersistenceMapper.toEntity(pregunta));
        return PreguntaPersistenceMapper.toDomain(guardada);
    }

    @Override
    public Optional<Pregunta> findById(Long id) {
        return repositorio.findById(id).map(PreguntaPersistenceMapper::toDomain);
    }

    @Override
    public List<Pregunta> findAll() {
        return repositorio.findAllByOrderByIdAsc().stream()
                .map(PreguntaPersistenceMapper::toDomain)
                .toList();
    }
}
