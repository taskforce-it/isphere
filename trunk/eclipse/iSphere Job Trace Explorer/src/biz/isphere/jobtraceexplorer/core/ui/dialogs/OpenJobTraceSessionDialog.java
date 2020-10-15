/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.dialogs;

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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.swt.widgets.HistoryCombo;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.jobtraceexplorer.core.ISphereJobTraceExplorerCorePlugin;
import biz.isphere.jobtraceexplorer.core.Messages;

import com.ibm.as400.access.AS400;

public class OpenJobTraceSessionDialog extends XDialog {

    private static final String CONNECTION = "CONNECTION"; //$NON-NLS-1$
    private static final String SESSION_ID = "SESSION_ID"; //$NON-NLS-1$
    private static final String LIBRARY_NAME = "LIBRARY_NAME"; //$NON-NLS-1$
    private static final String EXCLUDE_IBM_DATA = "SQL_WHERE_NO_IBM_DATA"; //$NON-NLS-1$

    private ComboViewer cmbConnections;
    private HistoryCombo txtSessionID;
    private HistoryCombo txtLibraryName;
    private Button chkExcludeIBMData;

    private Composite container;
    private String connectionName;
    private String sessionID;
    private String libraryName;
    private boolean isIBMDataExcluded;

    public OpenJobTraceSessionDialog(Shell shell) {
        super(shell);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.MessageDialog_Open_Job_Trace_Session_Title);
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        container = (Composite)super.createDialogArea(parent);
        container.setLayout(new GridLayout(2, false));

        createLabel(container, Messages.OpenJobTraceSessionDialog_Connection, Messages.Tooltip_OpenJobTraceSessionDialog_Connection);

        cmbConnections = new ComboViewer(container, SWT.READ_ONLY);
        cmbConnections.getControl().setLayoutData(createLayoutData(100));
        cmbConnections.getControl().setToolTipText(Messages.Tooltip_OpenJobTraceSessionDialog_Connection);

        createLabel(container, Messages.OpenJobTraceSessionDialog_SessionID, Messages.Tooltip_OpenJobTraceSessionDialog_SessionID);
        txtSessionID = WidgetFactory.createNameHistoryCombo(container);
        txtSessionID.setLayoutData(createLayoutData());
        txtSessionID.setToolTipText(Messages.Tooltip_OpenJobTraceSessionDialog_SessionID);

        createLabel(container, Messages.OpenJobTraceSessionDialog_Library, Messages.Tooltip_OpenJobTraceSessionDialog_Library);
        txtLibraryName = WidgetFactory.createNameHistoryCombo(container);
        txtLibraryName.setLayoutData(createLayoutData());
        txtLibraryName.setToolTipText(Messages.Tooltip_OpenJobTraceSessionDialog_Library);

        WidgetFactory.createLineFiller(container);

        chkExcludeIBMData = WidgetFactory.createCheckbox(container, Messages.OpenJobTraceSessionDialog_Exclude_IBM_Data);
        chkExcludeIBMData.setLayoutData(createSpanLayoutData(2));
        chkExcludeIBMData.setToolTipText(Messages.Tooltip_OpenJobTraceSessionDialog_Exclude_IBM_data);

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

    public String getSessionID() {
        return sessionID;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public boolean isIBMDataExcluded() {
        return isIBMDataExcluded;
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

        if (StringHelper.isNullOrEmpty(sessionID)) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.Error_AllDataRequired);
            txtSessionID.setFocus();
            return false;
        }

        if (StringHelper.isNullOrEmpty(libraryName)) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.Error_AllDataRequired);
            txtLibraryName.setFocus();
            return false;
        }

        AS400 system = IBMiHostContributionsHandler.getSystem(connectionName);
        if (system == null) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R,
                Messages.bind(Messages.Error_Connection_A_not_found_or_not_available, connectionName));
            cmbConnections.getControl().setFocus();
            return false;
        }

        if (!hasSession(system, libraryName, sessionID)) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R,
                Messages.bind(Messages.Error_Job_trace_session_B_not_found_in_library_A, libraryName, sessionID));
            txtSessionID.setFocus();
            return false;
        }

        return true;
    }

    private boolean hasSession(AS400 system, String libraryName, String memberName) {
        return ISphereHelper.checkMember(system, libraryName, "QAYPETIDX", memberName); //$NON-NLS-1$
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

    private GridData createSpanLayoutData(int horizontalSpan) {
        GridData gridData = new GridData();
        gridData.horizontalSpan = horizontalSpan;
        return gridData;
    }

    @Override
    public void setFocus() {

        if (cmbConnections.getSelection().isEmpty()) {
            cmbConnections.getControl().setFocus();
            return;
        }

        if (StringHelper.isNullOrEmpty(txtSessionID.getText())) {
            txtSessionID.setFocus();
            return;
        }

        if (StringHelper.isNullOrEmpty(txtLibraryName.getText())) {
            txtLibraryName.setFocus();
            return;
        }

        txtSessionID.setFocus();
    }

    private void loadValues() {

        cmbConnections.setSelection(null);
        if (haveConnections()) {
            String connectionName = loadValue(CONNECTION, null);
            if (connectionName != null) {
                cmbConnections.setSelection(new StructuredSelection(connectionName));
            }
        }

        txtSessionID.load(getDialogSettingsManager(), getKey("#sessionID")); //$NON-NLS-1$
        txtLibraryName.load(getDialogSettingsManager(), getKey("#libraryName")); //$NON-NLS-1$

        txtSessionID.setText(loadValue(SESSION_ID, ""));
        txtLibraryName.setText(loadValue(LIBRARY_NAME, ""));

        // isIBMDataExcluded = loadBooleanValue(SQL_WHERE_NO_IBM_DATA, true);
        isIBMDataExcluded = true;
        chkExcludeIBMData.setSelection(isIBMDataExcluded);
    }

    private void storeValues() {

        storeValue(CONNECTION, connectionName);
        storeValue(SESSION_ID, sessionID);
        storeValue(LIBRARY_NAME, libraryName);
        storeValue(EXCLUDE_IBM_DATA, isIBMDataExcluded);

        updateAndStoreHistory(txtSessionID, sessionID);
        updateAndStoreHistory(txtLibraryName, libraryName);
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

        txtSessionID.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                sessionID = txtSessionID.getText().trim();
                return;
            }
        });

        txtLibraryName.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                libraryName = txtLibraryName.getText().trim();
                return;
            }
        });

        chkExcludeIBMData.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                isIBMDataExcluded = chkExcludeIBMData.getSelection();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
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
        return super.getDialogBoundsSettings(ISphereJobTraceExplorerCorePlugin.getDefault().getDialogSettings());
    }

    @Override
    public Point getMinimalSize() {
        return new Point(430, 260);
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