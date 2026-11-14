package co.edu.unicauca.saberpro.domain.ports.inbound;

import java.util.List;

/**
 * Comando de entrada del caso de uso "crear pregunta". Es un registro inmutable
 * de datos primitivos: el dominio construye sus Value Objects a partir de el.
 */
public record CrearPreguntaCommand(String enunciado, String justificacion, List<OpcionCommand> opciones) {

    public CrearPreguntaCommand {
        opciones = (opciones == null) ? List.of() : List.copyOf(opciones);
    }

    /** Datos de una opcion de respuesta. */
    public record OpcionCommand(String texto, boolean esCorrecta) {
    }
}
