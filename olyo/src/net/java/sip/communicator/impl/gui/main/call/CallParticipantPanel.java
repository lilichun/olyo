/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.gui.main.call;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.Timer;

import net.java.sip.communicator.impl.gui.i18n.*;
import net.java.sip.communicator.impl.gui.utils.*;
import net.java.sip.communicator.service.protocol.*;

/**
 * The <tt>CallParticipantPanel</tt> is the panel containing data for a call
 * participant in a given call. It contains information like call participant
 * name, photo, call duration, etc. 
 * 
 * @author Yana Stamcheva
 */
public class CallParticipantPanel
    extends JPanel
    implements ContainerListener
{
    private JLayeredPane contactPanel = new JLayeredPane();
    
    private JPanel namePanel = new JPanel(new GridLayout(0, 1));
    
    private JLabel nameLabel = new JLabel("", JLabel.CENTER);
    
    private JLabel stateLabel = new JLabel("Unknown", JLabel.CENTER);
    
    private JLabel timeLabel = new JLabel("00:00:00", JLabel.CENTER);
    
    private JLabel photoLabel = new JLabel(new ImageIcon(
            ImageLoader.getImage(ImageLoader.DEFAULT_USER_PHOTO)));
        
    private DialButton dialButton;
    
    private JPanel northPanel = new JPanel();
    
    private CallParticipantDialpadDialog dialpadDialog;
    
    private Date callStartTime;
    
    private Date conversationStartTime;
    
    private Date callDuration;
    
    private Timer timer;
    
    private String callType;
    
    private String participantName;
    
    private CallManager callManager;
    
    private CallParticipant callParticipant;
    
    /**
     * Creates a <tt>CallParticipantPanel</tt> for the given call participant.
     * 
     * @param callManager the <tt>CallManager</tt> that manages the call
     * @param callParticipant a call participant
     */
    public CallParticipantPanel(CallManager callManager,
        CallParticipant callParticipant)
    {   
        this(callManager, callParticipant.getAddress());
        
        this.callParticipant = callParticipant;
        
        this.stateLabel.setText(callParticipant.getState().getStateString());
        
        dialButton = new DialButton(callManager,
            new ImageIcon(ImageLoader.getImage(ImageLoader.DIAL_BUTTON)));
        
        dialButton.setBounds(94, 74, 36, 36);
        
        contactPanel.add(dialButton, new Integer(1));
    }
    
    /**
     * Creates a <tt>CallParticipantPanel</tt> for the given participant name.
     * 
     * @param callManager the <tt>CallManager</tt> that manages the call
     * @param participantName a string representing the participant name
     */
    public CallParticipantPanel(CallManager callManager, String participantName)
    {   
        super(new BorderLayout());
        
        this.callManager = callManager;
        
        // Initialize the call start time. This date is meant to be used in
        // the GuiCallParticipantRecord, which is added to the CallList after
        // a call.        
        this.callStartTime = new Date(System.currentTimeMillis());
        
        this.participantName = participantName;
        
        this.nameLabel.setText(participantName);
        
        this.timer = new Timer(1000, new CallTimerListener());
        this.timer.setRepeats(true);
        
        //Initialize the date to 0
        //Need to use Calendar because new Date(0) retuns a date where the
        //hour is intialized to 1.
        Calendar c = Calendar.getInstance();
        c.set(0, 0, 0, 0, 0, 0);
        this.callDuration = c.getTime();
        
        namePanel.add(nameLabel);
        namePanel.add(stateLabel);
        namePanel.add(timeLabel);

        contactPanel.setPreferredSize(new Dimension(130, 150));
        
        photoLabel.setBounds(20, 0, 90, 100);
        namePanel.setBounds(0, 110, 130, 40);
        
        contactPanel.add(photoLabel, new Integer(0));
        contactPanel.add(namePanel, new Integer(0));
        
        northPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        northPanel.add(contactPanel);
        
        this.add(northPanel, BorderLayout.NORTH);        
    }
    
    /**
     * Sets the state of the contained call participant.
     * @param state the state of the contained call participant
     */
    public void setState(String state)
    {
        this.stateLabel.setText(state);
    }
    
    /**
     * Starts the timer that counts call duration.
     */
    public void startCallTimer()
    {
        this.conversationStartTime = new Date(System.currentTimeMillis());
        this.timer.start();
    }
    
    /**
     * Stops the timer that counts call duration.
     */
    public void stopCallTimer()
    {
        this.timer.stop();
    }

    /**
     * Each second refreshes the time label to show to the user the exact
     * duration of the call.
     */
    private class CallTimerListener implements ActionListener
    {   
        public void actionPerformed(ActionEvent e)
        {
            Date time = GuiUtils.substractDates(
                    new Date(System.currentTimeMillis()),
                    conversationStartTime);
            
            callDuration.setTime(time.getTime());
            
            timeLabel.setText(GuiUtils.formatTime(time));
        }
    }

    /**
     * Returns the start time of the conversation. If no conversation was made
     * will return null.
     * 
     * @return the start time of the conversation
     */
    public Date getConversationStartTime()
    {
        return conversationStartTime;
    }
    
    /**
     * Returns the start time of the contained participant call. Note that the
     * start time of the call is different from the conversation start time.
     * For example if we receive a call, the call start time is when the call
     * is received and the conversation start time would be when we accept the
     * call.
     * 
     * @return the start time of the contained participant call
     */
    public Date getCallStartTime()
    {        
        return callStartTime;
    }
    
    /**
     * Returns the duration of the contained participant call.
     * 
     * @return the duration of the contained participant call
     */
    public Date getCallDuration()
    {
        return callDuration;
    }
    
    /**
     * Returns this call type - GuiCallParticipantRecord: INCOMING_CALL
     * or OUTGOING_CALL
     * @return Returns this call type : INCOMING_CALL or OUTGOING_CALL
     */
    public String getCallType()
    {
        if(callDuration != null)
            return callType;
        else
            return GuiCallParticipantRecord.INCOMING_CALL;
    }
    
    /**
     * Sets the type of the call. Call type could be
     * <tt>GuiCallParticipantRecord.INCOMING_CALL</tt> or
     * <tt>GuiCallParticipantRecord.INCOMING_CALL</tt>.
     * 
     * @param callType the type of call to set
     */
    public void setCallType(String callType)
    {
        this.callType = callType;
    }
    
    /**
     * Returns the name of the participant, contained in this panel.
     * 
     * @return the name of the participant, contained in this panel
     */
    public String getParticipantName()
    {
        return participantName;
    }
    
    /**
     * Customized button for dialpad.
     */
    private class DialButton extends JButton
    {
        private CallManager callManager;
        
        public DialButton(CallManager manager, ImageIcon iconImage)
        {
            super(iconImage);
        
            this.callManager = manager;
            
            this.setOpaque(false);
            
            this.setToolTipText(Messages.getI18NString("dialpad").getText());
            
            dialpadDialog
                = new CallParticipantDialpadDialog(callManager, callParticipant);
            
            this.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {                    
                    if(!dialpadDialog.isVisible())
                    {
                        dialpadDialog.setSize(
                            callManager.getMainFrame().getWidth() - 20, 200);
                        
                        dialpadDialog.setLocation(
                            callManager.getMainFrame().getX() + 10,
                            getLocationOnScreen().y + getHeight());
                        
                        dialpadDialog.setVisible(true);
                    }
                    else
                    {   
                        dialpadDialog.setVisible(false);
                    }
                }
            });
        }
    }

    public void componentAdded(ContainerEvent e)
    {}

    /**
     * Remove all dialogs open by this participant panel.
     */
    public void componentRemoved(ContainerEvent e)
    {   
        this.removeDialogs();
        
        e.getContainer().removeContainerListener(this);        
    }
    
    /**
     * Removes all dialogs open by this participant panel.
     */
    public void removeDialogs()
    {
        if(dialpadDialog != null && dialpadDialog.isVisible())
            dialpadDialog.setVisible(false);        
    }
}
