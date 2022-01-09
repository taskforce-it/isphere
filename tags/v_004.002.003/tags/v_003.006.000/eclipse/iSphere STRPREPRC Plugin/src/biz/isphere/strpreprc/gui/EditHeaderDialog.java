/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.strpreprc.gui;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.api.qcapcmd.QCAPCMD;
import biz.isphere.core.clcommands.CLCommand;
import biz.isphere.core.clcommands.CLParser;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.swt.widgets.ContentAssistText;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.strpreprc.Messages;
import biz.isphere.strpreprc.model.StrPrePrcParser;
import biz.isphere.strpreprc.preferences.Preferences;

import com.ibm.as400.access.AS400;

public class EditHeaderDialog extends XDialog {

    public final static int HEADER = 1;
    public final static int PRE_POST_COMMAND = 2;

    public final static int PROMPT = 901;

    private final static String CONNECTION_NAME = "CONNECTION_NAME"; //$NON-NLS-1$
    private final static String COMMAND = "COMMAND"; //$NON-NLS-1$
    private final static String PARAMETERS = "PARAMETERS"; //$NON-NLS-1$
    private final static String MEMBER_TYPE = "MEMBER_TYPE"; //$NON-NLS-1$

    private String title;
    private int mode;

    private String memberType;
    private String connectionName;
    private String commandString;
    private String parameters;

    private Composite mainArea;
    private Combo comboConnections;
    private Text textCommand;
    private ContentAssistText textParameters;

    public EditHeaderDialog(Shell parentShell, String title, int mode) {
        super(parentShell);

        this.title = title;
        this.mode = mode;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(title);
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        mainArea = new Composite(parent, SWT.NONE);
        mainArea.setLayout(new GridLayout(2, false));
        mainArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label labelConnection = new Label(mainArea, SWT.NONE);
        labelConnection.setText(Messages.Connection_colon);

        comboConnections = WidgetFactory.createReadOnlyCombo(mainArea);

        Label labelCommand = new Label(mainArea, SWT.NONE);
        labelCommand.setText(Messages.Command_colon);

        textCommand = WidgetFactory.createNameText(mainArea);
        textCommand.setTextLimit(10);

        Label labelParameters = new Label(mainArea, SWT.NONE);
        labelParameters.setText(Messages.Parameters_colon);
        labelParameters.setLayoutData(new GridData(SWT.DEFAULT, SWT.BEGINNING, false, false));

        textParameters = WidgetFactory.createContentAssistText(mainArea);
        textParameters.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        textParameters.setHint(Messages.Tooltip_Header_Text);
        textParameters.enableAutoActivation(true);
        textParameters.enableAutoInsert(true);
        textParameters.setToolTipText(Messages.Tooltip_Header_Text);
        textParameters.setContentAssistProposals(StrPrePrcParser.getContentAssistProposals());

        Button insertVariable = WidgetFactory.createPushButton(mainArea, Messages.Label_Insert_Variable);
        insertVariable.setLayoutData(new GridData(SWT.END, SWT.DEFAULT, false, false, 2, 1));
        insertVariable.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                textParameters.setFocus();
                textParameters.setSelectedRange(1, 0);
                textParameters.setSelectedRange(0, 0);
                textParameters.doOperation(ISourceViewer.CONTENTASSIST_PROPOSALS);
            }
        });
        textParameters.addKeyListener(new KeyAdapter() {

            public void keyReleased(KeyEvent e) {
                if (!e.doit) {
                    return;
                }
                if (e.stateMask == SWT.CTRL) {
                    switch (e.character) {
                    case ' ':
                        textParameters.doOperation(ISourceViewer.CONTENTASSIST_PROPOSALS);
                        break;

                    case '\032':
                        textParameters.doOperation(ITextOperationTarget.UNDO);
                    }

                }
            }
        });

        createStatusLine(mainArea);

        loadScreenValues();

        setControlEnablement();

        return mainArea;
    }

    @Override
    public void setFocus() {
        if (comboConnections.getText() == null || comboConnections.getText().trim().length() == 0) {
            textCommand.setFocus();
        } else if (textCommand.getText() == null || textCommand.getText().trim().length() == 0) {
            textCommand.setFocus();
        } else {
            textParameters.setFocus();
        }
    }

    @Override
    protected void buttonPressed(int buttonId) {

        if (buttonId == PROMPT) {
            promptPressed();
            return;
        }

        super.buttonPressed(buttonId);
    }

    protected void okPressed() {

        if (!validateUserInput()) {
            return;
        }

        storeScreenValues();

        super.okPressed();
    }

    protected void promptPressed() {

        if (!validateUserInput()) {
            return;
        }

        storeScreenValues();

        setReturnCode(PROMPT);
        close();
    }

    private boolean validateUserInput() {

        String connectionName = comboConnections.getText();
        if (StringHelper.isNullOrEmpty(connectionName)) {
            setErrorMessage(Messages.Missing_connection_name);
            return false;
        }

        AS400 system = IBMiHostContributionsHandler.getSystem(connectionName);
        if (system == null) {
            setErrorMessage(Messages.Could_not_create_AS400_object);
            return false;
        }

        String command = textCommand.getText();
        if (StringHelper.isNullOrEmpty(command)) {
            setErrorMessage(Messages.Missing_object_creation_command);
            return false;
        }

        String fullCommand = (command + " " + textParameters.getText()).trim(); //$NON-NLS-1$
        int offset;
        if ((offset = validateReplacementVariables(fullCommand)) >= 0) {
            int position = offset + 1;
            String variable = retrieveReplacementVariable(offset, fullCommand);
            MessageDialog.openError(
                getShell(),
                Messages.E_R_R_O_R,
                Messages.bind(Messages.Invalid_replacement_variable_A_found_at_position_B_command_C, new String[] {
                    variable.replace("&", "&&"), Integer.toString(position), fullCommand.replaceAll("&", "&&") })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            return false;
        }

        String errorMessage = null;
        QCAPCMD qcapcmd = new QCAPCMD(system);
        if (!qcapcmd.checkCLCommand(fullCommand)) {
            errorMessage = Messages.bind(Messages.Command_A_is_invalid_or_could_not_be_found_The_original_error_message_is_B, new String[] { command,
                qcapcmd.getErrorMessage() });
        }

        if (!StringHelper.isNullOrEmpty(errorMessage)) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, errorMessage);
            return false;
        }

        return true;
    }

    private int validateReplacementVariables(String fullCommand) {

        boolean isComment = false;
        int offset = 0;
        while (offset < fullCommand.length()) {
            String currentChar = fullCommand.substring(offset, offset + 1);
            if (!isComment && "'".equals(currentChar)) { //$NON-NLS-1$
                isComment = true;
            } else if (isComment && "'".equals(currentChar)) { //$NON-NLS-1$
                isComment = false;
            }
            if (!isComment && "&".equals(currentChar)) { //$NON-NLS-1$
                if (!StrPrePrcParser.isVariable(retrieveReplacementVariable(offset, fullCommand))) {
                    return offset;
                }
            }
            offset++;
        }

        return -1;
    }

    private String retrieveReplacementVariable(int offset, String fullCommand) {

        int startOffset = offset;
        while (offset < fullCommand.length()) {
            if (!fullCommand.substring(offset, offset + 1).matches("[&a-zA-Z0-9]")) { //$NON-NLS-1$
                return fullCommand.substring(startOffset, offset);
            }
            offset++;
        }

        return fullCommand.substring(startOffset, offset);
    }

    private void setControlEnablement() {

        // if (connectionName == null) {
        // comboConnections.setEnabled(true);
        // } else {
        // comboConnections.setEnabled(false);
        // }
        //
        // if (commandString == null) {
        // textCommand.setEnabled(true);
        // } else {
        // textCommand.setEnabled(false);
        // }

    }

    private void loadScreenValues() {

        comboConnections.setItems(IBMiHostContributionsHandler.getConnectionNames());
        if (!StringHelper.isNullOrEmpty(connectionName)) {
            comboConnections.setText(connectionName);
        } else {
            comboConnections.setText(getDialogBoundsSettings().get(connectionName));
        }

        if (!StringHelper.isNullOrEmpty(commandString)) {
            textCommand.setText(commandString);
        } else {
            if (isSameMemberType()) {
                String tempCommand = getDialogBoundsSettings().get(COMMAND);
                if (!StringHelper.isNullOrEmpty(tempCommand)) {
                    textCommand.setText(tempCommand);
                }
            }
        }

        if (!StringHelper.isNullOrEmpty(parameters)) {
            textParameters.setText(parameters);
        } else {
            if (isSameMemberType()) {
                String tempParameters = getDialogBoundsSettings().get(PARAMETERS);
                if (!StringHelper.isNullOrEmpty(tempParameters)) {
                    textParameters.setText(tempParameters);
                }
            }
        }
    }

    private boolean isSameMemberType() {

        if (!StringHelper.isNullOrEmpty(memberType) && memberType.equals(getDialogBoundsSettings().get(MEMBER_TYPE))) {
            return true;
        }

        return false;
    }

    private void storeScreenValues() {

        connectionName = comboConnections.getText();
        commandString = textCommand.getText();
        parameters = textParameters.getText();

        if (!StringHelper.isNullOrEmpty(connectionName)) {
            getDialogBoundsSettings().put(CONNECTION_NAME, connectionName);
        }

        if (!StringHelper.isNullOrEmpty(commandString)) {
            getDialogBoundsSettings().put(COMMAND, commandString);
        }

        if (!StringHelper.isNullOrEmpty(memberType)) {
            getDialogBoundsSettings().put(MEMBER_TYPE, memberType);
        }
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getConnectionName() {
        return this.connectionName;
    }

    public void setCommand(String fullCommandString) {

        CLParser parser = new CLParser();
        CLCommand clCommand = parser.parseCommand(fullCommandString);
        if (clCommand != null) {
            this.commandString = clCommand.getCommand();
            this.parameters = clCommand.getParametersString().toString();
        } else {
            this.commandString = null;
            if (mode == HEADER) {
                this.parameters = Preferences.getInstance().getDefaultKeywords();
            } else {
                this.parameters = ""; //$NON-NLS-1$
            }
        }
    }

    public String getCommand() {
        return this.commandString;
    }

    public String getParameters() {
        return this.parameters;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {

        createButton(parent, PROMPT, Messages.Button_Prompt, false);

        super.createButtonsForButtonBar(parent);
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
        return new Point(530, 360);
    }

    /**
     * Overridden to let {@link XDialog} store the state of this dialog in a
     * separate section of the dialog settings file.
     */
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return super.getDialogBoundsSettings(ISpherePlugin.getDefault().getDialogSettings());
    }

    @Override
    public boolean close() {
        textParameters.dispose();
        return super.close();
    }
}
