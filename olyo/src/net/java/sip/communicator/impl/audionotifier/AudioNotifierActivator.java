/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.audionotifier;

import net.java.sip.communicator.service.audionotifier.*;
import net.java.sip.communicator.service.configuration.*;
import net.java.sip.communicator.util.*;

import org.osgi.framework.*;

/**
 * The AudioNotifier activator class.
 * 
 * @author Yana Stamcheva
 */
public class AudioNotifierActivator implements BundleActivator
{
    private AudioNotifierServiceImpl audioNotifier;
    
    private ConfigurationService configService;
    
    private static Logger logger = Logger.getLogger(
        AudioNotifierActivator.class.getName());
    
    /**
     * Called when this bundle is started.
     *
     * @param bundleContext The execution context of the bundle being started.
     */
    public void start(BundleContext bundleContext) throws Exception
    {
        try {
            //Create the audio notifier service
            audioNotifier = new AudioNotifierServiceImpl();

            ServiceReference configReference = bundleContext
                .getServiceReference(ConfigurationService.class.getName());
        
            configService = (ConfigurationService) bundleContext
                .getService(configReference);
    
            String isSoundEnabled = configService.getString(
                "net.java.sip.communicator.impl.sound.isSoundEnabled");
    
            if(isSoundEnabled != null && isSoundEnabled != "") {
                audioNotifier.setMute(
                    !new Boolean(isSoundEnabled).booleanValue());
            }

            logger.logEntry();
            
            logger.info("Audio Notifier Service...[  STARTED ]");

            bundleContext.registerService(AudioNotifierService.class.getName(),
                    audioNotifier, null);

            logger.info("Audio Notifier Service ...[REGISTERED]");
            
        } finally {
            logger.logExit();
        }
    }
    
    /**
     * Called when this bundle is stopped so the Framework can perform the
     * bundle-specific activities necessary to stop the bundle.
     *
     * @param bundleContext The execution context of the bundle being stopped.
     * @throws Exception If this method throws an exception, the bundle is
     *   still marked as stopped, and the Framework will remove the bundle's
     *   listeners, unregister all services registered by the bundle, and
     *   release all services used by the bundle.
     */
    public void stop(BundleContext bundleContext) throws Exception
    {
        //TODO: Stop all currently playing sounds here
        try {
            configService.setProperty(
                "net.java.sip.communicator.impl.sound.isSoundEnabled",
                new Boolean(!audioNotifier.isMute()));

        }
        catch (PropertyVetoException e1) {
            logger.error("The proposed property change "
                    + "represents an unacceptable value");
        }
        
        logger.info("AudioNotifier Service ...[STOPPED]");
    }
}
