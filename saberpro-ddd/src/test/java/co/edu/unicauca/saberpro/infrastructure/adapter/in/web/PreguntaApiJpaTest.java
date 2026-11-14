package co.edu.unicauca.saberpro.infrastructure.adapter.in.web;

import co.edu.unicauca.saberpro.domain.ports.outbound.PreguntaRepositoryPort;
import co.edu.unicauca.saberpro.infrastructure.adapter.out.persistence.JpaQuestionAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/** Paso 1 del Taller 12: la aplicacion corre con JpaQuestionAdapter. */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "saberpro.persistence.adapter=jpa",
        "spring.datasource.url=jdbc:h2:mem:apijpatest;DB_CLOSE_DELAY=-1"})
class PreguntaApiJpaTest extends PreguntaApiContractTest {

    @Autowired
    private PreguntaRepositoryPort repositorio;

    @Test
    void elAdaptadorActivoEsJpa() {
        assertInstanceOf(JpaQuestionAdapter.class, repositorio);
    }
}
