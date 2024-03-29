/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.gui.customcontrols;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;

import net.java.sip.communicator.impl.gui.lookandfeel.*;

/**
 * <tt>SIPCommSmartComboBox</tt> is an editable combo box which selects an item
 * according to user input.
 *  
 * @author Yana Stamcheva
 */
public class SIPCommSmartComboBox extends JComboBox
{
    private ArrayList historyList = new ArrayList();
    
    /**
     * Creates an instance of <tt>SIPCommSmartComboBox</tt>.
     */
    public SIPCommSmartComboBox()
    {
        setModel(new FilterableComboBoxModel(historyList));
        setEditor(new CallComboEditor());
        setEditable(true);
        setFocusable(true);
    }
        
    /**
     * The data model used for this combo box. Filters the contents of the
     * combo box popup according to the user input. 
     */
    public class FilterableComboBoxModel
        extends AbstractListModel
        implements MutableComboBoxModel
    {

        private List items;
        private Filter filter;
        private List filteredItems;
        private Object selectedItem;
        
        public FilterableComboBoxModel(List items)
        {
            this.items = new ArrayList(items);
            filteredItems = new ArrayList(items.size());
            updateFilteredItems();
        }

        public boolean contains(Object obj)
        {
            return items.contains(obj);
        }

        public void addElement( Object obj )
        {
            items.add(obj);
            updateFilteredItems();
        }

        public void removeElement( Object obj )
        {
            items.remove(obj);
            updateFilteredItems();
        }

        public void removeElementAt(int index)
        {   
            items.remove(index);
            updateFilteredItems();
        }

        public void insertElementAt( Object obj, int index ) {
            items.add(index, obj);
            updateFilteredItems();
        }

        public void setFilter(Filter filter)
        {   
            this.filter = filter;
            updateFilteredItems();
        }

        protected void updateFilteredItems()
        {
            fireIntervalRemoved(this, 0, filteredItems.size());
            filteredItems.clear();

            if (filter == null)
                filteredItems.addAll(items);
            else {
                for (Iterator iterator = items.iterator(); iterator.hasNext();) {
                    
                    Object item = iterator.next();

                    if (filter.accept(item))
                        filteredItems.add(item);
                }
            }
            fireIntervalAdded(this, 0, filteredItems.size());
        }

        public int getSize()
        {
            return filteredItems.size();
        }

        public Object getElementAt(int index)
        {
            return filteredItems.get(index);
        }

        public Object getSelectedItem()
        {
            return selectedItem;
        }
        
        public void setSelectedItem(Object val)
        {   
            if ((selectedItem == null) && (val == null))
                return;
        
            if ((selectedItem != null) && selectedItem.equals(val))
                return;
        
            if ((val != null) && val.equals(selectedItem))
                return;
        
            selectedItem = val;
            fireContentsChanged(this, -1, -1);
        }
    }
    
    public static interface Filter
    {
        public boolean accept(Object obj);
    }
    
    class StartsWithFilter implements Filter
    {
        private String prefix;
        
        public StartsWithFilter(String prefix)
        { 
            this.prefix = prefix.toLowerCase();
        }
        
        public boolean accept(Object o)
        {
            if(o != null) {
                String objectString = o.toString().toLowerCase(); 
                return (objectString.indexOf(prefix) >= 0) ? true : false;
            }
            
            return false;
        }
    }
    
    public class CallComboEditor
    implements  ComboBoxEditor,
                DocumentListener
    {
        private JTextField text;
        private volatile boolean filtering = false;
        private volatile boolean setting = false;
        
        public CallComboEditor()
        {
            text = new JTextField(15);
            text.getDocument().addDocumentListener(this);

            // Enable delete button from the UI.
            if (text.getUI() instanceof SIPCommTextFieldUI)
            {
                ((SIPCommTextFieldUI) text.getUI())
                    .setDeleteButtonEnabled(true);
            }
        }

        public Component getEditorComponent() { return text; }

        public void setItem(Object item)
        {
            if(filtering)
                return;

            setting = true;
            String newText = (item == null) ? "" : item.toString();

            text.setText(newText);
            setting = false;
        }
        
        public Object getItem()
        {
            return text.getText();
        }
        
        public void selectAll() { text.selectAll(); }
        
        public void addActionListener(ActionListener l)
        {
            text.addActionListener(l);
        }
        
        public void removeActionListener(ActionListener l)
        {
            text.removeActionListener(l);
        }
        
        public void insertUpdate(DocumentEvent e) { handleChange(); }
        public void removeUpdate(DocumentEvent e) { handleChange(); }
        public void changedUpdate(DocumentEvent e) { }
        
        protected void handleChange()
        {
            if (setting)
                return;
        
            filtering = true;
            
            Filter filter = null;
            if (text.getText().length() > 0) {
                filter = new StartsWithFilter(text.getText());
            }
            
            ((FilterableComboBoxModel) getModel()).setFilter(filter);
            
            setPopupVisible(false);
            
            if(getModel().getSize() > 0)
                setPopupVisible(true);
            
            filtering = false;
        }
    }
}
