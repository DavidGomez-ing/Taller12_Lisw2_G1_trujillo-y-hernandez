package co.edu.unicauca.saberpro.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/** Representacion JPA (embebida) de una OpcionRespuesta del dominio. */
@Embeddable
public class OpcionRespuestaJpa {

    @Column(name = "texto", nullable = false, length = 1000)
    String texto;

    @Column(name = "es_correcta", nullable = false)
    boolean esCorrecta;

    protected OpcionRespuestaJpa() {
    }

    OpcionRespuestaJpa(String texto, boolean esCorrecta) {
        this.texto = texto;
        this.esCorrecta = esCorrecta;
    }
}
