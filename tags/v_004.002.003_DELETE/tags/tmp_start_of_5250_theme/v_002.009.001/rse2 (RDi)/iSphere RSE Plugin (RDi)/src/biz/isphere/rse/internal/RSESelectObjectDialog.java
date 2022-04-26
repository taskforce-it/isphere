/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.internal;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.rse.Messages;

import com.ibm.etools.iseries.rse.ui.widgets.IBMiConnectionCombo;
import com.ibm.etools.iseries.rse.ui.widgets.QSYSMsgFilePrompt;
import com.ibm.etools.iseries.rse.ui.widgets.QSYSObjectPrompt;
import com.ibm.etools.iseries.services.qsys.api.IQSYSObject;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

public class RSESelectObjectDialog extends XDialog {

    private static final String CONNECTION_NAME = "CONNECTION_NAME"; //$NON-NLS-1$
    private static final String LIBRARY_NAME = "LIBRARY_NAME"; //$NON-NLS-1$
    private static final String FILE_NAME = "FILE_NAME"; //$NON-NLS-1$

    private IBMiConnection connection;
    private String objectType;
    private String libraryName;
    private String objectName;

    private IBMiConnectionCombo connectionCombo;
    private QSYSObjectPrompt objectPrompt;

    private RemoteObject remoteObject;

    public static RSESelectObjectDialog createSelectMessageFileDialog(Shell shell, IBMiConnection connection) {
        return new RSESelectObjectDialog(shell, connection, ISeries.MSGF);
    }

    private RSESelectObjectDialog(Shell parentShell, IBMiConnection connection, String objectType) {
        super(parentShell);

        this.connection = connection;
        this.objectType = objectType;
        this.libraryName = "";
        this.objectName = "";
    }

    public void setLibraryName(String libraryName) {

        if (libraryName == null) {
            this.libraryName = "";
        } else {
            this.libraryName = libraryName;
        }
    }

    public void setMessageFileName(String objectName) {

        if (objectName == null) {
            this.objectName = "";
        } else {
            this.objectName = objectName;
        }
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);

        if (ISeries.MSGF.equals(objectType)) {
            newShell.setText(Messages.Select_Message_File);
        } else {
            newShell.setText(Messages.Select_Object);
        }
    }

    @Override
    public Control createDialogArea(Composite parent) {

        Composite dialogArea = new Composite(parent, SWT.NONE);
        GridLayout rightLayout = new GridLayout();
        rightLayout.numColumns = 1;
        dialogArea.setLayout(rightLayout);
        dialogArea.setLayoutData(new GridData());

        connectionCombo = new IBMiConnectionCombo(dialogArea, connection, false);
        connectionCombo.setLayoutData(new GridData());
        connectionCombo.getCombo().setLayoutData(new GridData());

        connectionCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                objectPrompt.setSystemConnection(connectionCombo.getHost());
            }
        });

        objectPrompt = new QSYSMsgFilePrompt(dialogArea, SWT.NONE, false, true);
        objectPrompt.setSystemConnection(connectionCombo.getHost());
        objectPrompt.setLibraryName(""); //$NON-NLS-1$
        objectPrompt.setObjectName(""); //$NON-NLS-1$

        ModifyListener modifyListener = new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                if (getButton(IDialogConstants.OK_ID) != null) {
                    getButton(IDialogConstants.OK_ID).setEnabled(canFinish());
                }
            }
        };

        objectPrompt.getObjectCombo().addModifyListener(modifyListener);
        objectPrompt.getLibraryCombo().addModifyListener(modifyListener);

        objectPrompt.getLibraryCombo().setFocus();

        loadScreenValues();

        return dialogArea;
    }

    @Override
    protected boolean isResizable() {
        return false;
    }

    @Override
    protected void okPressed() {

        String library = objectPrompt.getLibraryName().trim();
        String file = objectPrompt.getObjectName().trim();
        IBMiConnection connection = connectionCombo.getISeriesConnection();

        IQSYSObject qsysObject = null;

        try {

            if (connection.getLibrary(library, null) == null) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.bind(Messages.Library_A_not_found, library));
                return;
            }

            qsysObject = connection.getObject(library, file, objectType, null);
            if (qsysObject == null) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R,
                    Messages.bind(Messages.Object_A_in_library_B_not_found, new String[] { file, library }));
                return;
            }

        } catch (Exception e) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, e.getLocalizedMessage());
            return;
        }

        remoteObject = new RemoteObject(connection.getConnectionName(), qsysObject.getName(), qsysObject.getLibrary(), objectType,
            qsysObject.getDescription());

        saveScreenValues();

        // Close dialog
        super.okPressed();
    }

    public RemoteObject getRemoteObject() {

        return remoteObject;
    }

    private boolean canFinish() {

        if (objectPrompt.getLibraryName().trim().length() == 0) {
            return false;
        }

        if (objectPrompt.getObjectName().trim().length() == 0) {
            return false;
        }

        return true;
    }

    private void loadScreenValues() {

        String connectionName = getDialogBoundsSettings().get(CONNECTION_NAME);
        if (connectionName != null) {
            connectionCombo.getCombo().setText(connectionName);
        }

        if (!StringHelper.isNullOrEmpty(libraryName)) {
            objectPrompt.getLibraryCombo().setText(libraryName);
        } else {
            String libraryName = getDialogBoundsSettings().get(LIBRARY_NAME);
            if (libraryName != null) {
                objectPrompt.getLibraryCombo().setText(libraryName);
            }
        }

        if (!StringHelper.isNullOrEmpty(objectName)) {
            objectPrompt.getObjectCombo().setText(objectName);
        } else {
            String objectName = getDialogBoundsSettings().get(FILE_NAME);
            if (objectName != null) {
                objectPrompt.getObjectCombo().setText(objectName);
            }
        }
    }

    private void saveScreenValues() {

        getDialogBoundsSettings().put(CONNECTION_NAME, connectionCombo.getCombo().getText().trim());
        getDialogBoundsSettings().put(LIBRARY_NAME, objectPrompt.getLibraryCombo().getText().trim().toUpperCase());
        getDialogBoundsSettings().put(FILE_NAME, objectPrompt.getObjectCombo().getText().trim().toUpperCase());
    }

    /**
     * Overridden to let {@link XDialog} store the state of this dialog in a
     * separate section of the dialog settings file.
     */
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return super.getDialogBoundsSettings(ISpherePlugin.getDefault().getDialogSettings());
    }
}
