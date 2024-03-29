/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package net.java.sip.communicator.impl.gui.customcontrols;

import java.io.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import net.java.sip.communicator.impl.gui.i18n.*;
import net.java.sip.communicator.impl.gui.lookandfeel.*;
import net.java.sip.communicator.impl.gui.utils.*;
import net.java.sip.communicator.util.*;

/**
 * The <tt>MessageDialog</tt> is a <tt>JDialog</tt> that contains a question
 * message, two buttons to confirm or cancel the question and a check box that
 * allows user to choose to not be questioned any more over this subject.
 * <p>
 * The message and the name of the "OK" button could be configured.
 *
 * @author Yana Stamcheva
 */
public class ErrorDialog
    extends SIPCommDialog
    implements  ActionListener,
                HyperlinkListener
{
    private Logger logger = Logger.getLogger(ErrorDialog.class);

    private JButton okButton = new JButton(
        Messages.getI18NString("ok").getText());

    private JLabel iconLabel = new JLabel(new ImageIcon(ImageLoader
            .getImage(ImageLoader.ERROR_ICON)));

    private StyledHTMLEditorPane htmlMsgEditorPane = new StyledHTMLEditorPane();

    private SIPCommMsgTextArea msgTextArea = new SIPCommMsgTextArea();

    private JTextArea stackTraceTextArea = new JTextArea();

    private JScrollPane stackTraceScrollPane = new JScrollPane();

    private JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

    private JPanel infoMessagePanel = new JPanel();

    private JPanel messagePanel = new JPanel(new BorderLayout());

    private JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

    public static final int WARNING = 1;

    public static final int ERROR = 0;

    /**
     * Creates an instance of <tt>MessageDialog</tt> by specifying the
     * owner window and the message to be displayed.
     * 
     * @param owner the dialog owner
     * @param title the title of the dialog
     * @param message the message to be displayed
     */
    public ErrorDialog( Frame owner,
                        String title,
                        String message)
    {
        super(owner, false);

        this.mainPanel.setBorder(
            BorderFactory.createEmptyBorder(20, 20, 10, 20));

        this.stackTraceScrollPane.setBorder(
            new SIPCommBorders.BoldRoundBorder());

        this.stackTraceScrollPane.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        this.setTitle(title);

        this.infoMessagePanel.setLayout(
            new BoxLayout(infoMessagePanel, BoxLayout.Y_AXIS));

        this.infoMessagePanel.add(msgTextArea);

        this.msgTextArea.setLineWrap(true);
        this.msgTextArea.setWrapStyleWord(true);
        this.msgTextArea.setText(message);

        this.init();
    }

    /**
     * Creates an instance of <tt>MessageDialog</tt> by specifying the
     * owner window and the message to be displayed.
     * @param owner the dialog owner
     * @param title the title of the dialog
     * @param message the message to be displayed
     * @param e the exception corresponding to the error
     */
    public ErrorDialog( Frame owner,
                        String title,
                        String message,
                        Exception e)
    {
        this(owner, title, message);

        this.setTitle(title);

        this.htmlMsgEditorPane.setEditable(false);
        this.htmlMsgEditorPane.setOpaque(false);

        this.htmlMsgEditorPane.addHyperlinkListener(this);

        String startDivTag = "<DIV id=\"message\">";
        String endDivTag = "</DIV>";

        String msgString = startDivTag
                            + " <A href=''>more info</A>"
                            + endDivTag;

        htmlMsgEditorPane.appendToEnd(msgString);

        this.infoMessagePanel.add(htmlMsgEditorPane);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.close();

        String stackTrace = sw.toString();

        try
        {
            sw.close();
        }
        catch (IOException ex)
        {
            //really shouldn't happen. but log anyway
            logger.error("Failed to close a StringWriter. ", ex);
        }

        this.stackTraceTextArea.setText(stackTrace);
    }

    /**
     * Creates an instance of <tt>MessageDialog</tt> by specifying the
     * owner window and the message to be displayed.
     *
     * @param owner the dialog owner
     * @param title the title of the error dialog
     * @param message the message to be displayed
     * @param type the dialog type
     */
    public ErrorDialog( Frame owner,
                        String title,
                        String message,
                        int type)
    {
        this(owner, title, message);

        if(type == WARNING)
            iconLabel.setIcon(new ImageIcon(ImageLoader
                .getImage(ImageLoader.WARNING_ICON)));
    }

    /**
     * Initializes this dialog.
     */
    private void init()
    {
        this.getRootPane().setDefaultButton(okButton);

        this.stackTraceScrollPane.getViewport().add(stackTraceTextArea);
        this.stackTraceScrollPane.setPreferredSize(
            new Dimension(this.getWidth(), 100));

        this.buttonsPanel.add(okButton);

        this.okButton.addActionListener(this);

        this.mainPanel.add(iconLabel, BorderLayout.WEST);

        this.messagePanel.add(infoMessagePanel, BorderLayout.NORTH);

        this.mainPanel.add(messagePanel, BorderLayout.CENTER);
        this.mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        this.getContentPane().add(mainPanel);
    }

    /**
     * Sets the message to be displayed.
     * @param message The message to be displayed.
     */
    public void setMessage(String message)
    {
        this.msgTextArea.setText(message);
    }

    /**
     * Shows the dialog.
     */
    public void showDialog()
    {
        this.pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        this.setLocation(screenSize.width/2 - this.getWidth()/2,
                screenSize.height/2 - this.getHeight()/2);

        this.setVisible(true);

        this.toFront();
    }

    /**
     * Handles the <tt>ActionEvent</tt>. Depending on the user choice sets
     * the return code to the appropriate value.
     *
     * @param e the <tt>ActionEvent</tt> instance that has just been fired.
     */
    public void actionPerformed(ActionEvent e)
    {
        JButton button = (JButton) e.getSource();

        if(button.equals(okButton))
            this.dispose();
    }

    protected void close(boolean isEscaped)
    {
        this.okButton.doClick();
    }

    public void hyperlinkUpdate(HyperlinkEvent e)
    {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
        {
            this.messagePanel.add(stackTraceScrollPane, BorderLayout.CENTER);
            this.messagePanel.revalidate();
            this.messagePanel.repaint();
            this.pack();
        }
    }
}
