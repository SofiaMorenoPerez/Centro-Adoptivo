package co.edu.unbosque.centroadoptivo;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Inicializador del servlet para despliegue de la aplicación en un servidor externo.
 * <p>
 * Extiende {@link SpringBootServletInitializer} para permitir el despliegue
 * como archivo WAR en un contenedor de servlets externo.
 * </p>
 */
public class ServletInitializer extends SpringBootServletInitializer {

    /**
     * Configura la aplicación Spring Boot para el despliegue en servidor externo.
     *
     * @param application el constructor de la aplicación Spring
     * @return el {@link SpringApplicationBuilder} configurado con la clase principal
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(CentroadoptivoApplication.class);
    }
}