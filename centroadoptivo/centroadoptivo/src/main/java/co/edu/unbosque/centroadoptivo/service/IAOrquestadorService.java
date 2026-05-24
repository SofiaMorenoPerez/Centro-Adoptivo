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

        // Datos que las IAs van a detectar
        String especie = "desconocido";
        String raza = "desconocida";
        String color = "desconocido";
        String edad = "ADULT";
        String clasificacion = "DOMESTIC";

        // ── IA 1: Gemini detecta especie, raza, color y edad ────────────
        try {
            String jsonRespuesta = geminiClient.detectarDatosAnimal(imagenBase64);

            // Limpiamos posibles bloques de código markdown
            String jsonLimpio = jsonRespuesta
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            JsonObject datos = new Gson().fromJson(jsonLimpio, JsonObject.class);
            especie = datos.get("especie").getAsString();
            raza = datos.get("raza").getAsString();
            color = datos.get("color").getAsString();
            edad = datos.get("edad").getAsString();

            votos++;
            detalle.append("Gemini: detectó ")
                    .append(especie).append(" ")
                    .append(raza).append(" | ");

        } catch (Exception e) {
            detalle.append("Gemini: ERROR | ");
        }

        // ── IA 2: Gemini verifica clasificación ──────────────────────────
        try {
            String resultadoClasificacion = geminiClient.verificarClasificacion(imagenBase64);
            clasificacion = resultadoClasificacion.contains("NON") ?
                    "NON_DOMESTIC" : "DOMESTIC";
            votos++;
            detalle.append("Gemini clasificación: ")
                    .append(clasificacion).append(" | ");
        } catch (Exception e) {
            detalle.append("Gemini clasificación: ERROR | ");
        }
        
     // ── Validar especie doméstica ────────────────────────────────────
        try {
            LanzadorDeExcepcion.verificarEspecieDomestica(especie);
        } catch (ValidacionIAException e) {
            ResultadoIADTO resultado = new ResultadoIADTO();
            resultado.setAprobado(false);
            resultado.setVotos(0);
            resultado.setTotalIAs(totalIAs);
            resultado.setDetalle(e.getMessage());
            return resultado;
        }

        // ── IA 3: OpenRouter verifica la clasificación ───────────────────
        try {
            String resultado = claudeClient.verificarClasificacion(
                    imagenBase64, clasificacion);
            if (resultado.contains("CORRECT")) votos++;
            detalle.append("Claude: ").append(resultado).append(" | ");
        } catch (Exception e) {
            detalle.append("Claude: ERROR | ");
        }

        // ── IA 4: BLIP describe el animal ────────────────────────────────
        try {
            String descripcionBlip = blipClient.analizarDescripcion(imagenBytes);
            // Si BLIP menciona la especie detectada es un buen signo
            if (descripcionBlip.toLowerCase().contains(especie.toLowerCase()) ||
                descripcionBlip.toLowerCase().contains("dog") ||
                descripcionBlip.toLowerCase().contains("cat") ||
                descripcionBlip.toLowerCase().contains("animal")) {
                votos++;
            }
            detalle.append("BLIP: ").append(descripcionBlip).append(" | ");
        } catch (Exception e) {
            detalle.append("BLIP: ERROR | ");
        }

        // ── IA 5: DeepSeek valida coherencia de todo ─────────────────────
        try {
            String resultado = deepSeekClient.validarCoherencia(
                    especie, raza, color, edad, clasificacion, observaciones);
            if (resultado.equals("VALIDO")) votos++;
            detalle.append("DeepSeek: ").append(resultado).append(" | ");
        } catch (Exception e) {
            detalle.append("DeepSeek: ERROR | ");
        }

        // ── Mistral modera las observaciones ─────────────────────────────
        try {
            String resultado = mistralClient.moderarDescripcion(observaciones);
            if (resultado.equals("APROBADO")) votos++;
            detalle.append("Mistral: ").append(resultado);
        } catch (Exception e) {
            detalle.append("Mistral: ERROR");
        }

        // ── Resultado final ───────────────────────────────────────────────
        boolean aprobado = votos >= 4;

        ResultadoIADTO resultado = new ResultadoIADTO();
        resultado.setEspecie(especie);
        resultado.setRaza(raza);
        resultado.setColor(color);
        resultado.setEdad(edad);
        resultado.setClasificacion(clasificacion);
        resultado.setAprobado(aprobado);
        resultado.setVotos(votos);
        resultado.setTotalIAs(totalIAs);
        resultado.setDetalle(detalle.toString());

        return resultado;
    }
}