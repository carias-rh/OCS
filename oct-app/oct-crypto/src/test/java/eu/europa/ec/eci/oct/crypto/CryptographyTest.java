package eu.europa.ec.eci.oct.crypto;

import junit.framework.Assert;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.apache.commons.codec.binary.Hex.encodeHex;
import static org.junit.Assert.*;

public class CryptographyTest {
    static final String PUBK = "30820222300d06092a864886f70d01010105000382020f003082020a0282020100c6edf1d6df309ad018593340da5aa6cb3f8d7c28ae2e80caed959debc18f261e8725db72c311e0628fa6fba796d6d181dc98e455c1915ec664620405cce4334f97dbc5b697d89292c7a733657e5db82a21f05af42662d1e4b40aa75afdc494576baf604668967925d46940b3f7dfd3303436ac763d22de8a87ef15ffd1e3ee56e61aeaadd87d94d1896085a40345ecaa6bc4c8790ff82b030095b3f02da46456975ace2f8f3288439dff772c61f465b46ec64c5ec3a1b9d7a28871b7e8327c98e721712bf98a2ef068f9aac02990c0804135d4bedf605c1771d023c31b5866aa9eb476aa07ed928366ff67f142cf611113874e03c7d64b002e35300b8fab8b961bef1da023481faab27e2edb5d7c02e38cdaa83e12865218f3c94328c63c728512065aa38f2cbde68dbe000ffb055c6a2b8977d460ee4ab1b46d53fc552ad67a05b988e2ec233e9881fd82f9c11e03e0a5faa89ff26bedccad2d24b48a56716b28ba98aa90611352a3c6afe54c5d5bbd7a4dd894dd35d6350a3264112d5db5b8a120860daa5541bb16ff87074c02ef2ab66f0d5e06871eceda6224fe5f23762e529f7d357b58fd8c56fbcf93e21ae4a16f81203172fa2b60ef2ed7271dfe4796836c90e896d0db748b5fd4c905f627215b2361b436ad6349f2bc4b1b8b405c61937f9a4e9b27aa2730ef8366146a1ec4a0cc01115876cf5a6a212c11abbe4b8f0203010001";
    static final String PRIVK = "30820943020100300d06092a864886f70d01010105000482092d308209290201000282020100c6edf1d6df309ad018593340da5aa6cb3f8d7c28ae2e80caed959debc18f261e8725db72c311e0628fa6fba796d6d181dc98e455c1915ec664620405cce4334f97dbc5b697d89292c7a733657e5db82a21f05af42662d1e4b40aa75afdc494576baf604668967925d46940b3f7dfd3303436ac763d22de8a87ef15ffd1e3ee56e61aeaadd87d94d1896085a40345ecaa6bc4c8790ff82b030095b3f02da46456975ace2f8f3288439dff772c61f465b46ec64c5ec3a1b9d7a28871b7e8327c98e721712bf98a2ef068f9aac02990c0804135d4bedf605c1771d023c31b5866aa9eb476aa07ed928366ff67f142cf611113874e03c7d64b002e35300b8fab8b961bef1da023481faab27e2edb5d7c02e38cdaa83e12865218f3c94328c63c728512065aa38f2cbde68dbe000ffb055c6a2b8977d460ee4ab1b46d53fc552ad67a05b988e2ec233e9881fd82f9c11e03e0a5faa89ff26bedccad2d24b48a56716b28ba98aa90611352a3c6afe54c5d5bbd7a4dd894dd35d6350a3264112d5db5b8a120860daa5541bb16ff87074c02ef2ab66f0d5e06871eceda6224fe5f23762e529f7d357b58fd8c56fbcf93e21ae4a16f81203172fa2b60ef2ed7271dfe4796836c90e896d0db748b5fd4c905f627215b2361b436ad6349f2bc4b1b8b405c61937f9a4e9b27aa2730ef8366146a1ec4a0cc01115876cf5a6a212c11abbe4b8f02030100010282020100b49f8480e772887efe3cafeee8d47ab1fd68881d9fda15932c1a37a3c2c229887a8a0331b6b15a9de37568f6885d5129aa95206e452dab7253df336bd8d83346c7ca034181aa619be8b08658575edd4354419b34284e7dea3d179751652dc2cb85b95d9f5083a0489b0f5e728a54aebb0c0dc09633789fbc05252f4272df949f4e9b8e0e7f8db73c72923385f27ae122f46b5ee39d7e65ff79de73dd5673f7af300a814069b3264acf0110c239c6e139ed831c00c77ce6f3465ec1728ce09a857307c86c774de2c4e36c1e8010bda02c54097ebc38b293abade9a12f1c7b2ded29e10e05e0e0f3b16029f3c4fa36b619fd3353c0d7085a5d359791a24db7232b74ece8fefcf7378bd1edd00915a28d9c8cdabfa1ad91a9b43a454588b8767474b6456c370781414582929eae0ee984fff806eb58a0eddbc19dbdc250cd1d95470c83be08fcc2dde9c36bd160db629f83b778872eb7c36dea56fd62c081ad0ba43ba92ec8ef9082ac91cd6c21585907a019f1ed5ae354745bcd0c078436c43b1c0171014837349217a56c71f260f1ea7dd04bd129c95442914a0a2b3ed7c7d44dbbc0879a1336439a2f48c2711527c50c27907d0c234e235f4afb1227d6190ddee3d33e76cc17d414c994d7ea09e812ee1de57b73773541356c31ed04aa0955b512d17e3d57bc6e0ad940f179a0dcb40c2a0504dd509793afdc586f4fd2d875e10282010100e9cfd4c09028cc3beb821ec5efa8843b69f7c07d28bad87517f15994c4307c6eae741c6903afa94c5037bf5f73a84c00fd15d533eb14be1840e9ca6fa123ca37a71974c98d8dd1bd4921058a2526dab03b170e12182a58a70e4447a3eee0f9ed561e0a602427a73e8ca503f9548ccfbfda559ded6e456c905bae917938f633c391e4a0c872aa816b7a8a1d0a26727a84a7ce90cf994091cd278969e882e4ddd24e0a808121e59ea4ce670fad3af8cca871a15c9fcd0dc8802c62d3445886042ee4be60bf6865d57c6f12b523c0ac99e7b9e882e5f3e4d107147e58fcaf4d95efa15d014ed75eba8c2cfadb2e878d02262cf7637a256aede46baf38738afecef70282010100d9ceb0900be5f5ad5b925a22cf56bcca980f9e7cb852db3aa5a3a6c8b331694ad63d0f461593657847c3490a92bd750124daae7a086dacfcdfe73e02dbb32132f813cd6308f4a0a463fcb9f46fec45d28648a191dba8749b61d69fb11729ba26b0f45c088131ef7e7632f75c1f12e9341755d1da08143bb22658935c46620b663a53f322932fabe8d851477955e6b9399fbab27afce7a5af72de4898097db06f90fbd81defb4331da897d6f5fa879f3d3a90ae2aebdfc738e8bf95fb15eea3c12a301c2386a6b37ca6949e2cbe809f95ea24b9daf53250998ec904b86100d8172b66efa183c788d0872091d5c7918dfbdbed668158ddd4789569a66b80128a29028201007177f01b079b150a9b4f8999aba60f8e39ee07577635bf288c570f8f57eae3e85ef770763258c48389869474414fd29ac4381d40d5fc3a08d12fe4936a135166d2cc52d621c14aa15ee50ca82f417cedfbaf439fda646aa3a013d8e4a0e043ca3f79d2b537b69abe8b48e5f5d16788c6e5b1993409c8dd57ed767998e7e0ebdc24685cf30a5adef6070111f8c411d88d9a5670069fac2b55f1b30957d27639847f652f59fbd650b85086c99fdb37b532a5fa9655e310a65635b43c2d35fccbf8e9ffce39f5f33db8e6baa077da60a94c40f7aad6e8170aae31eee346b5d8a44427fe57cd284e0ba04d8fec60d75dc2463e9a4c8791b324022f9458841257789f0282010030cece66192478ff560bdfc2f92f78b2f58c5a799c5c84e0098a38afb4e817ae3b8007522f378e7f7ba55952d33b3d62fa3d3ee7fdc786024cb2670500f28d37fcbb8344f416cd80c91f3314819832b5507e2a26a3508680e355d3073e278d9d69fdf839b7a9d6ff41c17e08d8b5c628742a391260c917ef7e044f51583c544d9e387dbd7bba2d0dca9bbc268687636e744ac4002778497a9d850e4373ad04c29b7cb41b5141d24a8d23297a899fa6a173e444033e443142a1757d487ccbb34a7007c3e67a20150c93cfadfa5cde39e4c04966949869a402252b0ab1be9b0fd1715a69cc89d946e163c67aa7ab4704c831aa6b5404f400cedcbc861c84e0a2b10282010100835dbc824120fbe524057aab09a7ca1ada0f48cfd04ae0ec0a8d249d220339ece3835193534f5ac445e4a2c8df7fee80decb2b3f856e661cf7839bce96ade97b06fa5fc72eb95c3ccb8e07f154af69a4c293804c072a9074d135cad4603ff948d01960413b780c506e2f3bd1f6119e4fb339f1148faff8957cfb75ed204d39f7c076f9ab941b8cb2a8197ffbee6a689f4061ea748bcbcde4d81391814443f20b6d7a1df96d19415b1cb554c94e35a31cf03fe4f89d8828a4863727020f54bd5f5235228cea6f4f2026c3a6749cdcdc1ca792f6695f07209d8788a41219d522464b651fb54b03e21856a2dfba41485b1925f0cf23c7769268b8ea65edf8836043";
    static byte[] privk = null;
    static byte[] pubk = null;
	
	@BeforeClass
	public static void setUp() {
		try {
			KeyPair kp = Cryptography.generateKeyPair();			
			privk = kp.getPrivate().getEncoded();
			pubk = kp.getPublic().getEncoded();
            System.out.println("Public key representation: " + new String(Hex.encodeHex(pubk)));
            System.out.println("Private key representation: " + new String(Hex.encodeHex(privk)));

			Assert.assertNotNull("Generated key pair is null", kp);
			
		} catch (CryptoException e) {
			e.printStackTrace();
			Assert.fail("crypto exception: " + e.getMessage());
		} 	
	}

	
	@Test
	public void testFingerprint(){
		
		String text = "Very secret text to hash it";
		
		try {
			byte[] textHash = Cryptography.fingerprint(text.getBytes());
			byte[] hashToCompare = MessageDigest.getInstance(CryptoConstants.DIGESTER_ALG).digest(text.getBytes());
			
			Assert.assertTrue("Hashed text does not match expected", java.util.Arrays.equals(textHash, hashToCompare));
		} catch (CryptoException e) {
			e.printStackTrace();
			Assert.fail("crypto exception: " + e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			Assert.fail("no such algorithm: " + e.getMessage());
		}		
	}

    @Test
    public void generateSalt() {
        byte[] salt;
        try {
            salt = Cryptography.generateSalt();
            //we should have a 512 bits salt
            System.out.println(Hex.encodeHex(salt));
            assertEquals(512, salt.length * 8);
        } catch (CryptoException e) {
            e.printStackTrace();
            fail("crypto exception: " + e.getMessage());
        }

    }

    @Test
    public void fingerprintWithSalt() {
        String encodedSalt = "0f6dfeff1aae3ea5066a91e04b56c2603cde845f89fbb448eeb3b01d8252a8e8bfe77a7817a9694d67cf40f8e66b9b02444407f40406308d2e9266b81b8ab874";
        String encodedHash = "e288b908453250944421988ba8cde93cf4edbd57d624ecfc2e01aa5bcde2bccf59406fd88d11fe41870b62d0f53aa61a39dc10402a3d725c1da96f1f71b4d0ec";
        String pwd = "thisIsMyPassword !";
        try {
            byte[] hashPwd = Cryptography.fingerprintWithSalt(pwd, Cryptography.generateSalt());
            assertEquals(512, hashPwd.length * 8);
            byte[] hashPwd2 = Cryptography.fingerprintWithSalt(pwd, Cryptography.generateSalt());
            //Each new hash is different due to the use of a new generated salt
            assertNotEquals(hashPwd, hashPwd2);
            byte[] hashPwd3 = Cryptography.fingerprintWithSalt(pwd, Hex.decodeHex(encodedSalt.toCharArray()));
            //Same pwd with same salt produce the same hash if the number of iterations is the same
            assertEquals(encodedHash, new String(Hex.encodeHex(hashPwd3)));
        } catch (CryptoException e) {
            e.printStackTrace();
            fail("crypto exception: " + e.getMessage());
        } catch (DecoderException e) {
            e.printStackTrace();
            fail("hex decoder exception: " + e.getMessage());
        }
    }

    @Test
    public void fingerprintAndSaltOutputs() throws Exception {
        String password = "UnitTest2017!";

        byte[] salt = Cryptography.generateSalt();
        byte[] hashPwd = Cryptography.fingerprintWithSalt(password, salt);
        System.out.println("Password: " + password);
        System.out.println("Salt: " + new String(Hex.encodeHex(salt)));
        System.out.println("Hash: " + new String(Hex.encodeHex(hashPwd)));
    }

	
	@Test
	public void testKeyPairGeneration(){		
		
		try {
            KeyPair kp = Cryptography.generateKeyPair();
            Assert.assertNotNull("Generated key pair is null", kp);
            System.out.println("Public key representation: " + new String(Hex.encodeHex(kp.getPublic().getEncoded())));
            System.out.println("Private key representation: " + new String(Hex.encodeHex(kp.getPrivate().getEncoded())));
        } catch (CryptoException e) {
            e.printStackTrace();
			Assert.fail("crypto exception: " + e.getMessage());
		} 		
	}
	
	
	@Test
    public void testEndToEndWithGeneratedKeys() throws Exception {

		String plaintext = "Very secret text to be encrypted";
		byte[] plainbytes = null;
		try {
			plainbytes = plaintext.getBytes("UTF8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
            fail("unsupported encoding exception. message: " + e1.getMessage());
        }

		byte[] encrypted = null;
		byte[] decrypted = null;
		
		try {
            //The keys are generated
            encrypted = new Cryptography(CipherOperation.ENCRYPT, pubk).perform(plainbytes);
            decrypted = new Cryptography(CipherOperation.DECRYPT, privk).perform(encrypted);
            assertTrue("Decrypted text does not match expected", java.util.Arrays.equals(plainbytes, decrypted));
            //Use the fixed keys
            encrypted = new Cryptography(CipherOperation.ENCRYPT, Hex.decodeHex(PUBK.toCharArray())).perform(plainbytes);
            decrypted = new Cryptography(CipherOperation.DECRYPT, Hex.decodeHex(PRIVK.toCharArray())).perform(encrypted);
            assertTrue("Decrypted text does not match expected", java.util.Arrays.equals(plainbytes, decrypted));


		} catch (CryptoException e) {
			
			e.printStackTrace();
            fail("crypto exception: " + e.getMessage());
        }

    }

	@Test
	public void testCompleteEndToEnd() {

		String plaintext = "łóżźćąęâăîșțâ.,";
		byte[] plainbytes = null;
		try {
			plainbytes = plaintext.getBytes("ISO-8859-1");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			Assert.fail("unsupported encoding exception. message: " + e1.getMessage());
		}

		byte[] encrypted = null;
		byte[] decrypted = null;
		
		try {

            encrypted = new Cryptography(CipherOperation.ENCRYPT, pubk).perform(plainbytes);
            String encryptedValue = new String(encodeHex(encrypted));

            decrypted = new Cryptography(CipherOperation.DECRYPT, privk).perform(Hex.decodeHex(encryptedValue.toCharArray()));
        } catch (CryptoException e) {
			
			e.printStackTrace();
			Assert.fail("crypto exception: " + e.getMessage());
		}  catch (DecoderException e) {
			e.printStackTrace();
			Assert.fail("decoder exception: " + e.getMessage());
		}
		
			Assert.assertTrue("Decrypted text does not match expected", 
					java.util.Arrays.equals(plainbytes, decrypted));
		
	}
}
