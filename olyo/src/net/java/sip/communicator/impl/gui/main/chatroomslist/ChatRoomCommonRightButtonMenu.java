/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package net.java.sip.communicator.impl.gui.main.chatroomslist;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import net.java.sip.communicator.impl.gui.*;
import net.java.sip.communicator.impl.gui.event.*;
import net.java.sip.communicator.impl.gui.i18n.*;
import net.java.sip.communicator.impl.gui.main.*;
import net.java.sip.communicator.impl.gui.main.chatroomslist.createforms.*;
import net.java.sip.communicator.impl.gui.main.chatroomslist.joinforms.*;
import net.java.sip.communicator.impl.gui.utils.*;
import net.java.sip.communicator.service.gui.*;
import net.java.sip.communicator.service.gui.Container;
import net.java.sip.communicator.service.protocol.*;

/**
 * The <tt>ChatRoomsListRightButtonMenu</tt> is the menu, opened when user clicks
 * with the right mouse button on the chat rooms list panel. It's the one that
 * contains the create chat room item.
 *
 * @author Yana Stamcheva
 */
public class ChatRoomCommonRightButtonMenu
    extends JPopupMenu
    implements  ActionListener,
                PluginComponentListener
{
    private I18NString createChatRoomString
        = Messages.getI18NString("createChatRoom");
    
    private I18NString searchForChatRoomsString
        = Messages.getI18NString("joinChatRoom");
    
    private JMenuItem createChatRoomItem = new JMenuItem(
        createChatRoomString.getText(),
        new ImageIcon(ImageLoader.getImage(ImageLoader.CHAT_ROOM_16x16_ICON)));

    private JMenuItem searchForChatRoomsItem = new JMenuItem(
        searchForChatRoomsString.getText(),
        new ImageIcon(ImageLoader.getImage(ImageLoader.SEARCH_ICON_16x16)));

    private MainFrame mainFrame;

    private ProtocolProviderService protocolProvider;
    /**
     * Creates an instance of <tt>ChatRoomsListRightButtonMenu</tt>.
     */
    public ChatRoomCommonRightButtonMenu(MainFrame mainFrame,
        ProtocolProviderService pps)
    {
        super();

        this.mainFrame = mainFrame;
       
        this.protocolProvider = pps;
        
        this.setLocation(getLocation());

        this.init();
    }

    /**
     * Initializes the menu, by adding all containing menu items.
     */
    private void init()
    {
        this.add(createChatRoomItem);
        this.add(searchForChatRoomsItem);
        
        this.initPluginComponents();

        this.createChatRoomItem.setName("createChatRoom");
        this.searchForChatRoomsItem.setName("searchForChatRooms");
        
        this.createChatRoomItem
            .setMnemonic(createChatRoomString.getMnemonic());
        this.searchForChatRoomsItem
            .setMnemonic(searchForChatRoomsString.getMnemonic());
        
        this.createChatRoomItem.addActionListener(this);
        this.searchForChatRoomsItem.addActionListener(this);
    }
    
    /**
     * Adds all already registered plugin components to this menu.
     */
    private void initPluginComponents()
    {
        Iterator pluginComponents = GuiActivator.getUIService()
            .getComponentsForContainer(
                Container.CONTAINER_CONTACT_RIGHT_BUTTON_MENU);

        if(pluginComponents.hasNext())
            this.addSeparator();

        while (pluginComponents.hasNext())
        {
            Component o = (Component)pluginComponents.next();

            this.add(o);
        }
    }
    
    /**
     * Handles the <tt>ActionEvent</tt>. Determines which menu item was
     * selected and makes the appropriate operations.
     */
    public void actionPerformed(ActionEvent e){

        JMenuItem menuItem = (JMenuItem) e.getSource();
        String itemName = menuItem.getName();

        if (itemName.equals("createChatRoom"))
        {
            AddChatRoomDialog addChatRoomDialog
                = new AddChatRoomDialog(mainFrame, protocolProvider);

            addChatRoomDialog.setVisible(true);
        }
        else if (itemName.equals("searchForChatRooms"))
        {
            JoinChatRoomDialog joinChatRoomDialog
                = new JoinChatRoomDialog(mainFrame, protocolProvider);

            joinChatRoomDialog.showDialog();
        }
    }   
    
    /**
     * Implements the <tt>PluginComponentListener.pluginComponentAdded</tt>
     * method, in order to add the given plugin component in this container.
     */
    public void pluginComponentAdded(PluginComponentEvent event)
    {
        PluginComponent c = event.getPluginComponent();

        this.add((Component) c.getComponent());

        this.repaint();
    }

    /**
     * Implements the <tt>PluginComponentListener.pluginComponentRemoved</tt>
     * method, in order to remove the given component from this container.
     */
    public void pluginComponentRemoved(PluginComponentEvent event)
    {
        PluginComponent c = event.getPluginComponent();

        this.remove((Component) c.getComponent());
    }
}
