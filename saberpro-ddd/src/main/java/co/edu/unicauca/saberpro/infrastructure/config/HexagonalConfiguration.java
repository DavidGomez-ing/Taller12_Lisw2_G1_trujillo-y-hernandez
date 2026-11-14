package co.edu.unicauca.saberpro.infrastructure.config;

import co.edu.unicauca.saberpro.application.service.PreguntaService;
import co.edu.unicauca.saberpro.domain.ports.outbound.NotificacionPort;
import co.edu.unicauca.saberpro.domain.ports.outbound.PreguntaRepositoryPort;
import co.edu.unicauca.saberpro.infrastructure.adapter.out.memory.InMemoryQuestionAdapter;
import co.edu.unicauca.saberpro.infrastructure.adapter.out.notification.LogNotificacionAdapter;
import co.edu.unicauca.saberpro.infrastructure.adapter.out.persistence.JpaQuestionAdapter;
import co.edu.unicauca.saberpro.infrastructure.adapter.out.persistence.SpringDataPreguntaRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * COMPOSITION ROOT: el unico lugar que decide QUE adaptador se conecta a CADA
 * puerto. Para la prueba de intercambiabilidad solo cambia la propiedad
 *   saberpro.persistence.adapter = jpa | memory
 * (por defecto jpa). El paquete domain no se toca.
 */
@Configuration
public class HexagonalConfiguration {

    @Bean
    @ConditionalOnProperty(name = "saberpro.persistence.adapter", havingValue = "jpa", matchIfMissing = true)
    public PreguntaRepositoryPort jpaQuestionAdapter(SpringDataPreguntaRepository repositorio) {
        return new JpaQuestionAdapter(repositorio);
    }

    @Bean
    @ConditionalOnProperty(name = "saberpro.persistence.adapter", havingValue = "memory")
    public PreguntaRepositoryPort inMemoryQuestionAdapter() {
        return new InMemoryQuestionAdapter();
    }

    @Bean
    public NotificacionPort notificacionPort() {
        return new LogNotificacionAdapter();
    }

    /** El servicio es Java puro: aqui se le inyectan los puertos de salida. */
    @Bean
    public PreguntaService preguntaService(PreguntaRepositoryPort repositorio, NotificacionPort notificaciones) {
        return new PreguntaService(repositorio, notificaciones);
    }
}
