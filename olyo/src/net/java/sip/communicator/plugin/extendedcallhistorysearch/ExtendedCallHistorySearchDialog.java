/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package net.java.sip.communicator.plugin.extendedcallhistorysearch;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import net.java.sip.communicator.service.callhistory.*;

import com.toedter.calendar.*;

/**
 * The <tt>ExtendedCallHistorySearchDialog</tt> allows to search in call
 * history records, by specifying a period, or a call participant name, or type
 * of the call (incoming or outgoing).
 * 
 * @author Maxime Bourdon & Thomas Meyer
 */
public class ExtendedCallHistorySearchDialog
    extends JDialog
    implements  ActionListener,
                ItemListener
{
    /* PANEL */
    private JPanel mainSearchPanel = new JPanel(new BorderLayout());

    private JPanel mainPanel = new JPanel(new BorderLayout(3, 1));

    private JPanel searchPanel = new JPanel(new GridBagLayout());

    private JPanel callTypePanel = new JPanel(new GridBagLayout());

    private JPanel callListResultPanel = new JPanel(new BorderLayout());

    /* BUTTON */
    private JButton searchButton = new JButton(Resources.getString("search"),
        Resources.getImage("searchIcon"));

    /* TEXT FIELD */
    private JTextField contactNameField = new JTextField();

    /* LABEL */
    private JLabel contactNameLabel = new JLabel(Resources
        .getString("contactName") + ": ");

    private JLabel sinceDateLabel
        = new JLabel(Resources.getString("since") + ": ");

    private JLabel untilDateLabel
        = new JLabel(Resources.getString("until") + ": ");

    private JLabel callTypeLabel
        = new JLabel(Resources.getString("callType") + ": ");

    /* CHECKBOX */
    private JCheckBox inCheckBox = new JCheckBox(Resources
        .getString("incoming"), true);

    private JCheckBox outCheckBox = new JCheckBox(Resources
        .getString("outgoing"), true);

    /* SCROLL PANE */
    private JScrollPane scrollPane = new JScrollPane();

    /* contraint grid */
    private GridBagConstraints constraintsGRbag = new GridBagConstraints();

    Collection participants = null;

    private CallList callList = new CallList();

    /* Service */
    CallHistoryService callAccessService;

    private Date lastDateFromHistory = null;

    private Collection callListCollection;

    private JDateChooser untilDC
        = new JDateChooser("dd/MM/yyyy", "##/##/####", ' ');

    private JDateChooser sinceDC
        = new JDateChooser("dd/MM/yyyy", "##/##/####", ' ');

    private int direction;
    
    /**
     * Creates a new instance of <tt>ExtendedCallHistorySearchDialog</tt>.
     */
    public ExtendedCallHistorySearchDialog()
    {
        this.mainPanel.setPreferredSize(new Dimension(650, 550));

        this.setTitle(Resources.getString("advancedCallHistorySearch"));

        this.initPanels();

        this.initDateChooser();

        this.searchPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Search"), BorderFactory
                .createEmptyBorder(5, 5, 5, 5)));

        this.callTypePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(""), BorderFactory
                .createEmptyBorder(5, 5, 5, 5)));

        this.callListResultPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(""), BorderFactory
                .createEmptyBorder(5, 5, 5, 5)));

        this.getContentPane().add(mainPanel);
        this.pack();

        /* action listener */
        searchButton.addActionListener(this);
        inCheckBox.addItemListener(this);
        outCheckBox.addItemListener(this);
        
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    /**
     * Loads all calls from history and shows them in this dialog.
     */
    public void loadHistoryCalls()
    {
        /* get the call list collection */
        callAccessService = ExtendedCallHistorySearchActivator
            .getCallHistoryService();
        callListCollection = callAccessService.findByEndDate(new Date());
        loadTableRecords(
            callListCollection, Constants.INOUT_CALL, null, new Date());
    }
    
    /**
     * Initialize the "until" date field to the current date.
     */
    private void initDateChooser()
    {        
        untilDC.getJCalendar().setWeekOfYearVisible(false);
        untilDC.getJCalendar().setDate(new Date());
        sinceDC.getJCalendar().setWeekOfYearVisible(false);
    }

    /**
     * Init panels display.
     */
    private void initPanels()
    {
        this.getRootPane().setDefaultButton(searchButton);
        
        this.mainSearchPanel.add(searchPanel, BorderLayout.NORTH);
        this.mainSearchPanel.add(callTypePanel, BorderLayout.CENTER);

        this.mainPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.mainPanel.add(mainSearchPanel, BorderLayout.NORTH);
        this.mainPanel.add(callListResultPanel, BorderLayout.CENTER);

        /* SEARCH PANEL */
        constraintsGRbag.anchor = GridBagConstraints.WEST;
        constraintsGRbag.insets = new Insets(5, 5, 5, 5);
        constraintsGRbag.gridwidth = 1;
        constraintsGRbag.fill = GridBagConstraints.NONE;
        this.searchPanel.add(contactNameLabel, constraintsGRbag);

        constraintsGRbag.gridwidth = GridBagConstraints.REMAINDER;
        constraintsGRbag.fill = GridBagConstraints.HORIZONTAL;
        constraintsGRbag.weightx = 1.0;
        constraintsGRbag.gridx = GridBagConstraints.RELATIVE;
        this.searchPanel.add(contactNameField, constraintsGRbag);

        /* DATE */
        constraintsGRbag.anchor = GridBagConstraints.WEST;
        constraintsGRbag.gridwidth = 1;
        constraintsGRbag.gridy = 2;
        constraintsGRbag.fill = GridBagConstraints.NONE;
        this.searchPanel.add(sinceDateLabel, constraintsGRbag);

        constraintsGRbag.gridwidth = GridBagConstraints.RELATIVE;
        constraintsGRbag.fill = GridBagConstraints.HORIZONTAL;
        constraintsGRbag.weightx = 1.0;
        this.searchPanel.add(sinceDC, constraintsGRbag);

        constraintsGRbag.gridy = 3;
        constraintsGRbag.gridwidth = 1;
        constraintsGRbag.fill = GridBagConstraints.NONE;
        this.searchPanel.add(untilDateLabel, constraintsGRbag);

        constraintsGRbag.fill = GridBagConstraints.HORIZONTAL;
        this.searchPanel.add(untilDC, constraintsGRbag);

        /* BUTTON */
        constraintsGRbag.gridy = 4;
        constraintsGRbag.gridx = 3;
        constraintsGRbag.fill = GridBagConstraints.NONE;
        constraintsGRbag.anchor = GridBagConstraints.EAST;
        this.searchPanel.add(searchButton, constraintsGRbag);

        /* CALL TYPE */
        constraintsGRbag.anchor = GridBagConstraints.WEST;
        constraintsGRbag.insets = new Insets(5, 5, 5, 5);
        constraintsGRbag.gridwidth = 1;
        constraintsGRbag.gridx = 1;
        constraintsGRbag.gridy = 1;
        this.callTypePanel.add(callTypeLabel, constraintsGRbag);
        constraintsGRbag.gridx = 2;
        this.callTypePanel.add(inCheckBox, constraintsGRbag);
        constraintsGRbag.gridx = 3;
        this.callTypePanel.add(outCheckBox, constraintsGRbag);

        /* CALL LIST PANEL */
        this.scrollPane.getViewport().add(callList);
        this.callListResultPanel.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Loads the appropriate history calls when user clicks on the search
     * button.
     */
    public void actionPerformed(ActionEvent e)
    {
        JButton sourceButton = (JButton) e.getSource();

        if (sourceButton.equals(searchButton))
        {   
            /* update the callList */
            new Thread()
            {
                public void run()
                {   
                    callListCollection = callAccessService
                        .findByEndDate(new Date());
                    
                    if (inCheckBox.isSelected() && outCheckBox.isSelected())
                    {
                        direction = Constants.INOUT_CALL;
                    }
                    else
                    {
                        if (inCheckBox.isSelected())
                            direction = Constants.INCOMING_CALL;
                        else
                        {
                            if (outCheckBox.isSelected())
                                direction = Constants.OUTGOING_CALL;
                            else
                                direction = Constants.INOUT_CALL;
                        }
                    }

                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            loadTableRecords(callListCollection, direction,
                                sinceDC.getDate(), untilDC.getDate());
                        }
                    });
                }
            }.start();
        }
    }

    /**
     * Remove or add calls to the list of calls depending on the state of
     * incoming and outgoing checkboxes.
     */
    public void itemStateChanged(ItemEvent e)
    {
        JCheckBox sourceCheckBox = (JCheckBox) e.getSource();

        /* Incoming checkbox */
        if (sourceCheckBox.equals(inCheckBox)
            || sourceCheckBox.equals(outCheckBox))
        {
            /* INCOMING box Checked */
            if (inCheckBox.isSelected())
            {
                /* OUTCOMING box checked */
                if (outCheckBox.isSelected() == true)
                {
                    loadTableRecords(callListCollection,
                        Constants.INOUT_CALL,
                        sinceDC.getDate(), untilDC.getDate());
                }
                // only incoming is checked
                else
                {
                    loadTableRecords(callListCollection,
                        Constants.INCOMING_CALL,
                        sinceDC.getDate(), untilDC.getDate());
                }
            }
            /* check the OUTCOMING box */
            else
            {
                // checked
                if (outCheckBox.isSelected() == true)
                {
                    loadTableRecords(callListCollection,
                        Constants.OUTGOING_CALL,
                        sinceDC.getDate(), untilDC.getDate());
                }
                /* both are unchecked */
                else
                    loadTableRecords(callListCollection,
                        Constants.INOUT_CALL,
                        sinceDC.getDate(), untilDC.getDate());
            }
        }
    }

    private String processDate(Date date)
    {
        String resultString;
        long currentDate = System.currentTimeMillis();

        if (GuiUtils.compareDates(date, new Date(currentDate)) == 0)
        {

            resultString = Resources.getString("today");
        }
        else
        {
            Calendar c = Calendar.getInstance();
            c.setTime(date);

            resultString = GuiUtils.formatDate(date);
        }

        return resultString;
    }

    /**
     * Check if the callRecord direction equals the direction wanted by the user
     * 
     * @param callType integer value (incoming = 1, outgoing = 2, in/out = 3)
     * @param callrecord A Callrecord
     * @return A string containing the callRecord direction if it equals the
     *         callType Call, null if not
     */
    private String checkCallType(int callType, CallRecord callRecord)
    {
        String direction = null;

        // in
        if (callRecord.getDirection().equals(CallRecord.IN)
            && ((callType == Constants.INCOMING_CALL)
                || callType == Constants.INOUT_CALL))
            direction = GuiCallParticipantRecord.INCOMING_CALL;
        // out
        else if (callRecord.getDirection().equals(CallRecord.OUT)
            && (callType == Constants.OUTGOING_CALL
                || callType == Constants.INOUT_CALL))
            direction = GuiCallParticipantRecord.OUTGOING_CALL;

        return direction;
    }

    /**
     * Check if sinceDate <= callStartDate <= beforeDate
     * 
     * @param callStartDate
     * @param sinceDate
     * @param beforeDate
     * @return true if sinceDate <= callStartDate <= beforeDate false if not
     */
    private boolean checkDate(Date callStartDate, Date sinceDate, Date untilDate)
    {
        /* Test callStartDate >= sinceDate */
        if (sinceDate != null)
            if (callStartDate.before(sinceDate))
                return false;

        /* Test callStartDate <= beforeDate */
        if (untilDate != null)
            if (callStartDate.after(untilDate))
                return false;

        /* sinceDate <= callStartDate <= beforeDate */
        return true;
    }

    /**
     * Loads the collection of call records in the table.
     * 
     * @param historyCalls the collection of call records
     * @param calltype the type of the call - could be incoming or outgoing
     * @param since the start date of the search
     * @param before the end date of the search
     */
    private void loadTableRecords(Collection historyCalls, int calltype,
        Date since, Date before)
    {
        boolean addMe = true;
        lastDateFromHistory = null;
        callList.removeAll();
        // callList = new CallList();
        Iterator lastCalls = historyCalls.iterator();

        while (lastCalls.hasNext())
        {
            addMe = true;

            CallRecord callRecord = (CallRecord) lastCalls.next();

            /* DATE Checking */
            Date callStartDate = callRecord.getStartTime();

            if (checkDate(callStartDate, since, before))
            {
                if (lastDateFromHistory == null)
                {
                    callList.addItem(processDate(callStartDate));
                    lastDateFromHistory = callStartDate;
                }
                else
                {
                    int compareResult = GuiUtils.compareDates(callStartDate,
                        lastDateFromHistory);

                    if (compareResult != 0)
                    {
                        callList.addItem(processDate(callStartDate));
                        lastDateFromHistory = callStartDate;
                    }
                }
            }
            else
                addMe = false;

            /* PARTICIPANTS Checking */
            if (addMe)
            {
                Iterator participants = callRecord.getParticipantRecords()
                    .iterator();

                while (participants.hasNext() && addMe)
                {
                    CallParticipantRecord participantRecord
                        = (CallParticipantRecord) participants.next();

                    String participantName = participantRecord
                        .getParticipantAddress();

                    if (participantName.matches(
                        "(?i).*" + contactNameField.getText() + ".*"))
                    {
                        /* DIRECTION Checking */
                        String direction;
                        direction = checkCallType(calltype, callRecord);

                        if (direction != null)
                            callList.addItem(new GuiCallParticipantRecord(
                                participantRecord, direction));
                        else
                            addMe = false;
                    }
                    else
                        addMe = false; // useless
                }
            }
        }

        if (callList.getModel().getSize() > 0)
            callList.addItem(Resources.getString("olderCalls") + "...");
    }
}
