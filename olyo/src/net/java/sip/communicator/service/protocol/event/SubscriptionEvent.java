/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.service.protocol.event;

import java.util.*;

import net.java.sip.communicator.service.protocol.*;

/**
 * SubscriptionEvents indicate creation removal or failure of a given
 * Subscription. Note that in SIP Communicator the terms Subscription and
 * Contact are quite similar: A contact becomes available and it is possible
 * to query its presence status and user information, once a
 * subscription for this contact has been created.
 *
 * @author Emil Ivov
 */
public class SubscriptionEvent
    extends EventObject
{
    private int eventID = -1;

    /**
     * Indicates that the SubscriptionEvent instance was triggered by the
     * creation of a new subscription
     */
    public static final int SUBSCRIPTION_CREATED = 1;

    /**
     * Indicates that the SubscriptionEvent instance was triggered by the
     * removal of an existing subscriptionl'
     */
    public static final int SUBSCRIPTION_REMOVED = 2;

    /**
     * Indicates that the SubscriptionEvent instance was triggered by the fact
     * that no confirmation of the successful completion of a new subscription
     * has been received.
     */
    public static final int SUBSCRIPTION_FAILED  = 3;

    /**
     * Indicates that the SubscriptionEvent instance was triggered by the fact
     * that the presence of a particular contact in the contact list has been
     * confirmed by the server (resolved).
     */
    public static final int SUBSCRIPTION_RESOLVED  = 4;


    private ProtocolProviderService sourceProvider = null;
    private ContactGroup  parentGroup = null;

    /**
     * Creates a new Subscription event according to the specified parameters.
     * @param source the Contact instance that this subscription pertains to.
     * @param provider the ProtocolProviderService instance where this event
     * occurred
     * @param parentGroup the ContactGroup underwhich the corresponding Contact
     * is located
     * @param eventID one of the SUBSCRIPTION_XXX static fields indicating the
     * nature of the event.
     */
    public SubscriptionEvent( Contact source,
                       ProtocolProviderService provider,
                       ContactGroup parentGroup,
                       int eventID)
    {
        super(source);
        this.sourceProvider = provider;
        this.parentGroup = parentGroup;
        this.eventID = eventID;
    }

    /**
     * Returns the provider that the source contact belongs to.
     * @return the provider that the source contact belongs to.
     */
    public ProtocolProviderService getSourceProvider()
    {
        return sourceProvider;
    }

    /**
     * Returns the provider that the source contact belongs to.
     * @return the provider that the source contact belongs to.
     */
    public Contact getSourceContact()
    {
        return (Contact)getSource();
    }

    /**
     * Returns (if applicable) the group containing the contact that cause this
     * event. In the case of a non persistent presence operation set this
     * field is null.
     * @return the ContactGroup (if there is one) containing the contact that
     * caused the event.
     */
    public ContactGroup getParentGroup()
    {
        return parentGroup;
    }

    /**
     * Returns a String representation of this ContactPresenceStatusChangeEvent
     *
     * @return  A a String representation of this <tt>SubscriptionEvent</tt>.
     */
    public String toString()
    {
        StringBuffer buff
            = new StringBuffer("SubscriptionEvent-[ ContactID=");
        buff.append(getSourceContact().getAddress());
        buff.append(", eventID=").append(getEventID());
        if(getParentGroup() != null)
            buff.append(", ParentGroup=").append(getParentGroup().getGroupName());
        return buff.toString();
    }

    /**
     * Returns an event id specifying whether the type of this event (e.g.
     * SUBSCRIPTION_CREATED, SUBSCRIPTION_FAILED and etc.)
     * @return one of the SUBSCRIPTION_XXX int fields of this class.
     */
    public int getEventID()
    {
        return eventID;
    }
}
