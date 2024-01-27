/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.objectsynchronization.rse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
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
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.progress.UIJob;

import biz.isphere.base.internal.DialogSettingsManager;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.internal.UIHelper;
import biz.isphere.base.swt.events.TableAutoSizeControlListener;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.compareeditor.SourceMemberCompareEditorConfiguration;
import biz.isphere.core.externalapi.ISynchronizeMembersEditorConfiguration;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.internal.Member;
import biz.isphere.core.internal.MessageDialogAsync;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.core.internal.Size;
import biz.isphere.core.objectsynchronization.CompareOptions;
import biz.isphere.core.objectsynchronization.MemberDescription;
import biz.isphere.core.objectsynchronization.SynchronizeMembersEditorInput;
import biz.isphere.core.objectsynchronization.SynchronizeMembersJob;
import biz.isphere.core.objectsynchronization.TableContentProvider;
import biz.isphere.core.objectsynchronization.TableFilter;
import biz.isphere.core.objectsynchronization.TableFilterData;
import biz.isphere.core.objectsynchronization.TableSorter;
import biz.isphere.core.objectsynchronization.TableStatistics;
import biz.isphere.core.objectsynchronization.jobs.AbstractCompareMembersJob;
import biz.isphere.core.objectsynchronization.jobs.CompareMembersSharedJobValues;
import biz.isphere.core.objectsynchronization.jobs.FinishCompareMembersJob;
import biz.isphere.core.objectsynchronization.jobs.ISynchronizeMembersPostRun;
import biz.isphere.core.objectsynchronization.jobs.LoadCompareMembersJob;
import biz.isphere.core.objectsynchronization.jobs.ResolveGenericCompareElementsJob;
import biz.isphere.core.objectsynchronization.jobs.StartCompareMembersJob;
import biz.isphere.core.objectsynchronization.jobs.SyncMbrMode;
import biz.isphere.core.sourcemembercopy.CopyMemberItem;
import biz.isphere.core.sourcemembercopy.CopyMemberValidator;
import biz.isphere.core.sourcemembercopy.CopyMemberValidator.MemberValidationError;
import biz.isphere.core.sourcemembercopy.IItemErrorListener;
import biz.isphere.core.swt.widgets.HistoryCombo;
import biz.isphere.core.swt.widgets.WidgetFactory;

public abstract class AbstractSynchronizeMembersEditor extends EditorPart implements ISynchronizeMembersPostRun, IItemErrorListener {

    public static final String ID = "biz.isphere.core.objectsynchronization.rse.SynchronizeMembersEditor"; //$NON-NLS-1$

    private static final String CHKBOX_IGNORE_DATE = "CHKBOX_IGNORE_DATE"; //$NON-NLS-1$
    private static final String CHKBOX_RTN_CHG_ONLY = "CHKBOX_RTN_CHG_ONLY"; //$NON-NLS-1$
    private static final String BUTTON_COPY_LEFT = "BUTTON_COPY_LEFT"; //$NON-NLS-1$
    private static final String BUTTON_COPY_RIGHT = "BUTTON_COPY_RIGHT"; //$NON-NLS-1$
    private static final String BUTTON_NO_COPY = "BUTTON_NO_COPY"; //$NON-NLS-1$
    private static final String BUTTON_EQUAL = "BUTTON_EQUAL"; //$NON-NLS-1$
    private static final String BUTTON_SINGLES = "BUTTON_SINGLES"; //$NON-NLS-1$
    private static final String BUTTON_DUPLICATES = "BUTTON_DUPLICATES"; //$NON-NLS-1$
    private static final String BUTTON_COMPARE_AFTER_SYNC = "BUTTON_COMPARE_AFTER_SYNC"; //$NON-NLS-1$

    private static final String MEMBER_FILTER_HISTORY_KEY = "memberFilterHistory"; //$NON-NLS-1$

    // private SynchronizeMembersEditorInput input;

    private boolean isLeftObjectValid;
    private boolean isRightObjectValid;

    private TableViewer tableViewer;
    private TableFilter tableFilter;
    private TableFilterData filterData;
    private AbstractTableLabelProvider labelProvider;

    private Button btnCompare;
    private Button btnSynchronize;
    private Button btnCancel;
    private Button chkCompareAfterSync;

    private DialogSettingsManager dialogSettingsManager;

    private Label lblLeftObject;
    private Button btnSelectLeftObject;
    private HistoryCombo cboMemberFilter;
    private Label lblRightObject;
    private Button btnSelectRightObject;

    private Button btnCopyRight;
    private Button btnEqual;
    private Button btnNoCopy;
    private Button btnCopyLeft;
    private Button btnDuplicates;
    private Button btnSingles;

    private Button chkRtnChgOnly;
    private Button chkIgnoreDate;

    private Shell shell;

    private Composite headerArea;
    private Composite optionsArea;

    private StatusLine statusLine;
    private String statusMessage;
    private int numFilteredItems;

    private CompareMembersSharedJobValues sharedValues;

    private boolean isComparing;
    private boolean isSynchronizing;
    private IProgressMonitor jobToCancel;

    public AbstractSynchronizeMembersEditor() {

        isLeftObjectValid = false;
        isRightObjectValid = false;

        dialogSettingsManager = new DialogSettingsManager(ISpherePlugin.getDefault().getDialogSettings(), getClass());
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

        refreshAndCheckObjectNames();
        refreshTableFilter();

        if (getEditorInput().areSameObjects()) {
            MessageDialogAsync.displayError(getShell(), Messages.Warning_The_left_and_right_site_display_the_same_object);
        }
    }

    private void createHeaderArea(Composite parent) {

        headerArea = new Composite(parent, SWT.NONE);
        headerArea.setLayout(createGridLayoutNoBorder(3, false));
        headerArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Composite leftHeaderArea = new Composite(headerArea, SWT.NONE);
        GridData leftHeaderAreaLayoutData = new GridData(GridData.FILL_HORIZONTAL);
        leftHeaderAreaLayoutData.minimumWidth = 120;
        leftHeaderArea.setLayoutData(leftHeaderAreaLayoutData);
        leftHeaderArea.setLayout(createGridLayoutNoBorder(2, false));

        lblLeftObject = new Label(leftHeaderArea, SWT.BORDER);
        GridData lblLeftObjectLayoutData = new GridData(GridData.FILL_HORIZONTAL);
        lblLeftObjectLayoutData.minimumWidth = 120;
        lblLeftObject.setLayoutData(lblLeftObjectLayoutData);

        btnSelectLeftObject = WidgetFactory.createPushButton(leftHeaderArea);
        btnSelectLeftObject.setToolTipText(Messages.Tooltip_Select_object);
        btnSelectLeftObject.setImage(ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_OPEN).createImage());
        btnSelectLeftObject.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent arg0) {
                String connectionName = null;
                String libraryName = null;
                String objectName = null;
                if (getEditorInput().getLeftObject() != null) {
                    connectionName = getEditorInput().getLeftObject().getConnectionName();
                    libraryName = getEditorInput().getLeftObject().getLibrary();
                    objectName = getEditorInput().getLeftObject().getName();
                }
                RemoteObject object = performSelectRemoteObject(connectionName, libraryName, objectName);
                if (object != null) {
                    isLeftObjectValid = false;
                    getEditorInput().setLeftObject(object);
                    refreshAndCheckObjectNames();
                } else {
                    setButtonEnablementAndDisplayCompareStatus();
                }
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        Composite middleHeaderArea = new Composite(headerArea, SWT.NONE);
        middleHeaderArea.setLayout(createGridLayoutNoBorder(1, false));
        GridData middleHeaderAreaLayoutData = new GridData();
        middleHeaderAreaLayoutData.minimumWidth = 120;
        middleHeaderArea.setLayoutData(middleHeaderAreaLayoutData);

        cboMemberFilter = WidgetFactory.createHistoryCombo(middleHeaderArea);
        GridData cboMemberFilterLayoutData = new GridData(GridData.FILL_HORIZONTAL);
        cboMemberFilterLayoutData.widthHint = 150;
        cboMemberFilter.setLayoutData(cboMemberFilterLayoutData);
        cboMemberFilter.setToolTipText(Messages.Tooltip_Member_name_and_source_type_filter);

        Composite rightHeaderArea = new Composite(headerArea, SWT.NONE);
        rightHeaderArea.setLayout(createGridLayoutNoBorder(2, false));
        GridData rightHeaderAreaLayoutData = new GridData(GridData.FILL_HORIZONTAL);
        rightHeaderAreaLayoutData.minimumWidth = 120;
        rightHeaderArea.setLayoutData(rightHeaderAreaLayoutData);

        lblRightObject = new Label(rightHeaderArea, SWT.BORDER);
        GridData lblRightObjectLayoutData = new GridData(GridData.FILL_HORIZONTAL);
        lblRightObjectLayoutData.minimumWidth = 120;
        lblRightObject.setLayoutData(lblRightObjectLayoutData);

        btnSelectRightObject = WidgetFactory.createPushButton(rightHeaderArea);
        btnSelectRightObject.setToolTipText(Messages.Tooltip_Select_object);
        btnSelectRightObject.setImage(ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_OPEN).createImage());
        btnSelectRightObject.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent arg0) {
                String connectionName = null;
                String libraryName = null;
                String fileName = null;
                if (getEditorInput().getRightObject() != null) {
                    connectionName = getEditorInput().getRightObject().getConnectionName();
                    libraryName = getEditorInput().getRightObject().getLibrary();
                    fileName = getEditorInput().getRightObject().getName();
                }
                RemoteObject sourceFile = performSelectRemoteObject(connectionName, libraryName, fileName);
                if (sourceFile != null) {
                    isRightObjectValid = false;
                    getEditorInput().setRightObject(sourceFile);
                    refreshAndCheckObjectNames();
                } else {
                    setButtonEnablementAndDisplayCompareStatus();
                }
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });
    }

    private void createOptionsArea(Composite parent) {

        optionsArea = new Composite(parent, SWT.NONE);
        optionsArea.setLayout(createGridLayoutNoBorder(3, false));
        optionsArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        int verticalSpan = 3;
        btnCompare = WidgetFactory.createPushButton(optionsArea);
        btnCompare.setLayoutData(createButtonLayoutData(1));
        btnCompare.setText(Messages.Compare);
        btnCompare.setToolTipText(Messages.Tooltip_start_compare);
        btnCompare.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent event) {
                storeScreenValues();
                performCompareMembers();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
            }
        });

        createFilterOptionsArea(optionsArea, verticalSpan);

        if (isSynchronizationEnabled()) {
            btnSynchronize = WidgetFactory.createPushButton(optionsArea);
            btnSynchronize.setLayoutData(createButtonLayoutData(1, SWT.RIGHT));
            btnSynchronize.setText(Messages.Synchronize);
            btnSynchronize.setToolTipText(Messages.Tooltip_start_synchronize);
            btnSynchronize.addSelectionListener(new SelectionListener() {
                public void widgetSelected(SelectionEvent event) {
                    performSynchronizeMembers();
                }

                public void widgetDefaultSelected(SelectionEvent event) {
                }
            });
        }

        if (isSynchronizationEnabled()) {
            createCompareOptionsArea(optionsArea, verticalSpan - 1);
        }

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

        if (isSynchronizationEnabled()) {
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
        } else {
            createCompareOptionsArea(optionsArea, verticalSpan - 1);
        }
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

    private void createCompareOptionsArea(Composite parent, int verticalSpan) {

        Composite compareOptionsGroup = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginWidth = 0;
        gridLayout.marginLeft = 1;
        compareOptionsGroup.setLayout(gridLayout);
        compareOptionsGroup.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, true, 1, 2));

        chkIgnoreDate = WidgetFactory.createCheckbox(compareOptionsGroup, Messages.Label_Ignore_date);
        chkIgnoreDate.setToolTipText(Messages.Tooltip_Ignore_date);

        chkRtnChgOnly = WidgetFactory.createCheckbox(compareOptionsGroup, Messages.Label_Return_changed_only);
        chkRtnChgOnly.setToolTipText(Messages.Tooltip_Return_changed_only);
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
        TableColumn tblClmnDummy = new TableColumn(tableViewer.getTable(), SWT.NONE);
        tblClmnDummy.setResizable(true);
        tblClmnDummy.setWidth(Size.getSize(0));

        final TableColumn tblClmnLeftLibrary = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblClmnLeftLibrary.setText(Messages.Library);
        tblClmnLeftLibrary.setResizable(true);
        tblClmnLeftLibrary.setWidth(Size.getSize(80));

        final TableColumn tblClmnLeftFile = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblClmnLeftFile.setText(Messages.File);
        tblClmnLeftFile.setResizable(true);
        tblClmnLeftFile.setWidth(Size.getSize(80));

        final TableColumn tblClmnLeftMember = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblClmnLeftMember.setText(Messages.Member);
        tblClmnLeftMember.setResizable(true);
        tblClmnLeftMember.setWidth(Size.getSize(80));

        final TableColumn tblClmnLeftSourceType = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblClmnLeftSourceType.setText(Messages.Source_Type);
        tblClmnLeftSourceType.setResizable(true);
        tblClmnLeftSourceType.setWidth(Size.getSize(60));

        final TableColumn tblClmnLeftLastChanges = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblClmnLeftLastChanges.setText(Messages.Last_changed);
        tblClmnLeftLastChanges.setResizable(true);
        tblClmnLeftLastChanges.setWidth(Size.getSize(60));

        final TableColumn tblClmnLeftDescription = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblClmnLeftDescription.setText(Messages.Description);
        tblClmnLeftDescription.setWidth(Size.getSize(200));

        final TableColumn tblClmnCompareResult = new TableColumn(tableViewer.getTable(), SWT.CENTER);
        tblClmnCompareResult.setResizable(true);
        tblClmnCompareResult.setWidth(Size.getSize(25));

        final TableColumn tblClmnRightLibrary = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblClmnRightLibrary.setText(Messages.Library);
        tblClmnRightLibrary.setResizable(tblClmnLeftLibrary.getResizable());
        tblClmnRightLibrary.setWidth(tblClmnLeftLibrary.getWidth());

        final TableColumn tblClmnRightFile = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblClmnRightFile.setText(Messages.File);
        tblClmnRightFile.setResizable(tblClmnLeftFile.getResizable());
        tblClmnRightFile.setWidth(tblClmnLeftFile.getWidth());

        final TableColumn tblClmnRightMember = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblClmnRightMember.setText(Messages.Member);
        tblClmnRightMember.setResizable(tblClmnLeftMember.getResizable());
        tblClmnRightMember.setWidth(tblClmnLeftMember.getWidth());

        final TableColumn tblClmnRightSourceType = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblClmnRightSourceType.setText(Messages.Source_Type);
        tblClmnRightSourceType.setResizable(tblClmnLeftSourceType.getResizable());
        tblClmnRightSourceType.setWidth(tblClmnLeftSourceType.getWidth());

        final TableColumn tblClmnRightLastChanges = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblClmnRightLastChanges.setText(Messages.Last_changed);
        tblClmnRightLastChanges.setResizable(tblClmnLeftLastChanges.getResizable());
        tblClmnRightLastChanges.setWidth(tblClmnLeftLastChanges.getWidth());

        final TableColumn tblClmnRightDescription = new TableColumn(tableViewer.getTable(), SWT.LEFT);
        tblClmnRightDescription.setText(Messages.Description);
        tblClmnRightDescription.setResizable(tblClmnLeftDescription.getResizable());
        tblClmnRightDescription.setWidth(tblClmnLeftDescription.getWidth());

        tableViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                ISelection selection = event.getSelection();
                if (selection instanceof StructuredSelection) {
                    StructuredSelection structuredSelection = (StructuredSelection)selection;
                    for (Iterator<?> iterator = structuredSelection.iterator(); iterator.hasNext();) {
                        Object item = (Object)iterator.next();
                        if (item instanceof MemberCompareItem) {
                            MemberCompareItem compareItem = (MemberCompareItem)item;
                            performOpenCompareMembersDialog(compareItem);
                        }
                    }
                }
            }
        });

        TableStatistics tableStatistics = new TableStatistics();
        tableFilter = new TableFilter(tableStatistics);

        tableViewer.setContentProvider(new TableContentProvider(tableStatistics));
        tableViewer.addFilter(tableFilter);
        labelProvider = getTableLabelProvider(tableViewer, 7);
        tableViewer.setLabelProvider(labelProvider);
        Menu menuTableViewerContextMenu = new Menu(tableViewer.getTable());
        menuTableViewerContextMenu.addMenuListener(new TableContextMenu(menuTableViewerContextMenu, getEditorInput().getConfiguration()));
        tableViewer.getTable().setMenu(menuTableViewerContextMenu);

        TableAutoSizeControlListener tableAutoSizeAdapter = new TableAutoSizeControlListener(tableViewer.getTable());
        tableAutoSizeAdapter.addResizableColumn(tblClmnLeftDescription, 1);
        tableAutoSizeAdapter.addResizableColumn(tblClmnRightDescription, 1);
        tableViewer.getTable().addControlListener(tableAutoSizeAdapter);

        tableViewer.setSorter(new TableSorter(SyncMbrMode.LEFT_SYSTEM));
    }

    private void createrFooterArea(Composite parent) {

        Composite footerArea = new Composite(parent, SWT.NONE);
        footerArea.setLayout(new GridLayout(1, false));
        footerArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
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
        setTitleImage(((SynchronizeMembersEditorInput)input).getTitleImage());
    }

    @Override
    public void setFocus() {
    }

    protected Shell getShell() {
        return shell;
    }

    public SynchronizeMembersEditorInput getEditorInput() {

        IEditorInput input = super.getEditorInput();
        if (input instanceof SynchronizeMembersEditorInput) {
            return (SynchronizeMembersEditorInput)input;
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

    public static void openEditor(RemoteObject leftRemoteObject, RemoteObject rightRemoteObject, ISynchronizeMembersEditorConfiguration configuration)
        throws PartInitException {

        if (leftRemoteObject != null) {
            String leftConnectionName = leftRemoteObject.getConnectionName();
            if (!ISphereHelper.checkISphereLibrary(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), leftConnectionName)) {
                return;
            }
        }

        if (rightRemoteObject != null) {
            String rightConnectionName = rightRemoteObject.getConnectionName();
            if (!ISphereHelper.checkISphereLibrary(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), rightConnectionName)) {
                return;
            }
        }

        SynchronizeMembersEditorInput editorInput = new SynchronizeMembersEditorInput(leftRemoteObject, rightRemoteObject, configuration);
        UIHelper.getActivePage().openEditor(editorInput, AbstractSynchronizeMembersEditor.ID);
    }

    private void refreshAndCheckObjectNames() {

        SynchronizeMembersEditorInput editorInput = getEditorInput();
        if (editorInput != null) {
            lblLeftObject.setText(getEditorInput().getLeftObjectName());
            lblRightObject.setText(getEditorInput().getRightObjectName());
        } else {
            lblLeftObject.setText(Messages.EMPTY);
            lblRightObject.setText(Messages.EMPTY);
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

    private boolean isSynchronizationEnabled() {
        ISynchronizeMembersEditorConfiguration config = getEditorInput().getConfiguration();
        return config.isLeftEditorEnabled() || config.isRightEditorEnabled();
    }

    private synchronized void setButtonEnablementAndDisplayCompareStatus() {

        boolean isCompareEnabled = true;
        boolean isSynchronizeEnabled = true;

        if (getEditorInput().getLeftObject() != null && !isLeftObjectValid) {
            String connectionName = getEditorInput().getLeftObject().getConnectionName();
            if (!ISphereHelper.checkISphereLibrary(getShell(), connectionName)) {
                isCompareEnabled = false;
                isSynchronizeEnabled = false;
                isLeftObjectValid = false;
            } else {
                isLeftObjectValid = true;
            }
        }

        if (getEditorInput().getRightObject() != null && !isRightObjectValid) {
            String connectionName = getEditorInput().getRightObject().getConnectionName();
            if (!ISphereHelper.checkISphereLibrary(getShell(), connectionName)) {
                isCompareEnabled = false;
                isSynchronizeEnabled = false;
                isRightObjectValid = false;
            } else {
                isRightObjectValid = true;
            }
        }

        if (getEditorInput().getLeftObject() == null || getEditorInput().getRightObject() == null) {
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

        ISynchronizeMembersEditorConfiguration config = getEditorInput().getConfiguration();

        if (btnSynchronize != null && chkCompareAfterSync != null) {
            if (isWorking()) {
                btnSynchronize.setEnabled(false);
                chkCompareAfterSync.setEnabled(false);
            } else {
                if (config.isLeftEditorEnabled() || config.isRightEditorEnabled()) {
                    btnSynchronize.setEnabled(isSynchronizeEnabled);
                    chkCompareAfterSync.setEnabled(isSynchronizeEnabled);
                } else {
                    btnSynchronize.setEnabled(false);
                    chkCompareAfterSync.setEnabled(false);
                }
            }
        }

        if (config.isLeftSelectObjectEnabled()) {
            btnSelectLeftObject.setEnabled(true);
        } else {
            btnSelectLeftObject.setEnabled(false);
        }

        if (config.isRightSelectObjectEnabled()) {
            btnSelectRightObject.setEnabled(true);
        } else {
            btnSelectRightObject.setEnabled(false);
        }

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
        } else {
            TableStatistics tableStatistics = getTableStatistics();
            statusMessage = tableStatistics.toString();
            numFilteredItems = tableStatistics.getFilteredElements();
        }

        updateStatusLine();
    }

    public void loadHistory() {
        cboMemberFilter.load(dialogSettingsManager, MEMBER_FILTER_HISTORY_KEY);
    }

    public void updateHistory() {
        cboMemberFilter.updateHistory(cboMemberFilter.getText());
    }

    public void storeHistory() {
        cboMemberFilter.store();
    }

    /**
     * Restores the screen values of the last search search.
     */
    private void loadScreenValues() {

        chkIgnoreDate.setSelection(dialogSettingsManager.loadBooleanValue(CHKBOX_IGNORE_DATE, false));
        chkRtnChgOnly.setSelection(dialogSettingsManager.loadBooleanValue(CHKBOX_RTN_CHG_ONLY, false));

        btnCopyLeft.setSelection(dialogSettingsManager.loadBooleanValue(BUTTON_COPY_LEFT, true));
        btnCopyRight.setSelection(dialogSettingsManager.loadBooleanValue(BUTTON_COPY_RIGHT, true));
        btnEqual.setSelection(dialogSettingsManager.loadBooleanValue(BUTTON_EQUAL, true));
        btnNoCopy.setSelection(dialogSettingsManager.loadBooleanValue(BUTTON_NO_COPY, true));
        btnSingles.setSelection(dialogSettingsManager.loadBooleanValue(BUTTON_SINGLES, true));
        btnDuplicates.setSelection(dialogSettingsManager.loadBooleanValue(BUTTON_DUPLICATES, true));
        if (isSynchronizationEnabled()) {
            chkCompareAfterSync.setSelection(dialogSettingsManager.loadBooleanValue(BUTTON_COMPARE_AFTER_SYNC, true));
        }

        loadHistory();

        if (StringHelper.isNullOrEmpty(cboMemberFilter.getText())) {
            cboMemberFilter.setText("*.*");
            storeHistory();
        }
    }

    /**
     * Stores the screen values that are preserved for the next search.
     */
    private void storeScreenValues() {

        dialogSettingsManager.storeValue(CHKBOX_IGNORE_DATE, chkIgnoreDate.getSelection());
        dialogSettingsManager.storeValue(CHKBOX_RTN_CHG_ONLY, chkRtnChgOnly.getSelection());

        dialogSettingsManager.storeValue(BUTTON_COPY_LEFT, btnCopyLeft.getSelection());
        dialogSettingsManager.storeValue(BUTTON_COPY_RIGHT, btnCopyRight.getSelection());
        dialogSettingsManager.storeValue(BUTTON_EQUAL, btnEqual.getSelection());
        dialogSettingsManager.storeValue(BUTTON_NO_COPY, btnNoCopy.getSelection());
        dialogSettingsManager.storeValue(BUTTON_SINGLES, btnSingles.getSelection());
        dialogSettingsManager.storeValue(BUTTON_DUPLICATES, btnDuplicates.getSelection());
        if (isSynchronizationEnabled()) {
            dialogSettingsManager.storeValue(BUTTON_COMPARE_AFTER_SYNC, chkCompareAfterSync.getSelection());
        }

        updateHistory();
        storeHistory();
    }

    private MemberCompareItem[] getSelectedItems() {

        List<MemberCompareItem> selectedItems = new ArrayList<MemberCompareItem>();

        if (tableViewer.getSelection() instanceof StructuredSelection) {
            StructuredSelection selection = (StructuredSelection)tableViewer.getSelection();
            for (Iterator<?> iterator = selection.iterator(); iterator.hasNext();) {
                Object selectedItem = (Object)iterator.next();
                if (selectedItem instanceof MemberCompareItem) {
                    MemberCompareItem compareItem = (MemberCompareItem)selectedItem;
                    selectedItems.add(compareItem);
                }
            }
        }

        return selectedItems.toArray(new MemberCompareItem[selectedItems.size()]);
    }

    private void changeCompareStatus(int newStatus) {

        MemberCompareItem[] selectedItems = getSelectedItems();

        for (MemberCompareItem compareItem : selectedItems) {
            compareItem.setCompareStatus(newStatus, this.sharedValues.getCompareOptions());
            tableViewer.update(compareItem, null);
        }
        tableViewer.getTable().redraw();
        setButtonEnablementAndDisplayCompareStatus();
    }

    private void performCompareMembers() {

        final SynchronizeMembersEditorInput editorInput = getEditorInput();

        if (editorInput.getLeftObject() == null) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, "Left source file or library is missing.");
            return;
        }

        if (editorInput.getRightObject() == null) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, "Right source file or library is missing.");
            return;
        }

        if (editorInput.getLeftObjectName().equals(editorInput.getRightObjectName())) {
            MessageDialog dialog = new MessageDialog(getShell(), Messages.Warning, null, Messages.Warning_Both_sides_show_the_same_source_members,
                MessageDialog.WARNING, new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0);
            if (dialog.open() == 1) {
                return;
            }
        }

        tableViewer.setInput(getEditorInput().clearAll());

        setIsComparing(true);
        setButtonEnablementAndDisplayCompareStatus();

        sharedValues = createSharedValues();

        Job job = new Job(Messages.Loading_source_members) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {

                List<AbstractCompareMembersJob> workerJobs = new LinkedList<AbstractCompareMembersJob>();
                workerJobs.add(new StartCompareMembersJob(monitor, sharedValues, editorInput.getLeftObject(), editorInput.getRightObject()));

                ResolveGenericCompareElementsJob resolveGenericCompareElementsJob = new ResolveGenericCompareElementsJob(monitor, sharedValues);
                workerJobs.add(resolveGenericCompareElementsJob);

                LoadCompareMembersJob loadMembersJob = new LoadCompareMembersJob(monitor, sharedValues);
                workerJobs.add(loadMembersJob);

                FinishCompareMembersJob finishCompareMembersJob = new FinishCompareMembersJob(monitor, sharedValues);
                workerJobs.add(finishCompareMembersJob);

                // Get number of work items.
                int work = 0;
                for (AbstractCompareMembersJob job : workerJobs) {
                    work = work + job.getWorkCount();
                }

                int worked = 0;

                try {

                    jobToCancel = monitor;
                    UIJob job = new UIJob(Messages.EMPTY) {
                        @Override
                        public IStatus runInUIThread(IProgressMonitor monitor) {
                            setButtonEnablementAndDisplayCompareStatus();
                            return Status.OK_STATUS;
                        }
                    };
                    job.schedule();

                    monitor.beginTask(Messages.EMPTY, work);

                    // Process work items.
                    for (AbstractCompareMembersJob workerJob : workerJobs) {
                        if (monitor.isCanceled()) {
                            return cancelOperation();
                        }
                        worked = workerJob.execute(worked);
                    }

                    MemberDescription[] leftMessageDescriptions = loadMembersJob.getLeftMembers();
                    MemberDescription[] rightMessageDescriptions = loadMembersJob.getRightMembers();

                    getEditorInput().setLeftMemberDescriptions(leftMessageDescriptions);
                    getEditorInput().setRightMemberDescriptions(rightMessageDescriptions);

                } finally {

                    worked = finishCompareMembersJob.execute(worked);

                    monitor.done();

                    UIJob uiJob = new UIJob(Messages.EMPTY) {
                        @Override
                        public IStatus runInUIThread(IProgressMonitor monitor) {
                            if (tableViewer.getTable().isDisposed()) {
                                return Status.OK_STATUS;
                            }
                            jobToCancel = null;
                            tableViewer.setInput(getEditorInput());
                            setIsComparing(false);
                            setButtonEnablementAndDisplayCompareStatus();
                            return Status.OK_STATUS;
                        }
                    };
                    uiJob.schedule();
                }

                return Status.OK_STATUS;

            }

            private IStatus cancelOperation() {

                getEditorInput().setLeftMemberDescriptions(new MemberDescription[0]);
                getEditorInput().setRightMemberDescriptions(new MemberDescription[0]);

                return Status.OK_STATUS;
            }

        };
        job.schedule();
    }

    private CompareMembersSharedJobValues createSharedValues() {

        sharedValues = new CompareMembersSharedJobValues(new CompareOptions());
        updateCompareOptions();

        return sharedValues;
    }

    protected void updateCompareOptions() {

        if (sharedValues == null) {
            return;
        }

        CompareOptions compareOptions = sharedValues.getCompareOptions();

        boolean rtnChgOnly = chkRtnChgOnly.getSelection();
        boolean ignoreDate = chkIgnoreDate.getSelection();
        String memberFilter = cboMemberFilter.getText();

        compareOptions.setRtnChgOnly(rtnChgOnly);
        compareOptions.setIgnoreDate(ignoreDate);
        compareOptions.setMemberFilter(memberFilter);

        labelProvider.setCompareOptions(sharedValues.getCompareOptions());
        tableFilter.setCompareOptions(sharedValues.getCompareOptions());
    }

    private void performSynchronizeMembers() {

        setIsSynchronizing(true);
        setButtonEnablementAndDisplayCompareStatus();

        RemoteObject leftObject = getEditorInput().getLeftObject();
        RemoteObject rightObject = getEditorInput().getRightObject();

        SynchronizeMembersJob synchronizeMembersJob = new SynchronizeMembersJob(leftObject, rightObject, this);
        synchronizeMembersJob.setItemErrorListener(this);
        // TODO: create action control on UI
        // synchronizeMembersJob.setExistingMemberAction(ExistingMemberAction.REPLACE);

        for (int i = 0; i < tableViewer.getTable().getItemCount(); i++) {
            MemberCompareItem compareItem = (MemberCompareItem)tableViewer.getElementAt(i);
            compareItem.resetErrorStatus();
            synchronizeMembersJob.addItem(compareItem, sharedValues.getCompareOptions());
        }

        synchronizeMembersJob.schedule();
    }

    /**
     * File error callback of {@link CopyMemberValidator}.
     * <p>
     * {@inheritDoc}
     */
    public boolean reportError(Object sender, MemberValidationError errorId, String errorMessage) {

        debug(sender.getClass().getSimpleName() + " -> Validation error: " + errorMessage);

        return false; // continue
    }

    /**
     * Member error callback of {@link CopyMemberValidator}.
     * <p>
     * {@inheritDoc}
     */
    public boolean reportError(Object sender, CopyMemberItem item, String errorMessage) {

        debug(sender.getClass().getSimpleName() + " -> " + "Item " + item.getFromMember() + " validation error: " + errorMessage);

        final MemberCompareItem compareItem = (MemberCompareItem)item.getData();
        compareItem.setErrorStatus(errorMessage);

        UIJob uiJob = new UIJob(Messages.EMPTY) {
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                debug("Refreshing table item: " + compareItem.getMemberName());
                tableViewer.refresh(compareItem, true, true);
                return Status.OK_STATUS;
            }
        };
        uiJob.schedule();

        return false; // continue
    }

    /**
     * PostRun of {@link SynchronizeMembersJob}.
     * <p>
     * {@inheritDoc}
     */
    public void returnResultPostRun(boolean isError, int countMembersCopied) {

        debug("\nAbstractSynchronizeMembersEditor.copyMembersPostRun:");

        UIJob uiJob = new UIJob(Messages.EMPTY) {

            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {

                debug("Compare result:");
                TableItem[] items = tableViewer.getTable().getItems();
                for (TableItem tableItem : items) {
                    MemberCompareItem memberCompareItem = (MemberCompareItem)tableItem.getData();
                    if (memberCompareItem.isError()) {
                        debug(memberCompareItem.toString() + ": " + memberCompareItem.getErrorMessage());
                    }
                }

                if (tableViewer.getTable().isDisposed()) {
                    return Status.OK_STATUS;
                }

                jobToCancel = null;
                setIsSynchronizing(false);
                setButtonEnablementAndDisplayCompareStatus();

                if (isSynchronizationEnabled()) {
                    if (chkCompareAfterSync.getSelection()) {
                        performCompareMembers();
                    }
                }
                return Status.OK_STATUS;
            }
        };
        uiJob.schedule();

    }

    protected void performOpenCompareMembersDialog(MemberCompareItem compareItem) {

        if (compareItem.isSingle()) {
            return;
        }

        try {

            MemberDescription leftMemberDescription = compareItem.getLeftMemberDescription();
            MemberDescription rightMemberDescription = compareItem.getRightMemberDescription();

            Member leftMember = createRemoteObject(leftMemberDescription);
            Member rightMember = createRemoteObject(rightMemberDescription);

            SourceMemberCompareEditorConfiguration cc = new SourceMemberCompareEditorConfiguration();
            cc.setIgnoreCase(false);
            cc.setIgnoreChangesLeft(false);
            cc.setIgnoreChangesRight(false);
            cc.setConsiderDate(false);
            cc.setThreeWay(false);
            cc.setLeftEditable(false);
            cc.setRightEditable(false);
            cc.setLeftLabel(createLabel(leftMember));
            cc.setRightLabel(createLabel(rightMember));
            cc.setOpenInEditor(false);
            cc.setShowDialog(false);

            List<Member> members = new LinkedList<Member>();
            members.add(leftMember);
            members.add(rightMember);

            IBMiHostContributionsHandler.compareSourceMembers(members, cc);

        } catch (Throwable e) {
            ISpherePlugin.logError("*** Clould not open source member compare editor ***", e); //$NON-NLS-1$
        }
    }

    // private boolean isValidating() {
    //
    // if (copyMemberValidator != null && copyMemberValidator.isActive()) {
    // return true;
    // }
    //
    // return false;
    // }

    // private boolean isCopying() {
    //
    // if (copyMemberService != null && copyMemberService.isActive()) {
    // return true;
    // }
    //
    // return false;
    // }

    // private void setControlEnablement() {
    //
    // if (copyMemberService == null) {
    // setButtonEnablement(btnCompare, true);
    // setButtonEnablement(btnSynchronize, true);
    // setButtonEnablement(btnCancel, false);
    // setControlsEnables(true);
    // } else {
    //
    // if (isValidating()) {
    // setButtonEnablement(btnCompare, false);
    // setButtonEnablement(btnSynchronize, false);
    // setButtonEnablement(btnCancel, true);
    // setControlsEnables(false);
    // // setStatusMessage(Messages.Validating_dots);
    // } else if (isCopying()) {
    // setButtonEnablement(btnCompare, false);
    // setButtonEnablement(btnSynchronize, false);
    // setButtonEnablement(btnCancel, true);
    // setControlsEnables(false);
    // // setStatusMessage(Messages.Copying_dots);
    // } else {
    //
    // if (copyMemberService.hasItemsToCopy()) {
    // setButtonEnablement(btnSynchronize, true);
    // } else {
    // setButtonEnablement(btnSynchronize, false);
    // }
    //
    // setButtonEnablement(btnCompare, true);
    // setButtonEnablement(btnCancel, true);
    //
    // setControlsEnables(true);
    //
    // if (copyMemberService.isCanceled()) {
    // MessageDialog.openError(getShell(), Messages.E_R_R_O_R,
    // Messages.Operation_has_been_canceled_by_the_user);
    // }
    // }
    // }
    // }

    // private void setControlsEnables(boolean enabled) {
    //
    // if (optionsArea == null) {
    // // not yet created
    // return;
    // }
    //
    // chkIgnoreDate.setEnabled(enabled);
    // chkRtnChgOnly.setEnabled(enabled);
    //
    // btnCopyRight.setEnabled(enabled);
    // btnEqual.setEnabled(enabled);
    // btnNoCopy.setEnabled(enabled);
    // btnCopyLeft.setEnabled(enabled);
    //
    // btnDuplicates.setEnabled(enabled);
    // btnSingles.setEnabled(enabled);
    // }

    // private void setButtonEnablement(Button button, boolean enabled) {
    // if (button == null) {
    // return;
    // }
    //
    // button.setEnabled(enabled);
    // }

    private String createLabel(Member member) {

        StringBuilder buffer = new StringBuilder();

        buffer.append(member.getLibrary());
        buffer.append("/");
        buffer.append(member.getSourceFile());
        buffer.append("(");
        buffer.append(member.getMember());
        buffer.append(")");

        return buffer.toString();
    }

    private Member createRemoteObject(MemberDescription memberDescription) throws Exception {

        String connectionName = memberDescription.getConnectionName();
        String fileName = memberDescription.getFileName();
        String libraryName = memberDescription.getLibraryName();
        String memberName = memberDescription.getMemberName();

        Member remoteObject = IBMiHostContributionsHandler.getMember(connectionName, libraryName, fileName, memberName);

        return remoteObject;
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

        dispose(btnSelectLeftObject);
        dispose(btnSelectRightObject);
        dispose(btnCopyRight);
        dispose(btnCopyLeft);
        dispose(btnNoCopy);
        dispose(btnEqual);

        super.dispose();
    }

    private void dispose(Button button) {

        if (button.isDisposed()) {
            return;
        }

        if (button.getImage().isDisposed()) {
            return;
        }

        button.getImage().dispose();
    }

    protected abstract RemoteObject performSelectRemoteObject(String connectionName, String libraryName, String objectName);

    protected abstract AbstractTableLabelProvider getTableLabelProvider(TableViewer tableViewer, int columnIndex);

    /**
     * Class that implements the context menu for the table rows.
     */
    private class TableContextMenu extends MenuAdapter {

        private Menu parent;
        private ISynchronizeMembersEditorConfiguration configuration;

        private MenuItem menuItemRemoveSelection;
        private MenuItem menuItemSelectForCopyingToTheRight;
        private MenuItem menuItemSelectForCopyingToTheLeft;
        private MenuItem menuItemEditLeft;
        private MenuItem menuItemEditRight;
        private MenuItem menuItemCompareLeftAndRight;
        private MenuItem menuItemSeparator;
        private MenuItem menuItemDeleteLeft;
        private MenuItem menuItemDeleteRight;

        public TableContextMenu(Menu parent, ISynchronizeMembersEditorConfiguration configuration) {
            this.parent = parent;
            this.configuration = configuration;
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

            boolean isLeftEditorEnabled = configuration.isLeftEditorEnabled();
            boolean isRightEditorEnabled = configuration.isRightEditorEnabled();

            createMenuItemRemoveSelection();

            if (isLeftEditorEnabled) createMenuItemSelectForCopyingToTheLeft(getTheSelectedItem());
            if (isRightEditorEnabled) createMenuItemSelectForCopyingToTheRight(getTheSelectedItem());

            createMenuItemEditLeft(getTheSelectedItem(), isLeftEditorEnabled);
            createMenuItemEditRight(getTheSelectedItem(), isRightEditorEnabled);

            createMenuItemCompareLeftAndRight(getTheSelectedItem());

            if (isLeftEditorEnabled || isRightEditorEnabled) createMenuItemSeparator();

            if (isLeftEditorEnabled) createMenuItemDeleteLeft(getTheSelectedItem());
            if (isRightEditorEnabled) createMenuItemDeleteRight(getTheSelectedItem());
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
                    changeCompareStatus(MemberCompareItem.NO_ACTION);
                }
            });
        }

        private void createMenuItemSelectForCopyingToTheLeft(MemberCompareItem compareItem) {
            menuItemSelectForCopyingToTheLeft = new MenuItem(parent, SWT.NONE);
            menuItemSelectForCopyingToTheLeft.setText(Messages.Select_for_copying_right_to_left);
            menuItemSelectForCopyingToTheLeft.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    changeCompareStatus(MemberCompareItem.LEFT_MISSING);
                }
            });

            if (compareItem != null && compareItem.getRightMemberDescription() == null) {
                menuItemSelectForCopyingToTheLeft.setEnabled(false);
            }
        }

        private void createMenuItemSelectForCopyingToTheRight(MemberCompareItem compareItem) {
            menuItemSelectForCopyingToTheRight = new MenuItem(parent, SWT.NONE);
            menuItemSelectForCopyingToTheRight.setText(Messages.Select_for_copying_left_to_right);
            menuItemSelectForCopyingToTheRight.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    changeCompareStatus(MemberCompareItem.RIGHT_MISSING);
                }
            });

            if (compareItem != null && compareItem.getLeftMemberDescription() == null) {
                menuItemSelectForCopyingToTheRight.setEnabled(false);
            }
        }

        private void createMenuItemEditLeft(MemberCompareItem compareItem, final boolean isEditable) {

            String label;
            if (isEditable) {
                label = Messages.Edit_left;
            } else {
                label = Messages.Display_left;
            }

            menuItemEditLeft = new MenuItem(parent, SWT.NONE);
            menuItemEditLeft.setText(label);
            menuItemEditLeft.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    // performEditMessageDescriptions(LEFT, isEditable);
                }
            });

            if (compareItem != null && compareItem.getLeftMemberDescription() == null) {
                menuItemEditLeft.setEnabled(false);
            }
        }

        private void createMenuItemEditRight(MemberCompareItem compareItem, final boolean isEditable) {

            String label;
            if (isEditable) {
                label = Messages.Edit_right;
            } else {
                label = Messages.Display_right;
            }

            menuItemEditRight = new MenuItem(parent, SWT.NONE);
            menuItemEditRight.setText(label);
            menuItemEditRight.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    // performEditMessageDescriptions(RIGHT, isEditable);
                }
            });

            if (compareItem != null && compareItem.getRightMemberDescription() == null) {
                menuItemEditRight.setEnabled(false);
            }
        }

        private void createMenuItemCompareLeftAndRight(MemberCompareItem compareItem) {
            menuItemCompareLeftAndRight = new MenuItem(parent, SWT.NONE);
            menuItemCompareLeftAndRight.setText(Messages.Compare_left_AND_right);
            menuItemCompareLeftAndRight.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    performOpenCompareMembersDialog(getTheSelectedItem());
                }
            });

            if (compareItem == null || compareItem.getLeftMemberDescription() == null || compareItem.getRightMemberDescription() == null) {
                menuItemCompareLeftAndRight.setEnabled(false);
            }
        }

        private void createMenuItemDeleteLeft(MemberCompareItem compareItem) {
            menuItemDeleteLeft = new MenuItem(parent, SWT.NONE);
            menuItemDeleteLeft.setText(Messages.Delete_left);
            menuItemDeleteLeft.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    // performDeleteMessageDescriptions(LEFT);
                }
            });

            if (compareItem != null && compareItem.getLeftMemberDescription() == null) {
                menuItemDeleteLeft.setEnabled(false);
            }
        }

        private void createMenuItemDeleteRight(MemberCompareItem compareItem) {
            menuItemDeleteRight = new MenuItem(parent, SWT.NONE);
            menuItemDeleteRight.setText(Messages.Delete_right);
            menuItemDeleteRight.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    // performDeleteMessageDescriptions(RIGHT);
                }
            });

            if (compareItem != null && compareItem.getRightMemberDescription() == null) {
                menuItemDeleteRight.setEnabled(false);
            }
        }

        private MemberCompareItem getTheSelectedItem() {

            MemberCompareItem[] selectedItems = getSelectedItems();
            if (selectedItems.length == 1) {
                return selectedItems[0];
            }

            return null;
        }
    }

    public void setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public void updateActionsStatusAndStatusLine() {
        updateStatusLine();
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

    private void debug(String message) {
        System.out.println(message);
    }
}
