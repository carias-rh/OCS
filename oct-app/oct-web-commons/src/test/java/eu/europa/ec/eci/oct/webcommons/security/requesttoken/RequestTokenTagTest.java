//package eu.europa.ec.eci.oct.webcommons.security.requesttoken;
//
//import static org.junit.Assert.assertEquals;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.util.Date;
//import java.util.Enumeration;
//
//import javax.servlet.jsp.JspWriter;
//import javax.servlet.jsp.PageContext;
//import javax.servlet.jsp.tagext.TagSupport;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//
//public class RequestTokenTagTest {
//	
//	private RequestTokenTag requestTokenTag;
//	
//	@Mock
//	private PageContext pageContext;
//		
//	@Mock
//	private JspWriter writer;
//	
//	@Before
//	public void onSetUp() throws Exception {
//		MockitoAnnotations.initMocks(this);
//		
//		requestTokenTag = new RequestTokenTag();
//		requestTokenTag.setPageContext(pageContext);
//		
//		when(pageContext.getOut()).thenReturn(writer);
//	}
//	
//	@Test
//	public void testDoStartTag_NoRequestTokenAttribute() throws Exception {
//		when(pageContext.getAttributeNamesInScope(PageContext.REQUEST_SCOPE)).thenReturn(new Enumeration<String>() {
//			private int count = 0;
//			
//			@Override
//			public boolean hasMoreElements() {
//				return count < 2;
//			}
//			
//			@Override
//			public String nextElement() {
//				return (count++ == 0) ? "foo" : "bar";
//			}
//		});
//		when(pageContext.getAttribute("foo", PageContext.REQUEST_SCOPE)).thenReturn("FOO");
//		when(pageContext.getAttribute("bar", PageContext.REQUEST_SCOPE)).thenReturn(new Date());
//		
//		int result = requestTokenTag.doStartTag();
//		
//		assertEquals(TagSupport.SKIP_BODY, result);
//		
//		verify(writer).append("");
//		
//		verify(pageContext).getAttributeNamesInScope(PageContext.REQUEST_SCOPE);
//		verify(pageContext).getAttribute("foo", PageContext.REQUEST_SCOPE);
//		verify(pageContext).getAttribute("bar", PageContext.REQUEST_SCOPE);
//	}
//	
//	@Test
//	public void testDoStartTag_WithRequestTokenAttribute() throws Exception {
//		RequestToken requestToken = new RequestToken("HELLO", null);
//		
//		when(pageContext.getAttributeNamesInScope(PageContext.REQUEST_SCOPE)).thenReturn(new Enumeration<String>() {
//			private int count = 0;
//			
//			@Override
//			public boolean hasMoreElements() {
//				return count < 2;
//			}
//			
//			@Override
//			public String nextElement() {
//				return (count++ == 0) ? "foo" : "bar";
//			}
//		});
//		when(pageContext.getAttribute("foo", PageContext.REQUEST_SCOPE)).thenReturn("FOO");
//		when(pageContext.getAttribute("bar", PageContext.REQUEST_SCOPE)).thenReturn(requestToken);
//		
//		int result = requestTokenTag.doStartTag();
//	
//		assertEquals(TagSupport.SKIP_BODY, result);
//		
//		verify(writer).append("<input type=\"hidden\" name=\"oct_request_token\" value=\"HELLO\"/>");
//		
//		verify(pageContext).getAttributeNamesInScope(PageContext.REQUEST_SCOPE);
//		verify(pageContext).getAttribute("foo", PageContext.REQUEST_SCOPE);
//		verify(pageContext).getAttribute("bar", PageContext.REQUEST_SCOPE);
//	}
//	
//}
