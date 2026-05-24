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
 * Cliente de IA que se conecta a la API de DeepSeek para validar la coherencia
 * de los datos detectados de un animal (especie, raza, color, edad, clasificación
 * y observaciones) mediante procesamiento de lenguaje natural.
 * <p>
 * Utiliza el modelo {@code deepseek-chat} para verificar que los datos del animal
 * sean consistentes entre sí antes de ser registrados en el sistema.
 * </p>
 *
 * @author Centro Adoptivo
 * @version 1.0
 */
@Component
public class DeepSeekVisionClient {

    /**
     * Clave de API de DeepSeek inyectada desde {@code application.properties}.
     */
    @Value("${deepseek.api.key}")
    private String apiKey;

    /**
     * URL base del endpoint de chat de la API de DeepSeek.
     */
    private static final String URL = "https://api.deepseek.com/chat/completions";

    /**
     * Identificador del modelo de DeepSeek utilizado para la validación.
     */
    private static final String MODELO = "deepseek-chat";

    /**
     * Cliente HTTP configurado con HTTP/1.1 y un tiempo de espera de 30 segundos.
     */
    private final HttpClient CLIENTE = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    /**
     * Valida que los datos detectados de un animal sean coherentes entre sí,
     * verificando que la raza corresponda a la especie, que el color sea posible
     * para esa raza, y que las observaciones sean consistentes con los demás datos.
     *
     * @param especie        especie del animal detectada por Gemini
     * @param raza           raza del animal detectada por Gemini
     * @param color          color principal del animal detectado por Gemini
     * @param edad           edad del animal detectada por Gemini
     * @param clasificacion  clasificación del animal ({@code "DOMESTIC"} o {@code "NON_DOMESTIC"})
     * @param observaciones  observaciones ingresadas por el usuario sobre el animal
     * @return {@code "VALIDO"} si los datos son coherentes entre sí,
     *         {@code "NO_VALIDO"} si hay inconsistencias, o {@code "ERROR"}
     *         si ocurre algún problema durante la comunicación con la API
     */
    public String validarCoherencia(String especie, String raza,
            String color, String edad, String clasificacion, String observaciones) {

        String prompt =
            "Eres un validador de información de mascotas. " +
            "Verifica que los siguientes datos sean coherentes entre sí: " +
            "Especie: " + especie + ", " +
            "Raza: " + raza + ", " +
            "Color: " + color + ", " +
            "Edad: " + edad + ", " +
            "Clasificación: " + clasificacion + ", " +
            "Observaciones: " + observaciones + ". " +
            "Verifica que la raza corresponda a la especie, que el color sea posible " +
            "para esa raza, y que las observaciones sean coherentes. " +
            "Responde UNICAMENTE con: VALIDO o NO_VALIDO.";

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
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "ERROR";
        }

        return extraerRespuesta(respuesta.body());
    }

    /**
     * Construye el cuerpo JSON de la solicitud a la API de DeepSeek
     * con el prompt de validación de coherencia.
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
     * Extrae el texto de la respuesta JSON devuelta por la API de DeepSeek.
     *
     * @param bodyRespuesta cadena JSON con la respuesta completa de la API
     * @return texto extraído del primer choice de la respuesta, o {@code "ERROR"}
     *         si la respuesta no puede ser procesada correctamente
     */
    private String extraerRespuesta(String bodyRespuesta) {
        try {
            JsonObject json = new Gson().fromJson(bodyRespuesta, JsonObject.class);
            return json.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .get("message").getAsJsonObject()
                    .get("content").getAsString()
                    .trim();
        } catch (Exception e) {
            return "ERROR";
        }
    }
}