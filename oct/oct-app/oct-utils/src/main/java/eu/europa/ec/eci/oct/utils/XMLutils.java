package eu.europa.ec.eci.oct.utils;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class XMLutils {
	
	public static final String KEY_TAG_OPEN = "<key>";
	public static final String KEY_TAG_CLOSE = "</key>";
	public static final String VALUE_TAG_OPEN = "<value>";
	public static final String VALUE_TAG_CLOSE = "</value>";
	public static final String SIGNATORY_INFO_TAG_OPEN = "<signatoryInfo>";
	public static final String SIGNATORY_INFO_TAG_CLOSE = "</signatoryInfo>";

	public static DocumentBuilderFactory getDocumentBuilderFactory() throws Exception {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		/* XML EXTERNAL ENTITY (XXE) PREVENTION */
		String FEATURE = null;
		try {
			// This is the PRIMARY defense. If DTDs (doctypes) are disallowed,
			// almost all XML entity attacks are prevented
			// Xerces 2 only -
			// http://xerces.apache.org/xerces2-j/features.html#disallow-doctype-decl
			FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
			documentBuilderFactory.setFeature(FEATURE, true);

			// If you can't completely disable DTDs, then at least do the
			// following:
			// Xerces 1 -
			// http://xerces.apache.org/xerces-j/features.html#external-general-entities
			// Xerces 2 -
			// http://xerces.apache.org/xerces2-j/features.html#external-general-entities
			// JDK7+ - http://xml.org/sax/features/external-general-entities
			FEATURE = "http://xml.org/sax/features/external-general-entities";
			documentBuilderFactory.setFeature(FEATURE, false);

			// Xerces 1 -
			// http://xerces.apache.org/xerces-j/features.html#external-parameter-entities
			// Xerces 2 -
			// http://xerces.apache.org/xerces2-j/features.html#external-parameter-entities
			// JDK7+ - http://xml.org/sax/features/external-parameter-entities
			FEATURE = "http://xml.org/sax/features/external-parameter-entities";
			documentBuilderFactory.setFeature(FEATURE, false);

			// Disable external DTDs as well
			FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
			documentBuilderFactory.setFeature(FEATURE, false);

			// and these as well, per Timothy Morgan's 2014 paper:
			// "XML Schema, DTD, and Entity Attacks" (see reference below)
			documentBuilderFactory.setXIncludeAware(false);
			documentBuilderFactory.setExpandEntityReferences(false);

			// And, per Timothy Morgan: "If for some reason support for inline
			// DOCTYPEs are a requirement, then
			// ensure the entity settings are disabled (as shown above) and
			// beware that SSRF attacks
			// (http://cwe.mitre.org/data/definitions/918.html) and denial
			// of service attacks (such as billion laughs or decompression bombs
			// via "jar:") are a risk."
		} catch (ParserConfigurationException e) {
			// This should catch a failed setFeature feature
			String errorMessage = "ParserConfigurationException was thrown. The feature '" + FEATURE + "' is probably not supported by your XML processor.";
			throw new Exception(errorMessage, e);
		}
		return documentBuilderFactory;
	}
	
}
