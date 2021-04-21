package eu.europa.ec.eci.oct.crypto;

/**
 * @author marcin.dzierzak@ext.ec.europa.eu
 *
 */
public interface CryptoConstants {

    // cipher

    public static final String CIPHER_ALG_RSA_ECB_PKCS1 = "RSA/ECB/PKCS1Padding";

    /**
     * module-scoped setting for internal logic
     */
    public static final String CIPHER_ALG = CIPHER_ALG_RSA_ECB_PKCS1;


    // digester

    public static final String DIGESTER_ALG_SHA1 = "SHA1";

    public static final String DIGESTER_ALG_SHA256 = "SHA-256";
    //
    public static final String DIGESTER_ALG_SHA512 = "SHA-512";

    public static final String DIGESTER_ALG_MD5 = "MD5";

    /**
     * module-scoped setting for internal logic
     */
    public static final String DIGESTER_ALG = DIGESTER_ALG_SHA256;

    // key

    public static final String KEY_ALG_RSA = "RSA";

    /**
     * module-scoped setting for internal logic
     */
    public static final String KEY_ALG = KEY_ALG_RSA;

}
