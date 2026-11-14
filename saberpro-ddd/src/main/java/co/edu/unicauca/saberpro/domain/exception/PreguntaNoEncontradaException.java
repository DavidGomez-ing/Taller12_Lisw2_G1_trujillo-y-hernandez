package co.edu.unicauca.saberpro.domain.exception;

/** Se lanza cuando se solicita una pregunta que no existe en el repositorio. */
public class PreguntaNoEncontradaException extends DomainException {

    public PreguntaNoEncontradaException(Long id) {
        super("No existe una pregunta con id " + id + ".");
    }
}
