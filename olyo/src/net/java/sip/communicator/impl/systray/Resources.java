/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package net.java.sip.communicator.impl.systray;

import java.awt.image.*;
import java.io.*;
import java.util.*;

import javax.imageio.*;
import javax.swing.*;

import net.java.sip.communicator.util.*;
/**
 * The Messages class manages the access to the internationalization
 * properties files.
 * 
 * @author Nicolas Chamouard
 */
public class Resources 
{
    
    private static Logger log = Logger.getLogger(Resources.class);
    
    private static final String BUNDLE_NAME 
        = "resources.languages.impl.systray.resources";

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
            .getBundle(BUNDLE_NAME);

    private static final String APPLICATION_RESUORCE_LOCATION
        = "resources.application";

    private static final ResourceBundle applicationBundle 
        = ResourceBundle.getBundle(APPLICATION_RESUORCE_LOCATION);

    /**
     * Returns an internationalized string corresponding to the given key.
     * 
     * @param key The key of the string.
     * @return An internationalized string corresponding to the given key.
     */
    public static String getString(String key)
    {
        try
        {
            String resourceString = RESOURCE_BUNDLE.getString(key);

            int mnemonicIndex = resourceString.indexOf('&');

            if(mnemonicIndex > -1)
            {
                String firstPart = resourceString.substring(0, mnemonicIndex);
                String secondPart = resourceString.substring(mnemonicIndex + 1);

                resourceString = firstPart.concat(secondPart);
            }

            return resourceString;
        }
        catch (MissingResourceException e)
        {
            return '!' + key + '!';
        }
    }

    /**
     * Returns an internationalized string corresponding to the given key.
     * 
     * @param key The key of the string.
     * @return An internationalized string corresponding to the given key.
     */
    public static char getMnemonic(String key)
    {
        try
        {
            String resourceString = RESOURCE_BUNDLE.getString(key);

            int mnemonicIndex = resourceString.indexOf('&');

            if(mnemonicIndex > -1)
                return resourceString.charAt(mnemonicIndex + 1);
        }
        catch (MissingResourceException e)
        {
            return 0;
        }

        return 0;
    }

    /**
     * Loads an image from a given image identifier.
     * 
     * @param imageID The identifier of the image.
     * @return The image for the given identifier.
     */
    public static ImageIcon getImage(String imageID)
    {
        BufferedImage image = null;

        String path = Resources.getString(imageID);
        try
        {
            image =
                ImageIO.read(Resources.class.getClassLoader()
                    .getResourceAsStream(path));

        }
        catch (IOException e)
        {
            log.error("Failed to load image:" + path, e);
        }

        return new ImageIcon(image);
    }

    /**
     * Returns the application property string corresponding to the given key.
     *
     * @param key The key of the string.
     * @return the application property string corresponding to the given key
     */
    public static String getApplicationString(String key)
    {
        try
        {
            return applicationBundle.getString(key);
        }
        catch (MissingResourceException e)
        {
            return '!' + key + '!';
        }
    }
}
