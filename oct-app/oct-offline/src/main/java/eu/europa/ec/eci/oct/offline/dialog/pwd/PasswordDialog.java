package eu.europa.ec.eci.oct.offline.dialog.pwd;

import eu.europa.ec.eci.oct.offline.support.Utils;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJButton;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJCheckBox;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJLabel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * @author: micleva
 * @created: 11/22/11
 * @project OCT
 */
public abstract class PasswordDialog extends JDialog {

    private static final long serialVersionUID = 1816284675007177106L;

    protected Container parent;
    protected JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField userNameField;
    protected JButton confirmButton;
    private Border warningBorder = BorderFactory.createLineBorder(Color.red);

    private boolean showConfirmationPassword;
    private boolean showUsername;

    public PasswordDialog(Container parent, boolean showConfirmationPassword, boolean showUsername) {
        this.parent = parent;
        this.showConfirmationPassword = showConfirmationPassword;
        this.showUsername = showUsername;

        //do not allow closing this dialog
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        //the dialog needs to be modal in order to not allow interactions with the
        // behind components while this one is used
        this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        this.setResizable(false);

        Container container = this.getContentPane();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        //add all the components to this panel
        addWidgets(container);
    }
    
    protected abstract ActionListener attachCustomActionHandler();

    private void addWidgets(Container container) {

        JPanel detailsPanel = getExplanatoryPanel();
        detailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        container.add(detailsPanel);

        JPanel passPanel = buildPasswordPanel();
        passPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(passPanel);

        
        //show password Checkbox
        JPanel ckPanel = new JPanel();
        ckPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        ckPanel.setLayout(new BoxLayout(ckPanel, BoxLayout.X_AXIS));
        ckPanel.setAlignmentX(Component.LEFT_ALIGNMENT);


        LocalizedJCheckBox showPasswordCheckBox = new LocalizedJCheckBox("dialog.password.showPassword.button");
//        showPasswordCheckBox.setAlignmentY(Component.LEFT_ALIGNMENT);
//        ckPanel.add(Box.createRigidArea(new Dimension(5,0)));
        ckPanel.add(Box.createHorizontalGlue());
        ckPanel.add(showPasswordCheckBox);
//        showPasswordCheckBox.setAlignmentX(Component.RIGHT_ALIGNMENT);

        showPasswordCheckBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(passwordField.getEchoChar() == '*'){
    				passwordField.setEchoChar((char)0);
    				if(confirmPasswordField != null){
    					confirmPasswordField.setEchoChar((char)0);
    				}
				}else{
    				passwordField.setEchoChar('*');
    				if(confirmPasswordField != null){
    					confirmPasswordField.setEchoChar('*');
    				}
				}
			}
		});
        container.add(ckPanel);
        
        
        //button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        confirmButton = new LocalizedJButton("dialog.password.confirm.button");
        confirmButton.addActionListener(attachCustomActionHandler());
        buttonPanel.add(confirmButton);

        java.util.List<JButton> buttonList = getAdditionalButtons();
        for (JButton jButton : buttonList) {
            buttonPanel.add(Utils.getXSeparator(5));
            buttonPanel.add(Box.createHorizontalGlue());

            buttonPanel.add(jButton);
        }

        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        container.add(buttonPanel);
    }

    private JPanel buildPasswordPanel() {
        JPanel passPanel = new JPanel();
        passPanel.setLayout(new BoxLayout(passPanel, BoxLayout.Y_AXIS));
        passPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        final int separatorXSize = 10;
        final int passwordFiledColumns = 15;

        JPanel linePanel = new JPanel();
        linePanel.setLayout(new BoxLayout(linePanel, BoxLayout.X_AXIS));
        
        //username
        JLabel userNameLabel = null;
        if(showUsername){
            userNameLabel = new LocalizedJLabel("dialog.password.userName.label");
            userNameField = new JTextField(passwordFiledColumns);
            linePanel.add(userNameLabel);
            linePanel.add(Utils.getXSeparator(separatorXSize));
            linePanel.add(Box.createHorizontalGlue());
            linePanel.add(userNameField);
            passPanel.add(linePanel);
            passPanel.add(Utils.getYSeparator(5));
            linePanel = new JPanel();
            linePanel.setLayout(new BoxLayout(linePanel, BoxLayout.X_AXIS));
        	
        }

        
        //Password 
        JLabel passLabel = new LocalizedJLabel("dialog.password.label");
        linePanel.add(passLabel);
        linePanel.add(Utils.getXSeparator(separatorXSize));
        linePanel.add(Box.createHorizontalGlue());
        
        passwordField = new JPasswordField(passwordFiledColumns){
			private static final long serialVersionUID = -762563428113952779L;
			public void paste() { UIManager.getLookAndFeel().provideErrorFeedback(this); }
        };
        
        passwordField.setEchoChar('*');
        linePanel.add(passwordField);
        passPanel.add(linePanel);



        if (showConfirmationPassword) {
            passPanel.add(Utils.getYSeparator(5));
            linePanel = new JPanel();
            linePanel.setLayout(new BoxLayout(linePanel, BoxLayout.X_AXIS));
            JLabel confPassLabel = new LocalizedJLabel("dialog.password.confirm.label");
            linePanel.add(confPassLabel);
            linePanel.add(Utils.getXSeparator(separatorXSize));
            linePanel.add(Box.createHorizontalGlue());

            confirmPasswordField = new JPasswordField(passwordFiledColumns){
				private static final long serialVersionUID = 1674012043321908485L;
				public void paste() { UIManager.getLookAndFeel().provideErrorFeedback(this); }
            };
            
            confirmPasswordField.setEchoChar('*');
            linePanel.add(confirmPasswordField);

            int passWidthSize = (int) passLabel.getPreferredSize().getWidth();
            int cfPassWidthSize = (int) confPassLabel.getPreferredSize().getWidth();

            Dimension dimension = new Dimension(Math.max(passWidthSize, cfPassWidthSize), passLabel.getHeight());
            passLabel.setPreferredSize(dimension);
            confPassLabel.setPreferredSize(dimension);
            passPanel.add(linePanel);
            if(userNameLabel != null){
            	userNameLabel.setPreferredSize(dimension);
            }
        }

        return passPanel;
    }

    protected abstract List<JButton> getAdditionalButtons();

    public void openDialog(Container relatedParent) {
    	passwordField.setText(null);
    	if(confirmPasswordField != null){
    		confirmPasswordField.setText(null);
    	}
    	if(userNameField != null){
    		userNameField.setText(null);
    	}
        this.pack();
        if(relatedParent != null){
            this.setLocationRelativeTo(relatedParent);
        }else{
            this.setLocationRelativeTo(parent);
        }
        this.setVisible(true);
    }

    public void setPasswordConfirmedAction(ActionListener actionListener) {
        confirmButton.addActionListener(actionListener);
        passwordField.addActionListener(actionListener);
        if (confirmPasswordField != null) {
            confirmPasswordField.addActionListener(actionListener);
        }
    }

    public abstract JPanel getExplanatoryPanel();

    public char[] getPassword() {
        return passwordField.getPassword();
    }
    
    public char[] getConfPass() {
        return confirmPasswordField.getPassword();
    }
    
    public String getUsername() {
        return userNameField.getText();
    }

    
    public boolean validatePassword(int length) {
        boolean isValid = new SimplePasswordValidator(this).validate(getPassword(), length);

        handleBorderWarning(passwordField, isValid);

        return isValid;
    }

    public boolean validateConfPass() {
        boolean isValid = new ConfirmationPasswordValidator(this).
                validate(getPassword(), getConfPass());
        handleBorderWarning(confirmPasswordField, isValid);

        return isValid;
    }

    public boolean validateUsername() {
    	boolean isValid = true;
    	if(showUsername){
    		isValid = !userNameField.getText().equals("");
    		handleBorderWarning(userNameField, isValid);
    	}
    	
        return isValid;
    }

    private void handleBorderWarning(JTextField passwordField, boolean valid) {
        Border border = passwordField.getBorder();
        if (!valid && !(border instanceof CompoundBorder)) {
            passwordField.setBorder(BorderFactory.createCompoundBorder(
                    warningBorder,
                    passwordField.getBorder()
            ));
        } else if (valid && (border instanceof CompoundBorder)) {
            CompoundBorder compoundBorder = (CompoundBorder) border;
            passwordField.setBorder(compoundBorder.getInsideBorder());
        }
    }
    
    public Component getParentContainer(){
    	return parent;
    }
    
//    private void handleBorderWarningUsername(JTextField usernameField, boolean valid) {
//        Border border = usernameField.getBorder();
//        if (!valid && !(border instanceof CompoundBorder)) {
//            passwordField.setBorder(BorderFactory.createCompoundBorder(
//                    warningBorder,
//                    passwordField.getBorder()
//            ));
//        } else if (valid && (border instanceof CompoundBorder)) {
//            CompoundBorder compoundBorder = (CompoundBorder) border;
//            passwordField.setBorder(compoundBorder.getInsideBorder());
//        }
//    }

}
