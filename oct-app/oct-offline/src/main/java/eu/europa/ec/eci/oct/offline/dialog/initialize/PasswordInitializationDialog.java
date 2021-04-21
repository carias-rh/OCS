package eu.europa.ec.eci.oct.offline.dialog.initialize;

import eu.europa.ec.eci.oct.offline.dialog.pwd.PasswordDialog;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJButton;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

/**
 * @author: micleva
 * @created: 11/22/11
 * @project OCT
 */
public class PasswordInitializationDialog extends PasswordDialog {

	private static final long serialVersionUID = 7338221964195203503L;

	public PasswordInitializationDialog(Container parent) {
        super(parent, true, true);
    }

    @Override
    public JPanel getExplanatoryPanel() {

        JPanel panel = new JPanel();

        panel.add(new LocalizedJLabel("crypto.init.password.details"));

        return panel;
    }

    @Override
    protected List<JButton> getAdditionalButtons() {
        JButton cancelButton = new LocalizedJButton("decrypt.export.dialog.progress.button.cancel");
        final JDialog passDialog = this;
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                passDialog.dispose();
            }
        });

        return Collections.singletonList(cancelButton);
    }

	@Override
	protected ActionListener attachCustomActionHandler() {
		// TODO Auto-generated method stub
		return null;
	}
}
