package co.edu.unbosque.centroadoptivo.ia;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Cliente de IA que se conecta a la API de Imagga para analizar imágenes
 * de animales y obtener etiquetas descriptivas mediante reconocimiento visual.
 * <p>
 * Utiliza autenticación Basic Auth con las credenciales de Imagga y envía
 * la imagen codificada en Base64 para obtener las etiquetas más relevantes.
 * </p>
 *
 * @author Centro Adoptivo
 * @version 1.0
 */
@Component
public class BlipVisionClient {

    /**
     * Clave de API de Imagga inyectada desde {@code application.properties}.
     */
    @Value("${imagga.api.key}")
    private String apiKey;

    /**
     * Secreto de API de Imagga inyectado desde {@code application.properties}.
     */
    @Value("${imagga.api.secret}")
    private String apiSecret;

    /**
     * URL base del endpoint de etiquetado de la API de Imagga.
     */
    private static final String URL = "https://api.imagga.com/v2/tags";

    /**
     * Cliente HTTP configurado con HTTP/1.1 y un tiempo de espera de 30 segundos.
     */
    private final HttpClient CLIENTE = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    /**
     * Analiza una imagen de animal enviándola a la API de Imagga y retorna
     * una descripción con las etiquetas más relevantes detectadas.
     *
     * @param imagenBytes arreglo de bytes que representa la imagen a analizar
     * @return cadena con las primeras 3 etiquetas detectadas separadas por espacio,
     *         o {@code "ERROR"} si ocurre algún problema durante el proceso
     */
    public String analizarDescripcion(byte[] imagenBytes) {
        try {
            String imagenBase64 = Base64.getEncoder().encodeToString(imagenBytes);

            String body = "image_base64=" + java.net.URLEncoder.encode(imagenBase64, "UTF-8");

            String credenciales = Base64.getEncoder()
                    .encodeToString((apiKey + ":" + apiSecret).getBytes());

            HttpRequest solicitud = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .uri(URI.create(URL))
                    .setHeader("Authorization", "Basic " + credenciales)
                    .setHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            HttpResponse<String> respuesta = CLIENTE.send(
                    solicitud, HttpResponse.BodyHandlers.ofString());

            System.out.println("IMAGGA RESPONSE: " + respuesta.statusCode()
                    + " | " + respuesta.body());

            return extraerRespuesta(respuesta.body());

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    /**
     * Extrae y construye una descripción textual a partir del cuerpo JSON
     * de la respuesta de Imagga, tomando las primeras 3 etiquetas con mayor
     * nivel de confianza.
     *
     * @param bodyRespuesta cadena JSON con la respuesta completa de la API de Imagga
     * @return cadena con las etiquetas extraídas separadas por espacio,
     *         o {@code "ERROR"} si la respuesta no puede ser procesada
     */
    private String extraerRespuesta(String bodyRespuesta) {
        try {
            JsonObject json = new Gson().fromJson(bodyRespuesta, JsonObject.class);
            JsonArray tags = json.getAsJsonObject("result")
                    .getAsJsonArray("tags");

            StringBuilder descripcion = new StringBuilder();
            int limite = Math.min(3, tags.size());
            for (int i = 0; i < limite; i++) {
                JsonObject tag = tags.get(i).getAsJsonObject();
                String nombre = tag.getAsJsonObject("tag")
                        .get("en").getAsString();
                descripcion.append(nombre).append(" ");
            }
            return descripcion.toString().trim();

        } catch (Exception e) {
            return "ERROR";
        }
    }
}