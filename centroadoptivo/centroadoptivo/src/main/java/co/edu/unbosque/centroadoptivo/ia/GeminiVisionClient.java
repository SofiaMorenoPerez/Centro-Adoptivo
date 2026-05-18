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
public class GeminiVisionClient {

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";

    private final HttpClient CLIENTE = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public String analizarClasificacion(String imagenBase64) {

        String body = construirBody(imagenBase64);

        HttpRequest solicitud = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(URI.create(URL + apiKey))
                .setHeader("User-Agent", "Java 11 HttpClient")
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

        JsonObject imagenParte = new JsonObject();
        imagenParte.addProperty("mime_type", "image/jpeg");
        imagenParte.addProperty("data", imagenBase64);

        JsonObject inlineData = new JsonObject();
        inlineData.add("inline_data", imagenParte);

        JsonObject textoParte = new JsonObject();
        textoParte.addProperty("text",
                "Analiza esta imagen de un animal y responde UNICAMENTE " +
                "con una de estas dos palabras: DOMESTICO o NO_DOMESTICO " +
                "segun si el animal es domestico o salvaje.");

        JsonArray partes = new JsonArray();
        partes.add(inlineData);
        partes.add(textoParte);

        JsonObject contenido = new JsonObject();
        contenido.add("parts", partes);

        JsonArray contenidos = new JsonArray();
        contenidos.add(contenido);

        JsonObject bodyJson = new JsonObject();
        bodyJson.add("contents", contenidos);

        return new Gson().toJson(bodyJson);
    }

    private String extraerRespuesta(String bodyRespuesta) {
        JsonObject json = new Gson().fromJson(bodyRespuesta, JsonObject.class);
        return json.getAsJsonArray("candidates")
                .get(0)
                .getAsJsonObject()
                .getAsJsonObject("content")
                .getAsJsonArray("parts")
                .get(0)
                .getAsJsonObject()
                .get("text")
                .getAsString()
                .trim();
    }
    
    public String verificarCoincidenciaFotoDescripcion(String imagenBase64, String especie, String raza) {

        String body = construirBodyConPrompt(imagenBase64,
                "Analiza esta imagen y verifica si el animal corresponde a: " +
                "Especie: " + especie + ", Raza: " + raza + ". " +
                "Responde UNICAMENTE con: COINCIDE o NO_COINCIDE.");

        HttpRequest solicitud = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(URI.create(URL + apiKey))
                .setHeader("User-Agent", "Java 11 HttpClient")
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

    private String construirBodyConPrompt(String imagenBase64, String prompt) {

        JsonObject imagenParte = new JsonObject();
        imagenParte.addProperty("mime_type", "image/jpeg");
        imagenParte.addProperty("data", imagenBase64);

        JsonObject inlineData = new JsonObject();
        inlineData.add("inline_data", imagenParte);

        JsonObject textoParte = new JsonObject();
        textoParte.addProperty("text", prompt);

        JsonArray partes = new JsonArray();
        partes.add(inlineData);
        partes.add(textoParte);

        JsonObject contenido = new JsonObject();
        contenido.add("parts", partes);

        JsonArray contenidos = new JsonArray();
        contenidos.add(contenido);

        JsonObject bodyJson = new JsonObject();
        bodyJson.add("contents", contenidos);

        return new Gson().toJson(bodyJson);
    }
    
}