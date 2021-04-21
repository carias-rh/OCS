package eu.europa.ec.eci.oct.offline.support.crypto;

import eu.europa.ec.eci.oct.offline.support.log.OfflineCryptoToolLogger;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

/**
 * @author: micleva
 * @created: 11/22/11
 * @project OCT
 */
public class CryptographyHelper {
    private static OfflineCryptoToolLogger log = OfflineCryptoToolLogger.getLogger(CryptographyHelper.class.getName());

    private static byte[] key;

    public static boolean initiateCryptoModule(char[] pwd) {
        boolean success = false;
        try {
            key = KeyProvider.loadKeyFromFile(pwd);

            success = true;
        } catch (Exception e) {
            //do nothing
            log.debug("Unable to load the key from file with the given password", e);
        }

        return success;
    }

    public static boolean isCryptoModuleInitialized() {
        return key != null && key.length > 0;
    }

    public static byte[] getKey() {
        return key;
    }

    public static byte[] fastEncrypt(byte[] dataToEncrypt) throws Exception {
        Cipher cipher = initCipher(Cipher.ENCRYPT_MODE);
        return cipher.doFinal(dataToEncrypt);
    }

    public static byte[] fastDecrypt(byte[] dataToDecrypt) throws Exception {
        Cipher cipher = initCipher(Cipher.DECRYPT_MODE);
        return cipher.doFinal(dataToDecrypt);
    }

    private static Cipher initCipher(int cypherOption) throws Exception {
        // use only first 128 bit
        byte[] shortKet = Arrays.copyOf(key, 16);

        SecretKeySpec secretKeySpec = new SecretKeySpec(shortKet, "AES");

        // Instantiate the cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(cypherOption, secretKeySpec);
        return cipher;
    }
}
