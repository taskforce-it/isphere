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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.ui.labelproviders.IBMiConnectionLabelProvider;
import biz.isphere.journalexplorer.rse.shared.model.ConnectionDelegate;

public class AddJournalDialog extends XDialog {

    private static final String CONNECTION = "CONNECTION";
    private static final String LIBRARY = "LIBRARY";
    private static final String FILE = "FILE";
    private static final String MEMBER = "MEMBER";

    private ComboViewer cmbConnections;
    private Text txtLibraryName;
    private Text txtFileName;
    private Text txtMemberName;

    private String libraryName;
    private String fileName;
    private String memberName;

    private ConnectionDelegate connection;

    /**
     * Create the dialog.
     * 
     * @param parentShell
     */
    public AddJournalDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    public void create() {

        super.create();
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

        cmbConnections = new ComboViewer(container, SWT.READ_ONLY);
        GridData cmbConnectionLayoutData = new GridData();
        cmbConnectionLayoutData.minimumWidth = 100;
        cmbConnectionLayoutData.grabExcessHorizontalSpace = true;
        cmbConnections.getControl().setLayoutData(cmbConnectionLayoutData);

        Label lblLibrary = new Label(container, SWT.NONE);
        lblLibrary.setText(Messages.AddJournalDialog_Library);

        txtLibraryName = WidgetFactory.createNameText(container, true);
        txtLibraryName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblFileName = new Label(container, SWT.NONE);
        lblFileName.setText(Messages.AddJournalDialog_FileName);

        txtFileName = WidgetFactory.createNameText(container, true);
        txtFileName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblMemberName = new Label(container, SWT.NONE);
        lblMemberName.setText(Messages.AddJournalDialog_MemberName);

        txtMemberName = WidgetFactory.createNameText(container, true);
        txtMemberName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        configureControls();

        loadValues();

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
    }

    private void storeValues() {

        storeValue(CONNECTION, connection.getConnectionName());
        storeValue(LIBRARY, libraryName);
        storeValue(FILE, fileName);
        storeValue(MEMBER, memberName);
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
                }
            }
        });

        txtLibraryName.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                libraryName = txtLibraryName.getText().trim();
            }
        });

        txtFileName.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                fileName = txtFileName.getText().trim();
            }
        });

        txtMemberName.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                memberName = txtMemberName.getText().trim();
            }
        });
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
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.AddJournalDialog_OpenJournal);

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

        if (StringHelper.isNullOrEmpty(fileName) || StringHelper.isNullOrEmpty(libraryName) || connection == null) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.AddJournalDialog_AllDataRequired);
            return false;
        }

        if (!IBMiHostContributionsHandler.checkFile(connection.getConnectionName(), libraryName, fileName)) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R,
                Messages.bind(Messages.File_A_B_does_not_exist, new String[] { libraryName, fileName }));
            return false;
        }

        if (!IBMiHostContributionsHandler.checkMember(connection.getConnectionName(), libraryName, fileName, memberName)) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R,
                Messages.bind(Messages.Member_C_does_not_exist_in_file_A_B, new String[] { libraryName, fileName, memberName }));
            return false;
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
        return getShell().computeSize(410, 220, true);
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
