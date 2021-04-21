package eu.europa.ec.eci.oct.offline.support.crypto;

import java.security.KeyPair;
import java.security.PrivateKey;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.europa.ec.eci.oct.crypto.CryptoException;
import eu.europa.ec.eci.oct.crypto.Cryptography;

/**
 * @author: micleva
 * @created: 11/23/11
 * @project OCT
 */
public class KeyProviderTest {

    private static PrivateKey privateKey;

    @BeforeClass
	public static void setUp() {
		try {
			KeyPair kp = Cryptography.generateKeyPair();
			privateKey = kp.getPrivate();
			Assert.assertNotNull("Generated key pair is null", kp);

		} catch (CryptoException e) {
			Assert.fail("crypto exception: " + e.getMessage());
		}
	}

	/*@Test
	public void testSavedFileExists() throws Exception {
		KeyProvider.saveCryptoKeyWithPassword(privateKey, "thisIsMyPass");

	    assertTrue("The crypto file should exist at this stage", KeyProvider.keyFileExists());
	}

    @Test
	public void testSaveLoadPrivateFile() throws Exception {
		KeyProvider.saveCryptoKeyWithPassword(privateKey, "thisIsMyPass");

	    byte[] loadedKey = KeyProvider.loadKeyFromFile("thisIsMyPass");

        assertTrue("Loaded private key should be the same!", Arrays.equals(loadedKey, privateKey.getEncoded()));
	}*/

    @Test
    public void saveCryptoKeyWithPassword() throws Exception {
        KeyPair keyPair = Cryptography.generateKeyPair();
        String password = "ItsAWonderfullWorld!";
        KeyProvider.saveCryptoKeyWithPassword(keyPair.getPrivate(), password.toCharArray());
    }
}
