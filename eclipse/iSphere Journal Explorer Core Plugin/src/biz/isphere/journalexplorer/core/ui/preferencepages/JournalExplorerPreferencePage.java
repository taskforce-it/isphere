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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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
    private int maxNumRowsToFetch;

    private Preferences preferences;

    private Button checkboxEnableColoring;
    private Group groupColors;
    private Group groupSqlAttributes;
    private Text textMaxNumRowsToFetch;

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
        createGroupSqlAttributes(container);

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

    private void createGroupSqlAttributes(Composite container) {

        groupSqlAttributes = new Group(container, SWT.NONE);
        groupSqlAttributes.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        groupSqlAttributes.setLayout(new GridLayout(2, false));
        groupSqlAttributes.setText(Messages.Sql_Properties);

        Label labelMaxNumRowsToFetch = new Label(groupSqlAttributes, SWT.NONE);
        labelMaxNumRowsToFetch.setText(Messages.Maximum_number_of_rows_to_fetch);

        textMaxNumRowsToFetch = WidgetFactory.createDecimalText(groupSqlAttributes);
        textMaxNumRowsToFetch.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        textMaxNumRowsToFetch.setTextLimit(5);
        textMaxNumRowsToFetch.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent event) {
                maxNumRowsToFetch = IntHelper.tryParseInt(textMaxNumRowsToFetch.getText(), preferences.getInitialMaximumNumberOfRowsToFetch());
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
        preferences.setMaximumNumberOfRowsToFetch(maxNumRowsToFetch);
    }

    protected void setScreenToValues() {

        checkboxEnableColoring.setSelection(preferences.isColoringEnabled());

        columns = preferences.getSortedJournalEntryAppearancesAttributes();
        maxNumRowsToFetch = preferences.getMaximumNumberOfRowsToFetch();

        setScreenValues();
    }

    protected void setScreenToDefaultValues() {

        checkboxEnableColoring.setSelection(preferences.getInitialColoringEnabled());

        columns = preferences.getInitialSortedJournalEntryAppearanceAttributes();
        maxNumRowsToFetch = preferences.getInitialMaximumNumberOfRowsToFetch();

        setScreenValues();
    }

    protected void setScreenValues() {

        editor.setInput(columns);
        textMaxNumRowsToFetch.setText(Integer.toString(maxNumRowsToFetch));

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
