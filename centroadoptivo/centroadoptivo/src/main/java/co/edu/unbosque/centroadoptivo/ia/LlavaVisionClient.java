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
public class LlavaVisionClient {

    @Value("${huggingface.api.key}")
    private String apiKey;

    private static final String URL =
        "https://api-inference.huggingface.co/models/llava-hf/llava-1.5-7b-hf";

    private final HttpClient CLIENTE = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    public String verificarBienestarAnimal(String imagenBase64, String color, String edad) {

        String body = construirBody(imagenBase64, color, edad);

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

    private String construirBody(String imagenBase64, String color, String edad) {

        JsonObject inputs = new JsonObject();
        inputs.addProperty("image", imagenBase64);
        inputs.addProperty("question",
                "Analyze this animal in the image and verify: " +
                "1. Does it appear healthy and well cared for? " +
                "2. Is its predominant color: " + color + "? " +
                "3. Does its approximate life stage match: " + edad + "? " +
                "Answer ONLY with one word: SALUDABLE if everything matches, " +
                "NO_SALUDABLE if something does not match.");

        JsonObject bodyJson = new JsonObject();
        bodyJson.add("inputs", inputs);

        return new Gson().toJson(bodyJson);
    }

    private String extraerRespuesta(String bodyRespuesta) {
        JsonArray json = new Gson().fromJson(bodyRespuesta, JsonArray.class);
        String respuestaTexto = json.get(0)
                .getAsJsonObject()
                .get("generated_text")
                .getAsString()
                .trim()
                .toUpperCase();

        if (respuestaTexto.contains("SALUDABLE") && !respuestaTexto.contains("NO_SALUDABLE")) {
            return "SALUDABLE";
        } else {
            return "NO_SALUDABLE";
        }
    }
}