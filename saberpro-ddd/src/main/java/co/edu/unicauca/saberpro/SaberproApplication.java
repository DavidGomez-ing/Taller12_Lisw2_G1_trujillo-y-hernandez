package co.edu.unicauca.saberpro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Punto de arranque de la aplicacion (solo infraestructura: el dominio no lo conoce). */
@SpringBootApplication
public class SaberproApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaberproApplication.class, args);
    }
}
