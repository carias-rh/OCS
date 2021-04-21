package eu.europa.ec.eci.oct.export.utils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class ItemProcessorsUtils {

	public static XMLGregorianCalendar getXMLdate(Date registrationDate) throws DatatypeConfigurationException {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(registrationDate);
		DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
		return datatypeFactory.newXMLGregorianCalendar(calendar);
	}

	public static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
	public static final long ONE_DAY_MILLISEC = 86400000;

	public static Date parseDate(String text, String dateFormat) throws ParseException {
		final SimpleDateFormat dateParser = new SimpleDateFormat(dateFormat);
		dateParser.setLenient(false); // use strict date parsing
		dateParser.setTimeZone(TimeZone.getTimeZone("UTC"));
		final ParsePosition parsePosition = new ParsePosition(0);
		final Date date = dateParser.parse(text, parsePosition);
		// accept only, if the *entire format string length* was used during
		// parsing
		if (parsePosition.getIndex() < DEFAULT_DATE_FORMAT.length()) {
			throw new ParseException("Parsed an incomplete string with regard to format '" + DEFAULT_DATE_FORMAT + "': " + text.substring(0, parsePosition.getIndex()), parsePosition.getIndex());
		}
		return date;
	}
	
	public static String formatDate(Date date, String format) {
		return date == null ? "" : new SimpleDateFormat(format).format(date);
	}
}
