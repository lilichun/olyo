/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package net.java.sip.communicator.service.protocol.whiteboardobjects;

import net.java.sip.communicator.service.protocol.WhiteboardPoint;

/**
 * Used to access the content of instant whiteboard objects that are sent or
 * received via the WhiteboardOperationSet.
 *
 * @author Julien Waechter
 */
public interface WhiteboardObjectEllipse extends WhiteboardObject
{
    /**
     * A type string constant indicating that an object is of type ellipse.
     */
    public static final String NAME = "WHITEBOARDOBJECTELLIPSE";

    /**
     * Returns the coordinates of this whiteboard object.
     *
     * @return the coordinates of this object.
     */
    public WhiteboardPoint getWhiteboardPoint ();

    /**
     * Sets the coordinates of this whiteboard object.
     *
     * @param whiteboardPoint the coordinates of this object.
     */
    public void setWhiteboardPoint (WhiteboardPoint whiteboardPoint);

    /**
     * Returns the width radius (in pixels) of this whiteboard ellipse.
     *
     * @return the number of pixels for the width radius.
     */
    public double getRadiusX ();

    /**
     * Returns the height radius (in pixels) of this whiteboard ellipse.
     *
     * @return the number of pixels for the height  radius.
     */
    public double getRadiusY ();

    /**
     * Sets the width radius (in pixels) of this whiteboard ellipse.
     *
     * @param radiusX the number of pixels for the width radius.
     */
    public void setRadiusX (double radiusX);

    /**
     * Sets the height radius (in pixels) of this whiteboard ellipse.
     *
     * @param radiusY the number of pixels for the height radius.
     */
    public void setRadiusY (double radiusY);

    /**
     * Returns the fill state of the WhiteboardObject.
     *
     * @return True is filled, false is unfilled.
     */
    public boolean isFill ();

    /**
     * Sets the fill state of the WhiteboardObject.
     * True is filled, false is unfilled.
     *
     * @param fill The new fill state.
     */
    public void setFill (boolean fill);

    /**
     * Specifies the background color for this object. The color parameter
     * must be encoded with standard RGB encoding: bits 24-31 are alpha, 16-23
     * are red, 8-15 are green, 0-7 are blue.
     *
     * @param color the color that we'd like to set for the background of this
     * <tt>WhiteboardObject</tt> (using standard RGB encoding).
     */
    public void setBackgroundColor (int color);

    /**
     * Returns an integer representing the background color of this object. The
     * return value uses standard RGB encoding: bits 24-31 are alpha, 16-23 are
     * red, 8-15 are green, 0-7 are blue.
     *
     * @return the RGB value of the background color of this object.
     */
    public int getBackgroundColor ();
}