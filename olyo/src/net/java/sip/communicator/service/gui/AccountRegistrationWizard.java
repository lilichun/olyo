/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.service.gui;

import java.awt.*;
import java.util.*;

import net.java.sip.communicator.service.protocol.*;

/**
 * The <tt>AccountRegistrationWizard</tt> is meant to provide a wizard which
 * will guide the user through a protocol account registration. Each
 * <tt>AccountRegistrationWizard</tt> should provide a set of
 * <tt>WizardPage</tt>s, an icon, the name and the description of the
 * corresponding protocol.
 * <p>
 * Note that the <tt>AccountRegistrationWizard</tt> is NOT a real wizard, it
 * doesn't handle wizard events. Each UI Service implementation should provide
 * its own wizard UI control, which should manage all the events, panels and
 * buttons, etc.
 * <p>
 * It depends on the wizard implementation in the UI for whether or not a
 * summary will be shown to the user before "Finish".
 * 
 * @author Yana Stamcheva
 */
public interface AccountRegistrationWizard {

    /**
     * Returns the protocol icon that will be shown on the left of the protocol
     * name in the list, where user will choose the protocol to register to.
     * 
     * @return a short description of the protocol.
     */
    public byte[] getIcon();
    
    /**
     * Returns the image that will be shown on the left of the wizard pages.
     * @return the image that will be shown on the left of the wizard pages
     */
    public byte[] getPageImage();
    
    /**
     * Returns the protocol name that will be shown in the list, where user
     * will choose the protocol to register to.
     * 
     * @return the protocol name.
     */
    public String getProtocolName();
    
    /**
     * Returns a short description of the protocol that will be shown on the
     * right of the protocol name in the list, where user will choose the
     * protocol to register to.
     * 
     * @return a short description of the protocol.
     */
    public String getProtocolDescription();

    /**
     * Loads all data concerning the given <tt>ProtocolProviderService</tt>.
     * This method is meant to be used when a modification in an already
     * created account is needed.
     * 
     * @param protocolProvider The <tt>ProtocolProviderService</tt> to
     * load data from.
     */
    public void loadAccount(ProtocolProviderService protocolProvider);
    
    /**
     * Returns the set of <tt>WizardPage</tt>-s for this
     * wizard. 
     * 
     * @return the set of <tt>WizardPage</tt>-s for this
     * wizard. 
     */
    public Iterator getPages();

    /**
     * Returns the identifier of the first account registration wizard page.
     * This method is meant to be used by the wizard container to determine,
     * which is the first page to show to the user.
     * 
     * @return the identifier of the first account registration wizard page
     */
    public Object getFirstPageIdentifier();

    /**
     * Returns the identifier of the last account registration wizard page. This
     * method is meant to be used by the wizard container to determine which is
     * the page to show before the summary page (of course if there's a summary).
     * 
     * @return the identifier of the last account registration wizard page
     */
    public Object getLastPageIdentifier();

    /**
     * Returns a set of key-value pairs that will represent the summary for
     * this wizard.
     * 
     * @return a set of key-value pairs that will represent the summary for
     * this wizard. 
     */
    public Iterator getSummary();
    
    /**
     * Defines the operations that will be executed when the user clicks on
     * the wizard "Finish" button.
     * 
     */
    public ProtocolProviderService finish();
    
    /**
     * Returns the preferred dimensions of this wizard.
     * 
     * @return the preferred dimensions of this wizard.
     */
    public Dimension getSize();

    /**
     * Sets the modification property to indicate if this wizard is opened for
     * a modification.
     * 
     * @param isModification indicates if this wizard is opened for modification
     * or for creating a new account. 
     */
    public void setModification(boolean isModification);

    /**
     * Indicates if this wizard is modifying an existing account or is creating
     * a new one.
     * 
     * @return <code>true</code> to indicate that this wizard is currently in
     * modification mode, <code>false</code> - otherwise.
     */
    public boolean isModification();
}
