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

    private static final String URL =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";

    private final HttpClient CLIENTE = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    // Detecta especie, raza, color y edad desde la imagen
    // Devuelve JSON: {"especie":"Perro","raza":"Golden Retriever","color":"Dorado","edad":"ADULT"}
    public String detectarDatosAnimal(String imagenBase64) {

        String prompt =
            "Analiza esta imagen de un animal y responde UNICAMENTE con un JSON " +
            "con este formato exacto, sin explicaciones ni texto adicional: " +
            "{\"especie\":\"valor\",\"raza\":\"valor\",\"color\":\"valor\",\"edad\":\"valor\"} " +
            "Para especie usa el nombre en español (Perro, Gato, Conejo, etc). " +
            "Para raza usa el nombre de la raza en español o inglés. " +
            "Para color describe el color principal del animal. " +
            "Para edad usa UNICAMENTE una de estas opciones: PUPPY, YOUNG, ADULT, SENIOR.";

        String body = construirBody(imagenBase64, prompt);

        HttpRequest solicitud = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(URI.create(URL + apiKey))
                .setHeader("Content-Type", "application/json")
                .build();

        HttpResponse<String> respuesta = null;
        try {
            respuesta = CLIENTE.send(solicitud, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }

        return extraerRespuesta(respuesta.body());
    }

    // Verifica clasificación doméstico/no doméstico
    public String verificarClasificacion(String imagenBase64) {

        String prompt =
            "Analiza esta imagen de un animal y responde UNICAMENTE " +
            "con una de estas dos palabras: DOMESTIC o NON_DOMESTIC " +
            "segun si el animal es domestico o salvaje.";

        String body = construirBody(imagenBase64, prompt);

        HttpRequest solicitud = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(URI.create(URL + apiKey))
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

    private String extraerRespuesta(String bodyRespuesta) {
        try {
            JsonObject json = new Gson().fromJson(bodyRespuesta, JsonObject.class);
            return json.getAsJsonArray("candidates")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("content")
                    .getAsJsonArray("parts")
                    .get(0).getAsJsonObject()
                    .get("text").getAsString()
                    .trim();
        } catch (Exception e) {
            return "ERROR";
        }
    }
}