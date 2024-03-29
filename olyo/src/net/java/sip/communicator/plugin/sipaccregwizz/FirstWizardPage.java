/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 * 
 * Distributable under LGPL license. See terms of license at gnu.org.
 */
package net.java.sip.communicator.plugin.sipaccregwizz;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import net.java.sip.communicator.service.gui.*;
import net.java.sip.communicator.service.protocol.*;

/**
 * The <tt>FirstWizardPage</tt> is the page, where user could enter the uin
 * and the password of the account.
 * 
 * @author Yana Stamcheva
 * @author Damian Minkov
 * @author Zheng Wei
 * @author Dong Fengyu
 */
public class FirstWizardPage
    extends JPanel
    implements WizardPage, DocumentListener, ItemListener
{
    public static final String FIRST_PAGE_IDENTIFIER = "FirstPageIdentifier";

    private static String DEFAULT_PORT = "5060";

    private static String DEFAULT_TLS_PORT = "5061";

    private static String DEFAULT_TRANSPORT = "UDP";

    private static String DEFAULT_POLL_PERIOD = "30";

    private static String DEFAULT_SUBSCRIBE_EXPIRES = "3600";

    private static String DEFAULT_KEEP_ALIVE_INTERVAL = "25";

    private JPanel firstTabPanel = new JPanel(new BorderLayout());

    private JPanel uinPassPanel = new JPanel(new BorderLayout(10, 10));

    private JPanel labelsPanel = new JPanel();

    private JPanel valuesPanel = new JPanel();

    private JLabel uinLabel = new JLabel(Resources.getString("uin"));
		

    private JLabel passLabel = new JLabel(Resources.getString("password"));

	

    private JPanel emptyPanel = new JPanel();

    private JLabel uinExampleLabel = new JLabel("Ex: john@voiphone.net");

    private JLabel existingAccountLabel =
        new JLabel(Resources.getString("existingAccount"));

    private JTextField uinField = new JTextField();

    private JPasswordField passField = new JPasswordField();


    private JCheckBox rememberPassBox =
        new JCheckBox(Resources.getString("rememberPassword"));
 

    private JPanel advancedOpPanel = new JPanel(new BorderLayout(10, 10));

    private JPanel labelsAdvOpPanel = new JPanel(new GridLayout(0, 1, 10, 10));

    private JPanel valuesAdvOpPanel = new JPanel(new GridLayout(0, 1, 10, 10));

    private JCheckBox enableAdvOpButton =
        new JCheckBox(Resources.getString("ovverideServerOps"), false);

    private JLabel serverLabel = new JLabel(Resources.getString("registrar"));

    private JLabel proxyLabel = new JLabel(Resources.getString("proxy"));

    private JLabel serverPortLabel =
        new JLabel(Resources.getString("serverPort"));

    private JLabel proxyPortLabel =
        new JLabel(Resources.getString("proxyPort"));

    private JLabel transportLabel =
        new JLabel(Resources.getString("preferredTransport"));

    private JTextField serverField = new JTextField();

    private JTextField proxyField = new JTextField();

    private JTextField serverPortField = new JTextField(DEFAULT_PORT);

    private JTextField proxyPortField = new JTextField(DEFAULT_PORT);

    private JComboBox transportCombo = new JComboBox(new Object[]
    { "UDP", "TLS", "TCP" });

    private JPanel presenceOpPanel = new JPanel(new BorderLayout(10, 10));

    private JPanel buttonsPresOpPanel =
        new JPanel(new GridLayout(0, 1, 10, 10));

    private JPanel labelsPresOpPanel = new JPanel(new GridLayout(0, 1, 10, 10));

    private JPanel valuesPresOpPanel = new JPanel(new GridLayout(0, 1, 10, 10));

    private JCheckBox enablePresOpButton =
        new JCheckBox(Resources.getString("enablePresence"), true);

    private JCheckBox forceP2PPresOpButton =
        new JCheckBox(Resources.getString("forceP2PPresence"), true);

/**
  *Added by Zheng Wei
  *Added by Dong Fengyu
  */
     private JPanel checkBoxPanel = new JPanel(new BorderLayout(10, 10));

     private JCheckBox enableP2PSIPBox =
        new JCheckBox(Resources.getString("enableP2PSIP"),true);


     private JCheckBox enablePassCheckBox =
	 new JCheckBox(Resources.getString("enablePassCheck"),false);


	

    private JLabel pollPeriodLabel =
        new JLabel(Resources.getString("offlineContactPollingPeriod"));

    private JLabel subscribeExpiresLabel =
        new JLabel(Resources.getString("subscriptionExpiration"));

    private JTextField pollPeriodField = new JTextField(DEFAULT_POLL_PERIOD);

    private JTextField subscribeExpiresField =
        new JTextField(DEFAULT_SUBSCRIBE_EXPIRES);

    private JPanel keepAlivePanel = new JPanel(new BorderLayout(10, 10));

    private JPanel keepAliveLabels = new JPanel(new GridLayout(0, 1, 5, 5));

    private JPanel keepAliveValues = new JPanel(new GridLayout(0, 1, 5, 5));

    private JLabel keepAliveMethodLabel
        = new JLabel(Resources.getString("keepAliveMethod"));

    private JLabel keepAliveIntervalLabel
        = new JLabel(Resources.getString("keepAliveInterval"));

    private JLabel keepAliveIntervalExampleLabel
        = new JLabel(Resources.getString("keepAliveIntervalEx"));

    private JComboBox keepAliveMethodBox
        = new JComboBox(new Object []
                                    {
                                        Resources.getString("register"),
                                        Resources.getString("options")
                                    });

    private JTextField keepAliveIntervalValue = new JTextField();

    private JTabbedPane tabbedPane = new JTabbedPane();

    private JPanel advancedPanel = new JPanel();

    private Object nextPageIdentifier = WizardPage.SUMMARY_PAGE_IDENTIFIER;

    private SIPAccountRegistrationWizard wizard;

    /**
     * Creates an instance of <tt>FirstWizardPage</tt>.
     * 
     * @param wizard the parent wizard
     */
    public FirstWizardPage(SIPAccountRegistrationWizard wizard)
    {
        super(new BorderLayout());

        this.wizard = wizard;

        advancedPanel.setLayout(new BoxLayout(advancedPanel, BoxLayout.Y_AXIS));

        this.init();

        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        this.labelsPanel
            .setLayout(new BoxLayout(labelsPanel, BoxLayout.Y_AXIS));

        this.valuesPanel
            .setLayout(new BoxLayout(valuesPanel, BoxLayout.Y_AXIS));
    }

    /**
     * Initializes all panels, buttons, etc.
     */
    private void init()
    {
        this.uinField.getDocument().addDocumentListener(this);
        this.transportCombo.addItemListener(this);
		

//Added by Dong Fengyu

        this.rememberPassBox.setSelected(false);
        this.rememberPassBox.setEnabled(false);


        existingAccountLabel.setForeground(Color.RED);

        this.uinExampleLabel.setForeground(Color.GRAY);
        this.uinExampleLabel.setFont(uinExampleLabel.getFont().deriveFont(8));
        this.emptyPanel.setMaximumSize(new Dimension(40, 35));
        this.uinExampleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8,
            0));

        labelsPanel.add(uinLabel);
        labelsPanel.add(emptyPanel);
        labelsPanel.add(passLabel);

        valuesPanel.add(uinField);
        valuesPanel.add(uinExampleLabel);
        valuesPanel.add(passField);

	

/**
  *Added by Zheng Wei
  *Added by Dong Fengyu
  */


        passField.setEnabled(false);

	 checkBoxPanel.add(rememberPassBox, BorderLayout.NORTH);

        checkBoxPanel.add(enablePassCheckBox, BorderLayout.CENTER);
		
        checkBoxPanel.add(enableP2PSIPBox, BorderLayout.SOUTH);

        uinPassPanel.add(labelsPanel, BorderLayout.WEST);
        uinPassPanel.add(valuesPanel, BorderLayout.CENTER);
	 uinPassPanel.add(checkBoxPanel, BorderLayout.SOUTH);

        uinPassPanel.setBorder(BorderFactory.createTitledBorder(Resources
            .getString("uinAndPassword")));

        firstTabPanel.add(uinPassPanel, BorderLayout.NORTH);

        tabbedPane.addTab(  Resources.getString("summary"),
                            firstTabPanel);

/*
  *Added by Dong Fengyu
 */


       enablePassCheckBox.addActionListener(new ActionListener()
	{
            public void actionPerformed(ActionEvent evt)
            {
                // Perform action
                JCheckBox cb = (JCheckBox) evt.getSource();
                rememberPassBox.setEnabled(cb.isSelected());
                passField.setEnabled(cb.isSelected());
            }
        });
	 




		

        serverField.setEnabled(false);
        serverPortField.setEnabled(false);
        proxyField.setEnabled(false);
        proxyPortField.setEnabled(false);
        transportCombo.setEnabled(false);	

        enableAdvOpButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                // Perform action
                JCheckBox cb = (JCheckBox) evt.getSource();

                if (!wizard.isModification())
                    serverField.setEnabled(cb.isSelected());

                serverPortField.setEnabled(cb.isSelected());
                proxyField.setEnabled(cb.isSelected());
                proxyPortField.setEnabled(cb.isSelected());
                transportCombo.setEnabled(cb.isSelected());

                if(!cb.isSelected())
                {
                    setServerFieldAccordingToUIN();

                    serverPortField.setText(DEFAULT_PORT);
                    proxyPortField.setText(DEFAULT_PORT);
                    transportCombo.setSelectedItem(DEFAULT_TRANSPORT);
                }
            }
        });

        transportCombo.setSelectedItem(DEFAULT_TRANSPORT);

        labelsAdvOpPanel.add(serverLabel);
        labelsAdvOpPanel.add(serverPortLabel);
        labelsAdvOpPanel.add(proxyLabel);
        labelsAdvOpPanel.add(proxyPortLabel);
        labelsAdvOpPanel.add(transportLabel);

        valuesAdvOpPanel.add(serverField);
        valuesAdvOpPanel.add(serverPortField);
        valuesAdvOpPanel.add(proxyField);
        valuesAdvOpPanel.add(proxyPortField);
        valuesAdvOpPanel.add(transportCombo);

        advancedOpPanel.add(enableAdvOpButton, BorderLayout.NORTH);
        advancedOpPanel.add(labelsAdvOpPanel, BorderLayout.WEST);
        advancedOpPanel.add(valuesAdvOpPanel, BorderLayout.CENTER);

        advancedOpPanel.setBorder(BorderFactory.createTitledBorder(Resources
            .getString("advancedOptions")));

        advancedPanel.add(advancedOpPanel);

        enablePresOpButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                // Perform action
                JCheckBox cb = (JCheckBox) evt.getSource();

                forceP2PPresOpButton.setEnabled(cb.isSelected());
                pollPeriodField.setEnabled(cb.isSelected());
                subscribeExpiresField.setEnabled(cb.isSelected());
            }
        });

        labelsPresOpPanel.add(pollPeriodLabel);
        labelsPresOpPanel.add(subscribeExpiresLabel);

        valuesPresOpPanel.add(pollPeriodField);
        valuesPresOpPanel.add(subscribeExpiresField);

        buttonsPresOpPanel.add(enablePresOpButton);
        buttonsPresOpPanel.add(forceP2PPresOpButton);

        presenceOpPanel.add(buttonsPresOpPanel, BorderLayout.NORTH);
        presenceOpPanel.add(labelsPresOpPanel, BorderLayout.WEST);
        presenceOpPanel.add(valuesPresOpPanel, BorderLayout.CENTER);

        presenceOpPanel.setBorder(BorderFactory.createTitledBorder(
            Resources.getString("presenceOptions")));

        advancedPanel.add(presenceOpPanel);

        JPanel emptyLabelPanel = new JPanel();
        emptyLabelPanel.setMaximumSize(new Dimension(40, 35));

        keepAliveLabels.add(keepAliveMethodLabel);
        keepAliveLabels.add(keepAliveIntervalLabel);
        keepAliveLabels.add(emptyLabelPanel);

        this.keepAliveIntervalExampleLabel.setForeground(Color.GRAY);
        this.keepAliveIntervalExampleLabel
            .setFont(uinExampleLabel.getFont().deriveFont(8));
        this.keepAliveIntervalExampleLabel
            .setMaximumSize(new Dimension(40, 35));
        this.keepAliveIntervalExampleLabel
            .setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        keepAliveIntervalValue.setText(DEFAULT_KEEP_ALIVE_INTERVAL);

        keepAliveValues.add(keepAliveMethodBox);
        keepAliveValues.add(keepAliveIntervalValue);
        keepAliveValues.add(keepAliveIntervalExampleLabel);

        keepAlivePanel.add(keepAliveLabels, BorderLayout.WEST);
        keepAlivePanel.add(keepAliveValues, BorderLayout.CENTER);

        keepAlivePanel.setBorder(BorderFactory.createTitledBorder(
            Resources.getString("keepAlive")));

        advancedPanel.add(keepAlivePanel);

        tabbedPane.addTab("Advanced", advancedPanel);

        this.add(tabbedPane, BorderLayout.NORTH);
    }

    /**
     * Implements the <code>WizardPage.getIdentifier</code> to return this
     * page identifier.
     */
    public Object getIdentifier()
    {
        return FIRST_PAGE_IDENTIFIER;
    }

    /**
     * Implements the <code>WizardPage.getNextPageIdentifier</code> to return
     * the next page identifier - the summary page.
     */
    public Object getNextPageIdentifier()
    {
        return nextPageIdentifier;
    }

    /**
     * Implements the <code>WizardPage.getBackPageIdentifier</code> to return
     * the next back identifier - the default page.
     */
    public Object getBackPageIdentifier()
    {
        return WizardPage.DEFAULT_PAGE_IDENTIFIER;
    }

    /**
     * Implements the <code>WizardPage.getWizardForm</code> to return this
     * panel.
     */
    public Object getWizardForm()
    {
        return this;
    }

    /**
     * Before this page is displayed enables or disables the "Next" wizard
     * button according to whether the UIN field is empty.
     */
    public void pageShowing()
    {
        this.setNextButtonAccordingToUIN();
    }

    /**
     * Saves the user input when the "Next" wizard buttons is clicked.
     */
    public void pageNext()
    {
        String uin = uinField.getText();
        int indexOfSeparator = uin.indexOf('@'); 
        if (indexOfSeparator > -1) {
            uin = uin.substring(0, indexOfSeparator);
        }
        
        String server = serverField.getText();

        if (!wizard.isModification() && isExistingAccount(uin, server))
        {
            nextPageIdentifier = FIRST_PAGE_IDENTIFIER;
            uinPassPanel.add(existingAccountLabel, BorderLayout.NORTH);
            this.revalidate();
        }
        else
        {
            nextPageIdentifier = SUMMARY_PAGE_IDENTIFIER;
            uinPassPanel.remove(existingAccountLabel);

            SIPAccountRegistration registration = wizard.getRegistration();

            registration.setUin(uinField.getText());

            if (passField.getPassword() != null)
                registration.setPassword(new String(passField.getPassword()));

            registration.setRememberPassword(rememberPassBox.isSelected());

            registration.setServerAddress(serverField.getText());
            registration.setServerPort(serverPortField.getText());
            registration.setProxy(proxyField.getText());
            registration.setProxyPort(proxyPortField.getText());
            registration.setPreferredTransport(transportCombo.getSelectedItem()
                .toString());

            registration.setEnablePresence(enablePresOpButton.isSelected());
            registration.setForceP2PMode(forceP2PPresOpButton.isSelected());

/**
  *
  *Added by Dong Fengyu
  */
            registration.setP2PSIP(enableP2PSIPBox.isSelected());

            registration.setPassCheck(enablePassCheckBox.isSelected());



            registration.setPollingPeriod(pollPeriodField.getText());
            registration.setSubscriptionExpiration(subscribeExpiresField
                .getText());
            registration.setKeepAliveMethod(
                keepAliveMethodBox.getSelectedItem().toString());
            registration.setKeepAliveInterval(keepAliveIntervalValue.getText());
        }
    }

    /**
     * Enables or disables the "Next" wizard button according to whether the UIN
     * field is empty.
     */
    private void setNextButtonAccordingToUIN()
    {
        if (uinField.getText() == null || uinField.getText().equals(""))
        {
            wizard.getWizardContainer().setNextFinishButtonEnabled(false);
        }
        else
        {
            wizard.getWizardContainer().setNextFinishButtonEnabled(true);
        }
    }

    /**
     * Handles the <tt>DocumentEvent</tt> triggered when user types in the UIN
     * field. Enables or disables the "Next" wizard button according to whether
     * the UIN field is empty.
     */
    public void insertUpdate(DocumentEvent e)
    {
        this.setNextButtonAccordingToUIN();
        this.setServerFieldAccordingToUIN();
    }

    /**
     * Handles the <tt>DocumentEvent</tt> triggered when user deletes letters
     * from the UIN field. Enables or disables the "Next" wizard button
     * according to whether the UIN field is empty.
     */
    public void removeUpdate(DocumentEvent e)
    {
        this.setNextButtonAccordingToUIN();
        this.setServerFieldAccordingToUIN();
    }

    public void changedUpdate(DocumentEvent e)
    {
    }

    public void pageHiding()
    {
    }

    public void pageShown()
    {
    }

    public void pageBack()
    {
    }

    /**
     * Fills the UIN and Password fields in this panel with the data coming from
     * the given protocolProvider.
     * 
     * @param protocolProvider The <tt>ProtocolProviderService</tt> to load
     *            the data from.
     */
    public void loadAccount(ProtocolProviderService protocolProvider)
    {
        AccountID accountID = protocolProvider.getAccountID();
        String password =
            (String) accountID.getAccountProperties().get(
                ProtocolProviderFactory.PASSWORD);

        String serverAddress =
            (String) accountID.getAccountProperties().get(
                ProtocolProviderFactory.SERVER_ADDRESS);

        String serverPort =
            (String) accountID.getAccountProperties().get(
                ProtocolProviderFactory.SERVER_PORT);

        String proxyAddress =
            (String) accountID.getAccountProperties().get(
                ProtocolProviderFactory.PROXY_ADDRESS);

        String proxyPort =
            (String) accountID.getAccountProperties().get(
                ProtocolProviderFactory.PROXY_PORT);

        String preferredTransport =
            (String) accountID.getAccountProperties().get(
                ProtocolProviderFactory.PREFERRED_TRANSPORT);

        boolean enablePresence = new Boolean(
            (String) accountID.getAccountProperties().get(
                ProtocolProviderFactory.IS_PRESENCE_ENABLED)).booleanValue();

        boolean forceP2P = new Boolean(
            (String) accountID.getAccountProperties().get(
                ProtocolProviderFactory.FORCE_P2P_MODE)).booleanValue();

/**
  *
  *Added by Dong Fengyu
  */
       boolean enableP2PSIP =new Boolean(
		(String) accountID.getAccountProperties().get(
		    ProtocolProviderFactory.IS_P2PSIP_ENABLED)).booleanValue();

      boolean  enablePassCheck =new Boolean(
	  	(String) accountID.getAccountProperties().get(
	  	     ProtocolProviderFactory.IS_PASSCHECK_ENABLED)).booleanValue();


        String pollingPeriod =
            (String) accountID.getAccountProperties().get(
                ProtocolProviderFactory.POLLING_PERIOD);

        String subscriptionPeriod =
            (String) accountID.getAccountProperties().get(
                ProtocolProviderFactory.SUBSCRIPTION_EXPIRATION);

        String keepAliveMethod
            = (String) accountID.getAccountProperties()
                .get("KEEP_ALIVE_METHOD");

        String keepAliveInterval
            = (String) accountID.getAccountProperties()
                .get("KEEP_ALIVE_INTERVAL");

        uinField.setEnabled(false);
        this.uinField.setText(accountID.getUserID());

        if (password != null)
        {
            this.passField.setText(password);
            this.rememberPassBox.setSelected(true);
        }

        serverField.setText(serverAddress);
        serverField.setEnabled(false);
        serverPortField.setText(serverPort);
        proxyField.setText(proxyAddress);

        // The order of the next two fields is important, as a changelister of
        // the transportCombo sets the proxyPortField to its default
        transportCombo.setSelectedItem(preferredTransport);
        proxyPortField.setText(proxyPort);

        if (!(serverPort.equals(DEFAULT_PORT)
                || serverPort.equals(DEFAULT_TLS_PORT))
            || !(proxyPort.equals(DEFAULT_PORT)
                || proxyPort.equals(DEFAULT_TLS_PORT))
            || !transportCombo.getSelectedItem().equals(DEFAULT_TRANSPORT))
        {
            enableAdvOpButton.setSelected(true);

            // The server field should stay disabled in modification mode,
            // because the user should not be able to change anything concerning
            // the account identifier and server name is part of it.
            serverField.setEnabled(false);

            serverPortField.setEnabled(true);
            proxyField.setEnabled(true);
            proxyPortField.setEnabled(true);
            transportCombo.setEnabled(true);
        }

        enablePresOpButton.setSelected(enablePresence);
        forceP2PPresOpButton.setSelected(forceP2P);

/**
  *
  *Added by Dong Fengyu
  */

        enableP2PSIPBox.setSelected(enableP2PSIP);

        enablePassCheckBox.setSelected(enablePassCheck);


        pollPeriodField.setText(pollingPeriod);
        subscribeExpiresField.setText(subscriptionPeriod);

        if (!enablePresence)
        {
            pollPeriodField.setEnabled(false);
            subscribeExpiresField.setEnabled(false);
        }
        
        keepAliveMethodBox.setSelectedItem(keepAliveMethod);
        keepAliveIntervalValue.setText(keepAliveInterval);
    }

    /**
     * Parse the server part from the sip id and set it to server as default
     * value. If Advanced option is enabled Do nothing.
     */
    private void setServerFieldAccordingToUIN()
    {
        if (!enableAdvOpButton.isSelected())
        {
            String uin = uinField.getText();
            int delimIndex = uin.indexOf("@");
            if (delimIndex != -1)
            {
                String newServerAddr = uin.substring(delimIndex + 1);

                serverField.setText(newServerAddr);
                proxyField.setText(newServerAddr);
            }
        }
    }

    /**
     * Disables Next Button if Port field value is incorrect
     */
    private void setNextButtonAccordingToPort()
    {
        try
        {
            wizard.getWizardContainer().setNextFinishButtonEnabled(true);
        }
        catch (NumberFormatException ex)
        {
            wizard.getWizardContainer().setNextFinishButtonEnabled(false);
        }
    }

    public void itemStateChanged(ItemEvent e)
    {
        if (e.getStateChange() == ItemEvent.SELECTED
            && e.getItem().equals("TLS"))
        {
            serverPortField.setText(DEFAULT_TLS_PORT);
            proxyPortField.setText(DEFAULT_TLS_PORT);
        }
        else
        {
            serverPortField.setText(DEFAULT_PORT);
            proxyPortField.setText(DEFAULT_PORT);
        }
    }

    private boolean isExistingAccount(String accountName, String serverName)
    {
        ProtocolProviderFactory factory =
            SIPAccRegWizzActivator.getSIPProtocolProviderFactory();

        ArrayList registeredAccounts = factory.getRegisteredAccounts();

        for (int i = 0; i < registeredAccounts.size(); i++)
        {
            AccountID accountID = (AccountID) registeredAccounts.get(i);

            if (accountName.equalsIgnoreCase(accountID.getUserID())
                    && serverName.equalsIgnoreCase(accountID.getService()))
                return true;
        }
        return false;
    }
}
