package co.edu.unbosque.centroadoptivo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.centroadoptivo.dto.ValidacionIADTO;
import co.edu.unbosque.centroadoptivo.ia.DeepSeekVisionClient;
import co.edu.unbosque.centroadoptivo.ia.GeminiVisionClient;
import co.edu.unbosque.centroadoptivo.ia.LlavaVisionClient;
import co.edu.unbosque.centroadoptivo.ia.MistralVisionClient;
import co.edu.unbosque.centroadoptivo.ia.OpenRouterVisionClient;


@Service
public class IAOrquestadorService {

    @Autowired
    private OpenRouterVisionClient openRouterClient;

    @Autowired
    private DeepSeekVisionClient deepSeekClient;

    @Autowired
    private GeminiVisionClient geminiClient;

    @Autowired
    private LlavaVisionClient llavaClient;

    @Autowired
    private MistralVisionClient mistralClient;

    public ValidacionIADTO validarMascota(String imagenBase64, String especie,
            String raza, String descripcion) {

        int votos = 0;
        int totalIAs = 5;
        StringBuilder detalle = new StringBuilder();

        try {
            String resultado = openRouterClient.analizarClasificacion(imagenBase64);
            if (resultado.equals("DOMESTICO")) votos++;
            detalle.append("OpenRouter: ").append(resultado).append(" | ");
        } catch (Exception e) {
            detalle.append("OpenRouter: ERROR | ");
        }

        try {
            String resultado = deepSeekClient.validarInformacionMascota(especie, raza, descripcion);
            if (resultado.equals("VALIDO")) votos++;
            detalle.append("DeepSeek: ").append(resultado).append(" | ");
        } catch (Exception e) {
            detalle.append("DeepSeek: ERROR | ");
        }

        try {
            String resultado = geminiClient.verificarCoincidenciaFotoDescripcion(imagenBase64, especie, raza);
            if (resultado.equals("COINCIDE")) votos++;
            detalle.append("Gemini: ").append(resultado).append(" | ");
        } catch (Exception e) {
            detalle.append("Gemini: ERROR | ");
        }

        try {
            String resultado = llavaClient.verificarBienestarAnimal(imagenBase64);
            if (resultado.equals("SALUDABLE")) votos++;
            detalle.append("LLaVA: ").append(resultado).append(" | ");
        } catch (Exception e) {
            detalle.append("LLaVA: ERROR | ");
        }

        try {
            String resultado = mistralClient.moderarDescripcion(descripcion);
            if (resultado.equals("APROBADO")) votos++;
            detalle.append("Mistral: ").append(resultado);
        } catch (Exception e) {
            detalle.append("Mistral: ERROR");
        }

        boolean aprobado = votos >= 4;
        ValidacionIADTO resultado = new ValidacionIADTO();
        resultado.setAprobado(aprobado);
        resultado.setVotos(votos);
        resultado.setTotalIAs(totalIAs);
        resultado.setDetalle(detalle.toString());    
        
        return resultado;
        
    }
      
}