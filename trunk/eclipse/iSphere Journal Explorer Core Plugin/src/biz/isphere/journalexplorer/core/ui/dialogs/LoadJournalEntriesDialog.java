/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.dialogs;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.internal.DateTimeHelper;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.extension.point.IDateEdit;
import biz.isphere.core.swt.widgets.extension.point.ITimeEdit;
import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.internals.QualifiedName;
import biz.isphere.journalexplorer.core.model.JournalEntryType;
import biz.isphere.journalexplorer.core.preferences.Preferences;
import biz.isphere.journalexplorer.rse.handlers.contributions.extension.point.ISelectedFile;

public class LoadJournalEntriesDialog extends XDialog {

    private static final String DEFAULT_DATE = "DEFAULT_DATE";
    private static final String STARTING_DATE = "STARTING_DATE";
    private static final String ENDING_DATE = "ENDING_DATE";
    private static final String SHOW_RECORDS_ONLY = "SHOW_RECORDS_ONLY";

    private static final String TABLE_VIEWER_PROPERTY_SELECTED = "SELECTED";
    private static final String TABLE_VIEWER_PROPERTY_TEXT = "TEXT";

    public static int WIDTH_SELECTED = 30;
    public static int WIDTH_TEXT = 500;

    private List<ISelectedFile> files;

    private IDateEdit startingDateDateTime;
    private ITimeEdit startingTimeDateTime;
    private IDateEdit endingDateDateTime;
    private ITimeEdit endingTimeDateTime;

    private Button radioBtnLastUsedValues;
    private Button radioBtnToday;
    private Button radioBtnYesterday;
    private Button chkboxRecordsOnly;

    private TableViewer tableViewer;
    private List<SelectableJournalEntryType> journalEntryTypes;
    private Label lblCmdNone;
    private Label lblCmdAll;
    private Label lblCmdInsert;
    private Label lblCmdUpdate;
    private Label lblCmdDelete;

    private SelectionCriterias selectionCriterias;

    public LoadJournalEntriesDialog(Shell parentShell, List<ISelectedFile> files) {
        super(parentShell);

        this.files = files;

        journalEntryTypes = new LinkedList<SelectableJournalEntryType>();
        journalEntryTypes.add(new SelectableJournalEntryType(JournalEntryType.PT, true));
        journalEntryTypes.add(new SelectableJournalEntryType(JournalEntryType.PX, true));
        journalEntryTypes.add(new SelectableJournalEntryType(JournalEntryType.UB, true));
        journalEntryTypes.add(new SelectableJournalEntryType(JournalEntryType.UP, true));
        journalEntryTypes.add(new SelectableJournalEntryType(JournalEntryType.BR, true));
        journalEntryTypes.add(new SelectableJournalEntryType(JournalEntryType.UR, true));
        journalEntryTypes.add(new SelectableJournalEntryType(JournalEntryType.DL, true));
        journalEntryTypes.add(new SelectableJournalEntryType(JournalEntryType.DR, true));
        journalEntryTypes.add(new SelectableJournalEntryType(JournalEntryType.IL, true));
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.DisplayJournalEntriesDialog_Title + addJournaledObjects(files));
    }

    private String addJournaledObjects(List<ISelectedFile> files) {

        if (files.size() != 1) {
            return ""; //$NON-NLS-1$
        }

        ISelectedFile file = files.get(0);

        return ": " + QualifiedName.getMemberName(file.getLibrary(), file.getName(), file.getMember()); //$NON-NLS-1$
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        Composite mainArea = new Composite(parent, SWT.NONE);
        mainArea.setLayout(new GridLayout(2, false));
        mainArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createLeftPanel(mainArea);

        createRightPanel(mainArea);

        createBottomPanel(mainArea);

        createStatusLine(mainArea);

        loadScreenValues();

        setControlEnablement();

        return mainArea;
    }

    private void createLeftPanel(Composite mainArea) {

        Composite leftPanel = new Composite(mainArea, SWT.NONE);
        leftPanel.setLayout(new GridLayout(2, false));
        leftPanel.setLayoutData(new GridData());

        // From date and time

        Label lblStartingDateDateTime = new Label(leftPanel, SWT.NONE);
        lblStartingDateDateTime.setText(Messages.DisplayJournalEntriesDialog_From_date_colon);
        lblStartingDateDateTime.setToolTipText(Messages.DisplayJournalEntriesDialog_Tooltip_From_date_colon);

        startingDateDateTime = WidgetFactory.createDateEdit(leftPanel);
        startingDateDateTime.setLayoutData(createLayoutData(1));
        startingDateDateTime.setToolTipText(Messages.DisplayJournalEntriesDialog_Tooltip_From_date_colon);

        Label lblStartingTimeDateTime = new Label(leftPanel, SWT.NONE);
        lblStartingTimeDateTime.setText(Messages.DisplayJournalEntriesDialog_From_time_colon);
        lblStartingTimeDateTime.setToolTipText(Messages.DisplayJournalEntriesDialog_Tooltip_From_time_colon);

        startingTimeDateTime = WidgetFactory.createTimeEdit(leftPanel);
        startingTimeDateTime.setLayoutData(createLayoutData(1));
        startingTimeDateTime.setToolTipText(Messages.DisplayJournalEntriesDialog_Tooltip_From_time_colon);

        // To date and time

        Label lblEndingDateDateTime = new Label(leftPanel, SWT.NONE);
        lblEndingDateDateTime.setText(Messages.DisplayJournalEntriesDialog_To_date_colon);
        lblEndingDateDateTime.setToolTipText(Messages.DisplayJournalEntriesDialog_Tooltip_To_date_colon);

        endingDateDateTime = WidgetFactory.createDateEdit(leftPanel);
        endingDateDateTime.setLayoutData(createLayoutData(1));
        endingDateDateTime.setToolTipText(Messages.DisplayJournalEntriesDialog_Tooltip_To_date_colon);

        Label lblEndingTimeDateTime = new Label(leftPanel, SWT.NONE);
        lblEndingTimeDateTime.setText(Messages.DisplayJournalEntriesDialog_To_time_colon);
        lblEndingTimeDateTime.setToolTipText(Messages.DisplayJournalEntriesDialog_Tooltip_To_time_colon);

        endingTimeDateTime = WidgetFactory.createTimeEdit(leftPanel);
        endingTimeDateTime.setLayoutData(createLayoutData(1));
        endingTimeDateTime.setToolTipText(Messages.DisplayJournalEntriesDialog_Tooltip_To_time_colon);
    }

    private void createRightPanel(Composite mainArea) {

        Composite rightPanel = new Composite(mainArea, SWT.NONE);
        rightPanel.setLayout(new GridLayout(1, false));
        rightPanel.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

        Group grpDatePresets = new Group(rightPanel, SWT.NONE);
        grpDatePresets.setLayout(new GridLayout(1, false));
        grpDatePresets.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        grpDatePresets.setText(Messages.DisplayJournalEntriesDialog_Fast_date_presets);
        grpDatePresets.setToolTipText(Messages.DisplayJournalEntriesDialog_Tooltip_Fast_date_presets);

        radioBtnLastUsedValues = WidgetFactory.createRadioButton(grpDatePresets, Messages.DisplayJournalEntriesDialog_Time_Last_used_values);
        radioBtnLastUsedValues.addSelectionListener(new TimeRangeSelectionListener());

        radioBtnToday = WidgetFactory.createRadioButton(grpDatePresets, Messages.DisplayJournalEntriesDialog_Time_Today);
        radioBtnToday.addSelectionListener(new TimeRangeSelectionListener());

        radioBtnYesterday = WidgetFactory.createRadioButton(grpDatePresets, Messages.DisplayJournalEntriesDialog_Time_Yesterday);
        radioBtnYesterday.addSelectionListener(new TimeRangeSelectionListener());

        // WidgetFactory.createLineFiller(rightPanel);

        // chkboxRecordsOnly = WidgetFactory.createCheckbox(rightPanel,
        // Messages.DisplayJournalEntriesDialog_Show_Record_entries_only);
    }

    private void createBottomPanel(Composite mainArea) {

        Composite bottomPanel = new Composite(mainArea, SWT.NONE);
        bottomPanel.setLayout(new GridLayout(1, false));
        bottomPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

        chkboxRecordsOnly = WidgetFactory.createCheckbox(bottomPanel, Messages.DisplayJournalEntriesDialog_Show_Record_entries_only);
        chkboxRecordsOnly.setToolTipText(Messages.DisplayJournalEntriesDialog_Tooltip_Show_Record_entries_only);
        chkboxRecordsOnly.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent event) {
                setControlEnablement();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });

        tableViewer = new TableViewer(bottomPanel, SWT.BORDER | SWT.FULL_SELECTION);
        tableViewer.getTable().setToolTipText(Messages.DisplayJournalEntriesDialog_Tooltip_Selectable_Journal_entry_types);
        tableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
        tableViewer.getTable().setHeaderVisible(false);
        tableViewer.getTable().setLinesVisible(true);
        tableViewer.setColumnProperties(new String[] { TABLE_VIEWER_PROPERTY_SELECTED, TABLE_VIEWER_PROPERTY_TEXT });

        CellEditor[] editors = new CellEditor[] { new CheckboxCellEditor(tableViewer.getTable()) };
        tableViewer.setCellEditors(editors);

        tableViewer.setCellModifier(new JournalTypesCellModifier(tableViewer));

        new TableColumn(tableViewer.getTable(), SWT.NONE).setWidth(WIDTH_SELECTED);
        new TableColumn(tableViewer.getTable(), SWT.NONE).setWidth(WIDTH_TEXT);

        tableViewer.setLabelProvider(new JournalTypesLabelProvider());
        tableViewer.setContentProvider(new JournalTypesContentProvider());
        tableViewer.getTable().addKeyListener(new SpaceBarSelector());

        Composite cmdPanel = new Composite(bottomPanel, SWT.NONE);
        GridLayout cmdPanelLayout = new GridLayout(5, false);
        cmdPanelLayout.marginHeight = 0;
        cmdPanel.setLayout(cmdPanelLayout);
        cmdPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        lblCmdNone = new Label(cmdPanel, SWT.NONE);
        lblCmdNone.setText(Messages.DisplayJournalEntriesDialog_Label_cmd_None);
        lblCmdNone.setToolTipText(Messages.DisplayJournalEntriesDialog_Tooltip_cmd_None);
        lblCmdNone.addMouseListener(new JournalEntryTypeCmdLabelsMouseAdapter(tableViewer, false, false));

        lblCmdAll = new Label(cmdPanel, SWT.NONE);
        lblCmdAll.setText(Messages.DisplayJournalEntriesDialog_Label_cmd_All);
        lblCmdAll.setToolTipText(Messages.DisplayJournalEntriesDialog_Tooltip_cmd_All);
        lblCmdAll.addMouseListener(new JournalEntryTypeCmdLabelsMouseAdapter(tableViewer, false, true));

        lblCmdInsert = new Label(cmdPanel, SWT.NONE);
        lblCmdInsert.setText(Messages.DisplayJournalEntriesDialog_Label_cmd_Insert);
        lblCmdInsert.setToolTipText(Messages.DisplayJournalEntriesDialog_Tooltip_cmd_Insert);
        lblCmdInsert.addMouseListener(new JournalEntryTypeCmdLabelsMouseAdapter(tableViewer, true, true, JournalEntryType.PT.label(),
            JournalEntryType.PX.label()));

        lblCmdUpdate = new Label(cmdPanel, SWT.NONE);
        lblCmdUpdate.setText(Messages.DisplayJournalEntriesDialog_Label_cmd_Update);
        lblCmdUpdate.setToolTipText(Messages.DisplayJournalEntriesDialog_Tooltip_cmd_Update);
        lblCmdUpdate.addMouseListener(new JournalEntryTypeCmdLabelsMouseAdapter(tableViewer, true, true, JournalEntryType.UB.label(),
            JournalEntryType.UP.label(), JournalEntryType.BR.label(), JournalEntryType.UR.label()));

        lblCmdDelete = new Label(cmdPanel, SWT.NONE);
        lblCmdDelete.setText(Messages.DisplayJournalEntriesDialog_Label_cmd_Delete);
        lblCmdDelete.setToolTipText(Messages.DisplayJournalEntriesDialog_Tooltip_cmd_Delete);
        lblCmdDelete.addMouseListener(new JournalEntryTypeCmdLabelsMouseAdapter(tableViewer, true, true, JournalEntryType.DL.label(),
            JournalEntryType.DR.label()));

        tableViewer.setInput(journalEntryTypes.toArray(new SelectableJournalEntryType[journalEntryTypes.size()]));
    }

    @Override
    protected void okPressed() {

        selectionCriterias = new SelectionCriterias();

        Calendar startingDate = getTimestamp(startingDateDateTime, startingTimeDateTime);
        selectionCriterias.setStartDate(new java.sql.Timestamp(startingDate.getTimeInMillis()));

        Calendar endingDate = getTimestamp(endingDateDateTime, endingTimeDateTime);
        selectionCriterias.setEndDate(new java.sql.Timestamp(endingDate.getTimeInMillis()));

        boolean recordsOnly = chkboxRecordsOnly.getSelection();
        selectionCriterias.setRecordsOnly(recordsOnly);

        if (recordsOnly) {
            for (SelectableJournalEntryType journalEntryType : journalEntryTypes) {
                if (journalEntryType.isSelected()) {
                    selectionCriterias.addJournalEntryType(JournalEntryType.find(journalEntryType.getLabel()));
                }
            }
        }

        if (recordsOnly && selectionCriterias.getJournalEntryTypes().length == 0) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.Error_No_journal_entry_types_selected);
            tableViewer.getTable().setFocus();
            return;
        }

        storeScreenValues();

        super.okPressed();
    }

    public SelectionCriterias getSelectionCriterias() {
        return selectionCriterias;
    }

    private Calendar getTimestamp(IDateEdit startingDateDate, ITimeEdit startingDateTime) {

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, startingDateDate.getYear());
        calendar.set(Calendar.MONTH, startingDateDate.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH, startingDateDate.getDay());

        calendar.set(Calendar.HOUR_OF_DAY, startingDateTime.getHours());
        calendar.set(Calendar.MINUTE, startingDateTime.getMinutes());
        calendar.set(Calendar.SECOND, startingDateTime.getSeconds());
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }

    private GridData createLayoutData(int numColumns) {

        GridData gd = new GridData();
        gd.widthHint = 75;
        gd.horizontalSpan = numColumns;

        return gd;
    }

    private void loadScreenValues() {

        String defaultDate = loadValue(DEFAULT_DATE, Preferences.getInstance().getDefaultDateGUILabel());
        if (radioBtnToday.getText().equals(defaultDate)) {
            setRadioButtonSelected(radioBtnToday);
        } else if (radioBtnYesterday.getText().equals(defaultDate)) {
            setRadioButtonSelected(radioBtnYesterday);
        } else {
            setRadioButtonSelected(radioBtnLastUsedValues);
        }

        chkboxRecordsOnly.setSelection(loadBooleanValue(SHOW_RECORDS_ONLY, true));

        for (SelectableJournalEntryType journalEntryType : journalEntryTypes) {
            boolean isSelected = loadBooleanValue("JOURNAL_ENTRY_TYPE_" + journalEntryType.getLabel(), true);
            journalEntryType.setSelected(isSelected);
        }

        tableViewer.refresh(true);
    }

    private void setRadioButtonSelected(Button button) {

        button.setSelection(true);
        button.notifyListeners(SWT.Selection, new Event());
    }

    private void storeScreenValues() {

        if (radioBtnToday.getSelection()) {
            storeValue(DEFAULT_DATE, radioBtnToday.getText());
        } else if (radioBtnYesterday.getSelection()) {
            storeValue(DEFAULT_DATE, radioBtnYesterday.getText());
        } else {
            storeValue(DEFAULT_DATE, radioBtnLastUsedValues.getText());
        }

        Calendar startingDate = getTimestamp(startingDateDateTime, startingTimeDateTime);
        Calendar endingDate = getTimestamp(endingDateDateTime, endingTimeDateTime);
        boolean recordsOnly = chkboxRecordsOnly.getSelection();

        storeValue(STARTING_DATE, startingDate.getTime());
        storeValue(ENDING_DATE, endingDate.getTime());
        storeValue(SHOW_RECORDS_ONLY, recordsOnly);

        for (SelectableJournalEntryType journalEntryType : journalEntryTypes) {
            storeValue("JOURNAL_ENTRY_TYPE_" + journalEntryType.getLabel(), journalEntryType.isSelected());
        }
    }

    private void setDateScreenValuesToLastUsedValues() {

        Calendar startOfDay = Calendar.getInstance();
        startOfDay.setTime(loadDateValue(STARTING_DATE, DateTimeHelper.getStartOfDay().getTime()));

        setStartingDate(startOfDay);
        setStartingTime(startOfDay);

        Calendar endOfDay = Calendar.getInstance();
        endOfDay.setTime(loadDateValue(ENDING_DATE, DateTimeHelper.getEndOfDay().getTime()));

        setEndingDate(endOfDay);
        setEndingTime(endOfDay);
    }

    private void setDateScreenValuesToToday() {

        Calendar startOfDay = DateTimeHelper.getStartOfDay();
        setStartingDate(startOfDay);
        setStartingTime(startOfDay);

        Calendar endOfDay = DateTimeHelper.getEndOfDay();
        setEndingDate(endOfDay);
        setEndingTime(endOfDay);
    }

    private void setDateScreenValuesToYesterday() {

        Calendar startOfDay = DateTimeHelper.getStartOfDay(-1);
        setStartingDate(startOfDay);
        setStartingTime(startOfDay);

        Calendar endOfDay = DateTimeHelper.getEndOfDay(-1);
        setEndingDate(endOfDay);
        setEndingTime(endOfDay);
    }

    private void setStartingDate(Calendar calendar) {

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        startingDateDateTime.setDate(year, month, day);
    }

    private void setStartingTime(Calendar calendar) {

        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);

        startingTimeDateTime.setTime(hours, minutes, seconds);
    }

    private void setEndingDate(Calendar calendar) {

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        endingDateDateTime.setDate(year, month, day);
    }

    private void setEndingTime(Calendar calendar) {

        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);

        endingTimeDateTime.setTime(hours, minutes, seconds);
    }

    private void setControlEnablement() {

        if (chkboxRecordsOnly.getSelection()) {
            tableViewer.getTable().setEnabled(true);
            lblCmdNone.setEnabled(true);
            lblCmdAll.setEnabled(true);
            lblCmdInsert.setEnabled(true);
            lblCmdUpdate.setEnabled(true);
            lblCmdDelete.setEnabled(true);
        } else {
            tableViewer.getTable().setEnabled(false);
            lblCmdNone.setEnabled(false);
            lblCmdAll.setEnabled(false);
            lblCmdInsert.setEnabled(false);
            lblCmdUpdate.setEnabled(false);
            lblCmdDelete.setEnabled(false);
        }
    }

    /**
     * Overridden make this dialog resizable {@link XDialog}.
     */
    @Override
    protected boolean isResizable() {
        return true;
    }

    /**
     * Overridden to provide a default size to {@link XDialog}.
     */
    @Override
    protected Point getDefaultSize() {
        return new Point(570, 480);
    }

    /**
     * Overridden to ensure a minimum dialog size.
     */
    @Override
    protected Point getInitialSize() {

        Point size = super.getInitialSize();

        if (size.x < 310) {
            size.x = 310;
        }

        if (size.y < 380) {
            size.y = 380;
        }

        return size;
    }

    /**
     * Overridden to let {@link XDialog} store the state of this dialog in a
     * separate section of the dialog settings file.
     */
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return super.getDialogBoundsSettings(ISphereJournalExplorerCorePlugin.getDefault().getDialogSettings());
    }

    private class TimeRangeSelectionListener implements SelectionListener {

        public void widgetDefaultSelected(SelectionEvent event) {
            widgetSelected(event);

        }

        public void widgetSelected(SelectionEvent event) {

            Button button = (Button)event.getSource();
            String label = button.getText();

            if (!button.getSelection()) {
                return;
            }

            if (label.equals(Messages.DisplayJournalEntriesDialog_Time_Last_used_values)) {
                setDateScreenValuesToLastUsedValues();
            } else if (label.equals(Messages.DisplayJournalEntriesDialog_Time_Today)) {
                setDateScreenValuesToToday();
            } else {
                setDateScreenValuesToYesterday();
            }
        }

    }

    public class SelectionCriterias {

        private java.sql.Timestamp startDate;
        private java.sql.Timestamp endDate;
        private boolean isRecordsOnly;
        private Set<JournalEntryType> journalEntryTypes;
        int maxItemsToRetrieve;

        public SelectionCriterias() {
            this(null, null, false, Preferences.getInstance().getMaximumNumberOfRowsToFetch());
        }

        public SelectionCriterias(java.sql.Timestamp startDate, java.sql.Timestamp endDate, boolean recordsOnly, int maxItemsToRetrieve) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.isRecordsOnly = recordsOnly;
            this.maxItemsToRetrieve = maxItemsToRetrieve;
            this.journalEntryTypes = new HashSet<JournalEntryType>();
        }

        public java.sql.Timestamp getStartDate() {
            return startDate;
        }

        public void setStartDate(java.sql.Timestamp startDate) {
            this.startDate = startDate;
        }

        public java.sql.Timestamp getEndDate() {
            return endDate;
        }

        public void setEndDate(java.sql.Timestamp endDate) {
            this.endDate = endDate;
        }

        public boolean isRecordsOnly() {
            return isRecordsOnly;
        }

        public void setRecordsOnly(boolean recordsOnly) {
            this.isRecordsOnly = recordsOnly;
        }

        public JournalEntryType[] getJournalEntryTypes() {
            return journalEntryTypes.toArray(new JournalEntryType[journalEntryTypes.size()]);
        }

        public void addJournalEntryType(JournalEntryType journalEntryType) {
            journalEntryTypes.add(journalEntryType);
        }

        public int getMaxItemsToRetrieve() {
            return maxItemsToRetrieve;
        }

        public void setMaxItemsToRetrieve(int maxItemsToRetrieve) {
            this.maxItemsToRetrieve = maxItemsToRetrieve;
        }
    }

    private class JournalTypesContentProvider implements IStructuredContentProvider {

        private SelectableJournalEntryType[] inputData;

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

            if (newInput != null) {
                inputData = (SelectableJournalEntryType[])newInput;
            } else {
                inputData = null;
            }
        }

        public Object[] getElements(Object inputData) {
            return (SelectableJournalEntryType[])inputData;
        }

    }

    private class JournalTypesLabelProvider extends LabelProvider implements ITableLabelProvider {

        public Image getColumnImage(Object object, int index) {

            switch (index) {
            case 0:
                if (((SelectableJournalEntryType)object).isSelected()) {
                    return ISphereJournalExplorerCorePlugin.getDefault().getImage(ISphereJournalExplorerCorePlugin.IMAGE_CHECKED);
                } else {
                    return ISphereJournalExplorerCorePlugin.getDefault().getImage(ISphereJournalExplorerCorePlugin.IMAGE_UNCHECKED);
                }

            case 1:
                return null;

            default:
                return null;
            }
        }

        public String getColumnText(Object object, int index) {

            switch (index) {
            case 0:
                // if (((SelectableJournalEntryType)object).isSelected()) {
                // return "X";
                // } else {
                // return "";
                // }
                return null;

            case 1:
                return ((SelectableJournalEntryType)object).getText();

            default:
                return null;
            }
        }
    }

    private class JournalTypesCellModifier implements ICellModifier {

        private TableViewer tableViewer;

        public JournalTypesCellModifier(TableViewer tableViewer) {
            this.tableViewer = tableViewer;
        }

        public boolean canModify(Object element, String property) {
            return true;
        }

        public Object getValue(Object element, String property) {

            SelectableJournalEntryType currentValue = (SelectableJournalEntryType)element;

            return currentValue.isSelected();
        }

        public void modify(Object element, String property, Object value) {

            TableItem item = (TableItem)element;
            SelectableJournalEntryType currentValue = (SelectableJournalEntryType)item.getData();
            currentValue.setSelected(!currentValue.isSelected());

            tableViewer.update(currentValue, new String[] { property });
        }

    }

    private class JournalEntryTypeCmdLabelsMouseAdapter extends MouseAdapter {

        private TableViewer tableViewer;
        private boolean allowNegation;
        private boolean isSelected;
        private Set<String> labels;

        // Silly hack for WDSCi, which is missing MouseEvent.count.
        private boolean isDoubleClick;

        public JournalEntryTypeCmdLabelsMouseAdapter(TableViewer tableViewer, boolean allowNegation, boolean isSelected, String... labels) {
            this.tableViewer = tableViewer;
            this.allowNegation = allowNegation;
            this.isSelected = isSelected;
            this.labels = new HashSet<String>(Arrays.asList(labels));
        }

        @Override
        public void mouseDown(MouseEvent arg0) {
            isDoubleClick = false;
        }

        @Override
        public void mouseDoubleClick(MouseEvent event) {

            isDoubleClick = true;

            // Negate default behaviour on double-click, no modifiers
            if (allowNegation && event.stateMask == 0) {
                performOperation(!isSelected);
            }
        }

        @Override
        public void mouseUp(MouseEvent event) {

            try {

                if (isDoubleClick) {
                    return;
                }

                boolean newSelectedState = isSelected;

                // Negate default behaviour
                // MOD1 = CTRL on most platforms, COMMAND on Mac
                // MOD3 = ALT on most platforms
                if (allowNegation && ((event.stateMask & SWT.MOD1) == SWT.MOD1 | (event.stateMask & SWT.MOD3) == SWT.MOD3)) {
                    newSelectedState = !isSelected;
                }

                performOperation(newSelectedState);

            } finally {
                isDoubleClick = false;
            }
        }

        private void performOperation(boolean newSelectedState) {
            for (SelectableJournalEntryType journalEntryType : journalEntryTypes) {
                if (labels.size() == 0 || labels.contains(journalEntryType.getLabel())) {
                    journalEntryType.setSelected(newSelectedState);
                }
            }
            tableViewer.refresh(true);
        }
    }

    private class SelectableJournalEntryType {

        private JournalEntryType journalEntryType;
        private boolean selected;

        public SelectableJournalEntryType(JournalEntryType journalEntryType, boolean selected) {
            this.journalEntryType = journalEntryType;
            this.selected = selected;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public String getLabel() {
            return journalEntryType.label();
        }

        public String getText() {
            return journalEntryType.toString();
        }

    }

    private class SpaceBarSelector extends KeyAdapter {

        public void keyPressed(KeyEvent event) {
            if (isSpacebar(event)) {
                Table table = (Table)event.getSource();
                int itemIndex = table.getSelectionIndex();
                if (itemIndex >= 0) {
                    TableItem tableItem = table.getItem(itemIndex);
                    SelectableJournalEntryType journalEntry = (SelectableJournalEntryType)tableItem.getData();
                    journalEntry.setSelected(!journalEntry.isSelected());
                    tableViewer.update(journalEntry, new String[] { TABLE_VIEWER_PROPERTY_SELECTED });
                }
            }
        }

        private boolean isSpacebar(KeyEvent event) {
            if (event.character == ' ' && event.stateMask == 0) {
                return true;
            }
            return false;
        }
    }
}
