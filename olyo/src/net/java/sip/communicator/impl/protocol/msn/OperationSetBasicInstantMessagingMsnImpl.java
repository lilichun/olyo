/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.protocol.msn;

import java.util.*;
import java.text.*;

import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.service.protocol.event.*;
import net.java.sip.communicator.service.protocol.msnconstants.*;
import net.java.sip.communicator.util.*;
import net.sf.jml.*;
import net.sf.jml.event.*;
import net.sf.jml.message.*;
import net.java.sip.communicator.impl.protocol.msn.mail.utils.*;

/**
 * A straightforward implementation of the basic instant messaging operation
 * set.
 *
 * @author Damian Minkov
 */
public class OperationSetBasicInstantMessagingMsnImpl
    implements OperationSetBasicInstantMessaging
{
    private static final Logger logger =
        Logger.getLogger(OperationSetBasicInstantMessagingMsnImpl.class);

    /**
     * A list of listeners registered for message events.
     */
    private Vector messageListeners = new Vector();

    /**
     * The provider that created us.
     */
    private ProtocolProviderServiceMsnImpl msnProvider = null;

    /**
     * A reference to the persistent presence operation set that we use
     * to match incoming messages to <tt>Contact</tt>s and vice versa.
     */
    private OperationSetPersistentPresenceMsnImpl opSetPersPresence = null;

    /**
     * Creates an instance of this operation set.
     * @param provider a ref to the <tt>ProtocolProviderServiceImpl</tt>
     * that created us and that we'll use for retrieving the underlying aim
     * connection.
     */
    OperationSetBasicInstantMessagingMsnImpl(
        ProtocolProviderServiceMsnImpl provider)
    {
        this.msnProvider = provider;
        provider.addRegistrationStateChangeListener(new RegistrationStateListener());
    }

    /**
     * Registers a MessageListener with this operation set so that it gets
     * notifications of successful message delivery, failure or reception of
     * incoming messages..
     *
     * @param listener the <tt>MessageListener</tt> to register.
     */
    public void addMessageListener(MessageListener listener)
    {
        synchronized(messageListeners)
        {
            if(!messageListeners.contains(listener))
            {
                this.messageListeners.add(listener);
            }
        }
    }

    /**
     * Unregisters <tt>listener</tt> so that it won't receive any further
     * notifications upon successful message delivery, failure or reception of
     * incoming messages..
     *
     * @param listener the <tt>MessageListener</tt> to unregister.
     */
    public void removeMessageListener(MessageListener listener)
    {
        synchronized(messageListeners)
        {
            this.messageListeners.remove(listener);
        }
    }

    /**
     * Determines whether the protocol provider (or the protocol itself) support
     * sending and receiving offline messages. Most often this method would
     * return true for protocols that support offline messages and false for
     * those that don't. It is however possible for a protocol to support these
     * messages and yet have a particular account that does not (i.e. feature
     * not enabled on the protocol server). In cases like this it is possible
     * for this method to return true even when offline messaging is not
     * supported, and then have the sendMessage method throw an
     * OperationFailedException with code - OFFLINE_MESSAGES_NOT_SUPPORTED.
     *
     * @return <tt>true</tt> if the protocol supports offline messages and
     * <tt>false</tt> otherwise.
     */
    public boolean isOfflineMessagingSupported()
    {
        return false;
    }
    
    /**
     * Determines whether the protocol supports the supplied content type
     *
     * @param contentType the type we want to check
     * @return <tt>true</tt> if the protocol supports it and
     * <tt>false</tt> otherwise.
     */
    public boolean isContentTypeSupported(String contentType)
    {
        if(contentType.equals(DEFAULT_MIME_TYPE))
            return true;
        else
           return false;
    }

    /**
     * Create a Message instance for sending arbitrary MIME-encoding content.
     *
     * @param content content value
     * @param contentType the MIME-type for <tt>content</tt>
     * @param contentEncoding encoding used for <tt>content</tt>
     * @param subject a <tt>String</tt> subject or <tt>null</tt> for now subject.
     * @return the newly created message.
     */
    public Message createMessage(byte[] content, String contentType,
                                 String contentEncoding, String subject)
    {
        return new MessageMsnImpl(new String(content), contentType
                                  , contentEncoding, subject);
    }

    /**
     * Create a Message instance for sending a simple text messages with
     * default (text/plain) content type and encoding.
     *
     * @param messageText the string content of the message.
     * @return Message the newly created message
     */
    public Message createMessage(String messageText)
    {
        return new MessageMsnImpl(messageText, DEFAULT_MIME_TYPE
                                  , DEFAULT_MIME_ENCODING, null);
    }

    /**
     * Sends the <tt>message</tt> to the destination indicated by the
     * <tt>to</tt> contact.
     *
     * @param to the <tt>Contact</tt> to send <tt>message</tt> to
     * @param message the <tt>Message</tt> to send.
     * @throws java.lang.IllegalStateException if the underlying stack is
     * not registered and initialized.
     * @throws java.lang.IllegalArgumentException if <tt>to</tt> is not an
     * instance of ContactImpl.
     */
    public void sendInstantMessage(Contact to, Message message)
        throws IllegalStateException, IllegalArgumentException
    {
        assertConnected();

        if( !(to instanceof ContactMsnImpl) )
           throw new IllegalArgumentException(
               "The specified contact is not an MSN contact."
               + to);

        if( to.isPersistent() &&
            to.getPresenceStatus().equals(MsnStatusEnum.OFFLINE))
        {
            MessageDeliveryFailedEvent evt =
                new MessageDeliveryFailedEvent(
                    message,
                    to,
                    MessageDeliveryFailedEvent.OFFLINE_MESSAGES_NOT_SUPPORTED,
                    new Date());
            fireMessageEvent(evt);
            return;
        }

        msnProvider.getMessenger().
            sendText(
                ((ContactMsnImpl)to).getSourceContact().getEmail(),
                message.getContent()
            );
        MessageDeliveredEvent msgDeliveredEvt
            = new MessageDeliveredEvent(
                message, to, new Date());

        fireMessageEvent(msgDeliveredEvt);
    }

    /**
     * Utility method throwing an exception if the stack is not properly
     * initialized.
     * @throws java.lang.IllegalStateException if the underlying stack is
     * not registered and initialized.
     */
    private void assertConnected() throws IllegalStateException
    {
        if (msnProvider == null)
            throw new IllegalStateException(
                "The provider must be non-null and signed on the "
                +"service before being able to communicate.");
        if (!msnProvider.isRegistered())
            throw new IllegalStateException(
                "The provider must be signed on the service before "
                +"being able to communicate.");
    }

    /**
     * Our listener that will tell us when we're registered to
     */
    private class RegistrationStateListener
        implements RegistrationStateChangeListener
    {
        /**
         * The method is called by a ProtocolProvider implementation whenever
         * a change in the registration state of the corresponding provider had
         * occurred.
         * @param evt ProviderStatusChangeEvent the event describing the status
         * change.
         */
        public void registrationStateChanged(RegistrationStateChangeEvent evt)
        {
            logger.debug("The provider changed state from: "
                         + evt.getOldState()
                         + " to: " + evt.getNewState());

            if (evt.getNewState() == RegistrationState.REGISTERED)
            {
                opSetPersPresence = (OperationSetPersistentPresenceMsnImpl)
                    msnProvider.getSupportedOperationSets()
                        .get(OperationSetPersistentPresence.class.getName());

                msnProvider.getMessenger().
                    addMessageListener(new MsnMessageListener());
                msnProvider.getMessenger().
                    addEmailListener(new MsnMessageListener());
            }
        }
    }

    /**
     * Delivers the specified event to all registered message listeners.
     * @param evt the <tt>EventObject</tt> that we'd like delivered to all
     * registered message listeners.
     */
    private void fireMessageEvent(EventObject evt)
    {
        Iterator listeners = null;
        synchronized (messageListeners)
        {
            listeners = new ArrayList(messageListeners).iterator();
        }

        while (listeners.hasNext())
        {
            MessageListener listener
                = (MessageListener) listeners.next();

            if (evt instanceof MessageDeliveredEvent)
            {
                listener.messageDelivered( (MessageDeliveredEvent) evt);
            }
            else if (evt instanceof MessageReceivedEvent)
            {
                listener.messageReceived( (MessageReceivedEvent) evt);
            }
            else if (evt instanceof MessageDeliveryFailedEvent)
            {
                listener.messageDeliveryFailed(
                    (MessageDeliveryFailedEvent) evt);
            }
        }
    }

    private class MsnMessageListener
        extends MsnMessageAdapter
        implements MsnEmailListener
    {
        public void instantMessageReceived(MsnSwitchboard switchboard,
                                           MsnInstantMessage message,
                                           MsnContact contact)
        {
            Message newMessage = createMessage(message.getContent());
            Contact sourceContact = opSetPersPresence.
                findContactByID(contact.getEmail().getEmailAddress());

            if(sourceContact == null)
            {
                logger.debug("received a message from an unknown contact: "
                                   + contact);
                //create the volatile contact
                sourceContact = opSetPersPresence.
                    createVolatileContact(contact);
            }

            MessageReceivedEvent msgReceivedEvt
                = new MessageReceivedEvent(
                    newMessage, sourceContact , new Date() );

            fireMessageEvent(msgReceivedEvt);
        }

        public void initialEmailNotificationReceived(MsnSwitchboard switchboard,
                                                     MsnEmailInitMessage message, 
                                                     MsnContact contact)
        {
        }

        public void initialEmailDataReceived(MsnSwitchboard switchboard,
                                             MsnEmailInitEmailData message,
                                             MsnContact contact)
        {
        }

        public void newEmailNotificationReceived(MsnSwitchboard switchboard,
                                                 MsnEmailNotifyMessage message,
                                                 MsnContact contact)
        {
            // we don't process incoming event without email.
            if ((message.getFromAddr() == null)
                || (message.getFromAddr().indexOf('@') < 0))
            {
                return;
            }
            
            String subject = message.getSubject();

            try
            {
                subject = MimeUtility.decodeText(subject);
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }

            Message newMailMessage = new MessageMsnImpl(
                    MessageFormat.format(Resources.getString("newMail"), 
                        new Object[]{message.getFrom(), 
                                     message.getFromAddr(), 
                                     subject}),
                     DEFAULT_MIME_TYPE,
                     DEFAULT_MIME_ENCODING,
                     subject);

             Contact sourceContact = opSetPersPresence.
                 findContactByID(message.getFromAddr());

             if (sourceContact == null)
             {
                 logger.debug("received a new mail from an unknown contact: "
                                    + message.getFrom()
                                    + " &lt;" + message.getFromAddr() + "&gt;");
                 //create the volatile contact
                 sourceContact = opSetPersPresence
                     .createVolatileContact(contact);
             }
             MessageReceivedEvent msgReceivedEvt
                 = new MessageReceivedEvent(
                     newMailMessage, sourceContact, new Date(),
                     MessageReceivedEvent.SYSTEM_MESSAGE_RECEIVED);

             fireMessageEvent(msgReceivedEvt);
        }

        public void activityEmailNotificationReceived(MsnSwitchboard switchboard,
                                                      MsnEmailActivityMessage message,
                                                      MsnContact contact)
        {
        }
    }    
}
