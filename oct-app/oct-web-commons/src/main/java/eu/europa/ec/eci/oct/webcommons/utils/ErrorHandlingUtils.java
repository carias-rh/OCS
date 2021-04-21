/** ====================================================================
 * Licensed under the European Union Public Licence (EUPL v1.2) 
 * https://joinup.ec.europa.eu/community/eupl/topic/public-consultation-draft-eupl-v12
 * ====================================================================
 *
 * @author Daniel CHIRITA
 * @created: 23/05/2013
 *
 */
package eu.europa.ec.eci.oct.webcommons.utils;

import java.util.Random;

/**
 * Error handling related utilities.
 * 
 * @author chiridl
 * 
 */
public final class ErrorHandlingUtils {

	/**
	 * Generates a random hexadecimal string to be used as error token.
	 * 
	 * @param tokenLength
	 *            the length of the string to be generated
	 * @return
	 */
	public static final String generateErrorToken(byte tokenLength) {
		final StringBuilder result = new StringBuilder();

		Random random = new Random();
		for (byte idx = 0; idx < tokenLength; idx++) {
			result.append(Integer.toHexString(random.nextInt(17)));
		}

		return result.toString().toUpperCase();
	}

}
