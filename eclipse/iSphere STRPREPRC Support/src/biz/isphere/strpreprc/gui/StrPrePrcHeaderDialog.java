/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.strpreprc.gui;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.rse.services.clientserver.messages.SystemMessageException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.strpreprc.ISphereStrPrePrcSupportPlugin;
import biz.isphere.strpreprc.cl.CLFormatter;
import biz.isphere.strpreprc.gui.AbstractTableViewer.ModifyEvent;
import biz.isphere.strpreprc.gui.AbstractTableViewer.ModifyListener;
import biz.isphere.strpreprc.model.StrPrePrcHeader;

import com.ibm.as400.ui.util.CommandPrompter;
import com.ibm.etools.iseries.rse.util.clprompter.CLPrompter;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

public class StrPrePrcHeaderDialog extends XDialog implements ModifyListener, org.eclipse.swt.events.ModifyListener {

    private StrPrePrcHeader header;

    private Text textCommand;
    private Text textPreviewBaseCommand;
    private ParametersTableViewer compileParametersViewer;
    private CommandsTableViewer preCommandsViewer;
    private CommandsTableViewer postCommandsViewer;

    private boolean updating;

    public StrPrePrcHeaderDialog(Shell parentShell) {
        super(parentShell);
    }

    public void setInput(StrPrePrcHeader header) {
        this.header = header;
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        Composite container = (Composite)super.createDialogArea(parent);
        container.setLayout(new GridLayout(3, false));

        Label labelCommand = new Label(container, SWT.NONE);
        labelCommand.setText("Command:");

        textCommand = WidgetFactory.createUpperCaseText(container);
        // textCommand.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
        // false));
        GridData textCommandLayoutData = new GridData();
        textCommandLayoutData.widthHint = 100;
        textCommand.setLayoutData(textCommandLayoutData);
        textCommand.setTextLimit(10);
        textCommand.addModifyListener(this);

        Button buttonPrompt = WidgetFactory.createPushButton(container);
        buttonPrompt.setText("Prompt");
        buttonPrompt.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                performPromptCommand(header.getFullCommand());
            }
        });

        Label labelPreview = new Label(container, SWT.NONE);
        labelPreview.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
        labelPreview.setText("Base command:");

        textPreviewBaseCommand = WidgetFactory.createMultilineText(container, true, false);
        textPreviewBaseCommand.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        textPreviewBaseCommand.setEditable(false);

        Label labelParameters = new Label(container, SWT.NONE);
        labelParameters.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
        labelParameters.setText("Overridden parameters:");

        compileParametersViewer = new ParametersTableViewer(container);
        compileParametersViewer.addModifyListener(this);

        Label labelPreCommands = new Label(container, SWT.NONE);
        labelPreCommands.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
        labelPreCommands.setText("Pre-Commands:");

        preCommandsViewer = new CommandsTableViewer(container);
        preCommandsViewer.addModifyListener(this);

        Label labelPostCommands = new Label(container, SWT.NONE);
        labelPostCommands.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
        labelPostCommands.setText("Post-Commands:");

        postCommandsViewer = new CommandsTableViewer(container);
        postCommandsViewer.addModifyListener(this);

        updatePreview(null);

        return container;
    }

    @Override
    protected void okPressed() {

        updateHeader();
        
        super.okPressed();
    }

    private boolean performPromptCommand(String createCommand) {

        if (createCommand == null) {
            MessageDialog.openError(getShell(), "E R R O R", "Command not specified.");
            return false;
        }

        CLPrompter prompter;
        try {

            IBMiConnection connection = IBMiConnection.getConnection("ghentw.gfd.de");

            CLFormatter formatter = new CLFormatter(connection.getAS400ToolboxObject());
            String formattedCommand = formatter.format(createCommand);
            if (formattedCommand == null) {
                return false;
            }

            prompter = new CLPrompter();
            prompter.setCommandString(formattedCommand);
            prompter.setMode(CLPrompter.EDIT_MODE);
            prompter.setConnection(connection);
            prompter.setParent(Display.getCurrent().getActiveShell());

            if (prompter.showDialog() != CommandPrompter.OK) {
                return false;
            }

            header.update(prompter.getCommandString());

            updatePreview(null);

            return true;

        } catch (SystemMessageException e) {
            MessageDialog.openError(getShell(), "E R R O R", ExceptionHelper.getLocalizedMessage(e));
            return false;
        }
    }

    @Override
    public void modified(ModifyEvent paramModifyEvent) {

//        updateHeader();
        updatePreview(paramModifyEvent.getClass());
    }

    @Override
    public void modifyText(org.eclipse.swt.events.ModifyEvent paramModifyEvent) {

//        updateHeader();
        updatePreview(paramModifyEvent.widget);
    }

    private void updateHeader() {
        
        header.setCommand(textCommand.getText());
        header.setCompileParameters(compileParametersViewer.getInput());
        header.setPreCommands(preCommandsViewer.getInput());
        header.setPostCommands(postCommandsViewer.getInput());
    }
    
    private void updatePreview(Object source) {

        if (updating) {
            return;
        }

        try {
            updating = true;

            setScreenValues(source);
        } finally {
            updating = false;
        }

    }

    private void setScreenValues(Object source) {

        if (source != textCommand) {
            textCommand.setText(header.getCommand());
        }

        if (source != compileParametersViewer) {
            compileParametersViewer.setInput(header.getParameters());
        }

        if (source != preCommandsViewer) {
            preCommandsViewer.setInput(header.getPreCommands());
        }

        if (source != postCommandsViewer) {
            postCommandsViewer.setInput(header.getPostCommands());
        }

        textPreviewBaseCommand.setText(header.getBaseCommand());
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    @Override
    protected Point getDefaultSize() {
        return getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
    }

    /**
     * Overridden to let {@link XDialog} store the state of this dialog in a
     * separate section of the dialog settings file.
     */
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return super.getDialogBoundsSettings(ISphereStrPrePrcSupportPlugin.getDefault().getDialogSettings());
    }
}
