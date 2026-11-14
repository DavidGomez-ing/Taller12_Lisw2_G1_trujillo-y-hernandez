package co.edu.unicauca.saberpro.domain.ports.inbound;

import co.edu.unicauca.saberpro.domain.model.Pregunta;

/**
 * PUERTO DE ENTRADA (driving). Casos de uso de creacion y envio a revision.
 * Lo invocan los adaptadores primarios (controlador REST, pruebas, CLI...) sin
 * conocer la implementacion del servicio de aplicacion.
 */
public interface CrearPreguntaUseCase {

    /** Crea una pregunta en estado BORRADOR validando los Value Objects. */
    Pregunta crearPregunta(CrearPreguntaCommand comando);

    /** Valida las invariantes del agregado y la pasa a PENDIENTE_REVISION. */
    Pregunta enviarARevision(Long id);
}
