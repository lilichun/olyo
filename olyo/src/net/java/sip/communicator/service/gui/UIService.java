/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.service.gui;

import java.util.*;

import net.java.sip.communicator.service.protocol.*;

/**
 * The <tt>UIService</tt> offers generic access to the graphical user interface
 * for all modules that would like to interact with the user.
 * <p>
 * Through the <tt>UIService</tt> all modules can add their own components in
 * different menus, toolbars, etc. within the ui. Each <tt>UIService</tt>
 * implementation should export its supported "plugable" containers - a set of
 * <tt>Container</tt>s corresponding to different "places" in the application,
 * where a module can add a component.
 * <p>
 * The <tt>UIService</tt> provides also methods that would allow to other
 * modules to control the visibility, size and position of the main application
 * window. Some of these methods are: setVisible, minimize, maximize, resize,
 * move, etc. 
 * <p>
 * A way to show different types of simple windows is provided to allow other
 * modules to show different simple messages, like warning or error messages.
 * In order to show a simple warning message, a module should invoke the 
 * getPopupDialog method and then one of the showXXX methods, which corresponds
 * best to the required dialog. 
 * <p>
 * Certain components within the GUI, like "AddContact" window for example,
 * could be also shown from outside the ui. To make one of
 * these component exportable, the <tt>UIService</tt> implementation should attach
 * to it an <tt>WindowID</tt>. A window then could be shown, by invoking
 * <code>getExportedWindow(WindowID)</code> and then 
 * <code>show</code>. The <tt>WindowID</tt> above should be obtained from
 * <code>getSupportedExportedWindows</code>.
 * 
 * @author Yana Stamcheva
 */
public interface UIService
{
    /**
     * Main application window "file menu" container.
     * @deprecated replaced by {@link Container#CONTAINER_FILE_MENU}
     */
    public static final Container CONTAINER_FILE_MENU 
        = new Container("File");
    /**
     * Main application window "tools menu" container.
     * @deprecated replaced by {@link Container#CONTAINER_TOOLS_MENU}
     */
    public static final Container CONTAINER_TOOLS_MENU 
        = new Container("Tools");
    /**
     * Main application window "view menu" container.
     * @deprecated replaced by {@link Container#CONTAINER_VIEW_MENU}
     */
    public static final Container CONTAINER_VIEW_MENU 
        = new Container("View");
    /**
     * Main application window "help menu" container.
     * @deprecated replaced by {@link Container#CONTAINER_HELP_MENU}
     */    
    public static final Container CONTAINER_HELP_MENU 
        = new Container("Help");
    /**
     * Main application window "settings menu" container.
     * @deprecated replaced by {@link Container#CONTAINER_SETTINGS_MENU}
     */
    public static final Container CONTAINER_SETTINGS_MENU 
        = new Container("Settings");
    /**
     * Main application window main toolbar container.
     * @deprecated replaced by {@link Container#CONTAINER_MAIN_TOOL_BAR}
     */
    public static final Container CONTAINER_MAIN_TOOL_BAR 
        = new Container("MainToolBar");
    /**
     * Main application window main tabbedpane container.
     * @deprecated replaced by {@link Container#CONTAINER_MAIN_TABBED_PANE}
     */
    public static final Container CONTAINER_MAIN_TABBED_PANE
        = new Container("MainTabbedPane");
    /**
     * Chat window toolbar container.
     * @deprecated replaced by {@link Container#CONTAINER_CHAT_TOOL_BAR}
     */
    public static final Container CONTAINER_CHAT_TOOL_BAR 
        = new Container("ChatToolBar");
    /**
     * Main application window "right button menu" over a contact container.
     * @deprecated replaced by {@link Container#CONTAINER_CONTACT_RIGHT_BUTTON_MENU}
     */
    public static final Container CONTAINER_CONTACT_RIGHT_BUTTON_MENU
        = new Container("ContactRightButtonMenu");
    
    /**
     * Main application window "right button menu" over a group container.
     * @deprecated replaced by {@link Container#CONTAINER_GROUP_RIGHT_BUTTON_MENU}
     */
    public static final Container CONTAINER_GROUP_RIGHT_BUTTON_MENU
        = new Container("GroupRightButtonMenu");
        
    /**
     * Chat window "menu bar" container.
     * @deprecated replaced by {@link Container#CONTAINER_CHAT_MENU_BAR}
     */
    public static final Container CONTAINER_CHAT_MENU_BAR 
        = new Container("ChatMenuBar");
    /**
     * Chat window "file menu" container.
     * @deprecated replaced by {@link Container#CONTAINER_CHAT_FILE_MENU}
     */
    public static final Container CONTAINER_CHAT_FILE_MENU 
        = new Container("ChatFileMenu");
    /**
     * Chat window "edit menu" container.
     * @deprecated replaced by {@link Container#CONTAINER_CHAT_EDIT_MENU}
     */
    public static final Container CONTAINER_CHAT_EDIT_MENU 
        = new Container("ChatEditMenu");
    /**
     * Chat window "settings menu" container.
     * @deprecated replaced by {@link Container#CONTAINER_CHAT_SETTINGS_MENU}
     */
    public static final Container CONTAINER_CHAT_SETTINGS_MENU 
        = new Container("ChatSettingsMenu");

    /**
     * Chat window "help menu" container.
     * @deprecated replaced by {@link Container#CONTAINER_CHAT_HELP_MENU}
     */
    public static final Container CONTAINER_CHAT_HELP_MENU 
        = new Container("ChatHelpMenu");

    /**
     * Chat window "south area" container.
     * @deprecated replaced by {@link Container#CONTAINER_CHAT_WINDOW}
     * The south location is now indicated with the help of the constraints,
     * defined in the <tt>Container<tt>.
     */
    public static final Container CONTAINER_CHAT_WINDOW_SOUTH
        = new Container("ChatWindowSouth");

    /**
     * Indicates the west area on the left of the contact list.
     * @deprecated replaced by {@link Container#CONTAINER_CONTACT_LIST}
     * The west location is now indicated with the help of the constraints,
     * defined in the <tt>Container<tt>.
     */
    public static final Container CONTAINER_CONTACT_LIST_WEST
        = new Container("ContactListWest");

    /**
     * Indicates the east area on the right of the contact list.
     * @deprecated replaced by {@link Container#CONTAINER_CONTACT_LIST}
     * The east location is now indicated with the help of the constraints,
     * defined in the <tt>Container<tt>.
     */
    public static final Container CONTAINER_CONTACT_LIST_EAST
        = new Container("ContactListEast");

    /**
     * Indicates the north area on the top of the contact list.
     * @deprecated replaced by {@link Container#CONTAINER_CONTACT_LIST}
     * The north location is now indicated with the help of the constraints,
     * defined in the <tt>Container<tt>.
     */
    public static final Container CONTAINER_CONTACT_LIST_NORTH
        = new Container("ContactListNorth");

    /**
     * Indicates the south area on the bottom of the contact list.
     * @deprecated replaced by {@link Container#CONTAINER_CONTACT_LIST}
     * The south location is now indicated with the help of the constraints,
     * defined in the <tt>Container<tt>.
     */
    public static final Container CONTAINER_CONTACT_LIST_SOUTH
        = new Container("ContactListSouth");

    /**
     * Call history panel container.
     * @deprecated replaced by {@link Container#CONTAINER_CALL_HISTORY}
     */
    public static final Container CONTAINER_CALL_HISTORY
        = new Container("CallHistoryPanel");
    
    /*
     * Constraints
     */
    /**
     * Indicates the most left/top edge of a container.
     * @deprecated replaced by {@link Container#START}
     */
    public static final String START = "Start";
    /**
     * Indicates the most right/bottom edge of a container.
     * @deprecated replaced by {@link Container#END}
     */
    public static final String END = "End";
    /**
     * Indicates the top edge of a container.
     * @deprecated replaced by {@link Container#TOP}
     */
    public static final String TOP = "Top";
    /**
     * Indicates the bottom edge of a container.
     * @deprecated replaced by {@link Container#BOTTOM}
     */
    public static final String BOTTOM = "Bottom";
    /**
     * Indicates the left edge of a container.
     * @deprecated replaced by {@link Container#LEFT}
     */
    public static final String LEFT = "Left";
    /**
     * Indicates the right edge of a container.
     * @deprecated replaced by {@link Container#RIGHT}
     */
    public static final String RIGHT = "Right";
    
    /**
     * Returns TRUE if the application is visible and FALSE otherwise.
     * This method is meant to be used by the systray service in order to
     * detect the visibility of the application.
     * 
     * @return <code>true</code> if the application is visible and
     * <code>false</code> otherwise.
     * 
     * @see #setVisible(boolean)
     */
    public boolean isVisible();
    
    /**
     * Shows or hides the main application window depending on the value of
     * parameter <code>visible</code>. Meant to be used by the systray when it
     * needs to show or hide the application.
     * 
     * @param visible  if <code>true</code>, shows the main application window;
     * otherwise, hides the main application window.
     * 
     * @see #isVisible()
     */
    public void setVisible(boolean visible);
    
    /**
     * Minimizes the main application window.
     */
    public void minimize();
    
    /**
     * Mawimizes the main application window.
     */
    public void maximize();
    
    /**
     * Restores the main application window.
     */
    public void restore();
    
    /**
     * Resizes the main application window with the given width and height.
     * 
     * @param width The new width.
     * @param height The new height.
     */
    public void resize(int width, int height);
    
    /**
     * Moves the main application window to the given coordinates.
     * 
     * @param x The x coordinate.
     * @param y The y coordinate.
     */
    public void move(int x, int y);
        

    /**
     * Sets the exitOnClose property. When TRUE, the user could exit the
     * application by simply closing the main application window (by clicking
     * the X button or pressing Alt-F4). When set to FALSE the main application
     * window will be only hidden.
     * 
     * @param exitOnClose When TRUE, the user could exit the
     * application by simply closing the main application window (by clicking
     * the X button or pressing Alt-F4). When set to FALSE the main application
     * window will be only hidden.
     */
    public void setExitOnMainWindowClose(boolean exitOnClose);
    
    /**
     * Returns TRUE if the application could be exited by closing the main
     * application window, otherwise returns FALSE.
     * 
     * @return Returns TRUE if the application could be exited by closing the
     * main application window, otherwise returns FALSE
     */
    public boolean getExitOnMainWindowClose();
    
    /**
     * Returns an exported window given by the <tt>WindowID</tt>.
     * This could be for example the "Add contact" window or any other window
     * within the application. The <tt>windowID</tt> should be one of the
     * WINDOW_XXX obtained by the <tt>getSupportedExportedWindows</tt> method.
     *  
     * @param windowID One of the WINDOW_XXX WindowID-s.
     * @throws IllegalArgumentException if the specified <tt>windowID</tt>
     * is not recognized by the implementation (note that implementations
     * MUST properly handle all WINDOW_XXX ID-s.
     * @return the window to be shown
     * @see #getSupportedExportedWindows()
     */
    public ExportedWindow getExportedWindow(WindowID windowID)
        throws IllegalArgumentException;
    
    /**
     * Returns a configurable popup dialog, that could be used to show either
     * a warning message, error message, information message, etc. or to prompt
     * user for simple one field input or to question the user.
     *  
     * @return a <code>PopupDialog</code>.
     * @see PopupDialog
     */
    public PopupDialog getPopupDialog();
         
    /**
     * Returns the <tt>Chat</tt> corresponding to the given <tt>Contact</tt>.
     * 
     * @param contact the <tt>Contact</tt> for which the searched chat is about.
     * @return the <tt>Chat</tt> corresponding to the given <tt>Contact</tt>.
     */
    public Chat getChat(Contact contact);

    /**
     * Returns the <tt>Chat</tt> corresponding to the given <tt>ChatRoom</tt>.
     * 
     * @param chatRoom the <tt>ChatRoom</tt> for which the searched chat is
     * about.
     * @return the <tt>Chat</tt> corresponding to the given <tt>ChatRoom</tt>.
     */
    public Chat getChat(ChatRoom chatRoom);

    /**
     * Returns the selected <tt>Chat</tt>.
     * 
     * @return the selected <tt>Chat</tt>.
     */
    public Chat getCurrentChat();
    
    /**
     * Returns an <tt>ExportableComponent</tt> that corresponds to an
     * authentication window for the given protocol provider and user
     * inromation. Initially this method is meant to be used by the
     * <tt>SystrayService</tt> in order to show a login window when user tries
     * to connect using the systray menu.
     *     
     * @param protocolProvider the <tt>ProtocolProviderService</tt> for which
     * the authentication window is about.
     * @param realm the realm
     * @param userCredentials the <tt>UserCredentials</tt>, where the username
     * and password details are stored
     * @param isUserNameEditable indicates if the user name could be changed
     * by user.
     * @return an <tt>ExportableComponent</tt> that corresponds to an
     * authentication window for the given protocol provider and user information.
     */
    public ExportedWindow getAuthenticationWindow(
        ProtocolProviderService protocolProvider,
        String realm,
        UserCredentials userCredentials,
        boolean isUserNameEditable);
    
    /**
     * Returns the <tt>ConfigurationWindow</tt> implementation for this
     * UIService implementation. The <tt>ConfigurationWindow</tt> is a
     * contianer contianing <tt>ConfigurationForm</tt>s. It is meant to be
     * implemented by the UIService implementation to provide a mechanism
     * for adding and removing configuration forms in the GUI. 
     * 
     * @return the <tt>ConfigurationWindow</tt> implementation for this
     * UIService implementation
     */
    public ConfigurationWindow getConfigurationWindow();
    
    /**
     * Returns an iterator over a set of windowID-s. Each <tt>WindowID</tt>
     * points to a window in the current UI implementation. Each
     * <tt>WindowID</tt> in the set is one of the constants in the
     * <tt>ExportedWindow</tt> interface. The method is meant to be used by
     * bundles that would like to have access to some windows in the gui
     * - for example the "Add contact" window, the "Settings" window, the
     * "Chat window", etc.
     *       
     * @return Iterator An iterator to a set containing WindowID-s 
     * representing all exported windows supported by the current UI
     * implementation.
     */
    public Iterator getSupportedExportedWindows();
    
    /**
     * Chechks if a window with the given <tt>WindowID</tt> is contained in the
     * current UI implementation.
     * 
     * @param windowID one of the <tt>WindowID</tt>-s, defined in the
     * <tt>ExportedWindow</tt> interface. 
     * @return <code>true</code> if the component with the given
     * <tt>WindowID</tt> is contained in the current UI implementation,
     * <code>false</code> otherwise.
     */
    public boolean isExportedWindowSupported(WindowID windowID);
    
    /**
     * Returns the <tt>AccountRegistrationWizardContainer</tt> for the current
     * UIService implementation. The <tt>AccountRegistrationWizardContainer</tt>
     * is meant to be implemented by the UI service implementation in order to
     * allow other modules to add to the GUI <tt>AccountRegistrationWizard</tt>
     * s. Each of these wizards is made for a given protocol and should provide
     * a sequence of user interface forms through which the user could
     * registrate a new account.
     * 
     * @return Returns the <tt>AccountRegistrationWizardContainer</tt> for the
     * current UIService implementation.
     */
    public AccountRegistrationWizardContainer getAccountRegWizardContainer();
    
    /**
     * Adds the specified UI component to the container given by Container. 
     * The method is meant to be used by plugins or bundles that would like to
     * add components to the user interface. The <tt>containerID</tt> is used 
     * by the implementation to determine the place where the component should
     * be added. The <tt>containerID</tt> SHOULD be one of the CONTAINER_XXX 
     * constants. It is up to the service implementation to verify that
     * <tt>component</tt> is an instance of a class compatible with the gui
     * library used by it. If this is not the case and adding the requested
     * object would not be possible the implementation MUST through a
     * ClassCastException exception. Implementations of this service MUST
     * understand and know how to handle all Container-s defined by this
     * interface, they MAY also define additional constraints. In case the
     * addComponent method is called with a <tt>containerID</tt> that the
     * implementation does not understand it MUST through a
     * java.lang.IllegalArgumentException. 
     * <br>
     * @param containerID One of the CONTAINER_XXX Container-s. 
     * @param component The component to be added.
     * @throws ClassCastException if <tt>component</tt> is not an
     * instance of a class supported by the service implementation. An SWT impl
     * would, for example through a ClassCastException if handed a
     * java.awt.Component
     * @throws IllegalArgumentException if the specified <tt>containerID</tt>
     * is not recognized by the implementation (note that implementations
     * MUST properly handle all CONTAINER_XXX containerID-s.
     * 
     * @deprecated replaced by {@link PluginComponent}
     * This method should not be used anymore. Instead plugin
     * components should implement the <tt>PluginComponent</tt> interface and
     * should register their implementations of this interface through the OSGI
     * bundle context.
     * @see PluginComponent
     */
    public void addComponent(Container containerID, Object component)
        throws ClassCastException, IllegalArgumentException;
    
    /**
     * Adds the specified UI component to the container given by Container. 
     * The method is meant to be used by plugins or bundles that would like to
     * add components to the user interface. The <tt>containerID</tt> is used 
     * by the implementation to determine the place where the component should
     * be added. The <tt>containerID</tt> SHOULD be one of the CONTAINER_XXX 
     * constants.
     * <br>
     * The <tt>ContactAwareComponent</tt> is a plugin component that
     * is interested of the current meta contact in the container.
     * <br>
     * Implementations of this service MUST understand and know how to handle
     * all Container-s defined by this interface, they MAY also define
     * additional constraints. In case the addComponent method is called with a
     * <tt>containerID</tt> that the implementation does not understand it MUST
     * through a java.lang.IllegalArgumentException. 
     * <br>
     * @param containerID One of the CONTAINER_XXX Container-s. 
     * @param component The component to be added.
     * @throws ClassCastException if <tt>component</tt> is not an
     * instance of a class supported by the service implementation. An SWT impl
     * would, for example through a ClassCastException if handed a
     * java.awt.Component
     * @throws IllegalArgumentException if the specified <tt>containerID</tt>
     * is not recognized by the implementation (note that implementations
     * MUST properly handle all CONTAINER_XXX containerID-s.
     * 
     * @deprecated replaced by {@link PluginComponent}
     * This method should not be used anymore. Instead plugin
     * components should implement the <tt>PluginComponent</tt> interface and
     * should register their implementations of this interface through the OSGI
     * bundle context.
     * @see PluginComponent
     */
    public void addComponent(Container containerID,
        ContactAwareComponent component)
        throws ClassCastException, IllegalArgumentException;
    
    
    /**
     * Adds the specified UI component to the container given by
     * <tt>containerID</tt> at the position specified by <tt>constraint</tt>
     * String. The method is meant to be used by plugins or bundles that would
     * like to add components to the user interface. The <tt>containerID</tt>
     * is used by the implementation to determine the place where the component
     * should be added. The <tt>containerID</tt> SHOULD be one of the
     * CONTAINER_XXX constants. The <tt>constraint</tt> String is used to
     * determine the exact position of the component in the container (LEFT,
     * RIGHT, START, etc.). The <tt>constraint</tt> String SHOULD be one of the 
     * START, END, TOP, BOTTOM, etc. String constants.
     * <br> 
     * It is up to the service implementation to verify that <tt>component</tt>
     * is an instance of a class compatible with the gui library used by it. If
     * this is not the case and adding the requested object would not be 
     * possible the implementation MUST through a ClassCastException exception.
     * Implementations of this service MUST understand and know how to handle
     * all Container-s defined by this interface, they MAY also define
     * additional constraints. In case the addComponent method is called with a
     * <tt>containerID</tt> that the implementation does not understand it MUST
     * through a java.lang.IllegalArgumentException 
     * <br>
     * @param containerID One of the CONTAINER_XXX Container-s.
     * @param constraint One of the START, END, BOTTOM, etc. String constants. 
     * @param component The component to be added.
     * @throws ClassCastException if <tt>component</tt> is not an
     * instance of a class supported by the service implementation. An SWT impl
     * would, for example through a ClassCastException if handed a
     * java.awt.Component
     * @throws IllegalArgumentException if the specified <tt>containerID</tt>
     * is not recognized by the implementation (note that implementations
     * MUST properly handle all CONTAINER_XXX containerID-s.
     * 
     * @deprecated replaced by {@link PluginComponent}
     * This method should not be used anymore. Instead plugin
     * components should implement the <tt>PluginComponent</tt> interface and
     * should register their implementations of this interface through the OSGI
     * bundle context.
     * @see PluginComponent
     */
    public void addComponent(Container containerID, 
                String constraint, Object component)
        throws ClassCastException, IllegalArgumentException;
    
    /**
     * Adds the specified UI component to the container given by
     * <tt>containerID</tt> at the position specified by <tt>constraint</tt>
     * String. The method is meant to be used by plugins or bundles that would
     * like to add components to the user interface. The <tt>containerID</tt>
     * is used by the implementation to determine the place where the component
     * should be added. The <tt>containerID</tt> SHOULD be one of the
     * CONTAINER_XXX constants. The <tt>constraint</tt> String is used to
     * determine the exact position of the component in the container (LEFT,
     * RIGHT, START, etc.). The <tt>constraint</tt> String SHOULD be one of the 
     * START, END, TOP, BOTTOM, etc. String constants.
     * <br>
     * The <tt>ContactAwareComponent</tt> is a plugin component that
     * is interested of the current meta contact in the container. 
     * <br>
     * @param containerID One of the CONTAINER_XXX Container-s.
     * @param constraint One of the START, END, BOTTOM, etc. String constants. 
     * @param component The component to be added.
     * @throws ClassCastException if <tt>component</tt> is not an
     * instance of a class supported by the service implementation. An SWT impl
     * would, for example through a ClassCastException if handed a
     * java.awt.Component
     * @throws IllegalArgumentException if the specified <tt>containerID</tt>
     * is not recognized by the implementation (note that implementations
     * MUST properly handle all CONTAINER_XXX containerID-s.
     * 
     * @deprecated replaced by {@link PluginComponent}
     * This method should not be used anymore. Instead plugin
     * components should implement the <tt>PluginComponent</tt> interface and
     * should register their implementations of this interface through the OSGI
     * bundle context.
     * @see PluginComponent
     */
    public void addComponent(Container containerID, 
                String constraint, ContactAwareComponent component)
        throws ClassCastException, IllegalArgumentException;
    
    /**
     * Removes the given UI component from the container given by
     * <tt>containerID</tt>. 
     * This method is meant to be used by plugins or bundles that have added
     * their components to the user interface and for some reason want to remove
     * them. The <tt>containerID</tt> is used by the implementation to determine
     * the place where the component was added. The <tt>containerID</tt> SHOULD
     * be one of the CONTAINER_XXX constants. It is up to the service
     * implementation to verify that the <tt>component</tt> is really contained
     * in the specified container. If this is not the case nothing will happen.
     * <br>
     * @param containerID one of the CONTAINER_XXX Container-s 
     * @param component the component to remove
     * @throws IllegalArgumentException if the specified <tt>containerID</tt>
     * is not recognized by the implementation (note that implementations
     * MUST properly handle all CONTAINER_XXX containerID-s.
     * 
     * @deprecated replaced by {@link PluginComponent}
     * This method should not be used anymore. Instead plugin
     * components should implement the <tt>PluginComponent</tt> interface and
     * should register their implementations of this interface through the OSGI
     * bundle context.
     * @see PluginComponent
     */
    public void removeComponent(Container containerID, Object component)
        throws IllegalArgumentException;

    /**
     * Returns an iterator over a set containing containerID-s pointing to
     * containers supported by the current UI implementation. Each containerID
     * in the set is one of the CONTAINER_XXX constants. The method is meant to
     * be used by plugins or bundles that would like to add components to the 
     * user interface. Before adding any component they should use this method
     * to obtain all possible places, which could contain external components,
     * like different menus, toolbars, etc.
     * 
     * @return Iterator An iterator to a set containing containerID-s 
     * representing all containers supported by the current UI implementation.
     */
    public Iterator getSupportedContainers();

    /**
     * Chechks if the container with the given <tt>Container</tt> is supported
     * from the current UI implementation.
     * 
     * @param containderID One of the CONTAINER_XXX Container-s. 
     * @return <code>true</code> if the contaner with the given 
     * <tt>Container</tt> is supported from the current UI implementation,
     * <code>false</code> otherwise.
     */
    public boolean isContainerSupported(Container containderID);

    /**
     * Returns an iterator over a set of all constraints supported by the
     * given <tt>containerID</tt>. Each constraint in the set is one of the
     * START, END, TOP, BOTTOM, etc. constants. This method is meant to be used
     * to obtain all layout constraints supported by a given container.
     * 
     * @param containerID The containerID pointing to the desired container.
     * @return Iterator An iterator to a set containing all component
     * constraints
     */
    public Iterator getConstraintsForContainer(Container containerID);

    /**
     * Returns an Iterator over a set of all components added to a given
     * constraint. Meant to be called in the process of initialization of the
     * container, defined by the given constraint in order to obtain all
     * external components that should be added in it.
     * 
     * @param containerID One of the containerID-s supported by the current UI
     * implementation.
     * @return An Iterator to a set containing all components added to a given
     * constraint.
     */
    public Iterator getComponentsForContainer(Container containerID)
        throws IllegalArgumentException;
}
