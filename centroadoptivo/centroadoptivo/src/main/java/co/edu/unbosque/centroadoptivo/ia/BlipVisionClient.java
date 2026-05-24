package co.edu.unbosque.centroadoptivo.ia;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Component
public class BlipVisionClient {

	@Value("${imagga.api.key}")
	private String apiKey;

	@Value("${imagga.api.secret}")
	private String apiSecret;

    private static final String URL = "https://api.imagga.com/v2/tags";

    private final HttpClient CLIENTE = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    public String analizarDescripcion(byte[] imagenBytes) {
        try {
            // Imagga recibe la imagen como base64 en el body
            String imagenBase64 = Base64.getEncoder().encodeToString(imagenBytes);

            String body = "image_base64=" + java.net.URLEncoder.encode(imagenBase64, "UTF-8");

            // Credenciales en Base64 para Basic Auth
            String credenciales = Base64.getEncoder()
                    .encodeToString((apiKey + ":" + apiSecret).getBytes());

            HttpRequest solicitud = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .uri(URI.create(URL))
                    .setHeader("Authorization", "Basic " + credenciales)
                    .setHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            HttpResponse<String> respuesta = CLIENTE.send(
                    solicitud, HttpResponse.BodyHandlers.ofString());

            System.out.println("IMAGGA RESPONSE: " + respuesta.statusCode() 
                    + " | " + respuesta.body());

            return extraerRespuesta(respuesta.body());

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    private String extraerRespuesta(String bodyRespuesta) {
        try {
            JsonObject json = new Gson().fromJson(bodyRespuesta, JsonObject.class);
            JsonArray tags = json.getAsJsonObject("result")
                    .getAsJsonArray("tags");

            StringBuilder descripcion = new StringBuilder();
            // Tomamos las primeras 3 etiquetas con mayor confianza
            int limite = Math.min(3, tags.size());
            for (int i = 0; i < limite; i++) {
                JsonObject tag = tags.get(i).getAsJsonObject();
                String nombre = tag.getAsJsonObject("tag")
                        .get("en").getAsString();
                descripcion.append(nombre).append(" ");
            }
            return descripcion.toString().trim();

        } catch (Exception e) {
            return "ERROR";
        }
    }
}