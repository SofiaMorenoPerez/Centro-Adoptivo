package co.edu.unbosque.centroadoptivo.ia;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Cliente de IA que se conecta a la API de Mistral para moderar las descripciones
 * u observaciones ingresadas por el usuario sobre un animal en adopción.
 * <p>
 * Utiliza el modelo {@code mistral-small-latest} para verificar que el contenido
 * sea apropiado, coherente y no contenga información fraudulenta o inapropiada.
 * </p>
 *
 * @author Centro Adoptivo
 * @version 1.0
 */
@Component
public class MistralVisionClient {

    /**
     * Clave de API de Mistral inyectada desde {@code application.properties}.
     */
    @Value("${mistral.api.key}")
    private String apiKey;

    /**
     * URL base del endpoint de chat de la API de Mistral.
     */
    private static final String URL = "https://api.mistral.ai/v1/chat/completions";

    /**
     * Identificador del modelo de Mistral utilizado para la moderación de contenido.
     */
    private static final String MODELO = "mistral-small-latest";

    /**
     * Cliente HTTP configurado con HTTP/2 y un tiempo de espera de 10 segundos.
     */
    private final HttpClient CLIENTE = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * Modera la descripción u observaciones de una mascota verificando que
     * el contenido sea apropiado, coherente y libre de lenguaje inapropiado
     * o información sospechosa.
     *
     * @param descripcion texto con las observaciones ingresadas por el usuario
     *                    sobre la mascota que desea dar en adopción
     * @return {@code "APROBADO"} si la descripción es adecuada para la plataforma,
     *         o {@code "RECHAZADO"} si contiene contenido inapropiado, incoherente
     *         o sospechoso
     */
    public String moderarDescripcion(String descripcion) {
        String prompt = "Eres un moderador de contenido para una plataforma de adopción de mascotas. " +
                "Analiza la siguiente descripción de una mascota: '" + descripcion + "'. " +
                "Verifica que: " +
                "1. No contenga lenguaje inapropiado u ofensivo. " +
                "2. Sea una descripción real y coherente de una mascota. " +
                "3. No contenga información sospechosa o fraudulenta. " +
                "Responde UNICAMENTE con una de estas palabras: " +
                "APROBADO si la descripción es adecuada, " +
                "RECHAZADO si hay algo inapropiado o sospechoso.";

        String body = construirBody(prompt);

        HttpRequest solicitud = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(URI.create(URL))
                .setHeader("Authorization", "Bearer " + apiKey)
                .setHeader("Content-Type", "application/json")
                .build();

        HttpResponse<String> respuesta = null;
        try {
            respuesta = CLIENTE.send(solicitud, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return extraerRespuesta(respuesta.body());
    }

    /**
     * Construye el cuerpo JSON de la solicitud a la API de Mistral
     * con el prompt de moderación de contenido.
     *
     * @param prompt texto del prompt a enviar al modelo
     * @return cadena JSON lista para ser enviada como cuerpo de la solicitud HTTP
     */
    private String construirBody(String prompt) {
        JsonObject mensaje = new JsonObject();
        mensaje.addProperty("role", "user");
        mensaje.addProperty("content", prompt);

        JsonArray messages = new JsonArray();
        messages.add(mensaje);

        JsonObject bodyJson = new JsonObject();
        bodyJson.addProperty("model", MODELO);
        bodyJson.addProperty("max_tokens", 10);
        bodyJson.add("messages", messages);

        return new Gson().toJson(bodyJson);
    }

    /**
     * Extrae el texto de la respuesta JSON devuelta por la API de Mistral.
     *
     * @param bodyRespuesta cadena JSON con la respuesta completa de la API
     * @return texto extraído del primer choice de la respuesta con el resultado
     *         de la moderación
     */
    private String extraerRespuesta(String bodyRespuesta) {
        JsonObject json = new Gson().fromJson(bodyRespuesta, JsonObject.class);
        return json.getAsJsonArray("choices")
                .get(0)
                .getAsJsonObject()
                .get("message")
                .getAsJsonObject()
                .get("content")
                .getAsString()
                .trim();
    }
}