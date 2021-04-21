package eu.europa.ec.eci.oct.offline;

import java.io.IOException;

import eu.europa.ec.eci.oct.offline.startup.CryptoOfflineTool;
import eu.europa.ec.eci.oct.offline.support.log.OfflineCryptoToolLogger;

/**
 * @author: micleva
 * @created: 11/7/11
 * @project OCT
 */
public class COTLauncher {
	private static final String VERBOSE_ARG_NAME = "-verbose";

	public static void main(String[] args) throws IOException {
		OfflineCryptoToolLogger.setup(readBooleanArgValue(VERBOSE_ARG_NAME, args));

		CryptoOfflineTool offlineTool = CryptoOfflineTool.initialize();
		offlineTool.show();
	}

	private static boolean readBooleanArgValue(String argName, String[] args) {
		for (String arg : args) {
			if (arg.startsWith(argName)) {
				String value = arg.substring(argName.length() + 1);
				return Boolean.valueOf(value);
			}
		}
		return false;
	}
}
