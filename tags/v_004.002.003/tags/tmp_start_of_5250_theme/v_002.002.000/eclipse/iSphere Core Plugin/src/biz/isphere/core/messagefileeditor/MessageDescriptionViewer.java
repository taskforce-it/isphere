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
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import biz.isphere.base.versioncheck.PluginCheck;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.DialogActionTypes;
import biz.isphere.core.internal.IEditor;
import biz.isphere.core.internal.Size;

import com.ibm.as400.access.AS400;

public class MessageDescriptionViewer {

    private AS400 as400;
    private String connection;
    private String library;
    private String messageFile;
    private String mode;
    private Text textFilter;
    private String filterMessage = "";
    private TableViewer _tableViewer;
    private Table _table;
    private Object[] selectedItems;
    private Shell shell;
    private FilterTableViewer _filterTableViewer;
    private Button buttonNo;
    private Button buttonYes;
    private Object[] messageDescriptions;
    private IWorkbenchPartSite site;

    private class LabelProviderTableViewer extends LabelProvider implements ITableLabelProvider {
        public String getColumnText(Object element, int columnIndex) {
            if (columnIndex == 0) {
                return ((MessageDescription)element).getMessageId();
            } else if (columnIndex == 1) {
                return ((MessageDescription)element).getMessage();
            }
            return "*UNKNOWN";
        }

        public Image getColumnImage(Object element, int columnIndex) {
            if (columnIndex == 0) {
                return ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_MESSAGE);
            } else {
                return null;
            }
        }
    }

    private class ContentProviderTableViewer implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            if (messageDescriptions == null) {
                QMHRTVM qmhrtvm = new QMHRTVM();
                messageDescriptions = qmhrtvm.run(as400, connection, library, messageFile, "*ALL");
            }
            return messageDescriptions;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

    private class SorterTableViewer extends ViewerSorter {
        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            return ((MessageDescription)e1).getMessageId().compareTo(((MessageDescription)e2).getMessageId());
        }
    }

    private class FilterTableViewer extends ViewerFilter {
        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element) {

            if (!filterMessage.equals("")) {
                String elementMessage = ((MessageDescription)element).getMessage();
                if (buttonNo.getSelection() && !elementMessage.toUpperCase().contains(filterMessage.toUpperCase())) {
                    return false;
                } else if (buttonYes.getSelection() && !elementMessage.contains(filterMessage)) {
                    return false;
                }
            }

            return true;

        }
    }

    private class TableViewerSelectionAdapter extends SelectionAdapter {
        @Override
        public void widgetSelected(SelectionEvent event) {
            try {
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(AbstractViewMessageDescriptionPreview.ID);
            } catch (PartInitException e) {
                // ignore errors
            }
        }
    }

    public MessageDescriptionViewer(AS400 as400, String connection, String library, String messageFile, String mode, IWorkbenchPartSite site) {
        this.as400 = as400;
        this.connection = connection;
        this.library = library;
        this.messageFile = messageFile;
        this.mode = mode;
        this.messageDescriptions = null;
        this.site = site;
    }

    /**
     * @wbp.parser.entryPoint
     */
    public void createContents(Composite parent) {

        shell = parent.getShell();

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());

        Composite compositeHeader = new Composite(container, SWT.NONE);
        compositeHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        GridLayout gridLayoutCompositeHeader = new GridLayout(5, false);
        compositeHeader.setLayout(gridLayoutCompositeHeader);

        Label labelFilter = new Label(compositeHeader, SWT.NONE);
        labelFilter.setText(Messages.Filter_colon);

        textFilter = new Text(compositeHeader, SWT.BORDER);
        textFilter.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent keyEvent) {
                String text = textFilter.getText();
                if (!text.equals(filterMessage)) {
                    filterMessage = text;
                    _tableViewer.addFilter(_filterTableViewer);
                }
            }
        });
        textFilter.setText("");
        textFilter.setEditable(true);
        textFilter.setLayoutData(new GridData(Size.getSize(100), SWT.DEFAULT));

        Label labelCaseSensitive = new Label(compositeHeader, SWT.NONE);
        labelCaseSensitive.setText(Messages.Case_sensitive_colon);

        Composite groupCaseSensitive = new Composite(compositeHeader, SWT.NONE);
        groupCaseSensitive.setBackgroundMode(SWT.INHERIT_FORCE);
        GridLayout editableLayout = new GridLayout();
        editableLayout.numColumns = 2;
        groupCaseSensitive.setLayout(editableLayout);

        buttonNo = new Button(groupCaseSensitive, SWT.RADIO);
        buttonNo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                _tableViewer.addFilter(_filterTableViewer);
            }
        });
        buttonNo.setText(Messages.No);
        buttonNo.setSelection(true);

        buttonYes = new Button(groupCaseSensitive, SWT.RADIO);
        buttonYes.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                _tableViewer.addFilter(_filterTableViewer);
            }
        });
        buttonYes.setText(Messages.Yes);
        buttonYes.setSelection(false);

        Composite compositePreviewButton = new Composite(compositeHeader, SWT.NONE);
        compositePreviewButton.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false, 1, 1));
        compositePreviewButton.setLayout(new GridLayout(1, false));

        if (PluginCheck.hasPlugin("biz.isphere.rse")) {
            Button buttonMessagePreview = new Button(compositePreviewButton, SWT.PUSH);
            buttonMessagePreview.setText(Messages.Display_MessageDescription_Preview_View);
            buttonMessagePreview.setToolTipText(Messages.Display_MessageDescription_Preview_View_ToolTip);
            buttonMessagePreview.addSelectionListener(new TableViewerSelectionAdapter());
        }

        _tableViewer = new TableViewer(container, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
        _tableViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                if (_tableViewer.getSelection() instanceof IStructuredSelection) {

                    IStructuredSelection structuredSelection = (IStructuredSelection)_tableViewer.getSelection();
                    MessageDescription _messageDescription = (MessageDescription)structuredSelection.getFirstElement();

                    MessageDescriptionDetailDialog _messageDescriptionDetailDialog = new MessageDescriptionDetailDialog(shell, as400,
                        DialogActionTypes.getSubEditorActionType(mode), _messageDescription);
                    if (_messageDescriptionDetailDialog.open() == Dialog.OK) {
                    }

                }
            }
        });
        _tableViewer.setSorter(new SorterTableViewer());
        _tableViewer.setLabelProvider(new LabelProviderTableViewer());
        _tableViewer.setContentProvider(new ContentProviderTableViewer());
        _filterTableViewer = new FilterTableViewer();
        _tableViewer.addFilter(_filterTableViewer);
         site.setSelectionProvider(_tableViewer);

        _table = _tableViewer.getTable();
        _table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent keyEvent) {
                if (keyEvent.keyCode == 127) {
                    String[] validMenuItems = investigateValidMenuItems();
                    for (int idx = 0; idx < validMenuItems.length; idx++) {
                        if (validMenuItems[idx].equals("*DELETE")) {
                            executeMenuItemDelete();
                            break;
                        }
                    }
                }
            }
        });
        _table.setLinesVisible(true);
        _table.setHeaderVisible(true);
        _table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final TableColumn columnMessageId = new TableColumn(_table, SWT.NONE);
        columnMessageId.setWidth(Size.getSize(100));
        columnMessageId.setText(Messages.Message_Id);

        final TableColumn columnMessage = new TableColumn(_table, SWT.NONE);
        columnMessage.setWidth(Size.getSize(900));
        columnMessage.setText(Messages.Message);

        final Menu menuTableMessageDescriptions = new Menu(_table);
        menuTableMessageDescriptions.addMenuListener(new MenuAdapter() {

            private MenuItem menuItemNew;
            private MenuItem menuItemChange;
            private MenuItem menuItemCopy;
            private MenuItem menuItemDelete;
            private MenuItem menuItemDisplay;
            private MenuItem menuSeparator1;
            private MenuItem menuItemRefresh;

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
                if (!((menuSeparator1 == null) || (menuSeparator1.isDisposed()))) {
                    menuSeparator1.dispose();
                }
                if (!((menuItemRefresh == null) || (menuItemRefresh.isDisposed()))) {
                    menuItemRefresh.dispose();
                }

            }

            public void createMenuItems() {

                String[] validMenuItems = investigateValidMenuItems();

                boolean isNew = false;
                boolean isChange = false;
                boolean isCopy = false;
                boolean isDelete = false;
                boolean isDisplay = false;
                boolean isRefresh = false;
                boolean isBlock1 = false;
                boolean isBlock2 = false;

                for (int idx = 0; idx < validMenuItems.length; idx++) {
                    if (validMenuItems[idx].equals("*NEW")) {
                        isNew = true;
                        isBlock1 = true;
                    } else if (validMenuItems[idx].equals("*CHANGE")) {
                        isChange = true;
                        isBlock1 = true;
                    } else if (validMenuItems[idx].equals("*COPY")) {
                        isCopy = true;
                        isBlock1 = true;
                    } else if (validMenuItems[idx].equals("*DELETE")) {
                        isDelete = true;
                        isBlock1 = true;
                    } else if (validMenuItems[idx].equals("*DISPLAY")) {
                        isDisplay = true;
                        isBlock1 = true;
                    } else if (validMenuItems[idx].equals("*REFRESH")) {
                        isRefresh = true;
                        isBlock2 = true;
                    }
                }

                if (isNew) {
                    menuItemNew = new MenuItem(menuTableMessageDescriptions, SWT.NONE);
                    menuItemNew.setText(Messages.New);
                    menuItemNew.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_NEW));
                    menuItemNew.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            executeMenuItemNew();
                        }
                    });
                }

                if (isChange) {
                    menuItemChange = new MenuItem(menuTableMessageDescriptions, SWT.NONE);
                    menuItemChange.setText(Messages.Change);
                    menuItemChange.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_CHANGE));
                    menuItemChange.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            executeMenuItemChange();
                        }
                    });
                }

                if (isCopy) {
                    menuItemCopy = new MenuItem(menuTableMessageDescriptions, SWT.NONE);
                    menuItemCopy.setText(Messages.Copy);
                    menuItemCopy.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_COPY));
                    menuItemCopy.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            executeMenuItemCopy();
                        }
                    });
                }

                if (isDelete) {
                    menuItemDelete = new MenuItem(menuTableMessageDescriptions, SWT.NONE);
                    menuItemDelete.setText(Messages.Delete);
                    menuItemDelete.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_DELETE));
                    menuItemDelete.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            executeMenuItemDelete();
                        }
                    });
                }

                if (isDisplay) {
                    menuItemDisplay = new MenuItem(menuTableMessageDescriptions, SWT.NONE);
                    menuItemDisplay.setText(Messages.Display);
                    menuItemDisplay.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_DISPLAY));
                    menuItemDisplay.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            executeMenuItemDisplay();
                        }
                    });
                }

                if (isBlock1 && isBlock2) {
                    menuSeparator1 = new MenuItem(menuTableMessageDescriptions, SWT.SEPARATOR);
                }

                if (isRefresh) {
                    menuItemRefresh = new MenuItem(menuTableMessageDescriptions, SWT.NONE);
                    menuItemRefresh.setText(Messages.Refresh);
                    menuItemRefresh.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_REFRESH));
                    menuItemRefresh.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            executeMenuItemRefresh();
                        }
                    });
                }

            }
        });
        _table.setMenu(menuTableMessageDescriptions);

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

    private String[] investigateValidMenuItems() {
        ArrayList<String> validMenuItem = new ArrayList<String>();
        int menuItemChange = 0;
        int menuItemCopy = 0;
        int menuItemDelete = 0;
        int menuItemDisplay = 0;
        for (int idx = 0; idx < selectedItems.length; idx++) {
            if (selectedItems[idx] instanceof MessageDescription) {
                MessageDescription _messageDescription = (MessageDescription)selectedItems[idx];
                if (mode.equals(IEditor.EDIT)) {
                    if (isValid(_messageDescription, "*CHANGE")) {
                        menuItemChange++;
                    }
                    if (isValid(_messageDescription, "*COPY")) {
                        menuItemCopy++;
                    }
                    if (isValid(_messageDescription, "*DELETE")) {
                        menuItemDelete++;
                    }
                }
                if (isValid(_messageDescription, "*DISPLAY")) {
                    menuItemDisplay++;
                }
            }
        }
        if (menuItemChange > 0 && menuItemChange == selectedItems.length) {
            validMenuItem.add("*CHANGE");
        }
        if (menuItemCopy > 0 && menuItemCopy == selectedItems.length) {
            validMenuItem.add("*COPY");
        }
        if (menuItemDelete > 0 && menuItemDelete == selectedItems.length) {
            validMenuItem.add("*DELETE");
        }
        if (menuItemDisplay > 0 && menuItemDisplay == selectedItems.length) {
            validMenuItem.add("*DISPLAY");
        }
        if (mode.equals(IEditor.EDIT)) {
            validMenuItem.add("*NEW");
        }
        validMenuItem.add("*REFRESH");

        String[] validMenuItems = new String[validMenuItem.size()];
        validMenuItem.toArray(validMenuItems);
        return validMenuItems;
    }

    public boolean isValid(MessageDescription _messageDescription, Object additionalInformation) {
        String menuItem = (String)additionalInformation;
        if (menuItem.equals("*CHANGE")) {
            return true;
        } else if (menuItem.equals("*COPY")) {
            return true;
        } else if (menuItem.equals("*DELETE")) {
            return true;
        } else if (menuItem.equals("*DISPLAY")) {
            return true;
        }
        return false;
    }

    private void executeMenuItemNew() {

        MessageDescription _messageDescription = new MessageDescription();
        _messageDescription.setConnection(connection);
        _messageDescription.setLibrary(library);
        _messageDescription.setMessageFile(messageFile);
        MessageDescriptionDetailDialog _messageDescriptionDetailDialog = new MessageDescriptionDetailDialog(shell, as400, DialogActionTypes.CREATE,
            _messageDescription);
        if (_messageDescriptionDetailDialog.open() == Dialog.OK) {
            _tableViewer.add(_messageDescription);
        }

        deSelectAllItems();

    }

    private void executeMenuItemChange() {

        for (int idx = 0; idx < selectedItems.length; idx++) {
            if (selectedItems[idx] instanceof MessageDescription) {

                MessageDescriptionDetailDialog _messageDescriptionDetailDialog = new MessageDescriptionDetailDialog(shell, as400,
                    DialogActionTypes.CHANGE, (MessageDescription)selectedItems[idx]);
                if (_messageDescriptionDetailDialog.open() == Dialog.OK) {
                    _tableViewer.update((selectedItems[idx]), null);
                }

            }
        }

        deSelectAllItems();

    }

    private void executeMenuItemCopy() {

        for (int idx = 0; idx < selectedItems.length; idx++) {
            if (selectedItems[idx] instanceof MessageDescription) {

                MessageDescription _messageDescription = new MessageDescription();
                _messageDescription.setConnection(connection);
                _messageDescription.setLibrary(library);
                _messageDescription.setMessageFile(messageFile);
                _messageDescription.setMessageId(((MessageDescription)selectedItems[idx]).getMessageId());
                _messageDescription.setMessage(((MessageDescription)selectedItems[idx]).getMessage());
                _messageDescription.setHelpText(((MessageDescription)selectedItems[idx]).getHelpText());
                _messageDescription.setSeverity(((MessageDescription)selectedItems[idx]).getSeverity());
                _messageDescription.setCcsid(((MessageDescription)selectedItems[idx]).getCcsid());

                ArrayList<?> fieldFormats1 = ((MessageDescription)selectedItems[idx]).getFieldFormats();
                ArrayList<FieldFormat> fieldFormats2 = new ArrayList<FieldFormat>();
                for (int pos = 0; pos < fieldFormats1.size(); pos++) {
                    FieldFormat fieldFormat1 = (FieldFormat)fieldFormats1.get(pos);
                    FieldFormat fieldFormat2 = new FieldFormat();
                    fieldFormat2.setType(fieldFormat1.getType());
                    fieldFormat2.setVary(fieldFormat1.isVary());
                    fieldFormat2.setLength(fieldFormat1.getLength());
                    fieldFormat2.setDecimalPositions(fieldFormat1.getDecimalPositions());
                    fieldFormat2.setBytes(fieldFormat1.getBytes());
                    fieldFormats2.add(fieldFormat2);
                }
                _messageDescription.setFieldFormats(fieldFormats2);

                MessageDescriptionDetailDialog _messageDescriptionDetailDialog = new MessageDescriptionDetailDialog(shell, as400,
                    DialogActionTypes.COPY, _messageDescription);
                if (_messageDescriptionDetailDialog.open() == Dialog.OK) {
                    _tableViewer.add(_messageDescription);
                }

            }
        }

        deSelectAllItems();

    }

    private void executeMenuItemDelete() {

        for (int idx = 0; idx < selectedItems.length; idx++) {
            if (selectedItems[idx] instanceof MessageDescription) {

                MessageDescriptionDetailDialog _messageDescriptionDetailDialog = new MessageDescriptionDetailDialog(shell, as400,
                    DialogActionTypes.DELETE, (MessageDescription)selectedItems[idx]);
                if (_messageDescriptionDetailDialog.open() == Dialog.OK) {
                    _tableViewer.remove((selectedItems[idx]));
                }

            }
        }

        deSelectAllItems();

    }

    private void executeMenuItemDisplay() {

        for (int idx = 0; idx < selectedItems.length; idx++) {
            if (selectedItems[idx] instanceof MessageDescription) {

                MessageDescriptionDetailDialog _messageDescriptionDetailDialog = new MessageDescriptionDetailDialog(shell, as400,
                    DialogActionTypes.DISPLAY, (MessageDescription)selectedItems[idx]);
                if (_messageDescriptionDetailDialog.open() == Dialog.OK) {
                }

            }
        }

        deSelectAllItems();

    }

    private void executeMenuItemRefresh() {

        messageDescriptions = null;

        _tableViewer.refresh();

        deSelectAllItems();

    }

    private void deSelectAllItems() {
        _tableViewer.setSelection(new StructuredSelection(), true);
        selectedItems = new Object[0];
    }

}
