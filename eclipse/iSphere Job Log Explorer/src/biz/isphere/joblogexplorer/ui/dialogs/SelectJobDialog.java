/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.MessageDialogAsync;
import biz.isphere.core.swt.widgets.HistoryCombo;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.joblogexplorer.ISphereJobLogExplorerPlugin;
import biz.isphere.joblogexplorer.Messages;
import biz.isphere.joblogexplorer.api.retrievejobinformation.JOBI0400;
import biz.isphere.joblogexplorer.api.retrievejobinformation.QUSRJOBI;

import com.ibm.as400.access.AS400;

public class SelectJobDialog extends XDialog {

    private static final String CONNECTION = "CONNECTION"; //$NON-NLS-1$
    private static final String JOB_NAME = "JOB_NAME"; //$NON-NLS-1$
    private static final String USER_NAME = "USER_NAME"; //$NON-NLS-1$
    private static final String JOB_NUMBER = "JOB_NUMBER"; //$NON-NLS-1$

    private ComboViewer cmbConnections;
    private HistoryCombo txtJobName;
    private HistoryCombo txtUserName;
    private HistoryCombo txtJobNumber;

    private String connectionName;
    private String jobName;
    private String userName;
    private String jobNumber;

    public SelectJobDialog(Shell shell) {
        super(shell);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.Explore_job_log);
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        Composite container = (Composite)super.createDialogArea(parent);
        container.setLayout(new GridLayout(2, false));

        createLabel(container, Messages.SelectJobDialog_Connection, Messages.SelectJobDialog_Connection_Tooltip);

        cmbConnections = new ComboViewer(container, SWT.READ_ONLY);
        cmbConnections.getControl().setLayoutData(createLayoutData(100));
        cmbConnections.getControl().setToolTipText(Messages.SelectJobDialog_Connection_Tooltip);

        createLabel(container, Messages.SelectJobDialog_JobName, Messages.SelectJobDialog_JobName_Tooltip);
        txtJobName = WidgetFactory.createNameHistoryCombo(container);
        txtJobName.setLayoutData(createLayoutData());

        createLabel(container, Messages.SelectJobDialog_UserName, Messages.SelectJobDialog_UserName_Tooltip);
        txtUserName = WidgetFactory.createNameHistoryCombo(container);
        txtUserName.setLayoutData(createLayoutData());

        createLabel(container, Messages.SelectJobDialog_JobNumber, Messages.SelectJobDialog_JobNumber_Tooltip);
        txtJobNumber = WidgetFactory.createIntegerHistoryCombo(container, 6);
        txtJobNumber.setLayoutData(createLayoutData());

        configureControls();

        loadValues();

        return container;
    }

    public boolean haveConnections() {

        if (cmbConnections.getCombo().getItemCount() > 0) {
            return true;
        }

        return false;
    }

    public String getConnectionName() {

        return connectionName;
    }

    public String getJobName() {
        return jobName;
    }

    public String getUserName() {
        return userName;
    }

    public String getJobNumber() {
        return jobNumber;
    }

    @Override
    protected void okPressed() {

        if (validated()) {
            storeValues();
            super.okPressed();
        }
    }

    private boolean validated() {

        if (!haveConnections()) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.Error_No_connections_available);
            cmbConnections.getCombo().setFocus();
            return false;
        }

        if (StringHelper.isNullOrEmpty(connectionName)) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.Error_AllDataRequired);
            cmbConnections.getCombo().setFocus();
            return false;
        }

        if (StringHelper.isNullOrEmpty(jobName)) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.Error_AllDataRequired);
            txtJobName.setFocus();
            return false;
        }

        if (StringHelper.isNullOrEmpty(userName)) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.Error_AllDataRequired);
            txtUserName.setFocus();
            return false;
        }

        if (StringHelper.isNullOrEmpty(jobNumber)) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.Error_AllDataRequired);
            txtJobNumber.setFocus();
            return false;
        }

        AS400 system = IBMiHostContributionsHandler.getSystem(connectionName);
        if (system == null) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R,
                Messages.bind(Messages.Error_Connection_A_not_found_or_not_available, connectionName));
            cmbConnections.getControl().setFocus();
            return false;
        }

        QUSRJOBI qusrjobi = new QUSRJOBI(system);
        qusrjobi.setJob(jobName, userName, jobNumber);

        JOBI0400 jobi0400 = new JOBI0400(system);
        if (!qusrjobi.execute(jobi0400)) {
            String errorID = qusrjobi.getErrorMessageID();
            if (JOBI0400.JOB_NOT_FOUND_MSGID.equals(errorID)) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R,
                    Messages.bind(Messages.Error_Job_not_found, new Object[] { jobName, userName, jobNumber }));
                cmbConnections.getControl().setFocus();
                return false;
            }
        }

        return true;
    }

    private GridData createLayoutData() {
        GridData gridData = new GridData();
        gridData.widthHint = 160;
        return gridData;
    }

    private GridData createLayoutData(int minWidth) {

        GridData gridData = createLayoutData();
        gridData.minimumWidth = minWidth;
        gridData.grabExcessHorizontalSpace = true;

        return gridData;
    }

    @Override
    public void setFocus() {

        if (cmbConnections.getSelection().isEmpty()) {
            cmbConnections.getControl().setFocus();
            return;
        }

        if (StringHelper.isNullOrEmpty(txtJobName.getText())) {
            txtJobName.setFocus();
            return;
        }

        if (StringHelper.isNullOrEmpty(txtUserName.getText())) {
            txtUserName.setFocus();
            return;
        }

        txtJobNumber.setFocus();
    }

    private void loadValues() {

        cmbConnections.setSelection(null);
        if (haveConnections()) {

            Object system = null;
            final String connectionName = loadValue(CONNECTION, null);

            if (connectionName != null) {
                try {
                    system = IBMiHostContributionsHandler.getSystem(connectionName);
                } catch (Throwable e) {
                    // Ignore errors
                }
                if (system == null) {
                    MessageDialogAsync
                        .displayError(getShell(), Messages.bind(Messages.Error_Connection_A_not_found_or_not_available, connectionName));
                }
            } else {
                system = cmbConnections.getElementAt(0);
            }

            if (system != null) {
                cmbConnections.setSelection(new StructuredSelection(connectionName));
            }
        }

        txtJobName.load(getDialogSettingsManager(), getKey("#jobName")); //$NON-NLS-1$
        txtUserName.load(getDialogSettingsManager(), getKey("#userName")); //$NON-NLS-1$
        txtJobNumber.load(getDialogSettingsManager(), getKey("#jobNumber")); //$NON-NLS-1$

        txtJobName.setText(loadValue(JOB_NAME, ""));
        txtUserName.setText(loadValue(USER_NAME, ""));
        txtJobNumber.setText(loadValue(JOB_NUMBER, ""));
    }

    private void storeValues() {

        storeValue(CONNECTION, connectionName);
        storeValue(JOB_NAME, jobName);
        storeValue(USER_NAME, userName);
        storeValue(JOB_NUMBER, jobNumber);

        updateAndStoreHistory(txtJobName, jobName);
        updateAndStoreHistory(txtUserName, userName);
        updateAndStoreHistory(txtJobNumber, jobNumber);
    }

    private void updateAndStoreHistory(HistoryCombo historyCombo, String value) {

        historyCombo.updateHistory(value);
        historyCombo.store();
    }

    private String getKey(String key) {
        return getClass().getName() + "." + key; //$NON-NLS-1$
    }

    private void configureControls() {

        cmbConnections.setContentProvider(new ArrayContentProvider());
        cmbConnections.setLabelProvider(new ConnectionLabelProvider());
        cmbConnections.setInput(IBMiHostContributionsHandler.getConnectionNames());
        cmbConnections.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                if (selection.size() > 0) {
                    connectionName = (String)selection.getFirstElement();
                }
            }
        });

        txtJobName.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                jobName = txtJobName.getText().trim();
            }
        });

        txtUserName.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                userName = txtUserName.getText().trim();
            }
        });

        txtJobNumber.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                jobNumber = txtJobNumber.getText().trim();
            }
        });
    }

    private Label createLabel(Composite parent, String text, String tooltip) {

        Label label = new Label(parent, SWT.NONE);
        label.setText(text);
        label.setToolTipText(tooltip);

        return label;
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
        return super.getDefaultSize();
    }

    /**
     * Overridden to let {@link XDialog} store the state of this dialog in a
     * separate section of the dialog settings file.
     */
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return super.getDialogBoundsSettings(ISphereJobLogExplorerPlugin.getDefault().getDialogSettings());
    }

    private class ConnectionLabelProvider extends LabelProvider {

        @Override
        public String getText(Object element) {

            if (element instanceof String) {
                return (String)element;
            }

            return super.getText(element);
        }
    }
}
