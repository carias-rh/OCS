package eu.europa.ec.eci.oct.webcommons.services.crypto;

import eu.europa.ec.eci.oct.crypto.CipherOperation;
import eu.europa.ec.eci.oct.crypto.CryptoException;
import eu.europa.ec.eci.oct.crypto.Cryptography;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;


public class CryptographyService {
	
    private Cryptography crypto;

    private CryptographyService(char[] publicKey) throws OCTCryptoException {

        try {
            crypto = new Cryptography(CipherOperation.ENCRYPT, Hex.decodeHex(publicKey));
        } catch (CryptoException e) {
            throw new OCTCryptoException("crypto exception while crypto service initialization. message: " + e.getMessage(), e);
        } catch (DecoderException e) {
            throw new OCTCryptoException("decoder exception while crypto service initialization. message: " + e.getMessage(), e);
        }

    }

    public static CryptographyService getService(char[] publiKey) throws OCTCryptoException {
        return new CryptographyService(publiKey);
    }

    public byte[] encrypt(String value) throws OCTCryptoException {

        try {
            // TODO use UTF-8 explicitely
            return crypto.perform(value.getBytes("UTF8"));
        } catch (CryptoException e) {
            throw new OCTCryptoException("crypto exception while performing crypto operation. message: " + e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            throw new OCTCryptoException("crypto exception while performing crypto operation. message: " + e.getMessage(), e);
        }
    }

    public byte[] fingerprint(String value) throws OCTCryptoException {
        try {
            return Cryptography.fingerprint(value.getBytes("UTF8"));
        } catch (CryptoException e) {
            throw new OCTCryptoException("crypto exception while counting hash. message: " + e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            throw new OCTCryptoException("crypto exception while counting hash. message: " + e.getMessage(), e);
        }
    }

}
