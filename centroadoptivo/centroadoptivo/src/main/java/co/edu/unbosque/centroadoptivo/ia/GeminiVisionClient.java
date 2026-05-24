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

/**
 * Cliente de IA que se conecta a la API de Google Gemini para analizar imágenes
 * de animales y detectar sus características principales, así como verificar
 * su clasificación como animal doméstico o no doméstico.
 * <p>
 * Utiliza el modelo {@code gemini-2.5-flash} con capacidad de visión para
 * procesar imágenes y generar respuestas estructuradas en formato JSON.
 * </p>
 *
 * @author Centro Adoptivo
 * @version 1.0
 */
@Component
public class GeminiVisionClient {

    /**
     * Clave de API de Google Gemini inyectada desde {@code application.properties}.
     */
    @Value("${gemini.api.key}")
    private String apiKey;

    /**
     * URL base del endpoint de generación de contenido de la API de Gemini.
     * La clave de API se concatena al final de esta URL en cada solicitud.
     */
    private static final String URL =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    /**
     * Cliente HTTP configurado con HTTP/1.1 y un tiempo de espera de 30 segundos.
     */
    private final HttpClient CLIENTE = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    /**
     * Analiza una imagen de animal y detecta sus datos principales mediante
     * visión artificial. Retorna un JSON con especie, raza, color y edad.
     * <p>
     * Ejemplo de respuesta esperada:
     * {@code {"especie":"Perro","raza":"Golden Retriever","color":"Dorado","edad":"ADULT"}}
     * </p>
     *
     * @param imagenBase64 imagen del animal codificada en Base64
     * @return cadena JSON con los campos {@code especie}, {@code raza},
     *         {@code color} y {@code edad}, o {@code null} si ocurre
     *         un error durante la comunicación con la API
     */
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
            System.out.println("GEMINI RESPONSE: " + respuesta.statusCode() + " | " + respuesta.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }

        return extraerRespuesta(respuesta.body());
    }

    /**
     * Verifica si un animal en la imagen es doméstico o no doméstico.
     * <p>
     * Considera como domésticos: perros, gatos, conejos, hamsters, cobayas,
     * cuyes, loros, pericos, canarios, peces de acuario, tortugas de tierra,
     * hurones y animales comunes de compañía.
     * </p>
     *
     * @param imagenBase64 imagen del animal codificada en Base64
     * @return {@code "DOMESTIC"} si el animal es doméstico,
     *         {@code "NON_DOMESTIC"} si es salvaje, o {@code "ERROR"}
     *         si ocurre algún problema durante la comunicación con la API
     */
    public String verificarClasificacion(String imagenBase64) {

        String prompt =
            "Analiza esta imagen de un animal y responde UNICAMENTE " +
            "con una de estas dos palabras: DOMESTIC o NON_DOMESTIC " +
            "segun si el animal es domestico o salvaje. " +
            "Considera DOMESTIC los siguientes animales: perros, gatos, conejos, " +
            "hamsters, cobayas, cuyes, loros, pericos, canarios, peces de acuario, " +
            "tortugas de tierra, hurones y cualquier animal común de compañía. " +
            "Considera NON_DOMESTIC: leones, tigres, osos, lobos, elefantes, " +
            "serpientes salvajes, cocodrilos y animales de zoológico o selva.";

        String body = construirBody(imagenBase64, prompt);

        HttpRequest solicitud = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(URI.create(URL + apiKey))
                .setHeader("Content-Type", "application/json")
                .build();

        HttpResponse<String> respuesta = null;
        try {
            respuesta = CLIENTE.send(solicitud, HttpResponse.BodyHandlers.ofString());
            System.out.println("GEMINI CLASIF RESPONSE: " + respuesta.statusCode() + " | " + respuesta.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "ERROR";
        }

        return extraerRespuesta(respuesta.body());
    }

    /**
     * Construye el cuerpo JSON de la solicitud a la API de Gemini,
     * incluyendo la imagen en Base64 como dato inline y el prompt de instrucción.
     *
     * @param imagenBase64 imagen codificada en Base64 a incluir en el mensaje
     * @param prompt       texto del prompt que se enviará junto a la imagen
     * @return cadena JSON lista para ser enviada como cuerpo de la solicitud HTTP
     */
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

    /**
     * Extrae el texto de la respuesta JSON devuelta por la API de Gemini,
     * navegando por la estructura de candidatos y partes de la respuesta.
     *
     * @param bodyRespuesta cadena JSON con la respuesta completa de la API
     * @return texto extraído del primer candidato y primera parte de la respuesta,
     *         o {@code "ERROR"} si la respuesta no puede ser procesada correctamente
     */
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