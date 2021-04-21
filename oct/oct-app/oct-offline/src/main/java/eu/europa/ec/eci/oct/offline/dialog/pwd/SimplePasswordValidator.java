package eu.europa.ec.eci.oct.offline.dialog.pwd;

import java.awt.Container;

import javax.swing.JOptionPane;

import eu.europa.ec.eci.oct.offline.support.localization.LocalizationMessageProvider;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationProvider;

/**
 * @author: micleva
 * @created: 11/22/11
 * @project OCT
 */
public class SimplePasswordValidator {

	private Container parent;

	public SimplePasswordValidator(Container parent) {
		this.parent = parent;
	}

	public boolean validate(char[] pass, int minLength) {
		String pwd = new String(pass);
		if (pwd.length() < minLength || !validateStrength(pwd)) {

			LocalizationProvider localizationProvider = LocalizationProvider.getInstance();
			LocalizationMessageProvider messageProvider = localizationProvider.getCurrentMessageProvider();

			JOptionPane.showMessageDialog(parent, 
					messageProvider.getMessage("generated.html.dialog.password.validate.minimum.length",String.valueOf(minLength)),
					messageProvider.getMessage("common.validation.dialog.title"), JOptionPane.WARNING_MESSAGE);

			return false;
		}
		return true;
	}

	/**
	 * Validates the password strength. A password is considered strong when it contains at least one lower case letter,
	 * at least one upper case letter, at least one digit and at least one symbol.
	 * 
	 * @param password
	 * @return boolean indicating whether password is strong or not
	 */
	private boolean validateStrength(String password) {
		byte lowerCaseLetters = 0;
		byte upperCaseLetters = 0;
		byte digits = 0;

		for (byte idx = 0; idx < password.length(); idx++) {
			int c = password.charAt(idx);
			if (Character.isLowerCase(c)) {
				lowerCaseLetters++;
			}
			if (Character.isUpperCase(c)) {
				upperCaseLetters++;
			}
			if (Character.isDigit(c)) {
				digits++;
			}
		}

		byte symbols = (byte) (password.length() - (lowerCaseLetters + upperCaseLetters + digits));

		return lowerCaseLetters > 0 && upperCaseLetters > 0 && digits > 0 && symbols > 0;
	}
}
