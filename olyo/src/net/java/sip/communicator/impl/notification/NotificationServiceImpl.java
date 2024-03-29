/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.notification;

import java.util.*;

import net.java.sip.communicator.impl.notification.EventNotification.*;
import net.java.sip.communicator.service.configuration.*;
import net.java.sip.communicator.service.notification.*;
import net.java.sip.communicator.service.notification.event.*;
import net.java.sip.communicator.util.*;

/**
 * The implementation of the <tt>NotificationService</tt>.
 * 
 * @author Yana Stamcheva
 */
public class NotificationServiceImpl
    implements NotificationService
{
    private Logger logger = Logger.getLogger(NotificationServiceImpl.class);
    
    /**
     * A set of all registered event notifications.
     */
    private Hashtable notificationsTable = new Hashtable();

    /**
     * A list of all registered <tt>NotificationChangeListener</tt>s.
     */
    private Vector changeListeners = new Vector();
    
    /**
     * Creates an instance of <tt>NotificationServiceImpl</tt> by loading all
     * previously saved notifications. 
     */
    public NotificationServiceImpl()
    {
        // Load all previously saved notifications.
        this.loadNotifications();
    }
    
    /**
     * Returns an instance of <tt>CommandNotificationHandlerImpl</tt>.
     * 
     * @return an instance of <tt>CommandNotificationHandlerImpl</tt>.
     */
    public CommandNotificationHandler createCommandNotificationHandler(
        String commandDescriptor)
    {
        return new CommandNotificationHandlerImpl(commandDescriptor);
    }

    /**
     * Returns an instance of <tt>LogMessageNotificationHandlerImpl</tt>.
     * 
     * @return an instance of <tt>LogMessageNotificationHandlerImpl</tt>.
     */
    public LogMessageNotificationHandler createLogMessageNotificationHandler(
                                                                String logType)
    {
        return new LogMessageNotificationHandlerImpl(logType);
    }

    /**
     * Returns an instance of <tt>PopupMessageNotificationHandlerImpl</tt>.
     * 
     * @return an instance of <tt>PopupMessageNotificationHandlerImpl</tt>.
     */
    public PopupMessageNotificationHandler createPopupMessageNotificationHandler(
                                                        String defaultMessage)
    {
        return new PopupMessageNotificationHandlerImpl(defaultMessage);
    }

    /**
     * Returns an instance of <tt>SoundNotificationHandlerImpl</tt>.
     * 
     * @return an instance of <tt>SoundNotificationHandlerImpl</tt>.
     */
    public SoundNotificationHandler createSoundNotificationHandler(
        String soundFileDescriptor, int loopInterval)
    {
        return new SoundNotificationHandlerImpl(
            soundFileDescriptor, loopInterval);
    }
    
    /**
     * Creates a new <tt>EventNotification</tt> or obtains the corresponding
     * existing one and registers a new action in it.
     * 
     * @param eventType the name of the event (as defined by the plugin that's
     * registering it) that we are setting an action for.
     * @param actionType the type of the action that is to be executed when the
     * specified event occurs (could be one of the ACTION_XXX fields).
     * @param handler the <tt>NotificationActionHandler</tt> responsible for
     * handling the given <tt>actionType</tt> 
     */
    public void registerNotificationForEvent(   String eventType,
                                                String actionType,
                                                NotificationActionHandler handler)
    {
        EventNotification notification = null;

        if(notificationsTable.containsKey(eventType))
            notification = (EventNotification) notificationsTable.get(eventType);
        else
        {
            notification = new EventNotification(eventType);
            
            notificationsTable.put(eventType, notification);
            
            this.fireNotificationEventTypeEvent(
                NotificationEventTypeEvent.EVENT_TYPE_ADDED, eventType);
            
            // Save the notification through the ConfigurationService.
            this.saveNotification(  eventType,
                                    actionType,
                                    handler);
        }
        
        Object existingAction = notification.addAction(actionType, handler);
        
        // We fire the appropriate event depending on whether this is an
        // already existing actionType or a new one.
        if (existingAction != null)
        {
            fireNotificationActionTypeEvent(
                NotificationActionTypeEvent.ACTION_CHANGED,
                eventType,
                actionType,
                handler);
        }
        else
        {
            fireNotificationActionTypeEvent(
                NotificationActionTypeEvent.ACTION_ADDED,
                eventType,
                actionType,
                handler);
        }
    }
    
    /**
     * Creates a new <tt>EventNotification</tt> or obtains the corresponding
     * existing one and registers a new action in it.
     * 
     * @param eventType the name of the event (as defined by the plugin that's
     * registering it) that we are setting an action for.
     * @param actionType the type of the action that is to be executed when the
     * specified event occurs (could be one of the ACTION_XXX fields).
     * @param actionDescriptor a String containing a description of the action
     * (a URI to the sound file for audio notifications or a command line for
     * exec action types) that should be executed when the action occurs.
     * @param defaultMessage the default message to use if no specific message
     * has been provided when firing the notification.
     */
    public void registerNotificationForEvent(   String eventType,
                                                String actionType,
                                                String actionDescriptor,
                                                String defaultMessage)
    {   
        if (actionType.equals(NotificationService.ACTION_SOUND))
        {
            registerNotificationForEvent (eventType, actionType,
                new SoundNotificationHandlerImpl(actionDescriptor, -1));
        }
        else if (actionType.equals(NotificationService.ACTION_LOG_MESSAGE))
        {
            registerNotificationForEvent (eventType, actionType,
                new LogMessageNotificationHandlerImpl(
                    LogMessageNotificationHandler.INFO_LOG_TYPE));
        }
        else if (actionType.equals(NotificationService.ACTION_POPUP_MESSAGE))
        {
            registerNotificationForEvent (eventType, actionType,
                new PopupMessageNotificationHandlerImpl(defaultMessage));
        }
        else if (actionType.equals(NotificationService.ACTION_COMMAND))
        {
            registerNotificationForEvent (eventType, actionType,
                new CommandNotificationHandlerImpl(actionDescriptor));
        }
    }

    /**
     * Removes the <tt>EventNotification</tt> corresponding to the given
     * <tt>eventType</tt> from the table of registered event notifications.
     * 
     * @param eventType the name of the event (as defined by the plugin that's
     * registering it) to be removed.
     */
    public void removeEventNotification(String eventType)
    {
        notificationsTable.remove(eventType);
        
        this.fireNotificationEventTypeEvent(
            NotificationEventTypeEvent.EVENT_TYPE_REMOVED, eventType);
    }

    /**
     * Removes the given actionType from the list of actions registered for the
     * given <tt>eventType</tt>.
     * 
     * @param eventType the name of the event (as defined by the plugin that's
     * registering it) for which we'll remove the notification.
     * @param actionType the type of the action that is to be executed when the
     * specified event occurs (could be one of the ACTION_XXX fields).
     */
    public void removeEventNotificationAction(  String eventType,
                                                String actionType)
    {
        EventNotification notification
            = (EventNotification) notificationsTable.get(eventType);
        
        if(notification == null)
            return;
        
        Action action = notification.getAction(actionType);
        
        notification.removeAction(actionType);
        
        fireNotificationActionTypeEvent(
            NotificationActionTypeEvent.ACTION_REMOVED,
            eventType,
            action.getActionType(),
            action.getActionHandler());
    }

    /**
     * Returns an iterator over a list of all events registered in this
     * notification service. Each line in the returned list consists of
     * a String, representing the name of the event (as defined by the plugin
     * that registered it).
     *   
     * @return an iterator over a list of all events registered in this
     * notifications service
     */
    public Iterator getRegisteredEvents()
    {
        return Collections.unmodifiableSet(
            notificationsTable.keySet()).iterator();
    }
    
    /**
     * Goes through all actions registered for the given <tt>eventType</tt> and
     * returns a Map of all (actionType, actionDescriptor) key-value pairs. 
     * 
     * @param eventType the name of the event that we'd like to retrieve actions
     * for
     * @return a <tt>Map</tt> containing the <tt>actionType</tt>s (as keys) and
     * <tt>actionHandler</tt>s (as values) that should be executed when
     * an event with the specified name has occurred, or null if no actions
     * have been defined for <tt>eventType</tt>.
     */
    public Map getEventNotifications(String eventType)
    {
        Hashtable actions = new Hashtable();

        EventNotification notification
            = (EventNotification) notificationsTable.get(eventType);
        
        if(notification == null)
            return null;

        Iterator srcActions = notification.getActions().values().iterator();
        
        while(srcActions.hasNext())
        {
            Action action = (Action) srcActions.next();
            
            actions.put(action.getActionType(), action.getActionHandler());
        }
        
        return actions;
    }

    /**
     * Returns the notification handler corresponding to the given
     * <tt>eventType</tt> and <tt>actionType</tt>.
     * 
     * @param eventType the type of the event that we'd like to retrieve.
     * @param actionType the type of the action that we'd like to retrieve a
     * descriptor for.
     * @return the notification handler of the action to be executed
     * when an event of the specified type has occurred.
     */
    public NotificationActionHandler getEventNotificationActionHandler(
                                                            String eventType,
                                                            String actionType)
    {
        EventNotification notification
            = (EventNotification) notificationsTable.get(eventType);

        if(notification == null)
            return null;

        EventNotification.Action action = notification.getAction(actionType);

        if(action == null)
            return null;

        return action.getActionHandler();
    }

    /**
     * Adds the given <tt>listener</tt> to the list of change listeners.
     * 
     * @param listener the listener that we'd like to register to listen for
     * changes in the event notifications stored by this service.
     */
    public void addNotificationChangeListener(
        NotificationChangeListener listener)
    {
        synchronized (changeListeners)
        {
            changeListeners.add(listener);
        }
    }

    /**
     * Removes the given <tt>listener</tt> from the list of change listeners.
     * 
     * @param listener the listener that we'd like to remove
     */
    public void removeNotificationChangeListener(
        NotificationChangeListener listener)
    {
        synchronized (changeListeners)
        {
            changeListeners.remove(listener);
        }
    }

    /**
     * If there is a registered event notification of the given
     * <tt>eventType</tt> and the event notification is currently activated, we
     * go through the list of registered actions and execute them.
     * 
     * @param eventType the type of the event that we'd like to fire a
     * notification for.
     * @param title the title of the given message
     * @param message the message to use if and where appropriate (e.g. with
     * systray or log notification.)
     */
    public void fireNotification(String eventType, String title, String message)
    {
        EventNotification notification
            = (EventNotification) notificationsTable.get(eventType);
        
        if(notification == null || !notification.isActive())
            return;
        
        Iterator actions = notification.getActions().values().iterator();

        while(actions.hasNext())
        {
            Action action = (Action) actions.next();
            
            String actionType = action.getActionType();

            NotificationActionHandler handler = action.getActionHandler();

            if (!handler.isEnabled())
                continue;

            if (actionType.equals(NotificationService.ACTION_POPUP_MESSAGE))
            {
                ((PopupMessageNotificationHandler) handler)
                    .popupMessage(title, message);
            }
            else if (actionType.equals(NotificationService.ACTION_LOG_MESSAGE))
            {
                ((LogMessageNotificationHandler) handler)
                    .logMessage(message);
            }
            else if (actionType.equals(NotificationService.ACTION_SOUND))
            {
                ((SoundNotificationHandler) handler)
                    .start();
            }
            else if (actionType.equals(NotificationService.ACTION_COMMAND))
            {
                ((CommandNotificationHandler) handler)
                    .execute();
            }
        }
    }

    /**
     * If there is a registered event notification of the given
     * <tt>eventType</tt> and the event notification is currently activated, we
     * go through the list of registered actions and execute them.
     * 
     * @param eventType the type of the event that we'd like to fire a
     * notification for.
     */
    public void fireNotification(String eventType)
    {
        this.fireNotification(eventType, null, null);
    }
    
    /**
     * Saves the event notification given by these parameters through the
     * <tt>ConfigurationService</tt>.
     * 
     * @param eventType the name of the event
     * @param actionType the type of action
     * @param actionHandler the notification action handler responsible for
     * handling the given <tt>actionType</tt>
     */
    private void saveNotification(  String eventType,
                                    String actionType,
                                    NotificationActionHandler actionHandler)
    {
        ConfigurationService configService
            = NotificationActivator.getConfigurationService();
        
        String eventPrefix = "net.java.sip.communicator.impl.notifications";
        
        String eventTypeNodeName = null;
        String actionTypeNodeName = null;

        List eventTypes = configService
                .getPropertyNamesByPrefix(eventPrefix, true);
    
        Iterator eventTypesIter = eventTypes.iterator();
    
        while(eventTypesIter.hasNext())
        {
            String eventTypeRootPropName
                = (String) eventTypesIter.next();
    
            String eType
                = configService.getString(eventTypeRootPropName);
            
            if(eType.equals(eventType))
                eventTypeNodeName = eventTypeRootPropName;
        }

        // If we didn't find the given event type in the configuration we save
        // it here.
        if(eventTypeNodeName == null)
        {
            eventTypeNodeName = eventPrefix
                                + ".eventType" 
                                + Long.toString(System.currentTimeMillis());
            
            configService.setProperty(eventTypeNodeName, eventType);
        }
        
        // Go through contained actions.
        String actionPrefix = eventTypeNodeName + ".actions";
    
        List actionTypes = configService
                .getPropertyNamesByPrefix(actionPrefix, true);
        
        Iterator actionTypesIter = actionTypes.iterator();
        
        while(actionTypesIter.hasNext())
        {
            String actionTypeRootPropName
                = (String) actionTypesIter.next();
        
            String aType
                = configService.getString(actionTypeRootPropName);
            
            if(aType.equals(actionType))
                actionTypeNodeName = actionTypeRootPropName;
        }

        // If we didn't find the given actionType in the configuration we save
        // it here.
        if(actionTypeNodeName == null)
        {
            actionTypeNodeName = actionPrefix
                                    + ".actionType"
                                    + Long.toString(System.currentTimeMillis());
        
            configService.setProperty(actionTypeNodeName, actionType);        
        }
        
        if(actionHandler instanceof SoundNotificationHandler)
        {
            SoundNotificationHandler soundHandler
                = (SoundNotificationHandler) actionHandler;
            
            configService.setProperty(
                actionTypeNodeName + ".soundFileDescriptor",
                soundHandler.getDescriptor());
            
            configService.setProperty(
                actionTypeNodeName + ".loopInterval",
                new Integer(soundHandler.getLoopInterval()));
        }
        else if(actionHandler instanceof PopupMessageNotificationHandler)
        {
            PopupMessageNotificationHandler messageHandler
                = (PopupMessageNotificationHandler) actionHandler;
        
            configService.setProperty(
                actionTypeNodeName + ".defaultMessage",
                messageHandler.getDefaultMessage());            
        }
        else if(actionHandler instanceof LogMessageNotificationHandler)
        {
            LogMessageNotificationHandler logMessageHandler
                = (LogMessageNotificationHandler) actionHandler;
    
            configService.setProperty(
                actionTypeNodeName + ".logType",
                logMessageHandler.getLogType());
        }
        else if(actionHandler instanceof CommandNotificationHandler)
        {
            CommandNotificationHandler commandHandler
                = (CommandNotificationHandler) actionHandler;
    
            configService.setProperty(
                actionTypeNodeName + ".commandDescriptor",
                commandHandler.getDescriptor());
        }
    }
    
    /**
     * Loads all previously saved event notifications.
     */
    private void loadNotifications()
    {
        ConfigurationService configService
            = NotificationActivator.getConfigurationService();
        
        String prefix = "net.java.sip.communicator.impl.notifications";
        
        List eventTypes = configService
                .getPropertyNamesByPrefix(prefix, true);
    
        Iterator eventTypesIter = eventTypes.iterator();
    
        while(eventTypesIter.hasNext())
        {
            String eventTypeRootPropName
                = (String) eventTypesIter.next();
            
            String eventType
                = configService.getString(eventTypeRootPropName);
        
            List actions = configService
                .getPropertyNamesByPrefix(
                    eventTypeRootPropName + ".actions", true);
            
            Iterator actionsIter = actions.iterator();

            while(actionsIter.hasNext())
            {
                String actionPropName
                    = (String) actionsIter.next();
                
                String actionType
                    = configService.getString(actionPropName);
                
                NotificationActionHandler handler = null;
                
                if(actionType.equals(ACTION_SOUND))
                {
                    String soundFileDescriptor
                        = configService.getString(
                            actionPropName + ".soundFileDescriptor");
                
                    String loopInterval
                        = configService.getString(
                            actionPropName + ".loopInterval");
            
                    handler = new SoundNotificationHandlerImpl(
                        soundFileDescriptor,
                        new Integer(loopInterval).intValue());
                }
                else if(handler instanceof PopupMessageNotificationHandler)
                {
                    String defaultMessage
                        = configService.getString(
                            actionPropName + ".defaultMessage");
            
                    handler = new PopupMessageNotificationHandlerImpl(
                                                                defaultMessage);
                }
                else if(handler instanceof LogMessageNotificationHandler)
                {
                    String logType
                        = configService.getString(
                            actionPropName + ".logType");
            
                    handler = new LogMessageNotificationHandlerImpl(logType);
                }
                else if(handler instanceof CommandNotificationHandler)
                {
                    String commandDescriptor
                        = configService.getString(
                            actionPropName + ".commandDescriptor");
        
                    handler = new LogMessageNotificationHandlerImpl(
                                                            commandDescriptor);            
                }
                
                // Load the data in the notifications table.
                EventNotification notification
                    = new EventNotification(eventType);
                
                notificationsTable.put(eventType, notification);
                
                notification.addAction(actionType, handler);
            }
        }
    }

    /**
     * Finds the <tt>EventNotification</tt> corresponding to the given
     * <tt>eventType</tt> and marks it as activated/desactivated.
     * 
     * @param eventType the name of the event, which actions should be activated
     * /desactivated. 
     * @param isActive indicates whether to activate or desactivate the actions
     * related to the specified <tt>eventType</tt>.
     */
    public void setActive(String eventType, boolean isActive)
    {
        EventNotification eventNotification
            = (EventNotification) notificationsTable.get(eventType);
        
        if(eventNotification == null)
            return;
        
        eventNotification.setActive(isActive);    
    }

    /**
     * Finds the <tt>EventNotification</tt> corresponding to the given
     * <tt>eventType</tt> and returns its isActive status.
     * 
     * @param eventType the name of the event (as defined by the plugin that's
     * registered it) that we are checking.
     * @return <code>true</code> if actions for the specified <tt>eventType</tt>
     * are activated, <code>false</code> - otherwise. If the given
     * <tt>eventType</tt> is not contained in the list of registered event
     * types - returns <code>false</code>.
     */
    public boolean isActive(String eventType)
    {
        EventNotification eventNotification
            = (EventNotification) notificationsTable.get(eventType);
        
        if(eventNotification == null)
            return false;
        
        return eventNotification.isActive();
    }
    
    /**
     * Notifies all registered <tt>NotificationChangeListener</tt>s that a
     * <tt>NotificationEventTypeEvent</tt> has occured.
     * 
     * @param eventType the type of the event, which is one of EVENT_TYPE_XXX
     * constants declared in the <tt>NotificationEventTypeEvent</tt> class.
     * @param sourceEventType the <tt>eventType</tt>, for which this event is
     * about
     */
    private void fireNotificationEventTypeEvent(String eventType,
                                                String sourceEventType)
    {
        NotificationEventTypeEvent event
            = new NotificationEventTypeEvent(this, eventType, sourceEventType);
        
        NotificationChangeListener listener;
        
        for (int i = 0 ; i < changeListeners.size(); i ++)
        {
            listener = (NotificationChangeListener) changeListeners.get(i);
            
            if (eventType.equals(NotificationEventTypeEvent.EVENT_TYPE_ADDED))
            {
                listener.eventTypeAdded(event);
            }
            else if (eventType.equals(
                NotificationEventTypeEvent.EVENT_TYPE_REMOVED))
            {
                listener.eventTypeRemoved(event);
            }
        }
    }
    
    /**
     * Notifies all registered <tt>NotificationChangeListener</tt>s that a
     * <tt>NotificationActionTypeEvent</tt> has occured.
     * 
     * @param eventType the type of the event, which is one of ACTION_XXX
     * constants declared in the <tt>NotificationActionTypeEvent</tt> class.
     * @param sourceEventType the <tt>eventType</tt>, which is the parent of the
     * action
     * @param sourceActionType the <tt>actionType</tt>, for which the event is
     * about
     * @param actionHandler the notification action handler
     */
    private void fireNotificationActionTypeEvent(
                                        String eventType,
                                        String sourceEventType,
                                        String sourceActionType,
                                        NotificationActionHandler actionHandler)
    {
        NotificationActionTypeEvent event
            = new NotificationActionTypeEvent(  this,
                                                eventType,
                                                sourceEventType,
                                                sourceActionType,
                                                actionHandler);

        NotificationChangeListener listener;

        for (int i = 0 ; i < changeListeners.size(); i ++)
        {
            listener = (NotificationChangeListener) changeListeners.get(i);

            if (eventType.equals(NotificationActionTypeEvent.ACTION_ADDED))
            {
                listener.actionAdded(event);
            }
            else if (eventType.equals(
                NotificationActionTypeEvent.ACTION_REMOVED))
            {
                listener.actionRemoved(event);
            }
            else if (eventType.equals(
                NotificationActionTypeEvent.ACTION_CHANGED))
            {
                listener.actionChanged(event);
            }
        }
    }
}
