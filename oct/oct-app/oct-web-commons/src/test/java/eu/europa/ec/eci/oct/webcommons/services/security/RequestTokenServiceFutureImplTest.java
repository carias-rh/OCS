package eu.europa.ec.eci.oct.webcommons.services.security;

import java.text.MessageFormat;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import eu.europa.ec.eci.oct.webcommons.services.commons.ServicesTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/services-test.xml")
public class RequestTokenServiceFutureImplTest extends ServicesTest {
	
	@Autowired
	private RequestTokenService  requestTokenServiceFuture;
	
    private static final ExecutorService tp = Executors.newFixedThreadPool(8);
    private static final ExecutorService tpService = Executors.newFixedThreadPool(8); 

	@Test
	public void testOverloadFullEffordServiceByTime() throws InterruptedException, ExecutionException{
		long interations = 50000;
		
		TokenCacheFromServiceWorker worker1 = new TokenCacheFromServiceWorker(interations); 
		TokenCacheFromServiceWorker worker2 = new TokenCacheFromServiceWorker(interations); 
		TokenCacheFromServiceWorker worker3 = new TokenCacheFromServiceWorker(interations); 
		TokenCacheFromServiceWorker worker4 = new TokenCacheFromServiceWorker(interations); 
		Future<Long> future1 = tpService.submit(worker1);
		Future<Long> future2 = tpService.submit(worker2);
		Future<Long> future3 = tpService.submit(worker3);
		Future<Long> future4 = tpService.submit(worker4);

		while(!future1.isDone() || !future2.isDone()|| !future3.isDone() || !future4.isDone()){
			logger.debug(MessageFormat.format("future1 = {0} future2 = {1} future2 = {2} future3 = {3} ", future1.isDone(), future2.isDone(), future3.isDone(), future4.isDone()));			
			logger.debug(MessageFormat.format("size {0} ", requestTokenServiceFuture.getSize()));
			Thread.sleep(1000); 
			
		
		}
		logger.debug(MessageFormat.format("future1 = {0} future2 = {1} future2 = {2} future3 = {3} ", future1.isDone(), future2.isDone(), future3.isDone(), future4.isDone()));			
		logger.debug(MessageFormat.format("future1 = {0} future2 = {1} future2 = {2} future3 = {3} ", future1.get(), future2.get(), future3.get(), future4.get()));			
		
        
		tpService.shutdown();

		assertEquals(interations*4, requestTokenServiceFuture.getSize());
	}	

    
	@Test
	public void testOverloadFullEffordByTime() throws InterruptedException, ExecutionException{
		long cacheSize = 1000000;

		final Cache<String, String> tokenCache = CacheBuilder.newBuilder()
				.maximumSize(cacheSize)
				.expireAfterWrite(90000, TimeUnit.SECONDS)
				.concurrencyLevel(8)
				.removalListener(
					new RemovalListener<String, String>() {
					//Keep in mind that the removal is done in read/write operation and it's not a separate task that periodically check the validity of the token
					//Mostly of debug operations.
					@Override
					public void onRemoval(RemovalNotification<String, String> notification) {
						logger.debug("Token Cache REMOVED cause:[" + notification.getCause().name() + "] - token : [" + notification.getKey() + "]");
					}
				})
				.build();

		long interations = 50000;
		
		TokenCacheWorker worker1 = new TokenCacheWorker(tokenCache, interations); 
		TokenCacheWorker worker2 = new TokenCacheWorker(tokenCache, interations); 
		TokenCacheWorker worker3 = new TokenCacheWorker(tokenCache, interations); 
		TokenCacheWorker worker4 = new TokenCacheWorker(tokenCache, interations); 
		Future<Long> future1 = tp.submit(worker1);
		Future<Long> future2 = tp.submit(worker2);
		Future<Long> future3 = tp.submit(worker3);
		Future<Long> future4 = tp.submit(worker4);

		while(!future1.isDone() || !future2.isDone()|| !future3.isDone() || !future4.isDone()){
			logger.debug(MessageFormat.format("future1 = {0} future2 = {1} future2 = {2} future3 = {3} ", future1.isDone(), future2.isDone(), future3.isDone(), future4.isDone()));			
			logger.debug(MessageFormat.format("size {0} ", tokenCache.size()));			
			Thread.sleep(1000); 
			
		
		}
		logger.debug(MessageFormat.format("future1 = {0} future2 = {1} future2 = {2} future3 = {3} ", future1.isDone(), future2.isDone(), future3.isDone(), future4.isDone()));			
		logger.debug(MessageFormat.format("future1 = {0} future2 = {1} future2 = {2} future3 = {3} ", future1.get(), future2.get(), future3.get(), future4.get()));			
		
        
		tp.shutdown();

		assertEquals(interations*4, tokenCache.size());
	}	
	
    private class TokenCacheWorker implements Callable<Long> {
		final Cache<String, String> tokenCache;
		long iterations;

    	TokenCacheWorker(final Cache<String, String> tokenCache, long iterations){
			this.tokenCache = tokenCache;
			this.iterations = iterations;
    	}
    	
		@Override
		public Long call() throws Exception {
			for(long x = 1 ; x <= iterations ; x++){
				String uuid = UUID.randomUUID().toString();
				tokenCache.put(uuid, uuid);
			}
			return new Random(1).nextLong();
		}
    }
	
    private class TokenCacheFromServiceWorker implements Callable<Long> {
		long iterations;

		TokenCacheFromServiceWorker(long iterations){
			this.iterations = iterations;
    	}
    	
		@Override
		public Long call() throws Exception {
			for(long x = 1 ; x <= iterations ; x++){
				requestTokenServiceFuture.generateAndStore();
			}
			return new Random(1).nextLong();
		}
    }
	
}

