package co.edu.unicauca.saberpro.infrastructure.adapter.out;

import co.edu.unicauca.saberpro.domain.ports.outbound.PreguntaRepositoryPort;
import co.edu.unicauca.saberpro.infrastructure.adapter.out.memory.InMemoryQuestionAdapter;

/** El adaptador de memoria debe cumplir el contrato del puerto. */
class InMemoryQuestionAdapterTest extends PreguntaRepositoryPortContractTest {

    @Override
    protected PreguntaRepositoryPort crearRepositorio() {
        return new InMemoryQuestionAdapter();
    }
}
