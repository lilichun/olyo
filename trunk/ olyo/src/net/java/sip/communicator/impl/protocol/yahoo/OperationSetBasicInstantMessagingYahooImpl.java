/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.protocol.yahoo;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.service.protocol.event.*;
import net.java.sip.communicator.util.*;
import ymsg.network.event.*;
import ymsg.support.*;

/**
 * A straightforward implementation of the basic instant messaging operation
 * set.
 *
 * @author Damian Minkov
 */
public class OperationSetBasicInstantMessagingYahooImpl
    implements OperationSetBasicInstantMessaging
{
    /**
     * Logger for this class
     */
    private static final Logger logger =
        Logger.getLogger(OperationSetBasicInstantMessagingYahooImpl.class);
    
    /**
     * HTML content type
     */
    private static final String CONTENT_TYPE_HTML = "text/html";

    /**
     * Yahoo has limit of message length. If exceeded 
     * message is not delivered and no notification is received for that.
     */
    private static int MAX_MESSAGE_LENGTH = 800; // 949
    
    /**
     * A list of listeneres registered for message events.
     */
    private Vector messageListeners = new Vector();

    /**
     * The provider that created us.
     */
    private ProtocolProviderServiceYahooImpl yahooProvider = null;

    /**
     * Message decoder allows to convert Yahoo formated messages, which can
     * contains some specials characters, to HTML or to plain text.
     */
     private MessageDecoder messageDecoder = new MessageDecoder();

    /**
     * A reference to the persistent presence operation set that we use
     * to match incoming messages to <tt>Contact</tt>s and vice versa.
     */
    private OperationSetPersistentPresenceYahooImpl opSetPersPresence = null;

    /**
     * Creates an instance of this operation set.
     * @param provider a ref to the <tt>ProtocolProviderServiceImpl</tt>
     * that created us and that we'll use for retrieving the underlying aim
     * connection.
     */
    OperationSetBasicInstantMessagingYahooImpl(
        ProtocolProviderServiceYahooImpl provider)
    {
        this.yahooProvider = provider;
        provider.addRegistrationStateChangeListener(
            new RegistrationStateListener());
    }

    /**
     * Registeres a MessageListener with this operation set so that it gets
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
     * Unregisteres <tt>listener</tt> so that it won't receive any further
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
     * Determines wheter the protocol provider (or the protocol itself) support
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
        return true;
    }

    /**
     * Determines wheter the protocol supports the supplied content type
     *
     * @param contentType the type we want to check
     * @return <tt>true</tt> if the protocol supports it and
     * <tt>false</tt> otherwise.
     */
    public boolean isContentTypeSupported(String contentType)
    {
        if(contentType.equals(DEFAULT_MIME_TYPE) || 
           contentType.equals(CONTENT_TYPE_HTML))
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
        return new MessageYahooImpl(new String(content), contentType
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
        return new MessageYahooImpl(
            messageText, 
            DEFAULT_MIME_TYPE, 
            DEFAULT_MIME_ENCODING, null);
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

        if( !(to instanceof ContactYahooImpl) )
           throw new IllegalArgumentException(
               "The specified contact is not a Yahoo contact."
               + to);

        try
        {
            String toUserID = ((ContactYahooImpl) to).getID();

            byte[] msgBytesToBeSent = message.getContent().getBytes();

            // split the message in parts with max allowed length
            // and send them all
            do
            {
                if(msgBytesToBeSent.length > MAX_MESSAGE_LENGTH)
                {
                    byte[] tmp1 = new byte[MAX_MESSAGE_LENGTH];
                    System.arraycopy(msgBytesToBeSent, 
                        0, tmp1, 0, MAX_MESSAGE_LENGTH);
                    
                    byte[] tmp2 = 
                        new byte[msgBytesToBeSent.length - MAX_MESSAGE_LENGTH];
                    System.arraycopy(msgBytesToBeSent, 
                        MAX_MESSAGE_LENGTH, tmp2, 0, tmp2.length);
                    
                    msgBytesToBeSent = tmp2;
                    
                    yahooProvider.getYahooSession().sendMessage(
                        toUserID,
                        new String(tmp1));
                }
                else
                {
                    yahooProvider.getYahooSession().sendMessage(
                        toUserID,
                        new String(msgBytesToBeSent));
                }
                
                MessageDeliveredEvent msgDeliveredEvt
                    = new MessageDeliveredEvent(
                        message, to, new Date());

                fireMessageEvent(msgDeliveredEvt);
            }
            while(msgBytesToBeSent.length > MAX_MESSAGE_LENGTH);
        }
        catch (IOException ex)
        {
            logger.fatal("Cannot Send Message! " + ex.getMessage());
            MessageDeliveryFailedEvent evt =
                new MessageDeliveryFailedEvent(
                    message,
                    to,
                    MessageDeliveryFailedEvent.NETWORK_FAILURE,
                    new Date());
            fireMessageEvent(evt);
            return;
        }
    }

    /**
     * Utility method throwing an exception if the stack is not properly
     * initialized.
     * @throws java.lang.IllegalStateException if the underlying stack is
     * not registered and initialized.
     */
    private void assertConnected() throws IllegalStateException
    {
        if (yahooProvider == null)
            throw new IllegalStateException(
                "The provider must be non-null and signed on the "
                +"service before being able to communicate.");
        if (!yahooProvider.isRegistered())
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
         * The method is called by a ProtocolProvider implementation whenver
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
                opSetPersPresence = (OperationSetPersistentPresenceYahooImpl)
                    yahooProvider.getSupportedOperationSets()
                        .get(OperationSetPersistentPresence.class.getName());

                yahooProvider.getYahooSession().
                    addSessionListener(new YahooMessageListener());
            }
        }
    }

    /**
     * Delivers the specified event to all registered message listeners.
     * @param evt the <tt>EventObject</tt> that we'd like delivered to all
     * registered message listerners.
     */
    private void fireMessageEvent(EventObject evt)
    {
        Iterator listeners = null;
        synchronized (messageListeners)
        {
            listeners = new ArrayList(messageListeners).iterator();
        }

        logger.debug("Dispatching  msg evt. Listeners="
                     + messageListeners.size()
                     + " evt=" + evt);

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

    /**
     * This class provides methods to listen for yahoo events which interest us.
     */
    private class YahooMessageListener
        extends SessionAdapter
    {
        /**
         * Overrides <tt>messageReceived</tt> from <tt>SessionAdapter</tt>,
         * called when we receive a new intant message.
         *
         * @param ev Event with information on the received message
         */
        public void messageReceived(SessionEvent ev)
        {
            handleNewMessage(ev);
        }

        /**
         * Overrides <tt>offlineMessageReceived</tt> from <tt>SessionAdapter</tt>,
         * called when we receive a message which has been sent to us
         * when we were offline.
         *
         * @param ev Event with information on the received message
         */
        public void offlineMessageReceived(SessionEvent ev)
        {
            handleNewMessage(ev);
        }
 
        /**
         * Overrides <tt>newMailReceived</tt> from <tt>SessionAdapter</tt>,
         * called when yahoo alert us that there is a new message in our mailbox.
         * There is two types of notification, the first one provides only
         * the number of unread mails and the second gives informations about
         * a precise new mail. Here, we care about only the second case in which
         * we should always have the email of the sender of the mail.
         *
         * @param ev Event with information on the received email
         */
         public void newMailReceived(SessionNewMailEvent ev)
         {
             // why, if I provide mail@yahoo.FR when registering my account,
             // SC later tells me that my email address is mail@yahoo.COM ??
             // because of this users will always be sent on yahoo.com mail
             // login page rather than their usual (yahoo.XXX) login page.
             String myEmail = yahooProvider.getAccountID().getAccountAddress();

             // we don't process incoming email event without source address.
             // it allows us to avoid some spams
             if ((ev.getEmailAddress() == null)
                    || (ev.getEmailAddress().indexOf('@') < 0))
             {
                 return;
             }

             String yahooMailLogon = "http://mail."
                     + myEmail.substring(myEmail.indexOf("@") + 1);

             yahooMailLogon = "<a href=\""
                     + yahooMailLogon + "\">"
                     + yahooMailLogon + "</a>";

             String newMail = "<b>" + Resources.getString("newMail") + " : </b> "
                     + ev.getSubject();

             newMail += "\n<br /><b>" + Resources.getString("from") + " : </b> "
                     + ev.getFrom() + " &lt;" + ev.getEmailAddress() + "&gt;";

             newMail += "\n<br />&nbsp;&nbsp;&nbsp;&nbsp;" + yahooMailLogon;

             Message newMailMessage = new MessageYahooImpl(
                     newMail,
                     CONTENT_TYPE_HTML,
                     DEFAULT_MIME_ENCODING,
                     null);

             Contact sourceContact = opSetPersPresence.
                 findContactByID(ev.getEmailAddress());

             if (sourceContact == null)
             {
                 logger.debug("received a new mail from an unknown contact: "
                                    + ev.getFrom()
                                    + " &lt;" + ev.getEmailAddress() + "&gt;");
                 //create the volatile contact
                 sourceContact = opSetPersPresence
                     .createVolatileContact(ev.getEmailAddress());
             }
             MessageReceivedEvent msgReceivedEvt
                 = new MessageReceivedEvent(
                     newMailMessage, sourceContact, new Date(),
                     MessageReceivedEvent.SYSTEM_MESSAGE_RECEIVED);

             fireMessageEvent(msgReceivedEvt);
         }

        /**
         * Handle incoming message by creating an appropriate Sip Communicator
         * <tt>Message</tt> and firing a <tt>MessageReceivedEvent</tt>
         * to interested listeners.
         *
         * @param ev The original <tt>SessionEvent</tt> which noticed us
         * of an incoming message.
         */
        private void handleNewMessage(SessionEvent ev)
        {
            logger.debug("Message received : " + ev);

            // to keep things simple, we can decodeToText()
            //String formattedMessage = processLinks(
            //        messageDecoder.decodeToText(ev.getMessage()));

            String formattedMessage = ev.getMessage();
            logger.debug("original message received : " + formattedMessage);

            // if the message is decorated by Yahoo, we try to "decode" it first.
            if (formattedMessage.startsWith("\u001b"))
            {
                formattedMessage = processLinks(
                        messageDecoder.decodeToHTML(formattedMessage));
            }
            else
            {
                formattedMessage = processLinks(formattedMessage);
            }

            // now, we try to fix a wrong usage of the size attribute in the
            // <font> HTML element
            // here, the zero 0 correspond to 10px
            formattedMessage =
                    formattedMessage.replaceAll("(<font) (.*) size=\"0\">",
                    "$1 $2 size=\"10\">");
            formattedMessage = 
                    formattedMessage.replaceAll("(<font) (.*) size=\"(\\d+)\">",
                    "$1 $2 style=\"font-size: $3px;\">");

            logger.debug("formatted Message : " + formattedMessage);
            //As no indications in the protocol is it html or not. No harm
            //to set all messages html - doesn't affect the appearance of the gui
            Message newMessage = createMessage(
                formattedMessage.getBytes(),
                CONTENT_TYPE_HTML,
                DEFAULT_MIME_ENCODING,
                null);

            Contact sourceContact = opSetPersPresence.
                findContactByID(ev.getFrom());

             if(sourceContact == null)
            {
                logger.debug("received a message from an unknown contact: "
                                   + ev.getFrom());
                //create the volatile contact
                sourceContact = opSetPersPresence
                    .createVolatileContact(ev.getFrom());
            }

            MessageReceivedEvent msgReceivedEvt
                = new MessageReceivedEvent(
                    newMessage, sourceContact , new Date() );

            fireMessageEvent(msgReceivedEvt);
        }
    }

    /**
     * Format links in the given message. Skips all links, which are already in
     * HTML format and converts all other links.
     * 
     * @param message The source message string.
     * @return The message string with properly formatted links.
     */
    private String processLinks(String message)
    {
        StringBuffer msgBuffer = new StringBuffer();

        // We match two groups of Strings. The first group is the group of any
        // String. The second group is a well formatted HTML link.
        Pattern p = Pattern.compile("(.*?)(<a[\\s][^<]*(/>|</a>))",
                                    Pattern.CASE_INSENSITIVE);

        Matcher m = p.matcher(message);

        int lastMatchIndex = 0;
        while (m.find())
        {
            lastMatchIndex = m.end();

            String matchGroup1 = m.group(1);
            String matchGroup2 = m.group(2);

            String formattedString = this.formatLinksToHTML(matchGroup1);

            m.appendReplacement(msgBuffer,
                replaceSpecialRegExpChars(formattedString) + matchGroup2);
        }

        String tailString = message.substring(lastMatchIndex);

        String formattedTailString = formatLinksToHTML(tailString);

        msgBuffer.append(formattedTailString);

        return msgBuffer.toString();
    }

    /**
     * Replaces some chars that are special in a regular expression.
     * 
     * @param text The initial text.
     * @return the formatted text
     */
    private String replaceSpecialRegExpChars(String text)
    {
        return text.replaceAll("([.()^&$*|])", "\\\\$1");
    }

    /**
     * Goes through the given text and converts all links to HTML links.
     * <p>
     * For example all occurrences of http://sip-communicator.org will be
     * replaced by <a href="http://sip-communicator.org">
     * http://sip-communicator.org</a\>. The same is true for all strings
     * starting with "www".
     * 
     * @param text the text on which the regular expression would be performed 
     * @return the initial text containing only HTML links
     */
    private String formatLinksToHTML(String text)
    {
        String wwwURL = "(www\\." + // Matches the "www" string.
                        "[^/?#<\"'\\s]+" + // Matches at least one char of
                                           // any type except / ? # < " '
                                           // and space.
                        "[\\.]" + // Matches the second point of the link.
                        "[^?#<\"'\\s]+" +   // Matches at least one char of
                                            // any type except ? # < " '
                                            // and space.
                        "(\\?[^#<\"'\\s]*)?" +
                        "(#.*)?)";

        String protocolURL
                =   "([^\"'<>:/?#\\s]+" +   // Matches at least one char of
                                            // any type except " ' < > : / ? #
                                            // and space.
                    "://" + // Matches the :// delimiter in links
                    "[^/?#<\"'\\s]*" +  // Matches any number of times any char
                                        // except / ? # < " ' and space.
                    "[^?#<\"'\\s]*" +   // Matches any number of times any char
                                        // except ? # < " ' and space.
                    "(\\?[^#<\"'\\s]*)?" +
                    "(#.*)?)";

        String url = "(" + wwwURL + "|" + protocolURL + ")";

        Pattern p = Pattern.compile(url, Pattern.CASE_INSENSITIVE);

        Matcher m = p.matcher(text);

        StringBuffer linkBuffer = new StringBuffer();

        String replacement;

        while (m.find())
        {
            String linkGroup = m.group();

            if (linkGroup.startsWith("www"))
            {
                replacement = "<A href=\"" + "http://"
                    + linkGroup + "\">" + linkGroup + "</A>";
            }
            else
            {
                replacement = "<A href=\"" + linkGroup
                    + "\">" + linkGroup + "</A>";
            }

            m.appendReplacement(linkBuffer,
                replaceSpecialRegExpChars(replacement));
        }

        m.appendTail(linkBuffer);

        return linkBuffer.toString();
    }
}
