/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.protocol.sip;

import java.io.*;
import java.util.*;

import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.util.*;

/**
 * The <tt>SipStatusEnum</tt> gives access to presence states for the Sip
 * protocol. All status icons corresponding to presence states are located with
 * the help of the <tt>imagePath</tt> parameter 
 * 
 * @author Emil Ivov
 * @author Yana Stamcheva
 */
public class SipStatusEnum
{
    private static final Logger logger
        = Logger.getLogger(SipStatusEnum.class);

    /**
     * Indicates an Offline status or status with 0 connectivity.
     */
    public static final String OFFLINE = "Offline";

    /**
     * The Online status. Indicate that the user is able and willing to
     * communicate.
     */
    public static final String ONLINE = "Online";

    /**
     * The busy status. Indicates that the user has connectivity but is doing
     * something else.
     */
    public static final String BUSY = "Busy (DND)";

    /**
     * The On the phone status. Indicates that the user is talking to the phone.
     */
    public static final String ON_THE_PHONE = "On the phone";

    /**
     * The Away  status. Indicates that the user has connectivity but might
     * not be able to immediately act upon initiation of communication.
     */
    public static final String AWAY = "Away";

    /**
     * The Unknown status. Indicate that we don't know if the user is present
     * or not.
     */
    public static final String UNKNOWN = "Unknown";

    /**
     * The path to the status icons.
     */
    private String iconPath;

    /**
     * Indicates an Offline status or status with 0 connectivity.
     */
    private SipPresenceStatus offlineStatus;

    /**
     * The Online status. Indicate that the user is able and willing to
     * communicate.
     */
    private SipPresenceStatus onlineStatus;

    /**
     * The busy status. Indicates that the user has connectivity but is doing
     * something else.
     */
    private SipPresenceStatus busyStatus;

    /**
     * The On the phone status. Indicates that the user is talking to the phone.
     */
    private SipPresenceStatus onThePhoneStatus;

    /**
     * The Away  status. Indicates that the user has connectivity but might
     * not be able to immediately act upon initiation of communication.
     */
    private SipPresenceStatus awayStatus;

    /**
     * The Unknown status. Indicate that we don't know if the user is present
     * or not.
     */
    private SipPresenceStatus unknownStatus;

    /**
     * The supported status set stores all statuses supported by this protocol
     * implementation.
     */
    public List<SipPresenceStatus> supportedStatusSet
        = new LinkedList<SipPresenceStatus>();

    public SipStatusEnum(String iconPath)
    {
        this.iconPath = iconPath;

        this.offlineStatus = new SipPresenceStatus(
            0,
            OFFLINE, 
            loadIcon(iconPath + "/sip16x16-offline.png"));

        this.busyStatus = new SipPresenceStatus(
            30,
            BUSY,
            loadIcon(iconPath + "/sip16x16-busy.png"));

        this.onThePhoneStatus = new SipPresenceStatus(
            37,
            ON_THE_PHONE,
            loadIcon(iconPath + "/sip16x16-phone.png"));

        this.awayStatus = new SipPresenceStatus(
            40,
            AWAY,
            loadIcon(iconPath + "/sip16x16-away.png"));

        this.onlineStatus = new SipPresenceStatus(
            65,
            ONLINE,
            loadIcon(iconPath + "/sip16x16-online.png"));

        this.unknownStatus = new SipPresenceStatus(
            1,
            UNKNOWN,
            loadIcon(iconPath + "/sip16x16-offline.png"));

        // Initialize the list of supported status states.
        supportedStatusSet.add(onlineStatus);
        supportedStatusSet.add(awayStatus);
        supportedStatusSet.add(onThePhoneStatus);
        supportedStatusSet.add(busyStatus);
        supportedStatusSet.add(offlineStatus);
    }

    /**
     * Returns the offline sip status.
     * 
     * @param statusName the name of the status.
     * @return the offline sip status.
     */
    public SipPresenceStatus getStatus(String statusName)
    {
        if (statusName.equals(ONLINE))
            return onlineStatus;
        else if (statusName.equals(OFFLINE))
            return offlineStatus;
        else if (statusName.equals(BUSY))
            return busyStatus;
        else if (statusName.equals(ON_THE_PHONE))
            return onThePhoneStatus;
        else if (statusName.equals(AWAY))
            return awayStatus; 
        else
              return unknownStatus;
    }

    /**
     * Returns an iterator over all status instances supported by the sip
     * provider.
     * @return an <tt>Iterator</tt> over all status instances supported by the
     * sip provider.
     */
    public Iterator getSupportedStatusSet()
    {
        return supportedStatusSet.iterator();
    }

    /**
     * Loads an image from a given image path.
     * @param imagePath The path to the image resource.
     * @return The image extracted from the resource at the specified path.
     */
    public static byte[] loadIcon(String imagePath)
    {
        InputStream is = SipStatusEnum.class.getClassLoader()
            .getResourceAsStream(imagePath);

        byte[] icon = null;
        try
        {
            icon = new byte[is.available()];
            is.read(icon);
        }
        catch (IOException exc)
        {
            logger.error("Failed to load icon: " + imagePath, exc);
        }
        return icon;
    }

    /**
     * An implementation of <tt>PresenceStatus</tt> that enumerates all states
     * that a SIP contact can currently have.
     */
    private class SipPresenceStatus
        extends PresenceStatus
    {
        /**
         * Creates an instance of <tt>SipPresneceStatus</tt> with the
         * specified parameters.
         * @param status the connectivity level of the new presence status
         * instance
         * @param statusName the name of the presence status.
         * @param statusIcon the icon associated with this status
         */
        private SipPresenceStatus(  int status,
                                    String statusName,
                                    byte[] statusIcon)
        {
            super(status, statusName, statusIcon);
        }
    }
}