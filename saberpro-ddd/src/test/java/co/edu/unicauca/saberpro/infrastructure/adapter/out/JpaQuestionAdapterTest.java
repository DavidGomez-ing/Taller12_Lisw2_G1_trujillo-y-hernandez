package co.edu.unicauca.saberpro.infrastructure.adapter.out;

import co.edu.unicauca.saberpro.domain.ports.outbound.PreguntaRepositoryPort;
import co.edu.unicauca.saberpro.infrastructure.adapter.out.persistence.JpaQuestionAdapter;
import co.edu.unicauca.saberpro.infrastructure.adapter.out.persistence.SpringDataPreguntaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * El adaptador JPA (sobre H2 real) debe pasar las MISMAS pruebas de contrato.
 *
 * Las pruebas NO corren dentro de una transaccion de prueba: cada operacion del
 * repositorio abre y cierra la suya, asi cada lectura va realmente a la base de
 * datos (no a la cache de primer nivel) y no hace falta forzar flush/clear.
 * Por eso se limpia la tabla antes de cada prueba.
 */
@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class JpaQuestionAdapterTest extends PreguntaRepositoryPortContractTest {

    @Autowired
    private SpringDataPreguntaRepository springData;

    @BeforeEach
    void limpiarTabla() {
        springData.deleteAll();
    }

    @Override
    protected PreguntaRepositoryPort crearRepositorio() {
        return new JpaQuestionAdapter(springData);
    }
}
