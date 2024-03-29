/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.protocol.icq;

import java.util.*;

import net.kano.joustsim.oscar.oscar.service.ssi.*;

/**
 * Used when initializing a volatile group.
 * @author Emil Ivov
 */
class VolatileGroup
    implements MutableGroup
{
    private String groupName = new String("NotInContactList");

    VolatileGroup(){}

    VolatileGroup(String groupName)
    {
        this.groupName = groupName;
    }

    /**
     * Returns the name of this group.
     *
     * @return the name of this group.
     */
    public String getName()
    {
        return groupName;
    }


    public void addGroupListener(GroupListener listener){}
    public List getBuddiesCopy(){return null;}
    public void removeGroupListener(GroupListener listener){}
    public void copyBuddies(List buddies){}
    public void deleteBuddies(List ingroup){}
    public void addBuddy(String screenname){}
    public void copyBuddies(Collection buddies){}
    public void deleteBuddy(Buddy buddy){}
    public void rename(String newName){}
}
