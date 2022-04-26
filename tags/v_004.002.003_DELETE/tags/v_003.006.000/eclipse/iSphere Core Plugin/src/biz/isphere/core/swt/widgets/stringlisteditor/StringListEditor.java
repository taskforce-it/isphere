/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.stringlisteditor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.swt.events.TableAutoSizeControlListener;
import biz.isphere.base.swt.widgets.UpperCaseOnlyVerifier;
import biz.isphere.core.Messages;
import biz.isphere.core.swt.widgets.WidgetFactory;

import com.ibm.etools.iseries.util.NlsUtil;

/**
 * Implements a string list editor with options for adding, removing and moving
 * items.
 * 
 * @see com.ibm.etools.iseries.rse.ui.widgets.IBMiLibraryListComposite
 */
public class StringListEditor extends Composite implements SelectionListener {

    private static final int COLUMN_ITEM = 0;

    private static final String COLUMN_PROPERTY_ITEM = "ITEM"; //$NON-NLS-1$
    private static final String[] COLUMN_PROPERTIES = { COLUMN_PROPERTY_ITEM }; //$NON-NLS-1$

    private boolean isEditable;
    private Shell shell;
    private Button parentDefaultButton;
    private List<Item> itemsList;

    private boolean enableLowerCase;

    private UpperCaseOnlyVerifier upperCaseOnlyVerifier;

    private Table itemsTable;
    private TableViewer itemsTableViewer;
    private Text textItem;
    private Button addButton;
    private Button removeButton;
    private Button removeAllButton;
    private Button moveUpButton;
    private Button moveDownButton;
    private IStringValidator validator;

    private CellEditor[] cellEditors;

    public StringListEditor(Composite parent, int style) {
        this(parent, true, style);
    }

    public StringListEditor(Composite parent, boolean isEditable, int style) {
        super(parent, style);

        this.isEditable = isEditable;
        this.shell = parent.getShell();
        this.itemsList = new ArrayList<Item>();

        this.upperCaseOnlyVerifier = new UpperCaseOnlyVerifier();

        int numColumns = 3;
        prepareComposite(numColumns);
        createContentArea(numColumns);
    }

    public void setValidator(IStringValidator validator) {
        this.validator = validator;
    }

    public void setTextLimit(int limit) {
        if (isEditable) {
            textItem.setTextLimit(limit);
            Text editorControl = (Text)cellEditors[COLUMN_ITEM].getControl();
            editorControl.setTextLimit(limit);
        }
    }

    public void setEnableLowerCase(boolean enable) {

        if (!isEditable) {
            return;
        }

        this.enableLowerCase = enable;

        Text editorControl = (Text)cellEditors[COLUMN_ITEM].getControl();
        if (this.enableLowerCase) {
            textItem.addVerifyListener(upperCaseOnlyVerifier);
            editorControl.addVerifyListener(upperCaseOnlyVerifier);
        } else {
            textItem.removeVerifyListener(upperCaseOnlyVerifier);
            editorControl.removeVerifyListener(upperCaseOnlyVerifier);
        }
    }

    public boolean contains(String item) {

        if (item == null) {
            return false;
        }

        for (int i = 0; i < itemsList.size(); i++) {
            if (item.trim().equals(itemsList.get(i).getValue().trim())) {
                return true;
            }
        }

        return false;
    }

    public void clearAll() {

        if (isEditable) {
            textItem.setText(""); //$NON-NLS-1$
        }

        itemsList.clear();
        itemsTableViewer.refresh();
    }

    public void setItems(String[] items) {
        this.itemsList.clear();
        for (String item : items) {
            this.itemsList.add(new Item(item));
        }

        itemsTableViewer.setInput(itemsList);

        setButtonEnablement();
    }

    public String[] getItems() {

        String[] items = new String[itemsList.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = itemsList.get(i).getValue().trim();
        }

        return items;
    }

    public int getItemCount() {
        return itemsTable.getItemCount();
    }

    @Override
    public boolean setFocus() {
        if (isEditable && textItem.isEnabled()) {
            return textItem.setFocus();
        } else {
            return super.setFocus();
        }
    }

    public boolean setFocus(int index) {
        if (index >= 0 && index < getItemCount()) {
            itemsTable.setTopIndex(index);
            return true;
        }

        return false;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if (!enabled) {
            setEnabled(textItem, false);
            setEnabled(itemsTable, false);
            setEnabled(addButton, false);
            setEnabled(removeButton, false);
            setEnabled(removeAllButton, false);
            setEnabled(moveUpButton, false);
            setEnabled(moveDownButton, false);
        } else {
            setEnabled(textItem, true);
            setEnabled(itemsTable, true);
            setButtonEnablement();
        }
    }

    private void setButtonEnablement() {

        // Add-button
        if (isEditable) {
            if (textItem.getText().trim().length() > 0) {
                setEnabled(addButton, true);
            } else {
                setEnabled(addButton, false);
            }
        }

        // Remove all-button
        if (itemsTable.getItemCount() <= 0 || !isEnabled()) {
            setEnabled(removeAllButton, false);
        } else {
            setEnabled(removeAllButton, true);
        }

        // Other buttons
        if (itemsTable.getSelectionCount() == 0) {
            setEnabled(removeButton, false);
            setEnabled(moveUpButton, false);
            setEnabled(moveDownButton, false);
        } else {
            int[] selectionIndices = itemsTable.getSelectionIndices();
            boolean upEnabled = true;
            boolean downEnabled = true;
            for (int loop = 0; loop < selectionIndices.length; loop++) {
                if (selectionIndices[loop] == 0) {
                    upEnabled = false;
                }
                if (selectionIndices[loop] == itemsTable.getItemCount() - 1) {
                    downEnabled = false;
                }
            }
            setEnabled(removeButton, true);
            setEnabled(moveUpButton, upEnabled);
            setEnabled(moveDownButton, downEnabled);
        }

    }

    private void setEnabled(Control control, boolean enabled) {

        if (control == null || control.isDisposed()) {
            return;
        }

        control.setEnabled(enabled);
    }

    protected Composite prepareComposite(int numColumns) {
        Composite composite = this;

        GridLayout layout = new GridLayout();
        layout.numColumns = numColumns;
        layout.marginWidth = 0;
        layout.marginHeight = 0;

        composite.setLayout(layout);

        GridData data = new GridData();

        data.horizontalAlignment = 4;
        data.grabExcessHorizontalSpace = true;

        data.verticalAlignment = 4;
        data.grabExcessVerticalSpace = true;

        composite.setLayoutData(data);
        return composite;
    }

    private void createContentArea(int numColumns) {

        Composite mainPanel = this; // new Composite(parent, SWT.BORDER);
        mainPanel.setLayout(new GridLayout(numColumns, false));
        mainPanel.setLayoutData(new GridData(GridData.FILL_BOTH));

        if (isEditable) {

            textItem = WidgetFactory.createText(mainPanel);
            textItem.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e) {
                    if (textItem.getText().trim().length() > 0) {
                        addButton.getParent().getShell().setDefaultButton(addButton);
                    }
                    setButtonEnablement();
                }
            });

            textItem.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    parentDefaultButton = addButton.getParent().getShell().getDefaultButton();
                    if (addButton.isEnabled()) {
                        addButton.getParent().getShell().setDefaultButton(addButton);
                    }
                }

                public void focusLost(FocusEvent e) {
                    shell.setDefaultButton(parentDefaultButton);
                }

            });

            addButton = WidgetFactory.createPushButton(mainPanel);
            addButton.setText(Messages.Add);
            addButton.addSelectionListener(this);
        }

        GridData itemsTableLayoutData = new GridData(GridData.FILL_BOTH);
        itemsTableLayoutData.horizontalSpan = (numColumns - 1);
        itemsTableLayoutData.grabExcessHorizontalSpace = true;
        itemsTableLayoutData.grabExcessVerticalSpace = true;

        itemsTableViewer = new TableViewer(mainPanel, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
        itemsTable = itemsTableViewer.getTable();
        itemsTable.setLayoutData(new GridData(GridData.FILL_BOTH));
        itemsTable.setLinesVisible(true);
        itemsTable.setHeaderVisible(true);
        itemsTable.addSelectionListener(this);
        itemsTable.setLayoutData(itemsTableLayoutData);

        TableColumn itemColumn = new TableColumn(itemsTable, SWT.NONE);
        itemColumn.setText(Messages.Item);
        itemColumn.setWidth(100);

        StringListContentProvider cp = new StringListContentProvider();
        itemsTableViewer.setContentProvider(cp);
        itemsTableViewer.setLabelProvider(new StringListLabelProvider());
        itemsTableViewer.setColumnProperties(COLUMN_PROPERTIES);
        itemsTableViewer.setInput(null);

        if (isEditable) {
            itemsTableViewer.setCellModifier(new StringListCellModifier());
        }

        cellEditors = new CellEditor[1];
        cellEditors[COLUMN_ITEM] = new TextCellEditor(itemsTable);
        cellEditors[COLUMN_ITEM].setValidator(new ICellEditorValidator() {
            public String isValid(Object value) {
                if (validator == null) {
                    return null;
                } else {
                    ValidationEvent event;
                    if (!cellEditors[COLUMN_ITEM].isActivated()) {
                        event = new ValidationEvent(ValidationEvent.ACTIVATE, (String)value);
                    } else {
                        event = new ValidationEvent(ValidationEvent.CHANGE, (String)value);
                    }
                    return validator.isValid(event);
                }
            }
        });

        itemsTableViewer.setCellEditors(cellEditors);

        TableAutoSizeControlListener tableAutoSizeListener = new TableAutoSizeControlListener(itemsTable);
        tableAutoSizeListener.addResizableColumn(itemColumn, 1);
        itemsTable.addControlListener(tableAutoSizeListener);

        Composite buttonsPanel = new Composite(mainPanel, 0);
        GridLayout layout = new GridLayout();
        numColumns = 1;
        buttonsPanel.setLayout(layout);

        removeButton = WidgetFactory.createPushButton(buttonsPanel);
        removeButton.setText(Messages.Remove);
        removeButton.setLayoutData(new GridData(256));
        removeButton.addSelectionListener(this);

        removeAllButton = WidgetFactory.createPushButton(buttonsPanel);
        removeAllButton.setText(Messages.Remove_all);
        removeAllButton.setLayoutData(new GridData(256));
        removeAllButton.addSelectionListener(this);

        moveUpButton = WidgetFactory.createPushButton(buttonsPanel);
        moveUpButton.setText(Messages.Move_up);
        moveUpButton.setLayoutData(new GridData(256));
        moveUpButton.addSelectionListener(this);

        moveDownButton = WidgetFactory.createPushButton(buttonsPanel);
        moveDownButton.setText(Messages.Move_down);
        moveDownButton.setLayoutData(new GridData(256));
        moveDownButton.addSelectionListener(this);

        setButtonEnablement();
    }

    public void widgetDefaultSelected(SelectionEvent e) {
    }

    public void widgetSelected(SelectionEvent e) {

        if (e.widget == addButton) {
            String itemToAdd = textItem.getText();
            if (validator != null && validator.isValid(new ValidationEvent(ValidationEvent.ADD, itemToAdd)) == null) {
                itemToAdd = itemToAdd.trim();

                if (itemToAdd.length() > 0) {
                    itemToAdd = NlsUtil.toUpperCase(itemToAdd);
                    Item item = new Item(itemToAdd);
                    itemsList.add(item);
                    textItem.setText(""); //$NON-NLS-1$
                    textItem.setFocus();
                    itemsTableViewer.setInput(itemsList);
                    if (itemsList.size() > 0) {
                        itemsTable.setTopIndex(itemsList.size());
                    }
                }
            }

        } else if (e.widget == removeButton) {
            int[] selections = itemsTable.getSelectionIndices();
            if (selections != null) {
                for (int loop = selections.length - 1; loop >= 0; loop--) {
                    itemsList.remove(selections[loop]);
                }
                itemsTableViewer.refresh();
            }
        } else if (e.widget == removeAllButton) {
            itemsList.clear();
            itemsTableViewer.refresh();
        } else if (e.widget == moveUpButton) {
            int[] selections = itemsTable.getSelectionIndices();
            itemsTable.deselectAll();
            if (selections != null) {

                for (int loop = 0; loop < selections.length; loop++) {
                    if (selections[loop] > 0) {
                        Item temp = (Item)itemsList.remove(selections[loop]);
                        itemsList.add(selections[loop] - 1, temp);
                    }
                }
                itemsTableViewer.refresh();

                for (int loop = 0; loop < selections.length; loop++) {
                    if (selections[loop] > 0) {
                        itemsTable.select(selections[loop] - 1);
                    } else {
                        itemsTable.select(selections[loop]);
                    }
                }
            }
        } else if (e.widget == moveDownButton) {
            int[] selections = itemsTable.getSelectionIndices();
            itemsTable.deselectAll();

            if (selections != null) {

                for (int loop = selections.length - 1; loop >= 0; loop--) {
                    if (selections[loop] < itemsList.size() - 1) {
                        Item temp = (Item)itemsList.remove(selections[loop]);
                        itemsList.add(selections[loop] + 1, temp);
                    }
                }
                itemsTableViewer.refresh();

                for (int loop = 0; loop < selections.length; loop++) {
                    if (selections[loop] < itemsList.size() - 1) {
                        itemsTable.select(selections[loop] + 1);
                    } else {
                        itemsTable.select(selections[loop]);
                    }
                }
            }
        }

        setButtonEnablement();
    }

    private class StringListContentProvider implements IStructuredContentProvider {

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

        public Object[] getElements(Object arg0) {
            return itemsList.toArray();
        }

    }

    private class StringListLabelProvider extends LabelProvider {
        @Override
        public String getText(Object element) {
            Item item = (Item)element;
            return item.getValue();
        }
    }

    private class StringListCellModifier implements ICellModifier {

        public boolean canModify(Object element, String property) {
            return true;
        }

        public Object getValue(Object element, String property) {
            Item item = (Item)element;
            return item.getValue();
        }

        public void modify(Object element, String property, Object value) {

            TableItem tableItem = (TableItem)element;

            Item item = (Item)tableItem.getData();
            item.setValue((String)value);
            itemsTableViewer.refresh();
        }

    }

    private class Item {

        private String value;

        public Item(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return getValue();
        }

    }
}
