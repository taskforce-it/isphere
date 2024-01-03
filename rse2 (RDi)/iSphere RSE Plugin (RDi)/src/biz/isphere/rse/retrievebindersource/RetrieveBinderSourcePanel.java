/*******************************************************************************
 * Copyright (c) 2012-2022 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.retrievebindersource;

import org.eclipse.rse.core.model.IHost;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ibm.etools.iseries.rse.ui.widgets.QSYSFilePrompt;
import com.ibm.etools.iseries.rse.ui.widgets.QSYSMemberPrompt;
import com.ibm.etools.iseries.services.qsys.api.IQSYSFile;
import com.ibm.etools.iseries.services.qsys.api.IQSYSLibrary;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.retrievebindersource.AbstractRetrieveBinderSourcePanel;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.connectioncombo.ConnectionCombo;
import biz.isphere.rse.Messages;
import biz.isphere.rse.connection.ConnectionManager;

public class RetrieveBinderSourcePanel extends AbstractRetrieveBinderSourcePanel {

    private Composite composite;

    private ConnectionCombo connectionCombo;
    private QSYSMemberPrompt sourceFilePrompt;
    private Text sourceMemberText;
    private Button copyToClipboardCheckBox;

    private boolean isEnabledConnectionCombo;

    public RetrieveBinderSourcePanel(Composite parent, int style) {

        this.composite = new Composite(parent, style);

        clearErrorMessage();

        createContent();
    }

    private void createContent() {

        composite.setLayout(new GridLayout());

        createConnectionGroup(composite);
        createSourceMemberGroup(composite);

        setControlEnablement();
    }

    private void createConnectionGroup(Composite parent) {

        Composite connectionGroup = new Composite(parent, SWT.NONE);
        GridLayout connectionGroupLayout = new GridLayout(2, false);
        connectionGroupLayout.marginHeight = 0;
        connectionGroupLayout.marginWidth = 0;
        connectionGroup.setLayout(connectionGroupLayout);
        connectionGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label connectionLabel = new Label(connectionGroup, SWT.NONE);
        connectionLabel.setText(Messages.Connection);

        connectionCombo = WidgetFactory.createConnectionCombo(connectionGroup);
        connectionCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        setEnableConnectionCombo(true);
    }

    private void createSourceMemberGroup(Composite parent) {

        sourceFilePrompt = new QSYSMemberPrompt(parent, SWT.NONE, true, true, QSYSFilePrompt.FILETYPE_SRC);
        sourceFilePrompt.setSystemConnection(getHost(connectionCombo.getQualifiedConnectionName()));
        sourceFilePrompt.getLibraryCombo().setToolTipText(Messages.Enter_or_select_a_library_name);
        sourceFilePrompt.getObjectCombo().setToolTipText(Messages.Enter_or_select_a_simple_or_generic_file_name);
        sourceFilePrompt.getMemberCombo().setToolTipText(Messages.Enter_or_select_a_simple_or_generic_member_name);
        sourceFilePrompt.getLibraryPromptLabel().setText(Messages.Library);
        sourceFilePrompt.setObjectPromptLabel(Messages.Source_File);
        sourceFilePrompt.setMemberPromptLabel(Messages.Source_Member);

        Composite sourceMemberTextPanel = new Composite(parent, SWT.NONE);
        GridLayout sourceMemberTextLayout = new GridLayout(2, false);
        sourceMemberTextLayout.marginHeight = 0;
        sourceMemberTextLayout.marginWidth = 0;
        sourceMemberTextPanel.setLayout(sourceMemberTextLayout);
        sourceMemberTextPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label sourceMemberTextLabel = new Label(sourceMemberTextPanel, SWT.NONE);
        sourceMemberTextLabel.setText(Messages.Description);

        sourceMemberText = WidgetFactory.createText(sourceMemberTextPanel);
        sourceMemberText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        sourceMemberText.setTextLimit(50);

        WidgetFactory.createSeparator(parent);

        copyToClipboardCheckBox = WidgetFactory.createCheckbox(parent);
        copyToClipboardCheckBox.setText(Messages.Copy_to_clipboard);
        copyToClipboardCheckBox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                super.widgetSelected(event);
            }

            @Override
            public void widgetSelected(SelectionEvent event) {
                setControlEnablement();
                setFocus();
            }
        });
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

    public boolean validate() {

        clearErrorMessage();

        if (isCopyToClipboard()) {
            return true;
        }

        if (!validateConnection(getConnectionName(), connectionCombo)) {
            return false;
        }

        IBMiConnection connection = getConnection(getConnectionName());

        if (!validateLibrary(connection, getSourceFileLibrary(), sourceFilePrompt.getLibraryCombo())) {
            return false;
        }

        if (!validateFile(connection, getSourceFileLibrary(), getSourceFile(), sourceFilePrompt.getFileCombo())) {
            return false;
        }

        if (!validateMember(connection, getSourceFileLibrary(), getSourceFile(), getSourceMember(), sourceFilePrompt.getMemberCombo())) {
            return false;
        }

        return true;
    }

    public void setFocus() {

        if (isError()) {
            super.setFocus();
            return;
        }

        if (isCopyToClipboard()) {
            copyToClipboardCheckBox.setFocus();
        }

        sourceMemberText.setFocus();
    }

    public void setEnableConnectionCombo(boolean enabled) {
        isEnabledConnectionCombo = enabled;
    }

    public void setConnectionName(String qualifiedConnectionName) {

        if (StringHelper.isNullOrEmpty(qualifiedConnectionName)) {
            return;
        }

        connectionCombo.setQualifiedConnectionName(qualifiedConnectionName);
    }

    public void setSourceLibrary(String library) {
        sourceFilePrompt.getLibraryCombo().setText(library);
    }

    public void setSourceFile(String file) {
        sourceFilePrompt.getFileCombo().setText(file);
    }

    public void setSourceMember(String member) {
        sourceFilePrompt.getMemberCombo().setText(member);
    }

    public void setSourceMemberDescription(String text) {
        sourceMemberText.setText(text);
    }

    public void setCopyToClipboard(boolean copyToClipboard) {

        copyToClipboardCheckBox.setSelection(copyToClipboard);

        setControlEnablement();
    }

    public String getConnectionName() {
        return connectionCombo.getQualifiedConnectionName();
    }

    public String getSourceFileLibrary() {
        return sourceFilePrompt.getLibraryCombo().getText();
    }

    public String getSourceFile() {
        return sourceFilePrompt.getObjectCombo().getText();
    }

    public String getSourceMember() {
        return sourceFilePrompt.getMemberCombo().getText();
    }

    public String getSourceMemberDescription() {
        return sourceMemberText.getText();
    }

    public boolean isCopyToClipboard() {
        return copyToClipboardCheckBox.getSelection();
    }

    public void updateHistory() {
        sourceFilePrompt.updateHistory();
    }

    private void setControlEnablement() {

        if (copyToClipboardCheckBox.getSelection()) {
            connectionCombo.setEnabled(false);
            sourceFilePrompt.setEnabled(false);
            sourceMemberText.setEnabled(false);
        } else {
            connectionCombo.setEnabled(isEnabledConnectionCombo);
            sourceFilePrompt.setEnabled(true);
            sourceMemberText.setEnabled(true);
        }
    }

    private boolean validateConnection(String connectionName, Control control) {

        IBMiConnection connection = getConnection(connectionName);
        if (connection == null) {
            setErrorMessage(Messages.bind(Messages.Connection_A_not_found, connectionName), control);
        }

        if (isError()) {
            control.setFocus();
        }

        return !isError();
    }

    private boolean validateLibrary(IBMiConnection connection, String libraryName, Control control) {

        try {

            IQSYSLibrary library = connection.getLibrary(libraryName, null);
            if (library == null) {
                setErrorMessage(Messages.bind(Messages.Library_A_not_found, libraryName), control);
            }

        } catch (Throwable e) {
            setErrorMessage(ExceptionHelper.getLocalizedMessage(e), control);
        }

        if (isError()) {
            control.setFocus();
        }

        return !isError();
    }

    private boolean validateFile(IBMiConnection connection, String libraryName, String fileName, Control control) {

        try {

            IQSYSFile file = connection.getFile(libraryName, fileName, null);
            if (file == null) {
                setErrorMessage(Messages.bind(Messages.File_A_in_library_B_not_found, fileName, libraryName), control);
            }

        } catch (Throwable e) {
            setErrorMessage(ExceptionHelper.getLocalizedMessage(e), control);
        }

        if (isError()) {
            control.setFocus();
        }

        return !isError();
    }

    private boolean validateMember(IBMiConnection connection, String libraryName, String fileName, String memberName, Control control) {

        if (isError()) {
            control.setFocus();
        }

        return !isError();
    }

    private IBMiConnection getConnection(String qualifiedConnectionName) {

        IBMiConnection connection = ConnectionManager.getIBMiConnection(qualifiedConnectionName);

        return connection;
    }
}
