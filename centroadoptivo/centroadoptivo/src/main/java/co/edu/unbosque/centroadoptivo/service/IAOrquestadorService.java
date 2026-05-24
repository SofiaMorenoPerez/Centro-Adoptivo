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

/**
 * Servicio orquestador que coordina las 5 IAs para detectar
 * y validar la información de un animal a partir de su imagen.
 *
 * <p>El flujo es el siguiente:
 * <ol>
 *   <li>Gemini detecta especie, raza, color y edad</li>
 *   <li>Gemini verifica la clasificación (doméstico o no)</li>
 *   <li>Claude confirma la clasificación</li>
 *   <li>BLIP analiza la descripción visual del animal</li>
 *   <li>DeepSeek valida la coherencia de todos los datos</li>
 *   <li>Mistral modera las observaciones del usuario</li>
 * </ol>
 *
 * <p>Se requieren mínimo 4 votos de 5 para aprobar el registro.
 *
 * @author Centro Adoptivo Unbosque
 * @version 1.0
 */
@Service
public class IAOrquestadorService {

    @Autowired private GeminiVisionClient geminiClient;
    @Autowired private ClaudeVisionClient claudeClient;
    @Autowired private BlipVisionClient blipClient;
    @Autowired private DeepSeekVisionClient deepSeekClient;
    @Autowired private MistralVisionClient mistralClient;

    /**
     * Orquesta las 5 IAs para detectar los datos del animal
     * a partir de la imagen y validar que sea apto para adopción.
     *
     * @param imagenBase64  imagen del animal codificada en Base64
     * @param imagenBytes   imagen del animal en bytes
     * @param observaciones observaciones ingresadas por el usuario
     * @return {@link ResultadoIADTO} con los datos detectados y el resultado de la validación
     */
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
        votos += validarConDeepSeek(especie, raza, color, edad,
                clasificacion, observaciones, detalle);
        votos += moderarConMistral(observaciones, detalle);

        boolean aprobado = votos >= 4;
        return construirResultado(especie, raza, color, edad,
                clasificacion, aprobado, votos, totalIAs, detalle.toString());
    }

    /**
     * Llama a Gemini para detectar especie, raza, color y edad del animal.
     *
     * @param imagenBase64 imagen en Base64
     * @param detalle      StringBuilder donde se acumula el detalle de cada IA
     * @return arreglo con [especie, raza, color, edad]
     */
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

    /**
     * Llama a Gemini para clasificar el animal como doméstico o no doméstico.
     *
     * @param imagenBase64 imagen en Base64
     * @param detalle      StringBuilder donde se acumula el detalle de cada IA
     * @return "DOMESTIC", "NON_DOMESTIC" o "ERROR" si falla
     */
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

    /**
     * Llama a Claude para confirmar la clasificación doméstico/no doméstico
     * detectada por Gemini.
     *
     * @param imagenBase64  imagen en Base64
     * @param clasificacion clasificación detectada por Gemini
     * @param detalle       StringBuilder donde se acumula el detalle de cada IA
     * @return 1 si Claude confirma la clasificación, 0 si no o si hay error
     */
    private int validarConClaude(String imagenBase64,
            String clasificacion, StringBuilder detalle) {
        try {
            String resultado = claudeClient.verificarClasificacion(
                    imagenBase64, clasificacion);
            detalle.append("Claude: ").append(resultado).append(" | ");
            return resultado.contains("CORRECT") ? 1 : 0;
        } catch (Exception e) {
            detalle.append("Claude: ERROR | ");
            return 0;
        }
    }

    /**
     * Llama a BLIP para obtener una descripción visual del animal
     * y verificar que mencione la especie detectada.
     *
     * @param imagenBytes bytes de la imagen
     * @param especie     especie detectada por Gemini
     * @param detalle     StringBuilder donde se acumula el detalle de cada IA
     * @return 1 si la descripción menciona la especie o palabras clave, 0 si no
     */
    private int analizarConImagga(byte[] imagenBytes,
            String especie, StringBuilder detalle) {
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

    /**
     * Llama a DeepSeek para validar que todos los datos detectados
     * sean coherentes entre sí.
     *
     * @param especie        especie detectada
     * @param raza           raza detectada
     * @param color          color detectado
     * @param edad           edad detectada
     * @param clasificacion  clasificación detectada
     * @param observaciones  observaciones del usuario
     * @param detalle        StringBuilder donde se acumula el detalle de cada IA
     * @return 1 si DeepSeek considera los datos válidos, 0 si no o si hay error
     */
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

    /**
     * Llama a Mistral para moderar las observaciones ingresadas
     * por el usuario y verificar que no contengan contenido inapropiado.
     *
     * @param observaciones observaciones del usuario
     * @param detalle       StringBuilder donde se acumula el detalle de cada IA
     * @return 1 si Mistral aprueba las observaciones, 0 si no o si hay error
     */
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

    /**
     * Construye un {@link ResultadoIADTO} de rechazo cuando el animal
     * no pasa una validación crítica antes de consultar todas las IAs.
     *
     * @param mensaje  mensaje descriptivo del motivo de rechazo
     * @param totalIAs número total de IAs configuradas
     * @return resultado con aprobado = false y votos = 0
     */
    private ResultadoIADTO resultadoRechazado(String mensaje, int totalIAs) {
        ResultadoIADTO resultado = new ResultadoIADTO();
        resultado.setAprobado(false);
        resultado.setVotos(0);
        resultado.setTotalIAs(totalIAs);
        resultado.setDetalle(mensaje);
        return resultado;
    }

    /**
     * Construye el {@link ResultadoIADTO} final con todos los datos
     * detectados por las IAs y el resultado de la validación.
     *
     * @param especie        especie detectada
     * @param raza           raza detectada
     * @param color          color detectado
     * @param edad           edad detectada
     * @param clasificacion  clasificación detectada
     * @param aprobado       true si obtuvo 4 o más votos
     * @param votos          número de votos positivos obtenidos
     * @param totalIAs       número total de IAs consultadas
     * @param detalle        detalle del resultado de cada IA
     * @return {@link ResultadoIADTO} con todos los datos
     */
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