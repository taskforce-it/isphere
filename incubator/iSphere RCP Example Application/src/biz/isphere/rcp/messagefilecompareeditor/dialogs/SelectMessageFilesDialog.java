/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rcp.messagefilecompareeditor.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.ibm.as400.access.AS400;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.swt.widgets.HistoryCombo;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.connectioncombo.ConnectionCombo;
import biz.isphere.core.swt.widgets.objectselector.ISelectQSYSObjectDialog;
import biz.isphere.core.swt.widgets.objectselector.SelectQSYSObjectDialog;
import biz.isphere.rcp.Messages;

public class SelectMessageFilesDialog extends XDialog {

    private static final String LEFT_CONNECTION_NAME = "LEFT_CONNECTION_NAME"; //$NON-NLS-1$
    private static final String LEFT_LIBRARY_NAME = "LEFT_LIBRARY_NAME"; //$NON-NLS-1$
    private static final String LEFT_FILE_NAME = "LEFT_FILE_NAME"; //$NON-NLS-1$

    private static final String RIGHT_CONNECTION_NAME = "RIGHT_CONNECTION_NAME"; //$NON-NLS-1$
    private static final String RIGHT_LIBRARY_NAME = "RIGHT_LIBRARY_NAME"; //$NON-NLS-1$
    private static final String RIGHT_FILE_NAME = "RIGHT_FILE_NAME"; //$NON-NLS-1$

    private static final String EMPTY = ""; //$NON-NLS-1$

    private ConnectionCombo cboLeftConnection;
    private HistoryCombo cboLeftLibrary;
    private HistoryCombo cboLeftMessageFile;
    private ConnectionCombo cboRightConnection;
    private HistoryCombo cboRightLibrary;
    private HistoryCombo cboRightMessageFile;

    private Button btnBrowseLeftLibrary;
    private Button btnBrowseLeftMessageFile;
    private Button btnBrowseRightLibrary;
    private Button btnBrowseRightMessageFile;

    private String leftConnectionName;
    private String leftLibraryName;
    private String leftMessageFileName;
    private String rightConnectionName;
    private String rightLibraryName;
    private String rightMessageFileName;

    public SelectMessageFilesDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);

        newShell.setText(Messages.Title_Select_Message_Files);
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        Composite dialogArea = (Composite)super.createDialogArea(parent);
        dialogArea.setLayout(new GridLayout(1, false));

        // Left message file
        Group grpLeftMessageFile = createMessageFileGroup(dialogArea);

        cboLeftConnection = createConnectionCombo(grpLeftMessageFile);
        cboLeftLibrary = createNameHistoryCombo(grpLeftMessageFile, Messages.Label_Library);
        btnBrowseLeftLibrary = WidgetFactory.createPushButton(grpLeftMessageFile, Messages.Button_Browse);
        cboLeftMessageFile = createNameHistoryCombo(grpLeftMessageFile, Messages.Label_Message_File);
        btnBrowseLeftMessageFile = WidgetFactory.createPushButton(grpLeftMessageFile, Messages.Button_Browse);

        // Right message file
        Group grpRightMessageFile = createMessageFileGroup(dialogArea);

        cboRightConnection = createConnectionCombo(grpRightMessageFile);
        cboRightLibrary = createNameHistoryCombo(grpRightMessageFile, Messages.Label_Library);
        btnBrowseRightLibrary = WidgetFactory.createPushButton(grpRightMessageFile, Messages.Button_Browse);
        cboRightMessageFile = createNameHistoryCombo(grpRightMessageFile, Messages.Label_Message_File);
        btnBrowseRightMessageFile = WidgetFactory.createPushButton(grpRightMessageFile, Messages.Button_Browse);

        loadScreenValues();

        configureControls();

        setControlsEnablement();

        return dialogArea;
    }

    private Group createMessageFileGroup(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout(3, false));
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        return group;
    }

    private ConnectionCombo createConnectionCombo(Composite parent) {
        new Label(parent, SWT.NONE).setText(Messages.Label_Connection);
        ConnectionCombo combo = WidgetFactory.createConnectionCombo(parent, SWT.NONE);
        combo.setLayoutData(createGridData());
        new Label(parent, SWT.NONE); // filler
        return combo;
    }

    private HistoryCombo createNameHistoryCombo(Group parent, String label) {
        new Label(parent, SWT.NONE).setText(label);
        HistoryCombo combo = WidgetFactory.createNameHistoryCombo(parent);
        combo.setLayoutData(createGridData());
        return combo;
    }

    private GridData createGridData() {
        return new GridData(GridData.FILL_HORIZONTAL);
    }

    @Override
    protected void okPressed() {

        // Left message file is required
        if (StringHelper.isNullOrEmpty(cboLeftConnection.getText())) {
            displayErrorMessage(Messages.Error_Please_select_a_connection);
            cboLeftConnection.setFocus();
            return;
        } else if (!checkConnection(cboLeftConnection.getText())) {
            displayErrorMessage(Messages.Error_Connection_A_not_available, cboLeftConnection.getText());
            cboLeftConnection.setFocus();
            return;
        }

        if (StringHelper.isNullOrEmpty(cboLeftLibrary.getText())) {
            displayErrorMessage(Messages.Error_Library_name_is_missing);
            cboLeftLibrary.setFocus();
            return;
        } else if (!checkLibrary(cboLeftConnection.getText(), cboLeftLibrary.getText())) {
            displayErrorMessage(Messages.Error_Library_A_not_found, cboLeftLibrary.getText());
            cboLeftLibrary.setFocus();
            return;
        }

        if (StringHelper.isNullOrEmpty(cboLeftMessageFile.getText())) {
            displayErrorMessage(Messages.Error_Message_file_name_is_missing);
            cboLeftMessageFile.setFocus();
            return;
        }

        if (!checkMessageFile(cboLeftConnection.getText(), cboLeftLibrary.getText(), cboLeftMessageFile.getText())) {
            displayErrorMessage(Messages.Error_Message_file_B_in_library_A_not_found, cboLeftLibrary.getText(), cboLeftMessageFile.getText());
            cboLeftMessageFile.setFocus();
            return;
        }

        // Right message file is optional
        boolean isRightMessageFile = true;
        if (StringHelper.isNullOrEmpty(cboRightConnection.getText())) {
            cboRightConnection.setFocus();
            isRightMessageFile = false;
        } else if (!checkConnection(cboRightConnection.getText())) {
            displayErrorMessage(Messages.Error_Connection_A_not_available, cboRightConnection.getText());
            cboRightConnection.setFocus();
            return;
        }

        if (StringHelper.isNullOrEmpty(cboRightLibrary.getText())) {
            cboRightLibrary.setFocus();
            isRightMessageFile = false;
        } else if (!checkLibrary(cboRightConnection.getText(), cboRightLibrary.getText())) {
            displayErrorMessage(Messages.Error_Library_A_not_found, cboRightLibrary.getText());
            cboRightLibrary.setFocus();
            return;
        }

        if (StringHelper.isNullOrEmpty(cboRightMessageFile.getText())) {
            cboRightMessageFile.setFocus();
            isRightMessageFile = false;
        }

        if (isRightMessageFile && !checkMessageFile(cboRightConnection.getText(), cboRightLibrary.getText(), cboRightMessageFile.getText())) {
            displayErrorMessage(Messages.Error_Message_file_B_in_library_A_not_found, cboRightLibrary.getText(), cboRightMessageFile.getText());
            cboRightMessageFile.setFocus();
            return;
        }

        // Set selected message files
        leftConnectionName = cboLeftConnection.getText();
        leftLibraryName = cboLeftLibrary.getText();
        leftMessageFileName = cboLeftMessageFile.getText();

        rightConnectionName = cboRightConnection.getText();
        rightLibraryName = cboRightLibrary.getText();
        rightMessageFileName = cboRightMessageFile.getText();

        saveScreenValues();

        super.okPressed();
    }

    private void displayErrorMessage(String nlsMessage, String... variables) {
        String message = NLS.bind(nlsMessage, variables);
        MessageDialog.openError(getShell(), Messages.Title_E_R_R_O_R, message);
    }

    private boolean checkConnection(String connectionName) {

        if (!IBMiHostContributionsHandler.isAvailable(connectionName)) {
            return false;
        }

        if (IBMiHostContributionsHandler.isOffline(connectionName)) {
            return false;
        }

        return true;
    }

    private boolean checkLibrary(String connectionName, String libraryName) {
        AS400 system = IBMiHostContributionsHandler.getSystem(connectionName);
        return ISphereHelper.checkObject(system, ISeries.QSYS_LIBRARY, libraryName, ISeries.LIB);
    }

    private boolean checkMessageFile(String connectionName, String libraryName, String messageFileName) {
        AS400 system = IBMiHostContributionsHandler.getSystem(connectionName);
        return ISphereHelper.checkObject(system, libraryName, messageFileName, ISeries.MSGF);
    }

    private void setControlsEnablement() {

        if (StringHelper.isNullOrEmpty(cboLeftConnection.getText())) {
            btnBrowseLeftLibrary.setEnabled(false);
            btnBrowseLeftMessageFile.setEnabled(false);
        } else {
            btnBrowseLeftLibrary.setEnabled(true);
            btnBrowseLeftMessageFile.setEnabled(true);
        }

        if (StringHelper.isNullOrEmpty(cboLeftLibrary.getText())) {
            btnBrowseLeftMessageFile.setEnabled(false);
        }

        if (StringHelper.isNullOrEmpty(cboRightConnection.getText())) {
            btnBrowseRightLibrary.setEnabled(false);
            btnBrowseRightMessageFile.setEnabled(false);
        } else {
            btnBrowseRightLibrary.setEnabled(true);
            btnBrowseRightMessageFile.setEnabled(true);
        }

        if (StringHelper.isNullOrEmpty(cboRightLibrary.getText())) {
            btnBrowseRightMessageFile.setEnabled(false);
        }
    }

    private void configureControls() {

        ControlEnablementListener controlsEnablementListerner = new ControlEnablementListener();

        // Left message file
        cboLeftConnection.addSelectionListener(controlsEnablementListerner);
        cboLeftLibrary.addModifyListener(controlsEnablementListerner);
        cboLeftMessageFile.addModifyListener(controlsEnablementListerner);

        btnBrowseLeftLibrary.addSelectionListener(new BrowseLibraryListener(getShell(), cboLeftConnection, cboLeftLibrary));
        btnBrowseLeftMessageFile
            .addSelectionListener(new BrowseMessageFileListener(getShell(), cboLeftConnection, cboLeftLibrary, cboLeftMessageFile));

        // Right message file
        cboRightConnection.addSelectionListener(controlsEnablementListerner);
        cboRightLibrary.addModifyListener(controlsEnablementListerner);
        cboRightMessageFile.addModifyListener(controlsEnablementListerner);

        btnBrowseRightLibrary.addSelectionListener(new BrowseLibraryListener(getShell(), cboRightConnection, cboRightLibrary));
        btnBrowseRightMessageFile
            .addSelectionListener(new BrowseMessageFileListener(getShell(), cboRightConnection, cboRightLibrary, cboRightMessageFile));
    }

    private void loadScreenValues() {

        cboLeftConnection.setText(loadValue(LEFT_CONNECTION_NAME, EMPTY));
        cboLeftLibrary.setText(loadValue(LEFT_LIBRARY_NAME, EMPTY));
        cboLeftMessageFile.setText(loadValue(LEFT_FILE_NAME, EMPTY));

        cboRightConnection.setText(loadValue(RIGHT_CONNECTION_NAME, EMPTY));
        cboRightLibrary.setText(loadValue(RIGHT_LIBRARY_NAME, EMPTY));
        cboRightMessageFile.setText(loadValue(RIGHT_FILE_NAME, EMPTY));

        cboLeftLibrary.load(getDialogSettingsManager(), LEFT_LIBRARY_NAME);
        cboLeftMessageFile.load(getDialogSettingsManager(), LEFT_FILE_NAME);

        cboRightLibrary.load(getDialogSettingsManager(), RIGHT_LIBRARY_NAME);
        cboRightMessageFile.load(getDialogSettingsManager(), RIGHT_FILE_NAME);
    }

    private void saveScreenValues() {

        storeValue(LEFT_CONNECTION_NAME, leftConnectionName);
        storeValue(LEFT_LIBRARY_NAME, leftLibraryName);
        storeValue(LEFT_FILE_NAME, leftMessageFileName);

        storeValue(RIGHT_CONNECTION_NAME, rightConnectionName);
        storeValue(RIGHT_LIBRARY_NAME, rightLibraryName);
        storeValue(RIGHT_FILE_NAME, rightMessageFileName);

        saveHistoryCombo(cboLeftLibrary);
        saveHistoryCombo(cboLeftMessageFile);

        saveHistoryCombo(cboRightLibrary);
        saveHistoryCombo(cboRightMessageFile);
    }

    private void saveHistoryCombo(HistoryCombo combo) {
        if (!StringHelper.isNullOrEmpty(combo.getText())) {
            combo.updateHistory(combo.getText());
            combo.store();
        }
    }

    public String getLeftConnectionName() {
        return leftConnectionName;
    }

    public String getLeftLibraryName() {
        return leftLibraryName;
    }

    public String getLeftMessageFIleName() {
        return leftMessageFileName;
    }

    public String getRightConnectionName() {
        return rightConnectionName;
    }

    public String getRightLibraryName() {
        return rightLibraryName;
    }

    public String getRightMessageFIleName() {
        return rightMessageFileName;
    }

    /**
     * Overridden to make this dialog resizable.
     */
    @Override
    protected boolean isResizable() {
        return true;
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

        @Override
        public void modifyText(ModifyEvent e) {
            setControlsEnablement();
        }
    }

    private class BrowseLibraryListener extends SelectionAdapter implements ModifyListener {

        private Shell shell;
        private ConnectionCombo cboConnection;
        private HistoryCombo cboLibrary;

        public BrowseLibraryListener(Shell shell, ConnectionCombo cboConnection, HistoryCombo cboLibrary) {
            this.shell = shell;
            this.cboConnection = cboConnection;
            this.cboLibrary = cboLibrary;
        }

        @Override
        public void widgetSelected(SelectionEvent event) {
            ISelectQSYSObjectDialog dialog = SelectQSYSObjectDialog.createSelectLibraryDialog(shell, cboConnection.getText());
            dialog.setLibraryListEnabled(true);
            if (dialog.open() == Dialog.OK) {
                cboLibrary.setText(dialog.getSelectedItem().getName());
            }
        }

        @Override
        public void modifyText(ModifyEvent e) {
            setControlsEnablement();
        }
    }

    private class BrowseMessageFileListener extends SelectionAdapter implements ModifyListener {

        private Shell shell;
        private ConnectionCombo cboConnection;
        private HistoryCombo cboLibrary;
        private HistoryCombo cboMessageFile;

        public BrowseMessageFileListener(Shell shell, ConnectionCombo cboConnection, HistoryCombo cboLibrary, HistoryCombo cboMessageFile) {
            this.shell = shell;
            this.cboConnection = cboConnection;
            this.cboLibrary = cboLibrary;
            this.cboMessageFile = cboMessageFile;
        }

        @Override
        public void widgetSelected(SelectionEvent event) {
            ISelectQSYSObjectDialog dialog = SelectQSYSObjectDialog.createSelectMessageFileDialog(shell, cboConnection.getText());
            dialog.setLibraryListEnabled(true);
            dialog.addLibrary(cboLibrary.getText());
            if (dialog.open() == Dialog.OK) {
                cboMessageFile.setText(dialog.getSelectedItem().getName());
            }
        }

        @Override
        public void modifyText(ModifyEvent e) {
            setControlsEnablement();
        }
    }
}
