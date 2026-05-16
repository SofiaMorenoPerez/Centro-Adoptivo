package co.edu.unbosque.centroadoptivo.util;

import static org.apache.commons.codec.binary.Base64.decodeBase64;
import static org.apache.commons.codec.binary.Base64.encodeBase64;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Clase de utilidad para operaciones de cifrado AES y funciones de hash. Proporciona métodos para
 * cifrar y descifrar texto usando AES en modo GCM, así como métodos para generar hashes usando
 * varios algoritmos (MD5, SHA1, SHA256, etc.).
 */
@Component
public class AESUtil {

    private static final String ALGORITMO = "AES";
    private static final String TIPOCIFRADO = "AES/GCM/NoPadding";

    private static String KEY;
    private static String IV;

    @Value("${aes.secret.key}")
    public void setKey(String key) {
        KEY = key;
    }

    @Value("${aes.secret.iv}")
    public void setIv(String iv) {
        IV = iv;
    }

    public static String encrypt(String llave, String iv, String texto) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(TIPOCIFRADO);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        SecretKeySpec secretKeySpec = new SecretKeySpec(llave.getBytes(), ALGORITMO);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, iv.getBytes());
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmParameterSpec);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        byte[] encrypted = null;
        try {
            encrypted = cipher.doFinal(texto.getBytes());
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return new String(encodeBase64(encrypted));
    }

    public static String decrypt(String llave, String iv, String encrypted) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(TIPOCIFRADO);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        SecretKeySpec secretKeySpec = new SecretKeySpec(llave.getBytes(), ALGORITMO);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, iv.getBytes());
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParameterSpec);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        byte[] enc = decodeBase64(encrypted);
        byte[] decrypted = null;
        try {
            decrypted = cipher.doFinal(enc);
            return new String(decrypted);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String decrypt(String encrypted) {
        return decrypt(KEY, IV, encrypted);
    }

    public static String encrypt(String plainText) {
        return encrypt(KEY, IV, plainText);
    }

    public static String hashingToMD5(String content) {
        return DigestUtils.md5Hex(content);
    }

    public static String hashingToSHA1(String content) {
        return DigestUtils.sha1Hex(content);
    }

    public static String hashingToSHA256(String content) {
        return DigestUtils.sha256Hex(content);
    }

    public static String hashingToSHA384(String content) {
        return DigestUtils.sha384Hex(content);
    }

    public static String hashingToSHA512(String content) {
        return DigestUtils.sha512Hex(content);
    }


}
