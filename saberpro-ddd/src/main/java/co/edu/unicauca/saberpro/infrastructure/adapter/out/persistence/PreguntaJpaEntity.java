package co.edu.unicauca.saberpro.infrastructure.adapter.out.persistence;

import co.edu.unicauca.saberpro.domain.model.EstadoPregunta;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de PERSISTENCIA (no es el agregado). Las anotaciones JPA viven aqui,
 * en infraestructura, y nunca en domain.model. El mapeo entre este registro y
 * el agregado Pregunta lo hace PreguntaPersistenceMapper.
 */
@Entity
@Table(name = "preguntas")
public class PreguntaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(length = 2000)
    String enunciado;

    @Column(length = 2000)
    String justificacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    EstadoPregunta estado;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "pregunta_opciones", joinColumns = @JoinColumn(name = "pregunta_id"))
    @OrderColumn(name = "posicion")
    List<OpcionRespuestaJpa> opciones = new ArrayList<>();

    protected PreguntaJpaEntity() {
    }
}
