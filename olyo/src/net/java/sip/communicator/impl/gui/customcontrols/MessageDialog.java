/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package net.java.sip.communicator.impl.gui.customcontrols;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import net.java.sip.communicator.impl.gui.i18n.*;
import net.java.sip.communicator.impl.gui.utils.*;

/**
 * The <tt>MessageDialog</tt> is a <tt>JDialog</tt> that contains a question
 * message, two buttons to confirm or cancel the question and a check box that
 * allows user to choose to not be questioned any more over this subject.
 * <p>
 * The message and the name of the "OK" button could be configured.
 * 
 * @author Yana Stamcheva
 */
public class MessageDialog
    extends SIPCommDialog
    implements ActionListener
{
    private JButton cancelButton = new JButton(
        Messages.getI18NString("cancel").getText());

    private JButton okButton = new JButton(
        Messages.getI18NString("ok").getText());

    private JCheckBox doNotAskAgain = new JCheckBox(Messages
            .getI18NString("doNotAskAgain").getText());

    private JLabel iconLabel = new JLabel(new ImageIcon(ImageLoader
            .getImage(ImageLoader.WARNING_ICON)));

    private StyledHTMLEditorPane messageArea = new StyledHTMLEditorPane();

    private JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

    private JPanel checkBoxPanel = new JPanel(
            new FlowLayout(FlowLayout.LEADING));

    private JPanel messagePanel = new JPanel(new BorderLayout(5, 5));
    
    private int returnCode;
    
    /**
     * Indicates that the OK button is pressed.
     */
    public static final int OK_RETURN_CODE = 0;
    
    /**
     * Indicates that the Cancel button is pressed.
     */
    public static final int CANCEL_RETURN_CODE = 1;
    
    /**
     * Indicates that the OK button is pressed and the Don't ask check box is
     * checked.
     */
    public static final int OK_DONT_ASK_CODE = 2;
    
    private Object lock = new Object();

    /**
     * Creates an instance of <tt>MessageDialog</tt> by specifying the
     * owner window.
     * @param owner This dialog owner.
     */
    public MessageDialog(Frame owner)
    {
        super(owner);

        this.getContentPane().setLayout(new BorderLayout(5, 5));

        this.messageArea.setOpaque(false);
        this.messageArea.setEditable(false);
        this.messageArea.setContentType("text/html");

        this.messagePanel.setBorder(
            BorderFactory.createEmptyBorder(10, 10, 0, 10));
        this.checkBoxPanel.setBorder(
            BorderFactory.createEmptyBorder(0, 10, 10, 10));

        this.init();
    }

    /**
     * Creates an instance of <tt>MessageDialog</tt> by specifying the
     * owner window and the message to be displayed.
     * @param owner the dialog owner
     * @param title the title of the message
     * @param message the message to be displayed
     */
    public MessageDialog(Frame owner, String title, String message)
    {
        this(owner);

        this.setTitle(title);

        this.messageArea.setText(message);
    }

    /**
     * Creates an instance of <tt>MessageDialog</tt> by specifying the
     * owner window and the message to be displayed.
     * @param owner the dialog owner
     * @param title the title of the message
     * @param message the message to be displayed
     * @param okButtonName ok button name
     */
    public MessageDialog(   Frame owner,
                            String title,
                            String message,
                            String okButtonName)
    {
        this(owner, title, message);

        this.okButton.setText(okButtonName);
        this.okButton.setMnemonic(okButtonName.charAt(0));
    }

    /**
     * Creates an instance of <tt>MessageDialog</tt> by specifying the
     * owner window and the message to be displayed.
     * @param owner the dialog owner
     * @param title the title of the message
     * @param message the message to be displayed
     * @param isCancelButtonEnabled <code>true</code> to show the Cancel button,
     * <code>false</code> - otherwise
     */
    public MessageDialog(   Frame owner,
                            String title,
                            String message,
                            boolean isCancelButtonEnabled)
    {
        this(owner, title, message);

        if(!isCancelButtonEnabled)
        {
            doNotAskAgain.setText(
                Messages.getI18NString("doNotShowAgain").getText());

            buttonsPanel.remove(cancelButton);
        }
    }

    /**
     * Initializes this dialog.
     */
    private void init()
    {
        this.getRootPane().setDefaultButton(okButton);

        this.checkBoxPanel.add(doNotAskAgain);

        this.buttonsPanel.add(okButton);
        this.buttonsPanel.add(cancelButton);

        this.okButton.addActionListener(this);
        this.cancelButton.addActionListener(this);

        this.cancelButton.setMnemonic(cancelButton.getText().charAt(0));
        this.messagePanel.add(iconLabel, BorderLayout.WEST);
        this.messagePanel.add(messageArea, BorderLayout.CENTER);

        this.getContentPane().add(messagePanel, BorderLayout.NORTH);
        this.getContentPane().add(checkBoxPanel, BorderLayout.CENTER);
        this.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
    }

    /**
     * Sets the message to be displayed.
     * @param message The message to be displayed.
     */
    public void setMessage(String message)
    {
        this.messageArea.setText(message);
    }

    /**
     * Shows the dialog.
     * @return The return code that should indicate what was the choice of
     * the user. If the user chooses cancel, the return code is the 
     * CANCEL_RETURN_CODE.
     */
    public int showDialog()
    {
        this.pack();

        this.setVisible(true);

        synchronized (lock)
        {
            try
            {
                lock.wait();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        return returnCode;
    }

    /**
     * Handles the <tt>ActionEvent</tt>. Depending on the user choice sets
     * the return code to the appropriate value.
     */
    public void actionPerformed(ActionEvent e)
    {
        JButton button = (JButton)e.getSource();

        if(button.equals(okButton))
        {
            if (doNotAskAgain.isSelected())
            {
                this.returnCode = OK_DONT_ASK_CODE;
            }
            else
            {
                this.returnCode = OK_RETURN_CODE;
            }
        }
        else
        {
            this.returnCode = CANCEL_RETURN_CODE;
        }

        synchronized (lock)
        {
            lock.notify();
        }

        this.dispose();
    }

    protected void close(boolean isEscaped)
    {
        this.cancelButton.doClick();
    }
}
