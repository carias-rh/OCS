package eu.europa.ec.eci.oct.offline.startup;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import eu.europa.ec.eci.oct.offline.support.crypto.KeyProvider;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationChangedListener;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationProvider;
import eu.europa.ec.eci.oct.offline.support.log.OfflineCryptoToolLogger;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJFrame;

/**
 * @author: micleva
 * @created: 11/7/11
 * @project OCT
 */
public class CryptoOfflineTool implements LocalizationChangedListener {

	private static OfflineCryptoToolLogger log = OfflineCryptoToolLogger.getLogger(CryptoOfflineTool.class.getName());
	/**
	 * As this is the main frame of the offline tool, allow only one instance
	 */
	private static CryptoOfflineTool cryptoOfflineToolInstance;
	private static Image defaultIcon;
	
	private CryptoOfflineContentPanel contentPanel;

	public JFrame frame;

	/**
	 * Timer to be used to lock the application on inactivity.
	 */
	public Timer timer;

	private CryptoOfflineTool() throws IOException {

		try {
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			// unable to load the nimbus look and feel..
			log.debug("Unable to load the Nimbus look and feel", e);
		}

		// initialize the frame
		setUpFrame();

		
		// add the menu
//		frame.setJMenuBar(new CryptoOfflineMenuBar(this));

		// add the frame content

		contentPanel = new CryptoOfflineContentPanel(this);
		frame.setContentPane(contentPanel);
		LocalizationProvider.getInstance().addLocalizedChangedListener(this);

		// initialize application locking mechanism
		initialiseLockingMechanism(SecurityConstants.APPLICATION_TIMEOUT);
		
	}

	private void setUpFrame() {
		frame = new LocalizedJFrame("offlineTool.title");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				log.info("CryptoOfflineTool closed successfully");
			}
		});
	}

	@Override
	public void onLocalizationChanged(Locale locale) {
		frame.pack();
	}

	public void show() {
		frame.pack();
		setFrameLocationOnScreen();
		frame.setResizable(true);
		frame.setVisible(true);

		StartupPassword startupPassword = StartupPassword.getInstance(frame);
		startupPassword.openAndValidatePassword();
		
	}

	private void setFrameLocationOnScreen() {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;

		int width = frame.getSize().width;
		int height = frame.getSize().height;

		// put in vertically at 33% from top and horizontally at the middle
		frame.setLocation((screenWidth / 2) - width / 2, (screenHeight / 2 - height / 2));

		if (defaultIcon != null) {
			frame.setIconImage(defaultIcon);
		}
	}

	public JFrame getFrame() {
		return frame;
	}

	public static synchronized CryptoOfflineTool initialize() throws IOException {
		if (cryptoOfflineToolInstance == null) {
			cryptoOfflineToolInstance = new CryptoOfflineTool();
		}

		try {
			defaultIcon = (new ImageIcon(CryptoOfflineTool.class.getClassLoader().getResource("images/Apps-preferences-desktop-cryptography-icon.png")))
					.getImage();
		} catch (Exception e) {
			// being unable to load the icon should not stop loading the tool.
			log.debug("Unable to load the tool's icon", e);
		}
		log.info("CryptoOfflineTool started successfully");

		return cryptoOfflineToolInstance;
	}

	/**
	 * The application needs to be locked when there is no user generated activity for a given amount of time.
	 */
	private void initialiseLockingMechanism(final int lockDelay) {
		initializeSecurityTimer(lockDelay);
		initialiseHumanInputListener();
	}

	/**
	 * Creates a timer which will be used to lock the application after some inactivity period.
	 */
	private void initializeSecurityTimer(final int delay) {
		timer = new Timer(delay, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (KeyProvider.keyFileExists()) {
					log.info("Security timer was fired. Will now lock the application.");
					StartupPassword startupPassword = StartupPassword.getInstance(frame);
					if (!startupPassword.isVisible()) {
						startupPassword.openAndValidatePassword();
						restartSecurityTimer();
					}
				} else {
					log.info("Security timer was fired. Application is not initialised. Will now kill the application.");
					frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
				}
			}
		});
		timer.setInitialDelay(delay);
		timer.setRepeats(true);
		timer.setCoalesce(true);

		timer.start();
	}

	/**
	 * As we are using a timer service to lock the application, we should be able to reset this timer on human
	 * interaction (mouse and keyboard).
	 */
	private void initialiseHumanInputListener() {
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			public void eventDispatched(AWTEvent event) {
				// listen only to specific events
				if (event instanceof MouseEvent) {
					MouseEvent evt = (MouseEvent) event;
					if (evt.getID() == MouseEvent.MOUSE_CLICKED || evt.getID() == MouseEvent.MOUSE_PRESSED || evt.getID() == MouseEvent.MOUSE_RELEASED) {
						restartSecurityTimer();
					}
				} else if (event instanceof KeyEvent) {
					KeyEvent evt = (KeyEvent) event;
					if (evt.getID() == KeyEvent.KEY_PRESSED) {
						restartSecurityTimer();
					}
				}

			}
		}, AWTEvent.MOUSE_EVENT_MASK + AWTEvent.KEY_EVENT_MASK);
	}

	/**
	 * Restarts the security timer.
	 */
	private void restartSecurityTimer() {
		timer.restart();
	}

	public static CryptoOfflineTool getInstance() {
		return cryptoOfflineToolInstance;
	}
	public CryptoOfflineContentPanel getContentPanel() {
		return contentPanel;
	}
	
	public void setContectPane(JPanel contentPanel){
		frame.setContentPane(contentPanel);
	}
	
	public void swap(){
		contentPanel.swapPanel();
	}
	

}
