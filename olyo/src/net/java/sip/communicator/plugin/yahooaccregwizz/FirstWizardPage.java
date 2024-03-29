/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 * 
 * Distributable under LGPL license. See terms of license at gnu.org.
 */
package net.java.sip.communicator.plugin.yahooaccregwizz;

import java.awt.*;
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
 */
public class FirstWizardPage
    extends JPanel
    implements  WizardPage,
                DocumentListener
{

    public static final String FIRST_PAGE_IDENTIFIER = "FirstPageIdentifier";

    private JPanel uinPassPanel = new JPanel(new BorderLayout(10, 10));

    private JPanel labelsPanel = new JPanel();

    private JPanel valuesPanel = new JPanel();

    private JLabel uinLabel = new JLabel(Resources.getString("uin"));

    private JLabel passLabel = new JLabel(Resources.getString("password"));

    private JLabel existingAccountLabel =
        new JLabel(Resources.getString("existingAccount"));

    private JPanel emptyPanel = new JPanel();

    private JLabel uinExampleLabel =
        new JLabel("Ex: johnsmith@yahoo.com or johnsmith");

    private JTextField uinField = new JTextField();

    private JPasswordField passField = new JPasswordField();

    private JCheckBox rememberPassBox =
        new JCheckBox(Resources.getString("rememberPassword"));

    private JPanel mainPanel = new JPanel();

    private Object nextPageIdentifier = WizardPage.SUMMARY_PAGE_IDENTIFIER;

    private YahooAccountRegistrationWizard wizard;

    /**
     * Creates an instance of <tt>FirstWizardPage</tt>.
     * 
     * @param wizard the parent wizard
     */
    public FirstWizardPage(YahooAccountRegistrationWizard wizard)
    {

        super(new BorderLayout());

        this.wizard = wizard;

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

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
        this.rememberPassBox.setSelected(true);

        this.existingAccountLabel.setForeground(Color.RED);

        this.uinExampleLabel.setForeground(Color.GRAY);
        this.uinExampleLabel.setFont(uinExampleLabel.getFont().deriveFont(8));
        this.emptyPanel.setMaximumSize(new Dimension(40, 35));
        this.uinExampleLabel.setBorder(
            BorderFactory.createEmptyBorder(0, 0, 8, 0));

        labelsPanel.add(uinLabel);
        labelsPanel.add(emptyPanel);
        labelsPanel.add(passLabel);

        valuesPanel.add(uinField);
        valuesPanel.add(uinExampleLabel);
        valuesPanel.add(passField);

        uinPassPanel.add(labelsPanel, BorderLayout.WEST);
        uinPassPanel.add(valuesPanel, BorderLayout.CENTER);
        uinPassPanel.add(rememberPassBox, BorderLayout.SOUTH);

        uinPassPanel.setBorder(BorderFactory.createTitledBorder(Resources
            .getString("uinAndPassword")));

        mainPanel.add(uinPassPanel);
        this.add(mainPanel, BorderLayout.NORTH);
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

        if (!wizard.isModification() && isExistingAccount(uin))
        {
            nextPageIdentifier = FIRST_PAGE_IDENTIFIER;
            uinPassPanel.add(existingAccountLabel, BorderLayout.NORTH);
            this.revalidate();
        }
        else
        {
            nextPageIdentifier = SUMMARY_PAGE_IDENTIFIER;
            uinPassPanel.remove(existingAccountLabel);

            YahooAccountRegistration registration = wizard.getRegistration();

            registration.setUin(uinField.getText());
            registration.setPassword(new String(passField.getPassword()));
            registration.setRememberPassword(rememberPassBox.isSelected());
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
    }

    /**
     * Handles the <tt>DocumentEvent</tt> triggered when user deletes letters
     * from the UIN field. Enables or disables the "Next" wizard button
     * according to whether the UIN field is empty.
     */
    public void removeUpdate(DocumentEvent e)
    {
        this.setNextButtonAccordingToUIN();
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
     * Fills the UIN and Password fields in this panel with the data coming
     * from the given protocolProvider.
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

        this.uinField.setEnabled(false);
        this.uinField.setText(accountID.getUserID());

        if (password != null)
        {
            this.passField.setText(password);
            this.rememberPassBox.setSelected(true);
        }
    }

    private boolean isExistingAccount(String accountName)
    {
        ProtocolProviderFactory factory =
            YahooAccRegWizzActivator.getYahooProtocolProviderFactory();

        ArrayList registeredAccounts = factory.getRegisteredAccounts();

        for (int i = 0; i < registeredAccounts.size(); i++)
        {
            AccountID accountID = (AccountID) registeredAccounts.get(i);

            if (accountName.equalsIgnoreCase(accountID.getUserID()))
                return true;
        }
        return false;
    }
}
