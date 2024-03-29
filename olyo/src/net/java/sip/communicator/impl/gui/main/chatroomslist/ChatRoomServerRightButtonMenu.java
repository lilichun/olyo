/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package net.java.sip.communicator.impl.gui.main.chatroomslist;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import net.java.sip.communicator.impl.gui.i18n.*;
import net.java.sip.communicator.impl.gui.main.*;
import net.java.sip.communicator.impl.gui.main.chat.conference.*;
import net.java.sip.communicator.impl.gui.main.chatroomslist.chatroomwizard.*;
import net.java.sip.communicator.impl.gui.main.chatroomslist.joinforms.*;
import net.java.sip.communicator.impl.gui.utils.*;
import net.java.sip.communicator.service.protocol.*;

/**
 * The <tt>ChatRoomsListRightButtonMenu</tt> is the menu, opened when user clicks
 * with the right mouse button on the chat rooms list panel. It's the one that
 * contains the create chat room item.
 *
 * @author Yana Stamcheva
 */
public class ChatRoomServerRightButtonMenu
    extends JPopupMenu
    implements  ActionListener
{
    private I18NString createChatRoomString
        = Messages.getI18NString("createChatRoom");
    
    private I18NString joinChannelString
        = Messages.getI18NString("joinChatRoom");
    
    private JMenuItem createChatRoomItem = new JMenuItem(
        createChatRoomString.getText(),
        new ImageIcon(ImageLoader.getImage(ImageLoader.CHAT_ROOM_16x16_ICON)));

    private JMenuItem joinChannelItem = new JMenuItem(
        joinChannelString.getText(),
        new ImageIcon(ImageLoader.getImage(ImageLoader.SEARCH_ICON_16x16)));

    private MainFrame mainFrame;
    
    private ProtocolProviderService protocolProvider;
        
    /**
     * Creates an instance of <tt>ChatRoomsListRightButtonMenu</tt>.
     * 
     * @param mainFrame the main application window
     * @param protocolProvider the protocol provider corresponding to the multi
     * user chat server
     */
    public ChatRoomServerRightButtonMenu(
        MainFrame mainFrame,
        ProtocolProviderService protocolProvider)
    {
        super();

        this.mainFrame = mainFrame;
     
        this.protocolProvider = protocolProvider;
        
        this.setLocation(getLocation());

        this.init();
    }

    /**
     * Initializes the menu, by adding all containing menu items.
     */
    private void init()
    {
        this.add(createChatRoomItem);
        this.add(joinChannelItem);
        
        this.createChatRoomItem.setName("createChatRoom");
        this.joinChannelItem.setName("joinChatRoom");
        
        this.createChatRoomItem
            .setMnemonic(createChatRoomString.getMnemonic());
        this.joinChannelItem
            .setMnemonic(joinChannelString.getMnemonic());
        
        this.createChatRoomItem.addActionListener(this);
        this.joinChannelItem.addActionListener(this);
    }
    
    /**
     * Handles the <tt>ActionEvent</tt>. Determines which menu item was
     * selected and makes the appropriate operations.
     */
    public void actionPerformed(ActionEvent e)
    {
        JMenuItem menuItem = (JMenuItem) e.getSource();
        String itemName = menuItem.getName();

        if (itemName.equals("createChatRoom"))
        {
            CreateChatRoomDialog createChatRoomDialog
                = new CreateChatRoomDialog(mainFrame, protocolProvider);
            
            createChatRoomDialog.setVisible(true);
        }
        else if (itemName.equals("joinChatRoom"))
        {
            JoinChatRoomDialog joinChannelDialog
                = new JoinChatRoomDialog(mainFrame, protocolProvider);
            
            joinChannelDialog.pack();
            joinChannelDialog.setLocation(
                Toolkit.getDefaultToolkit().getScreenSize().width/2
                    - joinChannelDialog.getWidth()/2,
                Toolkit.getDefaultToolkit().getScreenSize().height/2
                    - joinChannelDialog.getHeight()/2
                );
            joinChannelDialog.showDialog();
        }
    }
}
