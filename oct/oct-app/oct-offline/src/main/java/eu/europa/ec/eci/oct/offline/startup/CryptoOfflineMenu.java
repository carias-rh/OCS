package eu.europa.ec.eci.oct.offline.startup;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import eu.europa.ec.eci.oct.offline.actions.DefaultActionHandler;
import eu.europa.ec.eci.oct.offline.actions.OfflineToolCommandAction;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationProvider;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJLabel;

/**
 * 
 * @author falloda
 *
 */

public class CryptoOfflineMenu extends JPanel{

	private static final long serialVersionUID = -3661446153749496773L;
	
	private CryptoOfflineTool cryptoOfflineTool;


	public CryptoOfflineMenu(CryptoOfflineTool cryptoOfflineTool) {
		this.cryptoOfflineTool = cryptoOfflineTool;

    	this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
    	this.setBorder(BorderFactory.createRaisedBevelBorder());


        //add widgets
        addWidgets();
	}


	private void addWidgets() {
		
//    	JLabel euLogo = new JLabel(new ImageIcon(CryptoOfflineTool.class.getClassLoader().getResource("images/eu_66x30.png")));
//    	euLogo.setPreferredSize(new Dimension(60,30));
//    	this.add(euLogo);

    	this.add(Box.createRigidArea(new Dimension(220,0)));
    	LocalizedJLabel menuTitle = new LocalizedJLabel("offlineTool.menu.title");
    	Font f = new Font("Dialog", Font.PLAIN, 24);
    	menuTitle.setFont(f);
    	this.add(menuTitle);

    	this.add(Box.createRigidArea(new Dimension(120,0)));
//		TODO falloda, to enable when we will have a users manual 
//    	this.add(Box.createRigidArea(new Dimension(80,0)));
//    	ImageIcon imageIcon = new ImageIcon(CryptoOfflineTool.class.getClassLoader().getResource("images/help.png"));
//    	ImageIcon imageIconRollover = new ImageIcon(CryptoOfflineTool.class.getClassLoader().getResource("images/help-over.png"));
    	
//        JButton helpButton = new JButton(imageIcon);
//        helpButton.setPreferredSize(new Dimension(32,32));
//        helpButton.setContentAreaFilled(false);
//        helpButton.setFocusPainted(false);
//        helpButton.setBorderPainted(false);
//        helpButton.setRolloverEnabled(true);
//        helpButton.setRolloverIcon(imageIconRollover);
//        helpButton.addActionListener(new DefaultActionHandler(cryptoOfflineTool));
//        helpButton.setActionCommand(OfflineToolCommandAction.SHOW_HELP.name());
//        
//    	this.add(helpButton);
    	

    	
        CryptoOfflineConfig config = CryptoOfflineConfig.getInstance();

        //add the localization label/combo only if is enabled
        if (config.getBooleanConfigValue(ConfigProperty.LOCALIZATION_ENABLED, false)) {
            //add the language choice
            final LocalizationProvider localizationProvider = LocalizationProvider.getInstance();
            Locale currentLocale = localizationProvider.getCurrentLocale();

            final List<Locale> supportedLocales = localizationProvider.getSupportedLocales();
            int defaultSelectedIndex = 6;

            Pattern pattern = Pattern.compile(",");
            List<ComboMenuItemData> comboItemsList = new ArrayList<ComboMenuItemData>();
            
            for (int i = 0, availableLocalesLength = supportedLocales.size(); i < availableLocalesLength; i++) {
                Locale availableLocale = supportedLocales.get(i);
                
                String language = availableLocale.getLanguage();
                String propertyValues[] = pattern.split(localizationProvider.getCurrentMessageProvider().getMessage("menu.languages." + language));
                
                ComboMenuItemData comboMenuItemData = new ComboMenuItemData(language, new Integer(propertyValues[0]), propertyValues[1]);
                comboItemsList.add(comboMenuItemData);
                
                if (availableLocale.equals(currentLocale)) {
                    defaultSelectedIndex = comboMenuItemData.getOrder() - 1;
                }
            }
            final JComboBox comboBox = new JComboBox(new SortedComboBoxModel(comboItemsList));

            comboBox.setSelectedIndex(defaultSelectedIndex);

            comboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	String selectedLanguage = ((ComboMenuItemData)comboBox.getSelectedItem()).getLanguage();
                	Locale selectedLocale = null;
                	for(Locale locale : supportedLocales){
                		if(locale.getLanguage().equalsIgnoreCase(selectedLanguage)){
                			selectedLocale = locale;		
                		}
                	}
                    
                    localizationProvider.changeLocale(selectedLocale);
                }
            });
            
            comboBox.setMaximumSize(new Dimension(100, 40));
        	this.add(comboBox);
        }
	}
}
