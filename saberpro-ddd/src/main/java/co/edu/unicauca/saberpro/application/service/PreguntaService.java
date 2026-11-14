package co.edu.unicauca.saberpro.application.service;

import co.edu.unicauca.saberpro.domain.exception.PreguntaNoEncontradaException;
import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.ports.inbound.ConsultarPreguntaUseCase;
import co.edu.unicauca.saberpro.domain.ports.inbound.CrearPreguntaCommand;
import co.edu.unicauca.saberpro.domain.ports.inbound.CrearPreguntaUseCase;
import co.edu.unicauca.saberpro.domain.ports.outbound.NotificacionPort;
import co.edu.unicauca.saberpro.domain.ports.outbound.PreguntaRepositoryPort;

import java.util.List;

/**
 * Servicio de aplicacion: implementa los puertos de entrada y orquesta el caso
 * de uso usando solo puertos de salida. NO contiene reglas de negocio (viven en
 * el agregado Pregunta) y NO conoce a Spring: es Java puro; el "composition
 * root" (infrastructure.config) es quien lo instancia y le inyecta los adaptadores.
 */
public class PreguntaService implements CrearPreguntaUseCase, ConsultarPreguntaUseCase {

    private final PreguntaRepositoryPort repositorio;
    private final NotificacionPort notificaciones;

    public PreguntaService(PreguntaRepositoryPort repositorio, NotificacionPort notificaciones) {
        this.repositorio = repositorio;
        this.notificaciones = notificaciones;
    }

    @Override
    public Pregunta crearPregunta(CrearPreguntaCommand comando) {
        Pregunta pregunta = new Pregunta();
        pregunta.definirEnunciado(comando.enunciado());
        pregunta.definirJustificacion(comando.justificacion());
        for (CrearPreguntaCommand.OpcionCommand opcion : comando.opciones()) {
            pregunta.agregarOpcion(opcion.texto(), opcion.esCorrecta());
        }
        return repositorio.save(pregunta);
    }

    @Override
    public Pregunta enviarARevision(Long id) {
        Pregunta pregunta = obtenerPregunta(id);
        pregunta.enviarARevision();                       // el agregado valida sus invariantes
        Pregunta guardada = repositorio.save(pregunta);
        notificaciones.notificarEnvioARevision(guardada); // solo si todo lo anterior salio bien
        return guardada;
    }

    @Override
    public Pregunta obtenerPregunta(Long id) {
        return repositorio.findById(id).orElseThrow(() -> new PreguntaNoEncontradaException(id));
    }

    @Override
    public List<Pregunta> listarPreguntas() {
        return repositorio.findAll();
    }
}
