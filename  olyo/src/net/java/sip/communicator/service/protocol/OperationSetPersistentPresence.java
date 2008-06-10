/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.service.protocol;

import net.java.sip.communicator.service.protocol.event.*;

/**
 * This interface is an extension of the presence operation set, meant to be
 * implemented by protocols that support server stored contact lists (like icq
 * for example).
 * <p>
 * A server stored contact list is stored somewhere across the network and this
 * interface allows GUI and other plugins to use it in a way similar to the
 * way they'd use a javax.swing.tree.TreeModel, i.e. it would contain an initial
 * number of members/children that is likely to change, dispatching a series of
 * events delivered through the <tt>SubscriptionListener</tt> and
 * <tt>ServerStoredGroupChangeListener</tt> interfaces.
 * <p>
 * The interfaces defines extended subscription methods that include an extra
 * <tt>parentGroup</tt> parameter. Simple subscribe and usubscribe
 * operations defined by the parent <tt>OperationSetPresence</tt> operation
 * set, will still work, adding contacts to a default, root group.
 * to be used by GUI and other plugins the same way that they would use a
 *
 * @author Emil Ivov
 */
public interface OperationSetPersistentPresence
    extends OperationSetPresence
{

    /**
     * Persistently adds a subscription for the presence status of the  contact
     * corresponding to the specified contactIdentifier to the top level group.
     * Note that this method, unlike the subscibe method in
     * OperationSetPresence, is going the subscribe the specified contact in a
     * persistent manner or in other words, it will add it to a server stored
     * contact list and thus making the subscription for its presence status
     * last along multiple registrations/logins/signons.
     * <p>
     * Apart from an exception in the case
     * of an immediate failure, the method won't return any indication of
     * success or failure. That would happen later on through a
     * SubscriptionEvent generated by one of the methods of the
     * SubscriptionListener.
     * <p>
     * @param contactIdentifier the contact whose status updates we are subscribing
     *   for.
     * <p>
     * @throws OperationFailedException with code NETWORK_FAILURE if subscribing
     * fails due to errors experienced during network communication
     * @throws IllegalArgumentException if <tt>contact</tt> is not a contact
     * known to the underlying protocol provider
     * @throws IllegalStateException if the underlying protocol provider is not
     * registered/signed on a public service.
     */
    public void subscribe(String contactIdentifier)
        throws IllegalArgumentException,
               IllegalStateException,
               OperationFailedException;


    /**
     * Persistently adds a subscription for the presence status of the  contact
     * corresponding to the specified contactIdentifier and indicates that it
     * should be added to the specified group of the server stored contact list.
     * Note that apart from an exception in the case of an immediate failure,
     * the method won't return any indication of success or failure. That would
     * happen later on through a SubscriptionEvent generated by one of the
     * methods of the SubscriptionListener.
     * <p>
     * @param contactIdentifier the contact whose status updates we are subscribing
     *   for.
     * @param parent the parent group of the server stored contact list where
     * the contact should be added.
     * <p>
     * @throws OperationFailedException with code NETWORK_FAILURE if subscribing
     * fails due to errors experienced during network communication
     * @throws IllegalArgumentException if <tt>contact</tt> or
     * <tt>parent</tt> are not a contact known to the underlying protocol
     * provider.
     * @throws IllegalStateException if the underlying protocol provider is not
     * registered/signed on a public service.
     */
    public void subscribe(ContactGroup parent, String contactIdentifier)
        throws IllegalArgumentException,
               IllegalStateException,
               OperationFailedException;

    /**
     * Persistently removes a subscription for the presence status of the
     * specified contact. This method has a persistent effect and the specified
     * contact is completely removed from any server stored contact lists.
     *
     * @param contact the contact whose status updates we are unsubscribing
     *   from.
     *
     * @throws OperationFailedException with code NETWORK_FAILURE if unsubscribing
     * fails due to errors experienced during network communication
     * @throws IllegalArgumentException if <tt>contact</tt> is not a contact
     * known to the underlying protocol provider
     * @throws IllegalStateException if the underlying protocol provider is not
     * registered/signed on a public service.
     */
    public void unsubscribe(Contact contact)
        throws IllegalArgumentException,
               IllegalStateException,
               OperationFailedException;

    /**
     * Creates a group with the specified name and parent in the server stored
     * contact list.
     * @param groupName the name of the new group to create.
     * @param parent the group where the new group should be created
     *
     * @throws OperationFailedException with code NETWORK_FAILURE if creating
     * the group fails because of a network error.
     * @throws IllegalArgumentException if <tt>parent</tt> is not a contact
     * known to the underlying protocol provider
     * @throws IllegalStateException if the underlying protocol provider is not
     * registered/signed on a public service.
     */
    public void createServerStoredContactGroup(
            ContactGroup parent, String groupName)
        throws OperationFailedException;

    /**
     * Removes the specified group from the server stored contact list.
     * @param group the group to remove.
     *
     * @throws OperationFailedException with code NETWORK_FAILURE if deleting
     * the group fails because of a network error.
     * @throws IllegalArgumentException if <tt>parent</tt> is not a contact
     * known to the underlying protocol provider.
     * @throws IllegalStateException if the underlying protocol provider is not
     * registered/signed on a public service.
     */
    public void removeServerStoredContactGroup(ContactGroup group);

    /**
     * Renames the specified group from the server stored contact list. This
     * method would return before the group has actually been renamed. A
     * <tt>ServerStoredGroupEvent</tt> would be dispatched once new name
     * has been acknowledged by the server.
     *
     * @param group the group to rename.
     * @param newName the new name of the group.
     *
     * @throws OperationFailedException with code NETWORK_FAILURE if deleting
     * the group fails because of a network error.
     * @throws IllegalArgumentException if <tt>parent</tt> is not a contact
     * known to the underlying protocol provider.
     * @throws IllegalStateException if the underlying protocol provider is not
     * registered/signed on a public service.
     */
    public void renameServerStoredContactGroup(
                    ContactGroup group, String newName);

    /**
     * Removes the specified contact from its current parent and places it
     * under <tt>newParent</tt>.
     * @param contactToMove the <tt>Contact</tt> to move
     * @param newParent the <tt>ContactGroup</tt> where <tt>Contact</tt>
     * would be placed.
     */
    public void moveContactToGroup(Contact contactToMove,
                                   ContactGroup newParent);

    /**
     * Returns the root group of the server stored contact list. Most often this
     * would be a dummy group that user interface implementations may better not
     * show.
     *
     * @return the root ContactGroup for the ContactList stored by this service.
     */
    public ContactGroup getServerStoredContactListRoot();

    /**
     * Registers a listener that would receive events upong changes in server
     * stored groups.
     * @param listener a ServerStoredGroupChangeListener impl that would receive
     * events upong group changes.
     */
    public void addServerStoredGroupChangeListener(
        ServerStoredGroupListener listener);

    /**
     * Removes the specified group change listener so that it won't receive
     * any further events.
     * @param listener the ServerStoredGroupChangeListener to remove
     */
    public void removeServerStoredGroupChangeListener(
        ServerStoredGroupListener listener);


    /**
     * Creates and returns a unresolved contact from the specified
     * <tt>address</tt> and <tt>persistentData</tt>. The method will not try
     * to establish a network connection and resolve the newly created Contact
     * against the server. The protocol provider may will later try and resolve
     * the contact. When this happens the corresponding event would notify
     * interested subscription listeners.
     *
     * @param address an identifier of the contact that we'll be creating.
     * @param persistentData a String returned Contact's getPersistentData()
     * method during a previous run and that has been persistently stored
     * locally.
     * @param parentGroup the group where the unresolved contact is
     * supposed to belong to.
     *
     * @return the unresolved <tt>Contact</tt> created from the specified
     * <tt>address</tt> and <tt>persistentData</tt>
     */
    public Contact createUnresolvedContact(
        String address, String persistentData, ContactGroup parentGroup);

    /**
     * Creates and returns a unresolved contact group from the specified
     * <tt>address</tt> and <tt>persistentData</tt>. The method will not try
     * to establish a network connection and resolve the newly created
     * <tt>ContactGroup</tt> against the server or the contact itself. The
     * protocol provider will later resolve the contact group. When this happens
     * the corresponding event would notify interested subscription listeners.
     *
     * @param groupUID an identifier, returned by ContactGroup's getGroupUID,
     * that the protocol provider may use in order to create the group.
     * @param persistentData a String returned ContactGroups's getPersistentData()
     * method during a previous run and that has been persistently stored
     * locally.
     * @param parentGroup the group under which the new group is to be created
     * or null if this is group directly underneath the root.
     * @return the unresolved <tt>ContactGroup</tt> created from the specified
     * <tt>uid</tt> and <tt>persistentData</tt>
     */
    public ContactGroup createUnresolvedContactGroup(
        String groupUID, String persistentData, ContactGroup parentGroup);


}
