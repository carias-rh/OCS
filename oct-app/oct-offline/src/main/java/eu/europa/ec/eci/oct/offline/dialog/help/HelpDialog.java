package eu.europa.ec.eci.oct.offline.dialog.help;

import eu.europa.ec.eci.oct.offline.startup.ConfigProperty;
import eu.europa.ec.eci.oct.offline.startup.CryptoOfflineConfig;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationChangedListener;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationProvider;
import eu.europa.ec.eci.oct.offline.support.log.OfflineCryptoToolLogger;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJButton;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

/**
 * @author: micleva
 * @created: 11/9/11
 * @project OCT
 */
public class HelpDialog implements LocalizationChangedListener {
    private static OfflineCryptoToolLogger log = OfflineCryptoToolLogger.getLogger(HelpDialog.class.getName());

    private JDialog jDialog;
    private JTextPane helpTextPane;
    private JFrame ownerFrame;

    public HelpDialog(JFrame owner) {
        this.ownerFrame = owner;
        jDialog = new LocalizedJDialog(owner, "help.dialog.title");
        jDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        jDialog.setResizable(false);

        Container container = jDialog.getContentPane();
        //by default destroy all the objects associated with this dialog upon close
        jDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        //add widgets
        addWidgetsToContainer(container);

        //add the help text to the help text area
        fillHelpText(LocalizationProvider.getInstance().getCurrentLocale());
    }

    private void addWidgetsToContainer(Container dialogContainer) {

        //create the help text area
        helpTextPane = new JTextPane();
        helpTextPane.setBackground(Color.white);
        helpTextPane.setEditable(false);

        final JScrollPane scrollPane = new JScrollPane(helpTextPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        CryptoOfflineConfig config = CryptoOfflineConfig.getInstance();
        int width = config.getIntegerConfigValue(ConfigProperty.HELP_DIALOG_WIDTH, 300);
        int height = config.getIntegerConfigValue(ConfigProperty.HELP_DIALOG_HEIGHT, 200);
        scrollPane.setPreferredSize(new Dimension(width, height));

        jDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                //upon packing of the dialog, the scollbar moves down.
                //On window activation, put it up again
                JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
                verticalScrollBar.setValue(verticalScrollBar.getMinimum());
            }
        });

        JPanel wrappingPanel = new JPanel();
        wrappingPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        wrappingPanel.add(scrollPane);

        //add the help text and a separator
        dialogContainer.add(wrappingPanel, BorderLayout.NORTH);

        //prepare and add the close button
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
        JButton button = new LocalizedJButton("common.close");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jDialog.dispose();
            }
        });
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(button);

        //add the close button
        dialogContainer.add(buttonPane, BorderLayout.SOUTH);
    }

    private void fillHelpText(Locale locale) {
        BufferedReader bufferedReader =
                new BufferedReader(
                        new InputStreamReader(getHelpTextInputStreamForLocale(locale)));

        StringBuilder stringBuilder = new StringBuilder();
        try {
            String NL = System.getProperty("line.separator");
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append(NL);
            }
        } catch (IOException e) {
            stringBuilder.append("An exception appeared while trying to read the help text!");
            log.debug("Unable to read the content of the file", e);
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                //do nothing
                log.debug("Unable to close the help text file", e);
            }
        }
        helpTextPane.setContentType("text/html");
        helpTextPane.setText(stringBuilder.toString().trim());
    }

    private InputStream getHelpTextInputStreamForLocale(Locale locale) {
        ClassLoader classLoader = this.getClass().getClassLoader();

        InputStream inputStream = classLoader.getResourceAsStream("help/helpTextContent_" + locale.getLanguage() + ".html");

        if (inputStream == null) {
            return classLoader.getResourceAsStream("help/helpTextContent.html");
        }

        return inputStream;
    }

    public void show() {
        jDialog.pack();
        jDialog.setLocationRelativeTo(ownerFrame);
        jDialog.setVisible(true);
    }

    @Override
    public void onLocalizationChanged(Locale locale) {
        fillHelpText(locale);
    }
}
