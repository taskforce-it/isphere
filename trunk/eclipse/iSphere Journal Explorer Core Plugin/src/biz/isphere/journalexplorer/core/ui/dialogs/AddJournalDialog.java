/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.progress.WorkbenchJob;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.swt.widgets.ContentAssistProposal;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.model.MetaColumn;
import biz.isphere.journalexplorer.core.model.MetaDataCache;
import biz.isphere.journalexplorer.core.model.MetaTable;
import biz.isphere.journalexplorer.core.swt.widgets.SqlEditor;
import biz.isphere.journalexplorer.core.ui.labelproviders.IBMiConnectionLabelProvider;
import biz.isphere.journalexplorer.rse.shared.model.ConnectionDelegate;

public class AddJournalDialog extends XDialog {

    private static final String CONNECTION = "CONNECTION";
    private static final String LIBRARY = "LIBRARY";
    private static final String FILE = "FILE";
    private static final String MEMBER = "MEMBER";
    private static final String WHERE_CLAUSE = "WHERE_CLAUSE";

    private ComboViewer cmbConnections;
    private Text txtLibraryName;
    private Text txtFileName;
    private Text txtMemberName;
    private SqlEditor sqlEditor;

    private String libraryName;
    private String fileName;
    private String memberName;
    private String whereClause;

    private boolean isInitializing;

    private ConnectionDelegate connection;
    private RefreshJob refreshJob;

    /**
     * Create the dialog.
     * 
     * @param parentShell
     */
    public AddJournalDialog(Shell parentShell) {
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
        GridLayout gridLayout = (GridLayout)container.getLayout();
        gridLayout.numColumns = 2;
        gridLayout.verticalSpacing = 5;

        Label lblConnections = new Label(container, SWT.NONE);
        lblConnections.setText(Messages.AddJournalDialog_Conection);
        lblConnections.setToolTipText(Messages.AddJournalDialog_Conection_Tooltip);

        cmbConnections = new ComboViewer(container, SWT.READ_ONLY);
        GridData cmbConnectionLayoutData = new GridData();
        cmbConnectionLayoutData.minimumWidth = 100;
        cmbConnectionLayoutData.grabExcessHorizontalSpace = true;
        cmbConnections.getControl().setLayoutData(cmbConnectionLayoutData);
        cmbConnections.getControl().setToolTipText(Messages.AddJournalDialog_Conection_Tooltip);

        Label lblLibrary = new Label(container, SWT.NONE);
        lblLibrary.setText(Messages.AddJournalDialog_Library);
        lblLibrary.setToolTipText(Messages.AddJournalDialog_Library_Tooltip);

        txtLibraryName = WidgetFactory.createNameText(container, true);
        txtLibraryName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        txtLibraryName.setToolTipText(Messages.AddJournalDialog_Library_Tooltip);

        Label lblFileName = new Label(container, SWT.NONE);
        lblFileName.setText(Messages.AddJournalDialog_FileName);
        lblFileName.setToolTipText(Messages.AddJournalDialog_FileName_Tooltip);

        txtFileName = WidgetFactory.createNameText(container, true);
        txtFileName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtFileName.setToolTipText(Messages.AddJournalDialog_FileName_Tooltip);

        Label lblMemberName = new Label(container, SWT.NONE);
        lblMemberName.setText(Messages.AddJournalDialog_MemberName);
        lblMemberName.setToolTipText(Messages.AddJournalDialog_MemberName_Tooltip);

        txtMemberName = WidgetFactory.createNameText(container, true);
        txtMemberName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtMemberName.setToolTipText(Messages.AddJournalDialog_MemberName_Tooltip);

        sqlEditor = new SqlEditor(container, SqlEditor.BUTTON_ADD | SqlEditor.BUTTON_CLEAR);
        GridData sqlEditorLayoutData = new GridData(GridData.FILL_BOTH);
        sqlEditorLayoutData.horizontalSpan = 2;
        sqlEditor.setLayoutData(sqlEditorLayoutData);

        configureControls();

        loadValues();

        isInitializing = false;

        updateContentAssistProposals();

        return container;
    }

    @Override
    public void setFocus() {

        String connectionName = null;

        Object object = cmbConnections.getElementAt(0);
        if (ConnectionDelegate.instanceOf(object)) {
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

        String connectionName = loadValue(CONNECTION, null);
        if (connectionName == null) {
            Object object = cmbConnections.getElementAt(0);
            if (ConnectionDelegate.instanceOf(object)) {
                ConnectionDelegate connection = new ConnectionDelegate(object);
                connectionName = connection.getConnectionName();
            }
        }

        if (connectionName != null) {
            cmbConnections.setSelection(new StructuredSelection(ConnectionDelegate.getConnection(connectionName)));
        }

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

        txtFileName.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                fileName = txtFileName.getText().trim();
                updateContentAssistProposals();
            }
        });

        txtMemberName.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                memberName = txtMemberName.getText().trim();
                updateContentAssistProposals();
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
        refreshJob.schedule(autoRefreshDelay);
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

    private boolean validated() {

        if (StringHelper.isNullOrEmpty(memberName)) {
            txtMemberName.setText("*FIRST");
            txtMemberName.setFocus();
            return false;
        }

        if (!connection.isConnected()) {
            String message = connection.connect();
            if (message != null) {
                return false;
            }
        }

        if (connection == null) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.AddJournalDialog_AllDataRequired);
            cmbConnections.getCombo().setFocus();
            return false;
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

        if (memberName.startsWith("*") && !"*FIRST".equals(memberName)) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.bind(Messages.Special_value_A_is_not_allowed, memberName));
            txtMemberName.setFocus();
            return false;
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

        if (!IBMiHostContributionsHandler.checkMember(connection.getConnectionName(), libraryName, fileName, memberName)) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R,
                Messages.bind(Messages.Member_C_does_not_exist_in_file_A_B, new String[] { libraryName, fileName, memberName }));
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

        return memberName.toUpperCase();
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

            monitor.done();
            return Status.OK_STATUS;
        };
    }
}
