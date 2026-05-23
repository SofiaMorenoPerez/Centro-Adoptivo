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
public class DeepSeekVisionClient {

    @Value("${deepseek.api.key}")
    private String apiKey;

    private static final String URL = "https://api.deepseek.com/chat/completions";
    private static final String MODELO = "deepseek-chat";

    private final HttpClient CLIENTE = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    // Valida que los datos detectados sean coherentes entre sí
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