package eu.europa.ec.eci.oct.webcommons.security.requesttoken;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang3.StringEscapeUtils;

import eu.europa.ec.eci.oct.utils.CommonsConstants;

/**
 * Custom JSP tag for generating a hidden input which
 * transmits the current request token value, if any.
 * <p>
 * The current request token is expected to be registered
 * in the page scope of the JSP page. The current implementation
 * does not take into account the attribute name, it looks only
 * for a compatible type of the token object, namely
 * {@link RequestToken}.
 * </p>
 *
 * @author keschma
 *
 */
public class RequestTokenTag extends TagSupport {

	private static final long serialVersionUID = 7363104215172278745L;
	
	@Override
	public int doStartTag() throws JspException {
		try {
			pageContext.getOut().append(getHtml());
		} catch (IOException e) {
			throw new JspException("I/O Error", e);
		}
		
		return SKIP_BODY;
	}
	
	private String getHtml() {
		final StringBuilder builder = new StringBuilder();
		
		// locate any request token in the page scope
		RequestToken requestToken = null;
		for (Enumeration<String> e = pageContext.getAttributeNamesInScope(PageContext.REQUEST_SCOPE); e.hasMoreElements();) {
			final String attributeName = e.nextElement();
			final Object attributeObject = pageContext.getAttribute(attributeName, PageContext.REQUEST_SCOPE);
			if (attributeObject instanceof RequestToken) {
				requestToken = (RequestToken) attributeObject;
				break;
			}
		}
		
		// if the request token is found, generate a hidden input for it
		if (requestToken != null) {
			builder.append("<input type=\"hidden\" name=\"");
			builder.append(StringEscapeUtils.escapeHtml4(CommonsConstants.REQUEST_PARAM_REQUEST_TOKEN));
			builder.append("\" value=\"");
			builder.append(StringEscapeUtils.escapeHtml4(requestToken.getValue()));
			builder.append("\"/>");
		}
		
		return builder.toString();
	}

}
