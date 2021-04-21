package eu.europa.ec.eci.oct.offline.dialog.pwd;

import eu.europa.ec.eci.oct.offline.support.localization.LocalizationMessageProvider;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationProvider;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * @author: micleva
 * @created: 4/3/12
 * @project ECI
 */
public class ConfirmationPasswordValidator {
    private Container parent;

    public ConfirmationPasswordValidator(Container parent) {
        this.parent = parent;
    }

    public boolean validate(char[] pass, char[] confPass) {

        boolean match = Arrays.equals(pass, confPass);
        if (!match) {

            LocalizationProvider localizationProvider = LocalizationProvider.getInstance();
            LocalizationMessageProvider messageProvider = localizationProvider.getCurrentMessageProvider();

            JOptionPane.showMessageDialog(parent,
                    messageProvider.getMessage("dialog.password.validate.confirm"),
                    messageProvider.getMessage("common.validation.dialog.title"),
                    JOptionPane.WARNING_MESSAGE);

            return false;
        }
        return true;
    }
}
