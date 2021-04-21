package eu.europa.ec.eci.oct.webcommons.services.security;

import java.util.Calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europa.ec.eci.oct.webcommons.services.commons.ServicesTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/services-test.xml")
public class RequestTokenServiceTimedLoadImplTest extends ServicesTest {
	
	@Autowired
	private RequestTokenService  requestTokenServiceFuture;

	@Test
	public void testOverloadFullEffordByTime() throws InterruptedException{
		long cacheSize = 100000;
		long startTime = Calendar.getInstance().getTimeInMillis();
		
		assertEquals(0, requestTokenServiceFuture.getSize());
		for(long x = 1 ; x <= cacheSize ; x++){
			requestTokenServiceFuture.generateAndStore();
		}
		
		long endTime = Calendar.getInstance().getTimeInMillis();
		assertEquals(cacheSize, requestTokenServiceFuture.getSize());
		
		if(endTime - startTime > 3*60*1000){
			fail("was " + (endTime - startTime));
		}
	}	
}

