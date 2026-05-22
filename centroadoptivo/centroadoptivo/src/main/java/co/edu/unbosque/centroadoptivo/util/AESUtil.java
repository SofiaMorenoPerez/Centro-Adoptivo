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

@Component
public class AESUtil {

    private static final String ALGORITMO = "AES";
    private static final String TIPOCIFRADO = "AES/GCM/NoPadding";

    private static String KEY;
    private static String IV;

    @Value("${aes.secret.key}")
    public void setKey(String key) { KEY = key; }

    @Value("${aes.secret.iv}")
    public void setIv(String iv) { IV = iv; }

    public static String encrypt(String llave, String iv, String texto) {
        try {
            Cipher cipher = Cipher.getInstance(TIPOCIFRADO);
            SecretKeySpec secretKeySpec = new SecretKeySpec(llave.getBytes(), ALGORITMO);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, iv.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmParameterSpec);
            byte[] encrypted = cipher.doFinal(texto.getBytes());
            return new String(encodeBase64(encrypted));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException |
                 InvalidKeyException | InvalidAlgorithmParameterException |
                 IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException("Error al encriptar", e);
        }
    }

    public static String decrypt(String llave, String iv, String encrypted) {
        try {
            Cipher cipher = Cipher.getInstance(TIPOCIFRADO);
            SecretKeySpec secretKeySpec = new SecretKeySpec(llave.getBytes(), ALGORITMO);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, iv.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParameterSpec);
            byte[] enc = decodeBase64(encrypted);
            byte[] decrypted = cipher.doFinal(enc);
            return new String(decrypted);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException |
                 InvalidKeyException | InvalidAlgorithmParameterException |
                 IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException("Error al desencriptar", e);
        }
    }

    public static String decrypt(String encrypted) {
        return decrypt(KEY, IV, encrypted);
    }

    public static String encrypt(String plainText) {
        return encrypt(KEY, IV, plainText);
    }

    public static String hashingToMD5(String content) { return DigestUtils.md5Hex(content); }
    public static String hashingToSHA1(String content) { return DigestUtils.sha1Hex(content); }
    public static String hashingToSHA256(String content) { return DigestUtils.sha256Hex(content); }
    public static String hashingToSHA384(String content) { return DigestUtils.sha384Hex(content); }
    public static String hashingToSHA512(String content) { return DigestUtils.sha512Hex(content); }
}