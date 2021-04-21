package eu.europa.ec.eci.oct.webcommons.services.security;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import eu.europa.ec.eci.oct.webcommons.services.commons.ServicesTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/services-test.xml")
public class RequestTokenServiceImplTest extends ServicesTest {
	
	@Autowired
	private RequestTokenService  requestTokenService;
	
	@Test
	public void testTokenCacheNotSingleInstance4Application(){
		Cache<String, String> tokenCache1 = CacheBuilder.newBuilder()
				.maximumSize(100)
				.expireAfterWrite(100000, TimeUnit.SECONDS)
				.concurrencyLevel(1)
				.build();
		Cache<String, String> tokenCache2 = CacheBuilder.newBuilder()
				.maximumSize(100)
				.expireAfterWrite(100000, TimeUnit.SECONDS)
				.concurrencyLevel(1)
				.build();
		
		
		tokenCache1.put("aa1", "bb1");
		assertEquals(1, tokenCache1.size());

		assertEquals(0, tokenCache2.size());
		tokenCache2.put("aa2", "bb2");
		tokenCache2.put("aa23", "bb2");
		tokenCache2.put("aa42", "bb2");
		tokenCache2.put("aa52", "bb2");
		tokenCache2.put("aa62", "bb2");
		tokenCache2.put("aa72", "bb2");
		tokenCache2.put("aa82", "bb2");
		assertEquals(7, tokenCache2.size());
		
	}

	
	@Test
	public void testInitializationCacheEmptiness(){
		requestTokenService.invalidateAll();
		assertEquals(0, requestTokenService.getSize());

		assertEquals(0, requestTokenService.getStats().hitCount());		
		assertEquals(0, requestTokenService.getStats().evictionCount());
		assertEquals(0, requestTokenService.getStats().loadExceptionCount());
		assertEquals(0, requestTokenService.getStats().loadSuccessCount());
		assertEquals(0, requestTokenService.getStats().missCount());
		assertEquals(0, requestTokenService.getSize());
		
	}

	@Test
	public void testGenerateAndStoreToken() {
		requestTokenService.invalidateAll();
		assertEquals(0, requestTokenService.getSize());

		String token = requestTokenService.generateAndStore();
		assertNotNull(token);
		
		assertEquals(0, requestTokenService.getStats().hitCount());		
		assertEquals(0, requestTokenService.getStats().evictionCount());
		assertEquals(0, requestTokenService.getStats().loadExceptionCount());
		assertEquals(0, requestTokenService.getStats().loadSuccessCount());
		assertEquals(0, requestTokenService.getStats().missCount());
		assertEquals(1, requestTokenService.getSize());

	}
	
	@Test
	public void testConsume() {
		requestTokenService.invalidateAll();
		assertEquals(0, requestTokenService.getSize());

		String token = requestTokenService.generateAndStore();
		assertEquals(1, requestTokenService.getSize());
		
		assertTrue(requestTokenService.consume(token));
		assertEquals(0, requestTokenService.getSize());

		assertEquals(0, requestTokenService.getStats().hitCount());		
		assertEquals(0, requestTokenService.getStats().evictionCount());
		assertEquals(0, requestTokenService.getStats().loadExceptionCount());
		assertEquals(0, requestTokenService.getStats().loadSuccessCount());
		assertEquals(0, requestTokenService.getStats().missCount());
		
	}
	
	@Test
	public void testConsumeMultiple() {
		requestTokenService.invalidateAll();
		assertEquals(0, requestTokenService.getSize());

		String token1 = requestTokenService.generateAndStore();
		assertEquals(1, requestTokenService.getSize());
		
		String token2 = requestTokenService.generateAndStore();
		assertEquals(2, requestTokenService.getSize());
		
		String token3 = requestTokenService.generateAndStore();
		assertEquals(3, requestTokenService.getSize());
		
		String token4 = requestTokenService.generateAndStore();
		assertEquals(4, requestTokenService.getSize());
		 
		assertTrue(requestTokenService.consume(token2));
		assertEquals(3, requestTokenService.getSize());
		
		assertTrue(requestTokenService.consume(token3));
		assertEquals(2, requestTokenService.getSize());
		
		assertTrue(requestTokenService.consume(token1));
		assertEquals(1, requestTokenService.getSize());
		
		assertTrue(requestTokenService.consume(token4));
		assertEquals(0, requestTokenService.getSize());
		
		assertEquals(0, requestTokenService.getStats().hitCount());		
		assertEquals(0, requestTokenService.getStats().evictionCount());
		assertEquals(0, requestTokenService.getStats().loadExceptionCount());
		assertEquals(0, requestTokenService.getStats().loadSuccessCount());
		assertEquals(0, requestTokenService.getStats().missCount());
		
	}

	@Test
	public void testConsumeSame() {
		requestTokenService.invalidateAll();
		assertEquals(0, requestTokenService.getSize());
		
		String token1 = requestTokenService.generateAndStore();
		assertEquals(1, requestTokenService.getSize());
		
		assertTrue(requestTokenService.consume(token1));
		assertEquals(0, requestTokenService.getSize());

		assertFalse(requestTokenService.consume(token1));
		assertEquals(0, requestTokenService.getSize());
		
		
		assertEquals(0, requestTokenService.getStats().hitCount());		
		assertEquals(0, requestTokenService.getStats().evictionCount());
		assertEquals(0, requestTokenService.getStats().loadExceptionCount());
		assertEquals(0, requestTokenService.getStats().loadSuccessCount());
		assertEquals(0, requestTokenService.getStats().missCount());
	}

	@Test
	public void testExpireTokenAndConsume() throws InterruptedException{
		requestTokenService.invalidateAll();
		assertEquals(0, requestTokenService.getSize());
		
		String token1 = requestTokenService.generateAndStore();
		//4 seconds, by configuration the evict time is 3seconds
		for(int i = 5; i >= 0; i--) {
			   logger.info("[ExpireToken Scenario 1] - 4 seconds waiting..." + i + " seconds remaining, be patient.");
			   Thread.sleep(1000);
		}
		assertEquals(1, requestTokenService.getSize());
		
		assertFalse(requestTokenService.consume(token1));
		assertEquals(0, requestTokenService.getSize());
		
		
		assertEquals(0, requestTokenService.getStats().hitCount());		
		assertEquals(0, requestTokenService.getStats().evictionCount());
		assertEquals(0, requestTokenService.getStats().loadExceptionCount());
		assertEquals(0, requestTokenService.getStats().loadSuccessCount());
		assertEquals(0, requestTokenService.getStats().missCount());
	}
	
	@Test
	public void testExpireTokenAndConsumeMultiple() throws InterruptedException{
		requestTokenService.invalidateAll();
		assertEquals(0, requestTokenService.getSize());
		
		String token1 = requestTokenService.generateAndStore();
		assertEquals(1, requestTokenService.getSize());
		
		String token2 = requestTokenService.generateAndStore();
		assertEquals(2, requestTokenService.getSize());

		String token3 = requestTokenService.generateAndStore();
		assertEquals(3, requestTokenService.getSize());

		//4 seconds, by configuration the evict time is 3seconds
		for(int i = 5; i >= 0; i--) {
			   logger.info("[ExpireToken Scenario 2] - 4 seconds waiting..." + i + " seconds remaining, be patient.");
			   Thread.sleep(1000);
		}
		assertEquals(3, requestTokenService.getSize());
		
		assertFalse(requestTokenService.consume(token1));
		assertFalse(requestTokenService.consume(token2));
		assertFalse(requestTokenService.consume(token3));
		assertEquals(0, requestTokenService.getSize());
		
		
		assertEquals(0, requestTokenService.getStats().hitCount());		
		assertEquals(0, requestTokenService.getStats().evictionCount());
		assertEquals(0, requestTokenService.getStats().loadExceptionCount());
		assertEquals(0, requestTokenService.getStats().loadSuccessCount());
		assertEquals(0, requestTokenService.getStats().missCount());
	}

	@Test
	public void testFullCache() throws InterruptedException{
		requestTokenService.invalidateAll();
		assertEquals(0, requestTokenService.getSize());

		for(int x = 1 ; x <= 5000 ; x++){
			requestTokenService.generateAndStore();
			
		}
		
		//the max size of the cache is set to 100
		assertEquals(5000, requestTokenService.getSize());
		
		assertEquals(0, requestTokenService.getStats().hitCount());		
		assertEquals(0, requestTokenService.getStats().evictionCount());
		assertEquals(0, requestTokenService.getStats().loadExceptionCount());
		assertEquals(0, requestTokenService.getStats().loadSuccessCount());
		assertEquals(0, requestTokenService.getStats().missCount());
	}

    @Test
    public void testIsPresent() {
        requestTokenService.invalidateAll();
        assertEquals(0, requestTokenService.getSize());

        String token = requestTokenService.generateAndStore();
        assertEquals(1, requestTokenService.getSize());

        assertTrue(requestTokenService.isPresent(token));
        assertEquals(1, requestTokenService.getSize()); //the token is not removed
    }

		
}

