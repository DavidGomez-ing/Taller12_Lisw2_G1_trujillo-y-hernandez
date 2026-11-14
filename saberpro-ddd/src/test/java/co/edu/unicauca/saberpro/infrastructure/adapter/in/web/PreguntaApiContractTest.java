package co.edu.unicauca.saberpro.infrastructure.adapter.in.web;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integracion del flujo REST completo. Son las MISMAS para ambos
 * adaptadores: PreguntaApiJpaTest y PreguntaApiMemoryTest solo cambian la
 * propiedad saberpro.persistence.adapter.
 */
public abstract class PreguntaApiContractTest {

    @Autowired
    protected MockMvc mockMvc;

    static final String PREGUNTA_VALIDA = """
            {"enunciado":"¿Qué patrón separa la lógica de negocio de la interfaz?",
             "justificacion":"MVC separa modelo, vista y controlador.",
             "opciones":[{"texto":"MVC","esCorrecta":true},{"texto":"Singleton","esCorrecta":false},
                         {"texto":"Factory","esCorrecta":false},{"texto":"Observer","esCorrecta":false}]}
            """;

    private long crear(String json) throws Exception {
        MvcResult r = mockMvc.perform(post("/api/preguntas").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("BORRADOR"))
                .andReturn();
        return ((Number) JsonPath.read(r.getResponse().getContentAsString(), "$.id")).longValue();
    }

    @Test
    void creaUnaPreguntaEnBorradorYLaConsulta() throws Exception {
        long id = crear(PREGUNTA_VALIDA);
        mockMvc.perform(get("/api/preguntas/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.opciones.length()").value(4))
                .andExpect(jsonPath("$.opciones[0].texto").value("MVC"))
                .andExpect(jsonPath("$.opciones[0].esCorrecta").value(true));
    }

    @Test
    void enviaARevisionUnaPreguntaValida() throws Exception {
        long id = crear(PREGUNTA_VALIDA);
        mockMvc.perform(post("/api/preguntas/" + id + "/enviar-revision"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PENDIENTE_REVISION"));
        mockMvc.perform(get("/api/preguntas/" + id))
                .andExpect(jsonPath("$.estado").value("PENDIENTE_REVISION"));
    }

    @Test
    void rechazaElEnvioConMenosDeCuatroOpciones() throws Exception {
        long id = crear("""
                {"enunciado":"Enunciado de prueba","justificacion":"Justificación suficientemente larga.",
                 "opciones":[{"texto":"A","esCorrecta":true},{"texto":"B","esCorrecta":false}]}
                """);
        mockMvc.perform(post("/api/preguntas/" + id + "/enviar-revision"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
        mockMvc.perform(get("/api/preguntas/" + id))
                .andExpect(jsonPath("$.estado").value("BORRADOR"));
    }

    @Test
    void rechazaUnaJustificacionConMenosDeQuinceCaracteres() throws Exception {
        mockMvc.perform(post("/api/preguntas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"enunciado\":\"Enunciado\",\"justificacion\":\"corta\",\"opciones\":[]}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rechazaUnEnunciadoVacio() throws Exception {
        mockMvc.perform(post("/api/preguntas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"enunciado\":\"  \",\"justificacion\":\"Justificación suficientemente larga.\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void devuelve404ConUnaPreguntaInexistente() throws Exception {
        mockMvc.perform(get("/api/preguntas/999999")).andExpect(status().isNotFound());
        mockMvc.perform(post("/api/preguntas/999999/enviar-revision")).andExpect(status().isNotFound());
    }

    @Test
    void noPermiteEnviarDosVecesLaMismaPregunta() throws Exception {
        long id = crear(PREGUNTA_VALIDA);
        mockMvc.perform(post("/api/preguntas/" + id + "/enviar-revision")).andExpect(status().isOk());
        mockMvc.perform(post("/api/preguntas/" + id + "/enviar-revision")).andExpect(status().isBadRequest());
    }
}
