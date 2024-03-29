/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package net.java.sip.communicator.impl.gui.main;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import net.java.sip.communicator.impl.gui.*;
import net.java.sip.communicator.impl.gui.customcontrols.*;
import net.java.sip.communicator.impl.gui.event.*;
import net.java.sip.communicator.impl.gui.i18n.*;
import net.java.sip.communicator.impl.gui.main.call.*;
import net.java.sip.communicator.impl.gui.main.chat.*;
import net.java.sip.communicator.impl.gui.main.chat.conference.*;
import net.java.sip.communicator.impl.gui.main.chat.history.*;
import net.java.sip.communicator.impl.gui.main.chatroomslist.*;
import net.java.sip.communicator.impl.gui.main.contactlist.*;
import net.java.sip.communicator.impl.gui.main.login.*;
import net.java.sip.communicator.impl.gui.main.menus.*;
import net.java.sip.communicator.impl.gui.main.presence.*;
import net.java.sip.communicator.impl.gui.utils.*;
import net.java.sip.communicator.service.configuration.*;
import net.java.sip.communicator.service.contacteventhandler.*;
import net.java.sip.communicator.service.contactlist.*;
import net.java.sip.communicator.service.gui.*;
import net.java.sip.communicator.service.gui.Container;
import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.service.protocol.event.*;
import net.java.sip.communicator.util.*;

import org.osgi.framework.*;

/**
 * The main application window. This class is the core of this ui
 * implementation. It stores all available protocol providers and their
 * operation sets, as well as all registered accounts, the
 * <tt>MetaContactListService</tt> and all sent messages that aren't
 * delivered yet.
 *
 * @author Yana Stamcheva
 */
public class MainFrame
    extends SIPCommFrame
    implements PluginComponentListener
{
    private Logger logger = Logger.getLogger(MainFrame.class.getName());

    private JPanel contactListPanel = new JPanel(new BorderLayout());

    private JPanel mainPanel = new JPanel(new BorderLayout(0, 5));
    
    private MainMenu menu;

    private CallManager callManager;

    private StatusPanel statusPanel;

    private MainTabbedPane tabbedPane;

    private JComponent quickMenu;

    private LinkedHashMap protocolProviders = new LinkedHashMap();

    private MetaContactListService contactList;

    private LoginManager loginManager;

    private ChatWindowManager chatWindowManager;
    
    private MultiUserChatManager multiUserChatManager;

    private HistoryWindowManager historyWindowManager
        = new HistoryWindowManager();

    private Hashtable<ProtocolProviderService, ContactEventHandler>
        providerContactHandlers
            = new Hashtable<ProtocolProviderService, ContactEventHandler>();

    /**
     * Creates an instance of <tt>MainFrame</tt>.
     */
    public MainFrame()
    {
        this.chatWindowManager = new ChatWindowManager(this);

        callManager = new CallManager(this);
        multiUserChatManager = new MultiUserChatManager(this);

        tabbedPane = new MainTabbedPane(this);

        boolean isToolBarExtended
            = new Boolean(ApplicationProperties
                    .getProperty("isToolBarExteneded")).booleanValue();

        if (isToolBarExtended)
            quickMenu = new ExtendedQuickMenu(this);
        else
            quickMenu = new QuickMenu(this);

        statusPanel = new StatusPanel(this);
        menu = new MainMenu(this);

        this.addWindowListener(new MainFrameWindowAdapter());

        this.initBounds();

        this.initTitleFont();

        this.setTitle(ApplicationProperties.getProperty("applicationName"));

        this.init();

        this.initPluginComponents();
    }

    /**
     * Initiates the content of this frame.
     */
    private void init()
    {
        this.addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0),
                new RenameAction());
        this.addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,
            KeyEvent.ALT_DOWN_MASK), new ForwordTabAction());
        this.addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,
            KeyEvent.ALT_DOWN_MASK), new BackwordTabAction());

        this.contactListPanel.add(tabbedPane, BorderLayout.CENTER);
        this.contactListPanel.add(callManager, BorderLayout.SOUTH);

        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        this.mainPanel.add(contactListPanel, BorderLayout.CENTER);
        this.mainPanel.add(statusPanel, BorderLayout.SOUTH);

        JPanel menusPanel = new JPanel(new BorderLayout(0, 5));

        menusPanel.add(menu, BorderLayout.CENTER);
        menusPanel.add(quickMenu, BorderLayout.SOUTH);

        JPanel northPanel = new JPanel(new BorderLayout());

        northPanel.add(new LogoBar(), BorderLayout.NORTH);
        northPanel.add(menusPanel, BorderLayout.CENTER);

        this.getContentPane().add(northPanel, BorderLayout.NORTH);
        this.getContentPane().add(mainPanel, BorderLayout.CENTER);
    }
    
    /**
     * Sets frame size and position.
     */
    private void initBounds()
    {
        int width = SizeProperties.getSize("mainWindowWidth");
        int height = SizeProperties.getSize("mainWindowHeight");

        this.setSize(width, height);

        this.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width
                - this.getWidth(), 50);
    }

    /**
     * Initialize main window font.
     */
    private void initTitleFont()
    {
        JComponent layeredPane = this.getLayeredPane();

        Font font = new Font(
            ApplicationProperties.getProperty("fontName"),
            Font.BOLD,
            new Integer(ApplicationProperties.getProperty("titleFontSize"))
                .intValue());

        for (int i = 0; i < layeredPane.getComponentCount(); i++)
        {
            layeredPane.getComponent(i).setFont(font);
        }
    }

    /**
     * Returns the <tt>MetaContactListService</tt>.
     *
     * @return <tt>MetaContactListService</tt> The current meta contact list.
     */
    public MetaContactListService getContactList()
    {
        return this.contactList;
    }

    /**
     * Initializes the contact list panel.
     *
     * @param contactList The <tt>MetaContactListService</tt> containing
     * the contact list data.
     */
    public void setContactList(MetaContactListService contactList)
    {
        this.contactList = contactList;

        ContactListPanel clistPanel = this.tabbedPane.getContactListPanel();

        clistPanel.initList(contactList);

        CListKeySearchListener keyListener
            = new CListKeySearchListener(clistPanel.getContactList());

        //add a key listener to the tabbed pane, when the contactlist is
        //initialized
        this.tabbedPane.addKeyListener(keyListener);

        clistPanel.addKeyListener(keyListener);

        clistPanel.getContactList().addKeyListener(keyListener);

        clistPanel.getContactList().addListSelectionListener(callManager);
    }

    /**
     * Adds all protocol supported operation sets.
     *
     * @param protocolProvider The protocol provider.
     */
    public void addProtocolSupportedOperationSets(
            ProtocolProviderService protocolProvider)
    {
        Map supportedOperationSets
            = protocolProvider.getSupportedOperationSets();

        String ppOpSetClassName = OperationSetPersistentPresence
                                    .class.getName();
        String pOpSetClassName = OperationSetPresence.class.getName();

        // Obtain the presence operation set.
        if (supportedOperationSets.containsKey(ppOpSetClassName)
                || supportedOperationSets.containsKey(pOpSetClassName)) {

            OperationSetPresence presence = (OperationSetPresence)
                supportedOperationSets.get(ppOpSetClassName);

            if(presence == null) {
                presence = (OperationSetPresence)
                    supportedOperationSets.get(pOpSetClassName);
            }

            presence.addProviderPresenceStatusListener(
                        new GUIProviderPresenceStatusListener());
            presence.addContactPresenceStatusListener(
                        new GUIContactPresenceStatusListener());
        }

        // Obtain the basic instant messaging operation set.
        String imOpSetClassName = OperationSetBasicInstantMessaging
                                    .class.getName();

        if (supportedOperationSets.containsKey(imOpSetClassName)) {

            OperationSetBasicInstantMessaging im
                = (OperationSetBasicInstantMessaging)
                    supportedOperationSets.get(imOpSetClassName);

            //Add to all instant messaging operation sets the Message
            //listener implemented in the ContactListPanel, which handles
            //all received messages.
            im.addMessageListener(getContactListPanel());
        }

        // Obtain the typing notifications operation set.
        String tnOpSetClassName = OperationSetTypingNotifications
                                    .class.getName();

        if (supportedOperationSets.containsKey(tnOpSetClassName)) {

            OperationSetTypingNotifications tn
                = (OperationSetTypingNotifications)
                    supportedOperationSets.get(tnOpSetClassName);

            //Add to all typing notification operation sets the Message
            //listener implemented in the ContactListPanel, which handles
            //all received messages.
            tn.addTypingNotificationsListener(this.getContactListPanel());
        }

        // Obtain the basic telephony operation set.
        String telOpSetClassName = OperationSetBasicTelephony.class.getName();

        if (supportedOperationSets.containsKey(telOpSetClassName)) {

            OperationSetBasicTelephony telephony
                = (OperationSetBasicTelephony)
                    supportedOperationSets.get(telOpSetClassName);

            telephony.addCallListener(callManager);
            this.getContactListPanel().getContactList()
                .addListSelectionListener(callManager);
            this.tabbedPane.addChangeListener(callManager);
        }

        // Obtain the multi user chat operation set.
        String multiChatClassName = OperationSetMultiUserChat.class.getName();

        if (supportedOperationSets.containsKey(multiChatClassName))
        {
            OperationSetMultiUserChat multiUserChat
                = (OperationSetMultiUserChat)
                    supportedOperationSets.get(multiChatClassName);

            multiUserChat.addInvitationListener(multiUserChatManager);
            multiUserChat.addInvitationRejectionListener(multiUserChatManager);
            multiUserChat.addPresenceListener(multiUserChatManager);
            
            this.getChatRoomsListPanel()
                .getChatRoomsList()
                .addChatServer(protocolProvider, multiUserChat);
        }

        // Obtain the basic instant messaging operation set.
        String smsOpSetClassName = OperationSetSmsMessaging.class.getName();

        if (supportedOperationSets.containsKey(smsOpSetClassName))
        {
            OperationSetSmsMessaging smsOpSet
                = (OperationSetSmsMessaging)
                    supportedOperationSets.get(smsOpSetClassName);

            //Add to all instant messaging operation sets the Message
            //listener implemented in the ContactListPanel, which handles
            //all received messages.
            smsOpSet.addMessageListener(getContactListPanel());
        }
    }

    /**
     * Returns a set of all protocol providers.
     *
     * @return a set of all protocol providers.
     */
    public Iterator getProtocolProviders()
    {
        return ((LinkedHashMap)protocolProviders.clone()).keySet().iterator();
    }
    
    /**
     * Returns the protocol provider associated to the account given
     * by the account user identifier.
     *
     * @param accountName The account user identifier.
     * @return The protocol provider associated to the given account.
     */
    public ProtocolProviderService getProtocolProviderForAccount(
            String accountName)
    {
        Iterator i = this.protocolProviders.keySet().iterator();
        while(i.hasNext()) {
            ProtocolProviderService pps
                = (ProtocolProviderService)i.next();

            if (pps.getAccountID().getUserID().equals(accountName)) {
               return pps;
            }
        }

        return null;
    }

    /**
     * Adds a protocol provider.
     * @param protocolProvider The protocol provider to add.
     */
    public void addProtocolProvider(ProtocolProviderService protocolProvider)
    {
        logger.trace("Add the following protocol provider to the gui: "
            + protocolProvider.getAccountID().getAccountAddress());

        this.protocolProviders.put(protocolProvider,
                new Integer(initiateProviderIndex(protocolProvider)));

        this.addProtocolSupportedOperationSets(protocolProvider);

        this.addAccount(protocolProvider);

        ContactEventHandler contactHandler
            = this.getContactHandlerForProvider(protocolProvider);

        if (contactHandler == null)
            contactHandler = new DefaultContactEventHandler(this);

        this.addProviderContactHandler(protocolProvider, contactHandler);
    }

    /**
     * Returns the index of the given protocol provider.
     * @param protocolProvider the protocol provider to search for
     * @return the index of the given protocol provider
     */
    public int getProviderIndex(ProtocolProviderService protocolProvider)
    {
        Object o = protocolProviders.get(protocolProvider);

        if(o != null) {
            return ((Integer)o).intValue();
        }
        return 0;
    }

    /**
     * Adds an account to the application.
     *
     * @param protocolProvider The protocol provider of the account.
     */
    public void addAccount(ProtocolProviderService protocolProvider)
    {
        if (!getStatusPanel().containsAccount(protocolProvider)) {
            
            logger.trace("Add the following account to the status bar: "
                + protocolProvider.getAccountID().getAccountAddress());
            
            this.getStatusPanel().addAccount(protocolProvider);

            //request the focus in the contact list panel, which
            //permits to search in the contact list
            this.tabbedPane.getContactListPanel().getContactList()
                    .requestFocus();
        }
                
        if(!callManager.containsCallAccount(protocolProvider)
            && getTelephonyOpSet(protocolProvider) != null) {
            callManager.addCallAccount(protocolProvider);
        }
    }

    /**
     * Adds an account to the application.
     *
     * @param protocolProvider The protocol provider of the account.
     */
    public void removeProtocolProvider(ProtocolProviderService protocolProvider)
    {
        this.protocolProviders.remove(protocolProvider);
        this.updateProvidersIndexes(protocolProvider);

        if (getStatusPanel().containsAccount(protocolProvider))
        {
            this.getStatusPanel().removeAccount(protocolProvider);
        }

        if(callManager.containsCallAccount(protocolProvider))
        {
            callManager.removeCallAccount(protocolProvider);
        }

        // Remove all related chat rooms.
        this.getChatRoomsListPanel().getChatRoomsList()
            .removeChatServer(protocolProvider);
    }

    /**
     * Activates an account. Here we start the connecting process.
     *
     * @param protocolProvider The protocol provider of this account.
     */
    public void activateAccount(ProtocolProviderService protocolProvider)
    {
        this.getStatusPanel().startConnecting(protocolProvider);
    }

    /**
     * Returns the account user id for the given protocol provider.
     * @return The account user id for the given protocol provider.
     */
    public String getAccount(ProtocolProviderService protocolProvider)
    {
        return protocolProvider.getAccountID().getUserID();
    }

    /**
     * Returns the presence operation set for the given protocol provider.
     *
     * @param protocolProvider The protocol provider for which the
     * presence operation set is searched.
     * @return the presence operation set for the given protocol provider.
     */
    public OperationSetPresence getProtocolPresenceOpSet(
            ProtocolProviderService protocolProvider)
    {
        OperationSet opSet
            = protocolProvider.getOperationSet(OperationSetPresence.class);
        
        if(opSet != null && opSet instanceof OperationSetPresence)
            return (OperationSetPresence) opSet;
        
        return null;
    }

    /**
     * Returns the Web Contact Info operation set for the given
     * protocol provider.
     *
     * @param protocolProvider The protocol provider for which the TN
     * is searched.
     * @return OperationSetWebContactInfo The Web Contact Info operation
     * set for the given protocol provider.
     */
    public OperationSetWebContactInfo getWebContactInfoOpSet(
            ProtocolProviderService protocolProvider)
    {           
        OperationSet opSet
            = protocolProvider.getOperationSet(OperationSetWebContactInfo.class);
        
        if(opSet != null && opSet instanceof OperationSetWebContactInfo)
            return (OperationSetWebContactInfo) opSet;
        
        return null;
    }

    /**
     * Returns the telephony operation set for the given protocol provider.
     *
     * @param protocolProvider The protocol provider for which the telephony
     * operation set is about.
     * @return OperationSetBasicTelephony The telephony operation
     * set for the given protocol provider.
     */
    public OperationSetBasicTelephony getTelephonyOpSet(
            ProtocolProviderService protocolProvider)
    {
        OperationSet opSet
            = protocolProvider.getOperationSet(OperationSetBasicTelephony.class);
        
        if(opSet != null && opSet instanceof OperationSetBasicTelephony)
            return (OperationSetBasicTelephony) opSet;
        
        return null;
    }
    
    /**
     * Returns the multi user chat operation set for the given protocol provider.
     *
     * @param protocolProvider The protocol provider for which the multi user
     * chat operation set is about.
     * @return OperationSetMultiUserChat The telephony operation
     * set for the given protocol provider.
     */
    public OperationSetMultiUserChat getMultiUserChatOpSet(
            ProtocolProviderService protocolProvider)
    {
        OperationSet opSet
            = protocolProvider.getOperationSet(OperationSetMultiUserChat.class);
        
        if(opSet != null && opSet instanceof OperationSetMultiUserChat)
            return (OperationSetMultiUserChat) opSet;
        
        return null;
    }

    /**
     * Returns the call manager.
     * @return CallManager The call manager.
     */
    public CallManager getCallManager()
    {
        return callManager;
    }

    /**
     * Returns the status panel.
     * @return StatusPanel The status panel.
     */
    public StatusPanel getStatusPanel()
    {
        return statusPanel;
    }

    /**
     * Listens for all contactPresenceStatusChanged events in order
     * to refresh the contact list, when a status is changed.
     */
    private class GUIContactPresenceStatusListener implements
            ContactPresenceStatusListener
    {
        /**
         * Indicates that a contact has changed its status.
         * 
         * @param evt the presence event containing information about the
         * contact status change
         */
        public void contactPresenceStatusChanged(
                ContactPresenceStatusChangeEvent evt)
        {
            ContactListPanel clistPanel = tabbedPane.getContactListPanel();

            Contact sourceContact = evt.getSourceContact();

            MetaContact metaContact = contactList
                    .findMetaContactByContact(sourceContact);

            if (metaContact != null
                && (evt.getOldStatus() != evt.getNewStatus()))
            {
                // Update the status in the contact list.
                clistPanel.getContactList().refreshContact(metaContact);

                // Update the status in chat window.
                if(chatWindowManager.isChatOpenedForContact(metaContact))
                {
                    MetaContactChatPanel chatPanel
                        = chatWindowManager.getContactChat(metaContact);

                    chatPanel.updateContactStatus(
                        sourceContact, evt.getNewStatus());
                }
            }
        }
    }

    /**
     * Listens for all providerStatusChanged and providerStatusMessageChanged
     * events in order to refresh the account status panel, when a status is
     * changed.
     */
    private class GUIProviderPresenceStatusListener implements
            ProviderPresenceStatusListener
    {
        public void providerStatusChanged(ProviderPresenceStatusChangeEvent evt)
        {
            ProtocolProviderService pps = evt.getProvider();

            getStatusPanel().updateStatus(pps, evt.getNewStatus());

            if(callManager.containsCallAccount(pps))
            {
                callManager.updateCallAccountStatus(pps);
            }
        }

        public void providerStatusMessageChanged(PropertyChangeEvent evt) {

        }
    }

    /**
     * Returns the list of all groups.
     * @return The list of all groups.
     */
    public Iterator getAllGroups()
    {
        return getContactListPanel()
            .getContactList().getAllGroups();
    }

    /**
     * Returns the Meta Contact Group corresponding to the given MetaUID.
     *
     * @param metaUID An identifier of a group.
     * @return The Meta Contact Group corresponding to the given MetaUID.
     */
    public MetaContactGroup getGroupByID(String metaUID)
    {
        return getContactListPanel()
            .getContactList().getGroupByID(metaUID);
    }

    /**
     * Before closing the application window saves the current size and position
     * through the <tt>ConfigurationService</tt>.
     */
    public class MainFrameWindowAdapter extends WindowAdapter
    {
        public void windowClosing(WindowEvent e)
        {
            if(!GuiActivator.getUIService().getExitOnMainWindowClose())
            {
                new Thread()
                {
                    public void run()
                    {
                        if(ConfigurationManager.isQuitWarningShown())
                        {
                            MessageDialog dialog
                                = new MessageDialog(
                                    MainFrame.this,
                                    Messages.getI18NString("close").getText(),
                                    Messages.getI18NString("hideMainWindow")
                                        .getText(),
                                    false);

                            int returnCode = dialog.showDialog();

                            if (returnCode == MessageDialog.OK_DONT_ASK_CODE)
                            {
                                ConfigurationManager
                                    .setQuitWarningShown(false);
                            }
                        }
                    }
                }.start();

                ConfigurationManager.setApplicationVisible(false);
            }
        }
        
        public void windowClosed(WindowEvent e)
        {
            if(GuiActivator.getUIService().getExitOnMainWindowClose())
            {
                try
                {
                    GuiActivator.bundleContext.getBundle(0).stop();
                }
                catch (BundleException ex)
                {
                    logger.error("Failed to gently shutdown Felix", ex);
                    System.exit(0);
                }
                //stopping a bundle doesn't leave the time to the felix thread to
                //properly end all bundles and call their Activator.stop() methods.
                //if this causes problems don't uncomment the following line but
                //try and see why felix isn't exiting (suggesting: is it running
                //in embedded mode?)
                //System.exit(0);
            }
        }
    }    

    /**
     * Returns the class that manages user login.
     * @return the class that manages user login.
     */
    public LoginManager getLoginManager()
    {
        return loginManager;
    }

    /**
     * Sets the class that manages user login.
     * @param loginManager The user login manager.
     */
    public void setLoginManager(LoginManager loginManager)
    {
        this.loginManager = loginManager;
    }

    public CallListPanel getCallListManager()
    {
        return this.tabbedPane.getCallListPanel();
    }

    /**
     * Returns the panel containing the ContactList.
     * @return ContactListPanel the panel containing the ContactList
     */
    public ContactListPanel getContactListPanel()
    {
        return this.tabbedPane.getContactListPanel();
    }
    
    /**
     * Returns the panel containing the chat rooms list.
     * @return the panel containing the chat rooms list
     */
    public ChatRoomsListPanel getChatRoomsListPanel()
    {
        return this.tabbedPane.getChatRoomsListPanel();
    }

    /**
     * Adds a tab in the main tabbed pane, where the given call panel
     * will be added.
     */
    public void addCallPanel(CallPanel callPanel)
    {
        this.tabbedPane.addTab(callPanel.getTitle(), callPanel);
        this.tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
        this.tabbedPane.revalidate();
    }


    /**
     * Removes the tab in the main tabbed pane, where the given call panel
     * is contained.
     */
    public void removeCallPanel(CallPanel callPanel)
    {
        // Should remove all participant panels explicetly, thus removing also
        // all related dialogs (like dialpad for example).
        callPanel.removeDialogs();
        
        tabbedPane.remove(callPanel);

        Component c = getSelectedTab();

        if(c == null || !(c instanceof CallPanel))
            this.tabbedPane.setSelectedIndex(0);

        this.tabbedPane.revalidate();
    }

    /**
     * Returns the component contained in the currently selected tab.
     * @return the selected CallPanel or null if there's no CallPanel selected
     */
    public Component getSelectedTab()
    {
        Component c = this.tabbedPane.getSelectedComponent();

        return c;
    }


    /**
     * Checks in the configuration xml if there is already stored index for
     * this provider and if yes, returns it, otherwise creates a new account
     * index and stores it.
     *
     * @param protocolProvider the protocol provider
     * @return the protocol provider index
     */
    private int initiateProviderIndex(
            ProtocolProviderService protocolProvider)
    {
        ConfigurationService configService
            = GuiActivator.getConfigurationService();

        String prefix = "net.java.sip.communicator.impl.gui.accounts";

        List accounts = configService
                .getPropertyNamesByPrefix(prefix, true);

        Iterator accountsIter = accounts.iterator();

        boolean savedAccount = false;

        while(accountsIter.hasNext()) {
            String accountRootPropName
                = (String) accountsIter.next();

            String accountUID
                = configService.getString(accountRootPropName);

            if(accountUID.equals(protocolProvider
                    .getAccountID().getAccountUniqueID())) {

                savedAccount = true;
                String  index = configService.getString(
                        accountRootPropName + ".accountIndex");

                if(index != null) {
                    //if we have found the accountIndex for this protocol provider
                    //return this index
                    return new Integer(index).intValue();
                }
                else {
                    //if there's no stored accountIndex for this protocol
                    //provider, calculate the index, set it in the configuration
                    //service and return it.

                    int accountIndex = createAccountIndex(protocolProvider,
                            accountRootPropName);
                    return accountIndex;
                }
            }
        }

        if(!savedAccount) {
            String accNodeName
                = "acc" + Long.toString(System.currentTimeMillis());

            String accountPackage
                = "net.java.sip.communicator.impl.gui.accounts."
                        + accNodeName;

            configService.setProperty(accountPackage,
                    protocolProvider.getAccountID().getAccountUniqueID());

            int accountIndex = createAccountIndex(protocolProvider,
                    accountPackage);

            return accountIndex;
        }
        return -1;
    }

    /**
     * Creates and calculates the account index for the given protocol
     * provider.
     * @param protocolProvider the protocol provider
     * @param accountRootPropName the path to where the index should be saved
     * in the configuration xml
     * @return the created index
     */
    private int createAccountIndex(ProtocolProviderService protocolProvider,
            String accountRootPropName)
    {
        ConfigurationService configService
            = GuiActivator.getConfigurationService();

        int accountIndex = -1;
        Iterator pproviders = protocolProviders.keySet().iterator();
        ProtocolProviderService pps;

        while(pproviders.hasNext()) {
            pps = (ProtocolProviderService)pproviders.next();

            if(pps.getProtocolDisplayName().equals(
                    protocolProvider.getProtocolDisplayName())
                    && !pps.equals(protocolProvider)) {

                int index  = ((Integer)protocolProviders.get(pps)).intValue();

                if(accountIndex < index) {
                    accountIndex = index;
                }
            }
        }
        accountIndex++;
        configService.setProperty(
                accountRootPropName + ".accountIndex",
                new Integer(accountIndex));

        return accountIndex;
    }

    /**
     * Updates the indexes in the configuration xml, when a provider has been
     * removed.
     * @param removedProvider the removed protocol provider
     */
    private void updateProvidersIndexes(ProtocolProviderService removedProvider)
    {
        ConfigurationService configService
            = GuiActivator.getConfigurationService();

        String prefix = "net.java.sip.communicator.impl.gui.accounts";

        Iterator pproviders = protocolProviders.keySet().iterator();
        ProtocolProviderService currentProvider = null;
        int sameProtocolProvidersCount = 0;

        while(pproviders.hasNext()) {
            ProtocolProviderService pps
                = (ProtocolProviderService)pproviders.next();

            if(pps.getProtocolDisplayName().equals(
                    removedProvider.getProtocolDisplayName())) {

                sameProtocolProvidersCount++;
                if(sameProtocolProvidersCount > 1) {
                    break;
                }
                currentProvider = pps;
            }
        }

        if(sameProtocolProvidersCount < 2 && currentProvider != null) {
            protocolProviders.put(currentProvider, new Integer(0));

            List accounts = configService
                .getPropertyNamesByPrefix(prefix, true);

            Iterator accountsIter = accounts.iterator();

            while(accountsIter.hasNext()) {
                String rootPropName
                    = (String) accountsIter.next();

                String accountUID
                    = configService.getString(rootPropName);

                if(accountUID.equals(currentProvider
                        .getAccountID().getAccountUniqueID())) {

                    configService.setProperty(
                            rootPropName + ".accountIndex",
                            new Integer(0));
                }
            }

            this.getStatusPanel().updateAccountIndex(currentProvider);
        }
    }

    /**
     * If the protocol provider supports presence operation set searches the
     * last status which was selected, otherwise returns null.
     *
     * @param protocolProvider the protocol provider we're interested in.
     * @return the last protocol provider presence status, or null if this
     * provider doesn't support presence operation set
     */
    public Object getProtocolProviderLastStatus(
            ProtocolProviderService protocolProvider)
    {
        if(getProtocolPresenceOpSet(protocolProvider) != null)
            return this.statusPanel
                .getLastPresenceStatus(protocolProvider);
        else
            return this.statusPanel.getLastStatusString(protocolProvider);
    }

    /**
     * <tt>RenameAction</tt> is invoked when user presses the F2 key. Depending
     * on the selection opens the appropriate form for renaming.
     */
    private class RenameAction extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        {
            Object selectedObject
                = getContactListPanel().getContactList().getSelectedValue();

            if(selectedObject instanceof MetaContact) {
                RenameContactDialog dialog = new RenameContactDialog(
                        MainFrame.this, (MetaContact)selectedObject);

                dialog.setLocation(
                        Toolkit.getDefaultToolkit().getScreenSize().width/2
                            - 200,
                        Toolkit.getDefaultToolkit().getScreenSize().height/2
                            - 50
                        );

                dialog.setVisible(true);

                dialog.requestFocusInFiled();
            }
            else if(selectedObject instanceof MetaContactGroup) {

                RenameGroupDialog dialog = new RenameGroupDialog(
                        MainFrame.this, (MetaContactGroup)selectedObject);

                dialog.setLocation(
                        Toolkit.getDefaultToolkit().getScreenSize().width/2
                            - 200,
                        Toolkit.getDefaultToolkit().getScreenSize().height/2
                            - 50
                        );

                dialog.setVisible(true);

                dialog.requestFocusInFiled();
            }
        }
    }

    /**
     * Overwrites the <tt>SIPCommFrame</tt> close method. This method is
     * invoked when user presses the Escape key.
     */
    protected void close(boolean isEscaped)
    {
        ContactList contactList = getContactListPanel().getContactList();

        ContactRightButtonMenu contactPopupMenu
            = contactList.getContactRightButtonMenu();

        GroupRightButtonMenu groupPopupMenu
            = contactList.getGroupRightButtonMenu();

        CommonRightButtonMenu commonPopupMenu
            = getContactListPanel().getCommonRightButtonMenu();

        if(contactPopupMenu != null && contactPopupMenu.isVisible()) {
            contactPopupMenu.setVisible(false);
        }
        else if(groupPopupMenu != null && groupPopupMenu.isVisible()) {
            groupPopupMenu.setVisible(false);
        }
        else if(commonPopupMenu != null && commonPopupMenu.isVisible()) {
            commonPopupMenu.setVisible(false);
        }
        else if(statusPanel.hasSelectedMenus() || menu.hasSelectedMenus()) {
            MenuSelectionManager selectionManager
                = MenuSelectionManager.defaultManager();

            selectionManager.clearSelectedPath();
        }
    }

    /**
     * Returns the main menu in the application window.
     * @return the main menu in the application window
     */
    public MainMenu getMainMenu()
    {
        return menu;
    }

    /**
     * Returns the <tt>ChatWindowManager</tt>.
     * @return the <tt>ChatWindowManager</tt>
     */
    public ChatWindowManager getChatWindowManager()
    {
        return chatWindowManager;
    }

    /**
     * Returns the <tt>HistoryWindowManager</tt>.
     * @return the <tt>HistoryWindowManager</tt>
     */
    public HistoryWindowManager getHistoryWindowManager()
    {
        return historyWindowManager;
    }

    /**
     * Returns the class that manages all chat room invitation and message
     * events.
     * @return the class that manages all chat room invitation and message
     * events.
     */
    public MultiUserChatManager getMultiUserChatManager()
    {
        return multiUserChatManager;
    };

    /**
     * The <tt>ForwordTabAction</tt> is an <tt>AbstractAction</tt> that
     * changes the currently selected tab with the next one. Each time when the
     * last tab index is reached the first one is selected.
     */
    private class ForwordTabAction
        extends AbstractAction
    {
        /**
         * Changes the currently selected tab with the next one. Each time when
         * the last tab index is reached the first one is selected.
         * @param e the action event
         */
        public void actionPerformed(ActionEvent e)
        {
            int selectedIndex = tabbedPane.getSelectedIndex();

            if (selectedIndex < tabbedPane.getTabCount() - 1)
                tabbedPane.setSelectedIndex(selectedIndex + 1);
            else
                tabbedPane.setSelectedIndex(0);
        }
    };

    /**
     * The <tt>BackwordTabAction</tt> is an <tt>AbstractAction</tt> that
     * changes the currently selected tab with the previous one. Each time when
     * the first tab index is reached the last one is selected.
     */
    private class BackwordTabAction
        extends AbstractAction
    {
        /**
         * Changes the currently selected tab with the previous one. Each time
         * when the first tab index is reached the last one is selected.
         * @param e the action event
         */
        public void actionPerformed(ActionEvent e)
        {
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex != 0)
                tabbedPane.setSelectedIndex(selectedIndex - 1);
            else
                tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
        }
    };

    /**
     * 
     * @param protocolProvider
     * @param contactHandler
     */
    public void addProviderContactHandler(
        ProtocolProviderService protocolProvider,
        ContactEventHandler contactHandler)
    {
        providerContactHandlers.put(protocolProvider, contactHandler);
    }

    /**
     * Returns the <tt>ContactEventHandler</tt> registered for this protocol
     * provider.
     * 
     * @param protocolProvider the <tt>ProtocolProviderService</tt> for which
     * we are searching a <tt>ContactEventHandler</tt>.
     * @return the <tt>ContactEventHandler</tt> registered for this protocol
     * provider
     */
    public ContactEventHandler getContactHandler(
        ProtocolProviderService protocolProvider)
    {
        return providerContactHandlers.get(protocolProvider);
    }

    /**
     * 
     * @param protocolProvider
     * @return
     */
    private ContactEventHandler getContactHandlerForProvider(
        ProtocolProviderService protocolProvider)
    {
        ServiceReference[] serRefs = null;

        String osgiFilter = "("
            + ProtocolProviderFactory.PROTOCOL
            + "=" + protocolProvider.getProtocolName()+")";

        try
        {
            serRefs = GuiActivator.bundleContext.getServiceReferences(
                ContactEventHandler.class.getName(), osgiFilter);
        }
        catch (InvalidSyntaxException ex){
            logger.error("GuiActivator : " + ex);
        }

        if(serRefs == null)
            return null;

        return (ContactEventHandler) GuiActivator.bundleContext
            .getService(serRefs[0]);
    }

    /**
     * Initialize plugin components already registered for this container.
     */
    private void initPluginComponents()
    {
        Iterator pluginComponents = GuiActivator.getUIService()
                .getComponentsForContainer(
                    UIService.CONTAINER_CONTACT_LIST_SOUTH);

        while (pluginComponents.hasNext())
        {
            Component o = (Component) pluginComponents.next();

            this.getContentPane().add(o, BorderLayout.SOUTH);
        }

        pluginComponents = GuiActivator.getUIService()
                .getComponentsForContainer(
                    UIService.CONTAINER_CONTACT_LIST_NORTH);

        while (pluginComponents.hasNext())
        {
            Component o = (Component) pluginComponents.next();

            this.getContentPane().add(o, BorderLayout.NORTH);
        }

        pluginComponents = GuiActivator.getUIService()
                .getComponentsForContainer(
                    UIService.CONTAINER_CONTACT_LIST_EAST);

        while (pluginComponents.hasNext())
        {
            Component o = (Component) pluginComponents.next();

            this.getContentPane().add(o, BorderLayout.EAST);
        }

        pluginComponents = GuiActivator.getUIService()
                .getComponentsForContainer(
                    UIService.CONTAINER_CONTACT_LIST_WEST);

        while (pluginComponents.hasNext())
        {
            Component o = (Component) pluginComponents.next();

            this.getContentPane().add(o, BorderLayout.WEST);
        }

        // Search for plugin components registered through the OSGI bundle
        // context.
        ServiceReference[] serRefs = null;

        String osgiFilter = "("
            + Container.CONTAINER_ID
            + "="+Container.CONTAINER_CONTACT_LIST.getID()+")";

        try
        {
            serRefs = GuiActivator.bundleContext.getServiceReferences(
                PluginComponent.class.getName(),
                osgiFilter);
        }
        catch (InvalidSyntaxException exc)
        {
            logger.error("Could not obtain plugin reference.", exc);
        }

        if (serRefs != null)
        {
            for (int i = 0; i < serRefs.length; i ++)
            {
                PluginComponent c = (PluginComponent) GuiActivator
                    .bundleContext.getService(serRefs[i]);

                Object constraints = null;

                if (c.getConstraints() != null)
                    constraints = UIServiceImpl
                        .getBorderLayoutConstraintsFromContainer(c.getConstraints());
                else
                    constraints = BorderLayout.SOUTH;

                this.getContentPane().add(  (Component) c.getComponent(),
                                            constraints);
            }
        }
        GuiActivator.getUIService().addPluginComponentListener(this);
    }

    /**
     * Adds the associated with this <tt>PluginComponentEvent</tt> component
     * to the appropriate container.
     */
    public void pluginComponentAdded(PluginComponentEvent event)
    {
        PluginComponent c = event.getPluginComponent();

        if (c.getContainer().equals(Container.CONTAINER_CONTACT_LIST))
        {
            Object constraints = null;

            if (c.getConstraints() != null)
                constraints = UIServiceImpl
                    .getBorderLayoutConstraintsFromContainer(c.getConstraints());
            else
                constraints = BorderLayout.SOUTH;

            this.getContentPane().add((Component) c.getComponent(), constraints);
        }
        else if (c.getContainer().equals(
            UIService.CONTAINER_CONTACT_LIST_SOUTH))
        {
            this.getContentPane().add(  (Component) c.getComponent(),
                                        BorderLayout.SOUTH);
        }
        else if (c.getContainer().equals(
            UIService.CONTAINER_CONTACT_LIST_NORTH))
        {
            this.getContentPane().add(  (Component) c.getComponent(),
                                        BorderLayout.NORTH);
        }
        else if (c.getContainer().equals(
            UIService.CONTAINER_CONTACT_LIST_EAST))
        {
            this.getContentPane().add(  (Component) c.getComponent(),
                                        BorderLayout.EAST);
        }
        else if (c.getContainer().equals(
            UIService.CONTAINER_CONTACT_LIST_WEST))
        {
            this.getContentPane().add(  (Component) c.getComponent(),
                                        BorderLayout.WEST);
        }

        this.pack();
    }

    /**
     * Removes the associated with this <tt>PluginComponentEvent</tt> component
     * from this container.
     */
    public void pluginComponentRemoved(PluginComponentEvent event)
    {
        PluginComponent c = event.getPluginComponent();

        Container containerID = c.getContainer();

        if (containerID.equals(Container.CONTAINER_CONTACT_LIST)
            || containerID.equals(UIService.CONTAINER_CONTACT_LIST_SOUTH)
            || containerID.equals(UIService.CONTAINER_CONTACT_LIST_NORTH)
            || containerID.equals(UIService.CONTAINER_CONTACT_LIST_EAST)
            || containerID.equals(UIService.CONTAINER_CONTACT_LIST_WEST))
        {
            this.getContentPane().remove((Component) c.getComponent());
        }
    }

    /**
     * The logo bar is positioned on the top of the window and is meant to
     * contain the application logo.
     */
    private class LogoBar
        extends JPanel
    {
        /**
         * Creates the logo bar and specify the size.
         */
        public LogoBar()
        {
            int width = SizeProperties.getSize("logoBarWidth");
            int height = SizeProperties.getSize("logoBarHeight");

            this.setMinimumSize(new Dimension(width, height));
            this.setPreferredSize(new Dimension(width, height));
        }

        /**
         * Paints the logo bar.
         * 
         * @param g the <tt>Graphics</tt> object used to paint the background
         * image of this logo bar.
         */
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);

            Image backgroundImage
                = ImageLoader.getImage(ImageLoader.WINDOW_TITLE_BAR);

            g.setColor(new Color(
                ColorProperties.getColor("logoBarBackground")));

            g.fillRect(0, 0, this.getWidth(), this.getHeight());
            g.drawImage(backgroundImage, 0, 0, null);
        }
    }
}