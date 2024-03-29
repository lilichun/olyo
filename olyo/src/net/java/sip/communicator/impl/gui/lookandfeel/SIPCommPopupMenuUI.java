/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.gui.lookandfeel;

import java.awt.*;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

import net.java.sip.communicator.impl.gui.utils.*;

/**
 *  SIPCommPopupMenuUI implementation.
 * 
 * @author Yana Stamcheva
 */
public class SIPCommPopupMenuUI extends BasicPopupMenuUI
{
    /**
     * Creates a new SIPCommPopupMenuUI instance.
     */
    public static ComponentUI createUI(JComponent c) {
        return new SIPCommPopupMenuUI();
    }

    public void paint(Graphics g, JComponent c) {
        AntialiasingManager.activateAntialiasing(g);
        
        super.paint(g, c);
    }
}
