package eu.europa.ec.eci.oct.offline.support.crypto;

import eu.europa.ec.eci.oct.crypto.CryptoException;
import eu.europa.ec.eci.oct.crypto.Cryptography;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PbeCipherTest {
    public static KeyPair keyPair;
    public static byte[] salt;
    public String password = "ItsAWonderfullWorld!";
    public static final int DIGEST_ITERATIONS = 2500;

    @BeforeClass
    public static void setUp() {
        try {
            SecureRandom saltGenerator = new SecureRandom();
            salt = saltGenerator.generateSeed(256);


            keyPair = Cryptography.generateKeyPair();
            Assert.assertNotNull("Generated key pair is null", keyPair);

        } catch (CryptoException e) {
            fail("crypto exception: " + e.getMessage());
        }
    }

    @Test
    public void encrypt() {
        SecureRandom saltGenerator = new SecureRandom();
        byte[] salt2 = saltGenerator.generateSeed(256);
        byte[] encBytes = new byte[0];
        try {
            String password = "ItsAWonderfullWorld!";
            encBytes = PbeCipher.encrypt(keyPair.getPrivate().getEncoded(), password.toCharArray(), salt2, DIGEST_ITERATIONS);
            assertTrue(encBytes.length > 0);
        } catch (CryptoException e) {
            fail("CryptoException: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            fail("NoSuchAlgorithmException: " + e.getMessage());
        } catch (NoSuchPaddingException e) {
            fail("NoSuchPaddingException: " + e.getMessage());
        } catch (InvalidKeyException e) {
            fail("InvalidKeyException: " + e.getMessage());
        } catch (IllegalBlockSizeException e) {
            fail("IllegalBlockSizeException: " + e.getMessage());
        } catch (BadPaddingException e) {
            fail("BadPaddingException: " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            fail("UnsupportedEncodingException: " + e.getMessage());
        }
    }
}
