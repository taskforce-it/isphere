/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.objectselector;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.swt.widgets.HistoryCombo;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.connectioncombo.ConnectionCombo;
import biz.isphere.core.swt.widgets.objectselector.model.LibraryItem;
import biz.isphere.core.swt.widgets.objectselector.model.SelectedObject;

import com.ibm.as400.access.AS400;

public class SelectRemoteQSYSObjectDialog extends XDialog implements ISelectRemoteQSYSObjectDialog {

    private static final String CONNECTION_NAME = "CONNECTION_NAME"; //$NON-NLS-1$
    private static final String LIBRARY_NAME = "LIBRARY_NAME"; //$NON-NLS-1$
    private static final String FILE_NAME = "FILE_NAME"; //$NON-NLS-1$

    private String connectionName;
    private String objectType;
    private String objectLabel;

    private String libraryName;
    private String objectName;

    private ConnectionCombo cboConnectionName;
    private HistoryCombo cboLibraryName;
    private Button btnSelectLibrary;
    private HistoryCombo cboObjectName;
    private Button btnSelectObject;

    public static SelectRemoteQSYSObjectDialog createSelectMessageFileDialog(Shell shell, String connection) {
        return new SelectRemoteQSYSObjectDialog(shell, connection, ISeries.MSGF, Messages.Message_file);
    }

    private SelectRemoteQSYSObjectDialog(Shell parentShell, String connectionName, String objectType, String objectLabel) {
        super(parentShell);

        this.connectionName = connectionName;
        this.objectType = objectType;
        this.objectLabel = objectLabel;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);

        if (ISeries.LIB.equals(objectType)) {
            newShell.setText(Messages.bind("Select {0}", objectLabel));
        } else if (ISeries.MSGF.equals(objectType)) {
            newShell.setText(Messages.bind("Select {0}", objectLabel));
        } else {
            newShell.setText(Messages.bind("Select {0}", "Object"));
        }
    }

    @Override
    public Control createDialogArea(Composite parent) {

        Composite dialogArea = new Composite(parent, SWT.NONE);
        dialogArea.setLayout(new GridLayout(3, false));
        dialogArea.setLayoutData(new GridData(GridData.FILL_BOTH));

        Label lblConnectionName = new Label(dialogArea, SWT.NONE);
        lblConnectionName.setText(Messages.Connection_colon);

        cboConnectionName = WidgetFactory.createConnectionCombo(dialogArea, SWT.NONE);
        cboConnectionName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        new Label(dialogArea, SWT.NONE);

        Label lblLibraryName = new Label(dialogArea, SWT.NONE);
        lblLibraryName.setText(Messages.Library);

        cboLibraryName = WidgetFactory.createNameHistoryCombo(dialogArea);
        cboLibraryName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        btnSelectLibrary = WidgetFactory.createPushButton(dialogArea, "Browse");

        Label lblObjectName = new Label(dialogArea, SWT.NONE);
        lblObjectName.setText(objectLabel);

        cboObjectName = WidgetFactory.createNameHistoryCombo(dialogArea);
        cboObjectName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        btnSelectObject = WidgetFactory.createPushButton(dialogArea, "Browse");

        loadScreenValues();

        configureControls();

        setControlsEnablement();

        return dialogArea;
    }

    private void setControlsEnablement() {

        if (StringHelper.isNullOrEmpty(cboConnectionName.getText())) {
            btnSelectLibrary.setEnabled(false);
            btnSelectObject.setEnabled(false);
        } else {
            btnSelectLibrary.setEnabled(true);
            btnSelectObject.setEnabled(true);
        }

        if (StringHelper.isNullOrEmpty(cboLibraryName.getText())) {
            btnSelectObject.setEnabled(false);
        }
    }

    private void configureControls() {

        cboConnectionName.addSelectionListener(new SelectConnectionListener());

        SelectLibraryListener selectLibraryListener = new SelectLibraryListener(cboConnectionName, cboLibraryName);
        cboLibraryName.addModifyListener(selectLibraryListener);
        btnSelectLibrary.addSelectionListener(selectLibraryListener);

        SelectObjectListener selectObjectListener = new SelectObjectListener(cboConnectionName, cboLibraryName, cboObjectName, objectType);
        cboLibraryName.addModifyListener(selectObjectListener);
        btnSelectObject.addSelectionListener(selectObjectListener);
    }

    @Override
    protected void okPressed() {

        String connectionName = cboConnectionName.getText();
        String libraryName = cboLibraryName.getText();
        String fileName = cboObjectName.getText();

        try {

            AS400 system = IBMiHostContributionsHandler.getSystem(connectionName);

            if (!ISphereHelper.checkLibrary(system, libraryName)) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.bind(Messages.Library_A_not_found, libraryName));
                return;
            }

            if (!ISphereHelper.checkObject(system, libraryName, fileName, objectType)) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R,
                    Messages.bind("Object {0} in library {1} not_found", new Object[] { fileName, libraryName }));
                return;
            }

        } catch (Exception e) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, e.getLocalizedMessage());
            return;
        }

        connectionName = cboConnectionName.getText();
        libraryName = cboLibraryName.getText();
        objectName = cboObjectName.getText();

        saveScreenValues();

        // Close dialog
        super.okPressed();
    }

    public String getConnectionName() {
        return connectionName;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getObjectType() {
        return objectType;
    }

    public ISelectedObject getObject() {
        return new SelectedObject(getConnectionName(), getLibraryName(), getObjectName(), getObjectType());
    }

    private void loadScreenValues() {

        if (connectionName == null) {
            connectionName = getDialogBoundsSettings().get(CONNECTION_NAME);
        }
        if (!StringHelper.isNullOrEmpty(connectionName)) {
            cboConnectionName.setText(connectionName);
        }

        if (libraryName == null) {
            libraryName = getDialogBoundsSettings().get(LIBRARY_NAME);
        }
        if (!StringHelper.isNullOrEmpty(libraryName)) {
            cboLibraryName.setText(libraryName);
        }

        if (objectName == null) {
            objectName = getDialogBoundsSettings().get(FILE_NAME);
        }
        if (!StringHelper.isNullOrEmpty(objectName)) {
            cboObjectName.setText(objectName);
        }

        cboLibraryName.load(getDialogSettingsManager(), LIBRARY_NAME);
        cboObjectName.load(getDialogSettingsManager(), FILE_NAME);
    }

    private void saveScreenValues() {

        getDialogBoundsSettings().put(CONNECTION_NAME, cboConnectionName.getText());
        getDialogBoundsSettings().put(LIBRARY_NAME, cboLibraryName.getText());
        getDialogBoundsSettings().put(FILE_NAME, cboObjectName.getText());

        saveHistoryCombo(cboLibraryName);
        saveHistoryCombo(cboObjectName);
    }

    private void saveHistoryCombo(HistoryCombo combo) {
        if (!StringHelper.isNullOrEmpty(combo.getText())) {
            combo.updateHistory(combo.getText());
            combo.store();
        }
    }

    /**
     * Overridden make this dialog resizable {@link XDialog}.
     */
    @Override
    protected boolean isResizable() {
        return false;
    }

    /**
     * Overridden to let {@link XDialog} store the state of this dialog in a
     * separate section of the dialog settings file.
     */
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return super.getDialogBoundsSettings(ISpherePlugin.getDefault().getDialogSettings());
    }

    private class SelectConnectionListener extends SelectionAdapter {
        @Override
        public void widgetSelected(SelectionEvent e) {
            setControlsEnablement();
        }
    }

    private class SelectLibraryListener extends SelectionAdapter implements ModifyListener {

        private ConnectionCombo comboConnectionName;
        private HistoryCombo comboLibraryName;

        public SelectLibraryListener(ConnectionCombo comboConnectionName, HistoryCombo comboLibraryName) {
            this.comboConnectionName = comboConnectionName;
            this.comboLibraryName = comboLibraryName;
        }

        @Override
        public void widgetSelected(SelectionEvent e) {

            if (StringHelper.isNullOrEmpty(comboConnectionName.getText())) {
                return;
            }

            ISelectQSYSObjectDialog dialog = SelectQSYSObjectDialog.createSelectLibraryDialog(getShell(), comboConnectionName.getText());
            dialog.setLibraryListEnabled(true);
            dialog.setExpandLibraryListsEnabled(true);
            if (dialog.open() == Dialog.OK) {
                ISelectedObject selectedObject = dialog.getSelectedItem();
                LibraryItem objectItem = (LibraryItem)selectedObject;
                comboLibraryName.setText(objectItem.getLabel());
            }
        }

        public void modifyText(ModifyEvent arg0) {
            setControlsEnablement();
        }
    }

    private class SelectObjectListener extends SelectionAdapter implements ModifyListener {

        private ConnectionCombo comboConnectionName;
        private HistoryCombo comboLibraryName;
        private HistoryCombo comboObjectName;
        private String objectType;

        public SelectObjectListener(ConnectionCombo comboConnectionName, HistoryCombo comboLibraryName, HistoryCombo comboObjectName,
            String objectType) {
            this.comboConnectionName = comboConnectionName;
            this.comboLibraryName = comboLibraryName;
            this.comboObjectName = comboObjectName;
            this.objectType = objectType;
        }

        @Override
        public void widgetSelected(SelectionEvent e) {

            if (StringHelper.isNullOrEmpty(comboConnectionName.getText())) {
                return;
            }

            ISelectQSYSObjectDialog dialog = SelectQSYSObjectDialog.createSelectMessageFileDialog(getShell(), comboConnectionName.getText());
            dialog.setLibraryListEnabled(true);

            if (!StringHelper.isNullOrEmpty(comboLibraryName.getText())) {
                dialog.addLibrary(comboLibraryName.getText());
            }

            if (dialog.open() == Dialog.OK) {
                ISelectedObject selectedObject = dialog.getSelectedItem();
                if (objectType.equals(selectedObject.getObjectType())) {
                    comboLibraryName.setText(selectedObject.getLibrary());
                    comboObjectName.setText(selectedObject.getName());
                }
            }
        }

        public void modifyText(ModifyEvent arg0) {
            setControlsEnablement();
        }
    }
}
