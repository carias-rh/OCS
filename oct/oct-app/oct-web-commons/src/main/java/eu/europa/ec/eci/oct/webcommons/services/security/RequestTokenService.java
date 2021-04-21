package eu.europa.ec.eci.oct.webcommons.services.security;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheStats;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

@Service
public class RequestTokenService {
	protected Logger logger = LogManager.getLogger(RequestTokenService.class);

	public static final long DEFAULT_REQUEST_TOKEN_EXPIRE_TIME = 180; // 3 minutes as seconds
	public static final long DEFAULT_REQUEST_TOKEN_CACHE_SIZE = 10000;
	public static final int DEFAULT_REQUEST_TOKEN_CONCURRENCY_LEVEL = 1;

	public static final String X_OCS_TOKEN = "x-ocs-token";

	private Cache<String, String> tokenCache = null;

	private Long tokenExpireTime;
	private Long cacheMaxSize;
	private Integer concurrencyLevel;

	public RequestTokenService() {
		this.tokenExpireTime = DEFAULT_REQUEST_TOKEN_EXPIRE_TIME;
		this.cacheMaxSize = DEFAULT_REQUEST_TOKEN_CACHE_SIZE;
		this.concurrencyLevel = DEFAULT_REQUEST_TOKEN_CONCURRENCY_LEVEL;
	}

	public RequestTokenService(Long tokenExpireTime, Long cacheMaxSize, Integer concurrencyLevel) {
		this.tokenExpireTime = tokenExpireTime;
		this.cacheMaxSize = cacheMaxSize;
		this.concurrencyLevel = concurrencyLevel;
	}

	/**
	 * Initialize the TokenCache container.
	 * 
	 */
	public void initialize() {
		if (tokenCache == null) {
			tokenCache = CacheBuilder.newBuilder().maximumSize(cacheMaxSize)
					.expireAfterWrite(tokenExpireTime, TimeUnit.SECONDS)
					.expireAfterAccess(tokenExpireTime, TimeUnit.SECONDS).concurrencyLevel(concurrencyLevel)
					.removalListener(new RemovalListener<String, String>() {
						// Keep in mind that the removal is done in read/write operation and it's not a
						// separate task that periodically check the validity of the token
						// Mostly of debug operations.
						@Override
						public void onRemoval(RemovalNotification<String, String> notification) {
							logger.debug("Token Cache REMOVED cause:[" + notification.getCause().name()
									+ "] - token : [" + notification.getKey() + "]");
						}
					}).build();
			logger.debug("Token Cache initialised REQUEST_TOKEN_CACHE_SIZE:" + cacheMaxSize
					+ " / REQUEST_TOKEN_EXPIRE_TIME:" + tokenExpireTime);
		} else {
			logger.debug("Token Cache REQUEST_TOKEN_CACHE_SIZE:" + cacheMaxSize + " / REQUEST_TOKEN_EXPIRE_TIME:"
					+ tokenExpireTime);
		}
	}

	/**
	 * Exposed method to generate a new Token
	 * 
	 */
	public String generateAndStore() {
		String token = generateNew();
		tokenCache.put(token, token);
		// logger.debug("Token Cache NEW token : " + token);
		return token;
	}

	/**
	 * Check if the requested token is still available. If so it will be consumed.
	 * 
	 * Return boolean
	 * 
	 * True : if the token has been found valid and then consumed. False: if the
	 * token was no found, either expired or already consumed.
	 * 
	 */

	public boolean consume(String requestToken) {
		boolean _consumed = false;

		String _token = tokenCache.getIfPresent(requestToken);
		if (_token != null) {
			tokenCache.invalidate(requestToken);
			_consumed = true;
			logger.debug("Token Cache CONSUMED OK requested token:" + requestToken + " found and consumed");
		} else {
			logger.debug("Token Cache CONSUMED NOT OK requested token:" + requestToken
					+ " not found! either expired or already consumed");
		}

		/*
		 * DEBUGGING PURPOSE ONLY if(logger.isEnabledFor(Level.DEBUG)){ //dump token
		 * List<Map.Entry<String, String>> entries =
		 * ImmutableList.copyOf(tokenCache.asMap().entrySet()); Iterator<Entry<String,
		 * String>> i = entries.iterator();
		 * logger.debug("----------------- BEGIN DUMP REQUEST TOKEN CACHE - size(" +
		 * getSize() + ") ----------------------"); while(i.hasNext()){
		 * logger.debug(i.next().getKey()); };
		 * logger.debug("----------------- END DUMP REQUEST TOKEN CACHE - size(" +
		 * getSize() + ") ----------------------"); }
		 */
		return _consumed;
	}

	/**
	 * Verify if a token uis present in the cache
	 * 
	 * @param token
	 *            The token that must be verified
	 * @return true if the token is present in the cache
	 */
	public boolean isPresent(String token) {
		boolean isPresent = false;
		String _token = tokenCache.getIfPresent(token);
		if (_token != null) {
			isPresent = true;
		}
		return isPresent;
	}

	/**
	 * Internal method with the logic to generate a token
	 * 
	 */
	private String generateNew() {
		return UUID.randomUUID().toString();
	}

	/**
	 * Returns the statistics of the cache usage, for debugging and testing purpose
	 * ONLY
	 * 
	 */
	public CacheStats getStats() {
		return tokenCache.stats();
	}

	public long getSize() {
		return tokenCache.size();
	}

	public void invalidateAll() {
		tokenCache.invalidateAll();
	}
}
