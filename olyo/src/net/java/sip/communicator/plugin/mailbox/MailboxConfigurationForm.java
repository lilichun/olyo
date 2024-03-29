/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.plugin.mailbox;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.filechooser.*;
import java.io.*;
import java.awt.event.*;

import net.java.sip.communicator.service.gui.*;
import net.java.sip.communicator.service.configuration.*;
import org.osgi.framework.*;

/**
 * The <tt>ConfigurationForm</tt> that would be added in the user interface
 * configuration window. It allows the user to change parameters in the 
 * mailbox configuration
 * @author Ryan Ricard
 */
public class MailboxConfigurationForm
    extends JPanel
    implements ConfigurationForm, ActionListener
{
    private JLabel jlblOutgoingMessage = new JLabel(
                                Resources.getString("outgoing"));
    private JFileChooser  jfcOutgoingMessage = new JFileChooser();
    private JButton jbtnOutgoingMessage = new JButton(
                                    Resources.getString("browse"));
    private JTextField jtfOutgoingMessage = new JTextField();
    private JPanel jpOutgoingMessage = new JPanel
                                (new FlowLayout(FlowLayout.LEFT));
    private JLabel jlblIncomingMessage = new JLabel(
                                    Resources.getString("incoming"));
    private JFileChooser  jfcIncomingMessage = new JFileChooser();
    private JButton jbtnIncomingMessage = new JButton(
                                    Resources.getString("browse"));
    private JTextField jtfIncomingMessage = new JTextField();
    private JPanel jpIncomingMessage = new JPanel(
                                new FlowLayout(FlowLayout.LEFT));
    private JLabel jlblWaitTime = new JLabel(Resources.getString("waitTime"));
    private JSpinner jsWaitTime = new JSpinner(new SpinnerNumberModel(
                                                new Integer(10000),
                                                new Integer(0),
                                                null,
                                                new Integer(1000)));
    private JPanel jpWaitTime = new JPanel(new FlowLayout(FlowLayout.LEFT));

    private JLabel jlblMaxMessageTime = new JLabel(
                            Resources.getString("maxMessageTime"));
    private JSpinner jsMaxMessageTime = new JSpinner(new SpinnerNumberModel(
                                                new Integer(10000),
                                                new Integer(0),
                                                null,
                                                new Integer(1000)));
    private JPanel jpMaxMessageTime = new JPanel(
                                    new FlowLayout(FlowLayout.LEFT));

    private JButton jbtnConfirm = new JButton(Resources.getString("confirm"));
    private JButton jbtnDefault = new JButton(Resources.getString("default"));
    private JPanel jpConfirmDefault = new JPanel();
    ConfigurationService config;
    public MailboxConfigurationForm()
    {

        super(new GridLayout(5,1));
        config = MailboxActivator.getConfigurationService();
        //get our outgoing file panel set up
        jtfOutgoingMessage.setText(Mailbox.getOutgoingMessageFileLocation()
                                    .toString());
        jpOutgoingMessage.add(jlblOutgoingMessage);
        jpOutgoingMessage.add(jtfOutgoingMessage);
        jpOutgoingMessage.add(jbtnOutgoingMessage);

        //get our incoming file panel set up
        jtfIncomingMessage.setText(Mailbox.getIncomingMessageDirectory()
                                    .toString());
        jfcIncomingMessage.setFileSelectionMode( 
                            JFileChooser.DIRECTORIES_ONLY);
        jpIncomingMessage.add(jlblIncomingMessage);
        jpIncomingMessage.add(jtfIncomingMessage);
        jpIncomingMessage.add(jbtnIncomingMessage);

        //get our wait time panel set up
        jsWaitTime.setValue(new Integer(Mailbox.getWaitTime()));
        jpWaitTime.add(jlblWaitTime);
        jpWaitTime.add(jsWaitTime);

        //get our max message time panel set up
        jsMaxMessageTime.setValue(new Integer(
                            Mailbox.getMaxMessageDuration()));
        jpMaxMessageTime.add(jlblMaxMessageTime);
        jpMaxMessageTime.add(jsMaxMessageTime);

        //get our buttons panel set up
        jpConfirmDefault.add(jbtnConfirm);
        jpConfirmDefault.add(jbtnDefault);

        //add all the sub-panels
        this.add(jpOutgoingMessage);
        this.add(jpIncomingMessage);
        this.add(jpWaitTime);
        this.add(jpMaxMessageTime);
        this.add(jpConfirmDefault);

        //add action listeners
        jbtnIncomingMessage.addActionListener(this);
        jbtnOutgoingMessage.addActionListener(this);
        jbtnConfirm.addActionListener(this);
        jbtnDefault.addActionListener(this);

        //a little resizing
        jtfIncomingMessage.setColumns(20);
        jtfOutgoingMessage.setColumns(20);
        ((JSpinner.DefaultEditor)jsWaitTime.getEditor())
                                    .getTextField().setColumns(6);
        ((JSpinner.DefaultEditor)jsMaxMessageTime.getEditor())
                                    .getTextField().setColumns(6);
    }

    public void actionPerformed(ActionEvent e)
    {

        if (e.getSource() == jbtnIncomingMessage)
        {
            jfcIncomingMessage.setCurrentDirectory(
                                Mailbox.getIncomingMessageDirectory());
            int returnVal = jfcIncomingMessage.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                File file = jfcIncomingMessage.getSelectedFile();
                jtfIncomingMessage.setText(file.toString());
            }
        }
        else if (e.getSource() == jbtnOutgoingMessage)
        {
            jfcOutgoingMessage.setCurrentDirectory(
                            Mailbox.getOutgoingMessageFileLocation());
            int returnVal = jfcOutgoingMessage.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                File file = jfcOutgoingMessage.getSelectedFile();
                jtfIncomingMessage.setText(file.toString());
            }

        }
        else if (e.getSource() == jbtnConfirm)
        {
            config.setProperty(Mailbox.MAX_MSG_DURATION_PROPERTY_NAME,
                                (Integer)jsMaxMessageTime.getValue());
            config.setProperty(Mailbox.WAIT_TIME_PROPERTY_NAME,
                                (Integer)jsWaitTime.getValue());
            config.setProperty(Mailbox.INCOMING_MESSAGE_PROPERTY_NAME,
                                                jtfIncomingMessage.getText());
            config.setProperty(Mailbox.OUTGOING_MESSAGE_PROPERTY_NAME,
                                                jtfOutgoingMessage.getText());
            JOptionPane.showMessageDialog(this, "Values Set!");
        }
        else if (e.getSource() == jbtnDefault)
        {
            int choice = JOptionPane.showConfirmDialog(this,
                "OK to reset all values to default?", 
                "Set Defaults?", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION)
            {
                //reset all values to null and then let Mailbox plug in the defaults
                config.setProperty(Mailbox.MAX_MSG_DURATION_PROPERTY_NAME,
                                    null);
                config.setProperty(Mailbox.WAIT_TIME_PROPERTY_NAME,
                                    null);
                config.setProperty(Mailbox.INCOMING_MESSAGE_PROPERTY_NAME,
                                    null);
                config.setProperty(Mailbox.OUTGOING_MESSAGE_PROPERTY_NAME,
                                    null);
                config.setProperty(Mailbox.MAX_MSG_DURATION_PROPERTY_NAME,
                                new Integer(Mailbox.getMaxMessageDuration()));
                config.setProperty(Mailbox.WAIT_TIME_PROPERTY_NAME,
                                new Integer(Mailbox.getWaitTime()));
                config.setProperty(Mailbox.INCOMING_MESSAGE_PROPERTY_NAME,
                                Mailbox.getIncomingMessageDirectory());
                config.setProperty(Mailbox.OUTGOING_MESSAGE_PROPERTY_NAME,
                                Mailbox.getOutgoingMessageFileLocation());

                //now reset all the GUI elements to the defaults
                jtfIncomingMessage.setText(Mailbox
                                    .getIncomingMessageDirectory()
                                    .toString());
                jtfOutgoingMessage.setText(Mailbox
                                    .getOutgoingMessageFileLocation()
                                    .toString());
                jsWaitTime.setValue(new Integer(Mailbox.getWaitTime()));
                jsMaxMessageTime.setValue(new Integer(
                            Mailbox.getMaxMessageDuration()));
            }
        }

    }

    /**
     * Implements the <tt>ConfigurationForm.getTitle()</tt> method. Returns the
     * title of this configuration form.
     */
    public String getTitle()
    {
        return Resources.getString("mailbox");
    }

    /**
     * Implements the <tt>ConfigurationForm.getIcon()</tt> method. Returns the
     * icon of this configuration form.
     */
    public byte[] getIcon()
    {
        return Resources.getImageInBytes("mailboxIcon");
    }

    /**
     * Implements the <tt>ConfigurationForm.getForm()</tt> method. Returns the
     * component corresponding to this configuration form.
     */
    public Object getForm()
    {
        return this;
    }


}