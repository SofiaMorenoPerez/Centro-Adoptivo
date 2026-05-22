package co.edu.unbosque.centroadoptivo.util;

import java.io.IOException;
import java.util.Base64;

import org.springframework.web.multipart.MultipartFile;

public class ImageUtil {

    public static byte[] obtenerBytes(MultipartFile archivo) {

        try {

            return archivo.getBytes();

        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }
    }

    public static String convertirABase64(byte[] imagenBytes) {

        return Base64.getEncoder()
                .encodeToString(imagenBytes);
    }

    public static String multipartFileABase64(MultipartFile archivo) {

        try {

            byte[] imagenBytes = archivo.getBytes();

            return Base64.getEncoder()
                    .encodeToString(imagenBytes);

        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }
    }
}