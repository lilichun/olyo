/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.gui.main.chat.conference;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import net.java.sip.communicator.impl.gui.*;
import net.java.sip.communicator.impl.gui.i18n.*;
import net.java.sip.communicator.impl.gui.main.chat.*;
import net.java.sip.communicator.impl.gui.utils.*;
import net.java.sip.communicator.service.msghistory.*;
import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.service.protocol.event.*;
import net.java.sip.communicator.util.*;

/**
 * The <tt>ConferenceChatPanel</tt> is the chat panel corresponding to a
 * multi user chat.
 *
 * @author Yana Stamcheva
 */
public class ConferenceChatPanel
    extends ChatPanel
    implements  ChatRoomPropertyChangeListener,
                ChatRoomMemberPresenceListener
{
    private ChatRoomSubjectPanel subjectPanel;
    
    private Logger logger = Logger.getLogger(ConferenceChatPanel.class);

    private Date firstHistoryMsgTimestamp;

    private Date lastHistoryMsgTimestamp;

    private ChatRoomWrapper chatRoomWrapper;

    /**
     * Creates an instance of <tt>ConferenceChatPanel</tt>.
     *
     * @param chatWindow the <tt>ChatWindow</tt> that contains this chat panel
     * @param chatRoomWrapper the <tt>ChatRoomWrapper</tt> object, which
     * corresponds to a chat room
     */
    public ConferenceChatPanel(ChatWindow chatWindow,
        ChatRoomWrapper chatRoomWrapper)
    {
        super(chatWindow);

        this.chatRoomWrapper = chatRoomWrapper;

        subjectPanel = new ChatRoomSubjectPanel(chatWindow, chatRoomWrapper);

        // The subject panel is added here, because it's specific for the
        // multi user chat and is not contained in the single chat chat panel.
        this.add(subjectPanel, BorderLayout.NORTH);

        ChatRoom chatRoom = chatRoomWrapper.getChatRoom();

        if(chatRoom != null && chatRoom.isJoined())
        {
            this.loadChatRoom(chatRoom);
        }
    }

    /**
     * Implements the <tt>ChatPanel.getChatName</tt> method.
     *
     * @return the name of the chat room.
     */
    public String getChatName()
    {
        return chatRoomWrapper.getChatRoomName();
    }

    /**
     * Implements the <tt>ChatPanel.getChatIdentifier</tt> method.
     *
     * @return the <tt>ChatRoom</tt>
     */
    public Object getChatIdentifier()
    {
        return chatRoomWrapper;
    }

    /**
     * Implements the <tt>ChatPanel.getChatStatusIcon</tt> method.
     *
     * @return the status icon corresponding to this chat room
     */
    public ImageIcon getChatStatusIcon()
    {
        String status = Constants.OFFLINE_STATUS;

        if(chatRoomWrapper.getChatRoom() != null
            && chatRoomWrapper.getChatRoom().isJoined())
            status = Constants.ONLINE_STATUS;

        return new ImageIcon(Constants.getStatusIcon(status));
    }

    /**
     * Implements the <tt>ChatPanel.loadHistory</tt> method.
     * <br>
     * Loads the history for this chat room.
     */
    public void loadHistory()
    {
        if (chatRoomWrapper.getChatRoom() == null)
            return;

        final MessageHistoryService msgHistory
            = GuiActivator.getMsgHistoryService();

        if (msgHistory == null)
            return;

        new Thread() {
            public void run()
            {
                // Load the history period, which initializes the
                // firstMessageTimestamp and the lastMessageTimeStamp variables.
                // Used to disable/enable history flash buttons in the chat
                // window tool bar.
                loadHistoryPeriod(chatRoomWrapper.getChatRoom());

                // Load the last N=CHAT_HISTORY_SIZE messages from history.
                Collection historyList
                    = msgHistory.findLast(  chatRoomWrapper.getChatRoom(),
                                            Constants.CHAT_HISTORY_SIZE);

                if(historyList.size() > 0) {
                    class ProcessHistory implements Runnable
                    {
                        Collection historyList;
                        
                        ProcessHistory(Collection historyList)
                        {
                            this.historyList = historyList;
                        }
                        
                        public void run()
                        {
                            processHistory(historyList, null);
                        }
                    }
                    SwingUtilities.invokeLater(new ProcessHistory(historyList));
                }
            }
        }.start();
    }

    /**
     * Implements the <tt>ChatPanel.loadHistory(escapedMessageID)</tt> method.
     * <br>
     * Loads the history for this chat room and escapes the last message
     * received.
     */
    public void loadHistory(String escapedMessageID)
    {
        if (chatRoomWrapper.getChatRoom() == null)
            return;

        MessageHistoryService msgHistory
        = GuiActivator.getMsgHistoryService();

        if (msgHistory == null)
            return;

        Collection historyList
            = msgHistory.findLast(  chatRoomWrapper.getChatRoom(),
                                    Constants.CHAT_HISTORY_SIZE);

        processHistory(historyList, escapedMessageID);
    }

    /**
     * Implements the <tt>ChatPanel.loadPreviousFromHistory</tt> method.
     * <br>
     * Loads the previous "page" in the history.
     */
    public void loadPreviousPageFromHistory()
    {
        if (chatRoomWrapper.getChatRoom() == null)
            return;

        new Thread() {
            public void run()
            {
                MessageHistoryService msgHistory
                    = GuiActivator.getMsgHistoryService();
                
                ChatConversationPanel conversationPanel
                    = getChatConversationPanel();
                
                Date firstMsgDate
                    = conversationPanel.getPageFirstMsgTimestamp();
                
                Collection c = null;
                
                if(firstMsgDate != null)
                {
                    c = msgHistory.findLastMessagesBefore(
                        chatRoomWrapper.getChatRoom(),
                        firstMsgDate,
                        MESSAGES_PER_PAGE);
                }
                 
                if(c !=null && c.size() > 0)
                {   
                    SwingUtilities.invokeLater(
                            new HistoryMessagesLoader(c));
                }
            }   
        }.start();
    }

    /**
     * Implements the <tt>ChatPanel.loadNextFromHistory</tt> method.
     * <br>
     * Loads the next "page" in the history.
     */
    public void loadNextPageFromHistory()
    {
        if (chatRoomWrapper.getChatRoom() == null)
            return;

        new Thread()
        {
            public void run()
            {
                MessageHistoryService msgHistory
                    = GuiActivator.getMsgHistoryService();

                Date lastMsgDate
                    = getChatConversationPanel().getPageLastMsgTimestamp();

                Collection c = null;
                if(lastMsgDate != null)
                {
                    c = msgHistory.findFirstMessagesAfter(
                        chatRoomWrapper.getChatRoom(),
                        lastMsgDate,
                        MESSAGES_PER_PAGE);
                }

                if(c != null && c.size() > 0)
                    SwingUtilities.invokeLater(
                            new HistoryMessagesLoader(c));
            }
        }.start();
    }

    /**
     * Implements the <tt>ChatPanel.sendMessage</tt> method.
     * <br>
     * Sends a message to the chat room.
     */
    protected void sendMessage(String text)
    {   
        ChatRoom chatRoom = chatRoomWrapper.getChatRoom();
        
        Message msg = chatRoom.createMessage(text);
        
        try
        {   
            chatRoom.sendMessage(msg);
        }
        catch (IllegalStateException ex)
        {
            logger.error("Failed to send message.", ex);
            
            this.refreshWriteArea();
    
            this.processMessage(
                    chatRoom.getName(),
                    new Date(System.currentTimeMillis()),
                    Constants.OUTGOING_MESSAGE,
                    msg.getContent(),
                    msg.getContentType());
    
            this.processMessage(
                    chatRoom.getName(),
                    new Date(System.currentTimeMillis()),
                    Constants.ERROR_MESSAGE,
                    Messages.getI18NString("msgSendConnectionProblem")
                    .getText(), "text");
        }
        catch (Exception ex)
        {
            logger.error("Failed to send message.", ex);
            
            this.refreshWriteArea();
    
            this.processMessage(
                    chatRoom.getName(),
                    new Date(System.currentTimeMillis()),
                    Constants.OUTGOING_MESSAGE,
                    msg.getContent(),
                    msg.getContentType());
    
            this.processMessage(
                    chatRoom.getName(),
                    new Date(System.currentTimeMillis()),
                    Constants.ERROR_MESSAGE,
                    Messages.getI18NString("msgDeliveryInternalError")
                    .getText(), "text");
        }
    }

    /**
     * Implements the <tt>ChatPanel.treatReceivedMessage</tt> method.
     * <br>
     * Treats a received message from the given contact.
     */
    public void treatReceivedMessage(Contact sourceContact)
    {
    }

    /**
     * Implements the <tt>ChatPanel.sendTypingNotification</tt> method.
     * <br>
     * Sends a typing notification.
     */
    public int sendTypingNotification(int typingState)
    {
        return 0;
    }

    /**
     * Implements the <tt>ChatPanel.getFirstHistoryMsgTimestamp</tt> method.
     */
    public Date getFirstHistoryMsgTimestamp()
    {
        return firstHistoryMsgTimestamp;
    }

    /**
     * Implements the <tt>ChatPanel.getLastHistoryMsgTimestamp</tt> method.
     */
    public Date getLastHistoryMsgTimestamp()
    {
        return lastHistoryMsgTimestamp;
    }

    public void chatRoomChanged(ChatRoomPropertyChangeEvent event)
    {
    }

    /**
     * Invoked when <tt>ChatRoomMemberPresenceChangeEvent</tt> are received.
     * When a new <tt>ChatRoomMember</tt> has joined the chat adds it to the
     * list of chat participants on the right of the chat window. When a
     * <tt>ChatRoomMember</tt> has left or quit, or has being kicked it's
     * removed from the chat window.
     */
    public void memberPresenceChanged(ChatRoomMemberPresenceChangeEvent evt)
    {
        ChatRoom sourceChatRoom = (ChatRoom) evt.getSource();

        if(!sourceChatRoom.equals(chatRoomWrapper.getChatRoom()))
            return;
        
        String eventType = evt.getEventType();
        ChatRoomMember chatRoomMember = evt.getChatRoomMember();
        
        String statusMessage = null;
        
        if (eventType.equals(ChatRoomMemberPresenceChangeEvent.MEMBER_JOINED))
        {
            getChatContactListPanel()
                .addContact(new ChatContact(chatRoomMember));
            
            statusMessage = Messages.getI18NString("chatRoomUserJoined",
                new String[] {sourceChatRoom.getName()}).getText();
        }
        else if (eventType.equals(ChatRoomMemberPresenceChangeEvent.MEMBER_LEFT)
            || eventType.equals(ChatRoomMemberPresenceChangeEvent.MEMBER_KICKED)
            || eventType.equals(ChatRoomMemberPresenceChangeEvent.MEMBER_QUIT))
        {
            Iterator chatContacts
                = getChatContactListPanel().getChatContacts();
            
            while(chatContacts.hasNext())
            {
                ChatContact chatContact
                    = (ChatContact) chatContacts.next();
                
                if(chatContact.getSourceContact().equals(chatRoomMember))
                {
                    getChatContactListPanel().removeContact(chatContact);
                    break;
                }
            }
            
            if(eventType.equals(ChatRoomMemberPresenceChangeEvent.MEMBER_LEFT))
            {
                statusMessage = Messages.getI18NString("chatRoomUserLeft",
                    new String[] {sourceChatRoom.getName()}).getText();
            }   
            else if(eventType.equals(
                ChatRoomMemberPresenceChangeEvent.MEMBER_KICKED))
            {
                statusMessage = Messages.getI18NString("chatRoomUserKicked",
                    new String[] {sourceChatRoom.getName()}).getText();
            }
            else if(eventType.equals(
                ChatRoomMemberPresenceChangeEvent.MEMBER_QUIT))
            {
                statusMessage = Messages.getI18NString("chatRoomUserQuit",
                    new String[] {sourceChatRoom.getName()}).getText();
            }
        }
        
        this.processMessage(
            chatRoomMember.getName(),
            new Date(System.currentTimeMillis()),
            Constants.STATUS_MESSAGE,
            statusMessage,
            ChatConversationPanel.TEXT_CONTENT_TYPE);
    }
    
    /**
     * Loads the given chat room in the this chat conference panel. Loads all
     * members and adds all corresponding listeners.
     * 
     * @param chatRoom the <tt>ChatRoom</tt> to load
     */
    public void loadChatRoom(ChatRoom chatRoom)
    {
        List membersList = chatRoom.getMembers();

        for (int i = 0; i < membersList.size(); i ++)
        {
            ChatContact chatContact
                = new ChatContact((ChatRoomMember)membersList.get(i));

            getChatContactListPanel()
                .addContact(chatContact);
        }

        chatRoom.addPropertyChangeListener(this);
        chatRoom.addMemberPresenceListener(this);

        // Load the subject of the chat room.
        subjectPanel.setSubject(chatRoom.getSubject());
    }

    /**
     * Invites the given contact in this chat conferenece.
     * 
     * @param contactAddress the address of the contact to invite
     * @param reason the reason for the invitation
     */
    public void inviteChatContact(String contactAddress, String reason)
    {
        ChatRoom chatRoom = chatRoomWrapper.getChatRoom();
        
        if(chatRoom != null)
            chatRoom.invite(contactAddress, reason);
    }


    /**
     * Updates the chat panel when a property of the chat room has been modified.
     * 
     * @param evt the event containing information about the property change
     */
    public void chatRoomPropertyChanged(ChatRoomPropertyChangeEvent evt)
    {
        if(evt.getPropertyName().equals(
            ChatRoomPropertyChangeEvent.CHAT_ROOM_SUBJECT))
        {
            subjectPanel.setSubject((String) evt.getNewValue());

            this.processMessage(
                evt.getSourceChatRoom().getName(),
                new Date(System.currentTimeMillis()),
                Constants.STATUS_MESSAGE,
                Messages.getI18NString("chatRoomSubjectChanged",
                    new String []{evt.getSourceChatRoom().getName(),
                    evt.getNewValue().toString()}).getText(),
                ChatConversationPanel.TEXT_CONTENT_TYPE);
        }
    }

    /**
     * 
     */
    public void chatRoomPropertyChangeFailed(
        ChatRoomPropertyChangeFailedEvent evt)
    {
        
    }

    /**
     * Updates the status of this chat room.
     * 
     * @param status the new status of the chat room
     */
    public void updateChatRoomStatus(String status)
    {
        if(Constants.TABBED_CHAT_WINDOW)
        {
            if (getChatWindow().getChatTabCount() > 0) {
                getChatWindow().setTabIcon(this,
                    new ImageIcon(Constants.getStatusIcon(status)));
            }
        }
    }

    /**
     * Loads history period dates for the current chat.
     */
    private void loadHistoryPeriod(ChatRoom chatRoom)
    {
        MessageHistoryService msgHistory
            = GuiActivator.getMsgHistoryService();

        Collection firstMessage = msgHistory
            .findFirstMessagesAfter(chatRoom, new Date(0), 1);

        if(firstMessage.size() > 0)
        {
            Iterator i = firstMessage.iterator();

            Object o = i.next();

            if(o instanceof MessageDeliveredEvent)
            {
                MessageDeliveredEvent evt
                    = (MessageDeliveredEvent)o;

                this.firstHistoryMsgTimestamp = evt.getTimestamp();
            }
            else if(o instanceof MessageReceivedEvent)
            {
                MessageReceivedEvent evt = (MessageReceivedEvent)o;

                this.firstHistoryMsgTimestamp = evt.getTimestamp();
            }
        }

        Collection lastMessage = msgHistory
            .findLastMessagesBefore(chatRoom, new Date(Long.MAX_VALUE), 1);

        if(lastMessage.size() > 0)
        {
            Iterator i1 = lastMessage.iterator();
            
            Object o1 = i1.next();

            if(o1 instanceof MessageDeliveredEvent)
            {
                MessageDeliveredEvent evt
                    = (MessageDeliveredEvent)o1;

                this.lastHistoryMsgTimestamp = evt.getTimestamp();
            }
            else if(o1 instanceof MessageReceivedEvent)
            {
                MessageReceivedEvent evt = (MessageReceivedEvent)o1;

                this.lastHistoryMsgTimestamp = evt.getTimestamp();
            }
        }
    }

    /**
     * From a given collection of messages shows the history in the chat window.
     */
    private class HistoryMessagesLoader implements Runnable
    {
        private Collection msgHistory;

        public HistoryMessagesLoader(Collection msgHistory)
        {
            this.msgHistory = msgHistory;
        }

        public void run()
        {
            getChatConversationPanel().clear();

            processHistory(msgHistory, "");
            
            getChatConversationPanel().setDefaultContent();
        }
    }
}
