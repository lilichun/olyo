/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.gui.main.contactlist;

import java.awt.*;

import javax.swing.*;

import net.java.sip.communicator.impl.gui.customcontrols.*;
import net.java.sip.communicator.impl.gui.i18n.*;
import net.java.sip.communicator.impl.gui.utils.*;

/**
 * The <tt>RenameContactPanel</tt> is where the user could change the name of
 * a meta contact.
 * 
 * @author Yana Stamcheva
 */
public class RenameContactPanel extends JPanel {

    private JLabel uinLabel = new JLabel(
        Messages.getI18NString("newName").getText());
    
    private JTextField textField = new JTextField();
    
    private JPanel dataPanel = new JPanel(new BorderLayout(5, 5));
    
    private SIPCommMsgTextArea infoLabel = new SIPCommMsgTextArea(
        Messages.getI18NString("renameContactWizard").getText());
    
    private JLabel infoTitleLabel = new JLabel(
        Messages.getI18NString("renameContact").getText());
    
    private JLabel iconLabel = new JLabel(new ImageIcon(ImageLoader
            .getImage(ImageLoader.RENAME_DIALOG_ICON)));
    
    private JPanel labelsPanel = new JPanel(new GridLayout(0, 1));
    
    private JPanel rightPanel = new JPanel(new BorderLayout());
    
    /**
     * Creates an instance of <tt>RenameContactPanel</tt> and initializes it.
     */
    public RenameContactPanel(String oldName) {
        super(new BorderLayout());
                
        this.textField.setText(oldName);
        this.textField.select(0, oldName.length());
        
        this.setPreferredSize(new Dimension(500, 200));
        
        this.iconLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 10));
        
        this.infoLabel.setEditable(false);
                
        this.dataPanel.add(uinLabel, BorderLayout.WEST);
        
        this.dataPanel.add(textField, BorderLayout.CENTER);
        
        this.infoTitleLabel.setHorizontalAlignment(JLabel.CENTER);
        this.infoTitleLabel.setFont(Constants.FONT.deriveFont(Font.BOLD, 18));
        
        this.labelsPanel.add(infoTitleLabel);
        this.labelsPanel.add(infoLabel);        
        this.labelsPanel.add(dataPanel);
        
        this.rightPanel.add(labelsPanel, BorderLayout.NORTH);
        
        this.add(iconLabel, BorderLayout.WEST);
        this.add(rightPanel, BorderLayout.CENTER);
    }
    
    /**
     * Returns the new name entered by the user.
     * @return the new name entered by the user.
     */
    public String getNewName(){
        return textField.getText();
    }
    
    /**
     * Requests the focus in the text field.
     */
    public void requestFocusInField() {
        this.textField.requestFocus();
    }
}
