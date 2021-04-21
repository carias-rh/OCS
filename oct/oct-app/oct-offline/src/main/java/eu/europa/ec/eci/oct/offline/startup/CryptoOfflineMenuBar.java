package eu.europa.ec.eci.oct.offline.startup;

import eu.europa.ec.eci.oct.offline.actions.DefaultActionHandler;
import eu.europa.ec.eci.oct.offline.actions.OfflineToolCommandAction;
import eu.europa.ec.eci.oct.offline.support.Utils;
import eu.europa.ec.eci.oct.offline.support.crypto.KeyProvider;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationMessageProvider;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationProvider;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJLabel;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJMenu;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJMenuItem;

import javax.swing.*;
import javax.swing.border.BevelBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Locale;

/**
 * @author: micleva
 * @created: 11/9/11
 * @project OCT
 */
public class CryptoOfflineMenuBar extends JMenuBar {

	private static final long serialVersionUID = 3911458011306618539L;
	
	private CryptoOfflineTool cryptoOfflineTool;

    public CryptoOfflineMenuBar(CryptoOfflineTool cryptoOfflineTool) {
        this.cryptoOfflineTool = cryptoOfflineTool;

        this.setBorder(new BevelBorder(BevelBorder.RAISED));

        //add widgets
        addWidgets();
    }

    private void addWidgets() {
		LocalizationMessageProvider messageProvider = LocalizationProvider.getInstance().getCurrentMessageProvider();

        // Create a menu and add it to the menu bar.
        JMenu menu = new LocalizedJMenu("menuBar.menu.label");
        JMenuItem initItem = new LocalizedJMenuItem("menu.initialize.label");
        
        if (!KeyProvider.keyFileExists()) {
            initItem.setMnemonic('I');
            initItem.addActionListener(new DefaultActionHandler(cryptoOfflineTool));
            initItem.setActionCommand(OfflineToolCommandAction.INITIALIZE_CRYPTO_OFFLINE_TOOL.name());
        } else {
            initItem.setEnabled(false);
        }
        menu.add(initItem);

        JMenuItem hashPassword = new LocalizedJMenuItem("menu.hashPassword.label");
        hashPassword.setMnemonic('P');
        hashPassword.addActionListener(new DefaultActionHandler(cryptoOfflineTool));
        hashPassword.setActionCommand(OfflineToolCommandAction.HASH_PASSWORD.name());

        menu.add(hashPassword);

        CryptoOfflineConfig config = CryptoOfflineConfig.getInstance();

        if (config.getBooleanConfigValue(ConfigProperty.HELP_ENABLED, false)) {
            JMenuItem helpItem = new LocalizedJMenuItem("menu.help.label");
            helpItem.setMnemonic('H');
            helpItem.setActionCommand(OfflineToolCommandAction.SHOW_HELP.name());
            helpItem.addActionListener(new DefaultActionHandler(cryptoOfflineTool));
            menu.add(helpItem);
        }

        this.add(menu);
        this.add(new JSeparator(JSeparator.VERTICAL));

        //add the localization label/combo only if is enabled
        if (config.getBooleanConfigValue(ConfigProperty.LOCALIZATION_ENABLED, false)) {
            //add the language choice
            final LocalizationProvider localizationProvider = LocalizationProvider.getInstance();
            Locale currentLocale = localizationProvider.getCurrentLocale();
            
            
            final JComboBox comboBox = new JComboBox();
            comboBox.setMaximumSize(new Dimension(100, 23));
            final List<Locale> supportedLocales = localizationProvider.getSupportedLocales();
            int defaultSelectedIndex = 0;
            for (int i = 0, availableLocalesLength = supportedLocales.size(); i < availableLocalesLength; i++) {
                Locale availableLocale = supportedLocales.get(i);
                
                String language = availableLocale.getCountry();
                String description = localizationProvider.getCurrentMessageProvider().getMessage("menu.languages." + language);
                ComboMenuItemData comboMenuItemData = new ComboMenuItemData(language, 0 ,description);
                
              comboBox.addItem(comboMenuItemData);
            //                comboBox.addItem(availableLocale.getDisplayName());
                if (availableLocale.equals(currentLocale)) {
                    defaultSelectedIndex = i;
                }
            }
            comboBox.setSelectedIndex(defaultSelectedIndex);

            comboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Locale selectedLocale = supportedLocales.get(comboBox.getSelectedIndex());
                    localizationProvider.changeLocale(selectedLocale);
                }
            });
            //add the language on the right
            this.add(Box.createHorizontalGlue());
            this.add(new LocalizedJLabel("menuBar.language.label"));
            this.add(Utils.getXSeparator(3));
            this.add(comboBox);
        }
    }
}
