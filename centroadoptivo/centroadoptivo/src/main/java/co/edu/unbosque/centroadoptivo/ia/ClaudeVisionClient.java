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
public class ClaudeVisionClient {

    @Value("${claude.api.key}")
    private String apiKey;

    private static final String URL = "https://api.anthropic.com/v1/messages";
    private static final String MODELO = "claude-3-5-sonnet-20241022";

    private final HttpClient CLIENTE = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public String analizarClasificacion(String imagenBase64) {

        String body = construirBody(imagenBase64);

        HttpRequest solicitud = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(URI.create(URL))
                .setHeader("User-Agent", "Java 11 HttpClient")
                .setHeader("x-api-key", apiKey)
                .setHeader("anthropic-version", "2023-06-01")
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

    private String construirBody(String imagenBase64) {

        JsonObject fuente = new JsonObject();
        fuente.addProperty("type", "base64");
        fuente.addProperty("media_type", "image/jpeg");
        fuente.addProperty("data", imagenBase64);

        JsonObject partImagen = new JsonObject();
        partImagen.addProperty("type", "image");
        partImagen.add("source", fuente);

        JsonObject partTexto = new JsonObject();
        partTexto.addProperty("type", "text");
        partTexto.addProperty("text",
                "Analiza esta imagen de un animal y responde UNICAMENTE " +
                "con una de estas dos palabras: DOMESTICO o NO_DOMESTICO " +
                "segun si el animal es domestico o salvaje.");

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
        JsonObject json = new Gson().fromJson(bodyRespuesta, JsonObject.class);
        return json.getAsJsonArray("content")
                .get(0)
                .getAsJsonObject()
                .get("text")
                .getAsString()
                .trim();
    }
}