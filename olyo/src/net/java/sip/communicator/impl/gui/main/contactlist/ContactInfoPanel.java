/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package net.java.sip.communicator.impl.gui.main.contactlist;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import net.java.sip.communicator.impl.gui.customcontrols.*;
import net.java.sip.communicator.service.contactlist.*;

/**
 * The <tt>ContactInfoPanel</tt> is a popup dialog containing the contact
 * detailed info.
 * 
 * @author Yana Stamcheva
 */
public class ContactInfoPanel
    extends JDialog
    implements WindowFocusListener
{

    private JPanel protocolsPanel = new JPanel(new GridLayout(0, 1));

    private MetaContact contactItem;

    private TransparentBackground bg;

    /**
     * Creates an instance of the <tt>ContactInfoPanel</tt>.
     * 
     * @param owner The frame owner of this dialog.
     * @param contactItem The <tt>MetaContact</tt> for the info.
     */
    public ContactInfoPanel(Frame owner, MetaContact contactItem)
    {
        super(owner);

        this.contactItem = contactItem;

        this.setUndecorated(true);

        this.setModal(true);

        this.protocolsPanel.setOpaque(false);

        // Create the transparent background component
        this.bg = new TransparentBackground(this);

        this.bg.setLayout(new BorderLayout());

        this.bg.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        this.getContentPane().setLayout(new BorderLayout());

        this.init();

        this.getContentPane().add(bg, BorderLayout.CENTER);

        this.pack();

        this.setSize(140, 50);

        this.addWindowFocusListener(this);
    }

    /**
     * Initializes the <tt>ContactInfoPanel</tt>.
     */
    private void init()
    {
        /*
         * String[] protocolList = this.contactItem.getC();
         * 
         * if(protocolsPanel.getComponentCount() == 0){ for(int i = 0; i <
         * protocolList.length; i ++){
         * 
         * JLabel protocolLabel = new JLabel(protocolList[i], new
         * ImageIcon(Constants.getProtocolIcon(protocolList[i])), JLabel.LEFT);
         * 
         * this.protocolsPanel.add(protocolLabel); } }
         * 
         * this.bg.add(protocolsPanel, BorderLayout.CENTER);
         */
    }

    /**
     * Returns the panel containing all contact protocols' information.
     * 
     * @return the panel containing all contact protocols' information.
     */
    public JPanel getProtocolsPanel()
    {
        return protocolsPanel;
    }

    public void windowGainedFocus(WindowEvent e)
    {

    }

    public void windowLostFocus(WindowEvent e)
    {
        this.setVisible(false);
        this.dispose();
    }

    public void setPopupLocation(int x, int y)
    {
        this.setLocation(x, y);

        this.bg.updateBackground(x, y);
    }
}
