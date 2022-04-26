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

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
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

import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.base.swt.widgets.UpperCaseOnlyVerifier;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.internal.Validator;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.stringlisteditor.IStringValidator;
import biz.isphere.core.swt.widgets.stringlisteditor.StringListEditor;
import biz.isphere.core.swt.widgets.stringlisteditor.ValidationEvent;
import biz.isphere.messagesubsystem.Messages;
import biz.isphere.messagesubsystem.internal.QEZSNDMG;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.QueuedMessage;

public class SendMessageDialog extends XDialog implements IStringValidator {

    private static final int SEND = 1;
    private static final int FORWARD = 2;

    private static final String MESSAGE_TYPE = "MESSAGE_TYPE"; //$NON-NLS-1$
    private static final String DELIVERY_MODE = "DELIVERY_MODE"; //$NON-NLS-1$
    private static final String MESSAGE_TEXT = "MESSAGE_TEXT"; //$NON-NLS-1$
    private static final String MESSAGE_RECIPIENT_TYPES = "MESSAGE_RECIPIENT_TYPES"; //$NON-NLS-1$
    private static final String MESSAGE_RECIPIENT = "MESSAGE_RECIPIENT"; //$NON-NLS-1$
    private static final String MESSAGE_RECIPIENTS_COUNT = "MESSAGE_RECIPIENTS_COUNT"; //$NON-NLS-1$
    private static final String MESSAGE_RECIPIENT_ITEM = "MESSAGE_RECIPIENTS_ITEM_"; //$NON-NLS-1$
    private static final String MESSAGE_REPLY_QUEUE_NAME = "MESSAGE_REPLY_QUEUE_NAME"; //$NON-NLS-1$
    private static final String MESSAGE_REPLY_QUEUE_LIBRARY = "MESSAGE_REPLY_QUEUE_LIBRARY"; //$NON-NLS-1$

    private static final int DEFAULT_INDEX_MESSAGE_TYPE = 0;
    private static final int DEFAULT_INDEX_DELIVERY_MODE = 0;
    private static final int DEFAULT_INDEX_RECIPIENT_TYPES = 0;
    private static final int DEFAULT_INDEX_RECIPIENT = 0;
    private static final int DEFAULT_REPLY_MESSAGE_QUEUE_NAME = 0;
    private static final int DEFAULT_REPLY_MESSAGE_QUEUE_LIBRARY = 0;

    private static final String RECIPIENT_LIST = "*LIST"; //$NON-NLS-1$
    private static final String REPLY_MSGQ_NAME_SENDER = "*SENDER"; //$NON-NLS-1$ 
    private static final String REPLY_MSGQ_LIBRARY_LIBL = "*LIBL"; //$NON-NLS-1$ 
    private static final String REPLY_MSGQ_LIBRARY_CURLIB = "*CURLIB"; //$NON-NLS-1$ 
    private static final int BUTTON_RESET_ID = -1;

    private static final String LIBRARY_CURLIB = "*CURLIB"; //$NON-NLS-1$
    private static final String LIBRARY_LIBL = "*LIBL"; //$NON-NLS-1$

    private Combo comboMessageType;
    private Combo comboDeliveryMode;
    private Text textMessageText;
    private Combo comboRecipientTypes;
    private Combo comboRecipient;
    private Label labelReplyMessageQueueName;
    private Combo comboReplyMessageQueueName;
    private Label labelReplyMessageQueueLibrary;
    private Combo comboReplyMessageQueueLibrary;

    private String titleText;
    private SendMessageOptions sendMessageOptions;
    private StringListEditor recipientsEditor;

    private String overWriteMessageType;
    private String overWriteMessageText;

    private Validator nameValidator;
    private Validator libraryValidator;
    private AS400 system;

    public static SendMessageDialog createSendDialog(Shell shell, AS400 system) {
        return new SendMessageDialog(shell, system, SEND);
    }

    public static SendMessageDialog createForwardDialog(Shell shell, AS400 system) {
        return new SendMessageDialog(shell, system, FORWARD);
    }

    private SendMessageDialog(Shell shell, AS400 system, int type) {
        super(shell);

        switch (type) {
        case SEND:
            titleText = Messages.Send_Message;
            break;
        case FORWARD:
            titleText = Messages.Forward_Message;
            break;
        default:
            throw new IllegalArgumentException("Illegal argumt for parameter 'type': " + type); //$NON-NLS-1$
        }

        this.sendMessageOptions = null;
        this.nameValidator = Validator.getNameInstance(Integer.valueOf(system.getCcsid()));
        this.libraryValidator = Validator.getLibraryNameInstance(system.getCcsid());
        this.system = system;

        this.overWriteMessageType = null;
        this.overWriteMessageText = null;
    }

    public void setMessageType(int type) {

        if (type == QueuedMessage.INQUIRY) {
            this.overWriteMessageType = QEZSNDMG.TYPE_INQUERY;
        } else {
            this.overWriteMessageType = QEZSNDMG.TYPE_INFORMATIONAL;
        }
    }

    public void setMessageText(String text) {
        this.overWriteMessageText = text;
    }

    @Override
    public Control createDialogArea(Composite parent) {

        parent.getShell().setText(titleText);

        ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.NONE);
        scrolledComposite.setLayout(new GridLayout());
        scrolledComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);

        Composite mainPanel = new Composite(scrolledComposite, SWT.BORDER);
        mainPanel.setLayout(new GridLayout(2, false));
        mainPanel.setLayoutData(new GridData(GridData.FILL_BOTH));

        Label labelMessageType = new Label(mainPanel, SWT.NONE);
        labelMessageType.setLayoutData(createLabelLayoutData());
        labelMessageType.setText(Messages.Message_type_colon);

        comboMessageType = WidgetFactory.createReadOnlyCombo(mainPanel);
        comboMessageType.setLayoutData(createInputFieldLayoutData());
        comboMessageType.setItems(new String[] { QEZSNDMG.TYPE_INFORMATIONAL, QEZSNDMG.TYPE_INQUERY });
        comboMessageType.select(DEFAULT_INDEX_MESSAGE_TYPE);
        comboMessageType.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                setControlEnablement();
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        Label labelDeliveryMode = new Label(mainPanel, SWT.NONE);
        labelDeliveryMode.setLayoutData(createLabelLayoutData());
        labelDeliveryMode.setText(Messages.Delivery_mode_colon);

        comboDeliveryMode = WidgetFactory.createReadOnlyCombo(mainPanel);
        comboDeliveryMode.setLayoutData(createInputFieldLayoutData());
        comboDeliveryMode.setItems(new String[] { QEZSNDMG.DELIVERY_NORMAL, QEZSNDMG.DELIVERY_BREAK });
        comboDeliveryMode.select(DEFAULT_INDEX_DELIVERY_MODE);

        Label labelMessageText = new Label(mainPanel, SWT.NONE);
        labelMessageText.setLayoutData(createLabelLayoutData());
        labelMessageText.setText(Messages.Message_text_colon);

        textMessageText = WidgetFactory.createMultilineText(mainPanel, true, true);
        GridData textMessageTextLayoutData = new GridData(GridData.FILL_BOTH);
        textMessageTextLayoutData.minimumHeight = 100;
        textMessageText.setLayoutData(textMessageTextLayoutData);
        textMessageText.setText(""); //$NON-NLS-1$
        textMessageText.addKeyListener(new KeyListener() {
            public void keyReleased(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
                    e.doit = false;
                }
            }
        });
        textMessageText.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent e) {
                if (e.keyCode == SWT.TAB) {
                    e.doit = true;
                }
            }
        });
        textMessageText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                validateMessageText(textMessageText.getText());
            }
        });
        textMessageText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent event) {
                Object source = event.getSource();
                if (source instanceof Text) {
                    Text text = (Text)source;
                    text.setSelection(0, 0);
                }
            }
        });

        Label labelRecipientTypes = new Label(mainPanel, SWT.NONE);
        labelRecipientTypes.setLayoutData(createLabelLayoutData());
        labelRecipientTypes.setText(Messages.Recipient_type_colon);

        comboRecipientTypes = WidgetFactory.createReadOnlyCombo(mainPanel);
        comboRecipientTypes.setLayoutData(createInputFieldLayoutData());
        comboRecipientTypes.setItems(new String[] { QEZSNDMG.RECIPIENT_TYPE_USER, QEZSNDMG.RECIPIENT_TYPE_DISPLAY });
        comboRecipientTypes.select(DEFAULT_INDEX_RECIPIENT_TYPES);

        Label labelRecipients = new Label(mainPanel, SWT.NONE);
        labelRecipients.setLayoutData(createLabelLayoutData());
        labelRecipients.setText(Messages.Recipients_colon);

        comboRecipient = WidgetFactory.createCombo(mainPanel);
        comboRecipient.setTextLimit(10);
        comboRecipient.setLayoutData(createInputFieldLayoutData());
        comboRecipient.setItems(new String[] { RECIPIENT_LIST, QEZSNDMG.RECIPIENT_ALL, QEZSNDMG.RECIPIENT_ALLACT, QEZSNDMG.RECIPIENT_SYSOPR });
        comboRecipient.select(DEFAULT_INDEX_RECIPIENT);
        comboRecipient.addVerifyListener(new UpperCaseOnlyVerifier());
        comboRecipient.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                setControlEnablement();
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        comboRecipient.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                setControlEnablement();
            }
        });

        new Label(mainPanel, SWT.NONE); // place holder

        recipientsEditor = WidgetFactory.createStringListEditor(mainPanel);
        recipientsEditor.setTextLimit(10);
        recipientsEditor.setEnableLowerCase(true);
        recipientsEditor.setValidator(this);

        labelReplyMessageQueueName = new Label(mainPanel, SWT.NONE);
        labelReplyMessageQueueName.setLayoutData(createLabelLayoutData());
        labelReplyMessageQueueName.setText(Messages.getLabel(Messages.Reply_message_queue_name));

        comboReplyMessageQueueName = WidgetFactory.createCombo(mainPanel);
        comboReplyMessageQueueName.setTextLimit(10);
        comboReplyMessageQueueName.setLayoutData(createInputFieldLayoutData());
        comboReplyMessageQueueName.setItems(new String[] { REPLY_MSGQ_NAME_SENDER });
        comboReplyMessageQueueName.select(DEFAULT_REPLY_MESSAGE_QUEUE_NAME);
        comboReplyMessageQueueName.addVerifyListener(new UpperCaseOnlyVerifier());
        comboReplyMessageQueueName.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                adjustReplyMessageQueueLibrary();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });
        comboReplyMessageQueueName.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent paramModifyEvent) {
                adjustReplyMessageQueueLibrary();
            }
        });

        labelReplyMessageQueueLibrary = new Label(mainPanel, SWT.NONE);
        labelReplyMessageQueueLibrary.setLayoutData(createLabelLayoutData());
        labelReplyMessageQueueLibrary.setText(Messages.getLabel(Messages.Reply_message_queue_library));

        comboReplyMessageQueueLibrary = WidgetFactory.createCombo(mainPanel);
        comboReplyMessageQueueLibrary.setTextLimit(10);
        comboReplyMessageQueueLibrary.setLayoutData(createInputFieldLayoutData());
        comboReplyMessageQueueLibrary.setItems(new String[] { REPLY_MSGQ_LIBRARY_LIBL, REPLY_MSGQ_LIBRARY_CURLIB });
        comboReplyMessageQueueLibrary.select(DEFAULT_REPLY_MESSAGE_QUEUE_LIBRARY);
        comboReplyMessageQueueLibrary.addVerifyListener(new UpperCaseOnlyVerifier());
        comboReplyMessageQueueLibrary.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                adjustReplyMessageQueueLibrary();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });
        comboReplyMessageQueueLibrary.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent paramModifyEvent) {
                adjustReplyMessageQueueLibrary();
            }
        });

        createStatusLine(mainPanel);

        scrolledComposite.setContent(mainPanel);
        scrolledComposite.setMinSize(mainPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        loadScreenValues();

        setControlEnablement();

        return scrolledComposite;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        Button buttonReset = createButton(parent, BUTTON_RESET_ID, Messages.RESET_LABEL, false);
        if (buttonReset != null) {
            buttonReset.addSelectionListener(new SelectionListener() {
                public void widgetSelected(SelectionEvent arg0) {
                    resetPressed();
                }

                public void widgetDefaultSelected(SelectionEvent arg0) {
                }
            });
        }
        super.createButtonsForButtonBar(parent);
    }

    private GridData createLabelLayoutData() {
        return new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
    }

    private GridData createInputFieldLayoutData() {
        return new GridData(100, SWT.DEFAULT);
    }

    private void setControlEnablement() {

        if (RECIPIENT_LIST.equals(comboRecipient.getText())) {
            recipientsEditor.setEnabled(true);
        } else {
            recipientsEditor.setEnabled(false);
        }

        if (QEZSNDMG.TYPE_INQUERY.equals(comboMessageType.getText())) {
            labelReplyMessageQueueName.setEnabled(true);
            labelReplyMessageQueueLibrary.setEnabled(true);
            comboReplyMessageQueueName.setEnabled(true);
            comboReplyMessageQueueLibrary.setEnabled(true);
        } else {
            labelReplyMessageQueueName.setEnabled(false);
            labelReplyMessageQueueLibrary.setEnabled(false);
            comboReplyMessageQueueName.setEnabled(false);
            comboReplyMessageQueueLibrary.setEnabled(false);
        }
    }

    private void adjustReplyMessageQueueLibrary() {

        boolean isEmpty;
        if (comboReplyMessageQueueLibrary.getText().trim().length() == 0) {
            isEmpty = true;
        } else {
            isEmpty = false;
        }

        if (REPLY_MSGQ_NAME_SENDER.equals(comboReplyMessageQueueName.getText())) {
            if (!isEmpty) {
                comboReplyMessageQueueLibrary.setText(""); //$NON-NLS-1$
            }
        } else {
            if (isEmpty) {
                comboReplyMessageQueueLibrary.setText(LIBRARY_LIBL);
            }
        }
    }

    @Override
    public int open() {

        if (getShell() == null) {
            // create the window
            create();
        }

        getShell().forceActive();

        return super.open();
    }

    private void resetPressed() {
        performReset();
    }

    private void performReset() {

        comboMessageType.select(DEFAULT_INDEX_MESSAGE_TYPE);
        comboDeliveryMode.select(DEFAULT_INDEX_DELIVERY_MODE);
        textMessageText.setText(""); //$NON-NLS-1$
        comboRecipientTypes.select(DEFAULT_INDEX_RECIPIENT_TYPES);
        comboRecipient.select(DEFAULT_INDEX_RECIPIENT);

        overWriteInitialValues();

        recipientsEditor.clearAll();

        setControlEnablement();

        setErrorMessage(null);
    }

    @Override
    public void okPressed() {

        if (!validateInput()) {
            return;
        }

        storeInput();

        storeScreenValues();

        super.okPressed();
    }

    public String isValid(ValidationEvent event) {

        if (!(event instanceof ValidationEvent)) {
            return null;
        }

        ValidationEvent validationEvent = (ValidationEvent)event;

        if (validationEvent.getType() == ValidationEvent.ADD) {
            return validateAddItem(validationEvent.value);
        } else if (validationEvent.getType() == ValidationEvent.CHANGE) {
            return validateChangeItem(validationEvent.value);
        } else {
            return null;
        }
    }

    public String validateAddItem(String userProfile) {

        if (recipientsEditor.contains(userProfile)) {
            String message = Messages.bind(Messages.User_profile_A_has_already_been_added_to_the_list, userProfile);
            setErrorMessage(message);
            return message;
        }

        return validateChangeItem(userProfile);
    }

    public String validateChangeItem(String userProfile) {

        String message = validateUserProfile(userProfile);
        if (message != null) {
            setErrorMessage(message);
            return message;
        }

        setErrorMessage(null);

        return null;
    }

    public String validateUserProfile(String userProfile) {

        userProfile = userProfile.trim();

        if (userProfile.length() <= 0) {
            return Messages.Recipients_are_missing;
        }

        if (!(QEZSNDMG.RECIPIENT_SYSOPR.equals(userProfile) || QEZSNDMG.RECIPIENT_ALLACT.equals(userProfile))) {
            if (!nameValidator.validate(userProfile)) {
                return Messages.Invalid_recipient;
            }

            if (!ISphereHelper.checkUserProfile(system, userProfile)) {
                return Messages.bind(Messages.User_profile_A_does_not_exist, userProfile);
            }
        }

        return null;
    }

    private boolean validateInput() {

        if (!validateMessageText(textMessageText.getText())) {
            textMessageText.setFocus();
            return false;
        }

        if (!validateRecipient(comboRecipient.getText())) {
            comboRecipient.setFocus();
            return false;
        }

        if (RECIPIENT_LIST.equals(comboRecipient.getText())) {
            String[] recipients = recipientsEditor.getItems();
            int index = validateRecipients(recipients);
            if (index >= 0) {
                if (index < recipientsEditor.getItemCount()) {
                    recipientsEditor.setFocus(index);
                } else {
                    recipientsEditor.setFocus();
                }
                return false;
            }
        }

        boolean isALL = false;
        boolean isSysOpr = false;
        if (RECIPIENT_LIST.equals(comboRecipient.getText())) {
            isALL = recipientsEditor.contains(QEZSNDMG.RECIPIENT_ALL);
            isSysOpr = recipientsEditor.contains(QEZSNDMG.RECIPIENT_SYSOPR);
        } else {
            if (QEZSNDMG.RECIPIENT_ALL.equals(comboRecipient.getText())) {
                isALL = true;
            }
            if (QEZSNDMG.RECIPIENT_SYSOPR.equals(comboRecipient.getText())) {
                isSysOpr = true;
            }
        }

        if (isALL && QEZSNDMG.RECIPIENT_TYPE_DISPLAY.equals(comboRecipientTypes.getText())) {
            setErrorMessage(Messages.bind(Messages.A_cannot_be_used_if_B_is_specified_for_the_C_parameter, new String[] { QEZSNDMG.RECIPIENT_ALL,
                QEZSNDMG.RECIPIENT_TYPE_DISPLAY, Messages.Recipient_type }));
            recipientsEditor.setFocus();
            return false;
        }
        if (isSysOpr && QEZSNDMG.RECIPIENT_TYPE_DISPLAY.equals(comboRecipientTypes.getText())) {
            setErrorMessage(Messages.bind(Messages.A_cannot_be_used_if_B_is_specified_for_the_C_parameter, new String[] { QEZSNDMG.RECIPIENT_SYSOPR,
                QEZSNDMG.RECIPIENT_TYPE_DISPLAY, Messages.Recipient_type }));
            recipientsEditor.setFocus();
            return false;
        }

        if (QEZSNDMG.TYPE_INQUERY.equals(comboMessageType.getText())) {
            if (!validateMessageQueueLibrary(comboReplyMessageQueueLibrary.getText())) {
                setErrorMessage(Messages.Invalid_message_queue_library_name);
                comboReplyMessageQueueLibrary.setFocus();
                return false;
            }
            if (!validateMessageQueueName(comboReplyMessageQueueName.getText())) {
                setErrorMessage(Messages.Invalid_message_queue_name);
                comboReplyMessageQueueName.setFocus();
                return false;
            }
        }

        setErrorMessage(null);

        return true;
    }

    private boolean validateMessageText(String messageText) {

        if (messageText.trim().length() <= 0) {
            setErrorMessage(Messages.Message_text_is_missing);
            return false;
        }

        setErrorMessage(null);

        return true;
    }

    public boolean validateRecipient(String recipient) {

        recipient = recipient.trim();

        // Validate special values
        if (REPLY_MSGQ_NAME_SENDER.equals(recipient) || QEZSNDMG.RECIPIENT_ALL.equals(recipient) || QEZSNDMG.RECIPIENT_ALLACT.equals(recipient)
            || QEZSNDMG.RECIPIENT_SYSOPR.equals(recipient) || RECIPIENT_LIST.equals(recipient)) {
            setErrorMessage(null);
            return true;
        }

        // Validate single user profile
        if (recipient.length() <= 0) {
            setErrorMessage(Messages.Recipients_are_missing);
            return false;
        }

        if (!nameValidator.validate(recipient)) {
            setErrorMessage(Messages.Invalid_recipient);
            return false;
        }

        String message = validateUserProfile(recipient);
        if (message != null) {
            setErrorMessage(message);
            return false;
        }

        setErrorMessage(null);

        return true;
    }

    public int validateRecipients(String[] recipients) {

        if (recipients.length <= 0) {
            setErrorMessage(Messages.Recipients_are_missing);
            return Integer.MAX_VALUE;
        }

        String message;
        for (int i = 0; i < recipients.length; i++) {
            message = validateUserProfile(recipients[i]);
            if (message != null) {
                setErrorMessage(message);
                return i;
            }
        }

        setErrorMessage(null);

        return -1;
    }

    public boolean validateMessageQueueName(String messageQueueName) {

        if (REPLY_MSGQ_NAME_SENDER.equals(comboReplyMessageQueueName.getText())) {
            return true;
        }

        if (!nameValidator.validate(messageQueueName)) {
            return false;
        }

        String library = comboReplyMessageQueueLibrary.getText();
        if (!ISphereHelper.checkObject(system, library, messageQueueName, ISeries.MSGQ)) {
            return false;
        }

        setErrorMessage(null);

        return true;
    }

    public boolean validateMessageQueueLibrary(String library) {

        if (REPLY_MSGQ_NAME_SENDER.equals(comboReplyMessageQueueName.getText())) {
            return true;
        }

        if (LIBRARY_LIBL.equals(library)) {
            return true;
        }

        if (LIBRARY_CURLIB.equals(library)) {
            return true;
        }

        if (!libraryValidator.validate(library)) {
            return false;
        }

        if (!ISphereHelper.checkLibrary(system, library)) {
            return false;
        }

        setErrorMessage(null);

        return true;
    }

    private void storeInput() {

        sendMessageOptions = new SendMessageOptions();

        sendMessageOptions.setMessageType(comboMessageType.getText());
        sendMessageOptions.setDeliveryMode(comboDeliveryMode.getText());
        sendMessageOptions.setMessageText(textMessageText.getText());
        sendMessageOptions.setRecipientType(comboRecipientTypes.getText());

        String recipient = comboRecipient.getText().trim();
        if (RECIPIENT_LIST.equals(recipient)) {
            String[] recipients = recipientsEditor.getItems();
            sendMessageOptions.setRecipients(recipients);
        } else {
            sendMessageOptions.setRecipients(new String[] { comboRecipient.getText() });
        }

        if (QEZSNDMG.TYPE_INQUERY.equals(comboMessageType.getText())) {
            if (REPLY_MSGQ_NAME_SENDER.equals(comboReplyMessageQueueName.getText())) {
                sendMessageOptions.setReplyMessageQueueName(null);
                sendMessageOptions.setReplyMessageQueueLibrary(null);
            } else {
                sendMessageOptions.setReplyMessageQueueName(comboReplyMessageQueueName.getText());
                sendMessageOptions.setReplyMessageQueueLibrary(comboReplyMessageQueueLibrary.getText());
            }
        }
    }

    public SendMessageOptions getInput() {
        return sendMessageOptions;
    }

    /**
     * Restores the screen values of the last copy operation.
     */
    private void loadScreenValues() {

        comboMessageType.setText(loadValue(MESSAGE_TYPE, comboMessageType.getItems()[DEFAULT_INDEX_MESSAGE_TYPE]));
        comboDeliveryMode.setText(loadValue(DELIVERY_MODE, comboDeliveryMode.getItems()[DEFAULT_INDEX_DELIVERY_MODE]));
        textMessageText.setText(loadValue(MESSAGE_TEXT, "")); //$NON-NLS-1$
        comboRecipientTypes.setText(loadValue(MESSAGE_RECIPIENT_TYPES, comboRecipientTypes.getItem(DEFAULT_INDEX_RECIPIENT_TYPES)));
        comboRecipient.setText(loadValue(MESSAGE_RECIPIENT, comboRecipient.getItem(DEFAULT_INDEX_RECIPIENT))); //$NON-NLS-1$

        int count = loadIntValue(MESSAGE_RECIPIENTS_COUNT, 0);
        String[] recipients = new String[count];
        for (int i = 0; i < recipients.length; i++) {
            String recipient = loadValue(MESSAGE_RECIPIENT_ITEM + i, null);
            if (recipient != null) {
                recipients[i] = recipient;
            }
        }

        recipientsEditor.setItems(recipients);

        comboReplyMessageQueueName.setText(loadValue(MESSAGE_REPLY_QUEUE_NAME, comboReplyMessageQueueName.getItem(DEFAULT_REPLY_MESSAGE_QUEUE_NAME)));
        comboReplyMessageQueueLibrary.setText(loadValue(MESSAGE_REPLY_QUEUE_LIBRARY,
            comboReplyMessageQueueLibrary.getItem(DEFAULT_REPLY_MESSAGE_QUEUE_LIBRARY)));

        overWriteInitialValues();

        validateInput();
    }

    private void overWriteInitialValues() {

        if (overWriteMessageType != null) {
            comboMessageType.setText(overWriteMessageType);
        }

        if (overWriteMessageText != null) {
            textMessageText.setText(overWriteMessageText);
        }
    }

    /**
     * Stores the screen values that are preserved for the next copy operation.
     */
    private void storeScreenValues() {

        storeValue(MESSAGE_TYPE, comboMessageType.getText());
        storeValue(DELIVERY_MODE, comboDeliveryMode.getText());
        storeValue(MESSAGE_TEXT, textMessageText.getText());
        storeValue(MESSAGE_RECIPIENT_TYPES, comboRecipientTypes.getText());
        storeValue(MESSAGE_RECIPIENT, comboRecipient.getText());

        String[] recipients = recipientsEditor.getItems();
        storeValue(MESSAGE_RECIPIENTS_COUNT, recipients.length);
        for (int i = 0; i < recipients.length; i++) {
            storeValue(MESSAGE_RECIPIENT_ITEM + i, recipients[i]);
        }

        storeValue(MESSAGE_REPLY_QUEUE_NAME, comboReplyMessageQueueName.getText());
        storeValue(MESSAGE_REPLY_QUEUE_LIBRARY, comboReplyMessageQueueLibrary.getText());
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    /**
     * Overridden to provide a default size to {@link XDialog}.
     */
    @Override
    protected Point getDefaultSize() {
        return getShell().computeSize(600, SWT.DEFAULT, true);
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
