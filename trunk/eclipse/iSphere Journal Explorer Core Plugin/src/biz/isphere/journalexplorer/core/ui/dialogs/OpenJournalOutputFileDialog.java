/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.dialogs;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.progress.WorkbenchJob;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.MessageDialogAsync;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.swt.widgets.ContentAssistProposal;
import biz.isphere.core.swt.widgets.HistoryCombo;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.sqleditor.SqlEditor;
import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.model.MetaColumn;
import biz.isphere.journalexplorer.core.model.MetaDataCache;
import biz.isphere.journalexplorer.core.model.MetaTable;
import biz.isphere.journalexplorer.core.ui.labelproviders.IBMiConnectionLabelProvider;
import biz.isphere.journalexplorer.rse.shared.model.ConnectionDelegate;

public class OpenJournalOutputFileDialog extends XDialog {

    private static final String CONNECTION = "CONNECTION";
    private static final String LIBRARY = "LIBRARY";
    private static final String FILE = "FILE";
    private static final String MEMBER = "MEMBER";
    private static final String WHERE_CLAUSE = "WHERE_CLAUSE";

    private ComboViewer cmbConnections;
    private HistoryCombo txtLibraryName;
    private HistoryCombo txtFileName;
    private HistoryCombo txtMemberName;
    private SqlEditor sqlEditor;

    private String libraryName;
    private String fileName;
    private String memberName;
    private String whereClause;
    private String resolvedMemberName;

    private boolean isInitializing;

    private ConnectionDelegate connection;
    private RefreshJob refreshJob;

    /**
     * Create the dialog.
     * 
     * @param parentShell
     */
    public OpenJournalOutputFileDialog(Shell parentShell) {
        super(parentShell);

        this.isInitializing = true;
        this.refreshJob = new RefreshJob();
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.AddJournalDialog_OpenJournal);
    }

    /**
     * Create contents of the dialog.
     * 
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {

        Composite container = (Composite)super.createDialogArea(parent);
        container.setLayout(new GridLayout(2, false));

        Label lblConnections = new Label(container, SWT.NONE);
        lblConnections.setText(Messages.AddJournalDialog_Conection);
        lblConnections.setToolTipText(Messages.AddJournalDialog_Conection_Tooltip);

        cmbConnections = new ComboViewer(container, SWT.READ_ONLY);
        cmbConnections.getControl().setLayoutData(createLayoutData(100));
        cmbConnections.getControl().setToolTipText(Messages.AddJournalDialog_Conection_Tooltip);

        Label lblLibrary = new Label(container, SWT.NONE);
        lblLibrary.setText(Messages.AddJournalDialog_Library);
        lblLibrary.setToolTipText(Messages.AddJournalDialog_Library_Tooltip);

        txtLibraryName = WidgetFactory.createNameHistoryCombo(container);
        txtLibraryName.setLayoutData(createLayoutData());
        txtLibraryName.setToolTipText(Messages.AddJournalDialog_Library_Tooltip);

        Label lblFileName = new Label(container, SWT.NONE);
        lblFileName.setText(Messages.AddJournalDialog_FileName);
        lblFileName.setToolTipText(Messages.AddJournalDialog_FileName_Tooltip);

        txtFileName = WidgetFactory.createNameHistoryCombo(container);
        txtFileName.setLayoutData(createLayoutData());
        txtFileName.setToolTipText(Messages.AddJournalDialog_FileName_Tooltip);

        Label lblMemberName = new Label(container, SWT.NONE);
        lblMemberName.setText(Messages.AddJournalDialog_MemberName);
        lblMemberName.setToolTipText(Messages.AddJournalDialog_MemberName_Tooltip);

        txtMemberName = WidgetFactory.createNameHistoryCombo(container);
        txtMemberName.setLayoutData(createLayoutData());
        txtMemberName.setToolTipText(Messages.AddJournalDialog_MemberName_Tooltip);

        sqlEditor = WidgetFactory.createSqlEditor(container, getClass().getSimpleName(), getDialogSettingsManager(), SqlEditor.BUTTON_ADD
            | SqlEditor.BUTTON_CLEAR);
        GridData sqlEditorLayoutData = new GridData(GridData.FILL_BOTH);
        sqlEditorLayoutData.horizontalSpan = 2;
        sqlEditor.setLayoutData(sqlEditorLayoutData);

        configureControls();

        loadValues();

        isInitializing = false;

        updateContentAssistProposals();

        if (!haveConnections()) {
            MessageDialogAsync.displayError(getShell(), Messages.Error_No_connections_available);
        }

        return container;
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

        String connectionName = null;

        if (connection != null) {
            connectionName = connection.getConnectionName();
        }

        if (StringHelper.isNullOrEmpty(connectionName)) {
            cmbConnections.getControl().setFocus();
            return;
        }

        if (StringHelper.isNullOrEmpty(txtLibraryName.getText())) {
            txtLibraryName.setFocus();
            return;
        }

        if (StringHelper.isNullOrEmpty(txtFileName.getText())) {
            txtFileName.setFocus();
            return;
        }

        txtMemberName.setFocus();
    }

    private void loadValues() {

        cmbConnections.setSelection(null);
        if (haveConnections()) {
            String connectionName = loadValue(CONNECTION, null);
            if (connectionName != null) {
                cmbConnections.setSelection(new StructuredSelection(connection));
            }
        }

        txtLibraryName.load(getDialogSettingsManager(), getClass().getName() + "#library"); //$NON-NLS-1$
        txtFileName.load(getDialogSettingsManager(), getClass().getName() + "#file"); //$NON-NLS-1$
        txtMemberName.load(getDialogSettingsManager(), getClass().getName() + "#member"); //$NON-NLS-1$

        txtLibraryName.setText(loadValue(LIBRARY, ""));
        txtFileName.setText(loadValue(FILE, ""));
        txtMemberName.setText(loadValue(MEMBER, ""));
        sqlEditor.setWhereClause(loadValue(WHERE_CLAUSE, ""));
    }

    private void storeValues() {

        storeValue(CONNECTION, connection.getConnectionName());
        storeValue(LIBRARY, libraryName);
        storeValue(FILE, fileName);
        storeValue(MEMBER, memberName);
        storeValue(WHERE_CLAUSE, whereClause);

        txtLibraryName.updateHistory(libraryName);
        txtLibraryName.store();

        txtFileName.updateHistory(fileName);
        txtFileName.store();

        txtMemberName.updateHistory(memberName);
        txtMemberName.store();

        sqlEditor.storeHistory();
    }

    private void configureControls() {

        cmbConnections.setContentProvider(new ArrayContentProvider());
        cmbConnections.setLabelProvider(new IBMiConnectionLabelProvider());
        cmbConnections.setInput(ConnectionDelegate.getConnections());
        cmbConnections.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                if (selection.size() > 0) {
                    connection = new ConnectionDelegate(selection.getFirstElement());
                    updateContentAssistProposals();
                }
            }
        });

        txtLibraryName.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                libraryName = txtLibraryName.getText().trim();
                updateContentAssistProposals();
            }
        });

        txtLibraryName.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                libraryName = txtLibraryName.getText().trim();
                updateContentAssistProposals();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });

        txtFileName.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                fileName = txtFileName.getText().trim();
                updateContentAssistProposals();
            }
        });

        txtFileName.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                fileName = txtFileName.getText().trim();
                updateContentAssistProposals();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });

        txtMemberName.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                memberName = txtMemberName.getText().trim();
                updateContentAssistProposals();
            }
        });

        txtMemberName.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                memberName = txtMemberName.getText().trim();
                updateContentAssistProposals();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });

        sqlEditor.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                whereClause = sqlEditor.getWhereClause().trim();
                ((StyledText)event.getSource()).getText();
            }
        });
    }

    private void updateContentAssistProposals() {

        if (isInitializing) {
            return;
        }

        int autoRefreshDelay = Preferences.getInstance().getAutoRefreshDelay();

        refreshJob.cancel();

        if (connection != null && !StringHelper.isNullOrEmpty(fileName) && !StringHelper.isNullOrEmpty(libraryName)) {
            refreshJob.schedule(autoRefreshDelay);
        }
    }

    /**
     * Create contents of the button bar.
     * 
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {

        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    @Override
    protected void okPressed() {

        if (validated()) {
            storeValues();
            super.okPressed();
        }
    };

    @Override
    public boolean close() {
        // Important, must be called to ensure the SqlEditor is removed from
        // the list of preferences listeners.
        sqlEditor.dispose();
        return super.close();
    }

    private boolean validated() {

        resolvedMemberName = null;

        if (StringHelper.isNullOrEmpty(memberName)) {
            txtMemberName.setText("*FIRST");
            txtMemberName.setFocus();
            return false;
        }

        if (!haveConnections()) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.Error_No_connections_available);
            cmbConnections.getCombo().setFocus();
            return false;
        }

        if (connection == null) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.AddJournalDialog_AllDataRequired);
            cmbConnections.getCombo().setFocus();
            return false;
        }

        if (!connection.isConnected()) {
            String message = connection.connect();
            if (message != null) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, message);
                cmbConnections.getCombo().setFocus();
                return false;
            }
        }

        if (StringHelper.isNullOrEmpty(libraryName)) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.AddJournalDialog_AllDataRequired);
            txtLibraryName.setFocus();
            return false;
        }

        if (StringHelper.isNullOrEmpty(fileName)) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.AddJournalDialog_AllDataRequired);
            txtFileName.setFocus();
            return false;
        }

        if (StringHelper.isNullOrEmpty(memberName)) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.AddJournalDialog_AllDataRequired);
            txtMemberName.setFocus();
            return false;
        }

        if (memberName.startsWith("*")) {
            if ("*FIRST".equals(memberName)) {
                try {
                    resolvedMemberName = IBMiHostContributionsHandler.resolveMemberName(connection.getConnectionName(), libraryName, fileName,
                        memberName);
                } catch (Exception e) {
                    resolvedMemberName = null;
                    MessageDialog.openError(getShell(), Messages.E_R_R_O_R, e.getLocalizedMessage());
                    txtMemberName.setFocus();
                    return false;
                }
            } else {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.bind(Messages.Special_value_A_is_not_allowed, memberName));
                txtMemberName.setFocus();
                return false;
            }
        } else {
            resolvedMemberName = memberName;
        }

        if (!IBMiHostContributionsHandler.checkLibrary(connection.getConnectionName(), libraryName)) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.bind(Messages.Library_A_does_not_exist, new String[] { libraryName }));
            txtLibraryName.setFocus();
            return false;
        }

        if (!IBMiHostContributionsHandler.checkFile(connection.getConnectionName(), libraryName, fileName)) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R,
                Messages.bind(Messages.File_A_B_does_not_exist, new String[] { libraryName, fileName }));
            txtFileName.setFocus();
            return false;
        }

        if (!IBMiHostContributionsHandler.checkMember(connection.getConnectionName(), libraryName, fileName, resolvedMemberName)) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R,
                Messages.bind(Messages.Member_C_does_not_exist_in_file_A_B, new String[] { libraryName, fileName, resolvedMemberName }));
            txtMemberName.setFocus();
            return false;
        }

        Statement s = null;

        try {

            if (!StringHelper.isNullOrEmpty(whereClause)) {
                Connection c = IBMiHostContributionsHandler.getJdbcConnection(connection.getConnectionName());
                s = c.prepareStatement(String.format("SELECT * FROM %s.%s WHERE %s", libraryName, fileName, whereClause));
                s.close();
            }

        } catch (SQLException e) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.bind(Messages.Error_in_SQL_WHERE_CLAUSE_A, e.getLocalizedMessage()));
            sqlEditor.setFocus();
            return false;
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

    public boolean haveConnections() {

        if (cmbConnections.getCombo().getItemCount() > 0) {
            return true;
        }

        return false;
    }

    public String getConnectionName() {

        return connection.getConnectionName();
    }

    public String getLibrary() {

        return libraryName.toUpperCase();
    }

    public String getFileName() {

        return fileName.toUpperCase();
    }

    public String getMemberName() {

        return resolvedMemberName.toUpperCase();
    }

    public String getSqlWhere() {
        return whereClause;
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
        return getShell().computeSize(500, 300, true);
    }

    /**
     * Overridden to ensure a minimum dialog size.
     */
    @Override
    protected Point getInitialSize() {

        Point size = super.getInitialSize();

        if (size.x < 260) {
            size.x = 260;
        }

        if (size.y < 270) {
            size.y = 270;
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

    private class RefreshJob extends WorkbenchJob {

        public RefreshJob() {
            super("Refresh Job");
            setSystem(true); // set to false to show progress to user
        }

        public IStatus runInUIThread(IProgressMonitor monitor) {

            monitor.beginTask("Refreshing", IProgressMonitor.UNKNOWN);

            if (connection != null) {

                MetaTable metaData = null;

                try {
                    metaData = MetaDataCache.getInstance().retrieveMetaData(connection.getConnectionName(), libraryName, fileName);
                    if (!metaData.hasColumns()) {
                        MetaDataCache.getInstance().removeMetaData(metaData);
                    }
                } catch (Exception e) {
                    ISpherePlugin.logError("*** Could not load meta data of file '" + fileName + "' ***", e); //$NON-NLS-1$ //$NON-NLS-2$
                }

                List<ContentAssistProposal> proposals = new LinkedList<ContentAssistProposal>();

                if (metaData != null) {
                    for (MetaColumn column : metaData.getColumns()) {
                        proposals.add(new ContentAssistProposal(column.getName(), column.getFormattedType() + " - " + column.getText()));
                    }
                }

                sqlEditor.setContentAssistProposals(proposals.toArray(new ContentAssistProposal[proposals.size()]));

            }

            monitor.done();

            return Status.OK_STATUS;
        };
    }
}
