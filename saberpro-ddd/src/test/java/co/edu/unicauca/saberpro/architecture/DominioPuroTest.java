package co.edu.unicauca.saberpro.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regla de dependencias del hexagono, verificada sobre el codigo fuente:
 * domain no importa frameworks ni capas externas; application solo depende de domain.
 */
class DominioPuroTest {

    private static final Path BASE = Path.of("src/main/java/co/edu/unicauca/saberpro");

    private static final List<String> FRAMEWORKS = List.of(
            "org.springframework", "jakarta.persistence", "javax.persistence", "org.hibernate",
            "jakarta.servlet", "com.fasterxml", "com.h2database");

    private static List<String> importsInvalidos(String paquete, List<String> prohibidos) throws IOException {
        Path dir = BASE.resolve(paquete);
        assertTrue(Files.isDirectory(dir), "No existe el paquete " + dir);
        try (Stream<Path> archivos = Files.walk(dir)) {
            return archivos.filter(f -> f.toString().endsWith(".java"))
                    .flatMap(f -> {
                        try {
                            return Files.readAllLines(f).stream()
                                    .filter(l -> l.startsWith("import "))
                                    .filter(l -> prohibidos.stream().anyMatch(l::contains))
                                    .map(l -> f.getFileName() + " -> " + l);
                        } catch (IOException e) {
                            throw new IllegalStateException(e);
                        }
                    })
                    .collect(Collectors.toList());
        }
    }

    @Test
    void domainNoDependeDeFrameworks() throws IOException {
        List<String> malos = importsInvalidos("domain", FRAMEWORKS);
        assertTrue(malos.isEmpty(), "domain importa frameworks: " + malos);
    }

    @Test
    void domainNoDependeDeApplicationNiInfrastructure() throws IOException {
        List<String> malos = importsInvalidos("domain",
                List.of("co.edu.unicauca.saberpro.application", "co.edu.unicauca.saberpro.infrastructure"));
        assertTrue(malos.isEmpty(), "domain apunta hacia afuera: " + malos);
    }

    @Test
    void applicationNoDependeDeFrameworksNiDeInfrastructure() throws IOException {
        List<String> prohibidos = new java.util.ArrayList<>(FRAMEWORKS);
        prohibidos.add("co.edu.unicauca.saberpro.infrastructure");
        List<String> malos = importsInvalidos("application", prohibidos);
        assertTrue(malos.isEmpty(), "application depende de infraestructura: " + malos);
    }

    @Test
    void domainNoUsaAnotacionesDeFrameworks() throws IOException {
        try (Stream<Path> archivos = Files.walk(BASE.resolve("domain"))) {
            List<String> malos = archivos.filter(f -> f.toString().endsWith(".java"))
                    .filter(f -> {
                        try {
                            String c = Files.readString(f);
                            return c.contains("@Entity") || c.contains("@Table") || c.contains("@Component")
                                    || c.contains("@Service") || c.contains("@Autowired");
                        } catch (IOException e) {
                            throw new IllegalStateException(e);
                        }
                    })
                    .map(f -> f.getFileName().toString())
                    .collect(Collectors.toList());
            assertTrue(malos.isEmpty(), "Anotaciones de framework en domain: " + malos);
        }
    }
}
