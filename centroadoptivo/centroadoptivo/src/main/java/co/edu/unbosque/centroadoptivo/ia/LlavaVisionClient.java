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
            .connectTimeout(Duration.ofSeconds(30)) // LLaVA necesita más tiempo
            .build();

    public String verificarBienestarAnimal(String imagenBase64) {

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

        JsonObject inputs = new JsonObject();
        inputs.addProperty("image", imagenBase64);
        inputs.addProperty("question",
                "Look at this animal in the image. " +
                "Does it appear to be healthy, well-fed and properly cared for? " +
                "Answer ONLY with one word: SALUDABLE or NO_SALUDABLE.");

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