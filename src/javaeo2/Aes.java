package javaeo2;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Marvin
 */
public class Aes {

    private static final int ITERATION_COUNT = 1000;
    private static final int KEY_LENGTH = 128;
    private static final int IV_LENGTH = KEY_LENGTH / 8;
    private static final String FACTORY = "PBKDF2WithHmacSHA256";

    public static byte[] encrypt(char[] password, byte[] salt, String data) {
        return encrypt(password, salt, data.getBytes());
    }

    public static byte[] encrypt(char[] password, byte[] salt, byte[] data) {
        try {
            //Generate the secret
            SecretKey secret = createSecret(password, salt);
            if (secret == null) {
                return null;
            }

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret);

            /* destroy the secret after using it */
            secret = null;

            AlgorithmParameters params = cipher.getParameters();
            byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
            System.out.println("IV length: " + iv.length);
            byte[] encrypted = cipher.doFinal(data);

            byte[] output = new byte[iv.length + encrypted.length];

            /*
                                      ---------------------------------------
                Final message will be |16 bytes IV|x bytes encrypted message|
                                      ---------------------------------------
             */
            System.arraycopy(iv, 0, output, 0, iv.length);
            System.arraycopy(encrypted, 0, output, iv.length, encrypted.length);

            return output;
        } catch (InvalidKeyException | NoSuchPaddingException | InvalidParameterSpecException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException ex) {
            return null;
        }
    }

    public static byte[] decrypt(char[] password, byte[] salt, byte[] input) {

        try {
            //Generate the secret
            SecretKey secret = createSecret(password, salt);
            if (secret == null) {
                return null;
            }
            /*
                                                       ---------------------------------------
                copy the first 16 bytes from the data  |16 bytes IV|x bytes encrypted message|
                                                       ---------------------------------------
             */
            byte[] iv = Arrays.copyOfRange(input, 0, IV_LENGTH);
            byte[] data = Arrays.copyOfRange(input, IV_LENGTH, input.length);

            /* Decrypt the message, given derived key and initialization vector. */
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
            /* destroy secret after using it */
            secret = null;

            return cipher.doFinal(data);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
            return null;
        }
    }

    private static SecretKeySpec createSecret(char[] password, byte[] salt) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(FACTORY);
            KeySpec spec = new PBEKeySpec(password, salt, ITERATION_COUNT, KEY_LENGTH);

            /* clear the password array */
            clearPassword(password);

            /* generate the encryption key */
            SecretKey tmp = factory.generateSecret(spec);
            return new SecretKeySpec(tmp.getEncoded(), "AES");
        } catch (InvalidKeySpecException | NoSuchAlgorithmException ex) {
            return null;
        }
    }

    private static void clearPassword(char[] password) {
        for (int i = 0; i < password.length; i++) {
            password[i] = (char) 0x00;
        }
    }
}
