/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.preferencepages;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import biz.isphere.base.internal.IntHelper;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.jobtraceexplorer.core.ISphereJobTraceExplorerCorePlugin;
import biz.isphere.jobtraceexplorer.core.Messages;
import biz.isphere.jobtraceexplorer.core.preferences.Preferences;

public class JobTraceExplorerPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private int maxNumRowsToFetch;

    private biz.isphere.jobtraceexplorer.core.preferences.Preferences preferences;

    private ColorSelector buttonAttributesColor;
    private ColorSelector buttonProcedureColor;
    private ColorSelector buttonHiddenProceduresColor;

    private Group groupLimits;
    private Text textMaxNumRowsToFetch;
    private Text textSQLWhereNoIBMData;

    public JobTraceExplorerPreferencePage() {
        super();

        this.preferences = Preferences.getInstance();

        setPreferenceStore(ISphereJobTraceExplorerCorePlugin.getDefault().getPreferenceStore());
        getPreferenceStore();
    }

    public void init(IWorkbench workbench) {
    }

    @Override
    protected Control createContents(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));

        createGroupColors(container);
        createGroupLimits(container);
        createGroupDefaults(container);

        setScreenToValues();

        return container;
    }

    private void createGroupColors(Composite parent) {

        Group groupColors = new Group(parent, SWT.NONE);
        groupColors.setText(Messages.GroupLabel_Colors);
        groupColors.setLayout(new GridLayout(2, false));
        groupColors.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        buttonAttributesColor = createColorSelector(groupColors, Messages.ColorLabel_HighlighValues);
        buttonAttributesColor.getButton().setToolTipText(Messages.ColorTooltip_HighlighValues);
        buttonProcedureColor = createColorSelector(groupColors, Messages.ColorLabel_HighlighProcedures);
        buttonProcedureColor.getButton().setToolTipText(Messages.ColorTooltip_HighlighProcedures);
        buttonHiddenProceduresColor = createColorSelector(groupColors, Messages.ColorLabel_HighlighHiddenProcedures);
        buttonHiddenProceduresColor.getButton().setToolTipText(Messages.ColorTooltip_HighlighHiddenProcedures);
    }

    private ColorSelector createColorSelector(Group parent, String label) {

        new Label(parent, SWT.NONE).setText(label);
        ColorSelector colorSelector = WidgetFactory.createColorSelector(parent);

        return colorSelector;
    }

    private void createGroupDefaults(Composite parent) {

        Label labelExcludeIBMClause = new Label(parent, SWT.NONE);
        labelExcludeIBMClause.setLayoutData(getLayoutData(1));
        labelExcludeIBMClause.setText(Messages.ButtonLabel_Exclude_IBM_data_SQL_WHERE_clause);
        labelExcludeIBMClause.setToolTipText(Messages.ButtonTooltip_Exclude_IBM_data_SQL_WHERE_clause);

        textSQLWhereNoIBMData = WidgetFactory.createMultilineText(parent, true, true);
        textSQLWhereNoIBMData.setToolTipText(Messages.ButtonTooltip_Exclude_IBM_data_SQL_WHERE_clause);
        textSQLWhereNoIBMData.setLayoutData(getLayoutData(1, false, 200, 250));
    }

    private void createGroupLimits(Composite parent) {

        groupLimits = new Group(parent, SWT.NONE);
        groupLimits.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        groupLimits.setLayout(new GridLayout(2, false));
        groupLimits.setText(Messages.GroupLabel_Limitation_Properties);

        Label labelMaxNumRowsToFetch = new Label(groupLimits, SWT.NONE);
        labelMaxNumRowsToFetch.setText(Messages.ButtonLabel_Maximum_number_of_rows_to_fetch);
        labelMaxNumRowsToFetch.setToolTipText(Messages.ButtonTooltip_Maximum_number_of_rows_to_fetch_tooltip);

        textMaxNumRowsToFetch = WidgetFactory.createDecimalText(groupLimits);
        textMaxNumRowsToFetch.setToolTipText(Messages.ButtonTooltip_Maximum_number_of_rows_to_fetch_tooltip);
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

        preferences.setMaximumNumberOfRowsToFetch(maxNumRowsToFetch);
        preferences.setExcludeIBMDataSQLWhereClause(textSQLWhereNoIBMData.getText());

        preferences.setColorSeverity(HighlightColor.ATTRIBUTES, buttonAttributesColor.getColorValue());
        preferences.setColorSeverity(HighlightColor.PROCEDURES, buttonProcedureColor.getColorValue());
        preferences.setColorSeverity(HighlightColor.HIDDEN_PROCEDURES, buttonHiddenProceduresColor.getColorValue());
    }

    protected void setScreenToValues() {

        maxNumRowsToFetch = preferences.getMaximumNumberOfRowsToFetch();
        textSQLWhereNoIBMData.setText(preferences.getExcludeIBMDataSQLWhereClause());

        buttonAttributesColor.setColorValue(preferences.getColorSeverity(HighlightColor.ATTRIBUTES).getRGB());
        buttonProcedureColor.setColorValue(preferences.getColorSeverity(HighlightColor.PROCEDURES).getRGB());
        buttonHiddenProceduresColor.setColorValue(preferences.getColorSeverity(HighlightColor.HIDDEN_PROCEDURES).getRGB());

        setScreenValues();
    }

    protected void setScreenToDefaultValues() {

        maxNumRowsToFetch = preferences.getInitialMaximumNumberOfRowsToFetch();
        textSQLWhereNoIBMData.setText(preferences.getInitialExcludeIBMDataSQLWhereClause());

        buttonAttributesColor.setColorValue(preferences.getDefaultColorSeverity(HighlightColor.ATTRIBUTES));
        buttonProcedureColor.setColorValue(preferences.getDefaultColorSeverity(HighlightColor.PROCEDURES));
        buttonHiddenProceduresColor.setColorValue(preferences.getDefaultColorSeverity(HighlightColor.HIDDEN_PROCEDURES));

        setScreenValues();
    }

    protected void setScreenValues() {

        textMaxNumRowsToFetch.setText(Integer.toString(maxNumRowsToFetch));

        setControlsEnablement();
    }

    private GridData getLayoutData(int horizontalSpan) {
        return getLayoutData(horizontalSpan, false, SWT.DEFAULT, SWT.DEFAULT);
    }

    private GridData getLayoutData(int horizontalSpan, boolean grabVerticalSpace, int widthHint, int heightHint) {

        GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, grabVerticalSpace, horizontalSpan, 1);
        gridData.widthHint = widthHint;
        gridData.heightHint = heightHint;

        return gridData;
    }

    private void setControlsEnablement() {

    }
}
