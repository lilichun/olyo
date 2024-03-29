/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 * 
 * Distributable under LGPL license. See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.gui.lookandfeel;

import java.util.*;
import java.util.List;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;
import javax.swing.text.*;

import net.java.sip.communicator.impl.gui.utils.*;

/**
 * SipCommunicator default theme.
 * 
 * @author Yana Stamcheva
 */

public class SIPCommDefaultTheme
    extends DefaultMetalTheme
{
    /**
     * Used for tooltip borders, progress bar selection background, scroll bar
     * thumb shadow, tabbed pane focus, toolbar docking foreground
     */
    private static final ColorUIResource PRIMARY_CONTROL_DARK_SHADOW =
        new ColorUIResource(ColorProperties.getColor("primaryControlDarkShadow"));

    /**
     * Used for desktop color, menu selected background, focus color, slider
     * foreground, progress bar foreground, combo box selection background,
     * scroll bar thumb
     */
    private static final ColorUIResource PRIMARY_CONTROL_SHADOW =
        new ColorUIResource(ColorProperties.getColor("primaryControlShadow"));

    /**
     * Used for progress bar border, tooltip border inactive, tooltip foreground
     * inactive, scroll bar dark shadow.
     */
    private static final ColorUIResource CONTROL_DARK_SHADOW =
        new ColorUIResource(ColorProperties.getColor("controlDarkShadow"));

    private static final ColorUIResource CONTROL_SHADOW =
        new ColorUIResource(ColorProperties.getColor("controlShadow"));

    /**
     * Used for window title inactive background, menu background, tooltip
     * inactive background, combo box background, desktop icon background,
     * scroll bar background, tabbed pane tab area background.
     */
    private static final ColorUIResource CONTROL_COLOR =
        new ColorUIResource(ColorProperties.getColor("controlColor"));

    /**
     * Used for text hightlight color, window title background, scroll bar thumb
     * hightlight, split pane devider focus color, Tree.line, Tree.hash,
     * ToolBar.floatingForeground
     */
    private static final ColorUIResource PRIMARY_CONTROL_COLOR
        = new ColorUIResource(ColorProperties.getColor("primaryControlColor"));

    // Used to paint a gradient for a check box or a radio button.
    private static final ColorUIResource BUTTON_GRADIENT_DARK_COLOR
        = new ColorUIResource(ColorProperties.getColor("buttonGradientDark"));

    private static final ColorUIResource BUTTON_GRADIENT_LIGHT_COLOR
        = new ColorUIResource(ColorProperties.getColor("buttonGradientLight"));

    private static final ColorUIResource SLIDER_GRADIENT_DARK_COLOR
        = new ColorUIResource(ColorProperties.getColor("sliderGradientDark"));

    private static final ColorUIResource SLIDER_GRADIENT_LIGHT_COLOR
        = new ColorUIResource(ColorProperties.getColor("sliderGradientLight"));

    private static final ColorUIResource SELECTION_FOREGROUND
        = new ColorUIResource(ColorProperties.getColor("selectionForeground"));

    private static final ColorUIResource SELECTION_BACKGROUND
        = new ColorUIResource(ColorProperties.getColor("selectionBackground"));

    private static final ColorUIResource SPLIT_PANE_DEVIDER_FOCUS_COLOR
        = new ColorUIResource(ColorProperties
            .getColor("splitPaneDeviderFocused"));

    private static final ColorUIResource TABBED_PANE_HIGHLIGHT_COLOR
        = new ColorUIResource(ColorProperties
            .getColor("tabbedPaneBorderHighlight"));

    private static final ColorUIResource TABLE_GRID_COLOR
        = new ColorUIResource(ColorProperties.getColor("tableGrid"));

    private static final ColorUIResource SCROLL_BAR_TRACK_HIGHLIGHT
        = new ColorUIResource(ColorProperties
            .getColor("scrollBarTrackHighlight"));

    private static final ColorUIResource SCROLL_BAR_DARK_SHADOW
        = new ColorUIResource(ColorProperties.getColor("scrollBarDarkShadow"));

    private static final ColorUIResource DESKTOP_BACKGROUND_COLOR
        = new ColorUIResource(ColorProperties.getColor("desktopBackgroundColor"));

    private static final ColorUIResource CONTROL_TEXT_COLOR
        = new ColorUIResource(ColorProperties.getColor("textColor"));

    private static final ColorUIResource INACTIVE_CONTROL_TEXT_COLOR
        = new ColorUIResource(ColorProperties.getColor("inactiveTextColor"));

    private static final ColorUIResource MENU_DISABLED_FOREGROUND
        = new ColorUIResource(ColorProperties
            .getColor("menuDisabledForeground"));

    private static final ColorUIResource TAB_TITLE_HIGHLIGHT
        = new ColorUIResource(ColorProperties
            .getColor("tabTitleHighlight"));

    private static final ColorUIResource TAB_TITLE
        = new ColorUIResource(ColorProperties
            .getColor("tabTitle"));

    private static final FontUIResource BASIC_FONT
        = new FontUIResource(Constants.FONT);

    Object fieldInputMap = new UIDefaults.LazyInputMap(
        new Object[]
           {
            "meta C", DefaultEditorKit.copyAction,
            "ctrl C", DefaultEditorKit.copyAction,
            "meta V", DefaultEditorKit.pasteAction,
            "ctrl V", DefaultEditorKit.pasteAction,
            "meta X", DefaultEditorKit.cutAction,
            "ctrl X", DefaultEditorKit.cutAction,
            "COPY", DefaultEditorKit.copyAction,
            "PASTE", DefaultEditorKit.pasteAction,
            "CUT", DefaultEditorKit.cutAction,
            "shift LEFT", DefaultEditorKit.selectionBackwardAction,
            "shift KP_LEFT", DefaultEditorKit.selectionBackwardAction,
            "shift RIGHT", DefaultEditorKit.selectionForwardAction,
            "shift KP_RIGHT", DefaultEditorKit.selectionForwardAction,
            "ctrl LEFT", DefaultEditorKit.previousWordAction,
            "ctrl KP_LEFT", DefaultEditorKit.previousWordAction,
            "ctrl RIGHT", DefaultEditorKit.nextWordAction,
            "ctrl KP_RIGHT", DefaultEditorKit.nextWordAction,
            "ctrl shift LEFT", DefaultEditorKit.selectionPreviousWordAction,
            "ctrl shift KP_LEFT", DefaultEditorKit.selectionPreviousWordAction,
            "ctrl shift RIGHT", DefaultEditorKit.selectionNextWordAction,
            "ctrl shift KP_RIGHT", DefaultEditorKit.selectionNextWordAction,
            "ctrl A", DefaultEditorKit.selectAllAction,
            "meta LEFT", DefaultEditorKit.previousWordAction,
            "meta KP_LEFT", DefaultEditorKit.previousWordAction,
            "meta RIGHT", DefaultEditorKit.nextWordAction,
            "meta KP_RIGHT", DefaultEditorKit.nextWordAction,
            "meta shift LEFT", DefaultEditorKit.selectionPreviousWordAction,
            "meta shift KP_LEFT", DefaultEditorKit.selectionPreviousWordAction,
            "meta shift RIGHT", DefaultEditorKit.selectionNextWordAction,
            "meta shift KP_RIGHT", DefaultEditorKit.selectionNextWordAction,
            "meta A", DefaultEditorKit.selectAllAction,
            "HOME", DefaultEditorKit.beginLineAction,
            "END", DefaultEditorKit.endLineAction,
            "shift HOME", DefaultEditorKit.selectionBeginLineAction,
            "shift END", DefaultEditorKit.selectionEndLineAction,
            "BACK_SPACE", DefaultEditorKit.deletePrevCharAction,
            "ctrl H", DefaultEditorKit.deletePrevCharAction,
            "meta H", DefaultEditorKit.deletePrevCharAction,
            "DELETE", DefaultEditorKit.deleteNextCharAction,
            "RIGHT", DefaultEditorKit.forwardAction,
            "LEFT", DefaultEditorKit.backwardAction,
            "KP_RIGHT", DefaultEditorKit.forwardAction,
            "KP_LEFT", DefaultEditorKit.backwardAction,
            "ENTER", JTextField.notifyAction,
            "ctrl BACK_SLASH", "unselect"/*DefaultEditorKit.unselectAction*/,
            "control shift O", "toggle-componentOrientation"/*DefaultEditorKit.toggleComponentOrientation*/
           });

    Object passwordInputMap = new UIDefaults.LazyInputMap(
        new Object[]
           {
            "meta C", DefaultEditorKit.copyAction,
            "ctrl C", DefaultEditorKit.copyAction,
            "meta V", DefaultEditorKit.pasteAction,
            "ctrl V", DefaultEditorKit.pasteAction,
            "meta X", DefaultEditorKit.cutAction,
            "ctrl X", DefaultEditorKit.cutAction,
            "COPY", DefaultEditorKit.copyAction,
            "PASTE", DefaultEditorKit.pasteAction,
            "CUT", DefaultEditorKit.cutAction,
            "shift LEFT", DefaultEditorKit.selectionBackwardAction,
            "shift KP_LEFT", DefaultEditorKit.selectionBackwardAction,
            "shift RIGHT", DefaultEditorKit.selectionForwardAction,
            "shift KP_RIGHT", DefaultEditorKit.selectionForwardAction,
            "ctrl LEFT", DefaultEditorKit.beginLineAction,
            "ctrl KP_LEFT", DefaultEditorKit.beginLineAction,
            "ctrl RIGHT", DefaultEditorKit.endLineAction,
            "ctrl KP_RIGHT", DefaultEditorKit.endLineAction,
            "ctrl shift LEFT", DefaultEditorKit.selectionBeginLineAction,
            "ctrl shift KP_LEFT", DefaultEditorKit.selectionBeginLineAction,
            "ctrl shift RIGHT", DefaultEditorKit.selectionEndLineAction,
            "ctrl shift KP_RIGHT", DefaultEditorKit.selectionEndLineAction,
            "ctrl A", DefaultEditorKit.selectAllAction,
            "meta LEFT", DefaultEditorKit.beginLineAction,
            "meta KP_LEFT", DefaultEditorKit.beginLineAction,
            "meta RIGHT", DefaultEditorKit.endLineAction,
            "meta KP_RIGHT", DefaultEditorKit.endLineAction,
            "meta shift LEFT", DefaultEditorKit.selectionBeginLineAction,
            "meta shift KP_LEFT", DefaultEditorKit.selectionBeginLineAction,
            "meta shift RIGHT", DefaultEditorKit.selectionEndLineAction,
            "meta shift KP_RIGHT", DefaultEditorKit.selectionEndLineAction,
            "meta A", DefaultEditorKit.selectAllAction,
            "HOME", DefaultEditorKit.beginLineAction,
            "END", DefaultEditorKit.endLineAction,
            "shift HOME", DefaultEditorKit.selectionBeginLineAction,
            "shift END", DefaultEditorKit.selectionEndLineAction,
            "BACK_SPACE", DefaultEditorKit.deletePrevCharAction,
            "ctrl H", DefaultEditorKit.deletePrevCharAction,
            "DELETE", DefaultEditorKit.deleteNextCharAction,
            "RIGHT", DefaultEditorKit.forwardAction,
            "LEFT", DefaultEditorKit.backwardAction,
            "KP_RIGHT", DefaultEditorKit.forwardAction,
            "KP_LEFT", DefaultEditorKit.backwardAction,
            "ENTER", JTextField.notifyAction,
            "ctrl BACK_SLASH", "unselect"/*DefaultEditorKit.unselectAction*/,
            "control shift O", "toggle-componentOrientation"/*DefaultEditorKit.toggleComponentOrientation*/
    });

    Object multilineInputMap = new UIDefaults.LazyInputMap(
        new Object[]
           {
            "meta C", DefaultEditorKit.copyAction,
            "ctrl C", DefaultEditorKit.copyAction,
            "meta V", DefaultEditorKit.pasteAction,
            "ctrl V", DefaultEditorKit.pasteAction,
            "meta X", DefaultEditorKit.cutAction,
            "ctrl X", DefaultEditorKit.cutAction,
            "COPY", DefaultEditorKit.copyAction,
            "PASTE", DefaultEditorKit.pasteAction,
            "CUT", DefaultEditorKit.cutAction,
            "shift LEFT", DefaultEditorKit.selectionBackwardAction,
            "shift KP_LEFT", DefaultEditorKit.selectionBackwardAction,
            "shift RIGHT", DefaultEditorKit.selectionForwardAction,
            "shift KP_RIGHT", DefaultEditorKit.selectionForwardAction,
            "ctrl LEFT", DefaultEditorKit.previousWordAction,
            "ctrl KP_LEFT", DefaultEditorKit.previousWordAction,
            "ctrl RIGHT", DefaultEditorKit.nextWordAction,
            "ctrl KP_RIGHT", DefaultEditorKit.nextWordAction,
            "ctrl shift LEFT", DefaultEditorKit.selectionPreviousWordAction,
            "ctrl shift KP_LEFT", DefaultEditorKit.selectionPreviousWordAction,
            "ctrl shift RIGHT", DefaultEditorKit.selectionNextWordAction,
            "ctrl shift KP_RIGHT", DefaultEditorKit.selectionNextWordAction,
            "ctrl A", DefaultEditorKit.selectAllAction,
            "meta LEFT", DefaultEditorKit.previousWordAction,
            "meta KP_LEFT", DefaultEditorKit.previousWordAction,
            "meta RIGHT", DefaultEditorKit.nextWordAction,
            "meta KP_RIGHT", DefaultEditorKit.nextWordAction,
            "meta shift LEFT", DefaultEditorKit.selectionPreviousWordAction,
            "meta shift KP_LEFT", DefaultEditorKit.selectionPreviousWordAction,
            "meta shift RIGHT", DefaultEditorKit.selectionNextWordAction,
            "meta shift KP_RIGHT", DefaultEditorKit.selectionNextWordAction,
            "meta A", DefaultEditorKit.selectAllAction,
            "HOME", DefaultEditorKit.beginLineAction,
            "END", DefaultEditorKit.endLineAction,
            "shift HOME", DefaultEditorKit.selectionBeginLineAction,
            "shift END", DefaultEditorKit.selectionEndLineAction,

            "UP", DefaultEditorKit.upAction,
            "KP_UP", DefaultEditorKit.upAction,
            "DOWN", DefaultEditorKit.downAction,
            "KP_DOWN", DefaultEditorKit.downAction,
            "PAGE_UP", DefaultEditorKit.pageUpAction,
            "PAGE_DOWN", DefaultEditorKit.pageDownAction,
            "shift PAGE_UP", "selection-page-up",
            "shift PAGE_DOWN", "selection-page-down",
            "ctrl shift PAGE_UP", "selection-page-left",
            "ctrl shift PAGE_DOWN", "selection-page-right",
            "shift UP", DefaultEditorKit.selectionUpAction,
            "shift KP_UP", DefaultEditorKit.selectionUpAction,
            "shift DOWN", DefaultEditorKit.selectionDownAction,
            "shift KP_DOWN", DefaultEditorKit.selectionDownAction,
            "ENTER", DefaultEditorKit.insertBreakAction,
            "BACK_SPACE", DefaultEditorKit.deletePrevCharAction,
            "ctrl H", DefaultEditorKit.deletePrevCharAction,
            "DELETE", DefaultEditorKit.deleteNextCharAction,
            "RIGHT", DefaultEditorKit.forwardAction,
            "LEFT", DefaultEditorKit.backwardAction, 
            "KP_RIGHT", DefaultEditorKit.forwardAction,
            "KP_LEFT", DefaultEditorKit.backwardAction,
            "TAB", DefaultEditorKit.insertTabAction,
            "ctrl BACK_SLASH", "unselect"/*DefaultEditorKit.unselectAction*/,
            "ctrl HOME", DefaultEditorKit.beginAction,
            "ctrl END", DefaultEditorKit.endAction,
            "ctrl shift HOME", DefaultEditorKit.selectionBeginAction,
            "ctrl shift END", DefaultEditorKit.selectionEndAction,
            "ctrl T", "next-link-action",
            "ctrl shift T", "previous-link-action",
            "ctrl SPACE", "activate-link-action",
            "meta BACK_SLASH", "unselect"/*DefaultEditorKit.unselectAction*/,
            "meta HOME", DefaultEditorKit.beginAction,
            "meta END", DefaultEditorKit.endAction,
            "meta shift HOME", DefaultEditorKit.selectionBeginAction,
            "meta shift END", DefaultEditorKit.selectionEndAction,
            "meta T", "next-link-action",
            "meta shift T", "previous-link-action",
            "meta SPACE", "activate-link-action",
            "control shift O", "toggle-componentOrientation"/*DefaultEditorKit.toggleComponentOrientation*/
});

    public SIPCommDefaultTheme()
    {
    }

    public void addCustomEntriesToTable(UIDefaults table)
    {

        List buttonGradient
            = Arrays.asList(new Object[]
               { new Float(.3f), new Float(0f), BUTTON_GRADIENT_DARK_COLOR,
                getWhite(), BUTTON_GRADIENT_LIGHT_COLOR });

        List sliderGradient
            = Arrays.asList(new Object[]
               { new Float(.3f), new Float(.2f), SLIDER_GRADIENT_DARK_COLOR,
                getWhite(), SLIDER_GRADIENT_LIGHT_COLOR });

        Object textFieldBorder = SIPCommBorders.getTextFieldBorder();

        Object[] defaults =
            new Object[]
            {
                "Button.rollover", Boolean.TRUE,

                "CheckBox.rollover", Boolean.TRUE,
                "CheckBox.gradient", buttonGradient,

                "CheckBoxMenuItem.gradient", buttonGradient,

                "Menu.opaque", Boolean.FALSE,

                "MenuBar.border", null,

                "Menu.borderPainted", Boolean.FALSE,
                "Menu.border", textFieldBorder,
                "Menu.selectionBackground", SELECTION_BACKGROUND,
                "Menu.selectionForeground", SELECTION_FOREGROUND,
                "Menu.margin", new InsetsUIResource(0, 0, 0, 0),

                "MenuItem.borderPainted", Boolean.FALSE,
                "MenuItem.border", textFieldBorder,
                "MenuItem.selectionBackground", SELECTION_BACKGROUND,
                "MenuItem.selectionForeground", SELECTION_FOREGROUND,

                "CheckBoxMenuItem.borderPainted", Boolean.FALSE,
                "CheckBoxMenuItem.border", textFieldBorder,
                "CheckBoxMenuItem.selectionBackground", SELECTION_BACKGROUND,
                "CheckBoxMenuItem.selectionForeground", SELECTION_FOREGROUND,

                "InternalFrame.activeTitleGradient", buttonGradient,

                "OptionPane.warningIcon",
                new ImageIcon(ImageLoader.getImage(ImageLoader.WARNING_ICON)),

                "OptionPane.errorIcon",
                new ImageIcon(ImageLoader.getImage(ImageLoader.ERROR_ICON)),

                "OptionPane.infoIcon",
                new ImageIcon(ImageLoader.getImage(ImageLoader.INFO_ICON)),

                "RadioButton.gradient", buttonGradient,
                "RadioButton.rollover", Boolean.TRUE,

                "RadioButtonMenuItem.gradient", buttonGradient,

                "Spinner.arrowButtonBorder", SIPCommBorders.getTextFieldBorder(),

                "Slider.altTrackColor", SLIDER_GRADIENT_LIGHT_COLOR,
                "Slider.gradient", sliderGradient,
                "Slider.focusGradient", sliderGradient,

                "SplitPane.oneTouchButtonsOpaque", Boolean.FALSE,
                "SplitPane.dividerFocusColor", SPLIT_PANE_DEVIDER_FOCUS_COLOR,
                "SplitPane.dividerSize", new Integer(5),

                "ScrollBar.width", new Integer(12),
                "ScrollBar.horizontalThumbIcon",
                ImageLoader.getImage(ImageLoader.SCROLLBAR_THUMB_HORIZONTAL),
                "ScrollBar.verticalThumbIcon",
                ImageLoader.getImage(ImageLoader.SCROLLBAR_THUMB_VERTICAL),
                "ScrollBar.horizontalThumbHandleIcon",
                ImageLoader
                    .getImage(ImageLoader.SCROLLBAR_THUMB_HANDLE_HORIZONTAL),
                "ScrollBar.verticalThumbHandleIcon",
                ImageLoader
                    .getImage(ImageLoader.SCROLLBAR_THUMB_HANDLE_VERTICAL),
                "ScrollBar.trackHighlight", SCROLL_BAR_TRACK_HIGHLIGHT,
                "ScrollBar.highlight", SELECTION_BACKGROUND,
                "ScrollBar.darkShadow", SCROLL_BAR_DARK_SHADOW,

                "TabbedPane.borderHightlightColor", TABBED_PANE_HIGHLIGHT_COLOR,
                "TabbedPane.contentBorderInsets", new Insets(2, 2, 3, 3),
                "TabbedPane.selected", SELECTION_BACKGROUND,
                "TabbedPane.tabAreaInsets", new Insets(2, 2, 0, 6),
                "TabbedPane.unselectedBackground", SELECTION_BACKGROUND,
                "TabbedPane.shadow", CONTROL_SHADOW,
                "TabbedPane.darkShadow", CONTROL_DARK_SHADOW,
                "TabbedPane.tabTitleHighlight", TAB_TITLE_HIGHLIGHT,
                "TabbedPane.foreground", TAB_TITLE,

                "TextField.border", textFieldBorder,
                "TextField.margin", new InsetsUIResource(3, 3, 3, 3),

                "TextField.focusInputMap", fieldInputMap,
                "TextArea.focusInputMap", multilineInputMap,
                "TextPane.focusInputMap", multilineInputMap,
                "EditorPane.focusInputMap", multilineInputMap,

                "PasswordField.border", textFieldBorder,
                "PasswordField.margin", new InsetsUIResource(3, 3, 3, 3),
                "PasswordField.focusInputMap", passwordInputMap,

                "FormattedTextField.border", textFieldBorder,
                "FormattedTextField.margin", new InsetsUIResource(3, 3, 3, 3),

                "Table.gridColor", TABLE_GRID_COLOR,
                "Table.background", getDesktopColor(),

                "ToggleButton.gradient", buttonGradient,

                "ToolBar.isRollover", Boolean.TRUE,
                "ToolBar.separatorColor", PRIMARY_CONTROL_COLOR,
                "ToolBar.separatorSize", new DimensionUIResource(8, 22),

                "ToolTip.background", SELECTION_BACKGROUND,
                "ToolTip.backgroundInactive", SELECTION_BACKGROUND,
                "ToolTip.hideAccelerator", Boolean.FALSE,

                "TitledBorder.border", SIPCommBorders.getBoldRoundBorder()
            };

        table.putDefaults(defaults);
    }

    /**
     * Overriden to enable picking up the system fonts, if applicable.
     */
    boolean isSystemTheme()
    {
        return true;
    }

    public String getName()
    {
        return "SipCommunicator";
    }

    protected ColorUIResource getPrimary1()
    {
        return PRIMARY_CONTROL_DARK_SHADOW;
    }

    protected ColorUIResource getPrimary2()
    {
        return PRIMARY_CONTROL_SHADOW;
    }

    protected ColorUIResource getPrimary3()
    {
        return PRIMARY_CONTROL_COLOR;
    }

    protected ColorUIResource getSecondary1()
    {
        return CONTROL_DARK_SHADOW;
    }

    protected ColorUIResource getSecondary2()
    {
        return CONTROL_SHADOW;
    }

    protected ColorUIResource getSecondary3()
    {
        return CONTROL_COLOR;
    }

    protected ColorUIResource getBlack()
    {
        return CONTROL_TEXT_COLOR;
    }

    public ColorUIResource getDesktopColor()
    {
        return DESKTOP_BACKGROUND_COLOR;
    }

    public ColorUIResource getWindowBackground()
    {
        return getWhite();
    }

    public ColorUIResource getControl()
    {
        return DESKTOP_BACKGROUND_COLOR;
    }

    public ColorUIResource getMenuBackground()
    {
        return DESKTOP_BACKGROUND_COLOR;
    }

    public ColorUIResource getInactiveControlTextColor()
    {
        return INACTIVE_CONTROL_TEXT_COLOR;
    }

    public ColorUIResource getMenuDisabledForeground()
    {
        return MENU_DISABLED_FOREGROUND;
    }

    public FontUIResource getControlTextFont()
    {
        return BASIC_FONT;
    }

    public FontUIResource getSystemTextFont()
    {
        return BASIC_FONT;
    }

    public FontUIResource getUserTextFont()
    {
        return BASIC_FONT;
    }

    public FontUIResource getMenuTextFont()
    {
        return BASIC_FONT;
    }

    public FontUIResource getWindowTitleFont()
    {
        return BASIC_FONT;
    }

    public FontUIResource getSubTextFont()
    {
        return BASIC_FONT;
    }
}
