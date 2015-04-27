/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefilecompare.rse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.progress.UIJob;

import biz.isphere.base.internal.DialogSettingsManager;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.DialogActionTypes;
import biz.isphere.core.internal.IEditor;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.internal.MessageDescriptionHelper;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.core.internal.Size;
import biz.isphere.core.internal.StatusBar;
import biz.isphere.core.internal.api.retrievemessagedescription.IQMHRTVM;
import biz.isphere.core.messagefilecompare.MessageFileCompareEditorInput;
import biz.isphere.core.messagefilecompare.MessageFileCompareItem;
import biz.isphere.core.messagefileeditor.MessageDescription;
import biz.isphere.core.messagefileeditor.MessageDescriptionDetailDialog;
import biz.isphere.core.swt.widgets.WidgetFactory;

import com.ibm.as400.access.AS400;

public abstract class AbstractMessageFileCompareEditor extends EditorPart {

    public static final String ID = "biz.isphere.rse.messagefilecompare.rse.MessageFileCompareEditor"; //$NON-NLS-1$

    private static final String BUTTON_COPY_LEFT = "BUTTON_COPY_LEFT"; //$NON-NLS-1$
    private static final String BUTTON_COPY_RIGHT = "BUTTON_COPY_RIGHT"; //$NON-NLS-1$
    private static final String BUTTON_NO_COPY = "BUTTON_NO_COPY"; //$NON-NLS-1$
    private static final String BUTTON_EQUAL = "BUTTON_EQUAL"; //$NON-NLS-1$
    private static final String BUTTON_SINGLES = "BUTTON_SINGLES"; //$NON-NLS-1$
    private static final String BUTTON_DUPLICATES = "BUTTON_DUPLICATES"; //$NON-NLS-1$

    private MessageFileCompareEditorInput input;
    private MessageDescription[] leftMessageDescriptions;
    private MessageDescription[] rightMessageDescriptions;

    private boolean selectionChanged;
    private boolean isLeftMessageFileWarning;
    private boolean isRightMessageFileWarning;

    private TableViewer tableViewer;
    private TableFilter tableFilter;
    private TableFilterData filterData;

    private Button btnCompare;
    private Button btnSynchronize;

    private DialogSettingsManager dialogSettingsManager;

    private Label lblRightMessageFile;
    private Label lblLeftMessageFile;
    private Button btnCopyRight;
    private Button btnEqual;
    private Button btnNoCopy;
    private Button btnCopyLeft;
    private Button btnDuplicates;
    private Button btnSingles;

    private Shell shell;

    private Composite headerArea;
    private Composite optionsArea;

    private CLabel statusInfo;
    private CLabel statusBarFilterImage;
    private CLabel statusBarFilterText;

    private boolean isComparing;
    private boolean isSynchronizing;

    public AbstractMessageFileCompareEditor() {

        selectionChanged = true;
        isLeftMessageFileWarning = false;
        isRightMessageFileWarning = false;

        dialogSettingsManager = new DialogSettingsManager(getDialogSettings());
        shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    }

    @Override
    public void createPartControl(Composite parent) {

        parent.setLayout(new GridLayout(1, false));

        createHeaderArea(parent);
        createOptionsArea(parent);
        createCompareArea(parent);
        createrFooterArea(parent);

        loadScreenValues();

        refreshAndCheckMessageFileNames();
        refreshTableFilter();
    }

    private void createHeaderArea(Composite parent) {

        headerArea = new Composite(parent, SWT.NONE);
        headerArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        headerArea.setLayout(new GridLayout(2, true));

        Composite leftHeaderArea = new Composite(headerArea, SWT.NONE);
        leftHeaderArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        leftHeaderArea.setLayout(new GridLayout(2, false));

        lblLeftMessageFile = new Label(leftHeaderArea, SWT.BORDER);
        lblLeftMessageFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Button btnSelectLeftMessageFile = WidgetFactory.createPushButton(leftHeaderArea);
        btnSelectLeftMessageFile.setText(Messages.Select);
        btnSelectLeftMessageFile.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent arg0) {
                String connectionName = null;
                if (getEditorInput().getLeftMessageFile() != null) {
                    connectionName = getEditorInput().getLeftMessageFile().getConnectionName();
                }
                RemoteObject messageFile = performSelectRemoteObject(connectionName);
                if (messageFile != null) {
                    selectionChanged = true;
                    isLeftMessageFileWarning = false;
                    getEditorInput().setLeftMessageFile(messageFile);
                    refreshAndCheckMessageFileNames();
                }
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        Composite rightHeaderArea = new Composite(headerArea, SWT.NONE);
        rightHeaderArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        rightHeaderArea.setLayout(new GridLayout(2, false));

        lblRightMessageFile = new Label(rightHeaderArea, SWT.BORDER);
        lblRightMessageFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Button btnSelectRightMessageFile = WidgetFactory.createPushButton(rightHeaderArea);
        btnSelectRightMessageFile.setText(Messages.Select);
        btnSelectRightMessageFile.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent arg0) {
                String connectionName = null;
                if (getEditorInput().getRightMessageFile() != null) {
                    connectionName = getEditorInput().getRightMessageFile().getConnectionName();
                }
                RemoteObject messageFile = performSelectRemoteObject(connectionName);
                if (messageFile != null) {
                    selectionChanged = true;
                    isRightMessageFileWarning = false;
                    getEditorInput().setRightMessageFile(messageFile);
                    refreshAndCheckMessageFileNames();
                }
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });
    }

    private void createOptionsArea(Composite parent) {

        optionsArea = new Composite(parent, SWT.NONE);
        optionsArea.setLayout(new GridLayout(3, false));
        optionsArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        btnCompare = WidgetFactory.createPushButton(optionsArea);
        btnCompare.setLayoutData(createButtonLayoutData());
        btnCompare.setText(Messages.Compare);
        btnCompare.setToolTipText(Messages.Tooltip_start_compare);
        btnCompare.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent event) {
                performCompareMessageFiles();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
            }
        });

        createFilterOptionsArea(optionsArea);

        btnSynchronize = WidgetFactory.createPushButton(optionsArea);
        btnSynchronize.setLayoutData(createButtonLayoutData());
        btnSynchronize.setText(Messages.Synchronize);
        btnSynchronize.setToolTipText(Messages.Tooltip_start_synchronize);
        btnSynchronize.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                performSynchronizeMessageFiles();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
            }
        });
    }

    private void createFilterOptionsArea(Composite parent) {

        Group filterOptionsGroup = new Group(parent, SWT.NONE);
        filterOptionsGroup.setLayout(new GridLayout(5, false));
        filterOptionsGroup.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
        filterOptionsGroup.setText(Messages.Display);

        filterData = new TableFilterData();

        btnCopyRight = WidgetFactory.createToggleButton(filterOptionsGroup, SWT.FLAT);
        btnCopyRight.setImage(ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_COPY_RIGHT).createImage());
        btnCopyRight.setToolTipText(Messages.Tooltip_display_copy_from_left_to_right);
        btnCopyRight.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                refreshTableFilter();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
            }
        });

        btnEqual = WidgetFactory.createToggleButton(filterOptionsGroup, SWT.FLAT);
        btnEqual.setImage(ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_COPY_EQUAL).createImage());
        btnEqual.setToolTipText(Messages.Tooltip_display_equal_items);
        btnEqual.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                refreshTableFilter();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
            }
        });

        btnNoCopy = WidgetFactory.createToggleButton(filterOptionsGroup, SWT.FLAT);
        btnNoCopy.setImage(ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_COPY_NOT_EQUAL).createImage());
        btnNoCopy.setToolTipText(Messages.Tooltip_display_unequal_items);
        btnNoCopy.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                refreshTableFilter();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
            }
        });

        btnCopyLeft = WidgetFactory.createToggleButton(filterOptionsGroup, SWT.FLAT);
        btnCopyLeft.setImage(ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_COPY_LEFT).createImage());
        btnCopyLeft.setToolTipText(Messages.Tooltip_display_copy_from_right_to_left);
        btnCopyLeft.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                refreshTableFilter();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
            }
        });

        Composite displayOccurences = new Composite(filterOptionsGroup, SWT.NONE);
        displayOccurences.setLayout(new GridLayout());

        btnDuplicates = WidgetFactory.createToggleButton(displayOccurences);
        btnDuplicates.setLayoutData(createButtonLayoutData());
        btnDuplicates.setText(Messages.Duplicates);
        btnDuplicates.setToolTipText(Messages.Tooltip_display_duplicates);
        btnDuplicates.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                refreshTableFilter();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
            }
        });

        btnSingles = WidgetFactory.createToggleButton(displayOccurences);
        btnSingles.setLayoutData(createButtonLayoutData());
        btnSingles.setText(Messages.Singles);
        btnSingles.setToolTipText(Messages.Tooltip_display_singles);
        btnSingles.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                refreshTableFilter();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
            }
        });
    }

    private void createCompareArea(Composite parent) {

        Composite compareArea = new Composite(parent, SWT.NONE);
        compareArea.setLayout(new GridLayout(1, false));
        compareArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        tableViewer = new TableViewer(compareArea, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        Table table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        /* First column is always RIGHT aligned, see bug 151342 */
        // TableViewerColumn tableViewerColumnDummy = new
        // TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblClmnDummy = new TableColumn(tableViewer.getTable(), SWT.NONE);
        tblClmnDummy.setWidth(Size.getSize(0));

        // TableViewerColumn tableViewerColumnLeftMsgId = new
        // TableViewerColumn(tableViewer, SWT.LEFT);
        TableColumn tblClmnLeftMessageId = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblClmnLeftMessageId.setText(Messages.ID);
        tblClmnLeftMessageId.setWidth(Size.getSize(60));

        // TableViewerColumn tableViewerColumnLeftMsgText = new
        // TableViewerColumn(tableViewer, SWT.LEFT);
        TableColumn tblClmnLeftMessageText = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblClmnLeftMessageText.setText(Messages.Message_text);
        tblClmnLeftMessageText.setWidth(Size.getSize(200));

        // TableViewerColumn tableViewerColumnSyncAction = new
        // TableViewerColumn(tableViewer, SWT.CENTER);
        TableColumn tblClmnCompareResult = new TableColumn(tableViewer.getTable(), SWT.CENTER);
        tblClmnCompareResult.setResizable(false);
        tblClmnCompareResult.setWidth(Size.getSize(16));

        // TableViewerColumn tableViewerColumnRightMsgId = new
        // TableViewerColumn(tableViewer, SWT.LEFT);
        TableColumn tblclmnRightMessageId = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblclmnRightMessageId.setText(Messages.ID);
        tblclmnRightMessageId.setWidth(tblClmnLeftMessageId.getWidth());

        // TableViewerColumn tableViewerColumnRightMsgText = new
        // TableViewerColumn(tableViewer, SWT.LEFT);
        TableColumn tblClmnRightMessageText = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblClmnRightMessageText.setText(Messages.Message_text);
        tblClmnRightMessageText.setWidth(tblClmnLeftMessageText.getWidth());

        tableViewer.addDoubleClickListener(new IDoubleClickListener() {

            public void doubleClick(DoubleClickEvent event) {
                ISelection selection = event.getSelection();
                if (selection instanceof StructuredSelection) {
                    StructuredSelection structuredSelection = (StructuredSelection)selection;
                    for (Iterator<?> iterator = structuredSelection.iterator(); iterator.hasNext();) {
                        Object item = (Object)iterator.next();
                        if (item instanceof MessageFileCompareItem) {
                            MessageFileCompareItem compareItem = (MessageFileCompareItem)item;
                            performCompareMessageDescriptions(compareItem);
                        }
                    }
                }
            }
        });

        TableStatistics tableStatistics = new TableStatistics();
        TableContentProvider tableContentProvider = new TableContentProvider(tableStatistics);
        TableFilter tableFilter = new TableFilter(tableContentProvider);

        tableViewer.setContentProvider(new TableContentProvider(tableStatistics));
        tableViewer.addFilter(tableFilter);
        tableViewer.setLabelProvider(new TableLabelProvider());
        Menu menuTableViewerContextMenu = new Menu(tableViewer.getTable());
        menuTableViewerContextMenu.addMenuListener(new TableContextMenu(menuTableViewerContextMenu));
        tableViewer.getTable().setMenu(menuTableViewerContextMenu);
    }

    private void createrFooterArea(Composite parent) {

        Composite footerArea = new Composite(parent, SWT.NONE);
        footerArea.setLayout(new GridLayout(1, false));
        footerArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        StatusBar statusBar = new StatusBar(footerArea, false);
        statusInfo = statusBar.createStatusBarLabel(""); //$NON-NLS-1$
        statusBarFilterImage = statusBar.createStatusBarImage(ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_FILTERED_ITEMS).createImage());
        statusBarFilterText = statusBar.createStatusBarLabel("", 50, SWT.CENTER); //$NON-NLS-1$

    }

    private GridData createButtonLayoutData() {

        GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
        gridData.widthHint = 120;

        return gridData;
    }

    @Override
    public void init(IEditorSite site, IEditorInput input) {
        setSite(site);
        setInput(input);
        setPartName(input.getName());
        setTitleImage(((MessageFileCompareEditorInput)input).getTitleImage());
        this.input = (MessageFileCompareEditorInput)input;
    }

    @Override
    public void setFocus() {
    }

    protected Shell getShell() {
        return shell;
    }

    public MessageFileCompareEditorInput getEditorInput() {

        IEditorInput input = super.getEditorInput();
        if (input instanceof MessageFileCompareEditorInput) {
            return (MessageFileCompareEditorInput)input;
        }

        return null;
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    @Override
    public boolean isSaveOnCloseNeeded() {
        return false;
    }

    public static void openEditor(RemoteObject leftMessageFile, RemoteObject rightMessageFile, String mode) {

        try {

            MessageFileCompareEditorInput editorInput = new MessageFileCompareEditorInput(leftMessageFile, rightMessageFile, mode);
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, AbstractMessageFileCompareEditor.ID);

        } catch (PartInitException e) {
        }
    }

    private void refreshAndCheckMessageFileNames() {

        MessageFileCompareEditorInput editorInput = getEditorInput();
        if (editorInput != null) {
            lblLeftMessageFile.setText(input.getLeftMessageFileName());
            lblRightMessageFile.setText(input.getRightMessageFileName());
        } else {
            lblLeftMessageFile.setText(""); //$NON-NLS-1$
            lblRightMessageFile.setText(""); //$NON-NLS-1$
        }

        headerArea.layout(true);

        setButtonEnablement();
    }

    private void refreshTableFilter() {

        if (tableViewer != null) {

            if (tableFilter != null) {
                tableViewer.removeFilter(tableFilter);
            }

            if (filterData != null) {

                filterData.setCopyLeft(btnCopyLeft.getSelection());
                filterData.setCopyRight(btnCopyRight.getSelection());
                filterData.setEqual(btnEqual.getSelection());
                filterData.setNoCopy(btnNoCopy.getSelection());
                filterData.setSingles(btnSingles.getSelection());
                filterData.setDuplicates(btnDuplicates.getSelection());

                TableContentProvider contentProvider = (TableContentProvider)tableViewer.getContentProvider();
                if (tableFilter == null) {
                    tableFilter = new TableFilter(contentProvider);
                }
                contentProvider.getTableStatistics().clearStatistics();

                tableFilter.setFilterData(filterData);
                tableViewer.addFilter(tableFilter);
            }

            setButtonEnablement();

            storeScreenValues();
        }
    }

    private void setButtonEnablement() {

        boolean isCompareEnabled = true;
        boolean isSynchronizeEnabled = true;

        if (input.getLeftMessageFile() != null) {
            String connectionName = input.getLeftMessageFile().getConnectionName();
            AS400 system = IBMiHostContributionsHandler.getSystem(connectionName);
            if (isLeftMessageFileWarning || !ISphereHelper.checkISphereLibrary(getShell(), system)) {
                isCompareEnabled = false;
                isSynchronizeEnabled = false;
                isLeftMessageFileWarning = true;
            }
        }

        if (input.getRightMessageFile() != null) {
            String connectionName = input.getRightMessageFile().getConnectionName();
            AS400 system = IBMiHostContributionsHandler.getSystem(connectionName);
            if (isRightMessageFileWarning || !ISphereHelper.checkISphereLibrary(getShell(), system)) {
                isCompareEnabled = false;
                isSynchronizeEnabled = false;
                isRightMessageFileWarning = true;
            }
        }

        if (input.getLeftMessageFile() == null || input.getRightMessageFile() == null) {
            isCompareEnabled = false;
        }

        if (tableViewer.getTable().getItems().length <= 0) {
            isSynchronizeEnabled = false;
        }

        btnCompare.setEnabled(isCompareEnabled);
        btnSynchronize.setEnabled(isSynchronizeEnabled);

        if (isWorking()) {
            headerArea.setEnabled(false);
            optionsArea.setEnabled(false);
        } else {
            headerArea.setEnabled(true);
            optionsArea.setEnabled(true);
        }
        headerArea.update();
        optionsArea.update();

        displayCompareStatus();
    }

    private boolean isWorking() {
        return isComparing || isSynchronizing;
    }

    private void displayCompareStatus() {

        if (statusInfo != null) {
            if (isWorking()) {
                statusInfo.setText(Messages.Working);
            } else if (selectionChanged) {
                if (StringHelper.isNullOrEmpty(lblLeftMessageFile.getText()) || StringHelper.isNullOrEmpty(lblRightMessageFile.getText())) {
                    statusInfo.setText(Messages.Please_select_the_missing_message_file_then_press_Compare_to_start);
                } else {
                    statusInfo.setText(Messages.Please_press_Compare_to_start);
                }
                statusBarFilterText.setText("");//$NON-NLS-1$
            } else {
                TableStatistics tableStatistics = ((TableContentProvider)tableViewer.getContentProvider()).getTableStatistics();
                statusInfo.setText(tableStatistics.toString());
                statusBarFilterText.setText(Integer.toString(tableStatistics.getFilteredElements()));
            }
        }

    }

    /**
     * Restores the screen values of the last search search.
     */
    private void loadScreenValues() {

        btnCopyLeft.setSelection(dialogSettingsManager.loadBooleanValue(BUTTON_COPY_LEFT, true));
        btnCopyRight.setSelection(dialogSettingsManager.loadBooleanValue(BUTTON_COPY_RIGHT, true));
        btnEqual.setSelection(dialogSettingsManager.loadBooleanValue(BUTTON_EQUAL, true));
        btnNoCopy.setSelection(dialogSettingsManager.loadBooleanValue(BUTTON_NO_COPY, true));
        btnSingles.setSelection(dialogSettingsManager.loadBooleanValue(BUTTON_SINGLES, true));
        btnDuplicates.setSelection(dialogSettingsManager.loadBooleanValue(BUTTON_DUPLICATES, true));
    }

    /**
     * Stores the screen values that are preserved for the next search.
     */
    private void storeScreenValues() {

        dialogSettingsManager.storeValue(BUTTON_COPY_LEFT, btnCopyLeft.getSelection());
        dialogSettingsManager.storeValue(BUTTON_COPY_RIGHT, btnCopyRight.getSelection());
        dialogSettingsManager.storeValue(BUTTON_EQUAL, btnEqual.getSelection());
        dialogSettingsManager.storeValue(BUTTON_NO_COPY, btnNoCopy.getSelection());
        dialogSettingsManager.storeValue(BUTTON_SINGLES, btnSingles.getSelection());
        dialogSettingsManager.storeValue(BUTTON_DUPLICATES, btnDuplicates.getSelection());
    }

    /**
     * Returns the dialog settings if this editor.
     * 
     * @return settings the dialog settings used to store the dialog's location
     *         and/or size, or null if the dialog's bounds should never be
     *         stored.
     */
    protected IDialogSettings getDialogSettings() {

        IDialogSettings workbenchSettings = ISpherePlugin.getDefault().getDialogSettings();
        if (workbenchSettings == null) {
            throw new IllegalArgumentException("Parameter 'workbenchSettings' must not be null."); //$NON-NLS-1$
        }

        String sectionName = getClass().getName();
        IDialogSettings dialogSettings = workbenchSettings.getSection(sectionName);
        if (dialogSettings == null) {
            dialogSettings = workbenchSettings.addNewSection(sectionName);
        }

        return dialogSettings;
    }

    private MessageFileCompareItem[] getSelectedItems() {

        List<MessageFileCompareItem> selectedItems = new ArrayList<MessageFileCompareItem>();

        if (tableViewer.getSelection() instanceof StructuredSelection) {
            StructuredSelection selection = (StructuredSelection)tableViewer.getSelection();
            for (Iterator<?> iterator = selection.iterator(); iterator.hasNext();) {
                Object selectedItem = (Object)iterator.next();
                if (selectedItem instanceof MessageFileCompareItem) {
                    MessageFileCompareItem compareItem = (MessageFileCompareItem)selectedItem;
                    selectedItems.add(compareItem);
                }
            }
        }

        return selectedItems.toArray(new MessageFileCompareItem[selectedItems.size()]);
    }

    private void setCompareStatus(int newStatus) {

        MessageFileCompareItem[] selectedItems = getSelectedItems();

        for (MessageFileCompareItem compareItem : selectedItems) {
            compareItem.setCompareStatus(newStatus);
        }

        tableViewer.refresh();
    }

    private void performCompareMessageFiles() {

        isComparing = true;
        setButtonEnablement();

        final MessageFileCompareEditorInput editorInput = getEditorInput();
        if (editorInput.getLeftMessageFileName().equals(editorInput.getRightMessageFileName())) {
            MessageDialog.openWarning(getShell(), Messages.Warning, Messages.Warning_Both_sides_show_the_same_message_file);
        }

        Job job = new Job(Messages.Loading_message_descriptions) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {

                try {

                    monitor.beginTask("", 2);

                    UIJob job = new UIJob("") {
                        @Override
                        public IStatus runInUIThread(IProgressMonitor monitor) {
                            tableViewer.getTable().clearAll();
                            return Status.OK_STATUS;
                        }
                    };
                    job.schedule();

                    leftMessageDescriptions = getMessageDescriptions(editorInput.getLeftMessageFile());
                    monitor.worked(1);
                    
                    rightMessageDescriptions = getMessageDescriptions(editorInput.getRightMessageFile());
                    monitor.worked(2);

                    job = new UIJob("") {
                        @Override
                        public IStatus runInUIThread(IProgressMonitor monitor) {
                            tableViewer.setInput(getEditorInput());
                            selectionChanged = false;
                            setButtonEnablement();
                            return Status.OK_STATUS;
                        }
                    };
                    job.schedule();

                } finally {
                    monitor.done();

                    UIJob job = new UIJob("") {
                        @Override
                        public IStatus runInUIThread(IProgressMonitor monitor) {
                            isComparing = false;
                            setButtonEnablement();
                            return Status.OK_STATUS;
                        }
                    };
                    job.schedule();
                }

                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    private MessageDescription[] getMessageDescriptions(RemoteObject messageFile) {

        String connectionName = messageFile.getConnectionName();
        AS400 system = IBMiHostContributionsHandler.getSystem(connectionName);

        IQMHRTVM iqmhrtvm = new IQMHRTVM(system, connectionName);
        iqmhrtvm.setMessageFile(messageFile.getName(), messageFile.getLibrary());

        return iqmhrtvm.retrieveAllMessageDescriptions();
    }

    private void performSynchronizeMessageFiles() {

        isSynchronizing = true;
        setButtonEnablement();

        RemoteObject leftMessageFile = getEditorInput().getLeftMessageFile();
        RemoteObject rightMessageFile = getEditorInput().getRightMessageFile();

        for (int i = 0; i < tableViewer.getTable().getItemCount(); i++) {
            MessageFileCompareItem compareItem = (MessageFileCompareItem)tableViewer.getElementAt(i);

            if (compareItem.getCompareStatus() == MessageFileCompareItem.LEFT_MISSING) {
                performCopyToLeft(compareItem, leftMessageFile);
            } else if (compareItem.getCompareStatus() == MessageFileCompareItem.RIGHT_MISSING) {
                performCopyToRight(compareItem, rightMessageFile);
            }
        }

        performCompareMessageFiles();

        isSynchronizing = false;
        setButtonEnablement();
    }

    private void performCopyToLeft(MessageFileCompareItem compareItem, RemoteObject toMessageFile) {

        try {

            if (MessageDescriptionHelper.mergeMessageDescription(getShell(), compareItem.getRightMessageDescription(),
                toMessageFile.getConnectionName(), toMessageFile.getName(), toMessageFile.getLibrary()) == null) {
                compareItem.setLeftMessageDescription(MessageDescriptionHelper.retrieveMessageDescription(toMessageFile.getConnectionName(),
                    toMessageFile.getName(), toMessageFile.getLibrary(), compareItem.getMessageId()));
                compareItem.clearCompareStatus();
            }

            tableViewer.refresh(compareItem);

        } catch (Exception e) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, e.getLocalizedMessage());
        }
    }

    private void performCopyToRight(MessageFileCompareItem compareItem, RemoteObject toMessageFile) {

        try {

            if (MessageDescriptionHelper.mergeMessageDescription(getShell(), compareItem.getLeftMessageDescription(),
                toMessageFile.getConnectionName(), toMessageFile.getName(), toMessageFile.getLibrary()) == null) {
                compareItem.setRightMessageDescription(MessageDescriptionHelper.retrieveMessageDescription(toMessageFile.getConnectionName(),
                    toMessageFile.getName(), toMessageFile.getLibrary(), compareItem.getMessageId()));
                compareItem.clearCompareStatus();
            }

            tableViewer.refresh(compareItem);

        } catch (Exception e) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, e.getLocalizedMessage());
        }
    }

    protected void performCompareMessageDescriptions(MessageFileCompareItem compareItem) {

        if (compareItem.isSingle()) {
            return;
        }

        CompareConfiguration cc = new CompareConfiguration();
        cc.setLeftEditable(false);
        cc.setRightEditable(false);
        cc.setLeftLabel(Messages.Left_message_description);
        cc.setRightLabel(Messages.Right_message_description);

        MessageDescription leftMessageDescription = compareItem.getLeftMessageDescription();
        MessageDescription rightMessageDescription = compareItem.getRightMessageDescription();
        biz.isphere.core.messagefilecompare.compare.MessageDescriptionCompareEditorInput fInput = new biz.isphere.core.messagefilecompare.compare.MessageDescriptionCompareEditorInput(
            cc, leftMessageDescription, rightMessageDescription);

        // CompareUI.openCompareEditorOnPage(fInput,
        // ISpherePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage());
        CompareUI.openCompareDialog(fInput);
    }

    protected abstract RemoteObject performSelectRemoteObject(String connectionName);

    /**
     * Class to provide the content of the table viewer.
     */
    private class TableContentProvider implements IStructuredContentProvider {

        private TableStatistics tableStatistics;
        private MessageFileCompareEditorInput editorInput;
        private Map<String, MessageFileCompareItem> compareItems;

        public TableContentProvider(TableStatistics tableStatistics) {

            this.tableStatistics = tableStatistics;
            this.editorInput = null;
            this.compareItems = null;
        }

        public TableStatistics getTableStatistics() {
            return tableStatistics;
        }

        public Object[] getElements(Object inputElement) {

            if (editorInput != null && compareItems == null) {

                tableStatistics.clearStatistics();

                compareItems = new LinkedHashMap<String, MessageFileCompareItem>();

                for (MessageDescription leftMessageDescription : leftMessageDescriptions) {
                    compareItems.put(leftMessageDescription.getMessageId(), new MessageFileCompareItem(leftMessageDescription, null));
                }

                for (MessageDescription rightMessageDescription : rightMessageDescriptions) {
                    MessageFileCompareItem item = compareItems.get(rightMessageDescription.getMessageId());
                    if (item == null) {
                        compareItems.put(rightMessageDescription.getMessageId(), new MessageFileCompareItem(null, rightMessageDescription));
                    } else {
                        item.setRightMessageDescription(rightMessageDescription);
                    }
                }
            }

            MessageFileCompareItem[] compareItemsArray = compareItems.values().toArray(new MessageFileCompareItem[compareItems.size()]);
            Arrays.sort(compareItemsArray);

            return compareItemsArray;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

            editorInput = (MessageFileCompareEditorInput)newInput;
            compareItems = null;
            tableStatistics.clearStatistics();
        }
    }

    /**
     * Class the provides the content for the cells of the table.
     */
    private class TableLabelProvider extends LabelProvider implements ITableLabelProvider {

        private static final int COLUMN_DUMMY = 0;
        private static final int COLUMN_LEFT_MESSAGE_ID = 1;
        private static final int COLUMN_LEFT_MESSAGE_TEXT = 2;
        private static final int COLUMN_COMPARE_RESULT = 3;
        private static final int COLUMN_RIGHT_MESSAGE_ID = 4;
        private static final int COLUMN_RIGHT_MESSAGE_TEXT = 5;

        private Image copyToLeft;
        private Image copyToRight;
        private Image copyNotEqual;
        private Image copyEqual;

        public TableLabelProvider() {

            this.copyToLeft = ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_COPY_LEFT).createImage();
            this.copyToRight = ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_COPY_RIGHT).createImage();
            this.copyNotEqual = ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_COPY_NOT_EQUAL).createImage();
            this.copyEqual = ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_COPY_EQUAL).createImage();
        }

        public Image getColumnImage(Object element, int columnIndex) {

            if (columnIndex != COLUMN_COMPARE_RESULT) {
                return null;
            }

            if (!(element instanceof MessageFileCompareItem)) {
                return null;
            }

            MessageFileCompareItem compareItem = (MessageFileCompareItem)element;
            int compareStatus = compareItem.getCompareStatus();

            if (compareStatus == MessageFileCompareItem.RIGHT_MISSING) {
                return copyToRight;
            } else if (compareStatus == MessageFileCompareItem.LEFT_MISSING) {
                return copyToLeft;
            } else if (compareStatus == MessageFileCompareItem.LEFT_EQUALS_RIGHT) {
                return copyEqual;
            } else if (compareStatus == MessageFileCompareItem.NOT_EQUAL) {
                return copyNotEqual;
            }
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {

            if (columnIndex == COLUMN_COMPARE_RESULT) {
                return null;
            }

            if (!(element instanceof MessageFileCompareItem)) {
                return ""; //$NON-NLS-1$
            }

            MessageFileCompareItem compareItem = (MessageFileCompareItem)element;

            switch (columnIndex) {
            case COLUMN_DUMMY:
                return ""; //$NON-NLS-1$

            case COLUMN_LEFT_MESSAGE_ID:
                if (compareItem.getLeftMessageDescription() != null) {
                    return compareItem.getLeftMessageDescription().getMessageId();
                } else {
                    return ""; //$NON-NLS-1$
                }

            case COLUMN_LEFT_MESSAGE_TEXT:
                if (compareItem.getLeftMessageDescription() != null) {
                    return compareItem.getLeftMessageDescription().getMessage();
                } else {
                    return ""; //$NON-NLS-1$
                }

            case COLUMN_RIGHT_MESSAGE_ID:
                if (compareItem.getRightMessageDescription() != null) {
                    return compareItem.getRightMessageDescription().getMessageId();
                } else {
                    return ""; //$NON-NLS-1$
                }

            case COLUMN_RIGHT_MESSAGE_TEXT:
                if (compareItem.getRightMessageDescription() != null) {
                    return compareItem.getRightMessageDescription().getMessage();
                } else {
                    return ""; //$NON-NLS-1$
                }

            default:
                return ""; //$NON-NLS-1$
            }
        }

        @Override
        public void dispose() {

            copyToLeft.dispose();
            copyToRight.dispose();
            copyNotEqual.dispose();
            copyEqual.dispose();

            super.dispose();
        }
    }

    private class TableStatistics {

        private static final String SLASH = "/"; //$NON-NLS-1$
        private static final String SPACE = " "; //$NON-NLS-1$

        private int elements;
        private int elementsSelected;
        private int identicalElements;
        private int identicalElementsSelected;
        private int differentElements;
        private int differentElementsSelected;
        private int uniqueElementsLeft;
        private int uniqueElementsLeftSelected;
        private int uniqueElementsRight;
        private int uniqueElementsRightSelected;

        public void clearStatistics() {

            this.elements = 0;
            this.elementsSelected = 0;

            this.identicalElements = 0;
            this.identicalElementsSelected = 0;

            this.differentElements = 0;
            this.differentElementsSelected = 0;

            this.uniqueElementsLeft = 0;
            this.uniqueElementsLeftSelected = 0;

            this.uniqueElementsRight = 0;
            this.uniqueElementsRightSelected = 0;
        }

        public void countElements() {
            elements++;
        }

        public void countElementsSelected() {
            elementsSelected++;
        }

        public void countIdenticalElements() {
            identicalElements++;
        }

        public void countIdenticalElementsSelected() {
            identicalElementsSelected++;
        }

        public void countDifferentElements() {
            differentElements++;
        }

        public void countDifferentElementsSelected() {
            differentElementsSelected++;
        }

        public void countUniqueElementsLeft() {
            uniqueElementsLeft++;
        }

        public void countUniqueElementsLeftSelected() {
            uniqueElementsLeftSelected++;
        }

        public void countUniqueElementsRight() {
            uniqueElementsRight++;
        }

        public void countUniqueElementsRightSelected() {
            uniqueElementsRightSelected++;
        }

        public int getFilteredElements() {
            return elements - elementsSelected;
        }

        @Override
        public String toString() {

            StringBuilder statusMessage = new StringBuilder();

            statusMessage.append(Messages.Items_found_colon + SPACE + elementsSelected); //$NON-NLS-1$
            if (elementsSelected != elements) {
                statusMessage.append(SLASH + elements);
            }

            statusMessage.append(" ("); //$NON-NLS-1$
            statusMessage.append(Messages.Identical_colon + SPACE + identicalElementsSelected);
            if (identicalElementsSelected != identicalElements) {
                statusMessage.append(SLASH + identicalElements);
            }

            statusMessage.append(", " + Messages.Different_colon + SPACE + differentElementsSelected); //$NON-NLS-1$
            if (differentElementsSelected != differentElements) {
                statusMessage.append(SLASH + differentElements);
            }

            statusMessage.append(", " + Messages.Unique_left_colon + SPACE + uniqueElementsLeftSelected); //$NON-NLS-1$
            if (uniqueElementsLeftSelected != uniqueElementsLeft) {
                statusMessage.append(SLASH + uniqueElementsLeft);
            }

            statusMessage.append(", " + Messages.Unique_right_colon + SPACE + uniqueElementsRightSelected); //$NON-NLS-1$
            if (uniqueElementsRightSelected != uniqueElementsRight) {
                statusMessage.append(SLASH + uniqueElementsRight);
            }

            statusMessage.append(")"); //$NON-NLS-1$

            return statusMessage.toString();
        }
    }

    /**
     * Class to filter the content of the table according to the selection
     * settings that can be changed with the buttons above the table.
     */
    private class TableFilter extends ViewerFilter {

        private TableFilterData filterData;
        private TableStatistics tableStatistics;

        public TableFilter(TableContentProvider tableContentProvider) {
            this.tableStatistics = tableContentProvider.getTableStatistics();
        }

        public void setFilterData(TableFilterData filterData) {
            this.filterData = filterData;
        }

        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element) {

            MessageFileCompareItem compareItem = (MessageFileCompareItem)element;
            int compareStatus = compareItem.getCompareStatus();

            countElements(compareItem);

            if (filterData == null) {
                return countSelectedElements(compareItem, true);
            }

            if (compareItem.isDuplicate() && !filterData.isDuplicates()) {
                return countSelectedElements(compareItem, false);
            }

            if (compareItem.isSingle() && !filterData.isSingles()) {
                return countSelectedElements(compareItem, false);
            }

            if (compareStatus == MessageFileCompareItem.NO_ACTION) {
                return countSelectedElements(compareItem, true);
            }

            if (compareStatus == MessageFileCompareItem.LEFT_MISSING && filterData.isCopyLeft()) {
                return countSelectedElements(compareItem, true);
            }

            if (compareStatus == MessageFileCompareItem.RIGHT_MISSING && filterData.isCopyRight()) {
                return countSelectedElements(compareItem, true);
            }

            if (compareStatus == MessageFileCompareItem.NOT_EQUAL && filterData.isCopyNotEqual()) {
                return countSelectedElements(compareItem, true);
            }

            if (compareStatus == MessageFileCompareItem.LEFT_EQUALS_RIGHT && filterData.equal) {
                return countSelectedElements(compareItem, true);
            }

            return countSelectedElements(compareItem, false);
        }

        private void countElements(MessageFileCompareItem compareItem) {

            int compareStatus = compareItem.getCompareStatus();

            tableStatistics.countElements();

            if (compareStatus == MessageFileCompareItem.LEFT_EQUALS_RIGHT) {
                tableStatistics.countIdenticalElements();
            } else if (compareStatus == MessageFileCompareItem.NOT_EQUAL) {
                tableStatistics.countDifferentElements();
            }

            if (compareItem.isSingle()) {
                if (compareItem.getLeftMessageDescription() != null) {
                    tableStatistics.countUniqueElementsLeft();
                } else {
                    tableStatistics.countUniqueElementsRight();
                }
            }
        }

        private boolean countSelectedElements(MessageFileCompareItem compareItem, boolean isSelected) {

            if (!isSelected) {
                return false;
            }

            int compareStatus = compareItem.getCompareStatus();

            tableStatistics.countElementsSelected();

            if (compareStatus == MessageFileCompareItem.LEFT_EQUALS_RIGHT) {
                tableStatistics.countIdenticalElementsSelected();
            } else if (compareStatus == MessageFileCompareItem.NOT_EQUAL) {
                tableStatistics.countDifferentElementsSelected();
            }

            if (compareItem.isSingle()) {
                if (compareItem.getLeftMessageDescription() != null) {
                    tableStatistics.countUniqueElementsLeftSelected();
                } else {
                    tableStatistics.countUniqueElementsRightSelected();
                }
            }

            return true;
        }
    }

    /**
     * Class that provides the selection settings for the table filter.
     */
    private class TableFilterData {

        private boolean copyRight;
        private boolean copyLeft;
        private boolean noCopy;
        private boolean equal;
        private boolean singles;
        private boolean duplicates;

        public boolean isCopyRight() {
            return copyRight;
        }

        public void setCopyRight(boolean copyRight) {
            this.copyRight = copyRight;
        }

        public boolean isCopyLeft() {
            return copyLeft;
        }

        public void setCopyLeft(boolean copyLeft) {
            this.copyLeft = copyLeft;
        }

        public boolean isCopyNotEqual() {
            return noCopy;
        }

        public void setNoCopy(boolean copyNotEqual) {
            this.noCopy = copyNotEqual;
        }

        public boolean isEqual() {
            return equal;
        }

        public void setEqual(boolean copyEqual) {
            this.equal = copyEqual;
        }

        public boolean isSingles() {
            return singles;
        }

        public void setSingles(boolean singles) {
            this.singles = singles;
        }

        public boolean isDuplicates() {
            return duplicates;
        }

        public void setDuplicates(boolean duplicates) {
            this.duplicates = duplicates;
        }
    }

    /**
     * Class that implements the context menu for the table rows.
     */
    private class TableContextMenu extends MenuAdapter {

        private static final int LEFT = 1;
        private static final int RIGHT = 2;

        private Menu parent;
        private MenuItem menuItemRemoveSelection;
        private MenuItem menuItemSelectForCopyingToTheRight;
        private MenuItem menuItemSelectForCopyingToTheLeft;
        private MenuItem menuItemEditLeft;
        private MenuItem menuItemEditRight;
        private MenuItem menuItemCompareLeftAndRight;
        private MenuItem menuItemSeparator;
        private MenuItem menuItemDeleteLeft;
        private MenuItem menuItemDeleteRight;

        public TableContextMenu(Menu parent) {
            this.parent = parent;
        }

        @Override
        public void menuShown(MenuEvent event) {
            destroyMenuItems();
            createMenuItems();
        }

        private void destroyMenuItems() {
            if (!((menuItemRemoveSelection == null) || (menuItemRemoveSelection.isDisposed()))) {
                menuItemRemoveSelection.dispose();
            }
            if (!((menuItemSelectForCopyingToTheRight == null) || (menuItemSelectForCopyingToTheRight.isDisposed()))) {
                menuItemSelectForCopyingToTheRight.dispose();
            }
            if (!((menuItemSelectForCopyingToTheLeft == null) || (menuItemSelectForCopyingToTheLeft.isDisposed()))) {
                menuItemSelectForCopyingToTheLeft.dispose();
            }
            if (!((menuItemEditLeft == null) || (menuItemEditLeft.isDisposed()))) {
                menuItemEditLeft.dispose();
            }
            if (!((menuItemEditRight == null) || (menuItemEditRight.isDisposed()))) {
                menuItemEditRight.dispose();
            }
            if (!((menuItemCompareLeftAndRight == null) || (menuItemCompareLeftAndRight.isDisposed()))) {
                menuItemCompareLeftAndRight.dispose();
            }
            if (!((menuItemSeparator == null) || (menuItemSeparator.isDisposed()))) {
                menuItemSeparator.dispose();
            }
            if (!((menuItemDeleteLeft == null) || (menuItemDeleteLeft.isDisposed()))) {
                menuItemDeleteLeft.dispose();
            }
            if (!((menuItemDeleteRight == null) || (menuItemDeleteRight.isDisposed()))) {
                menuItemDeleteRight.dispose();
            }
        }

        private void createMenuItems() {

            if (tableViewer.getTable().getItems().length <= 0) {
                return;
            }

            createMenuItemRemoveSelection();
            createMenuItemSelectForCopyingToTheLeft(getTheSelectedItem());
            createMenuItemSelectForCopyingToTheRight(getTheSelectedItem());
            createMenuItemEditLeft(getTheSelectedItem());
            createMenuItemEditRight(getTheSelectedItem());
            createMenuItemCompareLeftAndRight(getTheSelectedItem());
            createMenuItemSeparator();
            createMenuItemDeleteLeft(getTheSelectedItem());
            createMenuItemDeleteRight(getTheSelectedItem());
        }

        private void createMenuItemSeparator() {
            menuItemSeparator = new MenuItem(parent, SWT.SEPARATOR);
        }

        private void createMenuItemRemoveSelection() {
            menuItemRemoveSelection = new MenuItem(parent, SWT.NONE);
            menuItemRemoveSelection.setText(Messages.Remove_selection);
            menuItemRemoveSelection.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    setCompareStatus(MessageFileCompareItem.NO_ACTION);
                }
            });
        }

        private void createMenuItemSelectForCopyingToTheLeft(MessageFileCompareItem compareItem) {
            menuItemSelectForCopyingToTheLeft = new MenuItem(parent, SWT.NONE);
            menuItemSelectForCopyingToTheLeft.setText(Messages.Select_for_copying_right_to_left);
            menuItemSelectForCopyingToTheLeft.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    setCompareStatus(MessageFileCompareItem.LEFT_MISSING);
                }
            });

            if (compareItem != null && compareItem.getRightMessageDescription() == null) {
                menuItemSelectForCopyingToTheLeft.setEnabled(false);
            }
        }

        private void createMenuItemSelectForCopyingToTheRight(MessageFileCompareItem compareItem) {
            menuItemSelectForCopyingToTheRight = new MenuItem(parent, SWT.NONE);
            menuItemSelectForCopyingToTheRight.setText(Messages.Select_for_copying_left_to_right);
            menuItemSelectForCopyingToTheRight.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    setCompareStatus(MessageFileCompareItem.RIGHT_MISSING);
                }
            });

            if (compareItem != null && compareItem.getLeftMessageDescription() == null) {
                menuItemSelectForCopyingToTheRight.setEnabled(false);
            }
        }

        private void createMenuItemEditLeft(MessageFileCompareItem compareItem) {
            menuItemEditLeft = new MenuItem(parent, SWT.NONE);
            menuItemEditLeft.setText(Messages.Edit_left);
            menuItemEditLeft.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    performEditMessageDescriptions(LEFT);
                }
            });

            if (compareItem != null && compareItem.getLeftMessageDescription() == null) {
                menuItemEditLeft.setEnabled(false);
            }
        }

        private void createMenuItemEditRight(MessageFileCompareItem compareItem) {
            menuItemEditRight = new MenuItem(parent, SWT.NONE);
            menuItemEditRight.setText(Messages.Edit_right);
            menuItemEditRight.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    performEditMessageDescriptions(RIGHT);
                }
            });

            if (compareItem != null && compareItem.getRightMessageDescription() == null) {
                menuItemEditRight.setEnabled(false);
            }
        }

        private void createMenuItemCompareLeftAndRight(MessageFileCompareItem compareItem) {
            menuItemCompareLeftAndRight = new MenuItem(parent, SWT.NONE);
            menuItemCompareLeftAndRight.setText(Messages.Compare_left_AND_right);
            menuItemCompareLeftAndRight.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    performCompareMessageDescriptions(getTheSelectedItem());
                }
            });

            if (compareItem == null || compareItem.getLeftMessageDescription() == null || compareItem.getRightMessageDescription() == null) {
                menuItemCompareLeftAndRight.setEnabled(false);
            }
        }

        private void createMenuItemDeleteLeft(MessageFileCompareItem compareItem) {
            menuItemDeleteLeft = new MenuItem(parent, SWT.NONE);
            menuItemDeleteLeft.setText(Messages.Delete_left);
            menuItemDeleteLeft.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    performDeleteMessageDescriptions(LEFT);
                }
            });

            if (compareItem != null && compareItem.getLeftMessageDescription() == null) {
                menuItemDeleteLeft.setEnabled(false);
            }
        }

        private void createMenuItemDeleteRight(MessageFileCompareItem compareItem) {
            menuItemDeleteRight = new MenuItem(parent, SWT.NONE);
            menuItemDeleteRight.setText(Messages.Delete_right);
            menuItemDeleteRight.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    performDeleteMessageDescriptions(RIGHT);
                }
            });

            if (compareItem != null && compareItem.getRightMessageDescription() == null) {
                menuItemDeleteRight.setEnabled(false);
            }
        }

        private MessageFileCompareItem getTheSelectedItem() {

            MessageFileCompareItem[] selectedItems = getSelectedItems();
            if (selectedItems.length == 1) {
                return selectedItems[0];
            }

            return null;
        }

        private void performEditMessageDescriptions(int side) {

            IStructuredSelection structuredSelection = (IStructuredSelection)tableViewer.getSelection();

            for (Iterator<?> iterator = structuredSelection.iterator(); iterator.hasNext();) {
                MessageFileCompareItem compareItem = (MessageFileCompareItem)iterator.next();

                MessageDescription messageDescription = null;
                if (side == LEFT) {
                    messageDescription = compareItem.getLeftMessageDescription();
                } else if (side == RIGHT) {
                    messageDescription = compareItem.getRightMessageDescription();
                } else {
                    throw new IllegalArgumentException("Invalid side value: " + side); //$NON-NLS-1$
                }

                AS400 as400 = IBMiHostContributionsHandler.getSystem(messageDescription.getConnection());
                MessageDescriptionDetailDialog messageDescriptionDetailDialog = new MessageDescriptionDetailDialog(getShell(), as400,
                    DialogActionTypes.getSubEditorActionType(IEditor.EDIT), messageDescription);
                if (messageDescriptionDetailDialog.open() == Dialog.OK) {
                    compareItem.clearCompareStatus();
                    tableViewer.update(compareItem, null);
                }
            }
        }

        private void performDeleteMessageDescriptions(int side) {

            IStructuredSelection structuredSelection = (IStructuredSelection)tableViewer.getSelection();

            for (Iterator<?> iterator = structuredSelection.iterator(); iterator.hasNext();) {
                MessageFileCompareItem compareItem = (MessageFileCompareItem)iterator.next();

                MessageDescription messageDescription = null;
                if (side == LEFT) {
                    messageDescription = compareItem.getLeftMessageDescription();
                } else if (side == RIGHT) {
                    messageDescription = compareItem.getRightMessageDescription();
                } else {
                    throw new IllegalArgumentException("Invalid side value: " + side); //$NON-NLS-1$
                }

                AS400 as400 = IBMiHostContributionsHandler.getSystem(messageDescription.getConnection());
                MessageDescriptionDetailDialog messageDescriptionDetailDialog = new MessageDescriptionDetailDialog(getShell(), as400,
                    DialogActionTypes.DELETE, messageDescription);
                if (messageDescriptionDetailDialog.open() == Dialog.OK) {

                    if (side == LEFT) {
                        compareItem.setLeftMessageDescription(null);
                    } else {
                        compareItem.setRightMessageDescription(null);
                    }

                    if (compareItem.getLeftMessageDescription() == null && compareItem.getRightMessageDescription() == null) {
                        tableViewer.remove(compareItem);
                    } else {
                        compareItem.clearCompareStatus();
                        tableViewer.update(compareItem, null);
                    }
                }
            }
        }
    }
}
