package co.edu.unbosque.centroadoptivo.util;

import java.io.IOException;
import java.util.Base64;
import org.springframework.web.multipart.MultipartFile;

/**
 * Clase utilitaria para el manejo y conversión de imágenes.
 * Proporciona métodos estáticos para obtener bytes y convertir
 * imágenes a formato Base64.
 *
 * @author Centro Adoptivo Unbosque
 * @version 1.0
 */
public class ImageUtil {

    /**
     * Obtiene los bytes de un archivo subido por el usuario.
     * En caso de error retorna un arreglo vacío.
     *
     * @param archivo archivo multipart recibido desde el frontend
     * @return arreglo de bytes del archivo, o arreglo vacío si ocurre un error
     */
    public static byte[] obtenerBytes(MultipartFile archivo) {
        try {
            return archivo.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    /**
     * Convierte un arreglo de bytes a su representación en Base64.
     *
     * @param imagenBytes arreglo de bytes de la imagen
     * @return cadena de texto en formato Base64
     */
    public static String convertirABase64(byte[] imagenBytes) {
        return Base64.getEncoder()
                .encodeToString(imagenBytes);
    }

    /**
     * Convierte directamente un archivo multipart a su representación en Base64.
     * En caso de error retorna una cadena vacía.
     *
     * @param archivo archivo multipart recibido desde el frontend
     * @return cadena de texto en formato Base64, o cadena vacía si ocurre un error
     */
    public static String multipartFileABase64(MultipartFile archivo) {
        try {
            byte[] imagenBytes = archivo.getBytes();
            return Base64.getEncoder()
                    .encodeToString(imagenBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}