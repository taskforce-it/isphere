package biz.isphere.strpreprc.gui;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.strpreprc.Messages;
import biz.isphere.strpreprc.model.StrPrePrcParser;
import biz.isphere.strpreprc.preferences.Preferences;

import com.ibm.as400.access.AS400;

public class EditHeaderDialog extends XDialog {

    public final static int PROMPT = 901;

    private final static String CONNECTION_NAME = "CONNECTION_NAME";
    private final static String COMMAND = "COMMAND";
    private final static String PARAMETERS = "PARAMETERS";
    private final static String MEMBER_TYPE = "MEMBER_TYPE";

    private String memberType;
    private String connectionName;
    private String commandString;
    private String parameters;

    private Composite mainArea;
    private Combo comboConnections;
    private Text textCommand;
    private Text textParameters;

    public EditHeaderDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.Create_STRPREPRC_Header);
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        mainArea = new Composite(parent, SWT.NONE);
        mainArea.setLayout(new GridLayout(2, false));
        mainArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label labelConnection = new Label(mainArea, SWT.NONE);
        labelConnection.setText("Connection:");

        comboConnections = WidgetFactory.createReadOnlyCombo(mainArea);

        Label labelCommand = new Label(mainArea, SWT.NONE);
        labelCommand.setText("Command:");

        textCommand = WidgetFactory.createNameText(mainArea);
        textCommand.setTextLimit(10);

        Label labelParameters = new Label(mainArea, SWT.NONE);
        labelParameters.setText("Parameters:");
        labelParameters.setLayoutData(new GridData(SWT.DEFAULT, SWT.BEGINNING, false, false));

        textParameters = WidgetFactory.createMultilineText(mainArea, true, true);
        textParameters.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createStatusLine(mainArea);

        loadScreenValues();

        setControlEnablement();

        return mainArea;
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
            setErrorMessage("Missing connection name."); // TODO: NLS
            return false;
        }

        AS400 system = IBMiHostContributionsHandler.getSystem(connectionName);
        if (system == null) {
            setErrorMessage("Could not get IBM i system."); // TODO: NLS
            return false;
        }

        String command = textCommand.getText();
        if (StringHelper.isNullOrEmpty(command)) {
            setErrorMessage("Missing creation command."); // TODO: NLS
            return false;
        }

        String fullCommand = (command + " " + textParameters.getText()).trim();
        int offset;
        if ((offset = validateReplacementVariables(fullCommand)) >= 0) {
            int position = offset + 1;
            String variable = retrieveReplacementVariable(offset, fullCommand);
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, "Invalid replacement variable " + variable.replace("&", "&&")
                + " found at position " + position + ".\n\n" + fullCommand.replaceAll("&", "&&"));
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
            if (!isComment && "'".equals(currentChar)) {
                isComment = true;
            } else if (isComment && "'".equals(currentChar)) {
                isComment = false;
            }
            if (!isComment && "&".equals(currentChar)) {
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
            if (!fullCommand.substring(offset, offset + 1).matches("[&a-zA-Z0-9]")) {
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
            this.parameters = Preferences.getInstance().getDefaultKeywords();
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
        return new Point(910, 600);
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
