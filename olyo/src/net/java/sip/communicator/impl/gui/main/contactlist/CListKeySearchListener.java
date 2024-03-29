/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package net.java.sip.communicator.impl.gui.main.contactlist;

import java.awt.event.*;
import javax.swing.text.*;

import net.java.sip.communicator.service.contactlist.*;

/**
 * The main goal of the <tt>CListKeySearchListener</tt> is to listen for key
 * events and to search the ContactList when a key is typed over it. It selects
 * the Contact name closest to the typed string. 
 * 
 * The time between two button presses is checked to determine whether the user
 * makes a new search or a continious search. When user types the same letter
 * consecutively the search mechanism selects the next Contact name starting
 * with the same letter.
 * 
 * The <tt>CListKeySearchListener</tt> is added to the <tt>MainFrame</tt> and
 * the <tt>ContactListPanel</tt> to provide a search functionality over the
 * contact list when one of them is focused.
 * 
 * The 'space' key, the '+' and the '-' keys are proccess seperately to provide
 * another functionality completely different from the search. When user types
 * a '-' or 'space' and there's currently a group selected, the selected group
 * is closed. When user types '+' the selected group is opened.
 *  
 * @author Yana Stamcheva
 */
public class CListKeySearchListener implements KeyListener {

    private ContactList contactList;

    private String lastTypedKey;

    private long lastTypedTimestamp = 0;

    private StringBuffer keyBuffer = new StringBuffer();

    /**
     * Creates an instance of CListKeySearchListener for the given
     * ContactList.
     * @param contactList The contact list.
     */
    public CListKeySearchListener(ContactList contactList) {
        this.contactList = contactList;
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    /**
     * Searches the contact list when any key, different from "space", "+" or
     * "-" is typed. Selects the Contact name closest to the typed string. The
     * time between two button presses is checked to determine whether the user
     * makes a new search or a continious search. When user types the same
     * letter consecutively the search mechanism selects the next Contact name
     * starting with the same letter.
     */
    public void keyTyped(KeyEvent e) {
        
        //Nothing to do if the contact list is empty
        if(contactList.getModel().getSize() <= 0)
            return;
        
        long eventTimestamp = e.getWhen();
        String keyChar = String.valueOf(e.getKeyChar());

        if(e.getKeyChar() == ' ') {
            closeGroup();
        }
        else if(e.getKeyChar() == '+') {
            openGroup();
        }
        else if(e.getKeyChar() == '-') {
            closeGroup();
        }
        else {
            
            if ((lastTypedTimestamp - eventTimestamp) > 1000) {
                keyBuffer.delete(0, keyBuffer.length() - 1);
            }
            this.lastTypedTimestamp = eventTimestamp;
            this.keyBuffer.append(keyChar);

            boolean selectedSameLetterContact = false;

            int selectedIndex = this.contactList.getSelectedIndex();

            // Check if there's any selected contact node and get its name.
            if (selectedIndex != -1) {
                Object selectedObject = this.contactList.getSelectedValue();

                if (selectedObject instanceof MetaContact) {
                    String selectedContactName = ((MetaContact) selectedObject)
                            .getDisplayName();

                    if (selectedContactName != null) {
                        selectedSameLetterContact 
                            = selectedContactName.substring(0, 1)
                                .equalsIgnoreCase(keyBuffer.toString());
                    }
                }
            }
            // The search starts from the beginning if:
            // 1) the newly entered character is different from the last one
            // or
            // 2) the currently selected contact starts with a different letter
            int contactIndex = -1;
            if (lastTypedKey != keyChar || !selectedSameLetterContact) {
                contactIndex = this.contactList.getNextMatch(
                        keyBuffer.toString(), 0, Position.Bias.Forward);
            } else {
                contactIndex = this.contactList.getNextMatch(
                        keyBuffer.toString(),
                        selectedIndex + 1, Position.Bias.Forward);
            }

            int currentlySelectedIndex = this.contactList.getSelectedIndex();

            if (currentlySelectedIndex != contactIndex && contactIndex != -1) {
                this.contactList.setSelectedIndex(contactIndex);
            }

            this.contactList.ensureIndexIsVisible(currentlySelectedIndex);

            this.lastTypedKey = keyChar;
        }        
    }
        
    /**
     * Closes a group when it's opened.
     */    
    public void closeGroup() {            
        Object selectedValue = this.contactList.getSelectedValue();
        
        if (selectedValue instanceof MetaContactGroup) {
            MetaContactGroup group = (MetaContactGroup) selectedValue;
            
            ContactListModel model 
                = (ContactListModel)contactList.getModel();
            
            if (!model.isGroupClosed(group)) {
                model.closeGroup(group);
            }
        }
    }
    
    
    /**
     * Opens a group when it's closed.
     */    
    public void openGroup() {
        Object selectedValue = this.contactList.getSelectedValue();
        
        if (selectedValue instanceof MetaContactGroup) {
            MetaContactGroup group = (MetaContactGroup) selectedValue;
            
            ContactListModel model 
                = (ContactListModel) contactList.getModel();
            
            if (model.isGroupClosed(group)) {
                model.openGroup(group);
            }
        }
    }
    
}