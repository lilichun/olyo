/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package net.java.sip.communicator.plugin.extendedcallhistorysearch;

import java.awt.*;
import java.util.*;

import javax.swing.*;


/**
 * The <tt>StringUtils</tt> class is used through this ui implementation for
 * some special operations with strings.
 * 
 * @author Yana Stamcheva
 */
public class GuiUtils {

    private static Calendar c1 = Calendar.getInstance();
    
    private static Calendar c2 = Calendar.getInstance();
    
    /**
     * Replaces some chars that are special in a regular expression.
     * @param text The initial text.
     * @return the formatted text
     */
    public static String replaceSpecialRegExpChars(String text) {
        return text.replaceAll("([.()^&$*|])", "\\\\$1");
    }

    /**
     * Returns the width in pixels of a text.
     * @param c the component where the text is contained
     * @param text the text to measure
     * @return the width in pixels of a text.
     */
    public static int getStringWidth(Component c, String text) {
        return SwingUtilities.computeStringWidth(c
                .getFontMetrics(Constants.FONT), text);
    }
    
    /**
     * Compares the two dates. The comparison is based only on the day, month
     * and year values. Returns 0 if the two dates are equals, a value < 0 if
     * the first date is before the second one and > 0 if the first date is after
     * the second one.
     * @param date1 the first date to compare
     * @param date2 the second date to compare with 
     * @return Returns 0 if the two dates are equals, a value < 0 if
     * the first date is before the second one and > 0 if the first date is after
     * the second one
     */
    public static int compareDates(Date date1, Date date2)
    {
        c1.setTime(date1);
        c2.setTime(date2);
        
        int day1 = c1.get(Calendar.DAY_OF_MONTH);
        int month1 = c1.get(Calendar.MONTH);
        int year1 = c1.get(Calendar.YEAR);
        
        int day2 = c2.get(Calendar.DAY_OF_MONTH);
        int month2 = c2.get(Calendar.MONTH);
        int year2 = c2.get(Calendar.YEAR);
        
        if((day1 == day2)
                && (month1 == month2)
                && (year1 == year2)) {
            
            return 0;
        }
        else if((day1 < day2)
                && (month1 <= month2)
                && (year1 <= year2)) {
            
            return -1;
        }
        else {
            return 1;
        }
    }
    
    /**
     * Formats the given date. The result format is the following:
     * [Month] [Day], [Year]. For example: Dec 24, 2000.
     * @param date the date to format
     * @return the formatted date string
     */
    public static String formatDate(Date date)
    {
        c1.setTime(date);
        
        return GuiUtils.processMonth(c1.get(Calendar.MONTH) + 1) + " " 
            + GuiUtils.formatTime(c1.get(Calendar.DAY_OF_MONTH)) + ", "                
            + GuiUtils.formatTime(c1.get(Calendar.YEAR));
    }
    
    /**
     * Formats the time for the given date. The result format is the following:
     * [Hour]:[Minute]:[Second]. For example: 12:25:30. 
     * @param date the date to format
     * @return the formatted hour string
     */
    public static String formatTime(Date date)
    {
        c1.setTime(date);
        
        return  GuiUtils.formatTime(c1.get(Calendar.HOUR_OF_DAY)) + ":"
            + GuiUtils.formatTime(c1.get(Calendar.MINUTE)) + ":"
            + GuiUtils.formatTime(c1.get(Calendar.SECOND)) ;
    }
    
    /**
     * Substracts the two dates.
     * @param date1 the first date argument
     * @param date2 the second date argument
     * @return the date resulted from the substracting
     */
    public static Date substractDates(Date date1, Date date2)
    {
        long d1 = date1.getTime();
        long d2 = date2.getTime();
        long difMil = d1-d2;
        long milPerDay = 1000*60*60*24;
        long milPerHour = 1000*60*60;
        long milPerMin = 1000*60;
        long milPerSec = 1000;

        long days = difMil / milPerDay;
        int hour = (int)(( difMil - days*milPerDay ) / milPerHour);
        int min
            = (int)(( difMil - days*milPerDay - hour*milPerHour ) / milPerMin);
        int sec
            = (int)(( difMil - days*milPerDay - hour*milPerHour - min*milPerMin )
                    / milPerSec);
        
        c1.clear();
        c1.set(Calendar.HOUR, hour);
        c1.set(Calendar.MINUTE, min);
        c1.set(Calendar.SECOND, sec);
        
        return c1.getTime();
    }
    
    /**
     * Replaces the month with its abbreviation.
     * @param month Value from 1 to 12, which indicates the month.
     * @return the corresponding month abbreviation
     */
    private static String processMonth(int month)
    {
        String monthString = "";
        if(month == 1)
            monthString = Resources.getString("january");
        else if(month == 2)
            monthString = Resources.getString("february");
        else if(month == 3)
            monthString = Resources.getString("march");
        else if(month == 4)
            monthString = Resources.getString("april");
        else if(month == 5)
            monthString = Resources.getString("may");
        else if(month == 6)
            monthString = Resources.getString("june");
        else if(month == 7)
            monthString = Resources.getString("july");
        else if(month == 8)
            monthString = Resources.getString("august");
        else if(month == 9)
            monthString = Resources.getString("september");
        else if(month == 10)
            monthString = Resources.getString("october");
        else if(month == 11)
            monthString = Resources.getString("november");
        else if(month == 12)
            monthString = Resources.getString("december");
        
        return monthString;
    }
    
    /**
     * Adds a 0 in the beginning of one digit numbers.
     *
     * @param time The time parameter could be hours, minutes or seconds.
     * @return The formatted minutes string.
     */
    private static String formatTime(int time)
    {
        String timeString = new Integer(time).toString();

        String resultString = "";
        if (timeString.length() < 2)
            resultString = resultString.concat("0").concat(timeString);
        else
            resultString = timeString;

        return resultString;
    }
}
