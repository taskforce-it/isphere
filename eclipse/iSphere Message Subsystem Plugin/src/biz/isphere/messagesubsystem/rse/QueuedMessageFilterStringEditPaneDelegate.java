/*******************************************************************************
 * Copyright (c) 2005 SoftLanding Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     SoftLanding - initial API and implementation
 *     iSphere Project Owners - Maintenance and enhancements
 *******************************************************************************/

package biz.isphere.messagesubsystem.rse;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.messagesubsystem.Messages;
import biz.isphere.messagesubsystem.internal.QueuedMessageHelper;

import com.ibm.as400.access.MessageQueue;

public class QueuedMessageFilterStringEditPaneDelegate {

    private static final String QUSRSYS = "QUSRSYS"; //$NON-NLS-1$
    private static final String ASTERISK = "*"; //$NON-NLS-1$

    private Combo messageQueueText;
    private Text libraryText;
    private Text userText;
    private Text idText;
    private Text severityText;
    private Text fromJobText;
    private Text fromJobNumberText;
    private Text fromProgramText;
    private Text textText;
    private Combo messageTypeCombo;

    public QueuedMessageFilterStringEditPaneDelegate() {
    }

    public Control createContents(Composite composite_prompts) {

        ((GridLayout)composite_prompts.getLayout()).marginWidth = 0;

        Label messageQueueLabel = new Label(composite_prompts, SWT.NONE);
        messageQueueLabel.setText(Messages.Message_queue_colon);

        messageQueueText = WidgetFactory.createUpperCaseCombo(composite_prompts);
        GridData gd = new GridData();
        gd.widthHint = 75;
        gd.horizontalSpan = 2;
        messageQueueText.setLayoutData(gd);
        messageQueueText.setTextLimit(10);
        messageQueueText.add(QueuedMessageFilter.MSGQ_CURRENT);
        messageQueueText.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setLibraryText(libraryText.getText());
            }
        });
        messageQueueText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                setLibraryText(libraryText.getText());
                if (QueuedMessageFilter.MSGQ_CURRENT.equals(messageQueueText.getText())) {
                    libraryText.setEnabled(false);
                } else {
                    libraryText.setEnabled(true);
                }
            }
        });

        Label libraryLabel = new Label(composite_prompts, SWT.NONE);
        libraryLabel.setText(Messages.Library_colon);

        libraryText = WidgetFactory.createUpperCaseText(composite_prompts);
        gd = new GridData();
        gd.widthHint = 75;
        gd.horizontalSpan = 2;
        libraryText.setLayoutData(gd);
        libraryText.setTextLimit(10);

        // From user: *generic*
        Label userLabel = new Label(composite_prompts, SWT.NONE);
        userLabel.setText(Messages.From_user_colon);

        userText = WidgetFactory.createUpperCaseText(composite_prompts);
        gd = new GridData();
        gd.widthHint = 75;
        userText.setLayoutData(gd);
        userText.setTextLimit(10);

        Label textUserGeneric = new Label(composite_prompts, SWT.NONE);
        textUserGeneric.setText(Messages.Label_Full_generic_string); //$NON-NLS-1$

        // Message Id: *generic*
        Label idLabel = new Label(composite_prompts, SWT.NONE);
        idLabel.setText(Messages.Message_ID_colon);

        idText = WidgetFactory.createUpperCaseText(composite_prompts);
        gd = new GridData();
        gd.widthHint = 75;
        idText.setLayoutData(gd);
        idText.setTextLimit(7);

        Label textIdGeneric = new Label(composite_prompts, SWT.NONE);
        textIdGeneric.setText(Messages.Label_Full_generic_string); //$NON-NLS-1$

        // Message severity: numeric value
        Label severityLabel = new Label(composite_prompts, SWT.NONE);
        severityLabel.setText(Messages.Severity_threshold_colon);

        severityText = WidgetFactory.createText(composite_prompts);
        gd = new GridData();
        gd.widthHint = 75;
        gd.horizontalSpan = 2;
        severityText.setLayoutData(gd);
        severityText.setTextLimit(2);

        // From job: *generic*
        Label fromJobLabel = new Label(composite_prompts, SWT.NONE);
        fromJobLabel.setText(Messages.From_job_colon);

        fromJobText = WidgetFactory.createUpperCaseText(composite_prompts);
        gd = new GridData();
        gd.widthHint = 75;
        fromJobText.setLayoutData(gd);
        fromJobText.setTextLimit(10);

        Label textFromJobGeneric = new Label(composite_prompts, SWT.NONE);
        textFromJobGeneric.setText(Messages.Label_Full_generic_string); //$NON-NLS-1$

        // From job number: *generic*
        Label fromJobNumberLabel = new Label(composite_prompts, SWT.NONE);
        fromJobNumberLabel.setText(Messages.From_job_number_colon);

        fromJobNumberText = WidgetFactory.createText(composite_prompts);
        gd = new GridData();
        gd.widthHint = 75;
        fromJobNumberText.setLayoutData(gd);
        fromJobNumberText.setTextLimit(6);

        Label textFromJobNumberGeneric = new Label(composite_prompts, SWT.NONE);
        textFromJobNumberGeneric.setText(Messages.Label_Full_generic_string); //$NON-NLS-1$

        // From program: *generic*
        Label fromProgramLabel = new Label(composite_prompts, SWT.NONE);
        fromProgramLabel.setText(Messages.From_program_colon);

        fromProgramText = WidgetFactory.createUpperCaseText(composite_prompts);
        gd = new GridData();
        gd.widthHint = 75;
        fromProgramText.setLayoutData(gd);
        fromProgramText.setTextLimit(10);

        Label textFromProgramGeneric = new Label(composite_prompts, SWT.NONE);
        textFromProgramGeneric.setText(Messages.Label_Full_generic_string); //$NON-NLS-1$

        // Message text: *generic*
        Label textLabel = new Label(composite_prompts, SWT.NONE);
        textLabel.setText(Messages.Message_text_contains_colon);

        textText = WidgetFactory.createText(composite_prompts);
        gd = new GridData();
        gd.widthHint = 150;
        textText.setLayoutData(gd);
        textText.setTextLimit(255);

        Label textTextGeneric = new Label(composite_prompts, SWT.NONE);
        textTextGeneric.setText(Messages.Label_Full_generic_string); //$NON-NLS-1$

        // Message type: combo box
        Label typeLabel = new Label(composite_prompts, SWT.NONE);
        typeLabel.setText(Messages.Message_type_colon);

        messageTypeCombo = WidgetFactory.createReadOnlyCombo(composite_prompts);
        messageTypeCombo.setItems(QueuedMessageHelper.getMessageTypeItems());
        messageTypeCombo.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, false, false, 2, 1));

        return composite_prompts;
    }

    public void addModifyListener(ModifyListener keyListener) {

        messageQueueText.addModifyListener(keyListener);
        libraryText.addModifyListener(keyListener);
        userText.addModifyListener(keyListener);
        idText.addModifyListener(keyListener);
        severityText.addModifyListener(keyListener);
        fromJobText.addModifyListener(keyListener);
        fromJobNumberText.addModifyListener(keyListener);
        fromProgramText.addModifyListener(keyListener);
        textText.addModifyListener(keyListener);
        messageTypeCombo.addModifyListener(keyListener);
    }

    public Control getInitialFocusControl() {
        return messageQueueText;
    }

    public void doInitializeFields(String inputFilterString) {

        if (messageQueueText == null) {
            return;
        }

        if (inputFilterString != null) {

            QueuedMessageFilter filter = new QueuedMessageFilter(inputFilterString);
            if (filter.getMessageQueue() != null) {

                messageQueueText.setText(filter.getMessageQueue());
                setLibraryText(filter.getLibrary());

                if (filter.getUser() != null) {
                    userText.setText(filter.getUser());
                } else {
                    userText.setText(ASTERISK);
                }

                if (filter.getId() != null) {
                    idText.setText(filter.getId());
                } else {
                    idText.setText(ASTERISK);
                }

                if (filter.getSeverity() == -1) {
                    severityText.setText(ASTERISK);
                } else {
                    severityText.setText(new Integer(filter.getSeverity()).toString());
                }

                if (filter.getFromJobName() != null) {
                    fromJobText.setText(filter.getFromJobName());
                } else {
                    fromJobText.setText(ASTERISK);
                }

                if (filter.getFromJobNumber() != null) {
                    fromJobNumberText.setText(filter.getFromJobNumber());
                } else {
                    fromJobNumberText.setText(ASTERISK);
                }

                if (filter.getFromProgram() != null) {
                    fromProgramText.setText(filter.getFromProgram());
                } else {
                    fromProgramText.setText(ASTERISK);
                }

                if (filter.getText() != null) {
                    textText.setText(filter.getText());
                } else {
                    textText.setText(ASTERISK);
                }

                if (filter.getMessageType() != -1) {
                    messageTypeCombo.select(messageTypeCombo.indexOf(QueuedMessageHelper.getMessageTypeAsText(filter.getMessageType())));
                }
            }
            if (messageTypeCombo.getSelectionIndex() == -1) {
                messageTypeCombo.select(messageTypeCombo.indexOf(QueuedMessageHelper.getMessageTypeAnyItem()));
            }
        }
    }

    public void resetFields() {

        messageQueueText.select(0);
        setLibraryText(ASTERISK);

        userText.setText(ASTERISK);
        idText.setText(ASTERISK);
        severityText.setText(ASTERISK);
        fromJobText.setText(ASTERISK);
        fromJobNumberText.setText(ASTERISK);
        fromProgramText.setText(ASTERISK);
        textText.setText(ASTERISK);
        messageTypeCombo.select(messageTypeCombo.indexOf(QueuedMessageHelper.getMessageTypeAnyItem()));
    }

    public String validateInput() {

        String messageQueue = messageQueueText.getText();
        if (StringHelper.isNullOrEmpty(messageQueue)) {
            return Messages.Message_queue_is_missing;
        }

        if (libraryText.isEnabled()) {
            String library = libraryText.getText();
            if (StringHelper.isNullOrEmpty(library)) {
                return Messages.Message_queue_library_is_invalid_or_missing;
            }

            // TODO: fix library name validator (pass CCSID)
            // if (!Validator.getLibraryNameInstance(new
            // Integer(ccsid)).validate(library)) {
            // return Messages.Message_queue_library_is_invalid_or_missing;
            // }
        }

        String fromUser = userText.getText();
        if (StringHelper.isNullOrEmpty(fromUser)) {
            return Messages.From_user_is_missing;
        }

        String messageId = idText.getText();
        if (StringHelper.isNullOrEmpty(messageId)) {
            return Messages.Message_ID_is_missing;
        }

        String severity = severityText.getText();
        if (StringHelper.isNullOrEmpty(severity)) {
            return Messages.Severity_is_missing;
        }

        String fromJob = fromJobText.getText();
        if (StringHelper.isNullOrEmpty(fromJob)) {
            return Messages.From_job_is_missing;
        }

        String fromJobNumber = fromJobNumberText.getText();
        if (StringHelper.isNullOrEmpty(fromJobNumber)) {
            return Messages.From_job_number_is_missing;
        }

        String fromProgram = fromProgramText.getText();
        if (StringHelper.isNullOrEmpty(fromProgram)) {
            return Messages.From_program_is_missing;
        }

        String messageText = textText.getText();
        if (StringHelper.isNullOrEmpty(messageText)) {
            return Messages.Message_text_is_missing;
        }

        String messageType = messageTypeCombo.getText();
        if (StringHelper.isNullOrEmpty(messageType)) {
            return Messages.Message_type_is_missing;
        }

        return null;
    }

    public boolean areFieldsComplete() {

        if (validateInput() != null) {
            return false;
        }

        return true;
    }

    public String getFilterString() {

        QueuedMessageFilter filter = new QueuedMessageFilter();

        filter.setMessageQueue(messageQueueText.getText().toUpperCase());
        filter.setLibrary(libraryText.getText().toUpperCase());
        filter.setUser(userText.getText().toUpperCase());
        filter.setId(idText.getText().toUpperCase());

        if (severityText.getText().equals(ASTERISK)) {
            filter.setSeverity(-1);
        } else {
            int severity = -1;
            try {
                severity = new Integer(severityText.getText()).intValue();
            } catch (Exception e) {
            }
            filter.setSeverity(severity);
        }

        filter.setFromJobName(fromJobText.getText().toUpperCase());
        filter.setFromJobNumber(fromJobNumberText.getText());
        filter.setFromProgram(fromProgramText.getText().toUpperCase());
        filter.setText(textText.getText());
        filter.setMessageType(QueuedMessageHelper.getMessageTypeFromText(messageTypeCombo.getText()));

        return filter.getFilterString();
    }

    private void setLibraryText(String library) {

        String newLibrary = null;

        if (MessageQueue.CURRENT.equals(messageQueueText.getText())) {
            newLibrary = ASTERISK; // Set library to "current"
        } else {

            if (library == null || library.trim().length() == 0) {
                newLibrary = QUSRSYS;
            } else {
                newLibrary = library;
            }
        }

        if (newLibrary != null && !newLibrary.equals(libraryText.getText())) {
            libraryText.setText(newLibrary);
        }
    }
}
