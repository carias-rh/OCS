package eu.europa.ec.eci.oct.webcommons.services.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import eu.europa.ec.eci.oct.crypto.CryptoException;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.AuthenticationException;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.AuthenticationLockedException;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;

public interface SecurityService {
    String generateChallenge()throws OCTException;
    boolean validateChallenge(String challengeResponse) throws OCTException;
    String authenticate(String user, String pwd, String challengeResult) throws OCTException, AuthenticationException,
            AuthenticationLockedException;
    void logout(String authToken) throws OCTException, AuthenticationException;
    boolean isAuthenticated(String authToken) throws OCTException;
    byte[] decrypt(byte[] dataToDecrypt, char[] pass, byte[] salt, int iterations) throws CryptoException,
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, UnsupportedEncodingException;
    String generateAuthToken();
    void overrideAuthenticationLock(Lock lock);
    /**
     * The token to validate
     * @param token a token obtained after a successful authentication
     * @return a principal or null if the token is not valid or expired
     */
    Principal validateAuthToken(String token);

    /**
     * Reset the authentication lock
     */
    void resetLock();

    Lock obtainLock(String username);
    
    
	/**
	 * Extend validity of the authenticated time
	 */
	void extendAuthenticationCache(String token) throws AuthenticationException;
	Integer getSessionExpireTime();
	Principal buildAndStorePrincipal(String authToken);

}
