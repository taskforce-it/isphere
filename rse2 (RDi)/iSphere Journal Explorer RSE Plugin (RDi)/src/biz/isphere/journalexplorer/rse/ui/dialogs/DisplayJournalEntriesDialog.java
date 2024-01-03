/*******************************************************************************
 * Copyright (c) 2012-2023 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.rse.ui.dialogs;

import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.ibm.etools.iseries.rse.ui.widgets.QSYSMemberPrompt;
import com.ibm.etools.iseries.rse.ui.widgets.QSYSObjectPrompt;
import com.ibm.etools.iseries.services.qsys.api.IQSYSLibrary;
import com.ibm.etools.iseries.services.qsys.api.IQSYSMember;
import com.ibm.etools.iseries.services.qsys.api.IQSYSObject;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.base.swt.widgets.UpperCaseOnlyVerifier;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.connectioncombo.ConnectionCombo;
import biz.isphere.journalexplorer.rse.Messages;
import biz.isphere.rse.connection.ConnectionManager;

/**
 * This dialog is used for entering the object, whose journal entries are going
 * to be loaded in the iSphere Journal Explorer. The associated action can be
 * accessed from the main menu: iSphere -> Display Journal Entries.
 */
public class DisplayJournalEntriesDialog extends XDialog {

    private static final String BASE_SETTINGS_KEY = "biz.isphere.journalexplorer.rse.ui."; //$NON-NLS-1$

    private static final String EMPTY = ""; //$NON-NLS-1$
    private static final String FIRST = "*FIRST"; //$NON-NLS-1$
    private static final String ASTERISK = "*"; //$NON-NLS-1$

    private static final String CONNECTION_NAME = "CONNECTION_NAME"; //$NON-NLS-1$
    private static final String OBJECT_TYPE = "type"; //$NON-NLS-1$
    private static final String LIBRARY_NAME = "LIBRARY_NAME"; //$NON-NLS-1$
    private static final String OBJECT_NAME = "OBJECT_NAME"; //$NON-NLS-1$
    private static final String MEMBER_NAME = "MEMBER_NAME"; //$NON-NLS-1$

    private static final String OBJECT_TYPE_LIB = "lib"; //$NON-NLS-1$
    private static final String OBJECT_TYPE_SRC = "src"; //$NON-NLS-1$
    private static final String OBJECT_TYPE_MBR = "mbr"; //$NON-NLS-1$

    private static final String[] OBJECT_TYPES = new String[] { "*DTAARA", "*DTAQ", "*FILE", "*JRN" }; //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$

    private String qualifiedConnectionName;
    private String objectType;
    private String libraryName;
    private String objectName;
    private String memberName;

    private ConnectionCombo connectionCombo;
    private Combo objectTypeCombo;
    private Group objectGroup;
    private QSYSObjectPrompt objectPrompt;

    public DisplayJournalEntriesDialog(Shell parentShell) {
        super(parentShell);
    }

    /**
     * Overridden to set the window title.
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.DisplayJournalEntriesDialog_Title);
    }

    @Override
    protected Control createContents(Composite parent) {

        loadScreenValues();

        Control control = super.createContents(parent);

        setInitialValues();

        return control;
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        Composite mainArea = new Composite(parent, SWT.NONE);
        mainArea.setLayout(new GridLayout(2, false));
        mainArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        connectionCombo = createConnectionCombo(mainArea);
        objectTypeCombo = createObjectTypeCombo(mainArea);
        objectPrompt = createObjectOrMemberPrompt(mainArea);

        return mainArea;
    }

    private ConnectionCombo createConnectionCombo(Composite parent) {

        Label connectionLabel = new Label(parent, SWT.NONE);
        connectionLabel.setText(Messages.Label_Connection);

        SelectionListener selectionListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // setOKButtonEnablement();
                objectPrompt.setSystemConnection(getHost(getCurrentConnectionName()));
            }
        };

        connectionCombo = WidgetFactory.createConnectionCombo(parent);
        connectionCombo.setLayoutData(getGridData());

        connectionCombo.addSelectionListener(selectionListener);

        return connectionCombo;
    }

    private Combo createObjectTypeCombo(Composite parent) {

        Label objectTypeLabel = new Label(parent, SWT.NONE);
        objectTypeLabel.setText(Messages.Label_Object_type);

        Combo combo = WidgetFactory.createReadOnlyCombo(parent);
        combo.setItems(OBJECT_TYPES);

        SelectionListener selectionListener = new SelectionAdapter() {

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }

            @Override
            public void widgetSelected(SelectionEvent e) {
                Combo combo = (Combo)e.widget;
                objectPrompt = createObjectOrMemberPrompt(combo.getParent());
            }
        };

        combo.addSelectionListener(selectionListener);

        ModifyListener modifyListener = new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                Combo combo = (Combo)e.widget;
                objectPrompt = createObjectOrMemberPrompt(combo.getParent());
            }
        };

        combo.addModifyListener(modifyListener);

        return combo;
    }

    private QSYSObjectPrompt createObjectOrMemberPrompt(Composite parent) {

        QSYSObjectPrompt objectPrompt;

        String savedLibraryName = null;
        String savedObjectName = null;
        String savedMemberName = null;

        if (objectGroup != null) {
            savedLibraryName = getCurrentLibraryName();
            savedObjectName = getCurrentObjectName();
            savedMemberName = getCurrentMemberName();
            objectGroup.dispose();
        }

        objectGroup = new Group(parent, SWT.NONE);
        objectGroup.setLayout(new GridLayout());
        objectGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

        if (ISeries.FILE.equals(getCurrentObjectType())) {
            objectPrompt = createMemberPrompt(objectGroup);
        } else {
            objectPrompt = createObjectPrompt(objectGroup);
        }

        if (savedLibraryName != null) {
            objectPrompt.getLibraryCombo().setText(savedLibraryName);
            objectPrompt.getObjectCombo().setText(savedObjectName);

            if (objectPrompt instanceof QSYSMemberPrompt) {
                QSYSMemberPrompt memberPrompt = (QSYSMemberPrompt)objectPrompt;
                memberPrompt.getMemberCombo().setText(savedMemberName);
            }
        }

        parent.layout();

        return objectPrompt;
    }

    private QSYSObjectPrompt createObjectPrompt(Composite parent) {

        QSYSObjectPrompt objectPrompt = new QSYSObjectPrompt(objectGroup, SWT.NONE, false, false);
        objectPrompt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        objectPrompt.getObjectCombo().setHistoryKey(getMemberPromptHistoryKey(OBJECT_TYPE_SRC));
        objectPrompt.getLibraryCombo().setHistoryKey(getMemberPromptHistoryKey(OBJECT_TYPE_LIB));

        objectPrompt.getObjectCombo().setAutoUpperCase(true);
        objectPrompt.getLibraryCombo().setAutoUpperCase(true);

        objectPrompt.getObjectCombo().getCombo().addVerifyListener(new UpperCaseOnlyVerifier());
        objectPrompt.getLibraryCombo().getCombo().addVerifyListener(new UpperCaseOnlyVerifier());

        return objectPrompt;
    }

    private QSYSMemberPrompt createMemberPrompt(Composite parent) {

        QSYSMemberPrompt memberPrompt = new QSYSMemberPrompt(objectGroup, SWT.NONE, false, false, QSYSMemberPrompt.FILETYPE_SRC);
        memberPrompt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        memberPrompt.getMemberCombo().setHistoryKey(getMemberPromptHistoryKey(OBJECT_TYPE_MBR));
        memberPrompt.getFileCombo().setHistoryKey(getMemberPromptHistoryKey(OBJECT_TYPE_SRC));
        memberPrompt.getLibraryCombo().setHistoryKey(getMemberPromptHistoryKey(OBJECT_TYPE_LIB));

        memberPrompt.getMemberCombo().setAutoUpperCase(true);
        memberPrompt.getFileCombo().setAutoUpperCase(true);
        memberPrompt.getLibraryCombo().setAutoUpperCase(true);

        memberPrompt.getMemberCombo().getCombo().addVerifyListener(new UpperCaseOnlyVerifier());
        memberPrompt.getFileCombo().getCombo().addVerifyListener(new UpperCaseOnlyVerifier());
        memberPrompt.getLibraryCombo().getCombo().addVerifyListener(new UpperCaseOnlyVerifier());

        return memberPrompt;
    }

    protected GridData getGridData() {
        return new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
    }

    protected String getMemberPromptHistoryKey(String objectType) {
        return BASE_SETTINGS_KEY + "." + objectType; //$NON-NLS-1$
    }

    private IBMiConnection getConnection(String qualifiedConnectionName) {

        if (StringHelper.isNullOrEmpty(qualifiedConnectionName)) {
            return null;
        }

        IBMiConnection ibMiConnection = ConnectionManager.getIBMiConnection(qualifiedConnectionName);
        if (ibMiConnection == null) {
            return null;
        }

        return ibMiConnection;
    }

    private IHost getHost(String qualifiedConnectionName) {

        if (StringHelper.isNullOrEmpty(qualifiedConnectionName)) {
            return null;
        }

        IBMiConnection ibMiConnection = ConnectionManager.getIBMiConnection(qualifiedConnectionName);
        if (ibMiConnection == null) {
            return null;
        }

        IHost host = ibMiConnection.getHost();
        return host;
    }

    private String getCurrentConnectionName() {
        return getCurrentConnectionName(false);
    }

    private String getCurrentConnectionName(boolean simpleName) {

        if (simpleName) {
            String simpleConnectionName = connectionCombo.getText();
            return simpleConnectionName;
        }

        String qualifiedConnectionName = connectionCombo.getQualifiedConnectionName();
        return qualifiedConnectionName;
    }

    private String getCurrentObjectType() {
        String objectName = objectTypeCombo.getText();
        return objectName;
    }

    private String getCurrentLibraryName() {
        String libraryName = objectPrompt.getLibraryName();
        return libraryName;
    }

    private String getCurrentObjectName() {
        String objectName = objectPrompt.getObjectName();
        return objectName;
    }

    private String getCurrentMemberName() {

        String memberName;
        if (objectPrompt instanceof QSYSMemberPrompt) {
            QSYSMemberPrompt memberPrompt = (QSYSMemberPrompt)objectPrompt;
            memberName = memberPrompt.getMemberName();
            if (StringHelper.isNullOrEmpty(memberName)) {
                memberName = FIRST;
            }
        } else {
            memberName = EMPTY;
        }

        return memberName;
    }

    private void setInitialValues() {

        IBMiConnection connection = getConnection(qualifiedConnectionName);
        if (connection != null) {
            connectionCombo.setQualifiedConnectionName(ConnectionManager.getConnectionName(connection));
        }

        objectTypeCombo.setText(objectType);

        objectPrompt.getLibraryCombo().setText(libraryName);
        objectPrompt.getObjectCombo().setText(objectName);

        if (objectPrompt instanceof QSYSMemberPrompt) {
            QSYSMemberPrompt memberPrompt = (QSYSMemberPrompt)objectPrompt;
            memberPrompt.getMemberCombo().setText(memberName);
        }
    }

    private void loadScreenValues() {

        qualifiedConnectionName = loadValue(CONNECTION_NAME, EMPTY);
        objectType = loadValue(OBJECT_TYPE, ISeries.FILE);
        libraryName = loadValue(LIBRARY_NAME, EMPTY);
        objectName = loadValue(OBJECT_NAME, EMPTY);
        memberName = loadValue(MEMBER_NAME, FIRST);
    }

    private void storeScreenValues() {

        storeValue(CONNECTION_NAME, connectionCombo.getQualifiedConnectionName());
        storeValue(OBJECT_TYPE, objectTypeCombo.getText());

        storeValue(LIBRARY_NAME, objectPrompt.getLibraryName());
        storeValue(OBJECT_NAME, objectPrompt.getObjectName());

        if (objectPrompt instanceof QSYSMemberPrompt) {
            QSYSMemberPrompt memberPrompt = (QSYSMemberPrompt)objectPrompt;
            storeValue(MEMBER_NAME, memberPrompt.getMemberName());
        }

        objectPrompt.updateHistory();
    }

    /**
     * Returns the selected qualified connection name.
     * 
     * @return qualified connection name
     */
    public String getQualifiedConnectionName() {
        return qualifiedConnectionName;
    }

    /**
     * Returns the selected object type, which is one of
     * <code>*DTAARA, *DTAQ, *FILE or *JRN</code>.
     * 
     * @return object type
     */
    public String getObjectType() {
        return objectType;
    }

    /**
     * Returns the selected library name.
     * 
     * @return library name
     */
    public String getLibraryName() {
        return libraryName;
    }

    /**
     * returns the selected object name.
     * 
     * @return object name
     */
    public String getObjectName() {
        return objectName;
    }

    /**
     * Returns the selected member name.
     * 
     * @return member name.
     */
    public String getMemberName() {
        return memberName;
    }

    @Override
    protected void okPressed() {

        if (!validateInput()) {
            return;
        }

        qualifiedConnectionName = getCurrentConnectionName();
        objectType = getCurrentObjectType();
        libraryName = getCurrentLibraryName();
        objectName = getCurrentObjectName();
        memberName = getCurrentMemberName();

        storeScreenValues();

        super.okPressed();
    }

    private boolean validateInput() {

        String qualifiedConnectionName = getCurrentConnectionName();

        if (StringHelper.isNullOrEmpty(qualifiedConnectionName) || ConnectionManager.getIBMiConnection(qualifiedConnectionName) == null) {
            String simpleConnectionName = getCurrentConnectionName(true);
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.bind(Messages.Error_Connection_not_found_A, simpleConnectionName));
            return false;
        }

        if (!new HashSet<Object>(Arrays.asList(OBJECT_TYPES)).contains(getCurrentObjectType())) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.bind(Messages.Error_Invalid_object_type_A, getCurrentObjectType()));
            return false;
        }

        if (ISeries.FILE.equals(getCurrentObjectType())) {
            return validateMember();
        } else {
            return validateObject();
        }
    }

    private boolean validateMember() {

        if (!validateObject()) {
            return false;
        }

        IBMiConnection connection = getConnection(getCurrentConnectionName());
        String libraryName = getCurrentLibraryName();
        String objectName = getCurrentObjectName();
        String memberName = getCurrentMemberName();

        QSYSMemberPrompt memberPrompt = (QSYSMemberPrompt)objectPrompt;

        if (!checkMember(connection, libraryName, objectName, memberName)) {
            displayMemberNotFoundMessage(libraryName, objectName, memberName, memberPrompt);
            return false;
        }

        return true;
    }

    private boolean validateObject() {

        IBMiConnection connection = getConnection(getCurrentConnectionName());
        String libraryName = getCurrentLibraryName();
        String objectName = getCurrentObjectName();
        String objectType = getCurrentObjectType();

        if (!checkLibrary(connection, libraryName)) {
            displayLibraryNotFoundMessage(libraryName, objectPrompt);
            return false;
        }

        if (!checkObject(connection, libraryName, objectName, objectType)) {
            displayObjectNotFoundMessage(libraryName, objectName, objectType, objectPrompt);
            return false;
        }

        return true;
    }

    private boolean checkLibrary(IBMiConnection connection, String libraryName) {

        IQSYSLibrary qsysLibrary = null;
        try {
            qsysLibrary = connection.getLibrary(libraryName, null);
        } catch (Exception e) {
        }

        if (qsysLibrary != null) {
            return true;
        }

        return false;
    }

    private void displayLibraryNotFoundMessage(String libraryName, QSYSObjectPrompt qsysObjectPrompt) {

        String message = Messages.bind(Messages.Error_Library_A_not_found, new Object[] { libraryName });
        MessageDialog.openError(getShell(), Messages.E_R_R_O_R, message);
        qsysObjectPrompt.getLibraryCombo().setFocus();
    }

    private boolean checkObject(IBMiConnection connection, String libraryName, String objectName, String objectType) {

        IQSYSObject qsysObject = null;
        try {
            qsysObject = connection.getObject(libraryName, objectName, objectType, null, false);
        } catch (Exception e) {
        }

        if (qsysObject != null) {
            return true;
        }

        return false;
    }

    private void displayObjectNotFoundMessage(String libraryName, String objectName, String objectType, QSYSObjectPrompt qsysObjectPrompt) {

        String message = Messages.bind(Messages.Error_Object_A_of_type_C_in_library_B_not_found,
            new Object[] { objectName, libraryName, objectType });
        MessageDialog.openError(getShell(), Messages.E_R_R_O_R, message);
        qsysObjectPrompt.getObjectCombo().setFocus();
    }

    private boolean checkMember(IBMiConnection connection, String libraryName, String fileName, String memberName) {

        IQSYSMember qsysMember = null;
        try {
            if (memberName.startsWith(ASTERISK)) {
                memberName = ISphereHelper.resolveMemberName(connection.getAS400ToolboxObject(), libraryName, fileName, memberName);
            }
            qsysMember = connection.getMember(libraryName, fileName, memberName, null, false);
        } catch (Exception e) {
        }

        if (qsysMember != null) {
            return true;
        }

        return false;
    }

    private void displayMemberNotFoundMessage(String libraryName, String fileName, String memberName, QSYSMemberPrompt qsysMemberPrompt) {

        String message = Messages.bind(Messages.Error_Member_2_of_file_1_in_library_0_not_found, new Object[] { libraryName, fileName, memberName });
        MessageDialog.openError(getShell(), Messages.E_R_R_O_R, message);
        qsysMemberPrompt.getMemberCombo().setFocus();

    }

    /**
     * Overridden to make this dialog resizable.
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
        return new Point(360, 340);
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
