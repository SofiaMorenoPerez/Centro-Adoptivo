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
public class ChatGptVisionClient {

    @Value("${openai.api.key}")
    private String apiKey;

    private static final String URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODELO = "gpt-4o";

    private final HttpClient CLIENTE = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public String validarInformacionMascota(String especie, String raza, String descripcion) {

        String prompt = "Eres un validador de información de mascotas. " +
                "Te voy a dar la siguiente información de una mascota: " +
                "Especie: " + especie + ", Raza: " + raza + ", Descripción: " + descripcion + ". " +
                "Responde UNICAMENTE con una de estas dos palabras: " +
                "VALIDO si la información es coherente y real, " +
                "NO_VALIDO si hay inconsistencias o información falsa.";

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