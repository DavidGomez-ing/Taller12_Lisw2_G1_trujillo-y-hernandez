package co.edu.unicauca.saberpro.domain.ports.outbound;

import co.edu.unicauca.saberpro.domain.model.Pregunta;

import java.util.List;
import java.util.Optional;

/**
 * PUERTO DE SALIDA (driven) de persistencia. Operaciones puras sobre el agregado
 * Pregunta: ni una sola referencia a JPA, SQL, Spring o HashMap.
 * Contrato: save devuelve la pregunta con su id asignado (no modifica el objeto
 * recibido); findById devuelve vacio si no existe.
 */
public interface PreguntaRepositoryPort {

    Pregunta save(Pregunta pregunta);

    Optional<Pregunta> findById(Long id);

    List<Pregunta> findAll();
}
