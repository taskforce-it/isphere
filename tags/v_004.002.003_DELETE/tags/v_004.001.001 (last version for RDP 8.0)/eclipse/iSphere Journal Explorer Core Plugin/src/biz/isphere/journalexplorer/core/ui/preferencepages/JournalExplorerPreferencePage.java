/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.preferencepages;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import biz.isphere.base.internal.IntHelper;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.preferences.Preferences;
import biz.isphere.journalexplorer.core.ui.model.JournalEntryAppearanceAttributes;
import biz.isphere.journalexplorer.core.ui.widgets.JournalEntryAppearanceAttributesEditor;

public class JournalExplorerPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private JournalEntryAppearanceAttributesEditor editor;
    private JournalEntryAppearanceAttributes[] columns;
    private String defaultDate;
    private int maxNumRowsToFetch;
    private int bufferSize;
    private boolean exportColumnHeadings;

    private Preferences preferences;

    private Button checkboxEnableColoring;
    private Group groupColors;
    private Group groupDefaults;
    private Group groupLimits;
    private Group groupExcelExport;
    private Combo comboDefaultDate;
    private Text textMaxNumRowsToFetch;
    private Combo comboBufferSize;
    private Button chkboxEportColumnHeadings;

    public JournalExplorerPreferencePage() {
        super();

        this.preferences = Preferences.getInstance();

        setPreferenceStore(ISphereJournalExplorerCorePlugin.getDefault().getPreferenceStore());
        getPreferenceStore();
    }

    public void init(IWorkbench workbench) {
    }

    @Override
    protected Control createContents(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));

        createGroupColors(container);
        createGroupDefaults(container);
        createGroupLimits(container);
        createGroupExcelExport(container);

        setScreenToValues();

        return container;
    }

    private void createGroupColors(Composite container) {

        checkboxEnableColoring = WidgetFactory.createCheckbox(container);
        checkboxEnableColoring.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        checkboxEnableColoring.setText(Messages.Enable_coloring);
        checkboxEnableColoring.setToolTipText(Messages.Enable_coloring_tooltip);
        checkboxEnableColoring.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                validateAll();
                setControlsEnablement();
            }
        });

        groupColors = new Group(container, SWT.NONE);
        groupColors.setLayoutData(new GridData(GridData.FILL_BOTH));
        groupColors.setLayout(new GridLayout(1, false));
        groupColors.setText(Messages.Colors);

        editor = new JournalEntryAppearanceAttributesEditor(groupColors);
        editor.setLayoutData(new GridData(GridData.FILL, SWT.FILL, true, true));
    }

    private void createGroupDefaults(Composite container) {

        groupDefaults = new Group(container, SWT.NONE);
        groupDefaults.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        groupDefaults.setLayout(new GridLayout(2, false));
        groupDefaults.setText(Messages.Default_Properties);

        Label labelDefaultDate = new Label(groupDefaults, SWT.NONE);
        labelDefaultDate.setText(Messages.Default_date);
        labelDefaultDate.setToolTipText(Messages.Default_date_tooltip);

        comboDefaultDate = WidgetFactory.createReadOnlyCombo(groupDefaults);
        comboDefaultDate.setToolTipText(Messages.Default_date_tooltip);
        comboDefaultDate.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        comboDefaultDate.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent event) {
                defaultDate = comboDefaultDate.getText();
            }
        });
        comboDefaultDate.setItems(preferences.getDefaultDateLabels());
    }

    private void createGroupLimits(Composite container) {

        groupLimits = new Group(container, SWT.NONE);
        groupLimits.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        groupLimits.setLayout(new GridLayout(2, false));
        groupLimits.setText(Messages.Limitation_Properties);

        Label labelMaxNumRowsToFetch = new Label(groupLimits, SWT.NONE);
        labelMaxNumRowsToFetch.setText(Messages.Maximum_number_of_rows_to_fetch);
        labelMaxNumRowsToFetch.setToolTipText(Messages.Maximum_number_of_rows_to_fetch_tooltip);

        textMaxNumRowsToFetch = WidgetFactory.createDecimalText(groupLimits);
        textMaxNumRowsToFetch.setToolTipText(Messages.Maximum_number_of_rows_to_fetch_tooltip);
        textMaxNumRowsToFetch.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        textMaxNumRowsToFetch.setTextLimit(5);
        textMaxNumRowsToFetch.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent event) {
                maxNumRowsToFetch = IntHelper.tryParseInt(textMaxNumRowsToFetch.getText(), preferences.getInitialMaximumNumberOfRowsToFetch());
            }
        });

        Label labelBufferSize = new Label(groupLimits, SWT.NONE);
        labelBufferSize.setText(Messages.Buffer_size);
        labelBufferSize.setToolTipText(Messages.Buffer_size_tooltip);

        comboBufferSize = WidgetFactory.createDecimalCombo(groupLimits);
        comboBufferSize.setToolTipText(Messages.Buffer_size_tooltip);
        comboBufferSize.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        comboBufferSize.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent event) {
                bufferSize = IntHelper.tryParseInt(comboBufferSize.getText(), -1);
                if (bufferSize == -1) {
                    bufferSize = (int)Math.min(IntHelper.convertLabelToStorageSize(comboBufferSize.getText()), Integer.MAX_VALUE);
                }
            }
        });
        comboBufferSize.setItems(preferences.getRetrieveJournalEntriesBufferSizeLabels());
    }

    private void createGroupExcelExport(Composite container) {

        groupExcelExport = new Group(container, SWT.NONE);
        groupExcelExport.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        groupExcelExport.setLayout(new GridLayout(1, false));
        groupExcelExport.setText(Messages.Excel_Export);

        chkboxEportColumnHeadings = WidgetFactory.createCheckbox(groupExcelExport, Messages.Export_Export_column_headings);
        chkboxEportColumnHeadings.setToolTipText(Messages.Export_Export_column_headings_tooltip);
        chkboxEportColumnHeadings.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                exportColumnHeadings = chkboxEportColumnHeadings.getSelection();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });
    }

    @Override
    protected void performApply() {
        setStoreToValues();
        super.performApply();
    }

    @Override
    protected void performDefaults() {
        setScreenToDefaultValues();
        super.performDefaults();
    }

    @Override
    public boolean performOk() {
        setStoreToValues();
        return super.performOk();
    }

    protected void setStoreToValues() {

        preferences.setColoringEnabled(checkboxEnableColoring.getSelection());

        columns = editor.getInput();

        preferences.setSortedJournalEntryAppearanceAttributes(columns);
        preferences.setDefaultDateGUILabel(defaultDate);
        preferences.setMaximumNumberOfRowsToFetch(maxNumRowsToFetch);
        preferences.setRetrieveJournalEntriesBufferSize(bufferSize);
        preferences.setExportColumnHeadings(exportColumnHeadings);
    }

    protected void setScreenToValues() {

        checkboxEnableColoring.setSelection(preferences.isColoringEnabled());

        columns = preferences.getSortedJournalEntryAppearancesAttributes();
        defaultDate = preferences.getDefaultDateGUILabel();
        maxNumRowsToFetch = preferences.getMaximumNumberOfRowsToFetch();
        bufferSize = preferences.getRetrieveJournalEntriesBufferSize();
        exportColumnHeadings = preferences.isExportColumnHeadings();

        setScreenValues();
    }

    protected void setScreenToDefaultValues() {

        checkboxEnableColoring.setSelection(preferences.getInitialColoringEnabled());

        columns = preferences.getInitialSortedJournalEntryAppearanceAttributes();
        defaultDate = preferences.getInitialDefaultDateGUILabel();
        maxNumRowsToFetch = preferences.getInitialMaximumNumberOfRowsToFetch();
        bufferSize = preferences.getInitialRetrieveJournalEntriesBufferSize();
        exportColumnHeadings = preferences.getInitialExportColumnHeadings();

        setScreenValues();
    }

    protected void setScreenValues() {

        editor.setInput(columns);
        comboDefaultDate.setText(defaultDate);
        textMaxNumRowsToFetch.setText(Integer.toString(maxNumRowsToFetch));

        String bufferSizeLabel = IntHelper.convertStorageSizeToLabel(bufferSize);
        if (bufferSizeLabel == null) {
            bufferSizeLabel = Long.toString(bufferSize);
        }
        comboBufferSize.setText(bufferSizeLabel);
        chkboxEportColumnHeadings.setSelection(exportColumnHeadings);

        setControlsEnablement();
    }

    private void setControlsEnablement() {

        if (checkboxEnableColoring.getSelection()) {
            setCompositeEnablement(groupColors, true);
        } else {
            setCompositeEnablement(groupColors, false);
        }
    }

    private void setCompositeEnablement(Composite composite, boolean enabled) {

        composite.setEnabled(enabled);

        for (Control control : composite.getChildren()) {
            control.setEnabled(enabled);
        }
    }

    private boolean validateAll() {

        return clearError();
    }

    private boolean clearError() {

        setErrorMessage(null);
        setValid(true);

        return true;
    }

}
