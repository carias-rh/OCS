package eu.europa.ec.eci.oct.offline.dialog.initialize;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.KeyPair;
import java.security.PrivateKey;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.apache.commons.codec.binary.Hex;

import eu.europa.ec.eci.oct.crypto.Cryptography;
import eu.europa.ec.eci.oct.offline.dialog.pwd.PasswordDialog;
import eu.europa.ec.eci.oct.offline.startup.SecurityConstants;
import eu.europa.ec.eci.oct.offline.support.crypto.CryptographyHelper;
import eu.europa.ec.eci.oct.offline.support.crypto.KeyProvider;
import eu.europa.ec.eci.oct.offline.support.log.OfflineCryptoToolLogger;

/**
 * @author: micleva
 * @created: 11/9/11
 * @project OCT
 */
public class InitializeActionHandler implements ActionListener {

	private static OfflineCryptoToolLogger log = OfflineCryptoToolLogger.getLogger(InitializeActionHandler.class.getName());

	private PasswordDialog jDialog;

	public InitializeActionHandler(JTextArea responseTextArea, JTextArea publicKeyTextArea, JTextField hashedPasswordResult, PasswordDialog jDialog) {
		this.jDialog = jDialog;
	}
	public InitializeActionHandler(PasswordDialog jDialog) {
		this.jDialog = jDialog;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (jDialog.validatePassword(SecurityConstants.MINIMUM_PASSWORD_LENGTH) && jDialog.validateConfPass()) {
			proceedWithGeneratingKeys(jDialog.getPassword());
		}
	}

	private void proceedWithGeneratingKeys(final char[] passwordForKey) {
		final Component glassPane = jDialog.getGlassPane();

		SwingWorker<String, Object> worker = new SwingWorker<String, Object>() {
			@Override
			protected String doInBackground() throws Exception {
				String publicKeyHexEncoded = null;
				try {
					KeyPair keyPair = Cryptography.generateKeyPair();

					// for now just print out the private key
					PrivateKey privateKey = keyPair.getPrivate();
					KeyProvider.saveCryptoKeyWithPassword(privateKey, passwordForKey);

					publicKeyHexEncoded = new String(Hex.encodeHex(keyPair.getPublic().getEncoded()));

					KeyProvider.saveOctKeyWithPassword(keyPair.getPublic());
					CryptographyHelper.initiateCryptoModule(passwordForKey);

//					byte[] passBytes = new String(passwordForKey).getBytes("UTF-8");
				} catch (Exception ex) {
					//TODO s
//					responseTextArea.setText("Unable to generate the key pairs: " + ex.getMessage());
					log.debug("Unable to generate the key pairs", ex);
				} finally {
					glassPane.setVisible(false);
				}

				return publicKeyHexEncoded;
			}

			@Override
			protected void done() {
				// re-enable the button

			}
		};

		glassPane.setVisible(true);
		jDialog.getContentPane().removeAll();
		jDialog.getContentPane().add(((CryptoInitializeDialog)jDialog).buildResponseInitPasswordPanel());
		worker.execute();
	}
}
