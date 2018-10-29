/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefilecompare.rse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
import biz.isphere.base.swt.events.TableAutoSizeControlListener;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.DialogActionTypes;
import biz.isphere.core.internal.IEditor;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.internal.MessageDescriptionHelper;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.core.internal.Size;
import biz.isphere.core.internal.api.retrievemessagedescription.IQMHRTVM;
import biz.isphere.core.messagefilecompare.MessageFileCompareEditorInput;
import biz.isphere.core.messagefilecompare.TableContentProvider;
import biz.isphere.core.messagefilecompare.TableFilter;
import biz.isphere.core.messagefilecompare.TableFilterData;
import biz.isphere.core.messagefilecompare.TableStatistics;
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
    private static final String BUTTON_COMPARE_AFTER_SYNC = "BUTTON_COMPARE_AFTER_SYNC"; //$NON-NLS-1$

    private MessageFileCompareEditorInput input;

    private boolean selectionChanged;
    private boolean isLeftMessageFileValid;
    private boolean isRightMessageFileValid;

    private TableViewer tableViewer;
    private TableFilter tableFilter;
    private TableFilterData filterData;

    private Button btnCompare;
    private Button btnSynchronize;
    private Button btnCancel;
    private Button chkCompareAfterSync;
    private IProgressMonitor jobToCancel;

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

    private boolean isComparing;
    private boolean isSynchronizing;

    private StatusLine statusLine;
    private String statusMessage;
    private int numFilteredItems;

    public AbstractMessageFileCompareEditor() {

        selectionChanged = true;
        isLeftMessageFileValid = false;
        isRightMessageFileValid = false;

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
        headerArea.setLayout(createGridLayoutNoBorder(2, true));
        headerArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Composite leftHeaderArea = new Composite(headerArea, SWT.NONE);
        leftHeaderArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        leftHeaderArea.setLayout(createGridLayoutNoBorder(2, false));

        lblLeftMessageFile = new Label(leftHeaderArea, SWT.BORDER);
        lblLeftMessageFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Button btnSelectLeftMessageFile = WidgetFactory.createPushButton(leftHeaderArea);
        btnSelectLeftMessageFile.setToolTipText(Messages.Tooltip_Select_object);
        btnSelectLeftMessageFile.setImage(ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_OPEN).createImage());
        btnSelectLeftMessageFile.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent arg0) {
                String connectionName = null;
                String libraryName = null;
                String messageFileName = null;
                if (getEditorInput().getLeftMessageFile() != null) {
                    connectionName = getEditorInput().getLeftMessageFile().getConnectionName();
                    libraryName = getEditorInput().getLeftMessageFile().getLibrary();
                    messageFileName = getEditorInput().getLeftMessageFile().getName();
                }
                RemoteObject messageFile = performSelectRemoteObject(connectionName, libraryName, messageFileName);
                if (messageFile != null) {
                    selectionChanged = true;
                    isLeftMessageFileValid = false;
                    getEditorInput().setLeftMessageFile(messageFile);
                    refreshAndCheckMessageFileNames();
                }
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        Composite rightHeaderArea = new Composite(headerArea, SWT.NONE);
        rightHeaderArea.setLayout(createGridLayoutNoBorder(2, false));
        rightHeaderArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        lblRightMessageFile = new Label(rightHeaderArea, SWT.BORDER);
        lblRightMessageFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Button btnSelectRightMessageFile = WidgetFactory.createPushButton(rightHeaderArea);
        btnSelectRightMessageFile.setToolTipText(Messages.Tooltip_Select_object);
        btnSelectRightMessageFile.setImage(ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_OPEN).createImage());
        btnSelectRightMessageFile.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent arg0) {
                String connectionName = null;
                String libraryName = null;
                String messageFileName = null;
                if (getEditorInput().getRightMessageFile() != null) {
                    connectionName = getEditorInput().getRightMessageFile().getConnectionName();
                    libraryName = getEditorInput().getRightMessageFile().getLibrary();
                    messageFileName = getEditorInput().getRightMessageFile().getName();
                }
                RemoteObject messageFile = performSelectRemoteObject(connectionName, libraryName, messageFileName);
                if (messageFile != null) {
                    selectionChanged = true;
                    isRightMessageFileValid = false;
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
        optionsArea.setLayout(createGridLayoutNoBorder(3, false));
        optionsArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        int verticalSpan = 3;
        btnCompare = WidgetFactory.createPushButton(optionsArea);
        btnCompare.setLayoutData(createButtonLayoutData(verticalSpan));
        btnCompare.setText(Messages.Compare);
        btnCompare.setToolTipText(Messages.Tooltip_start_compare);
        btnCompare.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent event) {
                performCompareMessageFiles();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
            }
        });

        createFilterOptionsArea(optionsArea, verticalSpan);

        btnSynchronize = WidgetFactory.createPushButton(optionsArea);
        btnSynchronize.setLayoutData(createButtonLayoutData(1, SWT.RIGHT));
        btnSynchronize.setText(Messages.Synchronize);
        btnSynchronize.setToolTipText(Messages.Tooltip_start_synchronize);
        btnSynchronize.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                performSynchronizeMessageFiles();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
            }
        });

        btnCancel = WidgetFactory.createPushButton(optionsArea);
        btnCancel.setLayoutData(createButtonLayoutData(1, SWT.RIGHT));
        btnCancel.setText(Messages.Cancel);
        btnCancel.setToolTipText(Messages.Tooltip_cancel_operation);
        btnCancel.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                performCancelOperation();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
            }
        });

        chkCompareAfterSync = WidgetFactory.createCheckbox(optionsArea);
        chkCompareAfterSync.setText(Messages.Compare_after_synchronization);
        chkCompareAfterSync.setToolTipText(Messages.Tooltip_Compare_after_synchronization);
        chkCompareAfterSync.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent paramSelectionEvent) {
                storeScreenValues();
            }

            public void widgetDefaultSelected(SelectionEvent paramSelectionEvent) {
            }
        });
    }

    private void createFilterOptionsArea(Composite parent, int verticalSpan) {

        Group filterOptionsGroup = new Group(parent, SWT.NONE);
        filterOptionsGroup.setLayout(createGridLayoutNoBorder(5, false));
        filterOptionsGroup.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, verticalSpan));
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
        tblClmnDummy.setResizable(true);
        tblClmnDummy.setWidth(Size.getSize(0));

        // TableViewerColumn tableViewerColumnLeftMsgId = new
        // TableViewerColumn(tableViewer, SWT.LEFT);
        final TableColumn tblClmnLeftMessageId = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblClmnLeftMessageId.setText(Messages.ID);
        tblClmnLeftMessageId.setResizable(true);
        tblClmnLeftMessageId.setWidth(Size.getSize(60));

        // TableViewerColumn tableViewerColumnLeftMsgText = new
        // TableViewerColumn(tableViewer, SWT.LEFT);
        final TableColumn tblClmnLeftMessageText = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblClmnLeftMessageText.setText(Messages.Message_text);
        tblClmnLeftMessageText.setWidth(Size.getSize(200));

        // TableViewerColumn tableViewerColumnSyncAction = new
        // TableViewerColumn(tableViewer, SWT.CENTER);
        final TableColumn tblClmnCompareResult = new TableColumn(tableViewer.getTable(), SWT.CENTER);
        tblClmnCompareResult.setResizable(true);
        tblClmnCompareResult.setWidth(Size.getSize(25));

        // TableViewerColumn tableViewerColumnRightMsgId = new
        // TableViewerColumn(tableViewer, SWT.LEFT);
        final TableColumn tblClmnRightMessageId = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblClmnRightMessageId.setText(Messages.ID);
        tblClmnRightMessageId.setResizable(tblClmnLeftMessageId.getResizable());
        tblClmnRightMessageId.setWidth(tblClmnLeftMessageId.getWidth());

        // TableViewerColumn tableViewerColumnRightMsgText = new
        // TableViewerColumn(tableViewer, SWT.LEFT);
        final TableColumn tblClmnRightMessageText = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblClmnRightMessageText.setText(Messages.Message_text);
        tblClmnRightMessageText.setResizable(tblClmnLeftMessageText.getResizable());
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
        TableFilter tableFilter = new TableFilter(tableStatistics);

        tableViewer.setContentProvider(new TableContentProvider(tableStatistics));
        tableViewer.addFilter(tableFilter);
        tableViewer.setLabelProvider(getTableLabelProvider(tableViewer, 3));
        Menu menuTableViewerContextMenu = new Menu(tableViewer.getTable());
        menuTableViewerContextMenu.addMenuListener(new TableContextMenu(menuTableViewerContextMenu));
        tableViewer.getTable().setMenu(menuTableViewerContextMenu);

        TableAutoSizeControlListener tableAutoSizeAdapter = new TableAutoSizeControlListener(tableViewer);
        tableAutoSizeAdapter.addResizableColumn(tblClmnLeftMessageText, 1);
        tableAutoSizeAdapter.addResizableColumn(tblClmnRightMessageText, 1);
        tableViewer.getTable().addControlListener(tableAutoSizeAdapter);
    }

    private void createrFooterArea(Composite parent) {

        Composite footerArea = new Composite(parent, SWT.NONE);
        footerArea.setLayout(new GridLayout(1, false));
        footerArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    }

    private GridLayout createGridLayoutNoBorder(int numColumns, boolean makeColumnsEqualWidth) {

        GridLayout layout = new GridLayout(numColumns, makeColumnsEqualWidth);
        layout.marginHeight = 0;
        layout.marginWidth = 0;

        return layout;
    }

    private GridData createButtonLayoutData() {
        return createButtonLayoutData(1);
    }

    private GridData createButtonLayoutData(int verticalSpan) {
        return createButtonLayoutData(verticalSpan, SWT.LEFT);
    }

    private GridData createButtonLayoutData(int verticalSpan, int horizontalAlignment) {

        GridData gridData = new GridData(horizontalAlignment, SWT.TOP, false, false, 1, 1);
        gridData.widthHint = 120;
        gridData.verticalSpan = verticalSpan;

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
        return;
    }

    @Override
    public void doSaveAs() {
        return;
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
        return true;
    }

    public static void openEditor(RemoteObject leftMessageFile, RemoteObject rightMessageFile, String mode) {

        try {

            MessageFileCompareEditorInput editorInput = new MessageFileCompareEditorInput(leftMessageFile, rightMessageFile);
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

        setButtonEnablementAndDisplayCompareStatus();
    }

    private void refreshTableFilter() {

        if (tableViewer != null) {

            if (tableFilter != null) {
                tableViewer.removeFilter(tableFilter);
                clearTableStatistics();
            }

            if (filterData != null) {

                filterData.setCopyLeft(btnCopyLeft.getSelection());
                filterData.setCopyRight(btnCopyRight.getSelection());
                filterData.setEqual(btnEqual.getSelection());
                filterData.setNoCopy(btnNoCopy.getSelection());
                filterData.setSingles(btnSingles.getSelection());
                filterData.setDuplicates(btnDuplicates.getSelection());

                if (tableFilter == null) {
                    tableFilter = new TableFilter(getTableStatistics());
                }

                clearTableStatistics();
                tableFilter.setFilterData(filterData);
                tableViewer.addFilter(tableFilter);
            }

            setButtonEnablementAndDisplayCompareStatus();

            storeScreenValues();
        }
    }

    private TableContentProvider getTableContentProvider() {

        return (TableContentProvider)tableViewer.getContentProvider();
    }

    private TableStatistics getTableStatistics() {

        return getTableContentProvider().getTableStatistics();
    }

    private void clearTableStatistics() {

        getTableStatistics().clearStatistics();
    }

    private synchronized void setButtonEnablementAndDisplayCompareStatus() {

        boolean isCompareEnabled = true;
        boolean isSynchronizeEnabled = true;

        if (input.getLeftMessageFile() != null && !isLeftMessageFileValid) {
            String connectionName = input.getLeftMessageFile().getConnectionName();
            if (!ISphereHelper.checkISphereLibrary(getShell(), connectionName)) {
                isCompareEnabled = false;
                isSynchronizeEnabled = false;
                isLeftMessageFileValid = false;
            } else {
                isLeftMessageFileValid = true;
            }
        }

        if (input.getRightMessageFile() != null && !isRightMessageFileValid) {
            String connectionName = input.getRightMessageFile().getConnectionName();
            if (!ISphereHelper.checkISphereLibrary(getShell(), connectionName)) {
                isCompareEnabled = false;
                isSynchronizeEnabled = false;
                isRightMessageFileValid = false;
            } else {
                isRightMessageFileValid = true;
            }
        }

        if (input.getLeftMessageFile() == null || input.getRightMessageFile() == null) {
            isCompareEnabled = false;
        }

        if (tableViewer.getTable().getItems().length <= 0) {
            isSynchronizeEnabled = false;
        }

        if (isWorking()) {
            setChildrenEnabled(headerArea, false);
            setChildrenEnabled(optionsArea, false);
            isCompareEnabled = false;
        } else {
            setChildrenEnabled(headerArea, true);
            setChildrenEnabled(optionsArea, true);
        }

        if (jobToCancel == null) {
            btnCancel.setEnabled(false);
        } else {
            btnCancel.setEnabled(true);
        }

        btnCompare.setEnabled(isCompareEnabled);
        btnSynchronize.setEnabled(isSynchronizeEnabled);

        displayCompareStatus();
    }

    private void setChildrenEnabled(Composite parent, boolean enabled) {
        for (Control control : parent.getChildren()) {
            if (control instanceof Button) {
                control.setEnabled(enabled);
            } else if (control instanceof Composite) {
                setChildrenEnabled((Composite)control, enabled);
            }
        }
    }

    private synchronized boolean isWorking() {
        return isComparing || isSynchronizing;
    }

    private synchronized void setIsComparing(boolean isComparing) {
        this.isComparing = isComparing;
    }

    private synchronized void setIsSynchronizing(boolean isSynchronizing) {
        this.isSynchronizing = isSynchronizing;
    }

    private void displayCompareStatus() {

        if (isWorking()) {
            statusMessage = Messages.Working;
        } else if (selectionChanged) {
            if (StringHelper.isNullOrEmpty(lblLeftMessageFile.getText()) || StringHelper.isNullOrEmpty(lblRightMessageFile.getText())) {
                statusMessage = Messages.Please_select_the_missing_message_file_then_press_Compare_to_start;
            } else {
                statusMessage = Messages.Please_press_Compare_to_start;
            }
            numFilteredItems = 0;//$NON-NLS-1$
        } else {
            TableStatistics tableStatistics = getTableStatistics();
            statusMessage = tableStatistics.toString();
            numFilteredItems = tableStatistics.getFilteredElements();
        }

        updateStatusLine();
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
        chkCompareAfterSync.setSelection(dialogSettingsManager.loadBooleanValue(BUTTON_COMPARE_AFTER_SYNC, true));
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
        dialogSettingsManager.storeValue(BUTTON_COMPARE_AFTER_SYNC, chkCompareAfterSync.getSelection());
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

    private void changeCompareStatus(int newStatus) {

        MessageFileCompareItem[] selectedItems = getSelectedItems();

        for (MessageFileCompareItem compareItem : selectedItems) {
            compareItem.setCompareStatus(newStatus);
            tableViewer.update(compareItem, null);
        }
        tableViewer.getTable().redraw();
        setButtonEnablementAndDisplayCompareStatus();
    }

    private void performCompareMessageFiles() {

        final MessageFileCompareEditorInput editorInput = getEditorInput();

        if (editorInput.getLeftMessageFileName().equals(editorInput.getRightMessageFileName())) {
            MessageDialog dialog = new MessageDialog(getShell(), Messages.Warning, null, Messages.Warning_Both_sides_show_the_same_message_file,
                MessageDialog.WARNING, new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0);
            if (dialog.open() == 1) {
                return;
            }
        }

        tableViewer.setInput(getEditorInput().clearAll());

        setIsComparing(true);
        setButtonEnablementAndDisplayCompareStatus();

        Job job = new Job(Messages.Loading_message_descriptions) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {

                try {

                    jobToCancel = monitor;
                    UIJob job = new UIJob("") {
                        @Override
                        public IStatus runInUIThread(IProgressMonitor monitor) {
                            setButtonEnablementAndDisplayCompareStatus();
                            return Status.OK_STATUS;
                        }
                    };
                    job.schedule();

                    monitor.beginTask("", 2);

                    MessageDescription[] leftMessageDescriptions = getMessageDescriptions(editorInput.getLeftMessageFile(), monitor);
                    getEditorInput().setLeftMessageDescriptions(leftMessageDescriptions);
                    monitor.worked(1);

                    if (monitor.isCanceled()) {
                        return cancelOperation();
                    }

                    MessageDescription[] rightMessageDescriptions = getMessageDescriptions(editorInput.getRightMessageFile(), monitor);
                    getEditorInput().setRightMessageDescriptions(rightMessageDescriptions);
                    monitor.worked(2);

                    if (monitor.isCanceled()) {
                        return cancelOperation();
                    }

                } finally {
                    monitor.done();

                    UIJob job = new UIJob("") {
                        @Override
                        public IStatus runInUIThread(IProgressMonitor monitor) {
                            if (tableViewer.getTable().isDisposed()) {
                                return Status.OK_STATUS;
                            }
                            jobToCancel = null;
                            tableViewer.setInput(getEditorInput());
                            selectionChanged = false;
                            setIsComparing(false);
                            setButtonEnablementAndDisplayCompareStatus();
                            return Status.OK_STATUS;
                        }
                    };
                    job.schedule();
                }

                return Status.OK_STATUS;
            }

            private IStatus cancelOperation() {

                getEditorInput().setLeftMessageDescriptions(new MessageDescription[0]);
                getEditorInput().setRightMessageDescriptions(new MessageDescription[0]);

                return Status.OK_STATUS;
            }

            private MessageDescription[] getMessageDescriptions(RemoteObject messageFile, IProgressMonitor monitor) {

                String connectionName = messageFile.getConnectionName();
                AS400 system = IBMiHostContributionsHandler.getSystem(connectionName);

                IQMHRTVM iqmhrtvm = new IQMHRTVM(system, connectionName);
                iqmhrtvm.setMessageFile(messageFile.getName(), messageFile.getLibrary());

                return iqmhrtvm.retrieveAllMessageDescriptions(monitor);
            }
        };
        job.schedule();
    }

    private void performSynchronizeMessageFiles() {

        setIsSynchronizing(true);

        try {

            setButtonEnablementAndDisplayCompareStatus();

            RemoteObject leftMessageFile = getEditorInput().getLeftMessageFile();
            RemoteObject rightMessageFile = getEditorInput().getRightMessageFile();

            for (int i = 0; i < tableViewer.getTable().getItemCount(); i++) {
                MessageFileCompareItem compareItem = (MessageFileCompareItem)tableViewer.getElementAt(i);

                getTableStatistics().removeElement(compareItem, filterData);
                if (compareItem.getCompareStatus() == MessageFileCompareItem.LEFT_MISSING) {
                    performCopyToLeft(compareItem, leftMessageFile);
                } else if (compareItem.getCompareStatus() == MessageFileCompareItem.RIGHT_MISSING) {
                    performCopyToRight(compareItem, rightMessageFile);
                }
                getTableStatistics().addElement(compareItem, filterData);
            }

            if (chkCompareAfterSync.getSelection()) {
                performCompareMessageFiles();
            }

        } finally {

            setIsSynchronizing(false);
            tableViewer.getTable().redraw();
            setButtonEnablementAndDisplayCompareStatus();

        }
    }

    private void performCopyToLeft(MessageFileCompareItem compareItem, RemoteObject toMessageFile) {

        try {

            if (MessageDescriptionHelper.mergeMessageDescription(getShell(), compareItem.getRightMessageDescription(),
                toMessageFile.getConnectionName(), toMessageFile.getName(), toMessageFile.getLibrary()) == null) {
                compareItem.setLeftMessageDescription(MessageDescriptionHelper.retrieveMessageDescription(toMessageFile.getConnectionName(),
                    toMessageFile.getName(), toMessageFile.getLibrary(), compareItem.getMessageId()));
                compareItem.clearCompareStatus();
            }

            tableViewer.update(compareItem, null);

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

            tableViewer.update(compareItem, null);

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

    private void performCancelOperation() {

        if (jobToCancel != null) {
            jobToCancel.setCanceled(true);
        }
    }

    @Override
    public void dispose() {

        if (jobToCancel != null) {
            jobToCancel.setCanceled(true);
        }

        super.dispose();
    }

    protected abstract RemoteObject performSelectRemoteObject(String connectionName, String libraryName, String objectName);

    protected abstract LabelProvider getTableLabelProvider(TableViewer tableViewer, int columnIndex);

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
                    changeCompareStatus(MessageFileCompareItem.NO_ACTION);
                }
            });
        }

        private void createMenuItemSelectForCopyingToTheLeft(MessageFileCompareItem compareItem) {
            menuItemSelectForCopyingToTheLeft = new MenuItem(parent, SWT.NONE);
            menuItemSelectForCopyingToTheLeft.setText(Messages.Select_for_copying_right_to_left);
            menuItemSelectForCopyingToTheLeft.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    changeCompareStatus(MessageFileCompareItem.LEFT_MISSING);
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
                    changeCompareStatus(MessageFileCompareItem.RIGHT_MISSING);
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

                getTableStatistics().removeElement(compareItem, filterData);
                MessageDescriptionHelper.refreshMessageDescription(messageDescription);
                tableViewer.update(compareItem, null);

                MessageDescriptionDetailDialog messageDescriptionDetailDialog = new MessageDescriptionDetailDialog(getShell(),
                    DialogActionTypes.getSubEditorActionType(IEditor.EDIT), messageDescription);
                if (messageDescriptionDetailDialog.open() == Dialog.OK) {
                    tableViewer.update(compareItem, null);
                }

                getTableStatistics().addElement(compareItem, filterData);

                tableViewer.getTable().redraw();
                setButtonEnablementAndDisplayCompareStatus();
            }
        }

        private void performDeleteMessageDescriptions(int side) {

            IStructuredSelection structuredSelection = (IStructuredSelection)tableViewer.getSelection();
            boolean yesToAll = false;

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

                int returnCode = MessageDescriptionDetailDialog.CANCEL;
                if (!yesToAll) {
                    boolean displayYesToAll;
                    if (structuredSelection.size() > 1) {
                        displayYesToAll = true;
                    } else {
                        displayYesToAll = false;
                    }
                    MessageDescriptionDetailDialog messageDescriptionDetailDialog = new MessageDescriptionDetailDialog(getShell(),
                        DialogActionTypes.DELETE, messageDescription, displayYesToAll);
                    returnCode = messageDescriptionDetailDialog.open();
                    if (returnCode == MessageDescriptionDetailDialog.YES_TO_ALL) {
                        returnCode = MessageDescriptionDetailDialog.OK;
                        yesToAll = true;
                    } else if (returnCode == MessageDescriptionDetailDialog.NO_TO_ALL) {
                        return;
                    }
                } else {
                    returnCode = MessageDescriptionDetailDialog.OK;
                }

                if (returnCode == MessageDescriptionDetailDialog.OK) {

                    try {

                        getTableStatistics().removeElement(compareItem, filterData);

                        MessageDescriptionHelper.removeMessageDescription(messageDescription);
                        if (side == LEFT) {
                            compareItem.setLeftMessageDescription(null);
                        } else {
                            compareItem.setRightMessageDescription(null);
                        }

                        if (compareItem.getLeftMessageDescription() == null && compareItem.getRightMessageDescription() == null) {
                            tableViewer.remove(compareItem);
                        } else {
                            tableViewer.update(compareItem, null);
                            getTableStatistics().addElement(compareItem, filterData);
                        }

                        tableViewer.getTable().redraw();
                        setButtonEnablementAndDisplayCompareStatus();

                    } catch (Exception e) {
                        MessageDialog.openError(getShell(), Messages.E_R_R_O_R, e.getLocalizedMessage());
                    }
                }
            }
        }
    }

    public void setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public void updateActionsStatusAndStatusLine() {
        updateActionsStatus();
        updateStatusLine();
    }

    private void updateActionsStatus() {

    }

    private void updateStatusLine() {

        if (statusLine == null) {
            return;
        }

        statusLine.setShowNumItems(true);
        statusLine.setShowMessage(true);

        if (statusLine != null) {
            statusLine.setMessage(statusMessage);
            statusLine.setNumItems(numFilteredItems);
        }

    }
}
