package eu.europa.ec.eci.oct.offline.dialog.challenge;

import eu.europa.ec.eci.oct.crypto.CipherOperation;
import eu.europa.ec.eci.oct.crypto.Cryptography;
import eu.europa.ec.eci.oct.offline.business.DecryptConstants;
import eu.europa.ec.eci.oct.offline.support.crypto.CryptographyHelper;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationMessageProvider;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationProvider;
import eu.europa.ec.eci.oct.offline.support.log.OfflineCryptoToolLogger;
import org.apache.commons.codec.binary.Hex;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author: micleva
 * @created: 11/9/11
 * @project OCT
 */
class DecryptActionHandler implements ActionListener {
    private static OfflineCryptoToolLogger log = OfflineCryptoToolLogger.getLogger(DecryptActionHandler.class.getName());

    private JTextField challengeResponse;
    private JTextArea challengeTextArea;

    DecryptActionHandler(JTextArea challengeTextArea, JTextField challengeResponse) {
        this.challengeTextArea = challengeTextArea;
        this.challengeResponse = challengeResponse;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final String inputText = challengeTextArea.getText();

        LocalizationProvider localizationProvider = LocalizationProvider.getInstance();
        LocalizationMessageProvider messageProvider = localizationProvider.getCurrentMessageProvider();
        try {
            String textToDecrypt = inputText.trim();
            if (textToDecrypt.length() > 0) {
                Cryptography cryptography = new Cryptography(CipherOperation.DECRYPT, CryptographyHelper.getKey());
                byte[] decryptedData = cryptography.perform(Hex.decodeHex(textToDecrypt.toCharArray()));
                String textValue = new String(decryptedData, DecryptConstants.CHARACTER_ENCODING);
                challengeResponse.setText(textValue);
            }
            challengeResponse.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.green),
                    BorderFactory.createEmptyBorder(0, 2, 0, 0)
            ));
        } catch (Exception ex) {
            log.debug("Unable to decrypt inputText text: " + inputText, ex);
            challengeResponse.setText(messageProvider.getMessage("decrypt.failed"));
            challengeResponse.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.red),
                    BorderFactory.createEmptyBorder(0, 2, 0, 0)
            ));
        }
    }
}
