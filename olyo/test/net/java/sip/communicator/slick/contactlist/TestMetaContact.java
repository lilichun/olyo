/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.slick.contactlist;

import java.util.*;

import junit.framework.*;
import net.java.sip.communicator.impl.protocol.mock.*;
import net.java.sip.communicator.service.contactlist.*;
import net.java.sip.communicator.service.protocol.*;

/**
 * @todo comment
 * @author Emil Ivov
 */
public class TestMetaContact extends TestCase
{
    /**
     * A reference to the SLICK fixture.
     */
    MclSlickFixture fixture = new MclSlickFixture(getClass().getName());

    /**
     * The MetaContact that we're doing the testing aginst.
     */
    MetaContact metaContact = null;

    /**
     * The mock contact that we're doing the testing against.
     */
    MockContact mockContact = null;


    public TestMetaContact(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        fixture.setUp();

        mockContact = MetaContactListServiceLick.subLevelContact;

        metaContact = fixture.metaClService.findMetaContactByContact(
                                                                mockContact);

    }

    protected void tearDown() throws Exception
    {
        fixture.tearDown();

        fixture = null;
        super.tearDown();

    }

    /**
     * Tests getContact()
     */
    public void testGetContact()
    {
        Contact actualReturn = metaContact.getContact(
                                mockContact.getAddress(), fixture.mockProvider);

        assertNotNull("getContact() return null.", actualReturn);

        assertSame("getContact() did not return the right proto group."
                   , mockContact, actualReturn);

    }

    /**
     * Test getContactCount()
     */
    public void testGetContactCount()
    {
        //we only have mock provider registered so the count should be one.
        assertEquals("getContactCount()", 1, metaContact.getContactCount());
    }

    /**
     * Test getContacts()
     */
    public void testGetContacts()
    {
        Iterator childContacts = metaContact.getContacts();

        assertNotNull("getContacts() returned a null iterator."
                      , childContacts);

        assertTrue("getContacts() returned an empty iterator."
                   , childContacts.hasNext());

        assertSame("The iterator returned by getContacts() ("
                   + mockContact.getAddress()
                   +")did not contain the "
                   +"right mock contact"
                   , mockContact, childContacts.next());
    }

    /**
     * Test getContactsForProvider
     */
    public void testGetContactsForProvider()
    {
        Iterator childContacts = metaContact.getContactsForProvider(
                                                        fixture.mockProvider);

        assertNotNull("getContactsForProvider() returned a null iterator."
                      , childContacts);

        assertTrue("getContactsForProvider() returned an empty iterator."
                   , childContacts.hasNext());

        assertSame("The iterator returned by getContactsForProvider() ("
                   + mockContact.getAddress()
                   +")did not contain the "
                   +"right mock contact"
                   , mockContact, childContacts.next());

    }

    /**
     * Tests that getDefaultContact() returns the contact that is currently the
     * best choice for communication with the tested meta contact.
     */
    public void testGetDefaultContact()
    {
        Contact actualReturn = metaContact.getDefaultContact();

        assertNotNull("getDefaultContact() return null.", actualReturn);

        assertSame("getDefaultContact() did not return the right proto group."
                   , actualReturn, mockContact);
    }

    /**
     * Checks whether the display name matches the one in th mock contact.
     */
    public void testGetDisplayName()
    {
        assertEquals("getDisplayName()",
                     mockContact.getDisplayName(),
                     metaContact.getDisplayName());
    }

    /**
     * Very light test of the existance and the uniqueness of meta UIDs
     */
    public void testGetMetaUID()
    {
        String metaUID = metaContact.getMetaUID();
        assertNotNull( "getMetaUID() did not seem to return a valid UID"
                       , metaUID);

        assertTrue( "getMetaUID() did not seem to return a valid UID"
                       , metaUID.trim().length() > 0);
    }

    /**
     * Verifies whether the compare method in meta contacts takes into account
     * all important details: i.e. contact status, alphabetical order.
     */
    public void testCompareTo()
    {
        verifyCompareToForAllContactsInGroupAndSubgroups(
                fixture.metaClService.getRoot());
    }

    /**
     * compare all neighbour contacts in <tt>group</tt> and its subgroups and
     * try to determine whether they'reproperly ordered.
     *
     * @param group the <tt>MetaContactGroup</tt> to walk through
     */
    public void verifyCompareToForAllContactsInGroupAndSubgroups(
                                MetaContactGroup group)
    {
        //first check order of contacts in this group
        Iterator contacts = group.getChildContacts();

        MetaContact previousContact = null;
        int previousContactIsOnlineStatus = 0;

        while(contacts.hasNext())
        {
            MetaContact currentContact  = (MetaContact)contacts.next();

            //calculate the total status for this contact
            Iterator protoContacts = currentContact.getContacts();
            int currentContactIsOnlineStatus = 0;

            while(protoContacts.hasNext())
            {
                if (((Contact)protoContacts.next())
                        .getPresenceStatus().isOnline())
                {
                    currentContactIsOnlineStatus = 1;
                }
            }

            if (previousContact != null)
            {
                assertTrue( previousContact + " with status="
                        + previousContactIsOnlineStatus
                        + " was wrongfully before "
                        + currentContact+ " with status="
                        + currentContactIsOnlineStatus
                        , previousContactIsOnlineStatus >= currentContactIsOnlineStatus);

                //if both were equal then assert alphabetical order.
                if (previousContactIsOnlineStatus == currentContactIsOnlineStatus)
                    assertTrue( "The display name: "
                               + previousContact.getDisplayName()
                               + " should be considered less than "
                               + currentContact.getDisplayName()
                               ,previousContact.getDisplayName()
                                    .compareToIgnoreCase(
                                        currentContact.getDisplayName())
                               <= 0);
            }
            previousContact = currentContact;
            previousContactIsOnlineStatus = currentContactIsOnlineStatus;
        }

        //now go over the subgroups
        Iterator subgroups = group.getSubgroups();

        while(subgroups.hasNext())
        {
            verifyCompareToForAllContactsInGroupAndSubgroups(
                    (MetaContactGroup)subgroups.next());
        }
    }

}
