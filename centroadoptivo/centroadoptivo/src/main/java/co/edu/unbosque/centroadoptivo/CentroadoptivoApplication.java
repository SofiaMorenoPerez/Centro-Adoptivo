package co.edu.unbosque.centroadoptivo;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Clase principal de la aplicación Centro Adoptivo.
 * <p>
 * Punto de entrada de la aplicación Spring Boot.
 * </p>
 */
@SpringBootApplication
public class CentroadoptivoApplication {

    /**
     * Método principal que inicia la aplicación Spring Boot.
     *
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        SpringApplication.run(CentroadoptivoApplication.class, args);
    }

    /**
     * Crea y registra un bean de {@link ModelMapper} en el contexto de Spring.
     *
     * @return una nueva instancia de {@link ModelMapper}
     */
    @Bean
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }
}