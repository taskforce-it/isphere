/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
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

import com.ibm.as400.access.AS400;

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
import biz.isphere.core.swt.widgets.objectselector.model.SelectedObject;

public class SelectRemoteQSYSObjectDialog extends XDialog implements ISelectRemoteQSYSObjectDialog {

    private static final String CONNECTION_NAME = "CONNECTION_NAME"; //$NON-NLS-1$
    private static final String LIBRARY_NAME = "LIBRARY_NAME"; //$NON-NLS-1$
    private static final String FILE_NAME = "FILE_NAME"; //$NON-NLS-1$

    private static final String EMPTY = ""; //$NON-NLS-1$

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

    public static SelectRemoteQSYSObjectDialog createSelectLibraryDialog(Shell shell, String connection) {
        return new SelectRemoteQSYSObjectDialog(shell, connection, ISeries.LIB, Messages.Library);
    }

    public static SelectRemoteQSYSObjectDialog createSelectMessageFileDialog(Shell shell, String connection) {
        return new SelectRemoteQSYSObjectDialog(shell, connection, ISeries.MSGF, Messages.Message_file);
    }

    public static SelectRemoteQSYSObjectDialog createSelectSourceFileDialog(Shell shell, String connection) {
        return new SelectRemoteQSYSObjectDialog(shell, connection, ISeries.FILE, Messages.Source_file);
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

        newShell.setText(Messages.bind(Messages.Select_A, objectLabel));
    }

    @Override
    public Control createDialogArea(Composite parent) {

        Composite dialogArea = new Composite(parent, SWT.NONE);
        dialogArea.setLayout(new GridLayout(3, false));
        dialogArea.setLayoutData(new GridData(GridData.FILL_BOTH));

        Label lblConnectionName = new Label(dialogArea, SWT.NONE);
        lblConnectionName.setText(Messages.Connection);

        cboConnectionName = WidgetFactory.createConnectionCombo(dialogArea, SWT.NONE);
        cboConnectionName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        new Label(dialogArea, SWT.NONE);

        if (!isSelectLibraryDialog()) {
            Label lblLibraryName = new Label(dialogArea, SWT.NONE);
            lblLibraryName.setText(Messages.Library);

            cboLibraryName = WidgetFactory.createNameHistoryCombo(dialogArea);
            cboLibraryName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            btnSelectLibrary = WidgetFactory.createPushButton(dialogArea, Messages.Browse);
        }

        Label lblObjectName = new Label(dialogArea, SWT.NONE);
        lblObjectName.setText(objectLabel);

        cboObjectName = WidgetFactory.createNameHistoryCombo(dialogArea);
        cboObjectName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        btnSelectObject = WidgetFactory.createPushButton(dialogArea, Messages.Browse);

        loadScreenValues();

        configureControls();

        setControlsEnablement();

        return dialogArea;
    }

    private void setControlsEnablement() {

        if (!cboConnectionName.hasConnection()) {
            setEnabled(btnSelectLibrary, false);
            setEnabled(btnSelectObject, false);
        } else {
            setEnabled(btnSelectLibrary, true);
            setEnabled(btnSelectObject, true);
        }

        if (!isSelectLibraryDialog()) {
            if (StringHelper.isNullOrEmpty(cboLibraryName.getText())) {
                setEnabled(btnSelectObject, false);
            }
        }
    }

    private void setEnabled(Control control, boolean enabled) {

        if (control != null) {
            control.setEnabled(enabled);
        }
    }

    private void configureControls() {

        ControlEnablementListener controlsEnablementListerner = new ControlEnablementListener();

        addSelectionListener(cboConnectionName, controlsEnablementListerner);

        addModifyListener(cboLibraryName, controlsEnablementListerner);
        addModifyListener(cboObjectName, controlsEnablementListerner);

        if (!isSelectLibraryDialog()) {
            btnSelectLibrary.addSelectionListener(new BrowseLibraryListener(cboConnectionName, cboLibraryName));
            btnSelectObject.addSelectionListener(new BrowseObjectListener(cboConnectionName, cboLibraryName, cboObjectName, objectType));
        } else {
            btnSelectObject.addSelectionListener(new BrowseLibraryListener(cboConnectionName, cboLibraryName));
        }

    }

    private void addSelectionListener(ConnectionCombo combo, ControlEnablementListener selectionListener) {

        if (combo != null) {
            combo.addSelectionListener(selectionListener);
        }
    }

    private void addModifyListener(HistoryCombo combo, ControlEnablementListener modifyListener) {

        if (combo != null) {
            combo.addModifyListener(modifyListener);
        }
    }

    private String getText(ConnectionCombo combo) {

        if (combo != null) {
            return combo.getQualifiedConnectionName();
        }

        return "";
    }

    private String getText(HistoryCombo combo) {

        if (combo != null) {
            return combo.getText();
        }

        return "";
    }

    @Override
    protected void okPressed() {

        connectionName = getText(cboConnectionName);
        libraryName = getText(cboLibraryName);
        objectName = getText(cboObjectName);

        try {

            AS400 system = IBMiHostContributionsHandler.getSystem(connectionName);

            if (!ISphereHelper.checkLibrary(system, libraryName)) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.bind(Messages.Library_A_not_found, libraryName));
                return;
            }

            if (!ISphereHelper.checkObject(system, libraryName, objectName, objectType)) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R,
                    Messages.bind(Messages.Object_A_of_type_C_in_Library_B_not_found_or_does_no_longer_exist,
                        new Object[] { objectName, libraryName, objectType }));
                return;
            }

        } catch (Exception e) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, e.getLocalizedMessage());
            return;
        }

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

    private boolean isSelectLibraryDialog() {

        if (ISeries.LIB.equals(objectType)) {
            return true;
        }

        return false;
    }

    private void loadScreenValues() {

        if (connectionName == null) {
            connectionName = loadValue(CONNECTION_NAME, EMPTY);
        }
        if (!StringHelper.isNullOrEmpty(connectionName)) {
            cboConnectionName.setQualifiedConnectionName(connectionName);
        }

        if (!isSelectLibraryDialog()) {
            if (libraryName == null) {
                libraryName = loadValue(LIBRARY_NAME, EMPTY);
            }
            if (!StringHelper.isNullOrEmpty(libraryName)) {
                cboLibraryName.setText(libraryName);
            }
            cboLibraryName.load(getDialogSettingsManager(), LIBRARY_NAME);
        }

        if (objectName == null) {
            objectName = loadValue(FILE_NAME, EMPTY);
        }
        if (!StringHelper.isNullOrEmpty(objectName)) {
            cboObjectName.setText(objectName);
        }
        cboObjectName.load(getDialogSettingsManager(), FILE_NAME);
    }

    private void saveScreenValues() {

        storeValue(CONNECTION_NAME, cboConnectionName.getQualifiedConnectionName());

        if (!isSelectLibraryDialog()) {
            storeValue(LIBRARY_NAME, cboLibraryName.getText());
            saveHistoryCombo(cboLibraryName);
        }

        storeValue(FILE_NAME, cboObjectName.getText());
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

    private class ControlEnablementListener extends SelectionAdapter implements ModifyListener {
        @Override
        public void widgetSelected(SelectionEvent e) {
            setControlsEnablement();
        }

        public void modifyText(ModifyEvent e) {
            setControlsEnablement();
        }
    }

    private class BrowseLibraryListener extends SelectionAdapter implements ModifyListener {

        private ConnectionCombo comboConnectionName;
        private HistoryCombo comboLibraryName;

        public BrowseLibraryListener(ConnectionCombo comboConnectionName, HistoryCombo comboLibraryName) {
            this.comboConnectionName = comboConnectionName;
            this.comboLibraryName = comboLibraryName;
        }

        @Override
        public void widgetSelected(SelectionEvent e) {

            if (!comboConnectionName.hasConnection()) {
                return;
            }

            ISelectQSYSObjectDialog dialog = SelectQSYSObjectDialog.createSelectLibraryDialog(getShell(),
                comboConnectionName.getQualifiedConnectionName());
            dialog.setLibraryListEnabled(true);
            dialog.setExpandLibraryListsEnabled(true);
            if (dialog.open() == Dialog.OK) {
                ISelectedObject selectedObject = dialog.getSelectedItem();
                ISelectedObject objectItem = (ISelectedObject)selectedObject;
                comboLibraryName.setText(objectItem.getName());
            }
        }

        public void modifyText(ModifyEvent arg0) {
            setControlsEnablement();
        }
    }

    private class BrowseObjectListener extends SelectionAdapter implements ModifyListener {

        private ConnectionCombo comboConnectionName;
        private HistoryCombo comboLibraryName;
        private HistoryCombo comboObjectName;
        private String objectType;

        public BrowseObjectListener(ConnectionCombo comboConnectionName, HistoryCombo comboLibraryName, HistoryCombo comboObjectName,
            String objectType) {
            this.comboConnectionName = comboConnectionName;
            this.comboLibraryName = comboLibraryName;
            this.comboObjectName = comboObjectName;
            this.objectType = objectType;
        }

        @Override
        public void widgetSelected(SelectionEvent e) {

            if (!comboConnectionName.hasConnection()) {
                return;
            }

            ISelectQSYSObjectDialog dialog;

            if (ISeries.MSGF.equals(objectType)) {
                dialog = SelectQSYSObjectDialog.createSelectMessageFileDialog(getShell(), comboConnectionName.getQualifiedConnectionName());
            } else if (ISeries.FILE.equals(objectType)) {
                dialog = SelectQSYSObjectDialog.createSelectSourceFileDialog(getShell(), comboConnectionName.getQualifiedConnectionName());
            } else {
                throw new IllegalArgumentException("Incorrect object type: " + objectType);
            }

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
