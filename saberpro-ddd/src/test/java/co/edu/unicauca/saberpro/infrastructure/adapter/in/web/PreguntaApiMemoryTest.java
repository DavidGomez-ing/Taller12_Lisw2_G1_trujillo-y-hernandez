package co.edu.unicauca.saberpro.infrastructure.adapter.in.web;

import co.edu.unicauca.saberpro.domain.ports.outbound.PreguntaRepositoryPort;
import co.edu.unicauca.saberpro.infrastructure.adapter.out.memory.InMemoryQuestionAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/** Pasos 2 y 3 del Taller 12: se activa InMemoryQuestionAdapter y TODO sigue funcionando igual. */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "saberpro.persistence.adapter=memory",
        "spring.datasource.url=jdbc:h2:mem:apimemorytest;DB_CLOSE_DELAY=-1"})
class PreguntaApiMemoryTest extends PreguntaApiContractTest {

    @Autowired
    private PreguntaRepositoryPort repositorio;

    @Test
    void elAdaptadorActivoEsElDeMemoria() {
        assertInstanceOf(InMemoryQuestionAdapter.class, repositorio);
    }
}
