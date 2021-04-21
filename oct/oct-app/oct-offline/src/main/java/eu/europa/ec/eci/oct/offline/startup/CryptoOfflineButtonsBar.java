package eu.europa.ec.eci.oct.offline.startup;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import eu.europa.ec.eci.oct.offline.actions.DefaultActionHandler;
import eu.europa.ec.eci.oct.offline.actions.OfflineToolCommandAction;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJButton;

public class CryptoOfflineButtonsBar extends JPanel{

	private static final long serialVersionUID = -4383359696967489989L;

	private CryptoOfflineTool cryptoOfflineTool;
	
	private static LocalizedJButton bInitializeSecurity;
	private static LocalizedJButton bGenerateWebAdminCredential;
	private static LocalizedJButton bDecryptLoginChallenge;
	private static LocalizedJButton bDecryptData;
	private static LocalizedJButton bOpenKeysFolder;

	private static CryptoOfflineButtonsBar instance;

	public static final CryptoOfflineButtonsBar getInstance(CryptoOfflineTool cryptoOfflineTool) {
		if (instance == null) {
			instance = new CryptoOfflineButtonsBar(cryptoOfflineTool);
		}
		return instance;
	}
	
	private CryptoOfflineButtonsBar(CryptoOfflineTool cryptoOfflineTool) {
		this.cryptoOfflineTool = cryptoOfflineTool;
		
		this.setLayout(new FlowLayout());
		
	    addWidgets();
	}


	private void addWidgets() {
		
		bInitializeSecurity = generateButton(
				"generated.html.menu.initialize.label",
				OfflineToolCommandAction.INITIALIZE_CRYPTO_OFFLINE_TOOL,
				new ImageIcon(CryptoOfflineTool.class.getClassLoader().getResource("images/initialize-cryptografy.png")));
		
		String bDecryptLoginChallengeText = "generated.html.menu.hashPassword.label";
//		if(KeyProvider.keyWacExists()){
//			bDecryptLoginChallengeText = "generated.html.menu.reHashPassword.label";
//		}
		bGenerateWebAdminCredential = generateButton(
				bDecryptLoginChallengeText, 
				OfflineToolCommandAction.HASH_PASSWORD,
				new ImageIcon(CryptoOfflineTool.class.getClassLoader().getResource("images/web-admin-credentials.png")));

		bOpenKeysFolder = generateButton(
				"generated.html.menu.openWebCredentialsFolder.label", 
				OfflineToolCommandAction.OPEN_WEB_SECURITY_FOLDER,
				new ImageIcon(CryptoOfflineTool.class.getClassLoader().getResource("images/folder-open.png")));

		bDecryptLoginChallenge = generateButton(
				"generated.html.decrypt.login.challenge.label", 
				OfflineToolCommandAction.DECRYPT_LOGIN_CHALLENGE,
				new ImageIcon(CryptoOfflineTool.class.getClassLoader().getResource("images/login-challenge.png")));

		bDecryptData = generateButton(
				"generated.html.decrypt.exportedData.label", 
				OfflineToolCommandAction.DECRYPT_EXPORTED_DATA,
				new ImageIcon(CryptoOfflineTool.class.getClassLoader().getResource("images/decrypt-data.png")));

		this.add(bInitializeSecurity);
		this.add(bGenerateWebAdminCredential);
		this.add(bOpenKeysFolder);
		this.add(bDecryptLoginChallenge);
		this.add(bDecryptData);
	}


	private LocalizedJButton generateButton(String messageKey, OfflineToolCommandAction actionCommand, Icon icon){
        LocalizedJButton button = new LocalizedJButton(messageKey, icon);
        button.setVerticalTextPosition(JLabel.BOTTOM);
        button.setHorizontalTextPosition(JLabel.CENTER);
        button.setPreferredSize(new Dimension(110,110));
        button.addActionListener(new DefaultActionHandler(cryptoOfflineTool));
        button.setActionCommand(actionCommand.name());
        button.setEnabled(CryptoOfflineButtonBarVisibility.getInstance().isEnabled(actionCommand));
        button.addMouseListener(new CryptoOfflineButtonBarMouseListener(cryptoOfflineTool));
        return button;
		
	}
	
	public static void setButtonStatus(CryptoOfflineStatus newStatus){
		bInitializeSecurity.setEnabled(CryptoOfflineButtonBarVisibility.getInstance().isEnabled(OfflineToolCommandAction.INITIALIZE_CRYPTO_OFFLINE_TOOL, newStatus));
		bGenerateWebAdminCredential.setEnabled(CryptoOfflineButtonBarVisibility.getInstance().isEnabled(OfflineToolCommandAction.HASH_PASSWORD, newStatus));
		if(newStatus == CryptoOfflineStatus.PASSWORD_HASHED){
			bGenerateWebAdminCredential.setLocalizedText("generated.html.menu.reHashPassword.label");
		}
		bOpenKeysFolder.setEnabled(CryptoOfflineButtonBarVisibility.getInstance().isEnabled(OfflineToolCommandAction.OPEN_WEB_SECURITY_FOLDER, newStatus));
		bDecryptLoginChallenge.setEnabled(CryptoOfflineButtonBarVisibility.getInstance().isEnabled(OfflineToolCommandAction.DECRYPT_LOGIN_CHALLENGE, newStatus));
		bDecryptData.setEnabled(CryptoOfflineButtonBarVisibility.getInstance().isEnabled(OfflineToolCommandAction.DECRYPT_EXPORTED_DATA, newStatus));
	}

	public static LocalizedJButton getbInitializeSecurity() {
		return bInitializeSecurity;
	}

	public static void setbInitializeSecurity(LocalizedJButton bInitializeSecurity) {
		CryptoOfflineButtonsBar.bInitializeSecurity = bInitializeSecurity;
	}

	public static LocalizedJButton getbOpenKeysFolder() {
		return bOpenKeysFolder;
	}

	public static void setbGenerateWebAdminCredential(
			LocalizedJButton bGenerateWebAdminCredential) {
		CryptoOfflineButtonsBar.bGenerateWebAdminCredential = bGenerateWebAdminCredential;
	}

	public static LocalizedJButton getbGenerateWebAdminCredential() {
		return bGenerateWebAdminCredential;
	}

	public static LocalizedJButton getbDecryptLoginChallenge() {
		return bDecryptLoginChallenge;
	}

	public static void setbDecryptLoginChallenge(
			LocalizedJButton bDecryptLoginChallenge) {
		CryptoOfflineButtonsBar.bDecryptLoginChallenge = bDecryptLoginChallenge;
	}

	public static LocalizedJButton getbDecryptData() {
		return bDecryptData;
	}

	public static void setbDecryptData(LocalizedJButton bDecryptData) {
		CryptoOfflineButtonsBar.bDecryptData = bDecryptData;
	}

}
