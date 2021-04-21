package eu.europa.ec.eci.oct.webcommons.services.security;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.Crypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import eu.europa.ec.eci.oct.crypto.CryptoException;
import eu.europa.ec.eci.oct.crypto.Cryptography;
import eu.europa.ec.eci.oct.entities.AuthenticationLock;
import eu.europa.ec.eci.oct.webcommons.services.BaseService;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.AuthenticationException;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.AuthenticationLockedException;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;
import eu.europa.ec.eci.oct.webcommons.services.persistence.PersistenceException;
import eu.europa.ec.eci.oct.webcommons.services.system.SystemManager;

@Service
@PropertySource(value = { "classpath:application.properties" })
public class SecurityServiceImpl extends BaseService implements SecurityService {
	@Autowired
	private SystemManager systemManager;
	@Autowired
	private Principal principal;
	@Autowired
	private Lock lock;

	private String hashedPhrase;
	private final String ROLE = "ADMIN";

	private Cache<String, Principal> authenticationCache = null;
	private Cache<String, Principal> challengeCache = null;

	private boolean override = false;

//	public static final int SESSION_EXPIRING_MINUTES = 1;
	public static final int SESSION_EXPIRING_MINUTES = 20;
	public static final int MAXIMUM_CACHE_ENTRY_SIZE = 10;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Value("${security.skip:false}")
	private boolean skipSecurity;

	public void overrideAuthenticationLock(Lock lock) {
		this.lock = lock;
		override = true;
	}

	public void resetLock() {
		lock.resetLock();
	}

	public SecurityServiceImpl() {
		authenticationCache = CacheBuilder.newBuilder().maximumSize(MAXIMUM_CACHE_ENTRY_SIZE)
				.expireAfterAccess(SESSION_EXPIRING_MINUTES, TimeUnit.MINUTES).concurrencyLevel(1).build();
		challengeCache = CacheBuilder.newBuilder().maximumSize(MAXIMUM_CACHE_ENTRY_SIZE)
				.expireAfterAccess(SESSION_EXPIRING_MINUTES, TimeUnit.MINUTES).concurrencyLevel(1).build();

	}

	@Override
	public String generateAuthToken() {
		String token = Crypt.crypt((new BigInteger(520, new SecureRandom()).toString(32)).getBytes(),
				"$6$ocs-token-authentication");
		return token;
	}

	@Override
	public Principal buildAndStorePrincipal(String authToken) {
		principal.setDate(new Date());
		principal.setToken(authToken);
		principal.setRole(ROLE);
		authenticationCache.cleanUp();
		authenticationCache.put(authToken, principal);
		return principal;
	}

	@Override
	public void extendAuthenticationCache(String authToken) throws AuthenticationException {
		if (isAuthenticated(authToken)) {
			logger.debug("Request to extend session. ");
		} else {
			logger.warn("Unauthorized attempt to extend the session");
			throw new AuthenticationException("Unauthorized request of session extension");
		}
	}

	@Override
	public void logout(String authToken) throws AuthenticationException {
		if (isAuthenticated(authToken)) {
			authenticationCache.invalidate(authToken);
		} else {
			throw new AuthenticationException("Unauthorized request of logout");
		}
	}

	@Override
	public boolean isAuthenticated(String authToken) {
		Principal principal = authenticationCache.getIfPresent(authToken);
		if (principal == null) {
			return false;
		}
		return true;
	}

	@Override
	public Principal validateAuthToken(String token) {
		return authenticationCache.getIfPresent(token);
	}

	@Override
	@Transactional(readOnly = true)
	public String generateChallenge() throws OCTException {
		String originalPhrase = generatePhrase();
		hashedPhrase = systemManager.hash(originalPhrase);
		return systemManager.generateChallenge(originalPhrase);
	}

	/*
	 * Validate only one challenge. The challenge must be associated with the
	 * request token to handle multiple challenge resolution.
	 */
	@Override
	public boolean validateChallenge(String challengeResponse) throws OCTException {
		if (skipSecurity) {
			logger.warn("SECURITY DISABLED");
			return true;
		}
		logger.info("SECURITY: ENABLED");
		if (challengeCache.getIfPresent(challengeResponse) != null) {
			logger.warn("CHALLENGE ALREADY USED");
			return false;
		}
		boolean validChallenge = systemManager.hash(challengeResponse).equals(hashedPhrase);
		if (validChallenge) {
			challengeCache.cleanUp();
			challengeCache.put(challengeResponse, principal);
			return true;
		}
		logger.warn("INVALID CHALLENGE");
		return false;
	}

	/*
	 * Due to the current challenge resolution implementation, this method handle
	 * only one administrator It will evolve using the auth cache
	 */
	@Override
	public String authenticate(String user, String pwd, String challengeResult)
			throws OCTException, AuthenticationException, AuthenticationLockedException {
		if (!isTentativeAllowed(user) && systemManager.isOnline())
			throw new AuthenticationLockedException("Authentication error: account locked");
		long start = Instant.now().toEpochMilli();

		boolean isUserPwdAuthenticated = systemManager.authenticate(user, pwd);
		boolean isChallengeValidated = validateChallenge(challengeResult);

		String freshToken = null;
		if (isUserPwdAuthenticated && isChallengeValidated) {
			authenticationCache.cleanUp();
			freshToken = obtainFreshToken(challengeResult);
		} else {
			increaseTentatives();
		}
		long end = Instant.now().toEpochMilli();

		// Get a valid ticket without any security checks
		// return obtainFreshToken();
		long diff = end - start;
		try {
			// Time based username enumeration (in collaboration with the authentication
			// lock).
			Thread.sleep(1000 - diff);
		} catch (InterruptedException e) {
			//
		}
		if (freshToken == null) {
			throw new AuthenticationException("Authentication error");
		} else {
			return freshToken;
		}
	}

	private String obtainFreshToken(String challenge) {
		String token = generateAuthToken();
		buildAndStorePrincipal(token);
		return token;
	}

	public byte[] decrypt(byte[] dataToDecrypt, char[] pass, byte[] salt, int iterations)
			throws CryptoException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		byte[] key = buildKey(pass, salt, iterations);
		key = Arrays.copyOf(key, 32); // use only first 128 bit
		SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

		// Instantiate the cipher
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
		return cipher.doFinal(dataToDecrypt);
	}

	public Lock obtainLock(String username) {
		AuthenticationLock authenticationLock;
		Lock lock = new Lock();
		try {
			authenticationLock = authenticationLockDAO.getAuthenticationLockForUser(username);
			lock.setLockPeriod(authenticationLock.getLockPeriod());
			lock.setMaxAttempts(authenticationLock.getMaxAttempts());
			if (authenticationLock.getTimeOfLastFailedAttempt() != null) {
				lock.setTimeOfLastFailedAttempt(authenticationLock.getTimeOfLastFailedAttempt().getTime());
			}
		} catch (PersistenceException e) {
			logger.error("Authentication lock parameters undetermined. " + e);
			return null;
		}
		return lock;
	}

	private boolean isTentativeAllowed(String username) {
		AuthenticationLock authenticationLock = null;
		if (!override) {

			try {
				authenticationLock = authenticationLockDAO.getAuthenticationLockForUser(username);
				if (authenticationLock == null)
					return true;
				lock.setLockPeriod(authenticationLock.getLockPeriod());
				lock.setMaxAttempts(authenticationLock.getMaxAttempts());
				if (authenticationLock.getTimeOfLastFailedAttempt() != null) {
					lock.setTimeOfLastFailedAttempt(authenticationLock.getTimeOfLastFailedAttempt().getTime());
				}
			} catch (PersistenceException e) {
				logger.error("Authentication lock parameters undetermined. " + e);
				return true;
			}
		}

		if (lock.getAttempts() < lock.getMaxAttempts()) {
			return true;
		} else {
			if ((new Date().getTime() - lock.getTimeOfLastFailedAttempt()) > lock.getLockPeriod() * 60 * 1000) { // in
				// ms
				lock.setTimeOfLastFailedAttempt(0);
				return true;
			} else {
				Date failedDate = Calendar.getInstance().getTime();
				lock.setTimeOfLastFailedAttempt(failedDate.getTime());
				return false;
			}
		}
	}

	private void increaseTentatives() {
		lock.setTimeOfLastFailedAttempt(new Date().getTime());
		if (lock.getAttempts() < lock.getMaxAttempts())
			lock.increaseAttempts();
	}

	private String generatePhrase() {
		// 256 bits (->260 multiple of 5)
		// 32=2^5 each character is represented on 5 bits
		return new BigInteger(260, new SecureRandom()).toString(32);
	}

	private static byte[] buildKey(char[] pass, byte[] salt, int iterations)
			throws CryptoException, UnsupportedEncodingException {
		byte[] hashedData = mixDataInDeterministicOrder(salt, pass);
		for (int i = 0; i < iterations; i++) {
			hashedData = Cryptography.fingerprint(hashedData);
		}
		return hashedData;
	}

	private static byte[] mixDataInDeterministicOrder(byte[] salt, char[] passChars)
			throws UnsupportedEncodingException {
		List<Byte> dataContent = new ArrayList<Byte>();
		for (byte data : salt) {
			dataContent.add(data);
		}
		int arrayLength = dataContent.size();
		byte[] pass = new String(passChars).getBytes("UTF-8");
		for (byte data : pass) {
			int position = data % arrayLength;
			dataContent.add(position, data);
		}
		byte[] result = new byte[dataContent.size()];
		for (int i = 0, dataContentSize = dataContent.size(); i < dataContentSize; i++) {
			Byte data = dataContent.get(i);
			result[i] = data;
		}
		return result;
	}

	@Override
	public Integer getSessionExpireTime() {
		return SESSION_EXPIRING_MINUTES;
	}

}
