package eu.europa.ec.eci.oct.crypto;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


/**
 * Wrapper class encapsulating leveraging java.security package
 *
 * @author marcin.dzierzak@ext.ec.europa.eu, franzmh
 */
public class Cryptography {

    private int opMode;

    private Cipher cipher;

    /**
     * Creates an <code>Cryptography</code> for given operation type and key value
     *
     * @param co       - operation type
     * @param keyValue - serialized key value
     * @throws CryptoException - if algorithms not found or key is invalid
     */
    public Cryptography(CipherOperation co, byte[] keyValue) throws CryptoException {
        opMode = getOperationMode(co);
        Key key;
        try {
            key = initKey(keyValue);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("no such key algorithm : " + CryptoConstants.KEY_ALG, e);
        } catch (InvalidKeySpecException e) {
            throw new CryptoException("invalid key specification", e);
        }

        try {
            cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-512AndMGF1Padding");
            javax.crypto.spec.OAEPParameterSpec oaepSpec = new javax.crypto.spec.OAEPParameterSpec("SHA-512", "MGF1",

                    java.security.spec.MGF1ParameterSpec.SHA512,
                    javax.crypto.spec.PSource.PSpecified.DEFAULT);
            cipher.init(opMode, key, oaepSpec);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("no such cipher algorithm : " + CryptoConstants.CIPHER_ALG, e);
        } catch (NoSuchPaddingException e) {
            throw new CryptoException("no such padding", e);
        } catch (InvalidKeyException e) {
            throw new CryptoException("invalid key", e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new CryptoException("invalid algorithm", e);
        }
    }

    /**
     * Generates <code>KeyPair</code> instance. Uses RSA algorithm and key size is 2048
     *
     * @return generated key pair
     * @throws CryptoException - if algorithm not found
     */
    public static KeyPair generateKeyPair() throws CryptoException {

        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(CryptoConstants.KEY_ALG);
            generator.initialize(4096);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("no such key algorithm : " + CryptoConstants.KEY_ALG, e);
        }
    }

    /**
     * Counts hash of the given byte array. Digester algorithm is SHA1
     *
     * @param bytes - input byte array
     * @return - hash of the input byte array
     * @throws CryptoException - if digester algorithm not found
     */
    public static byte[] fingerprint(byte[] bytes) throws CryptoException {

        try {
            return MessageDigest.getInstance(CryptoConstants.DIGESTER_ALG).digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("no such digest algorithm : " + CryptoConstants.DIGESTER_ALG, e);
        }
    }

    /**
     * Counts hash of the given byte array. Digester algorithm is SHA512
     * This method is only use to hash the organizer pwd.
     * The key's password is not affected by this method.
     *
     * @param bytes - input byte array
     * @return - hash of the input byte array
     * @throws CryptoException - if digester algorithm not found
     */
    public static byte[] fingerprint512(byte[] bytes) throws CryptoException {
        try {
            return MessageDigest.getInstance(CryptoConstants.DIGESTER_ALG_SHA512).digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("no such digest algorithm : " + CryptoConstants.DIGESTER_ALG_SHA512, e);
        }
    }

    public static byte[] generateSalt() throws CryptoException {
        SecureRandom random = null;
        String secureRandomAlgorithm = "SHA1PRNG";
        String secureRandomProvider = "SUN";

        byte[] salt = new byte[64]; //SHA-512

        try {
            random = SecureRandom.getInstance(secureRandomAlgorithm, secureRandomProvider);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("no such secure random algorithm : " + secureRandomAlgorithm, e);
        } catch (NoSuchProviderException e) {
            throw new CryptoException("no such secure random provider : " + secureRandomProvider, e);
        }
        byte[] seed = random.generateSeed(20);
        random.setSeed(seed);
        random.nextBytes(salt);
        return salt;
    }

    public static byte[] fingerprintWithSalt(String password, byte[] salt) throws CryptoException {
        int iterations = 100000;
        String secretKeyFactoryProvider = "PBKDF2WithHmacSHA512";

        KeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), salt, iterations, 64 * 8);
        SecretKeyFactory secretKeyFactory = null;
        byte[] hashPassword;
        try {
            secretKeyFactory = SecretKeyFactory.getInstance(secretKeyFactoryProvider);
            hashPassword = secretKeyFactory.generateSecret(pbeKeySpec).getEncoded();
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("no such secret key algorithm : " + secretKeyFactoryProvider, e);
        } catch (InvalidKeySpecException e) {
            throw new CryptoException("invalid key spec : " + pbeKeySpec, e);
        }
        return hashPassword;
    }


    /**
     * Performs cryptographic operation
     *
     * @param bytes - byte array to encode
     * @return - cryptographic operation result
     * @throws CryptoException - if block size or padding incorrect
     */
    public byte[] perform(byte[] bytes) throws CryptoException {

        try {
            return cipher.doFinal(bytes);
        } catch (IllegalBlockSizeException e) {
            throw new CryptoException("Illegal block size", e);
        } catch (BadPaddingException e) {
            throw new CryptoException("Bad padding", e);
        }
    }


    private int getOperationMode(CipherOperation co) {

        if (CipherOperation.DECRYPT.equals(co)) {
            return Cipher.DECRYPT_MODE;
        } else if (CipherOperation.ENCRYPT.equals(co)) {
            return Cipher.ENCRYPT_MODE;
        } else {
            throw new IllegalArgumentException("Illegal cipher operation: " + co.name());
        }
    }

    private Key initKey(byte[] keyValue) throws NoSuchAlgorithmException, InvalidKeySpecException {

        KeyFactory kf = KeyFactory.getInstance(CryptoConstants.KEY_ALG);
        Key localKey;

        switch (opMode) {
            case Cipher.DECRYPT_MODE:
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyValue);
                localKey = kf.generatePrivate(keySpec);
                break;
            case Cipher.ENCRYPT_MODE:
                X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(keyValue);
                localKey = kf.generatePublic(x509Spec);
                break;
            default:
                throw new IllegalArgumentException("Illegal operation mode: " + opMode);

        }
        return localKey;
    }
}
