package co.edu.unbosque.centroadoptivo.ia;

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
 * Cliente de IA que se conecta a la API de Anthropic (Claude) para verificar
 * la clasificación doméstica o no doméstica de un animal a partir de su imagen.
 * <p>
 * Utiliza el modelo {@code claude-haiku-4-5} con capacidad de visión para
 * analizar imágenes y validar si la clasificación asignada es correcta.
 * </p>
 *
 * @author Centro Adoptivo
 * @version 1.0
 */
@Component
public class ClaudeVisionClient {

    /**
     * Clave de API de Anthropic inyectada desde {@code application.properties}.
     */
    @Value("${anthropic.api.key}")
    private String apiKey;

    /**
     * URL base del endpoint de mensajes de la API de Anthropic.
     */
    private static final String URL = "https://api.anthropic.com/v1/messages";

    /**
     * Identificador del modelo de Claude utilizado para el análisis de imágenes.
     */
    private static final String MODELO = "claude-haiku-4-5";

    /**
     * Cliente HTTP configurado con HTTP/1.1 y un tiempo de espera de 30 segundos.
     */
    private final HttpClient CLIENTE = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    /**
     * Verifica si la clasificación de un animal (doméstico o no doméstico)
     * es correcta según el análisis visual realizado por Claude.
     *
     * @param imagenBase64   imagen del animal codificada en Base64
     * @param clasificacion  clasificación a verificar, puede ser {@code "DOMESTIC"}
     *                       o {@code "NON_DOMESTIC"}
     * @return {@code "CORRECT"} si Claude está de acuerdo con la clasificación,
     *         {@code "INCORRECT"} si no lo está, o {@code "ERROR"} si ocurre
     *         algún problema durante la comunicación con la API
     */
    public String verificarClasificacion(String imagenBase64, String clasificacion) {

        String prompt =
            "Look at this animal image. " +
            "Someone classified this animal as: " + clasificacion + ". " +
            "Domestic animals include: dogs, cats, rabbits, hamsters, guinea pigs, " +
            "parrots, canaries, fish, turtles, ferrets and common pets. " +
            "Do you agree with the classification? Answer ONLY with one word: CORRECT or INCORRECT.";

        String body = construirBody(imagenBase64, prompt);

        HttpRequest solicitud = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(URI.create(URL))
                .setHeader("x-api-key", apiKey)
                .setHeader("anthropic-version", "2023-06-01")
                .setHeader("Content-Type", "application/json")
                .build();

        HttpResponse<String> respuesta = null;
        try {
            respuesta = CLIENTE.send(solicitud, HttpResponse.BodyHandlers.ofString());
            System.out.println("CLAUDE RESPONSE: " + respuesta.statusCode()
                    + " | " + respuesta.body());
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }

        return extraerRespuesta(respuesta.body());
    }

    /**
     * Construye el cuerpo JSON de la solicitud a la API de Claude,
     * incluyendo la imagen en Base64 y el prompt de verificación.
     *
     * @param imagenBase64 imagen codificada en Base64 a incluir en el mensaje
     * @param prompt       texto del prompt que se enviará junto a la imagen
     * @return cadena JSON lista para ser enviada como cuerpo de la solicitud HTTP
     */
    private String construirBody(String imagenBase64, String prompt) {

        JsonObject imagenSource = new JsonObject();
        imagenSource.addProperty("type", "base64");
        imagenSource.addProperty("media_type", "image/jpeg");
        imagenSource.addProperty("data", imagenBase64);

        JsonObject partImagen = new JsonObject();
        partImagen.addProperty("type", "image");
        partImagen.add("source", imagenSource);

        JsonObject partTexto = new JsonObject();
        partTexto.addProperty("type", "text");
        partTexto.addProperty("text", prompt);

        JsonArray content = new JsonArray();
        content.add(partImagen);
        content.add(partTexto);

        JsonObject mensaje = new JsonObject();
        mensaje.addProperty("role", "user");
        mensaje.add("content", content);

        JsonArray messages = new JsonArray();
        messages.add(mensaje);

        JsonObject bodyJson = new JsonObject();
        bodyJson.addProperty("model", MODELO);
        bodyJson.addProperty("max_tokens", 10);
        bodyJson.add("messages", messages);

        return new Gson().toJson(bodyJson);
    }

    /**
     * Extrae el texto de la respuesta JSON devuelta por la API de Claude.
     *
     * @param bodyRespuesta cadena JSON con la respuesta completa de la API
     * @return texto extraído de la respuesta, o {@code "ERROR"} si no puede
     *         ser procesada correctamente
     */
    private String extraerRespuesta(String bodyRespuesta) {
        try {
            JsonObject json = new Gson().fromJson(bodyRespuesta, JsonObject.class);
            return json.getAsJsonArray("content")
                    .get(0).getAsJsonObject()
                    .get("text").getAsString()
                    .trim();
        } catch (Exception e) {
            return "ERROR";
        }
    }
}