package co.edu.unicauca.saberpro.infrastructure.adapter.in.web;

import co.edu.unicauca.saberpro.domain.ports.inbound.ConsultarPreguntaUseCase;
import co.edu.unicauca.saberpro.domain.ports.inbound.CrearPreguntaCommand;
import co.edu.unicauca.saberpro.domain.ports.inbound.CrearPreguntaUseCase;
import co.edu.unicauca.saberpro.infrastructure.adapter.in.web.dto.CrearPreguntaRequest;
import co.edu.unicauca.saberpro.infrastructure.adapter.in.web.dto.PreguntaResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ADAPTADOR DE ENTRADA (driving): traduce HTTP/JSON a llamadas a los puertos de
 * entrada. Depende de las INTERFACES (CrearPreguntaUseCase...), nunca del
 * servicio concreto ni de ningun repositorio.
 */
@RestController
@RequestMapping("/api/preguntas")
public class PreguntaController {

    private final CrearPreguntaUseCase crearPregunta;
    private final ConsultarPreguntaUseCase consultarPregunta;

    public PreguntaController(CrearPreguntaUseCase crearPregunta, ConsultarPreguntaUseCase consultarPregunta) {
        this.crearPregunta = crearPregunta;
        this.consultarPregunta = consultarPregunta;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PreguntaResponse crear(@RequestBody CrearPreguntaRequest request) {
        List<CrearPreguntaRequest.OpcionRequest> opciones =
                request.opciones() == null ? List.of() : request.opciones();
        CrearPreguntaCommand comando = new CrearPreguntaCommand(
                request.enunciado(),
                request.justificacion(),
                opciones.stream()
                        .map(o -> new CrearPreguntaCommand.OpcionCommand(o.texto(), o.esCorrecta()))
                        .toList());
        return PreguntaResponse.desde(crearPregunta.crearPregunta(comando));
    }

    @PostMapping("/{id}/enviar-revision")
    public PreguntaResponse enviarARevision(@PathVariable("id") Long id) {
        return PreguntaResponse.desde(crearPregunta.enviarARevision(id));
    }

    @GetMapping
    public List<PreguntaResponse> listar() {
        return consultarPregunta.listarPreguntas().stream().map(PreguntaResponse::desde).toList();
    }

    @GetMapping("/{id}")
    public PreguntaResponse obtener(@PathVariable("id") Long id) {
        return PreguntaResponse.desde(consultarPregunta.obtenerPregunta(id));
    }
}
