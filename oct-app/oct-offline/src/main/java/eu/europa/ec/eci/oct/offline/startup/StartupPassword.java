package eu.europa.ec.eci.oct.offline.startup;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import eu.europa.ec.eci.oct.offline.dialog.pwd.PasswordDialog;
import eu.europa.ec.eci.oct.offline.support.crypto.CryptographyHelper;
import eu.europa.ec.eci.oct.offline.support.crypto.KeyProvider;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationMessageProvider;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationProvider;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJButton;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJLabel;

/**
 * @author: micleva
 * @created: 11/22/11
 * @project OCT
 */
public class StartupPassword extends PasswordDialog {

	private static final long serialVersionUID = -1808347305605480559L;

	private static StartupPassword instance;

	public static final StartupPassword getInstance(Container parent) {
		if (instance == null) {
			instance = new StartupPassword(parent);
		}
		return instance;
	}

	private StartupPassword(Container parent) {
		super(parent, false, false);
	}

	@Override
	public JPanel getExplanatoryPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		panel.add(new LocalizedJLabel("generated.html.crypto.startup.password.details"));

		return panel;
	}

	public void openAndValidatePassword() {
		if (KeyProvider.keyFileExists()) {

			final PasswordDialog pwdDialog = this;
			setPasswordConfirmedAction(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					char[] pwd = pwdDialog.getPassword();
					if (pwd.length > 0) {
						if (CryptographyHelper.initiateCryptoModule(pwd)) {
							pwdDialog.dispose();
						} else {

							LocalizationProvider localizationProvider = LocalizationProvider.getInstance();
							LocalizationMessageProvider messageProvider = localizationProvider.getCurrentMessageProvider();

							JOptionPane.showMessageDialog(pwdDialog, messageProvider.getMessage("generated.html.crypto.startup.error"),
									messageProvider.getMessage("common.validation.dialog.title"), JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});

			super.openDialog(null);
		}
	}

	@Override
	protected List<JButton> getAdditionalButtons() {
		JButton closeButton = new LocalizedJButton("common.exit");
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame jFrame = CryptoOfflineTool.getInstance().getFrame();
				jFrame.dispatchEvent(new WindowEvent(jFrame, WindowEvent.WINDOW_CLOSING));
			}
		});

		return Collections.singletonList(closeButton);
	}

	@Override
	protected ActionListener attachCustomActionHandler() {
		return null;
	}
}
