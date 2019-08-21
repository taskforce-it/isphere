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

import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.iseries.core.api.ISeriesObject;
import com.ibm.etools.iseries.core.ui.widgets.ISeriesConnectionCombo;
import com.ibm.etools.iseries.core.ui.widgets.ISeriesMsgFilePrompt;
import com.ibm.etools.iseries.core.ui.widgets.ISeriesObjectPrompt;

public class RSESelectObjectDialog extends XDialog {

    private static final String CONNECTION_NAME = "CONNECTION_NAME"; //$NON-NLS-1$
    private static final String LIBRARY_NAME = "LIBRARY_NAME"; //$NON-NLS-1$
    private static final String FILE_NAME = "FILE_NAME"; //$NON-NLS-1$

    private ISeriesConnection connection;
    private String objectType;
    private String libraryName;
    private String objectName;

    private ISeriesConnectionCombo connectionCombo;
    private ISeriesObjectPrompt objectPrompt;

    private RemoteObject remoteObject;

    public static RSESelectObjectDialog createSelectMessageFileDialog(Shell shell, ISeriesConnection connection) {
        return new RSESelectObjectDialog(shell, connection, ISeries.MSGF);
    }

    private RSESelectObjectDialog(Shell parentShell, ISeriesConnection connection, String objectType) {
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

        connectionCombo = new ISeriesConnectionCombo(dialogArea, connection, false);
        connectionCombo.setLayoutData(new GridData());
        connectionCombo.getCombo().setLayoutData(new GridData());

        connectionCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                objectPrompt.setSystemConnection(connectionCombo.getSystemConnection());
            }
        });

        objectPrompt = new ISeriesMsgFilePrompt(dialogArea, SWT.NONE, false, true);
        objectPrompt.setSystemConnection(connectionCombo.getSystemConnection());
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
        ISeriesConnection connection = connectionCombo.getISeriesConnection();

        ISeriesObject[] qsysObjects = null;

        try {

            if (connection.listLibraries(getShell(), library) == null) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.bind(Messages.Library_A_not_found, library));
                return;
            }

            qsysObjects = connection.listObjects(getShell(), library, file, new String[] { objectType });
            if (qsysObjects == null) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.bind(Messages.Object_A_in_library_B_not_found, new String[] { file,
                    library }));
                return;
            }

        } catch (Exception e) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, e.getLocalizedMessage());
            return;
        }

        ISeriesObject qsysObject = qsysObjects[0];
        remoteObject = new RemoteObject(connection.getConnectionName(), qsysObject.getName(), qsysObject.getLibrary(), objectType, qsysObject
            .getDescription());

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
            String fileName = getDialogBoundsSettings().get(FILE_NAME);
            if (fileName != null) {
                objectPrompt.getObjectCombo().setText(fileName);
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
