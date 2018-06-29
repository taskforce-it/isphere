/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefileeditor;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.DialogActionTypes;
import biz.isphere.core.internal.Size;
import biz.isphere.core.swt.widgets.extension.WidgetFactory;

public class FieldFormatViewer {

    private TableViewer _tableViewer;
    private Table _table;
    private Object[] selectedItems;
    private Shell shell;
    private int actionType;
    private ArrayList<FieldFormat> _fieldFormats = new ArrayList<FieldFormat>();
    private Button buttonUp;
    private Button buttonDown;
    private Text message;
    private Text helpText;

    private class LabelProviderTableViewer extends LabelProvider implements ITableLabelProvider {
        public String getColumnText(Object element, int columnIndex) {
            FieldFormat fieldFormat = (FieldFormat)element;
            if (columnIndex == 0) {
                return "&" + Integer.toString(_fieldFormats.indexOf(fieldFormat) + 1);
            } else if (columnIndex == 1) {
                return fieldFormat.getType();
            } else if (columnIndex == 2) {
                if (fieldFormat.isVary()) {
                    return "*VARY";
                } else {
                    return Integer.toString(fieldFormat.getLength());
                }
            } else if (columnIndex == 3) {
                if (fieldFormat.isVary()) {
                    return Integer.toString(fieldFormat.getBytes());
                } else {
                    if (fieldFormat.getType().equals("*DEC")) {
                        return Integer.toString(fieldFormat.getDecimalPositions());
                    } else {
                        return "";
                    }
                }
            }
            return "*UNKNOWN";
        }

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
    }

    private class ContentProviderTableViewer implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            FieldFormat[] fieldFormats = new FieldFormat[_fieldFormats.size()];
            _fieldFormats.toArray(fieldFormats);
            return fieldFormats;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

    public FieldFormatViewer(int actionType, MessageDescription _messageDescription, Text message, Text helpText) {

        this.actionType = actionType;
        // this._messageDescription = _messageDescription;
        this.message = message;
        this.helpText = helpText;

        for (int idx = 0; idx < _messageDescription.getFieldFormats().size(); idx++) {
            _fieldFormats.add((FieldFormat)_messageDescription.getFieldFormats().get(idx));
        }

    }

    /**
     * @wbp.parser.entryPoint
     */
    public void createContents(Composite parent) {

        shell = parent.getShell();

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Group groupFieldFormats = new Group(container, SWT.NONE);
        groupFieldFormats.setText(Messages.Field_formats);
        GridLayout groupFieldFormatsLayout = new GridLayout(2, false);
        groupFieldFormatsLayout.verticalSpacing = 25;
        groupFieldFormats.setLayout(groupFieldFormatsLayout);
        GridData groupFieldFormatsGridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        groupFieldFormatsGridData.heightHint = getTableHeight(4);
        groupFieldFormats.setLayoutData(groupFieldFormatsGridData);

        _tableViewer = new TableViewer(groupFieldFormats, SWT.FULL_SELECTION | SWT.BORDER);
        _tableViewer.setLabelProvider(new LabelProviderTableViewer());
        _tableViewer.setContentProvider(new ContentProviderTableViewer());

        _tableViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                if (_tableViewer.getSelection() instanceof IStructuredSelection) {

                    IStructuredSelection structuredSelection = (IStructuredSelection)_tableViewer.getSelection();
                    FieldFormat _fieldFormat = (FieldFormat)structuredSelection.getFirstElement();

                    switch (actionType) {
                    case DialogActionTypes.CREATE:
                        performChange(_fieldFormat);
                        break;
                    case DialogActionTypes.COPY:
                        performChange(_fieldFormat);
                        break;
                    case DialogActionTypes.CHANGE:
                        performChange(_fieldFormat);
                        break;
                    case DialogActionTypes.DELETE:
                        performDisplay(_fieldFormat);
                        break;
                    case DialogActionTypes.DISPLAY:
                        performDisplay(_fieldFormat);
                        break;
                    }

                }
            }
        });

        _table = _tableViewer.getTable();
        _table.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                refreshUpDown();
            }
        });

        _table.setLinesVisible(true);
        _table.setHeaderVisible(true);
        _table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final TableColumn columnField = new TableColumn(_table, SWT.NONE);
        columnField.setWidth(Size.getSize(100));
        columnField.setText(Messages.Field);

        final TableColumn columnType = new TableColumn(_table, SWT.NONE);
        columnType.setWidth(Size.getSize(100));
        columnType.setText(Messages.Type);

        final TableColumn columnLength = new TableColumn(_table, SWT.NONE);
        columnLength.setWidth(Size.getSize(100));
        columnLength.setText(Messages.Length);

        final TableColumn columnDecimalPositionsBytes = new TableColumn(_table, SWT.NONE);
        columnDecimalPositionsBytes.setWidth(Size.getSize(100));
        columnDecimalPositionsBytes.setText(Messages.Decimal_positions_Bytes);

        Composite compositeUpDown = new Composite(groupFieldFormats, SWT.NONE);
        compositeUpDown.setLayout(new GridLayout(1, false));
        compositeUpDown.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1));

        buttonUp = WidgetFactory.createPushButton(compositeUpDown);
        buttonUp.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                moveUpDown(-1);
            }
        });
        buttonUp.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, true, 1, 1));
        buttonUp.setText(Messages.Up);
        buttonUp.setEnabled(false);

        buttonDown = WidgetFactory.createPushButton(compositeUpDown);
        buttonDown.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                moveUpDown(+1);
            }
        });
        buttonDown.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, false, true, 1, 1));
        buttonDown.setText(Messages.Down);
        buttonDown.setEnabled(false);

        final Menu menuTableFieldFormats = new Menu(_table);
        menuTableFieldFormats.addMenuListener(new MenuAdapter() {
            private MenuItem menuItemNew;
            private MenuItem menuItemChange;
            private MenuItem menuItemCopy;
            private MenuItem menuItemDelete;
            private MenuItem menuItemDisplay;

            @Override
            public void menuShown(MenuEvent event) {
                retrieveSelectedTableItems();
                destroyMenuItems();
                createMenuItems();
            }

            public void destroyMenuItems() {
                if (!((menuItemNew == null) || (menuItemNew.isDisposed()))) {
                    menuItemNew.dispose();
                }
                if (!((menuItemChange == null) || (menuItemChange.isDisposed()))) {
                    menuItemChange.dispose();
                }
                if (!((menuItemCopy == null) || (menuItemCopy.isDisposed()))) {
                    menuItemCopy.dispose();
                }
                if (!((menuItemDelete == null) || (menuItemDelete.isDisposed()))) {
                    menuItemDelete.dispose();
                }
                if (!((menuItemDisplay == null) || (menuItemDisplay.isDisposed()))) {
                    menuItemDisplay.dispose();
                }
            }

            public void createMenuItems() {
                boolean menuItemChange = false;
                boolean menuItemCopy = false;
                boolean menuItemDelete = false;
                boolean menuItemDisplay = false;
                for (int idx = 0; idx < selectedItems.length; idx++) {
                    if (selectedItems[idx] instanceof FieldFormat) {
                        if (actionType == DialogActionTypes.CREATE || actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.COPY) {
                            menuItemChange = true;
                            menuItemCopy = true;
                            menuItemDelete = true;
                        }
                        menuItemDisplay = true;
                    }
                }
                if (actionType == DialogActionTypes.CREATE || actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.COPY) {
                    createMenuItemNew();
                }
                if (menuItemChange) createMenuItemChange();
                if (menuItemCopy) createMenuItemCopy();
                if (menuItemDelete) createMenuItemDelete();
                if (menuItemDisplay) createMenuItemDisplay();

            }

            public void createMenuItemNew() {
                menuItemNew = new MenuItem(menuTableFieldFormats, SWT.NONE);
                menuItemNew.setText(Messages.New);
                menuItemNew.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_NEW));
                menuItemNew.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        ArrayList<Object> arrayListFieldFormats = new ArrayList<Object>();
                        for (int idx = 0; idx < _table.getItemCount(); idx++) {
                            arrayListFieldFormats.add(_tableViewer.getElementAt(idx));
                        }
                        FieldFormat _fieldFormat = new FieldFormat();
                        FieldFormatDetailDialog _fieldFormatDetailDialog = new FieldFormatDetailDialog(shell, DialogActionTypes.CREATE, _fieldFormat);
                        if (_fieldFormatDetailDialog.open() == Dialog.OK) {
                            _fieldFormats.add(_fieldFormat);
                            _tableViewer.refresh();
                        }
                        deSelectAllItems();
                    }
                });
            }

            public void createMenuItemChange() {
                menuItemChange = new MenuItem(menuTableFieldFormats, SWT.NONE);
                menuItemChange.setText(Messages.Change);
                menuItemChange.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_CHANGE));
                menuItemChange.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        for (int idx = 0; idx < selectedItems.length; idx++) {
                            if (selectedItems[idx] instanceof FieldFormat) {
                                performChange((FieldFormat)selectedItems[idx]);
                            }
                        }
                        deSelectAllItems();
                    }
                });
            }

            public void createMenuItemCopy() {
                menuItemCopy = new MenuItem(menuTableFieldFormats, SWT.NONE);
                menuItemCopy.setText(Messages.Copy);
                menuItemCopy.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_COPY));
                menuItemCopy.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        for (int idx = 0; idx < selectedItems.length; idx++) {
                            if (selectedItems[idx] instanceof FieldFormat) {
                                FieldFormat _fieldFormat = new FieldFormat();
                                _fieldFormat.setType(((FieldFormat)selectedItems[idx]).getType());
                                _fieldFormat.setVary(((FieldFormat)selectedItems[idx]).isVary());
                                _fieldFormat.setLength(((FieldFormat)selectedItems[idx]).getLength());
                                _fieldFormat.setDecimalPositions(((FieldFormat)selectedItems[idx]).getDecimalPositions());
                                _fieldFormat.setBytes(((FieldFormat)selectedItems[idx]).getBytes());
                                FieldFormatDetailDialog _fieldFormatDetailDialog = new FieldFormatDetailDialog(shell, DialogActionTypes.COPY,
                                    _fieldFormat);
                                if (_fieldFormatDetailDialog.open() == Dialog.OK) {
                                    _fieldFormats.add(_fieldFormat);
                                    _tableViewer.refresh();
                                }
                            }
                        }
                        deSelectAllItems();
                    }
                });
            }

            public void createMenuItemDelete() {
                menuItemDelete = new MenuItem(menuTableFieldFormats, SWT.NONE);
                menuItemDelete.setText(Messages.Delete);
                menuItemDelete.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_DELETE));
                menuItemDelete.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        for (int idx = 0; idx < selectedItems.length; idx++) {
                            if (selectedItems[idx] instanceof FieldFormat) {
                                int position = _fieldFormats.indexOf(selectedItems[idx]) + 1;
                                FieldFormatDetailDialog _fieldFormatDetailDialog = new FieldFormatDetailDialog(shell, DialogActionTypes.DELETE,
                                    (FieldFormat)selectedItems[idx]);
                                if (_fieldFormatDetailDialog.open() == Dialog.OK) {
                                    _fieldFormats.remove(selectedItems[idx]);
                                    _tableViewer.refresh();
                                    message.setText(removeField(message.getText(), position));
                                    helpText.setText(removeField(helpText.getText(), position));
                                }
                            }
                        }
                        deSelectAllItems();
                    }
                });
            }

            public void createMenuItemDisplay() {
                menuItemDisplay = new MenuItem(menuTableFieldFormats, SWT.NONE);
                menuItemDisplay.setText(Messages.Display);
                menuItemDisplay.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_DISPLAY));
                menuItemDisplay.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        for (int idx = 0; idx < selectedItems.length; idx++) {
                            if (selectedItems[idx] instanceof FieldFormat) {
                                performDisplay((FieldFormat)selectedItems[idx]);
                            }
                        }
                        deSelectAllItems();
                    }
                });
            }
        });
        _table.setMenu(menuTableFieldFormats);
        _tableViewer.setInput(new Object());
    }

    private void retrieveSelectedTableItems() {
        if (_tableViewer.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection)_tableViewer.getSelection();
            selectedItems = structuredSelection.toArray();
        } else {
            selectedItems = new Object[0];
        }
    }

    private void refreshUpDown() {

        buttonUp.setEnabled(false);
        buttonDown.setEnabled(false);

        if (actionType == DialogActionTypes.CREATE || actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.COPY) {

            retrieveSelectedTableItems();

            if (selectedItems.length == 1) {
                if (selectedItems[0] instanceof FieldFormat) {
                    FieldFormat fieldFormat = (FieldFormat)selectedItems[0];
                    int position = _fieldFormats.indexOf(fieldFormat) + 1;
                    if (position > 1) {
                        buttonUp.setEnabled(true);
                    }
                    if (position < _fieldFormats.size()) {
                        buttonDown.setEnabled(true);
                    }
                }
            }

        }

    }

    private void moveUpDown(int offset) {

        retrieveSelectedTableItems();

        if (selectedItems.length == 1) {

            if (selectedItems[0] instanceof FieldFormat) {

                FieldFormat fieldFormat = (FieldFormat)selectedItems[0];

                int position = _fieldFormats.indexOf(fieldFormat);

                _fieldFormats.remove(position);

                _fieldFormats.add(position + offset, fieldFormat);

                _tableViewer.refresh();

                refreshUpDown();

                _table.setFocus();

                message.setText(moveField(message.getText(), position + 1, offset));

                helpText.setText(moveField(helpText.getText(), position + 1, offset));

            }

        }

    }

    private String moveField(String text, int selectedField, int offset) {

        String old1 = "&" + Integer.toString(selectedField);
        String new1 = "&" + Integer.toString(selectedField + offset);

        String old2 = new1;
        String new2 = old1;

        StringBuffer target = new StringBuffer();
        boolean ampersand = false;
        boolean digit = false;
        StringBuffer field = null;
        for (int idx = 0; idx < text.length(); idx++) {
            String character = text.substring(idx, idx + 1);
            if (!ampersand && character.compareTo("&") == 0) {
                ampersand = true;
                field = new StringBuffer();
                field.append(character);
            } else if (ampersand && character.compareTo("0") >= 0 && character.compareTo("9") <= 0) {
                digit = true;
                field.append(character);
            } else {
                if (ampersand) {
                    if (digit) {
                        String _field = field.toString();
                        if (_field.equals(old1)) {
                            target.append(new1);
                        } else if (_field.equals(old2)) {
                            target.append(new2);
                        } else {
                            target.append(_field);
                        }
                    } else {
                        target.append(field.toString());
                    }
                }
                ampersand = false;
                digit = false;
                target.append(character);
            }
        }
        if (ampersand) {
            if (digit) {
                String _field = field.toString();
                if (_field.equals(old1)) {
                    target.append(new1);
                } else if (_field.equals(old2)) {
                    target.append(new2);
                } else {
                    target.append(_field);
                }
            } else {
                target.append(field.toString());
            }
        }

        return target.toString();

    }

    private String removeField(String text, int selectedField) {

        String old = "&" + Integer.toString(selectedField);

        StringBuffer target = new StringBuffer();
        boolean ampersand = false;
        boolean digit = false;
        StringBuffer field = null;
        for (int idx = 0; idx < text.length(); idx++) {
            String character = text.substring(idx, idx + 1);
            if (!ampersand && character.compareTo("&") == 0) {
                ampersand = true;
                field = new StringBuffer();
                field.append(character);
            } else if (ampersand && character.compareTo("0") >= 0 && character.compareTo("9") <= 0) {
                digit = true;
                field.append(character);
            } else {
                if (ampersand) {
                    if (digit) {
                        String _field = field.toString();
                        if (!_field.equals(old)) {
                            int fieldNumber = Integer.parseInt(_field.substring(1));
                            if (fieldNumber > selectedField) {
                                fieldNumber--;
                                target.append("&" + Integer.toString(fieldNumber));
                            } else {
                                target.append(_field);
                            }
                        }
                    } else {
                        target.append(field.toString());
                    }
                }
                ampersand = false;
                digit = false;
                target.append(character);
            }
        }
        if (ampersand) {
            if (digit) {
                String _field = field.toString();
                if (!_field.equals(old)) {
                    int fieldNumber = Integer.parseInt(_field.substring(1));
                    if (fieldNumber > selectedField) {
                        fieldNumber--;
                        target.append("&" + Integer.toString(fieldNumber));
                    } else {
                        target.append(_field);
                    }
                }
            } else {
                target.append(field.toString());
            }
        }

        return target.toString();

    }

    private void performChange(FieldFormat fieldFormat) {
        FieldFormatDetailDialog _fieldFormatDetailDialog = new FieldFormatDetailDialog(shell, DialogActionTypes.CHANGE, fieldFormat);
        if (_fieldFormatDetailDialog.open() == Dialog.OK) {
            _tableViewer.update(fieldFormat, null);
            _tableViewer.refresh();
        }
    }

    private void performDisplay(FieldFormat fieldFormat) {
        FieldFormatDetailDialog _fieldFormatDetailDialog = new FieldFormatDetailDialog(shell, DialogActionTypes.DISPLAY, fieldFormat);
        if (_fieldFormatDetailDialog.open() == Dialog.OK) {
        }
    }

    private void deSelectAllItems() {

        _tableViewer.setSelection(new StructuredSelection(), true);

        selectedItems = new Object[0];

        buttonUp.setEnabled(false);
        buttonDown.setEnabled(false);

    }

    // TODO: implement algorithm to properly calculate the table height for a
    // given number of rows.
    public int getTableHeight(int visibleTableItems) {
        return 102;
    }

    public ArrayList<FieldFormat> getFieldFormats() {
        return _fieldFormats;
    }
}
