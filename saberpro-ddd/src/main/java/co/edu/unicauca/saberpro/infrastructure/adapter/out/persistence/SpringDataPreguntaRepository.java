package co.edu.unicauca.saberpro.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Detalle interno del adaptador JPA: el nucleo nunca lo ve ni lo conoce. */
public interface SpringDataPreguntaRepository extends JpaRepository<PreguntaJpaEntity, Long> {

    List<PreguntaJpaEntity> findAllByOrderByIdAsc();
}
