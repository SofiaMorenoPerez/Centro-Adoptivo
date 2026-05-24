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

@Component
public class ClaudeVisionClient {

    @Value("${anthropic.api.key}")
    private String apiKey;

    private static final String URL = "https://api.anthropic.com/v1/messages";
    private static final String MODELO = "claude-haiku-4-5";

    private final HttpClient CLIENTE = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    public String verificarClasificacion(String imagenBase64, String clasificacion) {

        String prompt =
            "Look at this animal image. " +
            "Someone classified this animal as: " + clasificacion + ". " +
            "Do you agree? Answer ONLY with one word: CORRECT or INCORRECT.";

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