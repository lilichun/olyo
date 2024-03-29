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
import java.util.List;

import javax.swing.*;

import net.java.sip.communicator.impl.gui.*;
import net.java.sip.communicator.impl.gui.main.*;
import net.java.sip.communicator.impl.gui.main.chat.*;
import net.java.sip.communicator.impl.gui.main.chat.conference.*;
import net.java.sip.communicator.impl.gui.utils.*;
import net.java.sip.communicator.service.configuration.*;
import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.util.*;

/**
 * The <tt>ChatRoomsList</tt> is the list containing all chat rooms.
 *
 * @author Yana Stamcheva
 */
public class ChatRoomsList
    extends JList
    implements MouseListener
{
    private Logger logger = Logger.getLogger(ChatRoomsList.class);

    private MainFrame mainFrame;

    private DefaultListModel listModel = new DefaultListModel();

    private ChatWindowManager chatWindowManager;
    
    /**
     * Creates an instance of the <tt>ChatRoomsList</tt>.
     *
     * @param mainFrame The main application window.
     */
    public ChatRoomsList(MainFrame mainFrame)
    {
        this.mainFrame = mainFrame;
        
        this.chatWindowManager = mainFrame.getChatWindowManager();

        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        this.setOpaque(false);

        this.setModel(listModel);
        this.setCellRenderer(new ChatRoomsListCellRenderer());

        this.addMouseListener(this);
    }

    /**
     * Adds a chat server and all its existing chat rooms.
     *
     * @param pps the <tt>ProtocolProviderService</tt> corresponding to the chat
     * server
     * @param multiUserChatOperationSet the <tt>OperationSetMultiUserChat</tt>
     * from which we manage chat rooms
     */
    public void addChatServer(ProtocolProviderService pps,
        OperationSetMultiUserChat multiUserChatOperationSet)
    {
        MultiUserChatServerWrapper serverWrapper
            = new MultiUserChatServerWrapper(pps);

        listModel.addElement(serverWrapper);

        ConfigurationService configService
            = GuiActivator.getConfigurationService();

        String prefix = "net.java.sip.communicator.impl.gui.accounts";

        List accounts = configService
                .getPropertyNamesByPrefix(prefix, true);

        Iterator accountsIter = accounts.iterator();

        while(accountsIter.hasNext()) {
            String accountRootPropName
                = (String) accountsIter.next();

            String accountUID
                = configService.getString(accountRootPropName);

            if(accountUID.equals(pps
                    .getAccountID().getAccountUniqueID()))
            {   
                List chatRooms = configService
                    .getPropertyNamesByPrefix(
                        accountRootPropName + ".chatRooms", true);

                Iterator chatRoomsIter = chatRooms.iterator();
                
                while(chatRoomsIter.hasNext())
                {
                    String chatRoomPropName
                        = (String) chatRoomsIter.next();
                    
                    String chatRoomID
                        = configService.getString(chatRoomPropName);
                 
                    String chatRoomName = configService.getString(
                        chatRoomPropName + ".chatRoomName");

                    ChatRoomWrapper chatRoomWrapper
                        = new ChatRoomWrapper(pps, chatRoomID, chatRoomName);
                    
                    this.addChatRoom(chatRoomWrapper);
                }
            }
        }
    }

    /**
     * Removes the corresponding server and all related chat rooms from this
     * list.
     * 
     * @param pps the <tt>ProtocolProviderService</tt> corresponding to the
     * server to remove
     */
    public void removeChatServer (ProtocolProviderService pps)
    {
        MultiUserChatServerWrapper serverWrapper
            = findServerWrapperFromProvider(pps);

        int serverIndex = listModel.indexOf(serverWrapper);

        if (serverIndex == -1)
            return;

        for (int i = serverIndex + 1; i < listModel.getSize(); i ++)
        {
            Object o = listModel.get(i);

            if (o instanceof MultiUserChatServerWrapper)
            {
                listModel.removeRange(serverIndex, i - 1);
                break;
            }

            if (i == listModel.getSize() - 1)
                listModel.removeRange(serverIndex, i);
        }

        ConfigurationService configService
            = GuiActivator.getConfigurationService();

        String prefix = "net.java.sip.communicator.impl.gui.accounts";

        List accounts = configService
                .getPropertyNamesByPrefix(prefix, true);

        Iterator accountsIter = accounts.iterator();

        while(accountsIter.hasNext())
        {
            String accountRootPropName
                = (String) accountsIter.next();

            String accountUID
                = configService.getString(accountRootPropName);

            if(accountUID.equals(pps
                    .getAccountID().getAccountUniqueID()))
            {
                List chatRooms = configService
                    .getPropertyNamesByPrefix(
                        accountRootPropName + ".chatRooms", true);

                Iterator chatRoomsIter = chatRooms.iterator();

                while(chatRoomsIter.hasNext())
                {
                    String chatRoomPropName
                        = (String) chatRoomsIter.next();

                    configService.setProperty(
                        chatRoomPropName + ".chatRoomName",
                        null);
                }

                configService.setProperty(accountRootPropName, null);
            }
        }
    }

    /**
     * Adds a chat room to this list.
     *
     * @param chatRoomWrapper the <tt>ChatRoom</tt> to add
     */
    public void addChatRoom(ChatRoomWrapper chatRoomWrapper)
    {
        MultiUserChatServerWrapper serverWrapper
            = findServerWrapperFromProvider(chatRoomWrapper.getParentProvider());

        int parentIndex = listModel.indexOf(serverWrapper);

        boolean inserted = false;

        if(parentIndex != -1)
        {
            for(int i = parentIndex + 1; i < listModel.getSize(); i ++)
            {
                Object element = listModel.get(i);
                
                if(element instanceof MultiUserChatServerWrapper)
                {
                    listModel.insertElementAt(chatRoomWrapper, i);

                    // Indicate that we have found the last chat room in the
                    // list of server children and we have inserted there the
                    // new chat room.
                    inserted = true;

                    break;
                }
            }

            if(!inserted)
                listModel.addElement(chatRoomWrapper);
        }

        ConfigurationManager.updateChatRoom(
            chatRoomWrapper.getParentProvider(),
            chatRoomWrapper.getChatRoomID(),
            chatRoomWrapper.getChatRoomID(),
            chatRoomWrapper.getChatRoomName());

        this.refresh();
    }
    
    /**
     * Removes the given <tt>ChatRoom</tt> from the list of all chat rooms.
     * 
     * @param chatRoomWrapper the <tt>ChatRoomWrapper</tt> to remove
     */
    public void removeChatRoom(ChatRoomWrapper chatRoomWrapper)
    {
        listModel.removeElement(chatRoomWrapper);

        ConfigurationManager.updateChatRoom(
            chatRoomWrapper.getParentProvider(),
            chatRoomWrapper.getChatRoomID(),
            null,   // The new identifier.
            null);   // The name of the chat room.
    }

    /**
     * Returns the <tt>ChatRoomWrapper</tt> that correspond to the given
     * <tt>ChatRoom</tt>. If the list of chat rooms doesn't contain a
     * corresponding wrapper - returns null.
     *  
     * @param chatRoom the <tt>ChatRoom</tt> that we're looking for
     * @return the <tt>ChatRoomWrapper</tt> object corresponding to the given
     * <tt>ChatRoom</tt>
     */
    public ChatRoomWrapper findChatRoomWrapperFromChatRoom(ChatRoom chatRoom)
    {   
        for (int i = 0; i < listModel.getSize(); i ++)
        {   
            Object listItem = listModel.get(i);
            
            if (listItem instanceof ChatRoomWrapper)
            {
                ChatRoomWrapper chatRoomWrapper = (ChatRoomWrapper) listItem;
            
                if (chatRoom.equals(chatRoomWrapper.getChatRoom()))
                {
                    return chatRoomWrapper;
                }
            }
            else if (listItem instanceof MultiUserChatServerWrapper)
            {
                MultiUserChatServerWrapper serverWrapper
                    = (MultiUserChatServerWrapper) listItem;

                if (chatRoom.equals(
                    serverWrapper.getSystemRoomWrapper().getChatRoom()))
                {
                    return serverWrapper.getSystemRoomWrapper();
                }
            }
        }
        
        return null;
    }

    /**
     * Returns the <tt>MultiUserChatServerWrapper</tt> that correspond to the
     * given <tt>ProtocolProviderService</tt>. If the list doesn't contain a
     * corresponding wrapper - returns null.
     *  
     * @param protocolProvider the protocol provider that we're looking for
     * @return the <tt>MultiUserChatServerWrapper</tt> object corresponding to
     * the given <tt>ProtocolProviderService</tt>
     */
    public MultiUserChatServerWrapper findServerWrapperFromProvider(
        ProtocolProviderService protocolProvider)
    {
        for(int i = 0; i < listModel.getSize(); i ++)
        {
            Object listItem = listModel.get(i);

            if(listItem instanceof MultiUserChatServerWrapper)
            {
                MultiUserChatServerWrapper serverWrapper
                    = (MultiUserChatServerWrapper) listItem;

                if(protocolProvider.equals(serverWrapper.getProtocolProvider()))
                {
                    return serverWrapper;
                }
            }
        }

        return null;
    }

    /**
     * Determines if the chat server is closed.
     *
     * @param pps the protocol provider service that we'll be checking
     * @return true if the chat server is closed and false otherwise.
     */
    public boolean isChatServerClosed(ProtocolProviderService pps)
    {
        return false;
    }

    public void mouseClicked(MouseEvent e)
    {
        if (e.getClickCount() < 2)
            return;

        Object o = this.getSelectedValue();

        if(o instanceof MultiUserChatServerWrapper)
        {
            MultiUserChatServerWrapper serverWrapper
                = (MultiUserChatServerWrapper) o;

            ChatWindowManager chatWindowManager
                = mainFrame.getChatWindowManager();

            ConferenceChatPanel chatPanel
                = chatWindowManager.getMultiChat(
                    serverWrapper.getSystemRoomWrapper());

            chatWindowManager.openChat(chatPanel, true);
        }
        else if(o instanceof ChatRoomWrapper)
        {
            ChatRoomWrapper chatRoomWrapper = (ChatRoomWrapper) o;

            ChatWindowManager chatWindowManager
                = mainFrame.getChatWindowManager();

            ConferenceChatPanel chatPanel
                = chatWindowManager.getMultiChat(chatRoomWrapper);

            chatWindowManager.openChat(chatPanel, true);
        }
    }

    public void mouseEntered(MouseEvent e)
    {}

    public void mouseExited(MouseEvent e)
    {}
    
    public void mouseReleased(MouseEvent e)
    {}

    /**
     * A chat room was selected. Opens the chat room in the chat window.
     *
     * @param e the <tt>MouseEvent</tt> instance containing details of
     * the event that has just occurred.
     */
    public void mousePressed(MouseEvent e)
    {   
        //Select the object under the right button click.
        if ((e.getModifiers() & InputEvent.BUTTON2_MASK) != 0
            || (e.getModifiers() & InputEvent.BUTTON3_MASK) != 0
            || (e.isControlDown() && !e.isMetaDown()))
        {
            this.setSelectedIndex(locationToIndex(e.getPoint()));
        }

        Object o = this.getSelectedValue();

        if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
        {
            if(o instanceof MultiUserChatServerWrapper)
            {
                ChatRoomServerRightButtonMenu rightButtonMenu
                    = new ChatRoomServerRightButtonMenu(
                        mainFrame,
                        ((MultiUserChatServerWrapper) o).getProtocolProvider());

                rightButtonMenu.setInvoker(this);

                rightButtonMenu.setLocation(e.getX()
                        + mainFrame.getX() + 5, e.getY() + mainFrame.getY()
                        + 105);

                rightButtonMenu.setVisible(true);
            }
            else if (o instanceof ChatRoomWrapper)
            {
                ChatRoomRightButtonMenu rightButtonMenu
                    = new ChatRoomRightButtonMenu(mainFrame,
                        (ChatRoomWrapper) o);
                
                rightButtonMenu.setInvoker(this);
                
                rightButtonMenu.setLocation(e.getX()
                    + mainFrame.getX() + 5, e.getY() + mainFrame.getY()
                    + 105);

                rightButtonMenu.setVisible(true);
            }
        }
    }

    /**
     * Goes through the locally stored chat rooms list and for each
     * {@link ChatRoomWrapper} tries to find the corresponding server stored
     * {@link ChatRoom} in the specified operation set. Joins automatically all
     * found chat rooms.
     *
     * @param protocolProvider the protocol provider for the account to
     * synchronize
     * @param opSet the multi user chat operation set, which give us access to
     * chat room server 
     */
    public void synchronizeOpSetWithLocalContactList(
        ProtocolProviderService protocolProvider,
        final OperationSetMultiUserChat opSet)
    {
        MultiUserChatServerWrapper serverWrapper
            = findServerWrapperFromProvider(protocolProvider);

        int serverIndex = listModel.indexOf(serverWrapper);

        for(int i = serverIndex + 1; i < listModel.size(); i ++)
        {
            final Object o = listModel.get(i);

            if(!(o instanceof ChatRoomWrapper))
                break;

            new Thread()
            {
                public void run()
                {
                    ChatRoomWrapper chatRoomWrapper = (ChatRoomWrapper) o;
                    ChatRoom chatRoom = null;

                    try
                    {
                        chatRoom
                            = opSet.findRoom(chatRoomWrapper.getChatRoomName());
                    }
                    catch (OperationFailedException e1)
                    {
                        logger.error("Failed to find chat room with name:"
                            + chatRoomWrapper.getChatRoomName(), e1);
                    }
                    catch (OperationNotSupportedException e1)
                    {                        
                        logger.error("Failed to find chat room with name:"
                            + chatRoomWrapper.getChatRoomName(), e1);
                    }

                    if(chatRoom != null)
                    {
                        chatRoomWrapper.setChatRoom(chatRoom);

                        String lastChatRoomStatus
                            = ConfigurationManager.getChatRoomStatus(
                                chatRoomWrapper.getParentProvider(),
                                chatRoomWrapper.getChatRoomID());

                        if(lastChatRoomStatus == null
                            || lastChatRoomStatus.equals(
                                Constants.ONLINE_STATUS))
                        {
                            mainFrame.getMultiUserChatManager()
                                    .joinChatRoom(chatRoom);
                        }
                    }
                }
            }.start();
        }
    }

    /**
     * Refreshes the chat room's list. Meant to be invoked when a modification
     * in a chat room is made and the list should be refreshed in order to show
     * the new state correctly.
     */
    public void refresh()
    {
        this.revalidate();
        this.repaint();
    }
}
