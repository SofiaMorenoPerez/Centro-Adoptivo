package co.edu.unbosque.centroadoptivo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import co.edu.unbosque.centroadoptivo.dto.ResultadoIADTO;
import co.edu.unbosque.centroadoptivo.exception.LanzadorDeExcepcion;
import co.edu.unbosque.centroadoptivo.exception.ValidacionIAException;
import co.edu.unbosque.centroadoptivo.ia.BlipVisionClient;
import co.edu.unbosque.centroadoptivo.ia.ClaudeVisionClient;
import co.edu.unbosque.centroadoptivo.ia.DeepSeekVisionClient;
import co.edu.unbosque.centroadoptivo.ia.GeminiVisionClient;
import co.edu.unbosque.centroadoptivo.ia.MistralVisionClient;

@Service
public class IAOrquestadorService {

    @Autowired private GeminiVisionClient geminiClient;
    @Autowired private ClaudeVisionClient claudeClient;
    @Autowired private BlipVisionClient blipClient;
    @Autowired private DeepSeekVisionClient deepSeekClient;
    @Autowired private MistralVisionClient mistralClient;

    public ResultadoIADTO detectarYValidar(
            String imagenBase64,
            byte[] imagenBytes,
            String observaciones) {

        int votos = 0;
        int totalIAs = 5;
        StringBuilder detalle = new StringBuilder();

        String[] datosAnimal = detectarConGemini(imagenBase64, detalle);
        String especie = datosAnimal[0];
        String raza = datosAnimal[1];
        String color = datosAnimal[2];
        String edad = datosAnimal[3];

        try {
            LanzadorDeExcepcion.verificarEspecieDomestica(especie);
        } catch (ValidacionIAException e) {
            return resultadoRechazado(e.getMessage(), totalIAs);
        }

        if (!especie.equals("desconocido")) votos++;

        String clasificacion = clasificarConGemini(imagenBase64, detalle);
        if (!clasificacion.equals("ERROR")) votos++;

        votos += validarConClaude(imagenBase64, clasificacion, detalle);
        votos += analizarConImagga(imagenBytes, especie, detalle);
        votos += validarConDeepSeek(especie, raza, color, edad, clasificacion, observaciones, detalle);
        votos += moderarConMistral(observaciones, detalle);

        boolean aprobado = votos >= 4;
        return construirResultado(especie, raza, color, edad,
                clasificacion, aprobado, votos, totalIAs, detalle.toString());
    }


    private String[] detectarConGemini(String imagenBase64, StringBuilder detalle) {
        String especie = "desconocido";
        String raza = "desconocida";
        String color = "desconocido";
        String edad = "ADULT";
        try {
            String jsonRespuesta = geminiClient.detectarDatosAnimal(imagenBase64);
            String jsonLimpio = jsonRespuesta
                    .replace("```json", "").replace("```", "").trim();
            JsonObject datos = new Gson().fromJson(jsonLimpio, JsonObject.class);
            especie = datos.get("especie").getAsString();
            raza = datos.get("raza").getAsString();
            color = datos.get("color").getAsString();
            edad = datos.get("edad").getAsString();
            detalle.append("Gemini: detectó ").append(especie)
                   .append(" ").append(raza).append(" | ");
        } catch (Exception e) {
            detalle.append("Gemini: ERROR | ");
        }
        return new String[]{especie, raza, color, edad};
    }

    private String clasificarConGemini(String imagenBase64, StringBuilder detalle) {
        try {
            String resultado = geminiClient.verificarClasificacion(imagenBase64);
            String clasificacion = resultado.contains("NON") ? "NON_DOMESTIC" : "DOMESTIC";
            detalle.append("Gemini clasificación: ").append(clasificacion).append(" | ");
            return clasificacion;
        } catch (Exception e) {
            detalle.append("Gemini clasificación: ERROR | ");
            return "ERROR";
        }
    }

    private int validarConClaude(String imagenBase64, String clasificacion, StringBuilder detalle) {
        try {
            String resultado = claudeClient.verificarClasificacion(imagenBase64, clasificacion);
            detalle.append("Claude: ").append(resultado).append(" | ");
            return resultado.contains("CORRECT") ? 1 : 0;
        } catch (Exception e) {
            detalle.append("Claude: ERROR | ");
            return 0;
        }
    }

    private int analizarConImagga(byte[] imagenBytes, String especie, StringBuilder detalle) {
        try {
            String descripcion = blipClient.analizarDescripcion(imagenBytes);
            detalle.append("Imagga: ").append(descripcion).append(" | ");
            if (descripcion.toLowerCase().contains(especie.toLowerCase()) ||
                descripcion.toLowerCase().contains("dog") ||
                descripcion.toLowerCase().contains("cat") ||
                descripcion.toLowerCase().contains("animal") ||
                descripcion.toLowerCase().contains("pet")) {
                return 1;
            }
        } catch (Exception e) {
            detalle.append("Imagga: ERROR | ");
        }
        return 0;
    }

    private int validarConDeepSeek(String especie, String raza, String color,
            String edad, String clasificacion, String observaciones,
            StringBuilder detalle) {
        try {
            String resultado = deepSeekClient.validarCoherencia(
                    especie, raza, color, edad, clasificacion, observaciones);
            detalle.append("DeepSeek: ").append(resultado).append(" | ");
            return resultado.equals("VALIDO") ? 1 : 0;
        } catch (Exception e) {
            detalle.append("DeepSeek: ERROR | ");
            return 0;
        }
    }

    private int moderarConMistral(String observaciones, StringBuilder detalle) {
        try {
            String resultado = mistralClient.moderarDescripcion(observaciones);
            detalle.append("Mistral: ").append(resultado);
            return resultado.equals("APROBADO") ? 1 : 0;
        } catch (Exception e) {
            detalle.append("Mistral: ERROR");
            return 0;
        }
    }


    private ResultadoIADTO resultadoRechazado(String mensaje, int totalIAs) {
        ResultadoIADTO resultado = new ResultadoIADTO();
        resultado.setAprobado(false);
        resultado.setVotos(0);
        resultado.setTotalIAs(totalIAs);
        resultado.setDetalle(mensaje);
        return resultado;
    }

    private ResultadoIADTO construirResultado(String especie, String raza,
            String color, String edad, String clasificacion,
            boolean aprobado, int votos, int totalIAs, String detalle) {
        ResultadoIADTO resultado = new ResultadoIADTO();
        resultado.setEspecie(especie);
        resultado.setRaza(raza);
        resultado.setColor(color);
        resultado.setEdad(edad);
        resultado.setClasificacion(clasificacion);
        resultado.setAprobado(aprobado);
        resultado.setVotos(votos);
        resultado.setTotalIAs(totalIAs);
        resultado.setDetalle(detalle);
        return resultado;
    }
}