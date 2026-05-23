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
public class BlipVisionClient {

    @Value("${huggingface.api.key}")
    private String apiKey;

    private static final String URL =
            "https://api-inference.huggingface.co/models/Salesforce/blip-image-captioning-base";

    private final HttpClient CLIENTE = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    public String analizarDescripcion(byte[] imagenBytes) {
        try {
            HttpRequest solicitud = construirSolicitud(imagenBytes);
            HttpResponse<String> respuesta = CLIENTE.send(
                    solicitud,
                    HttpResponse.BodyHandlers.ofString());
            return extraerRespuesta(respuesta.body());
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    private HttpRequest construirSolicitud(
            byte[] imagenBytes) {

        return HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers
                        .ofByteArray(imagenBytes))
                .uri(URI.create(URL))
                .setHeader(
                        "Authorization",
                        "Bearer " + apiKey)
                .setHeader(
                        "Content-Type",
                        "image/jpeg")
                .setHeader(
                        "User-Agent",
                        "Java 11 HttpClient")
                .build();
    }

    private String extraerRespuesta(
            String bodyRespuesta) {

        JsonArray jsonArray =
                new Gson().fromJson(
                        bodyRespuesta,
                        JsonArray.class);

        JsonObject objeto =
                jsonArray.get(0)
                .getAsJsonObject();

        return objeto.get("generated_text")
                .getAsString()
                .trim();
    }
}
//edit