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

@Component
public class OpenRouterVisionClient {

    @Value("${openrouter.api.key}")
    private String apiKey;

    private static final String URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String MODELO = "meta-llama/llama-3.2-11b-vision-instruct:free";

    private final HttpClient CLIENTE = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    // Verifica si la clasificación detectada por Gemini es correcta
    public String verificarClasificacion(String imagenBase64, String clasificacion) {

        String prompt =
            "Look at this animal image. " +
            "Someone classified this animal as: " + clasificacion + ". " +
            "Do you agree? Answer ONLY with one word: CORRECT or INCORRECT.";

        String body = construirBody(imagenBase64, prompt);

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

    private String construirBody(String imagenBase64, String prompt) {

        JsonObject imagenUrl = new JsonObject();
        imagenUrl.addProperty("url", "data:image/jpeg;base64," + imagenBase64);

        JsonObject partImagen = new JsonObject();
        partImagen.addProperty("type", "image_url");
        partImagen.add("image_url", imagenUrl);

        JsonObject partTexto = new JsonObject();
        partTexto.addProperty("type", "text");
        partTexto.addProperty("text", prompt);

        JsonArray content = new JsonArray();
        content.add(partTexto);
        content.add(partImagen);

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