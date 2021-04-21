//package eu.europa.ec.eci.oct.webcommons.security.requesttoken;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertNull;
//import static org.junit.Assert.assertTrue;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.MockitoAnnotations;
//import org.springframework.mock.web.MockHttpServletRequest;
//
//import eu.europa.ec.eci.oct.webcommons.controller.CommonControllerConstants;
//
//public class RequestTokenHelperTest {
//	
//	private RequestTokenHelper requestTokenHelper;
//	
//	private MockHttpServletRequest request;
//	
//	private TokenConsumeCondition alwaysConsumeCondition = new TokenConsumeCondition() {			
//		private static final long serialVersionUID = -3415519161214671065L;
//
//		@Override
//		public boolean isConsumable(HttpServletRequest request) {
//			return true;
//		}
//		
//		@Override
//		public String toString() {
//			return "<consume always>";
//		}
//	};
//	
//	private TokenConsumeCondition neverConsumeCondition = new TokenConsumeCondition() {			
//		private static final long serialVersionUID = -5663532778809494044L;
//
//		@Override
//		public boolean isConsumable(HttpServletRequest request) {
//			return false;
//		}
//		
//		@Override
//		public String toString() {
//			return "<consume never>";
//		}
//	};
//	
//	@Before
//	public void onSetUp() throws Exception {
//		MockitoAnnotations.initMocks(this);
//		request = new MockHttpServletRequest();
//		
//		requestTokenHelper = new RequestTokenHelper();
//	}
//	
//	@Test
//	public void testGetToken() {
//		RequestToken token = requestTokenHelper.getToken(request);
//		
//		assertNotNull("token value should not be null", token.getValue());
//		assertNotNull("token expected on session", request.getSession(false).getAttribute(
//				CommonControllerConstants.SESSION_ATTR_EXPECTED_REQUEST_TOKEN));
//		assertEquals("bad token on session", token, request.getSession(false).getAttribute(
//				CommonControllerConstants.SESSION_ATTR_EXPECTED_REQUEST_TOKEN));
//	}
//	
//	@Test
//	public void testCheckAndConsume_NoTokenOnSession() {
//		request.setParameter(CommonControllerConstants.REQUEST_PARAM_REQUEST_TOKEN,
//				"GIVEN-TOKEN-VALUE");
//		
//		boolean isExpected = requestTokenHelper.checkAndConsume(request);
//		
//		assertFalse("isExpected should be false", isExpected);
//	}
//	
//	@Test
//	public void testCheckAndConsume_NoTokenOnRequest() {
//		request.getSession(true).setAttribute(CommonControllerConstants.SESSION_ATTR_EXPECTED_REQUEST_TOKEN,
//				new RequestToken("EXPECTED-TOKEN-VALUE", null));
//		
//		boolean isExpected = requestTokenHelper.checkAndConsume(request);
//		
//		assertFalse("isExpected should be false", isExpected);
//	}
//	
//	@Test
//	public void testCheckAndConsume_TokenMismatch() {
//		request.getSession(true).setAttribute(CommonControllerConstants.SESSION_ATTR_EXPECTED_REQUEST_TOKEN,
//				new RequestToken("EXPECTED-TOKEN-VALUE", null));
//		request.setParameter(CommonControllerConstants.REQUEST_PARAM_REQUEST_TOKEN,
//				"GIVEN-TOKEN-VALUE");
//		
//		boolean isExpected = requestTokenHelper.checkAndConsume(request);
//		
//		assertFalse("isExpected should be false", isExpected);
//	}
//	
//	@Test
//	public void testCheckAndConsume_OK_Consume() {
//		RequestToken expectedToken = new RequestToken("EXPECTED-TOKEN-VALUE", alwaysConsumeCondition);
//		request.getSession(true).setAttribute(CommonControllerConstants.SESSION_ATTR_EXPECTED_REQUEST_TOKEN,
//				expectedToken);
//		request.setParameter(CommonControllerConstants.REQUEST_PARAM_REQUEST_TOKEN,
//				"EXPECTED-TOKEN-VALUE");
//		
//		boolean isExpected = requestTokenHelper.checkAndConsume(request);
//		
//		assertTrue("isExpected should be true", isExpected);
//		assertNull("no token expected on session", request.getSession(false).getAttribute(
//				CommonControllerConstants.SESSION_ATTR_EXPECTED_REQUEST_TOKEN));
//	}
//	
//	@Test
//	public void testCheckAndConsume_OK_DoNotConsume() {
//		RequestToken expectedToken = new RequestToken("EXPECTED-TOKEN-VALUE", neverConsumeCondition);
//		request.getSession(true).setAttribute(CommonControllerConstants.SESSION_ATTR_EXPECTED_REQUEST_TOKEN,
//				expectedToken);
//		request.setParameter(CommonControllerConstants.REQUEST_PARAM_REQUEST_TOKEN,
//				"EXPECTED-TOKEN-VALUE");
//		
//		boolean isExpected = requestTokenHelper.checkAndConsume(request);
//		
//		assertTrue("isExpected should be true", isExpected);
//		assertEquals("token expected on session", expectedToken,
//				request.getSession(false).getAttribute(CommonControllerConstants.SESSION_ATTR_EXPECTED_REQUEST_TOKEN));
//	}
//
//}
