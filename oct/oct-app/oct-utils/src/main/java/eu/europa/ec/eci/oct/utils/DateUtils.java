package eu.europa.ec.eci.oct.utils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.ThreadLocalRandom;

public class DateUtils {
	public static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
	public static final String REVERSE_DATE_FORMAT = "yyyy-MM-dd'Z'";
	public static final String EXPORT_DATE_FORMAT = "yyyy-MM-dd";
	public static final String EP_CRYPTO_TOOL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.sss";

	public static String formatDate(Date date) {
		return DateUtils.formatDate(date, DEFAULT_DATE_FORMAT);
	}

	public static String formatDate(Date date, String format) {
		return date == null ? "" : new SimpleDateFormat(format).format(date);
	}

	/**
	 * Parses a Date from a string. The date format used is
	 * {@link DateValidator.DEFAULT_DATE_FORMAT}.
	 * 
	 * @param text
	 *            the text
	 * @return the date
	 * @throws ParseException
	 *             on parsing failures
	 */
	public static Date parseDate(String text, String dateFormat) throws ParseException {
		final SimpleDateFormat dateParser = new SimpleDateFormat(dateFormat);
		dateParser.setLenient(false); // use strict date parsing

		final ParsePosition parsePosition = new ParsePosition(0);
		final Date date = dateParser.parse(text, parsePosition);
		// accept only, if the *entire format string length* was used during
		// parsing
		if (parsePosition.getIndex() < DEFAULT_DATE_FORMAT.length()) {
			throw new ParseException("Parsed an incomplete string with regard to format '" + DEFAULT_DATE_FORMAT + "': " + text.substring(0, parsePosition.getIndex()), parsePosition.getIndex());
		}
		return date;
	}

	public static Date parseDate(String dateAsString) throws ParseException {
		return parseDate(dateAsString, DEFAULT_DATE_FORMAT);
	}

	public static Date getRandomDateBetween(Date minDate, Date startDate) {
		return getRandomDateBetween(minDate.getTime(), startDate.getTime());
	}

	public static Date getRandomDateBetween(long minDateMsec, long maxDateMsec) {
		long randomDateMsec = ThreadLocalRandom.current().nextLong(minDateMsec, maxDateMsec - 1);
		Date randomDate = new Date(randomDateMsec);
		return randomDate;
	}

	public static String getHumanTimeFromMillisec(long spentTime) {

		long second = (spentTime / 1000) % 60;
		long minute = (spentTime / (1000 * 60)) % 60;
		long hour = (spentTime / (1000 * 60 * 60)) % 24;

		String humanTime = String.format("%02d:%02d:%02d", hour, minute, second);

		return humanTime;
	}

	public static Date setBeginningOfTheDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, c.getActualMinimum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getActualMinimum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getActualMinimum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getActualMinimum(Calendar.MILLISECOND));
		return new Date(c.getTimeInMillis());
	}

	public static Date setDayAfter(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, c.getActualMaximum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getActualMaximum(Calendar.MINUTE));
		c.set(Calendar.MILLISECOND, c.getActualMaximum(Calendar.MILLISECOND));
		return new Date(c.getTimeInMillis());
	}

	/**
	 * Adds a number of days to a date returning a new object. The original
	 * {@code Date} is unchanged.
	 * 
	 * @param date
	 *            the date, not null
	 * @param amount
	 *            the amount to add, may be negative
	 * @return the new {@code Date} with the amount added
	 * @throws IllegalArgumentException
	 *             if the date is null
	 */
	public static Date addDays(Date date, int amount) {
		return add(date, Calendar.DAY_OF_MONTH, amount);
	}

	/**
	 * Adds to a date returning a new object. The original {@code Date} is
	 * unchanged.
	 * 
	 * @param date
	 *            the date, not null
	 * @param calendarField
	 *            the calendar field to add to
	 * @param amount
	 *            the amount to add, may be negative
	 * @return the new {@code Date} with the amount added
	 * @throws IllegalArgumentException
	 *             if the date is null
	 */
	public static Date add(Date date, int calendarField, int amount) {
		if (date == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(calendarField, amount);
		return c.getTime();
	}

	public static long getMillisecOfMidnightFromADate(Date dateOfSignature) {
		Calendar c = new GregorianCalendar();
		c.setTime(dateOfSignature);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.setTimeZone(TimeZone.getTimeZone("UTC"));
		return c.getTime().getTime();
	}

	public static String formatString(int month, int year) {
		String monthString = "";
		String yearString = year + "";
		if (month < 10) {
			monthString += "0";
		}
		monthString += month;
		return monthString + "/" + yearString;
	}

	public static boolean isBetween(Date startDate, Date endDate, Date betweenDate) {
		return betweenDate.compareTo(startDate) >= 0 && betweenDate.compareTo(endDate) <= 0;
	}

}
