/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.dialogs;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
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
import org.eclipse.swt.widgets.Text;
import org.medfoster.sqljep.ParseException;
import org.medfoster.sqljep.RowJEP;

import biz.isphere.base.internal.FileHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.MessageDialogAsync;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.extension.point.IFileDialog;
import biz.isphere.core.swt.widgets.sqleditor.SqlEditor;
import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.preferences.Preferences;
import biz.isphere.journalexplorer.core.ui.labelproviders.IBMiConnectionLabelProvider;

public class OpenJournalJsonFileDialog extends XDialog {

    private static final String CONNECTION = "CONNECTION";
    private static final String FILE = "FILE";
    private static final String WHERE_CLAUSE = "WHERE_CLAUSE";

    private static final String DEFAULT_CONNECTION_NAME = "*DEFAULT";

    private ComboViewer cmbConnections;
    private Text txtJsonFileName;
    private Button btnSelectFile;
    private SqlEditor sqlEditor;

    private String connectionName;
    private String jsonFileName;
    private String whereClause;

    /**
     * Create the dialog.
     * 
     * @param parentShell
     */
    public OpenJournalJsonFileDialog(Shell parentShell) {
        super(parentShell);
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
        lblConnections.setToolTipText(Messages.AddJournalDialog_Default_Conection_Tooltip);

        cmbConnections = new ComboViewer(container, SWT.READ_ONLY);
        cmbConnections.getControl().setLayoutData(createLayoutData(1, 100));
        cmbConnections.getControl().setToolTipText(Messages.AddJournalDialog_Default_Conection_Tooltip);

        Label lblFileName = new Label(container, SWT.NONE);
        lblFileName.setText(Messages.AddJournalDialog_ImportFileName);
        lblFileName.setToolTipText(Messages.AddJournalDialog_ImportFileName_Tooltip);

        Composite fileBox = new Composite(container, SWT.NONE);
        fileBox.setLayoutData(createLayoutData(1, 300));
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        fileBox.setLayout(layout);

        txtJsonFileName = WidgetFactory.createText(fileBox);
        txtJsonFileName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        txtJsonFileName.setToolTipText(Messages.AddJournalDialog_ImportFileName_Tooltip);

        btnSelectFile = WidgetFactory.createPushButton(fileBox);
        btnSelectFile.setImage(ISphereJournalExplorerCorePlugin.getDefault().getImage(ISphereJournalExplorerCorePlugin.IMAGE_OPEN_FILE));
        btnSelectFile.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

        sqlEditor = WidgetFactory.createSqlEditor(container, getClass().getSimpleName(), getDialogSettingsManager(), SqlEditor.BUTTON_ADD
            | SqlEditor.BUTTON_CLEAR);
        GridData sqlEditorLayoutData = new GridData(GridData.FILL_BOTH);
        sqlEditorLayoutData.horizontalSpan = 2;
        sqlEditor.setLayoutData(sqlEditorLayoutData);
        sqlEditor.setContentAssistProposals(JournalEntry.getBasicContentAssistProposals());

        configureControls();

        loadValues();

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

    private GridData createLayoutData(int horizontalSpan, int minWidth) {

        GridData gridData = createLayoutData();
        gridData.horizontalSpan = horizontalSpan;
        gridData.minimumWidth = minWidth;
        gridData.grabExcessHorizontalSpace = true;

        return gridData;
    }

    @Override
    public void setFocus() {

        if (StringHelper.isNullOrEmpty(connectionName)) {
            cmbConnections.getControl().setFocus();
            return;
        }

        txtJsonFileName.setFocus();
    }

    private void loadValues() {

        cmbConnections.setSelection(null);
        if (haveConnections()) {

            String connectionName = loadValue(CONNECTION, null);
            if (!isDefaultConnection(connectionName)) {
                if (!IBMiHostContributionsHandler.isAvailable(connectionName)) {
                    MessageDialogAsync.displayError(getShell(), Messages.E_R_R_O_R,
                        Messages.bind(Messages.Error_Connection_A_not_found, connectionName));
                }
            } else {
                connectionName = (String)cmbConnections.getElementAt(0);
            }

            if (connectionName != null) {
                cmbConnections.setSelection(new StructuredSelection(connectionName));
            }
        }

        String path = Preferences.getInstance().getExportPath();
        String file = Preferences.getInstance().getExportFileJson();
        jsonFileName = new File(path, file).getPath();
        txtJsonFileName.setText(jsonFileName);

        sqlEditor.setWhereClause(loadValue(WHERE_CLAUSE, ""));
    }

    private void storeValues() {

        storeValue(CONNECTION, connectionName);

        Preferences.getInstance().setExportPath(FileHelper.getPathName(jsonFileName));
        Preferences.getInstance().setExportFileJson(FileHelper.getFileName(jsonFileName));

        storeValue(WHERE_CLAUSE, whereClause);

        sqlEditor.storeHistory();
    }

    private void configureControls() {

        cmbConnections.setContentProvider(new ArrayContentProvider());
        cmbConnections.setLabelProvider(new IBMiConnectionLabelProvider());
        cmbConnections.setInput(loadConnectionNames());
        cmbConnections.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                if (selection.size() > 0) {
                    connectionName = (String)selection.getFirstElement();
                } else {
                    connectionName = null;
                }
            }
        });

        txtJsonFileName.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                jsonFileName = txtJsonFileName.getText().trim();
            }
        });

        txtJsonFileName.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                jsonFileName = txtJsonFileName.getText().trim();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });

        btnSelectFile.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                IFileDialog dialog = WidgetFactory.getFileDialog(getShell(), SWT.OPEN);
                dialog.setFilterNames(new String[] { "Json Files", FileHelper.getAllFilesText() }); //$NON-NLS-1$
                dialog.setFilterExtensions(new String[] { "*.json", FileHelper.getAllFilesFilter() }); //$NON-NLS-1$

                dialog.setFilterPath(Preferences.getInstance().getExportPath());
                dialog.setFileName(Preferences.getInstance().getExportFileJson());

                dialog.setOverwrite(false);
                final String importPath = dialog.open();

                if (importPath != null) {
                    txtJsonFileName.setText(FileHelper.getFileName(importPath));
                    Preferences.getInstance().setExportPath(FileHelper.getPathName(importPath));
                    Preferences.getInstance().setExportFileJson(FileHelper.getFileName(importPath));
                }

            }
        });

        sqlEditor.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                whereClause = sqlEditor.getWhereClause().trim();
            }
        });
    }

    private String[] loadConnectionNames() {

        List<String> connectionNames = new Vector<String>();
        connectionNames.add(DEFAULT_CONNECTION_NAME);

        for (String connectionName : IBMiHostContributionsHandler.getConnectionNames()) {
            connectionNames.add(connectionName);
        }

        return connectionNames.toArray(new String[connectionNames.size()]);
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

        if (!haveConnections()) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.Error_No_connections_available);
            cmbConnections.getCombo().setFocus();
            return false;
        }

        if (!isDefaultConnection(connectionName)) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.AddJournalDialog_AllDataRequired);
            cmbConnections.getCombo().setFocus();
            return false;
        }

        if (!isDefaultConnection(connectionName) && IBMiHostContributionsHandler.isOffline(connectionName)) {
            String message = Messages.bind(Messages.Error_Connection_A_is_offline, connectionName);
            if (message != null) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, message);
                cmbConnections.getCombo().setFocus();
                return false;
            }
        }

        if (StringHelper.isNullOrEmpty(jsonFileName) || !new File(jsonFileName).isFile()) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.AddJournalDialog_Import_file_does_not_exist);
            txtJsonFileName.setFocus();
            return false;
        }

        if (!StringHelper.isNullOrEmpty(whereClause)) {

            try {

                HashMap<String, Integer> columnMapping = JournalEntry.getBasicColumnMapping();
                RowJEP sqljep = new RowJEP(whereClause);

                sqljep.parseExpression(columnMapping);
                sqljep.getValue(JournalEntry.getSampleRow());

            } catch (ParseException e) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.bind(Messages.Error_in_SQL_WHERE_CLAUSE_A, e.getLocalizedMessage()));
                sqlEditor.setFocus();
            }

        }

        return true;
    }

    private boolean isDefaultConnection(String connectionName) {
        return (connectionName == null || DEFAULT_CONNECTION_NAME.equals(connectionName));
    }

    public boolean haveConnections() {

        if (cmbConnections.getCombo().getItemCount() > 0) {
            return true;
        }

        return false;
    }

    public String getConnectionName() {

        if (DEFAULT_CONNECTION_NAME.equals(connectionName)) {
            return null;
        }

        return connectionName;
    }

    public String getJsonFileName() {

        return jsonFileName.toUpperCase();
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
}
