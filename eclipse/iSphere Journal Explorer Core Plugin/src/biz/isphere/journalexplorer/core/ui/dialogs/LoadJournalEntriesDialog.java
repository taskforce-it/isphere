/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.dialogs;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.internal.DateTimeHelper;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.extension.point.IDateEdit;
import biz.isphere.core.swt.widgets.extension.point.ITimeEdit;
import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.preferences.Preferences;

public class LoadJournalEntriesDialog extends XDialog {

    private IDateEdit startingDateDateTime;
    private ITimeEdit startingTimeDateTime;
    private IDateEdit endingDateDateTime;
    private ITimeEdit endingTimeDateTime;

    private Preferences preferences;
    private Button radioBtnDefault;
    private Button radioBtnToday;
    private Button radioBtnYesterday;
    private Button chkboxRecordsOnly;

    private SelectionCriterias selectionCriterias;

    public LoadJournalEntriesDialog(Shell parentShell) {
        super(parentShell);

        this.preferences = Preferences.getInstance();
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.DisplayJournalEntriesDialog_Title);
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        Composite mainArea = new Composite(parent, SWT.NONE);
        mainArea.setLayout(new GridLayout(2, false));
        mainArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createLeftPanel(mainArea);

        createRightPanel(mainArea);

        createStatusLine(mainArea);

        loadScreenValues();

        setControlEnablement();

        return mainArea;
    }

    private void createLeftPanel(Composite mainArea) {

        Composite leftPanel = new Composite(mainArea, SWT.NONE);
        leftPanel.setLayout(new GridLayout(2, false));
        leftPanel.setLayoutData(new GridData(GridData.FILL_BOTH));

        // From date and time

        new Label(leftPanel, SWT.NONE).setText(Messages.DisplayJournalEntriesDialog_From_date_colon);

        startingDateDateTime = WidgetFactory.createDateEdit(leftPanel);
        startingDateDateTime.setLayoutData(createLayoutData(1));

        new Label(leftPanel, SWT.NONE).setText(Messages.DisplayJournalEntriesDialog_From_time_colon);

        startingTimeDateTime = WidgetFactory.createTimeEdit(leftPanel);
        startingTimeDateTime.setLayoutData(createLayoutData(1));

        // To date and time

        new Label(leftPanel, SWT.NONE).setText(Messages.DisplayJournalEntriesDialog_To_date_colon);

        endingDateDateTime = WidgetFactory.createDateEdit(leftPanel);
        endingDateDateTime.setLayoutData(createLayoutData(1));

        new Label(leftPanel, SWT.NONE).setText(Messages.DisplayJournalEntriesDialog_To_time_colon);

        endingTimeDateTime = WidgetFactory.createTimeEdit(leftPanel);
        endingTimeDateTime.setLayoutData(createLayoutData(1));
    }

    private void createRightPanel(Composite mainArea) {

        Composite rightPanel = new Composite(mainArea, SWT.NONE);
        rightPanel.setLayout(new GridLayout(1, false));
        rightPanel.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

        Group grpDatePresets = new Group(rightPanel, SWT.NONE);
        grpDatePresets.setLayout(new GridLayout(1, false));
        grpDatePresets.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        grpDatePresets.setText(Messages.DisplayJournalEntriesDialog_Fast_date_presets);

        radioBtnDefault = WidgetFactory.createRadioButton(grpDatePresets, Messages.DisplayJournalEntriesDialog_Time_Last_used_values);
        radioBtnDefault.addSelectionListener(new TimeRangeSelectionListener());

        radioBtnToday = WidgetFactory.createRadioButton(grpDatePresets, Messages.DisplayJournalEntriesDialog_Time_Today);
        radioBtnToday.addSelectionListener(new TimeRangeSelectionListener());

        radioBtnYesterday = WidgetFactory.createRadioButton(grpDatePresets, Messages.DisplayJournalEntriesDialog_Time_Yesterday);
        radioBtnYesterday.addSelectionListener(new TimeRangeSelectionListener());

        // WidgetFactory.createLineFiller(rightPanel);

        chkboxRecordsOnly = WidgetFactory.createCheckbox(rightPanel, Messages.DisplayJournalEntriesDialog_Time_Record_entries_only);
    }

    @Override
    protected void okPressed() {

        selectionCriterias = new SelectionCriterias();

        Calendar calendar;

        calendar = getTimestamp(startingDateDateTime, startingTimeDateTime);
        preferences.setStartingDate(calendar);
        selectionCriterias.setStartDate(calendar.getTime());

        calendar = getTimestamp(endingDateDateTime, endingTimeDateTime);
        preferences.setEndingDate(calendar);
        selectionCriterias.setEndDate(calendar.getTime());

        boolean recordsOnly = chkboxRecordsOnly.getSelection();
        preferences.setRecordsOnly(recordsOnly);
        selectionCriterias.setRecordsOnly(recordsOnly);

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

        loadDateScreenValues();

        chkboxRecordsOnly.setSelection(preferences.isRecordsOnly());
    }

    private void loadDateScreenValues() {

        Calendar startOfDay = preferences.getStartingDate();
        setStartingDate(startOfDay);
        setStartingTime(startOfDay);

        Calendar endOfDay = preferences.getEndingDate();
        setEndingDate(endOfDay);
        setEndingTime(endOfDay);

        radioBtnDefault.setSelection(true);
    }

    private void setDateScreenvaluesToToday() {

        Calendar startOfDay = DateTimeHelper.getStartOfDay();
        setStartingDate(startOfDay);
        setStartingTime(startOfDay);

        Calendar endOfDay = DateTimeHelper.getEndOfDay();
        setEndingDate(endOfDay);
        setEndingTime(endOfDay);
    }

    private void setDateScreenvaluesToYesterday() {

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
        return new Point(370, 250);
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
                loadDateScreenValues();
            } else if (label.equals(Messages.DisplayJournalEntriesDialog_Time_Today)) {
                setDateScreenvaluesToToday();
            } else {
                setDateScreenvaluesToYesterday();
            }
        }

    }

    public class SelectionCriterias {

        private Date startDate;
        private Date endDate;
        private boolean recordsOnly;
        int maxItemsToRetrieve;

        public SelectionCriterias() {
            this(null, null, false, Preferences.getInstance().getMaximumNumberOfRowsToFetch());
        }

        public SelectionCriterias(Date startDate, Date endDate, boolean recordsOnly, int maxItemsToRetrieve) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.recordsOnly = recordsOnly;
            this.maxItemsToRetrieve = maxItemsToRetrieve;
        }

        public Date getStartDate() {
            return startDate;
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }

        public Date getEndDate() {
            return endDate;
        }

        public void setEndDate(Date endDate) {
            this.endDate = endDate;
        }

        public boolean isRecordsOnly() {
            return recordsOnly;
        }

        public void setRecordsOnly(boolean recordsOnly) {
            this.recordsOnly = recordsOnly;
        }

        public int getMaxItemsToRetrieve() {
            return maxItemsToRetrieve;
        }

        public void setMaxItemsToRetrieve(int maxItemsToRetrieve) {
            this.maxItemsToRetrieve = maxItemsToRetrieve;
        }
    }
}
