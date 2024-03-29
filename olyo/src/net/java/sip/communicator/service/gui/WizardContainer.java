/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.service.gui;

/**
 * The <tt>WizardContainer</tt> is a base wizard interface that allows to
 * control the wizard buttons. It is extended by the
 * <tt>AccountRegistrationWizardContainer</tt> and for now is used only there.
 * In spite of this fact it's defined in a different interface, because of
 * the more general character of the methods. It could be extended in the
 * future to make a complete wizard interface.
 * 
 * @author Yana Stamcheva
 */
public interface WizardContainer {
    
    /**
     * Returns TRUE if "Back" wizard button is enabled, FALSE otherwise.
     * @return TRUE if "Back" wizard button is enabled, FALSE otherwise.
     */
    public boolean isBackButtonEnabled();
    
    /**
     * Sets the "Back" wizard button enabled or disabled.
     * @param newValue TRUE to enable the "Back" wizard button, FALSE to
     * disable it.
     */
    public void setBackButtonEnabled(boolean newValue);
    
    /**
     * Returns TRUE if "Next" or "Finish" wizard button is enabled, FALSE
     * otherwise.
     * @return TRUE if "Next" or "Finish" wizard button is enabled, FALSE
     * otherwise.
     */
    public boolean isNextFinishButtonEnabled();
    
    /**
     * Sets the "Next" or "Finish" wizard button enabled or disabled.
     * @param newValue TRUE to enable the "Next" or "Finish" wizard button,
     * FALSE to disable it.
     */
    public void setNextFinishButtonEnabled(boolean newValue);
    
    /**
     * Returns TRUE if "Cancel" wizard button is enabled, FALSE otherwise.
     * @return TRUE if "Cancel" wizard button is enabled, FALSE otherwise.
     */
    public boolean isCancelButtonEnabled();
    
    /**
     * Sets the "Cancel" wizard button enabled or disabled.
     * @param newValue TRUE to enable the "Cancel" wizard button, FALSE to
     * disable it.
     */
    public void setCancelButtonEnabled(boolean newValue);
    
    /**
     * Refreshes the current content of this wizard container.
     */
    public void refresh();
}
