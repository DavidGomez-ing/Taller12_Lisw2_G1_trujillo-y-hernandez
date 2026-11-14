package co.edu.unicauca.saberpro.domain.ports.inbound;

import co.edu.unicauca.saberpro.domain.model.Pregunta;

import java.util.List;

/** PUERTO DE ENTRADA (driving) de solo lectura: consultas sobre el banco de preguntas. */
public interface ConsultarPreguntaUseCase {

    Pregunta obtenerPregunta(Long id);

    List<Pregunta> listarPreguntas();
}
