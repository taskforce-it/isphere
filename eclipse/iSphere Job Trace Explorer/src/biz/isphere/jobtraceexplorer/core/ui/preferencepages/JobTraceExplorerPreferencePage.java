/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.preferencepages;

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

        createGroupLimits(container);

        setScreenToValues();

        return container;
    }

    private void createGroupLimits(Composite container) {

        groupLimits = new Group(container, SWT.NONE);
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

        Label labelExcludeIBMClause = new Label(container, SWT.NONE);
        labelExcludeIBMClause.setLayoutData(getLayoutData(1));
        labelExcludeIBMClause.setText(Messages.ButtonLabel_Exclude_IBM_data_SQL_WHERE_clause);
        labelExcludeIBMClause.setToolTipText(Messages.ButtonTooltip_Exclude_IBM_data_SQL_WHERE_clause);

        textSQLWhereNoIBMData = WidgetFactory.createMultilineText(container, true, true);
        textSQLWhereNoIBMData.setToolTipText(Messages.ButtonTooltip_Exclude_IBM_data_SQL_WHERE_clause);
        textSQLWhereNoIBMData.setLayoutData(getLayoutData(1, true, 200, 250));
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
    }

    protected void setScreenToValues() {

        maxNumRowsToFetch = preferences.getMaximumNumberOfRowsToFetch();
        textSQLWhereNoIBMData.setText(preferences.getExcludeIBMDataSQLWhereClause());

        setScreenValues();
    }

    protected void setScreenToDefaultValues() {

        maxNumRowsToFetch = preferences.getInitialMaximumNumberOfRowsToFetch();
        textSQLWhereNoIBMData.setText(preferences.getInitialExcludeIBMDataSQLWhereClause());

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
