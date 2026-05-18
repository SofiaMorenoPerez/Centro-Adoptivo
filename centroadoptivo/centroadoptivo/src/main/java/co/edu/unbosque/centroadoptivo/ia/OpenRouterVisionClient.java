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

    public String analizarClasificacion(String imagenBase64) {

        String body = construirBody(imagenBase64);

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

    private String construirBody(String imagenBase64) {

        JsonObject imagenUrl = new JsonObject();
        imagenUrl.addProperty("url", "data:image/jpeg;base64," + imagenBase64);

        JsonObject partImagen = new JsonObject();
        partImagen.addProperty("type", "image_url");
        partImagen.add("image_url", imagenUrl);

        // Parte del texto
        JsonObject partTexto = new JsonObject();
        partTexto.addProperty("type", "text");
        partTexto.addProperty("text",
                "Analiza esta imagen de un animal y responde UNICAMENTE " +
                "con una de estas dos palabras: DOMESTICO o NO_DOMESTICO " +
                "segun si el animal es domestico o salvaje.");

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